package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.chouette.exchange.neptune.JsonExtension;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
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

	@Override
	public VehicleJourneyType produce(VehicleJourney vehicleJourney, boolean addExtension) {
		VehicleJourneyType jaxbVehicleJourney = tridentFactory.createVehicleJourneyType();

		//
		populateFromModel(jaxbVehicleJourney, vehicleJourney);

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
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()) {
				if (vehicleJourneyAtStop != null) {
					VehicleJourneyAtStopType jaxbVehicleJourneyAtStop = tridentFactory.createVehicleJourneyAtStopType();
					if (vehicleJourneyAtStop.getBoardingAlightingPossibility() != null) {
						BoardingAlightingPossibilityEnum boardingAlightingPossibility = vehicleJourneyAtStop
								.getBoardingAlightingPossibility();
						try {
							jaxbVehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityType
									.fromValue(boardingAlightingPossibility.name()));
						} catch (IllegalArgumentException e) {
							// TODO generate report
						}
					}
					if (vehicleJourneyAtStop.getHeadwayFrequency() != null) {
						jaxbVehicleJourneyAtStop.setHeadwayFrequency(toDuration(vehicleJourneyAtStop
								.getHeadwayFrequency()));
					}
					jaxbVehicleJourneyAtStop.setOrder(BigInteger.valueOf(order++));
					jaxbVehicleJourneyAtStop.setStopPointId(getNonEmptyObjectId(vehicleJourneyAtStop.getStopPoint()));
					jaxbVehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyObjectId(vehicleJourneyAtStop
							.getVehicleJourney()));

					if (vehicleJourneyAtStop.getArrivalTime() != null) {
						jaxbVehicleJourneyAtStop.setArrivalTime(toCalendar(vehicleJourneyAtStop.getArrivalTime()));
					}
					if (vehicleJourneyAtStop.getDepartureTime() != null) {
						jaxbVehicleJourneyAtStop.setDepartureTime(toCalendar(vehicleJourneyAtStop.getDepartureTime()));
					}

					if (vehicleJourneyAtStop.getElapseDuration() != null) {
						jaxbVehicleJourneyAtStop
								.setElapseDuration(toDuration(vehicleJourneyAtStop.getElapseDuration()));
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

}
