/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsAgencyProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsStopProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.report.DataStats;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j
public class GtfsSharedDataProducerCommand implements Command, Constant 
{
	public static final String COMMAND = "GtfsSharedDataProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);

		try {

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null)
			{
				return ERROR;
			}

			saveData(context);	
			DataStats globalStats = report.getStats();
			globalStats.setConnectionLinkCount(collection.getConnectionLinks().size());
			globalStats.setStopAreaCount(collection.getCommercialStops().size()+collection.getPhysicalStops().size());
			globalStats.setTimeTableCount(collection.getTimetables().size());
			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}



	private void saveData(Context context) 
	{
		Metadata metadata = (Metadata) context.get(METADATA);
		GtfsExporter exporter = (GtfsExporter) context.get(GTFS_EXPORTER);
		GtfsStopProducer stopProducer = new GtfsStopProducer(exporter);
		GtfsTransferProducer transferProducer = new GtfsTransferProducer(exporter);
		GtfsAgencyProducer agencyProducer = null;
		GtfsServiceProducer calendarProducer = null;		

		ActionReport report = (ActionReport) context.get(REPORT);
		GtfsExportParameters configuration = (GtfsExportParameters) context
				.get(CONFIGURATION);
		TimeZone timezone = TimeZone.getTimeZone(configuration.getTimeZone());
		String prefix = configuration.getObjectIdPrefix();
		String sharedPrefix = prefix;
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Map<String, List<Timetable>> timetables = collection.getTimetableMap();
		Set<StopArea> commercialStops = collection.getCommercialStops();
		Set<StopArea> physicalStops = collection.getPhysicalStops();
		Set<ConnectionLink> connectionLinks = collection.getConnectionLinks();
		Set<Company> companies = collection.getCompanies();
		if (!companies.isEmpty())
		{
			agencyProducer = new GtfsAgencyProducer(exporter);
		}
		if (!timetables.isEmpty())
		{
			calendarProducer = new GtfsServiceProducer(exporter);
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
				if (metadata != null && stop.hasCoordinates())
					metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
			}
		}
		for (StopArea stop : physicalStops)
		{
			stopProducer.save(stop, report, sharedPrefix, commercialStops);
			if (metadata != null && stop.hasCoordinates())
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
			agencyProducer.save(company, report, prefix, timezone);
		}

		for (List<Timetable> tms : timetables.values())
		{
			calendarProducer.save(tms, report, sharedPrefix);
			if (metadata != null )
			{
				for (Timetable tm : tms)
				{
					metadata.getTemporalCoverage().update(tm.getStartOfPeriod(), tm.getEndOfPeriod());
				}
			}
		}

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsSharedDataProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsSharedDataProducerCommand.class.getName(),
				new DefaultCommandFactory());
	}


}
