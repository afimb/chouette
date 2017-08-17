package mobi.chouette.exchange.gtfs.parser;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsRoute;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.LocationType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.GtfsTrip.DirectionType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsException;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.gtfs.model.importer.RouteById;
import mobi.chouette.exchange.gtfs.model.importer.ShapeById;
import mobi.chouette.exchange.gtfs.model.importer.StopById;
import mobi.chouette.exchange.gtfs.model.importer.StopTimeByTrip;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.GtfsValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.JourneyCategoryEnum;
import mobi.chouette.model.type.SectionStatusEnum;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.PrecisionModel;

@Log4j
public class GtfsTripParser implements Parser, Validator, Constant {

	private static final Comparator<OrderedCoordinate> COORDINATE_SORTER = new OrderedCoordinateComparator();

	@Getter
	@Setter
	private String gtfsRouteId;

	@Override
	public void validate(Context context) throws Exception {
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		gtfsValidationReporter.getExceptions().clear();

		validateStopTimes(context);
		validateShapes(context);
		validateTrips(context);
		validateFrequencies(context);
	}

	private void validateStopTimes(Context context) throws Exception {

		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		Set<String> stopIds = new HashSet<String>();

		// stop_times.txt
		// log.info("validating stop_times");
		if (importer.hasStopTimeImporter()) { // the file "stop_times.txt"
												// exists ?
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_STOP_TIMES_FILE);

			Index<GtfsStopTime> stopTimeParser = null;
			try { // Read and check the header line of the file "stop_times.txt"
				stopTimeParser = importer.getStopTimeByTrip();
			} catch (Exception ex) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_STOP_TIMES_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_STOP_TIMES_FILE);
				}
			}

			gtfsValidationReporter.validateOkCSV(context, GTFS_STOP_TIMES_FILE);

			if (stopTimeParser == null) { // importer.getStopTimeByTrip() fails
											// for any other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception(
						"Cannot instantiate StopTimeByTrip class"), GTFS_STOP_TIMES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_STOP_TIMES_FILE, stopTimeParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}

			if (!stopTimeParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, stopTimeParser.getErrors(), GTFS_STOP_TIMES_FILE);
				stopTimeParser.getErrors().clear();
			}

			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_STOP_TIMES_FILE);

			if (stopTimeParser.getLength() == 0) {
				gtfsValidationReporter.reportError(context, new GtfsException(GTFS_STOP_TIMES_FILE, 1, null,
						GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_STOP_TIMES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_STOP_TIMES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}

			GtfsException fatalException = null;
			stopTimeParser.setWithValidation(true);
			for (GtfsStopTime bean : stopTimeParser) {

				if (bean.getStopId() != null)
					stopIds.add(bean.getStopId());
				try {
					stopTimeParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_STOP_TIMES_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_STOP_TIMES_FILE);
					}
				}
				for (GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_STOP_TIMES_FILE);
				gtfsValidationReporter.validate(context, GTFS_STOP_TIMES_FILE, bean.getOkTests());
			}
			// contrôle de la séquence
			stopTimeParser.setWithValidation(false);
			{
				Iterable<String> tripIds = stopTimeParser.keys();

				Map<Integer, Integer> stopSequences = new HashMap<>();
				for (String tripId : tripIds) {
					stopSequences.clear();
					Iterable<GtfsStopTime> stopTimes = stopTimeParser.values(tripId);
					for (GtfsStopTime bean : stopTimes) {
						Integer stopSequence = bean.getStopSequence();
						if (stopSequence != null) {
							if (stopSequences.containsKey(stopSequence)) {
								gtfsValidationReporter.reportError(
										context,
										new GtfsException(stopTimeParser.getPath(), bean.getId(), stopTimeParser
												.getIndex(StopTimeByTrip.FIELDS.stop_sequence.name()),
												StopTimeByTrip.FIELDS.trip_id.name() + ","
														+ StopTimeByTrip.FIELDS.stop_sequence.name(),
												GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE, null, tripId + ","
														+ stopSequence), GTFS_STOP_TIMES_FILE);
							} else {
								stopSequences.put(stopSequence, bean.getId());
								gtfsValidationReporter.validate(context, GTFS_STOP_TIMES_FILE,
										GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE);
							}
						}
					}
				}

			}
			int i = 1;
			boolean unsuedId = true;
			for (GtfsStop bean : importer.getStopById()) {
				if (LocationType.Stop.equals(bean.getLocationType())) {
					if (stopIds.add(bean.getStopId())) {
						unsuedId = false;
						gtfsValidationReporter.reportError(context, new GtfsException(GTFS_STOPS_FILE, i,
								StopById.FIELDS.stop_id.name(), GtfsException.ERROR.UNUSED_ID, null, bean.getStopId()),
								GTFS_STOPS_FILE);
					}
				}
				i++;
			}
			if (unsuedId)
				gtfsValidationReporter.validate(context, GTFS_STOPS_FILE, GtfsException.ERROR.UNUSED_ID);
			gtfsValidationReporter.getExceptions().clear();
			if (fatalException != null)
				throw fatalException;
		} else {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_STOP_TIMES_FILE, 1, null,
					GtfsException.ERROR.MISSING_FILE, null, null), GTFS_STOP_TIMES_FILE);
		}
	}

	private void validateShapes(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);

		// shapes.txt
		// log.info("validating shapes");
		if (importer.hasShapeImporter()) {
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_SHAPES_FILE);

			Index<GtfsShape> shapeParser = null;
			try { // Read and check the header line of the file "shapes.txt"
				shapeParser = importer.getShapeById();
			} catch (Exception ex) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_SHAPES_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_SHAPES_FILE);
				}
			}

			gtfsValidationReporter.validateOkCSV(context, GTFS_SHAPES_FILE);

			if (shapeParser == null) { // importer.getShapeById() fails for any
										// other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception("Cannot instantiate ShapeById class"),
						GTFS_SHAPES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_SHAPES_FILE, shapeParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}

			if (!shapeParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, shapeParser.getErrors(), GTFS_SHAPES_FILE);
				shapeParser.getErrors().clear();
			}

			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_SHAPES_FILE);

			if (shapeParser.getLength() == 0) {
				gtfsValidationReporter.reportError(context, new GtfsException(GTFS_SHAPES_FILE, 1, null,
						GtfsException.ERROR.OPTIONAL_FILE_WITH_NO_ENTRY, null, null), GTFS_SHAPES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_SHAPES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}

			GtfsException fatalException = null;
			shapeParser.setWithValidation(true);

			for (GtfsShape bean : shapeParser) {
				try {
					shapeParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_SHAPES_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_SHAPES_FILE);
					}
				}
				for (GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_SHAPES_FILE);
				gtfsValidationReporter.validate(context, GTFS_SHAPES_FILE, bean.getOkTests());
			}

			// contrôle de la séquence
			shapeParser.setWithValidation(false);
			{
				Iterable<String> tripIds = shapeParser.keys();

				Map<Integer, Integer> shapeSequences = new HashMap<>();
				for (String tripId : tripIds) {
					shapeSequences.clear();
					Iterable<GtfsShape> shapes = shapeParser.values(tripId);
					for (GtfsShape bean : shapes) {
						Integer stopSequence = bean.getShapePtSequence();
						if (stopSequence != null) {
							if (shapeSequences.containsKey(stopSequence)) {
								gtfsValidationReporter.reportError(
										context,
										new GtfsException(shapeParser.getPath(), bean.getId(), shapeParser
												.getIndex(ShapeById.FIELDS.shape_pt_sequence.name()),
												ShapeById.FIELDS.shape_id.name() + ","
														+ ShapeById.FIELDS.shape_pt_sequence.name(),
												GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE, null, tripId + ","
														+ stopSequence), GTFS_SHAPES_FILE);
							} else {
								shapeSequences.put(stopSequence, bean.getId());
								gtfsValidationReporter.validate(context, GTFS_SHAPES_FILE,
										GtfsException.ERROR.DUPLICATE_STOP_SEQUENCE);
							}
						}
					}
				}

			}

			shapeParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		} else {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_SHAPES_FILE, 1, null,
					GtfsException.ERROR.MISSING_OPTIONAL_FILE, null, null), GTFS_SHAPES_FILE);
		}
	}

	private void validateTrips(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);
		Set<String> routeIds = new HashSet<String>();

		// trips.txt
		// log.info("validating trips");
		if (importer.hasTripImporter()) { // the file "trips.txt" exists ?
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_TRIPS_FILE);

			Index<GtfsTrip> tripParser = null;
			try { // Read and check the header line of the file "trips.txt"
				tripParser = importer.getTripById();
			} catch (Exception ex) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_TRIPS_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_TRIPS_FILE);
				}
			}

			gtfsValidationReporter.validateOkCSV(context, GTFS_TRIPS_FILE);

			if (tripParser == null) { // importer.getTripById() fails for any
										// other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception("Cannot instantiate TripById class"),
						GTFS_TRIPS_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_TRIPS_FILE, tripParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}

			if (!tripParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, tripParser.getErrors(), GTFS_TRIPS_FILE);
				tripParser.getErrors().clear();
			}

			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_TRIPS_FILE);

			if (tripParser.getLength() == 0) {
				gtfsValidationReporter.reportError(context, new GtfsException(GTFS_TRIPS_FILE, 1, null,
						GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_TRIPS_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_TRIPS_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}

			GtfsException fatalException = null;
			tripParser.setWithValidation(true);
			for (GtfsTrip bean : tripParser) {
				if (bean.getRouteId() != null)
					routeIds.add(bean.getRouteId());
				try {
					tripParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, bean.getRouteId(), (GtfsException) ex,
								GTFS_TRIPS_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_TRIPS_FILE);
					}
				}
				for (GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getRouteId(), bean.getErrors(), GTFS_TRIPS_FILE);
				gtfsValidationReporter.validate(context, GTFS_TRIPS_FILE, bean.getOkTests());
			}
			tripParser.setWithValidation(false);
			int i = 1;
			boolean unsuedId = true;
			for (GtfsRoute bean : importer.getRouteById()) {
				if (routeIds.add(bean.getRouteId())) {
					unsuedId = false;
					gtfsValidationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, i,
							RouteById.FIELDS.route_id.name(), GtfsException.ERROR.UNUSED_ID, null, bean.getRouteId()),
							GTFS_TRIPS_FILE);
				}
				i++;
			}
			if (unsuedId)
				gtfsValidationReporter.validate(context, GTFS_ROUTES_FILE, GtfsException.ERROR.UNUSED_ID);
			if (fatalException != null)
				throw fatalException;
		} else {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_TRIPS_FILE, 1, null,
					GtfsException.ERROR.MISSING_FILE, null, null), GTFS_TRIPS_FILE);
		}
	}

	private void validateFrequencies(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsValidationReporter gtfsValidationReporter = (GtfsValidationReporter) context.get(GTFS_REPORTER);

		// frequencies.txt
		// log.info("validating frequencies");
		if (importer.hasFrequencyImporter()) {
			gtfsValidationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_FREQUENCIES_FILE);

			Index<GtfsFrequency> frequencyParser = null;
			try { // Read and check the header line of the file
					// "frequenciess.txt"
				frequencyParser = importer.getFrequencyByTrip();
			} catch (Exception ex) {
				if (ex instanceof GtfsException) {
					gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_FREQUENCIES_FILE);
				} else {
					gtfsValidationReporter.throwUnknownError(context, ex, GTFS_FREQUENCIES_FILE);
				}
			}

			gtfsValidationReporter.validateOkCSV(context, GTFS_FREQUENCIES_FILE);

			if (frequencyParser == null) { // importer.getFrequencyByTrip()
											// fails for any other reason
				gtfsValidationReporter.throwUnknownError(context, new Exception(
						"Cannot instantiate FrequencyByTrip class"), GTFS_FREQUENCIES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_FREQUENCIES_FILE, frequencyParser.getOkTests());
				gtfsValidationReporter.validateUnknownError(context);
			}

			if (!frequencyParser.getErrors().isEmpty()) {
				gtfsValidationReporter.reportErrors(context, frequencyParser.getErrors(), GTFS_FREQUENCIES_FILE);
				frequencyParser.getErrors().clear();
			}

			gtfsValidationReporter.validateOKGeneralSyntax(context, GTFS_FREQUENCIES_FILE);

			if (frequencyParser.getLength() == 0) {
				// validationReporter.reportUnsuccess(context,
				// GTFS_1_GTFS_Frequency_1, GTFS_FREQUENCIES_FILE);
				gtfsValidationReporter.reportError(context, new GtfsException(GTFS_FREQUENCIES_FILE, 1, null,
						GtfsException.ERROR.OPTIONAL_FILE_WITH_NO_ENTRY, null, null), GTFS_FREQUENCIES_FILE);
			} else {
				gtfsValidationReporter.validate(context, GTFS_FREQUENCIES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}

			GtfsException fatalException = null;
			frequencyParser.setWithValidation(true);
			for (GtfsFrequency bean : frequencyParser) {
				try {
					frequencyParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						gtfsValidationReporter.reportError(context, (GtfsException) ex, GTFS_FREQUENCIES_FILE);
					} else {
						gtfsValidationReporter.throwUnknownError(context, ex, GTFS_FREQUENCIES_FILE);
					}
				}
				for (GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				gtfsValidationReporter.reportErrors(context, bean.getErrors(), GTFS_FREQUENCIES_FILE);
				gtfsValidationReporter.validate(context, GTFS_FREQUENCIES_FILE, bean.getOkTests());
			}
			frequencyParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		} else {
			gtfsValidationReporter.reportError(context, new GtfsException(GTFS_FREQUENCIES_FILE, 1, null,
					GtfsException.ERROR.MISSING_OPTIONAL_FILE, null, null), GTFS_FREQUENCIES_FILE);
		}
	}

	@Override
	public void parse(Context context) throws Exception {

		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		Map<String, JourneyPattern> journeyPatternByStopSequence = new HashMap<String, JourneyPattern>();

		// VehicleJourney
		Index<GtfsTrip> gtfsTrips = importer.getTripByRoute();

		for (GtfsTrip gtfsTrip : gtfsTrips.values(gtfsRouteId)) {

			if (!importer.getStopTimeByTrip().values(gtfsTrip.getTripId()).iterator().hasNext()) {
				continue;
			}
			boolean hasTimes = true;
			for (GtfsStopTime gtfsStopTime : importer.getStopTimeByTrip().values(gtfsTrip.getTripId())) {
				if (gtfsStopTime.getArrivalTime() == null) {
					hasTimes = false;
					break;
				}
				if (gtfsStopTime.getDepartureTime() == null) {
					hasTimes = false;
					break;
				}
			}
			if (!hasTimes)
				continue;

			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					VehicleJourney.VEHICLEJOURNEY_KEY, gtfsTrip.getTripId(), log);
			VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, objectId);
			convert(context, gtfsTrip, vehicleJourney);

			// VehicleJourneyAtStop
			boolean afterMidnight = true;

			for (GtfsStopTime gtfsStopTime : importer.getStopTimeByTrip().values(gtfsTrip.getTripId())) {
				VehicleJourneyAtStopWrapper vehicleJourneyAtStop = new VehicleJourneyAtStopWrapper(
						gtfsStopTime.getStopId(), gtfsStopTime.getStopSequence(), gtfsStopTime.getShapeDistTraveled());
				convert(context, gtfsStopTime, vehicleJourneyAtStop);

				if (afterMidnight) {
					if (!gtfsStopTime.getArrivalTime().moreOneDay())
						afterMidnight = false;
					if (!gtfsStopTime.getDepartureTime().moreOneDay())
						afterMidnight = false;
				}

				vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			}

			Collections.sort(vehicleJourney.getVehicleJourneyAtStops(), VEHICLE_JOURNEY_AT_STOP_COMPARATOR);

			// Timetable
			String timetableId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					Timetable.TIMETABLE_KEY, gtfsTrip.getServiceId(), log);
			if (afterMidnight) {
				timetableId += GtfsCalendarParser.AFTER_MIDNIGHT_SUFFIX;
			}
			Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
			vehicleJourney.getTimetables().add(timetable);

			// JourneyPattern
			String journeyKey = gtfsTrip.getRouteId() + "_" + gtfsTrip.getDirectionId().ordinal();
			Iterable<GtfsShape> gtfsShapes = null;
			if (gtfsTrip.getShapeId() != null && !gtfsTrip.getShapeId().isEmpty()
					&& importer.getShapeById().containsKey(gtfsTrip.getShapeId())) {
				journeyKey += "_" + gtfsTrip.getShapeId();
				gtfsShapes = importer.getShapeById().values(gtfsTrip.getShapeId());
			}
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
				String stopId = ((VehicleJourneyAtStopWrapper) vehicleJourneyAtStop).stopId;
				journeyKey += "," + stopId;
			}
			JourneyPattern journeyPattern = journeyPatternByStopSequence.get(journeyKey);
			if (journeyPattern == null) {
				journeyPattern = createJourneyPattern(context, referential, configuration, gtfsTrip, gtfsShapes,
						vehicleJourney, journeyKey, journeyPatternByStopSequence);
			}

			vehicleJourney.setRoute(journeyPattern.getRoute());
			vehicleJourney.setJourneyPattern(journeyPattern);

			int length = journeyPattern.getStopPoints().size();
			for (int i = 0; i < length; i++) {
				VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStops().get(i);
				vehicleJourneyAtStop.setStopPoint(journeyPattern.getStopPoints().get(i));
			}

			// apply frequencies if any
			if (importer.hasFrequencyImporter()) {
				createJourneyFrequencies(context, referential, importer, configuration, gtfsTrip, vehicleJourney);
			}

		}
		// dispose collections
		journeyPatternByStopSequence.clear();
	}

	private void createJourneyFrequencies(Context context, Referential referential, GtfsImporter importer,
			GtfsImportParameters configuration, GtfsTrip gtfsTrip, VehicleJourney vehicleJourney) {
		int count = 0;
		for (GtfsFrequency frequency : importer.getFrequencyByTrip().values(gtfsTrip.getTripId())) {
			vehicleJourney.setJourneyCategory(JourneyCategoryEnum.Frequency);

			String timeBandObjectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					Timeband.TIMETABLE_KEY, gtfsTrip.getTripId() + "-" + count++, log);
			Timeband timeband = ObjectFactory.getTimeband(referential, timeBandObjectId);
			timeband.setName(getTimebandName(frequency));
			timeband.setStartTime(frequency.getStartTime().getTime());
			timeband.setEndTime(frequency.getEndTime().getTime());

			JourneyFrequency journeyFrequency = new JourneyFrequency();
			journeyFrequency.setExactTime(frequency.getExactTimes());
			journeyFrequency.setFirstDepartureTime(frequency.getStartTime().getTime());
			journeyFrequency.setLastDepartureTime(frequency.getEndTime().getTime());
			journeyFrequency.setScheduledHeadwayInterval(TimeUtil.valueOf(frequency.getHeadwaySecs()));
			journeyFrequency.setTimeband(timeband);
			journeyFrequency.setVehicleJourney(vehicleJourney);

			List<VehicleJourneyAtStop> vjass = vehicleJourney.getVehicleJourneyAtStops();
			VehicleJourneyAtStop firstVjas = vjass.get(0);
			Time firstArrivalTime = firstVjas.getArrivalTime();
			Time firstDepartureTime = firstVjas.getDepartureTime();
			for (VehicleJourneyAtStop vjas : vjass) {
				vjas.setArrivalTime(TimeUtil.substract(vjas.getArrivalTime(), firstArrivalTime));
				vjas.setDepartureTime(TimeUtil.substract(vjas.getDepartureTime(), firstDepartureTime));
			}
		}
	}

	private String getTimebandName(GtfsFrequency frequency) {
		Calendar startCal = Calendar.getInstance(TimeZone.getDefault());
		startCal.setTime(frequency.getStartTime().getTime());
		Calendar endCal = Calendar.getInstance(TimeZone.getDefault());
		endCal.setTime(frequency.getEndTime().getTime());
		return (startCal.get(Calendar.HOUR_OF_DAY) + ":" + startCal.get(Calendar.MINUTE) + " - "
				+ endCal.get(Calendar.HOUR_OF_DAY) + ":" + endCal.get(Calendar.MINUTE));
	}

	private JourneyPattern createJourneyPattern(Context context, Referential referential,
			GtfsImportParameters configuration, GtfsTrip gtfsTrip, Iterable<GtfsShape> gtfsShapes,
			VehicleJourney vehicleJourney, String journeyKey, Map<String, JourneyPattern> journeyPatternByStopSequence) {
		JourneyPattern journeyPattern;

		// Route
		Route route = createRoute(referential, configuration, gtfsTrip);

		// JourneyPattern
		String journeyPatternId = route.getObjectId().replace(Route.ROUTE_KEY, JourneyPattern.JOURNEYPATTERN_KEY);
		journeyPattern = ObjectFactory.getJourneyPattern(referential, journeyPatternId);
		journeyPattern.setName(gtfsTrip.getTripHeadSign());
		journeyPattern.setRoute(route);
		journeyPatternByStopSequence.put(journeyKey, journeyPattern);

		// StopPoints
		createStopPoint(route, journeyPattern, vehicleJourney.getVehicleJourneyAtStops(), referential, configuration);

		List<StopPoint> stopPoints = journeyPattern.getStopPoints();
		journeyPattern.setDepartureStopPoint(stopPoints.get(0));
		journeyPattern.setArrivalStopPoint(stopPoints.get(stopPoints.size() - 1));

		journeyPattern.setFilled(true);
		route.setFilled(true);

		if (route.getName() == null) {
			if (!route.getStopPoints().isEmpty()) {
				String first = route.getStopPoints().get(0).getContainedInStopArea().getName();
				String last = route.getStopPoints().get(route.getStopPoints().size() - 1).getContainedInStopArea()
						.getName();
				route.setName(first + " -> " + last);
			}
		}

		// Shape -> routeSections
		if (gtfsShapes != null) {
			List<RouteSection> sections = createRouteSections(context, referential, configuration, journeyPattern,
					vehicleJourney, gtfsShapes);
			if (!sections.isEmpty()) {
				journeyPattern.setRouteSections(sections);
				journeyPattern.setSectionStatus(SectionStatusEnum.Completed);
			}
		}
		return journeyPattern;
	}

	private static final double narrow = 0.0000001;

	private List<RouteSection> createRouteSections(Context context, Referential referential,
			GtfsImportParameters configuration, JourneyPattern journeyPattern, VehicleJourney vehicleJourney,
			Iterable<GtfsShape> gtfsShapes) {
		List<RouteSection> sections = new ArrayList<>();
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(10), 4326);
		List<OrderedCoordinate> coordinates = new ArrayList<>();
		List<LineSegment> segments = new ArrayList<>();
		Coordinate previous = null;
		String shapeId = null;
		// Integer lineNumber = null;
		for (GtfsShape gtfsShape : gtfsShapes) {
			if (gtfsShape.getShapePtLon() == null || gtfsShape.getShapePtLat() == null) {
				log.error("line " + gtfsShape.getId() + " missing coordinates for shape " + gtfsShape.getShapeId());
				return sections;
			}
			if (shapeId == null) {
				shapeId = gtfsShape.getShapeId();
			}
			OrderedCoordinate current = new OrderedCoordinate(gtfsShape.getShapePtLon().doubleValue(), gtfsShape
					.getShapePtLat().doubleValue(), gtfsShape.getShapePtSequence());
			if (previous != null) {
				// remove duplicate coords
				if (Math.abs(current.x - previous.x) < narrow && Math.abs(current.y - previous.y) < narrow) {
					continue;
				}
				coordinates.add(current);
			} else {
				coordinates.add(current);
			}
			previous = current;
		}
		if (coordinates.size() < 2) {
			log.warn("no segments found");
			return sections;
		}

		previous = null;
		Collections.sort(coordinates, COORDINATE_SORTER);
		for (OrderedCoordinate current : coordinates) {
			if (previous != null) {
				LineSegment segment = new LineSegment(previous, current);
				segments.add(segment);
			}
			previous = current;
		}

		int segmentRank = 0;
		previous = null;
		String prefix = journeyPattern.objectIdPrefix();
		StopArea previousLocation = null;
		for (StopPoint stop : journeyPattern.getStopPoints()) {
			// find nearest segment and project point on it
			StopArea location = stop.getContainedInStopArea();
			Coordinate point = new Coordinate(location.getLongitude().doubleValue(), location.getLatitude()
					.doubleValue());
			double distance_min = Double.MAX_VALUE;
			int rank = 0;
			for (int i = segmentRank; i < segments.size(); i++) {
				double distance = segments.get(i).distance(point);
				if (distance < distance_min) {
					distance_min = distance;
					rank = i;
				}
			}
			// compose routeSection
			Coordinate projection = null;
			boolean lastSegmentIncluded = false;
			double factor = segments.get(rank).projectionFactor(point);
			int intFactor = (int) (factor * 100.);
			if (factor <= 0.05) {
				// projection near or before first point
				projection = segments.get(rank).getCoordinate(0);
				intFactor = 0;
			} else if (factor >= 0.95) {
				// projection near or after last point
				projection = segments.get(rank).getCoordinate(1);
				lastSegmentIncluded = true;
				intFactor = 100;
			} else {
				// projection inside segment
				projection = segments.get(rank).project(point);
			}
			if (previous != null) {
				List<Coordinate> coords = new ArrayList<>();
				coords.add(previous);
				for (int i = segmentRank; i < rank; i++) {
					coords.add(segments.get(i).getCoordinate(1));
				}
				coords.add(projection);
				if (lastSegmentIncluded)
					rank++;
				String routeSectionId = prefix + ":" + RouteSection.ROUTE_SECTION_KEY + ":" + shapeId + "_"
						+ previousLocation.objectIdSuffix() + "_" + location.objectIdSuffix() + "_" + intFactor;
				RouteSection section = ObjectFactory.getRouteSection(referential, routeSectionId);
				if (!section.isFilled()) {
					Coordinate[] inputCoords = new Coordinate[2];
					section.setDeparture(previousLocation);
					inputCoords[0] = new Coordinate(previousLocation.getLongitude().doubleValue(), previousLocation
							.getLatitude().doubleValue());
					section.setArrival(location);
					inputCoords[1] = new Coordinate(location.getLongitude().doubleValue(), location.getLatitude()
							.doubleValue());
					section.setProcessedGeometry(factory.createLineString(coords.toArray(new Coordinate[coords.size()])));
					section.setInputGeometry(factory.createLineString(inputCoords));
					section.setNoProcessing(false);
					try {
						double distance = section.getProcessedGeometry().getLength();
						distance *= (Math.PI / 180) * 6378137;
						section.setDistance(BigDecimal.valueOf(distance));
					} catch (NumberFormatException e) {
						log.error(shapeId + " : problem with section between " + previousLocation.getName() + "("
								+ previousLocation.getObjectId() + " and " + location.getName() + "("
								+ location.getObjectId());
						log.error("coords (" + coords.size() + ") :");
						for (Coordinate coordinate : coords) {
							log.error("lat = " + coordinate.y + " , lon = " + coordinate.x);
						}
						sections.clear();
						return sections;
					}
				}
				section.setFilled(true);
				sections.add(section);
			}
			previous = projection;
			previousLocation = location;
			segmentRank = rank;

		}

		return sections;
	}

	/**
	 * create route for trip
	 *
	 * @param referential
	 * @param configuration
	 * @param gtfsTrip
	 * @return
	 */
	private Route createRoute(Referential referential, GtfsImportParameters configuration, GtfsTrip gtfsTrip) {
		String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				gtfsTrip.getRouteId(), log);
		Line line = ObjectFactory.getLine(referential, lineId);
		String routeKey = gtfsTrip.getRouteId() + "_" + gtfsTrip.getDirectionId().ordinal();
		if (gtfsTrip.getShapeId() != null && !gtfsTrip.getShapeId().isEmpty())
			routeKey += "_" + gtfsTrip.getShapeId();
		routeKey += "_" + line.getRoutes().size();
		String routeId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.ROUTE_KEY,
				routeKey, log);

		Route route = ObjectFactory.getRoute(referential, routeId);
		route.setLine(line);
		String wayBack = gtfsTrip.getDirectionId().equals(DirectionType.Outbound) ? "A" : "R";
		route.setWayBack(wayBack);
		return route;
	}

	protected void convert(Context context, GtfsStopTime gtfsStopTime, VehicleJourneyAtStop vehicleJourneyAtStop) {

		Referential referential = (Referential) context.get(REFERENTIAL);

		vehicleJourneyAtStop.setId(Long.valueOf(gtfsStopTime.getId().longValue()));

		String objectId = gtfsStopTime.getStopId();
		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, objectId);
		vehicleJourneyAtStop.setStopPoint(stopPoint);
		vehicleJourneyAtStop.setArrivalTime(gtfsStopTime.getArrivalTime().getTime());
		vehicleJourneyAtStop.setDepartureTime(gtfsStopTime.getDepartureTime().getTime());

		/**
		 * GJT : Setting arrival and departure offset to vehicleJourneyAtStop
		 * object
		 */
		vehicleJourneyAtStop.setArrivalDayOffset(gtfsStopTime.getArrivalTime().getDay());
		vehicleJourneyAtStop.setDepartureDayOffset(gtfsStopTime.getDepartureTime().getDay());
	}

	protected void convert(Context context, GtfsTrip gtfsTrip, VehicleJourney vehicleJourney) {

		if (gtfsTrip.getTripShortName() != null) {
			try {
				vehicleJourney.setNumber(Long.parseLong(gtfsTrip.getTripShortName()));
			} catch (NumberFormatException e) {
				vehicleJourney.setNumber(Long.valueOf(0));
				vehicleJourney.setPublishedJourneyName(gtfsTrip.getTripShortName());
			}
		}

		if (gtfsTrip.getWheelchairAccessible() != null) {
			switch (gtfsTrip.getWheelchairAccessible()) {
			case NoInformation:
				vehicleJourney.setMobilityRestrictedSuitability(null);
				break;
			case NoAllowed:
				vehicleJourney.setMobilityRestrictedSuitability(Boolean.FALSE);
				break;
			case Allowed:
				vehicleJourney.setMobilityRestrictedSuitability(Boolean.TRUE);
				break;
			}
		}
		vehicleJourney.setFilled(true);

	}

	/**
	 * create stopPoints for Route
	 *
	 * @param referential
	 * @param configuration
	 *
	 * @param routeId
	 *            route objectId
	 * @param stopTimesOfATrip
	 *            first trip's ordered GTFS StopTimes
	 * @param mapStopAreasByStopId
	 *            stopAreas to attach created StopPoints (parent relationship)
	 * @return
	 */
	private void createStopPoint(Route route, JourneyPattern journeyPattern, List<VehicleJourneyAtStop> list,
			Referential referential, GtfsImportParameters configuration) {
		Set<String> stopPointKeys = new HashSet<String>();

		int position = 0;
		for (VehicleJourneyAtStop vehicleJourneyAtStop : list) {
			VehicleJourneyAtStopWrapper wrapper = (VehicleJourneyAtStopWrapper) vehicleJourneyAtStop;
			String baseKey = route.getObjectId().replace(Route.ROUTE_KEY, StopPoint.STOPPOINT_KEY) + "a"
					+ wrapper.stopId.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
			String stopKey = baseKey;
			int dup = 1;
			while (stopPointKeys.contains(stopKey)) {
				stopKey = baseKey + "_" + (dup++);
			}
			stopPointKeys.add(stopKey);

			StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopKey);

			String stopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					StopArea.STOPAREA_KEY, wrapper.stopId, log);
			StopArea stopArea = ObjectFactory.getStopArea(referential, stopAreaId);
			stopPoint.setContainedInStopArea(stopArea);
			stopPoint.setRoute(route);
			stopPoint.setPosition(position++);

			journeyPattern.addStopPoint(stopPoint);
			stopPoint.setFilled(true);
		}
		NeptuneUtil.refreshDepartureArrivals(journeyPattern);
	}

	@AllArgsConstructor
	class VehicleJourneyAtStopWrapper extends VehicleJourneyAtStop {

		private static final long serialVersionUID = 5052093726657799027L;
		String stopId;
		int stopSequence;
		Float shapeDistTraveled;
	}

	public static final Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_COMPARATOR = new Comparator<VehicleJourneyAtStop>() {
		@Override
		public int compare(VehicleJourneyAtStop right, VehicleJourneyAtStop left) {
			int rightIndex = ((VehicleJourneyAtStopWrapper) right).stopSequence;
			int leftIndex = ((VehicleJourneyAtStopWrapper) left).stopSequence;
			return rightIndex - leftIndex;
		}
	};

	class OrderedCoordinate extends Coordinate {
		private static final long serialVersionUID = 1L;
		public int order;

		public OrderedCoordinate(double x, double y, Integer order) {
			this.x = x;
			this.y = y;
			this.order = order.intValue();
		}
	};

	static class OrderedCoordinateComparator implements Comparator<OrderedCoordinate> {
		@Override
		public int compare(OrderedCoordinate o1, OrderedCoordinate o2) {

			return o1.order - o2.order;
		}
	}

	static {
		ParserFactory.register(GtfsTripParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsTripParser();
			}
		});
	}
}
