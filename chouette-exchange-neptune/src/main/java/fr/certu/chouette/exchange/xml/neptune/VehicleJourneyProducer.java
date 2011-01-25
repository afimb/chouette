package fr.certu.chouette.exchange.xml.neptune;

import java.util.Date;

import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class VehicleJourneyProducer extends AbstractModelProducer<VehicleJourney, chouette.schema.VehicleJourney> {

	@Override
	public VehicleJourney produce(chouette.schema.VehicleJourney xmlVehicleJourney) {
		VehicleJourney vehicleJourney = new VehicleJourney();
		
		// objectId, objectVersion, creatorId, creationTime
		populateTridentObject(vehicleJourney, xmlVehicleJourney);
		
		// Comment optional
		vehicleJourney.setComment(getNonEmptyTrimedString(xmlVehicleJourney.getComment()));
		
		// Facility
		vehicleJourney.setFacility(getNonEmptyTrimedString(xmlVehicleJourney.getFacility()));
		
		// JourneyPatternId
		vehicleJourney.setJourneyPatternId(getNonEmptyTrimedString(xmlVehicleJourney.getJourneyPatternId()));

		// Number
		vehicleJourney.setNumber(xmlVehicleJourney.getNumber());
		
		// OperatorId
		vehicleJourney.setOperatorId(getNonEmptyTrimedString(xmlVehicleJourney.getOperatorId()));
		
		// PublishedJourneyIdentifier
		vehicleJourney.setPublishedJourneyIdentifier(getNonEmptyTrimedString(xmlVehicleJourney.getPublishedJourneyIdentifier()));
		
		// PublishedJourneyName
		vehicleJourney.setPublishedJourneyName(getNonEmptyTrimedString(xmlVehicleJourney.getPublishedJourneyName()));
		
		// RouteId
		vehicleJourney.setRouteId(getNonEmptyTrimedString(xmlVehicleJourney.getRouteId()));
		
		// ServiceStatusValue
		if(xmlVehicleJourney.getStatusValue() != null){
			vehicleJourney.setServiceStatusValue(ServiceStatusValueEnum.fromValue(xmlVehicleJourney.getStatusValue().value()));
		}
		
		// TimeSlotId
		vehicleJourney.setTimeSlotId(getNonEmptyTrimedString(xmlVehicleJourney.getTimeSlotId()));
		
		// TransportMode
		if(xmlVehicleJourney.getTransportMode() != null){
			vehicleJourney.setTransportMode(TransportModeNameEnum.fromValue(xmlVehicleJourney.getTransportMode().value()));
		}
		
		// VehicleTypeIdentifier
		vehicleJourney.setVehicleTypeIdentifier(getNonEmptyTrimedString(xmlVehicleJourney.getVehicleTypeIdentifier()));
		
		//VehicleJourneyAtStops
		for(chouette.schema.VehicleJourneyAtStop  xmlVehicleJourneyAtStop : xmlVehicleJourney.getVehicleJourneyAtStop()){
			VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();
			
			vehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getVehicleJourneyId()));
			vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			
			if (xmlVehicleJourneyAtStop.getBoardingAlightingPossibility() != null) {
				vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.fromValue(xmlVehicleJourneyAtStop.getBoardingAlightingPossibility().value()));
			}
			
			vehicleJourneyAtStop.setConnectingServiceId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getConnectingServiceId()));
			
			vehicleJourneyAtStop.setStopPointId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getStopPointId()));
			
			vehicleJourneyAtStop.setOrder(xmlVehicleJourneyAtStop.getOrder());
			
			if(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice() != null){
				VehicleJourneyAtStopTypeChoiceSequence xmlVehicleJourneyAtStopTypeChoiceSequence = xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence();
			
				if(xmlVehicleJourneyAtStopTypeChoiceSequence != null){
					vehicleJourneyAtStop.setArrivalTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getArrivalTime()));
					vehicleJourneyAtStop.setDepartureTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getDepartureTime()));
					vehicleJourneyAtStop.setWaitingTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getWaitingTime()));
				}
				
				if(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2() != null && xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration() != null){
					vehicleJourneyAtStop.setElapseDuration(new Date(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration().toLong()));
				}
			}
			
			if(xmlVehicleJourneyAtStop.getHeadwayFrequency() != null){
				vehicleJourneyAtStop.setHeadwayFrequency(new Date(xmlVehicleJourneyAtStop.getHeadwayFrequency().toLong()));
			}
			
			vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
		}
		
		return vehicleJourney;
	}

}
