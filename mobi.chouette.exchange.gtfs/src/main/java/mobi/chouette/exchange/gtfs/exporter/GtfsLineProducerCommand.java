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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsRouteProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsServiceProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsShapeProducer;
import mobi.chouette.exchange.gtfs.exporter.producer.GtfsTripProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NamingUtil;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j
public class GtfsLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "GtfsLineProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {

			Line line = (Line) context.get(LINE);
			GtfsExportParameters configuration = (GtfsExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			}
			reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			if (line.getCompany() == null) {
				reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
						ActionReporter.ERROR_CODE.INVALID_FORMAT, "no company for this line");
				return SUCCESS;
			}

			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}

			GtfsDataCollector collector = new GtfsDataCollector();
			boolean cont = collector.collect(collection, line, startDate, endDate);
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 0);
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
					collection.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, collection
					.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY,
					collection.getVehicleJourneys().size());

			if (cont) {
				context.put(EXPORTABLE_DATA, collection);

				saveLine(context, line);
				reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
				result = SUCCESS;
			} else {
				reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
						ActionReporter.ERROR_CODE.NO_DATA_ON_PERIOD, "no data on period");
				result = SUCCESS; // else export will stop here
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private boolean saveLine(Context context,

	Line line) {
		Metadata metadata = (Metadata) context.get(METADATA);
		GtfsExporter exporter = (GtfsExporter) context.get(GTFS_EXPORTER);
		GtfsServiceProducer calendarProducer = new GtfsServiceProducer(exporter);
		GtfsTripProducer tripProducer = new GtfsTripProducer(exporter);
		GtfsRouteProducer routeProducer = new GtfsRouteProducer(exporter);
		GtfsShapeProducer shapeProducer = new GtfsShapeProducer(exporter);

		GtfsExportParameters configuration = (GtfsExportParameters) context.get(CONFIGURATION);
		String prefix = configuration.getObjectIdPrefix();
		String sharedPrefix = prefix;
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Map<String, List<Timetable>> timetables = collection.getTimetableMap();
		Set<JourneyPattern> jps = new HashSet<JourneyPattern>();

		boolean hasLine = false;
		boolean hasVj = false;
		// utiliser la collection
		if (!collection.getVehicleJourneys().isEmpty()) {
			for (VehicleJourney vj : collection.getVehicleJourneys()) {
				String tmKey = calendarProducer.key(vj.getTimetables(), sharedPrefix, configuration.isKeepOriginalId());
				if (tmKey != null) {
					if (tripProducer.save(vj, tmKey, prefix, sharedPrefix, configuration.isKeepOriginalId())) {
						hasVj = true;
						jps.add(vj.getJourneyPattern());
						if (!timetables.containsKey(tmKey)) {
							timetables.put(tmKey, new ArrayList<Timetable>(vj.getTimetables()));
						}
					}
				}
			} // vj loop
			for (JourneyPattern jp : jps) {
				shapeProducer.save(jp, prefix, configuration.isKeepOriginalId());
			}
			if (hasVj) {
				routeProducer.save(line, prefix, configuration.isKeepOriginalId());
				hasLine = true;
				if (metadata != null) {
					metadata.getResources().add(
							metadata.new Resource(NeptuneObjectPresenter.getName(line.getNetwork()),
									NeptuneObjectPresenter.getName(line)));
				}
			}
		}
		return hasLine;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new GtfsLineProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories.put(GtfsLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
