package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.trident.schema.trident.BoardingAlightingPossibilityType;
import org.trident.schema.trident.TransportModeNameType;
import org.trident.schema.trident.VehicleJourneyAtStopType;
import org.trident.schema.trident.VehicleJourneyType;

public class VehicleJourneyProducer extends AbstractJaxbNeptuneProducer<VehicleJourneyType, VehicleJourney> implements
		JsonExtension {

	private static Comparator<VehicleJourneyAtStop> VEHICLE_JOURNEY_AT_STOP_SORTER = new Comparator<VehicleJourneyAtStop>() {

		@Override
		public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2) {
			return o1.getStopPoint().getPosition() - o2.getStopPoint().getPosition();
		}

	};
	
	// @Override
	public VehicleJourneyType produce(VehicleJourney vehicleJourney, boolean addExtension) {
		return produce(vehicleJourney, addExtension, 0);
	}

	public VehicleJourneyType produce(VehicleJourney vehicleJourney, boolean addExtension, int count) {
		VehicleJourneyType jaxbVehicleJourney = tridentFactory.createVehicleJourneyType();

		//
		populateFromModel(jaxbVehicleJourney, vehicleJourney);
		if (count > 0)
			jaxbVehicleJourney.setObjectId(jaxbVehicleJourney.getObjectId()+"-"+count);

		jaxbVehicleJourney.setComment(buildComment(vehicleJourney, addExtension));

		jaxbVehicleJourney.setFacility(getNotEmptyString(vehicleJourney.getFacility()));
		jaxbVehicleJourney.setJourneyPatternId(getNonEmptyObjectId(vehicleJourney.getJourneyPattern()));
		if (vehicleJourney.getNumber() != null)
			jaxbVehicleJourney.setNumber(BigInteger.valueOf(vehicleJourney.getNumber().longValue()));
		jaxbVehicleJourney.setOperatorId(getNonEmptyObjectId(vehicleJourney.getCompany()));
		jaxbVehicleJourney.setPublishedJourneyIdentifier(getNotEmptyString(vehicleJourney
				.getPublishedJourneyIdentifier()));
		jaxbVehicleJourney.setPublishedJourneyName(getNotEmptyString(vehicleJourney.getPublishedJourneyName()));
		jaxbVehicleJourney.setRouteId(getNonEmptyObjectId(vehicleJourney.getRoute()));

		// jaxbVehicleJourney.setTimeSlotId(getNonEmptyObjectId(vehicleJourney
		// .getTimeSlot()));
		if (vehicleJourney.getTransportMode() != null) {
			TransportModeNameEnum transportMode = vehicleJourney.getTransportMode();
			try {
				jaxbVehicleJourney.setTransportMode(TransportModeNameType.fromValue(transportMode.name()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		jaxbVehicleJourney.setVehicleTypeIdentifier(vehicleJourney.getVehicleTypeIdentifier());

		if (vehicleJourney.getVehicleJourneyAtStops() != null) {
			List<VehicleJourneyAtStop> lvjas = vehicleJourney.getVehicleJourneyAtStops();
			Collections.sort(lvjas, VEHICLE_JOURNEY_AT_STOP_SORTER);
			int order = 1;
			Time firstDeparture = null;
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
				if (vehicleJourneyAtStop != null) {
					VehicleJourneyAtStopType jaxbVehicleJourneyAtStop = tridentFactory.createVehicleJourneyAtStopType();
					jaxbVehicleJourneyAtStop.setBoardingAlightingPossibility(buildBoardingAndAlightingPossibility(vehicleJourneyAtStop.getStopPoint()));
					jaxbVehicleJourneyAtStop.setOrder(BigInteger.valueOf(order++));
					jaxbVehicleJourneyAtStop.setStopPointId(getNonEmptyObjectId(vehicleJourneyAtStop.getStopPoint()));
					jaxbVehicleJourneyAtStop.setVehicleJourneyId(jaxbVehicleJourney.getObjectId());
					switch(vehicleJourney.getJourneyCategory()) {
					case Timesheet:
						if (vehicleJourneyAtStop.getArrivalTime() != null) {
							jaxbVehicleJourneyAtStop.setArrivalTime(toCalendar(vehicleJourneyAtStop.getArrivalTime()));
						}
						if (vehicleJourneyAtStop.getDepartureTime() != null) {
							jaxbVehicleJourneyAtStop.setDepartureTime(toCalendar(vehicleJourneyAtStop.getDepartureTime()));
						}
						break;
					case Frequency:
						if (firstDeparture == null)
							firstDeparture = vehicleJourneyAtStop.getDepartureTime();
						
						jaxbVehicleJourneyAtStop.setElapseDuration(toDuration(TimeUtil.substract(vehicleJourneyAtStop.getDepartureTime(), firstDeparture)));
						jaxbVehicleJourneyAtStop.setHeadwayFrequency(toDuration(vehicleJourney.getJourneyFrequencies().get(count).getScheduledHeadwayInterval()));
					}
					jaxbVehicleJourney.getVehicleJourneyAtStop().add(jaxbVehicleJourneyAtStop);
				}
			}
		}
		return jaxbVehicleJourney;
	}

	protected String buildComment(VehicleJourney vj, boolean addExtension) {
		if (!addExtension)
			return getNotEmptyString(vj.getComment());
		try {
			JSONObject jsonComment = new JSONObject();
			if (!isEmpty(vj.getFootnotes())) {
				JSONArray noteRefs = new JSONArray();
				for (Footnote footNote : vj.getFootnotes()) {
					noteRefs.put(footNote.getKey());
				}
				jsonComment.put(FOOTNOTE_REFS, noteRefs);
			}
			if (vj.getFlexibleService() != null) {
				jsonComment.put(FLEXIBLE_SERVICE, vj.getFlexibleService());
			}
			if (vj.getMobilityRestrictedSuitability() != null) {
				jsonComment.put(MOBILITY_RESTRICTION, vj.getMobilityRestrictedSuitability());
			}

			if (jsonComment.length() == 0) {
				return getNotEmptyString(vj.getComment());
			} else {
				if (!isEmpty(vj.getComment())) {
					jsonComment.put(COMMENT, vj.getComment().trim());
				}
			}
			return jsonComment.toString();
		} catch (Exception e) {
			return getNotEmptyString(vj.getComment());
		}
	}

	protected BoardingAlightingPossibilityType buildBoardingAndAlightingPossibility(StopPoint point) {
		if (point.getForAlighting() == null && point.getForBoarding() == null)
			return null;
		AlightingPossibilityEnum forAlighting = point.getForAlighting() == null ? AlightingPossibilityEnum.normal
				: point.getForAlighting();
		BoardingPossibilityEnum forBoarding = point.getForBoarding() == null ? BoardingPossibilityEnum.normal : point
				.getForBoarding();

		switch (forAlighting) {
		case normal:
			switch (forBoarding) {
			case normal:
				return null;
			case forbidden:
				return BoardingAlightingPossibilityType.ALIGHT_ONLY;
			case request_stop:
				return BoardingAlightingPossibilityType.BOARD_ON_REQUEST;
			case is_flexible:
				return null;
			}
		case forbidden:
			switch (forBoarding) {
			case normal:
				return BoardingAlightingPossibilityType.BOARD_ONLY;
			case forbidden:
				return BoardingAlightingPossibilityType.NEITHER_BOARD_OR_ALIGHT;
			case request_stop:
				return BoardingAlightingPossibilityType.BOARD_ONLY;
			case is_flexible:
				return BoardingAlightingPossibilityType.BOARD_ONLY;
			}
		case request_stop:
			switch (forBoarding) {
			case normal:
				return BoardingAlightingPossibilityType.ALIGHT_ON_REQUEST;
			case forbidden:
				return BoardingAlightingPossibilityType.ALIGHT_ONLY;
			case request_stop:
				return BoardingAlightingPossibilityType.BOARD_AND_ALIGHT_ON_REQUEST;
			case is_flexible:
				return BoardingAlightingPossibilityType.ALIGHT_ON_REQUEST;
			}
		case is_flexible:
			switch (forBoarding) {
			case normal:
				return null;
			case forbidden:
				return BoardingAlightingPossibilityType.ALIGHT_ONLY;
			case request_stop:
				return BoardingAlightingPossibilityType.BOARD_ON_REQUEST;
			case is_flexible:
				return null;
			}
		}
		return null;

	}

}
