package mobi.chouette.exchange.hub.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Collections;
import java.util.Comparator;

import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.hub.Constant;
import mobi.chouette.exchange.hub.exporter.producer.HubCheminProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubCourseOperationProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubCourseProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubDirectionProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubHoraireProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubItlProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubLigneProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubMissionOperationProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubMissionProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubModeTransportProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubRenvoiProducer;
import mobi.chouette.exchange.hub.exporter.producer.HubSchemaProducer;
import mobi.chouette.exchange.hub.model.HubException;
import mobi.chouette.exchange.hub.model.exporter.HubExporter;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NamingUtil;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
public class HubLineProducerCommand implements Command, Constant {
	public static final String COMMAND = "HubLineProducerCommand";

	private static final String PMR_CODE = "pmr";
	private static final String PMR_LABEL = "PMR";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {
			Line line = (Line) context.get(LINE);

			HubExportParameters configuration = (HubExportParameters) context.get(CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
			if (collection == null) {
				collection = new ExportableData();
				context.put(EXPORTABLE_DATA, collection);
			}
			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}
			HubDataCollector collector = new HubDataCollector();

			boolean cont = collector.collect(collection, line, startDate, endDate);
			reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
					OBJECT_STATE.OK, IO_TYPE.OUTPUT);
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.LINE, 1);
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN,
					collection.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, collection
					.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY,
					collection.getVehicleJourneys().size());
			
			if (cont) {
				context.put(EXPORTABLE_DATA, collection);
				try {
					saveData(context);
					// merge lineStats to global ones
					result = SUCCESS;
				} catch (HubException ex) {
					log.error("invalid data on line : " + ex);
					Path path = new File(ex.getPath()).toPath();
					String msg = path.getFileName().toString() + " : " + ex.getError() + " " + ex.getField();
					if (ex.getCode() != null)
						msg += " code : " + ex.getCode();
					if (ex.getValue() != null)
						msg += " value : " + ex.getValue();
					reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
							ActionReporter.ERROR_CODE.INVALID_FORMAT, msg);
					// throw new Exception("invalid data");
				} catch (Exception e) {
					log.error("failure on line", e);
					reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
							ActionReporter.ERROR_CODE.WRITE_ERROR, e.getMessage());
				}

			} else {
				reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
						ActionReporter.ERROR_CODE.NO_DATA_ON_PERIOD, "no data on period");
				result = SUCCESS; // else export will stop here
			}

		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	/**
	 * @param context
	 * @param line
	 * @param collection
	 * @throws IOException
	 * @throws DatatypeConfigurationException
	 */
	private void saveData(Context context) throws IOException, DatatypeConfigurationException {
		Metadata metadata = (Metadata) context.get(METADATA);

		saveLine(context);
		saveModeTransport(context);
		saveRenvois(context); // must be called before
								// saveCoursesOperationsAndHoraires()
		saveSchema(context);
		saveItls(context);
		saveCheminsAndDirections(context);
		saveCoursesOperationsAndHoraires(context);

		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);

		metadata.getResources().add(
				metadata.new Resource(NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
						NeptuneObjectPresenter.getName(collection.getLine())));

	}

	private void saveLine(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubLigneProducer ligneProducer = (HubLigneProducer) context.get(HUB_LIGNE_PRODUCER);
		if (ligneProducer == null) {
			ligneProducer = new HubLigneProducer(exporter);
			context.put(HUB_LIGNE_PRODUCER, ligneProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		ligneProducer.save(context, collection.getLine());

	}

	private void saveModeTransport(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubModeTransportProducer modeTransportProducer = (HubModeTransportProducer) context
				.get(HUB_MODETRANSPORT_PRODUCER);
		if (modeTransportProducer == null) {
			modeTransportProducer = new HubModeTransportProducer(exporter);
			context.put(HUB_MODETRANSPORT_PRODUCER, modeTransportProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		modeTransportProducer.addLine(collection.getLine());

	}

	private void saveRenvois(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubRenvoiProducer renvoiProducer = (HubRenvoiProducer) context.get(HUB_RENVOI_PRODUCER);
		if (renvoiProducer == null) {
			renvoiProducer = new HubRenvoiProducer(exporter);
			context.put(HUB_RENVOI_PRODUCER, renvoiProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		// add PMR footnote if required
		if (collection.getPmrFootenoteId() == 0) {
			Footnote pmr = new Footnote();
			pmr.setCode(PMR_CODE);
			pmr.setLabel(PMR_LABEL);
			renvoiProducer.save(context,pmr);
			collection.setPmrFootenoteId(Integer.parseInt(pmr.getKey())); // preserve
																			// id
																			// for
																			// vehicle
																			// journeys
		}

		for (Footnote footnote : collection.getLine().getFootnotes()) {
			renvoiProducer.save(context,footnote);
		}

	}

	private void saveSchema(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubSchemaProducer schemaProducer = (HubSchemaProducer) context.get(HUB_SCHEMA_PRODUCER);
		if (schemaProducer == null) {
			schemaProducer = new HubSchemaProducer(exporter);
			context.put(HUB_SCHEMA_PRODUCER, schemaProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Collections.sort(collection.getRoutes(), new RouteSorter());
		for (Route route : collection.getRoutes()) {
			schemaProducer.save(context,route);
		}

	}

	private void saveItls(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubItlProducer itlProducer = (HubItlProducer) context.get(HUB_ITL_PRODUCER);
		if (itlProducer == null) {
			itlProducer = new HubItlProducer(exporter);
			context.put(HUB_ITL_PRODUCER, itlProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		for (StopPoint stopPoint : collection.getStopPoints()) {
			itlProducer.save(context,stopPoint);
		}

	}

	private void saveCheminsAndDirections(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubCheminProducer cheminProducer = (HubCheminProducer) context.get(HUB_CHEMIN_PRODUCER);
		if (cheminProducer == null) {
			cheminProducer = new HubCheminProducer(exporter);
			context.put(HUB_CHEMIN_PRODUCER, cheminProducer);
		}
		HubDirectionProducer directionProducer = (HubDirectionProducer) context.get(HUB_DIRECTION_PRODUCER);
		if (directionProducer == null) {
			directionProducer = new HubDirectionProducer(exporter);
			context.put(HUB_DIRECTION_PRODUCER, directionProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Collections.sort(collection.getJourneyPatterns(), new JourneyPatternSorter());
		for (JourneyPattern journeyPattern : collection.getJourneyPatterns()) {
			cheminProducer.save(context,journeyPattern);
			directionProducer.save(context,journeyPattern);
		}

	}

	private void saveCoursesOperationsAndHoraires(Context context) {
		HubExporter exporter = (HubExporter) context.get(HUB_EXPORTER);
		HubCourseProducer courseProducer = (HubCourseProducer) context.get(HUB_COURSE_PRODUCER);
		if (courseProducer == null) {
			courseProducer = new HubCourseProducer(exporter);
			context.put(HUB_COURSE_PRODUCER, courseProducer);
		}
		HubMissionProducer missionProducer = (HubMissionProducer) context.get(HUB_MISSION_PRODUCER);
		if (missionProducer == null) {
			missionProducer = new HubMissionProducer(exporter);
			context.put(HUB_MISSION_PRODUCER, missionProducer);
		}
		HubCourseOperationProducer courseOperationProducer = (HubCourseOperationProducer) context
				.get(HUB_COURSEOPERATION_PRODUCER);
		if (courseOperationProducer == null) {
			courseOperationProducer = new HubCourseOperationProducer(exporter);
			context.put(HUB_COURSEOPERATION_PRODUCER, courseOperationProducer);
		}
		HubMissionOperationProducer missionOperationProducer = (HubMissionOperationProducer) context
				.get(HUB_MISSIONOPERATION_PRODUCER);
		if (missionOperationProducer == null) {
			missionOperationProducer = new HubMissionOperationProducer(exporter);
			context.put(HUB_MISSIONOPERATION_PRODUCER, missionOperationProducer);
		}
		HubHoraireProducer horaireProducer = (HubHoraireProducer) context.get(HUB_HORAIRE_PRODUCER);
		if (horaireProducer == null) {
			horaireProducer = new HubHoraireProducer(exporter);
			context.put(HUB_HORAIRE_PRODUCER, horaireProducer);
		}
		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		Collections.sort(collection.getVehicleJourneys(), new VehicleJourneySorter());
		for (VehicleJourney vehicleJourney : collection.getVehicleJourneys()) {
			courseProducer.save(context,vehicleJourney, collection.getPmrFootenoteId(), 
					collection.getVehicleJourneyRank());
			missionProducer.save(context,vehicleJourney, collection.getPmrFootenoteId(), 
					collection.getVehicleJourneyRank());
			courseOperationProducer.save(context,vehicleJourney,  collection.getVehicleJourneyRank());
			missionOperationProducer.save(context,vehicleJourney,  collection.getVehicleJourneyRank());
			int lastItem = vehicleJourney.getVehicleJourneyAtStops().size() - 1;
			for (int i = 0; i <= lastItem; i++) {
				horaireProducer.save(context,vehicleJourney.getVehicleJourneyAtStops().get(i), i == 0, i == lastItem, 
						collection.getVehicleJourneyRank());
			}
			collection.setVehicleJourneyRank(collection.getVehicleJourneyRank() + 1);
		}

	}

	public class RouteSorter implements Comparator<Route> {
		@Override
		public int compare(Route arg0, Route arg1) {
			return arg0.getWayBack().compareTo(arg1.getWayBack());
		}
	}

	public class StopPointSorter implements Comparator<StopPoint> {
		@Override
		public int compare(StopPoint arg0, StopPoint arg1) {
			int ret = arg0.getRoute().getWayBack().compareTo(arg1.getRoute().getWayBack());
			if (ret == 0)
				ret = arg0.getPosition().compareTo(arg1.getPosition());
			return ret;
		}
	}

	public class JourneyPatternSorter implements Comparator<JourneyPattern> {
		@Override
		public int compare(JourneyPattern arg0, JourneyPattern arg1) {

			return arg0.objectIdSuffix().compareTo(arg1.objectIdSuffix());
		}
	}

	public class VehicleJourneySorter implements Comparator<VehicleJourney> {
		@Override
		public int compare(VehicleJourney arg0, VehicleJourney arg1) {

			return arg0.getPublishedJourneyIdentifier().compareTo(arg1.getPublishedJourneyIdentifier());
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new HubLineProducerCommand();

			return result;
		}
	}

	static {
		CommandFactory.factories.put(HubLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}
