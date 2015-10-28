/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.DropOffType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.PickupType;
import mobi.chouette.exchange.gtfs.model.GtfsTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

/**
 * produce Trips and stop_times for vehicleJourney
 * <p>
 * when vehicleJourney is on multiple timetables, it will be cloned for each
 * 
 * @ TODO : refactor to produce one calendar for each timetable groups
 */
@Log4j
public class GtfsTripProducer extends AbstractProducer {

	GtfsTrip trip = new GtfsTrip();
	GtfsStopTime time = new GtfsStopTime();

	public GtfsTripProducer(GtfsExporterInterface exporter) {
		super(exporter);
	}

	/**
	 * produce stoptimes for vehiclejourneyatstops @ TODO see how to manage ITL
	 * 
	 * @param vj
	 * @param sharedPrefix
	 * @return list of stoptimes
	 */
	private boolean saveTimes(VehicleJourney vj, ActionReport report, String prefix, String sharedPrefix) {
		if (vj.getVehicleJourneyAtStops().isEmpty())
			return false;
		Line l = vj.getRoute().getLine();
		Integer zero = Integer.valueOf(0);
		Integer one = Integer.valueOf(1);
		Integer tomorrowArrival = zero;
		Time previousArrival = null;
		Integer tomorrowDeparture = zero;
		Time previousDeparture = null;
		String tripId = toGtfsId(vj.getObjectId(), prefix);
		time.setTripId(tripId);
		List<VehicleJourneyAtStop> lvjas = new ArrayList<>(vj.getVehicleJourneyAtStops());
		Collections.sort(lvjas, new Comparator<VehicleJourneyAtStop>() {
			@Override
			public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
				return o1.getStopPoint().getPosition().compareTo(o2.getStopPoint().getPosition());
			}
		});
		for (VehicleJourneyAtStop vjas : lvjas) {
			time.setStopId(toGtfsId(vjas.getStopPoint().getContainedInStopArea().getObjectId(), sharedPrefix));
			Time arrival = vjas.getArrivalTime();
			if (arrival == null)
				arrival = vjas.getDepartureTime();
			if (tomorrowArrival != one && previousArrival != null && previousArrival.after(arrival)) {
				tomorrowArrival = one; // after midnight
			}
			previousArrival = arrival;
			time.setArrivalTime(new GtfsTime(arrival, tomorrowArrival));
			Time departure = vjas.getDepartureTime();
			if (tomorrowDeparture != one && previousDeparture != null && previousDeparture.after(departure)) {
				tomorrowDeparture = one; // after midnight
			}
			time.setDepartureTime(new GtfsTime(departure, tomorrowDeparture));
			previousDeparture = departure;
			time.setStopSequence((int) vjas.getStopPoint().getPosition());

			// time.setStopHeadsign();
			addDropOffAndPickUpType(time, l, vj, vjas);
			// time.setShapeDistTravelled()

			try {
				getExporter().getStopTimeExporter().export(time);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
				return false;
			}

		}
		return true;
	}

	private void addDropOffAndPickUpType(GtfsStopTime time, Line l, VehicleJourney vj, VehicleJourneyAtStop vjas) {

		boolean routeOnDemand = isTrue(l.getFlexibleService());
		boolean tripOnDemand = false;
		if (routeOnDemand) {
			// line is on demand, check if trip is not explicitly regular
			tripOnDemand = vj.getFlexibleService() == null || vj.getFlexibleService();
		} else {
			// line is regular or undefined , check if trip is explicitly on
			// demand
			tripOnDemand = isTrue(vj.getFlexibleService());
		}
		if (tripOnDemand) {
			time.setPickupType(PickupType.AgencyCall);
			time.setDropOffType(DropOffType.AgencyCall);
		} else if (routeOnDemand) {
			time.setPickupType(PickupType.Scheduled);
			time.setDropOffType(DropOffType.Scheduled);
		}
		// check stoppoint specifications
		StopPoint point = vjas.getStopPoint();
		if (point.getForBoarding() != null) {
			time.setPickupType(toPickUpType(point.getForBoarding(), time.getPickupType()));
		}
		if (point.getForAlighting() != null) {
			time.setDropOffType(toDropOffType(point.getForAlighting(), time.getDropOffType()));
		}

	}

	private DropOffType toDropOffType(AlightingPossibilityEnum forAlighting, DropOffType defaultValue) {
		switch (forAlighting) {
		case normal:
			return defaultValue == null ? DropOffType.Scheduled : defaultValue;
		case forbidden:
			return DropOffType.NoAvailable;
		case is_flexible:
			return DropOffType.AgencyCall;
		case request_stop:
			return DropOffType.DriverCall;
		}
		return defaultValue;
	}

	private PickupType toPickUpType(BoardingPossibilityEnum forBoarding, PickupType defaultValue) {
		switch (forBoarding) {
		case normal:
			return defaultValue == null ? PickupType.Scheduled : defaultValue;
		case forbidden:
			return PickupType.NoAvailable;
		case is_flexible:
			return PickupType.AgencyCall;
		case request_stop:
			return PickupType.DriverCall;
		}
		return defaultValue;
	}

	/**
	 * convert vehicle journey to trip for a specific timetable
	 * 
	 * @param vj
	 *            vehicle journey
	 * @param sharedPrefix
	 * @param timetableId
	 *            timetable id
	 * @param times
	 *            stoptimes model
	 * @param multipleTimetable
	 *            vehicle journey with multiple timetables
	 * @return gtfs trip
	 */
	public boolean save(VehicleJourney vj, String serviceId, ActionReport report, String prefix, String sharedPrefix) {

		String tripId = toGtfsId(vj.getObjectId(), prefix);

		trip.setTripId(tripId);

		JourneyPattern jp = vj.getJourneyPattern();
		Route route = vj.getRoute();
		Line line = route.getLine();
		trip.setRouteId(toGtfsId(line.getObjectId(), prefix));
		if ("R".equals(route.getWayBack())) {
			trip.setDirectionId(GtfsTrip.DirectionType.Inbound);
		} else {
			trip.setDirectionId(GtfsTrip.DirectionType.Outbound);
		}

		trip.setServiceId(serviceId);

		String name = vj.getPublishedJourneyName();
		if (isEmpty(name) && vj.getNumber() != null && !vj.getNumber().equals(Long.valueOf(0)))
			name = "" + vj.getNumber();

		if (!isEmpty(name))
			trip.setTripShortName(name);
		else
			trip.setTripShortName(null);

		if (!isEmpty(jp.getPublishedName()))
			trip.setTripHeadSign(jp.getPublishedName());
		else
			trip.setTripHeadSign(null);

		if (vj.getMobilityRestrictedSuitability() != null)
			trip.setWheelchairAccessible(vj.getMobilityRestrictedSuitability() ? GtfsTrip.WheelchairAccessibleType.Allowed
					: GtfsTrip.WheelchairAccessibleType.NoAllowed);
		else
			trip.setWheelchairAccessible(GtfsTrip.WheelchairAccessibleType.NoInformation);
		// trip.setBlockId(...);
		// trip.setShapeId(...);
		// trip.setBikeAllowed();

		// add StopTimes
		if (saveTimes(vj, report, prefix, sharedPrefix)) {
			try {
				getExporter().getTripExporter().export(trip);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return false;
			}
		}
		return true;
	}

}
