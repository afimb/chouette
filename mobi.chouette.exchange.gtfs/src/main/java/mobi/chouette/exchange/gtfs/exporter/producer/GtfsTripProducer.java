/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsFrequency;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.DropOffType;
import mobi.chouette.exchange.gtfs.model.GtfsStopTime.PickupType;
import mobi.chouette.exchange.gtfs.model.GtfsTime;
import mobi.chouette.exchange.gtfs.model.GtfsTrip;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.JourneyCategoryEnum;
import mobi.chouette.model.type.SectionStatusEnum;

import org.joda.time.LocalTime;

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
	GtfsFrequency frequency = new GtfsFrequency();
	GtfsShape shape = new GtfsShape();

	public GtfsTripProducer(GtfsExporterInterface exporter) {
		super(exporter);
	}

	/**
	 * produce stoptimes for vehiclejourneyatstops @ TODO see how to manage ITL
	 *
	 * @param vj
	 * @param sharedPrefix
	 * @param changesDestinationDisplay
	 * @return list of stoptimes
	 */
	private boolean saveTimes(VehicleJourney vj, String prefix, String sharedPrefix, boolean keepOriginalId, boolean changesDestinationDisplay, List<VehicleJourneyAtStop> lvjas) {
		if (vj.getVehicleJourneyAtStops().isEmpty())
			return false;
		Line l = vj.getRoute().getLine();

		/**
		 * GJT : Attributes used to handle times after midnight
		 */
		int departureOffset = 0;
		int arrivalOffset = 0;

		String tripId = toGtfsId(vj.getObjectId(), prefix, keepOriginalId);
		time.setTripId(tripId);
		float distance = (float) 0.0;
		List<RouteSection> routeSections = vj.getJourneyPattern().getRouteSections();
		int index = 0;
		for (VehicleJourneyAtStop vjas : lvjas) {

			if (vjas.getStopPoint().getScheduledStopPoint().getContainedInStopArea()!=null){
				time.setStopId(toGtfsId(vjas.getStopPoint().getScheduledStopPoint().getContainedInStopArea().getObjectId(), sharedPrefix, keepOriginalId));
			}

			LocalTime arrival = vjas.getArrivalTime();
			arrivalOffset = vjas.getArrivalDayOffset(); /** GJT */

			if (arrival == null) {
				arrival = vjas.getDepartureTime();
				arrivalOffset = vjas.getDepartureDayOffset(); /** GJT */
			}
			time.setArrivalTime(new GtfsTime(arrival, arrivalOffset)); /** GJT */

			LocalTime departure = vjas.getDepartureTime();
			departureOffset = vjas.getDepartureDayOffset(); /** GJT */
			if (departure == null) {
				departure = vjas.getArrivalTime();
				departureOffset = vjas.getArrivalDayOffset(); /** GJT */
			}
			time.setDepartureTime(new GtfsTime(departure, departureOffset)); /** GJT */

			time.setStopSequence((int) vjas.getStopPoint().getPosition());

			if(changesDestinationDisplay && vjas.getStopPoint().getDestinationDisplay() != null) {
				String stopHeadSign = vjas.getStopPoint().getDestinationDisplay().getFrontTextWithComputedVias();
				if(trip.getTripHeadSign() != null) {
					// Skip if equal to tripHeadSign
					if(!trip.getTripHeadSign().equals(stopHeadSign)) {
						time.setStopHeadsign(stopHeadSign);
					}
				} else {
					// Always set if tripheadSign is null
					time.setStopHeadsign(stopHeadSign);
				}
			}
			addDropOffAndPickUpType(time, l, vj, vjas);

			if (vj.getJourneyPattern().getSectionStatus() == SectionStatusEnum.Completed) {
				Float shapeDistTraveled = new Float(distance);
				time.setShapeDistTraveled(shapeDistTraveled);
				while (index < routeSections.size() && routeSections.get(index) == null) {
					index++;
				}
				if (index < routeSections.size()) {
					distance += (float) computeDistance(routeSections.get(index));
				}
				index++;
			}
			else
			{
			   time.setShapeDistTraveled(null);
			}

			try {
				getExporter().getStopTimeExporter().export(time);
			} catch (Exception e) {
		          log.error("fail to produce stoptime "+e.getClass().getName()+" "+e.getMessage());
				return false;
			}

		}
		return true;
	}

	private double computeDistance(RouteSection section)
	{
		if (isTrue(section.getNoProcessing()) || section.getProcessedGeometry() == null)
		{
			double distance = section.getInputGeometry().getLength();
			distance *= (Math.PI / 180) * 6378137;
			return distance;
		}
		else
		{
			double distance = section.getProcessedGeometry().getLength();
			distance *= (Math.PI / 180) * 6378137;
			return distance;
		}
	}

	private void addDropOffAndPickUpType(GtfsStopTime time, Line l, VehicleJourney vj, VehicleJourneyAtStop vjas) {

		time.setPickupType(null);
		time.setDropOffType(null);
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
		if (point.getScheduledStopPoint().getForBoarding() != null) {
			time.setPickupType(toPickUpType(point.getScheduledStopPoint().getForBoarding(), time.getPickupType()));
		}
		if (point.getScheduledStopPoint().getForAlighting() != null) {
			time.setDropOffType(toDropOffType(point.getScheduledStopPoint().getForAlighting(), time.getDropOffType()));
		}

	}

	private DropOffType toDropOffType(AlightingPossibilityEnum forAlighting, DropOffType defaultValue) {
		if(forAlighting == null) {
			// If not set on StopPoint return defaultValue (that is, the previous value) or if not set; Scheduled
			return defaultValue == null ? DropOffType.Scheduled : defaultValue;
		}

		switch (forAlighting) {
		case normal:
			return DropOffType.Scheduled;
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
		if(forBoarding == null) {
			// If not set on StopPoint return defaultValue (that is, the previous value) or if not set; Scheduled
			return defaultValue == null ? PickupType.Scheduled : defaultValue;
		}

		switch (forBoarding) {
		case normal:
			return PickupType.Scheduled;
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
	public boolean save(VehicleJourney vj, String serviceId,  String prefix, String sharedPrefix, boolean keepOriginalId) {

		String tripId = toGtfsId(vj.getObjectId(), prefix, keepOriginalId);

		trip.setTripId(tripId);

		JourneyPattern jp = vj.getJourneyPattern();
		if (jp.getSectionStatus() == SectionStatusEnum.Completed) {
			String shapeId = toGtfsId(jp.getObjectId(), prefix, keepOriginalId);
			trip.setShapeId(shapeId);
		}
		else
		{
			trip.setShapeId(null);
		}
		Route route = vj.getRoute();
		Line line = route.getLine();
		trip.setRouteId(toGtfsId(line.getObjectId(), prefix, keepOriginalId));
		if ("R".equals(route.getWayBack())) {
			trip.setDirectionId(GtfsTrip.DirectionType.Inbound);
		} else {
			trip.setDirectionId(GtfsTrip.DirectionType.Outbound);
		}

		trip.setServiceId(serviceId);

		// WARN workaround due to missing unique trip.id on NSB data 
//		String name = null;
		if (vj.getNumber() != null && !vj.getNumber().equals(Long.valueOf(0))) {
			trip.setTripShortName(vj.getNumber().toString());
		}
//		} else {
//			name = vj.getPublishedJourneyName();
//		}
//	
//		if (!isEmpty(name))
//			trip.setTripShortName(name);
//		else if (vj.getPublishedJourneyIdentifier() != null)
//			trip.setTripShortName(vj.getPublishedJourneyIdentifier());
		else {
			trip.setTripShortName(null);
		}
		List<VehicleJourneyAtStop> lvjas = new ArrayList<>(vj.getVehicleJourneyAtStops());
		Collections.sort(lvjas, new Comparator<VehicleJourneyAtStop>() {
			@Override
			public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
				return o1.getStopPoint().getPosition().compareTo(o2.getStopPoint().getPosition());
			}
		});


		List<DestinationDisplay> allDestinationDisplays = new ArrayList<>();
		for(VehicleJourneyAtStop vjas : lvjas) {
			if(vjas.getStopPoint().getDestinationDisplay() != null) {
				allDestinationDisplays.add(vjas.getStopPoint().getDestinationDisplay());
			}
		}
		DestinationDisplay startDestinationDisplay = lvjas.get(0).getStopPoint().getDestinationDisplay();
		boolean changesDestinationDisplay = allDestinationDisplays.size() > 1;

		if(startDestinationDisplay != null) {
			trip.setTripHeadSign(startDestinationDisplay.getFrontTextWithComputedVias());
		} else if (!isEmpty(vj.getPublishedJourneyName()))
			trip.setTripHeadSign(vj.getPublishedJourneyName());
		else if (!isEmpty(jp.getPublishedName()))
			trip.setTripHeadSign(jp.getPublishedName());
		else
			trip.setTripHeadSign(null);

		if (vj.getMobilityRestrictedSuitability() != null)
			trip.setWheelchairAccessible(vj.getMobilityRestrictedSuitability() ? GtfsTrip.WheelchairAccessibleType.Allowed
					: GtfsTrip.WheelchairAccessibleType.NoAllowed);
		else
			trip.setWheelchairAccessible(GtfsTrip.WheelchairAccessibleType.NoInformation);
		// trip.setBlockId(...);
		// trip.setBikeAllowed();

		// add StopTimes
		if (saveTimes(vj,  prefix, sharedPrefix, keepOriginalId,changesDestinationDisplay,lvjas)) {
			try {
				getExporter().getTripExporter().export(trip);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return false;
			}
		}

		// add frequencies
		if (JourneyCategoryEnum.Frequency == vj.getJourneyCategory()) {
			for (JourneyFrequency journeyFrequency : vj.getJourneyFrequencies()) { // Don't care about Timebands !
				frequency.setTripId(tripId);
				frequency.setExactTimes(journeyFrequency.getExactTime());
				frequency.setStartTime(new GtfsTime(journeyFrequency.getFirstDepartureTime(), 0));
				if (!journeyFrequency.getFirstDepartureTime().isAfter(journeyFrequency.getLastDepartureTime()))
					frequency.setEndTime(new GtfsTime(journeyFrequency.getLastDepartureTime(), 0));
				else
					frequency.setEndTime(new GtfsTime(journeyFrequency.getLastDepartureTime(), 1));
				frequency.setHeadwaySecs((int) journeyFrequency.getScheduledHeadwayInterval().getStandardSeconds());
				try {
					getExporter().getFrequencyExporter().export(frequency);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					return false;
				}
			}
		}

		return true;
	}

}
