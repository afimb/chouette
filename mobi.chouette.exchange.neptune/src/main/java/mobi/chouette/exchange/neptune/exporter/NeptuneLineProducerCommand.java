package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.sql.Date;

import javax.naming.InitialContext;
import javax.xml.bind.MarshalException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.exporter.SharedDataKeys;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;

import org.xml.sax.SAXParseException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class NeptuneLineProducerCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneLineProducerCommand";

	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {

			Line line = (Line) context.get(LINE);
			NeptuneExportParameters configuration = (NeptuneExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			} else {
				collection.clear();
			}

			SharedDataKeys sharedData = (SharedDataKeys) context.get(SHARED_DATA_KEYS);
			if (sharedData == null) {
				sharedData = new SharedDataKeys();
				context.put(SHARED_DATA_KEYS, sharedData);
			}
			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}

			NeptuneDataCollector collector = new NeptuneDataCollector();
			boolean cont = (collector.collect(collection, line, startDate, endDate));
			reporter.addObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 0);
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
					collection.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, collection
					.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY,
					collection.getVehicleJourneys().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.CONNECTION_LINK,
					collection.getConnectionLinks().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.TIMETABLE,
					collection.getTimetables().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ACCESS_POINT,
					collection.getAccessPoints().size());
			reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.STOP_AREA,
					collection.getStopAreas().size());

			if (cont) {
				try {
					ChouettePTNetworkProducer producer = new ChouettePTNetworkProducer();
					producer.produce(context);

					reporter.setStatToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
					// merge lineStats to global ones
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.NETWORK, "networks", OBJECT_STATE.OK,
							IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.NETWORK, OBJECT_TYPE.NETWORK,
							sharedData.getNetworkIds().size());
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.COMPANY, "companies", OBJECT_STATE.OK,
							IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.COMPANY, OBJECT_TYPE.COMPANY,
							sharedData.getCompanyIds().size());
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK, "connection links",
							OBJECT_STATE.OK, IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.CONNECTION_LINK,
							OBJECT_TYPE.CONNECTION_LINK, sharedData.getConnectionLinkIds().size());
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT, "access points",
							OBJECT_STATE.OK, IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.ACCESS_POINT,
							OBJECT_TYPE.ACCESS_POINT, sharedData.getAccessPointIds().size());
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, "stop areas", OBJECT_STATE.OK,
							IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.STOP_AREA, OBJECT_TYPE.STOP_AREA,
							sharedData.getStopAreaIds().size());
					reporter.addObjectReport(context, "merged", OBJECT_TYPE.TIMETABLE, "calendars", OBJECT_STATE.OK,
							IO_TYPE.OUTPUT);
					reporter.setStatToObjectReport(context, "merged", OBJECT_TYPE.TIMETABLE, OBJECT_TYPE.TIMETABLE,
							sharedData.getTimetableIds().size());
					result = SUCCESS;
				} catch (MarshalException e) {
					if (e.getCause() != null && e.getCause() instanceof SAXParseException) {
						log.error(e.getCause().getMessage());
						reporter.addErrorToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE,
								ActionReporter.ERROR_CODE.INVALID_FORMAT, e.getCause().getMessage());
					} else {
						log.error(e.getMessage());
						reporter.addErrorToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE,
								ActionReporter.ERROR_CODE.INVALID_FORMAT, e.getMessage());
					}
				}

			} else {
				reporter.addErrorToObjectReport(context, line.getChouetteId().getObjectId(), OBJECT_TYPE.LINE,
						ActionReporter.ERROR_CODE.NO_DATA_ON_PERIOD, "no data on period");
			}

		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			return new NeptuneLineProducerCommand();
		}
	}

	static {
		CommandFactory.factories.put(NeptuneLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}
}
