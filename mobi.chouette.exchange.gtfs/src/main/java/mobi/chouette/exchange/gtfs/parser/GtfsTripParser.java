package mobi.chouette.exchange.gtfs.parser;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
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
import mobi.chouette.exchange.gtfs.model.importer.StopById;
import mobi.chouette.exchange.gtfs.validation.Constant;
import mobi.chouette.exchange.gtfs.validation.ValidationReporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.apache.commons.beanutils.BeanUtils;

@Log4j
public class GtfsTripParser implements Parser, Validator, Constant {

	@Getter
	@Setter
	private String gtfsRouteId;

	@Override
	public void validate(Context context) throws Exception {
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		validationReporter.getExceptions().clear();
		
		validateStopTimes(context);
		validateShapes(context);
		validateTrips(context);
		validateFrequencies(context);
	}
	
	private void validateStopTimes(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		Set<String> stopIds = new HashSet<String>();
		
		// stop_times.txt
		if (importer.hasStopTimeImporter()) { // the file "stop_times.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_STOP_TIMES_FILE);

			Index<GtfsStopTime> stopTimeParser = null;
			try { // Read and check the header line of the file "stop_times.txt"
				stopTimeParser = importer.getStopTimeByTrip();
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_STOP_TIMES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_STOP_TIMES_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_STOP_TIMES_FILE);
			
			if (stopTimeParser == null) { // importer.getStopTimeByTrip() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate StopTimeByTrip class"), GTFS_STOP_TIMES_FILE);
			} else {
				validationReporter.validate(context, GTFS_STOP_TIMES_FILE, stopTimeParser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!stopTimeParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, stopTimeParser.getErrors(), GTFS_STOP_TIMES_FILE);
				stopTimeParser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_STOP_TIMES_FILE);
		
			if (stopTimeParser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_STOP_TIMES_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_STOP_TIMES_FILE);
			} else {
				validationReporter.validate(context, GTFS_STOP_TIMES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
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
						validationReporter.reportError(context, (GtfsException)ex, GTFS_STOP_TIMES_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_STOP_TIMES_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_STOP_TIMES_FILE);
				validationReporter.validate(context, GTFS_STOP_TIMES_FILE, bean.getOkTests());
			}
			stopTimeParser.setWithValidation(false);
			int i = 1;
			boolean unsuedId = true;
			for (GtfsStop bean : importer.getStopById()) {
				if (LocationType.Stop.equals(bean.getLocationType())) {
					if (stopIds.add(bean.getStopId())) {
						unsuedId = false;
						validationReporter.reportError(context, new GtfsException(GTFS_STOPS_FILE, i, StopById.FIELDS.stop_id.name(), GtfsException.ERROR.UNUSED_ID, null, bean.getStopId()), GTFS_STOP_TIMES_FILE);
					}
				}
				i++;
			}
			if (unsuedId)
				validationReporter.validate(context, GTFS_STOP_TIMES_FILE, GtfsException.ERROR.UNUSED_ID);
			validationReporter.getExceptions().clear();
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_STOP_TIMES_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_STOP_TIMES_FILE);
		}
	}
	
	private void validateShapes(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
				
		// shapes.txt
		if (importer.hasShapeImporter()) {
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_SHAPES_FILE);
			
			Index<GtfsShape> shapeParser = null;
			try { // Read and check the header line of the file "shapes.txt"
				shapeParser = importer.getShapeById(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_SHAPES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_SHAPES_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_SHAPES_FILE);
			
			if (shapeParser == null) { // importer.getShapeById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate ShapeById class"), GTFS_SHAPES_FILE);
			} else {
				validationReporter.validate(context, GTFS_SHAPES_FILE, shapeParser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!shapeParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, shapeParser.getErrors(), GTFS_SHAPES_FILE);
				shapeParser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_SHAPES_FILE);
			
			if (shapeParser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_SHAPES_FILE, 1, null, GtfsException.ERROR.OPTIONAL_FILE_WITH_NO_ENTRY, null, null), GTFS_SHAPES_FILE);
			} else {
				validationReporter.validate(context, GTFS_SHAPES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
			
			GtfsException fatalException = null;
			shapeParser.setWithValidation(true);
			for (GtfsShape bean : shapeParser) {
				try {
					shapeParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_SHAPES_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_SHAPES_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_SHAPES_FILE);
				validationReporter.validate(context, GTFS_SHAPES_FILE, bean.getOkTests());
			}
			shapeParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_SHAPES_FILE, 1, null, GtfsException.ERROR.MISSING_OPTIONAL_FILE, null, null), GTFS_SHAPES_FILE);
		}
	}
	
	private void validateTrips(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);
		Set<String> routeIds = new HashSet<String>();
		
		// trips.txt
		if (importer.hasTripImporter()) { // the file "trips.txt" exists ?
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_TRIPS_FILE);
			
			Index<GtfsTrip> tripParser = null;
			try { // Read and check the header line of the file "trips.txt"
				tripParser = importer.getTripById(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_TRIPS_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_TRIPS_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_TRIPS_FILE);
		
			if (tripParser == null) { // importer.getTripById() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate TripById class"), GTFS_TRIPS_FILE);
			} else {
				validationReporter.validate(context, GTFS_TRIPS_FILE, tripParser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!tripParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, tripParser.getErrors(), GTFS_TRIPS_FILE);
				tripParser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_TRIPS_FILE);
		
			if (tripParser.getLength() == 0) {
				validationReporter.reportError(context, new GtfsException(GTFS_TRIPS_FILE, 1, null, GtfsException.ERROR.FILE_WITH_NO_ENTRY, null, null), GTFS_TRIPS_FILE);
			} else {
				validationReporter.validate(context, GTFS_TRIPS_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
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
						validationReporter.reportError(context, (GtfsException)ex, GTFS_TRIPS_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_TRIPS_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_TRIPS_FILE);
				validationReporter.validate(context, GTFS_TRIPS_FILE, bean.getOkTests());
			}
			tripParser.setWithValidation(false);
			int i = 1;
			boolean unsuedId = true;
			for (GtfsRoute bean : importer.getRouteById()) {
				if (routeIds.add(bean.getRouteId())) {
					unsuedId = false;
					validationReporter.reportError(context, new GtfsException(GTFS_ROUTES_FILE, i, RouteById.FIELDS.route_id.name(), GtfsException.ERROR.UNUSED_ID, null, bean.getRouteId()), GTFS_TRIPS_FILE);
				}
				i++;
			}
			if (unsuedId)
				validationReporter.validate(context, GTFS_ROUTES_FILE, GtfsException.ERROR.UNUSED_ID);
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_TRIPS_FILE, 1, null, GtfsException.ERROR.MISSING_FILE, null, null), GTFS_TRIPS_FILE);
		}
	}
	
	private void validateFrequencies(Context context) throws Exception {
		GtfsImporter importer = (GtfsImporter) context.get(PARSER);
		ValidationReporter validationReporter = (ValidationReporter) context.get(GTFS_REPORTER);

		// frequencies.txt
		if (importer.hasFrequencyImporter()) {
			validationReporter.reportSuccess(context, GTFS_1_GTFS_Common_1, GTFS_FREQUENCIES_FILE);
			
			Index<GtfsFrequency> frequencyParser = null;
			try { // Read and check the header line of the file "frequenciess.txt"
				frequencyParser = importer.getFrequencyByTrip(); 
			} catch (Exception ex ) {
				if (ex instanceof GtfsException) {
					validationReporter.reportError(context, (GtfsException)ex, GTFS_FREQUENCIES_FILE);
				} else {
					validationReporter.throwUnknownError(context, ex, GTFS_FREQUENCIES_FILE);
				}
			}

			validationReporter.validateOkCSV(context, GTFS_FREQUENCIES_FILE);
			
			if (frequencyParser == null) { // importer.getFrequencyByTrip() fails for any other reason
				validationReporter.throwUnknownError(context, new Exception("Cannot instantiate FrequencyByTrip class"), GTFS_FREQUENCIES_FILE);
			} else {
				validationReporter.validate(context, GTFS_FREQUENCIES_FILE, frequencyParser.getOkTests());
				validationReporter.validateUnknownError(context);
			}
			
			if (!frequencyParser.getErrors().isEmpty()) {
				validationReporter.reportErrors(context, frequencyParser.getErrors(), GTFS_FREQUENCIES_FILE);
				frequencyParser.getErrors().clear();
			}
			
			validationReporter.validateOKGeneralSyntax(context, GTFS_FREQUENCIES_FILE);
			
			if (frequencyParser.getLength() == 0) {
				//validationReporter.reportUnsuccess(context, GTFS_1_GTFS_Frequency_1, GTFS_FREQUENCIES_FILE);
				validationReporter.reportError(context, new GtfsException(GTFS_FREQUENCIES_FILE, 1, null, GtfsException.ERROR.OPTIONAL_FILE_WITH_NO_ENTRY, null, null), GTFS_FREQUENCIES_FILE);
			} else {
				validationReporter.validate(context, GTFS_FREQUENCIES_FILE, GtfsException.ERROR.FILE_WITH_NO_ENTRY);
			}
			
			GtfsException fatalException = null;
			frequencyParser.setWithValidation(true);
			for (GtfsFrequency bean : frequencyParser) {
				try {
					frequencyParser.validate(bean, importer);
				} catch (Exception ex) {
					if (ex instanceof GtfsException) {
						validationReporter.reportError(context, (GtfsException)ex, GTFS_FREQUENCIES_FILE);
					} else {
						validationReporter.throwUnknownError(context, ex, GTFS_FREQUENCIES_FILE);
					}
				}
				for(GtfsException ex : bean.getErrors()) {
					if (ex.isFatal())
						fatalException = ex;
				}
				validationReporter.reportErrors(context, bean.getErrors(), GTFS_FREQUENCIES_FILE);
				validationReporter.validate(context, GTFS_FREQUENCIES_FILE, bean.getOkTests());
			}
			frequencyParser.setWithValidation(false);
			if (fatalException != null)
				throw fatalException;
		} else {
			validationReporter.reportError(context, new GtfsException(GTFS_FREQUENCIES_FILE, 1, null, GtfsException.ERROR.MISSING_OPTIONAL_FILE, null, null), GTFS_FREQUENCIES_FILE);
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

			if (!importer.getStopTimeByTrip().values(gtfsTrip.getTripId()).iterator().hasNext())
			{
				continue;
			}
			boolean hasTimes = true;
			for (GtfsStopTime gtfsStopTime : importer.getStopTimeByTrip().values(gtfsTrip.getTripId())) {
				if (gtfsStopTime.getArrivalTime() == null) 
				{
					hasTimes = false;
					break;
				}
				if (gtfsStopTime.getDepartureTime() == null) 
				{
					hasTimes = false;
					break;
				}
			}
			if (!hasTimes) continue;
			
			String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					VehicleJourney.VEHICLEJOURNEY_KEY, gtfsTrip.getTripId(), log);
			VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, objectId);
			convert(context, gtfsTrip, vehicleJourney);

			// VehicleJourneyAtStop
			boolean afterMidnight = true;

			for (GtfsStopTime gtfsStopTime : importer.getStopTimeByTrip().values(gtfsTrip.getTripId())) {
				VehicleJourneyAtStopWrapper vehicleJourneyAtStop = new VehicleJourneyAtStopWrapper(
						gtfsStopTime.getStopId(), gtfsStopTime.getStopSequence());
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
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
				String stopId = ((VehicleJourneyAtStopWrapper) vehicleJourneyAtStop).stopId;
				journeyKey += "," + stopId;
			}
			JourneyPattern journeyPattern = journeyPatternByStopSequence.get(journeyKey);
			if (journeyPattern == null) {

				journeyPattern = createJourneyPattern(referential, configuration, gtfsTrip, vehicleJourney, journeyKey,
						journeyPatternByStopSequence);

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

				for (GtfsFrequency frequency : importer.getFrequencyByTrip().values(gtfsTrip.getTripId())) {
					baseVehicleJourneyToTime(vehicleJourney, frequency.getStartTime().getTime().getTime());
					try {
						if (!frequency.getStartTime().moreOneDay() && frequency.getEndTime().moreOneDay()) {

							copyVehicleJourney(vehicleJourney,
									frequency.getEndTime().getTime().getTime() + 24 * 3600 * 1000,
									frequency.getHeadwaySecs() * 1000, referential);
						} else {
							copyVehicleJourney(vehicleJourney, frequency.getEndTime().getTime().getTime(),
									frequency.getHeadwaySecs() * 1000, referential);
						}
					} catch (Exception e) {
						// TODO add report
						log.error("cannot apply frequency ", e);
					}
				}
			}

		}
		// dispose collections
		journeyPatternByStopSequence.clear();
	}

	private JourneyPattern createJourneyPattern(Referential referential, GtfsImportParameters configuration,
			GtfsTrip gtfsTrip, VehicleJourney vehicleJourney, String journeyKey,
			Map<String, JourneyPattern> journeyPatternByStopSequence) {
		JourneyPattern journeyPattern;
		String lineId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Line.LINE_KEY,
				gtfsTrip.getRouteId(), log);
		Line line = ObjectFactory.getLine(referential, lineId);

		// Route
		String routeId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), Route.ROUTE_KEY,
				gtfsTrip.getRouteId() + "_" + gtfsTrip.getDirectionId().ordinal() + line.getRoutes().size(),
				log);

		Route route = ObjectFactory.getRoute(referential, routeId);
		route.setLine(line);
		String wayBack = gtfsTrip.getDirectionId().equals(DirectionType.Outbound) ? "A" : "R";
		route.setWayBack(wayBack);

		// JourneyPattern
		String journeyPatternId = route.getObjectId().replace(Route.ROUTE_KEY,
				JourneyPattern.JOURNEYPATTERN_KEY);
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
				String last = route.getStopPoints().get(route.getStopPoints().size() - 1)
						.getContainedInStopArea().getName();
				route.setName(first + " -> " + last);
			}
		}
		return journeyPattern;
	}

	protected void convert(Context context, GtfsStopTime gtfsStopTime, VehicleJourneyAtStop vehicleJourneyAtStop) {

		Referential referential = (Referential) context.get(REFERENTIAL);

		vehicleJourneyAtStop.setId(Long.valueOf(gtfsStopTime.getId().longValue()));

		String objectId = gtfsStopTime.getStopId();
		StopPoint stopPoint = ObjectFactory.getStopPoint(referential, objectId);
		vehicleJourneyAtStop.setStopPoint(stopPoint);
		vehicleJourneyAtStop.setArrivalTime(gtfsStopTime.getArrivalTime().getTime());
		vehicleJourneyAtStop.setDepartureTime(gtfsStopTime.getDepartureTime().getTime());
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

	private void baseVehicleJourneyToTime(VehicleJourney vehicleJourney, long t) {
		VehicleJourneyAtStop first = vehicleJourney.getVehicleJourneyAtStops().get(0);
		long depOffset = t - first.getDepartureTime().getTime();
		long arrOffset = t - first.getArrivalTime().getTime();

		for (VehicleJourneyAtStop vjas : vehicleJourney.getVehicleJourneyAtStops()) {
			vjas.setArrivalTime(shiftTime(vjas.getArrivalTime(), arrOffset));
			vjas.setDepartureTime(shiftTime(vjas.getDepartureTime(), depOffset));
		}
	}

	private Time shiftTime(Time t, long offset) {
		return new Time((t.getTime() + offset) % (24 * 3600 * 1000));
	}

	private void copyVehicleJourney(VehicleJourney oldVehicleJourney, long end, long headway, Referential referential) throws Exception {
		VehicleJourneyAtStop first = oldVehicleJourney.getVehicleJourneyAtStops().get(0);
		long start = first.getDepartureTime().getTime();
		long stop = end - start;
		int iter = 1;

		long offset = headway;
		while (offset <= stop) {

			String vehiculeJourneyId = oldVehicleJourney.getObjectId() + "a" + iter;

			VehicleJourney newVehicleJourney = ObjectFactory.getVehicleJourney(referential, vehiculeJourneyId);

			iter++;

			List<VehicleJourneyAtStop> vjass = oldVehicleJourney.getVehicleJourneyAtStops();
			for (VehicleJourneyAtStop vjas : vjass) {

				VehicleJourneyAtStop nvjas = new VehicleJourneyAtStop();
				BeanUtils.copyProperties(nvjas, vjas);
				nvjas.setVehicleJourney(newVehicleJourney);
				nvjas.setArrivalTime(shiftTime(nvjas.getArrivalTime(), offset));
				nvjas.setDepartureTime(shiftTime(nvjas.getDepartureTime(), offset));
				nvjas.setVehicleJourney(newVehicleJourney);
			}
			newVehicleJourney.setRoute(oldVehicleJourney.getRoute());
			newVehicleJourney.setJourneyPattern(oldVehicleJourney.getJourneyPattern());
			newVehicleJourney.getTimetables().addAll(oldVehicleJourney.getTimetables());
			newVehicleJourney.setFilled(true);
			offset += headway;
		}
		return;
	}

	/**
	 * create stopPoints for Route
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
	private void createStopPoint(Route route, JourneyPattern journeyPattern, List<VehicleJourneyAtStop> list, Referential referential, GtfsImportParameters configuration) {
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
	}

	public static final Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_COMPARATOR = new Comparator<VehicleJourneyAtStop>() {
		@Override
		public int compare(VehicleJourneyAtStop right, VehicleJourneyAtStop left) {
			int rightIndex = ((VehicleJourneyAtStopWrapper) right).stopSequence;
			int leftIndex = ((VehicleJourneyAtStopWrapper) left).stopSequence;
			return rightIndex - leftIndex;
		}
	};

	static {
		ParserFactory.register(GtfsTripParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new GtfsTripParser();
			}
		});
	}
}
