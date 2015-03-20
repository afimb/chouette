/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j

@Stateless(name = GtfsLineProducerCommand.COMMAND)

public class GtfsLineProducerCommand implements Command, Constant 
{
	public static final String COMMAND = "GtfsLineProducerCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);

		try {

			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);
			GtfsExportParameters configuration = (GtfsExportParameters) context
					.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null)
			{
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA,collection);
			}

			Date startDate = null;
			if(configuration.getStartDate() != null){
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if(configuration.getEndDate() != null){
				endDate = new Date(configuration.getEndDate().getTime());
			}

			GtfsDataCollector collector = new GtfsDataCollector();
			boolean cont =  (collector.collect(collection, line, startDate, endDate));
			LineInfo lineInfo = new LineInfo();
			lineInfo.setName(line.getName()+" ("+line.getNumber()+")");
			LineStats stats = new LineStats();
			// stats.setAccesPointCount(collection.getAccessPoints().size());
			// stats.setConnectionLinkCount(collection.getConnectionLinks().size());
			stats.setJourneyPatternCount(collection.getJourneyPatterns().size());
			stats.setRouteCount(collection.getRoutes().size());
			// stats.setStopAreaCount(collection.getCommercialStops().size()+collection.getPhysicalStops().size());
			// stats.setTimeTableCount(collection.getTimetables().size());
			stats.setVehicleJourneyCount(collection.getVehicleJourneys().size());

			if (cont)
			{
				context.put(EXPORTABLE_DATA, collection);

				saveLine(context,line);	
				// producer.produce(context);

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.setStats(globalStats);
				}
				globalStats.setLineCount(globalStats.getLineCount()
						+ stats.getLineCount());
				globalStats.setRouteCount(globalStats.getRouteCount()
						+ stats.getRouteCount());
				globalStats.setVehicleJourneyCount(globalStats.getVehicleJourneyCount()
						+ stats.getVehicleJourneyCount());
				globalStats.setJourneyPatternCount(globalStats.getJourneyPatternCount()
						+ stats.getJourneyPatternCount());
				result = SUCCESS;
			}
			else
			{
				lineInfo.setStatus(LINE_STATE.ERROR);
				result=ERROR;
			}
			report.getLines().add(lineInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}



	private boolean saveLine(Context context,

			Line line) 
	{
		Metadata metadata = (Metadata) context.get(METADATA);
		GtfsExporter exporter = (GtfsExporter) context.get(GTFS_EXPORTER);
		GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
		GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
		GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);

		ActionReport report = (ActionReport) context.get(REPORT);
		GtfsExportParameters configuration = (GtfsExportParameters) context
				.get(CONFIGURATION);
		String prefix = configuration.getObjectIdPrefix();
		String sharedPrefix = prefix;
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Map<String, List<Timetable>> timetables = collection.getTimetableMap();

		boolean hasLine = false;
		boolean hasVj = false;
		// utiliser la collection
		if (!collection.getVehicleJourneys().isEmpty())
		{
			for (VehicleJourney vj : collection.getVehicleJourneys())
			{
				String tmKey = calendarProducer.key(vj.getTimetables(), sharedPrefix);
				if (tmKey != null)
				{
					if (tripProducer.save(vj, tmKey, report, prefix, sharedPrefix))
					{
						hasVj = true;
						if (!timetables.containsKey(tmKey))
						{
							timetables.put(tmKey, new ArrayList<Timetable>(vj.getTimetables()));
						}
					}
				}
			} // vj loop
			if (hasVj)
			{
				routeProducer.save(line, report, prefix);
				hasLine = true;
				metadata.getResources().add(metadata.new Resource( 
						NeptuneObjectPresenter.getName(line.getNetwork()), NeptuneObjectPresenter.getName(line)));
			}
		}
		return hasLine;
	}


	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.gtfs/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsLineProducerCommand.class.getName(),
				new DefaultCommandFactory());
	}


}
