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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsExtendedStopProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.report.LineInfo;
import mobi.chouette.exchange.report.LineInfo.LINE_STATE;
import mobi.chouette.exchange.report.LineStats;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ChouetteAreaEnum;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j

@Stateless(name = GtfsSharedDataProducerCommand.COMMAND)

public class GtfsSharedDataProducerCommand implements Command, Constant 
{
	public static final String COMMAND = "GtfsLineProducerCommand";

	@EJB
	private LineDAO lineDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		Report report = (Report) context.get(REPORT);

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

				saveLine(context,null,line);	
				// producer.produce(context);

				lineInfo.setStatus(LINE_STATE.OK);
				// merge lineStats to global ones
				LineStats globalStats = report.getLines().getStats();
				if (globalStats == null) {
					globalStats = new LineStats();
					report.getLines().setStats(globalStats);
				}
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
			report.getLines().getList().add(lineInfo);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}


	/**
	 * @param lines
	 * @param report
	 * @param timeZone 
	 */
	public void saveLines(List<Line> lines, GtfsExporter exporter, Report report, String prefix, String sharedPrefix, TimeZone timeZone, Metadata metadata)
	{
		// à reprendre pour créer le GtfsExporterCommand; celui-ci doit appeler saveLine ou saveStopArea selon le type d'objets à exporter

		// l'export devra d'abord exporter les lignes une par une et collecter les objets communs pour les sauver en dernier
		// il est possible d'utilise le GtfsDataCollector pour ça; 
		// celui-ci remplit la structure ExportableData avec uniquement les objets utiles 
		// pour une période calendaire fournie
		// la progression se limitera à compter les lignes, les sauvegardes d'objets partagés se feront en phase finale.

		Map<String, List<Timetable>> timetables = new HashMap<String, List<Timetable>>();
		Set<StopArea> physicalStops = new HashSet<StopArea>();
		Set<StopArea> commercialStops = new HashSet<StopArea>();
		Set<Company> companies = new HashSet<Company>();
		Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
		GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
		GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
		GtfsAgencyProducer agencyProducer = new GtfsAgencyProducer(exporter);
		GtfsStopProducer stopProducer = new GtfsStopProducer(exporter);
		GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);
		GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
		boolean hasLines = false;
		for (Iterator<Line> lineIterator = lines.iterator(); lineIterator.hasNext();)
		{
			Line line = lineIterator.next();
			lineIterator.remove();

			hasLines = false;
		}
		if (hasLines)
		{
			for (Iterator<StopArea> iterator = commercialStops.iterator(); iterator.hasNext();)
			{
				StopArea stop = iterator.next();
				if (!stopProducer.save(stop, report, sharedPrefix, null))
				{
					iterator.remove();
				}
				else
				{
					if (stop.hasCoordinates())
						metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
				}
			}
			for (StopArea stop : physicalStops)
			{
				stopProducer.save(stop, report, sharedPrefix, commercialStops);
				if (stop.hasCoordinates())
					metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
			}
			// remove incomplete connectionlinks
			for (ConnectionLink link : connectionLinks)
			{
				if (!physicalStops.contains(link.getStartOfLink()) && !commercialStops.contains(link.getStartOfLink()))
				{
					continue;
				}
				else if (!physicalStops.contains(link.getEndOfLink()) && !commercialStops.contains(link.getEndOfLink()))
				{
					continue;
				}
				transferProducer.save(link, report, sharedPrefix);
			}

			for (Company company : companies)
			{
				agencyProducer.save(company, report, prefix, timeZone);
			}

			for (List<Timetable> tms : timetables.values())
			{
				calendarProducer.save(tms, report, sharedPrefix);
				for (Timetable tm : tms)
				{
					metadata.getTemporalCoverage().update(tm.getStartOfPeriod(), tm.getEndOfPeriod());
				}
			}

		}

	}


	private boolean saveLine(Context context,
			Metadata metadata, 

			Line line) 
	{
		GtfsExporter exporter = (GtfsExporter) context.get(GTFS_EXPORTER);
		GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
		GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
		GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);

		Report report = (Report) context.get(REPORT);
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
						NeptuneObjectPresenter.getName(line.getPtNetwork()), NeptuneObjectPresenter.getName(line)));
			}
		}
		return hasLine;
	}

	public void saveStopAreas(List<StopArea> beans, GtfsExporter exporter, Report report, String sharedPrefix, Metadata metadata)
	{
		Set<StopArea> physicalStops = new HashSet<StopArea>();
		Set<StopArea> commercialStops = new HashSet<StopArea>();
		Set<ConnectionLink> connectionLinks = new HashSet<ConnectionLink>();
		GtfsExtendedStopProducer stopProducer = new GtfsExtendedStopProducer(exporter);
		GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
		metadata.setDescription("limited to stops and transfers");
		for (StopArea area : beans)
		{
			if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) || area.getAreaType().equals(ChouetteAreaEnum.Quay))
			{
				if (area.hasCoordinates())
				{
					physicalStops.add(area);
					if (area.getConnectionStartLinks() != null)
						connectionLinks.addAll(area.getConnectionStartLinks());
					if (area.getConnectionEndLinks() != null)
						connectionLinks.addAll(area.getConnectionEndLinks());

					if (area.getParent() != null && area.getParent().hasCoordinates())
					{
						commercialStops.add(area.getParent());
						if (area.getParent().getConnectionStartLinks() != null)
							connectionLinks.addAll(area.getParent().getConnectionStartLinks());
						if (area.getParent().getConnectionEndLinks() != null)
							connectionLinks.addAll(area.getParent().getConnectionEndLinks());
					}
				}
			}

		}
		for (Iterator<StopArea> iterator = commercialStops.iterator(); iterator.hasNext();)
		{
			StopArea stop = iterator.next();
			if (!stopProducer.save(stop, report, sharedPrefix, null))
			{
				iterator.remove();
			}
			else
			{
				if (stop.hasCoordinates())
					metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
			}
		}
		for (StopArea stop : physicalStops)
		{
			stopProducer.save(stop, report, sharedPrefix, commercialStops);
			if (stop.hasCoordinates())
				metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
		}
		// remove incomplete connectionlinks
		for (ConnectionLink link : connectionLinks)
		{
			if (!physicalStops.contains(link.getStartOfLink()) && !commercialStops.contains(link.getStartOfLink()))
			{
				continue;
			}
			else if (!physicalStops.contains(link.getEndOfLink()) && !commercialStops.contains(link.getEndOfLink()))
			{
				continue;
			}
			transferProducer.save(link, report, sharedPrefix);
		}

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
		CommandFactory.factories.put(GtfsSharedDataProducerCommand.class.getName(),
				new DefaultCommandFactory());
	}


}
