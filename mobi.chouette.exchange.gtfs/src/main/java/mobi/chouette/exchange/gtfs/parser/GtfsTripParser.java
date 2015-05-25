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
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsImportParameters;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.GtfsTrip.DirectionType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.gtfs.model.importer.Index;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.Validator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
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

	@AllArgsConstructor
	class VehicleJourneyAtStopWrapper extends VehicleJourneyAtStop {
		/**
		 * 
		 */
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

	private Referential referential;
	private GtfsImporter importer;

	private GtfsImportParameters configuration;

	@Getter
	@Setter
	private String gtfsRouteId;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(PARSER);
		configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		Map<String, JourneyPattern> journeyPatternByStopSequence = new HashMap<String, JourneyPattern>();

		// VehicleJourney
		Index<GtfsTrip> gtfsTrips = importer.getTripByRoute();

		for (GtfsTrip gtfsTrip : gtfsTrips.values(gtfsRouteId)) {

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
			// timetable.addVehicleJourney(vehicleJourney);

			// JourneyPattern
			String journeyKey = gtfsTrip.getRouteId() + "_" + gtfsTrip.getDirectionId().ordinal();
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
				String stopId = ((VehicleJourneyAtStopWrapper) vehicleJourneyAtStop).stopId;
				journeyKey += "," + stopId;
			}
			JourneyPattern journeyPattern = journeyPatternByStopSequence.get(journeyKey);
			if (journeyPattern == null) {

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
				createStopPoint(route, journeyPattern, vehicleJourney.getVehicleJourneyAtStops());

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
									frequency.getHeadwaySecs() * 1000);
						} else {
							copyVehicleJourney(vehicleJourney, frequency.getEndTime().getTime().getTime(),
									frequency.getHeadwaySecs() * 1000);
						}
					} catch (Exception e) {
						// TODO add report
						log.error("cannot apply frequency ", e);
					}
				}
			}

		}
	}

	@Override
	public void validate(Context context) throws Exception {

		importer = (GtfsImporter) context.get(PARSER);
		ActionReport report = (ActionReport) context.get(REPORT);

		// stop_times.txt
		FileInfo file = new FileInfo(GTFS_STOP_TIMES_FILE,FILE_STATE.OK);
		report.getFiles().add(file);
		try {
			Index<GtfsStopTime> parser = importer.getStopTimeByTrip();
			for (GtfsStopTime bean : parser) {
				parser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
			throw ex;
		}

		// trips.txt
		file = new FileInfo(GTFS_TRIPS_FILE,FILE_STATE.OK);
		report.getFiles().add(file);
		try {
			Index<GtfsTrip> tripParser = importer.getTripById();

			for (GtfsTrip bean : tripParser) {
				tripParser.validate(bean, importer);
			}
		} catch (Exception ex) {
			AbstractConverter.populateFileError(file, ex);
			throw ex;
		}

		// frequencies.txt
		if (importer.hasFrequencyImporter()) {
			file = new FileInfo(GTFS_TRANSFERS_FILE,FILE_STATE.OK);
			report.getFiles().add(file);
			try {
				Index<GtfsFrequency> frequencyParser = importer.getFrequencyByTrip();
				for (GtfsFrequency bean : frequencyParser) {
					frequencyParser.validate(bean, importer);
				}
			} catch (Exception ex) {
				AbstractConverter.populateFileError(file, ex);
				throw ex;
			}
		}

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

	private void copyVehicleJourney(VehicleJourney oldVehicleJourney, long end, long headway) throws Exception {
		VehicleJourneyAtStop first = oldVehicleJourney.getVehicleJourneyAtStops().get(0);
		long start = first.getDepartureTime().getTime();
		long stop = end - start;
		int iter = 1;

		long offset = headway;
		while (offset <= stop) {

			String vehiculeJourneyId = oldVehicleJourney.getObjectId() + "a" + iter;

			VehicleJourney newVehicleJourney = ObjectFactory.getVehicleJourney(referential, vehiculeJourneyId);

			iter++;
//			for (Timetable timetable : newVehicleJourney.getTimetables()) {
//				timetable.addVehicleJourney(newVehicleJourney);
//			}
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
//			for (Timetable tm : oldVehicleJourney.getTimetables()) {
//
//				tm.addVehicleJourney(newVehicleJourney);
//			}
			newVehicleJourney.setFilled(true);
			offset += headway;
		}
		return;
	}

	/**
	 * create stopPoints for Route
	 * 
	 * @param routeId
	 *            route objectId
	 * @param stopTimesOfATrip
	 *            first trip's ordered GTFS StopTimes
	 * @param mapStopAreasByStopId
	 *            stopAreas to attach created StopPoints (parent relationship)
	 * @return
	 */
	private void createStopPoint(Route route, JourneyPattern journeyPattern, List<VehicleJourneyAtStop> list) {
		Set<String> stopPointKeys = new HashSet<String>();

		int position = 0;
		for (VehicleJourneyAtStop vehicleJourneyAtStop : list) {
			VehicleJourneyAtStopWrapper wrapper = (VehicleJourneyAtStopWrapper) vehicleJourneyAtStop;
			String baseKey = route.getObjectId().replace(Route.ROUTE_KEY, StopPoint.STOPPOINT_KEY) + "a"
					+ wrapper.stopId.trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
			String stopKey = baseKey;
			int dup = 1;
			while (stopPointKeys.contains(stopKey)) {
				stopKey = stopKey + "_" + (dup++);
			}
			stopPointKeys.add(stopKey);

			StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopKey);

			String stopAreaId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
					StopArea.STOPAREA_KEY, wrapper.stopId, log);
			StopArea stopArea = ObjectFactory.getStopArea(referential, stopAreaId);
			stopPoint.setContainedInStopArea(stopArea);
			// stopPoint.setName(stopArea.getName());
			stopPoint.setRoute(route);
			stopPoint.setPosition(position++);

			journeyPattern.addStopPoint(stopPoint);
			stopPoint.setFilled(true);
		}
		NeptuneUtil.refreshDepartureArrivals(journeyPattern);
	}

	static {
		ParserFactory.register(GtfsTripParser.class.getName(), new ParserFactory() {
			private GtfsTripParser instance = new GtfsTripParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
