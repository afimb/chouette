package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.exolab.castor.types.Duration;

import chouette.schema.VehicleJourneyAtStopTypeChoice;
import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence;
import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence2;
import chouette.schema.types.BoardingAlightingPossibilityType;
import chouette.schema.types.ServiceStatusValueType;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class VehicleJourneyProducer extends AbstractCastorNeptuneProducer<chouette.schema.VehicleJourney, VehicleJourney> {

	@Override
	public chouette.schema.VehicleJourney produce(VehicleJourney vehicleJourney) {
		chouette.schema.VehicleJourney castorVehicleJourney = new chouette.schema.VehicleJourney();
		
		//
		populateFromModel(castorVehicleJourney, vehicleJourney);
		
		castorVehicleJourney.setComment(getNotEmptyString(vehicleJourney.getComment()));
		castorVehicleJourney.setFacility(getNotEmptyString(vehicleJourney.getFacility()));
		castorVehicleJourney.setJourneyPatternId(getNonEmptyObjectId(vehicleJourney.getJourneyPattern()));
		castorVehicleJourney.setLineIdShortcut(vehicleJourney.getLineIdShortcut());
		castorVehicleJourney.setNumber(vehicleJourney.getNumber());
		castorVehicleJourney.setOperatorId(getNonEmptyObjectId(vehicleJourney.getCompany()));
		castorVehicleJourney.setPublishedJourneyIdentifier(getNotEmptyString(vehicleJourney.getPublishedJourneyIdentifier()));
		castorVehicleJourney.setPublishedJourneyName(getNotEmptyString(vehicleJourney.getPublishedJourneyName()));
		castorVehicleJourney.setRouteId(getNonEmptyObjectId(vehicleJourney.getRoute()));
		if(vehicleJourney.getServiceStatusValue() != null)
		{
			ServiceStatusValueEnum serviceStatusValue = vehicleJourney.getServiceStatusValue();
			try {
				castorVehicleJourney.setStatusValue(ServiceStatusValueType.fromValue(serviceStatusValue.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		castorVehicleJourney.setTimeSlotId(getNonEmptyObjectId(vehicleJourney.getTimeSlot()));
		if(vehicleJourney.getTransportMode() != null){
			TransportModeNameEnum transportMode = vehicleJourney.getTransportMode();
			try {
				castorVehicleJourney.setTransportMode(TransportModeNameType.fromValue(transportMode.value()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		castorVehicleJourney.setVehicleTypeIdentifier(vehicleJourney.getVehicleTypeIdentifier());
		
		if(vehicleJourney.getVehicleJourneyAtStops() != null){
			for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
				if(vehicleJourneyAtStop != null){
					chouette.schema.VehicleJourneyAtStop castorVehicleJourneyAtStop = new chouette.schema.VehicleJourneyAtStop();
					if(vehicleJourneyAtStop.getBoardingAlightingPossibility() != null){
						BoardingAlightingPossibilityEnum boardingAlightingPossibility = vehicleJourneyAtStop.getBoardingAlightingPossibility();
						try {
							castorVehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityType.fromValue(boardingAlightingPossibility.value()));
						} catch (IllegalArgumentException e) {
							// TODO generate report
						}
					}
					castorVehicleJourneyAtStop.setConnectingServiceId(vehicleJourneyAtStop.getConnectingServiceId());
					if(vehicleJourneyAtStop.getHeadwayFrequency() != null){
						castorVehicleJourneyAtStop.setHeadwayFrequency(new Duration(vehicleJourneyAtStop.getHeadwayFrequency().getTime()));
					}
					castorVehicleJourneyAtStop.setOrder(vehicleJourneyAtStop.getOrder());
					castorVehicleJourneyAtStop.setStopPointId(getNonEmptyObjectId(vehicleJourneyAtStop.getStopPoint()));
					castorVehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyObjectId(vehicleJourneyAtStop.getVehicleJourney()));

					VehicleJourneyAtStopTypeChoice castorVehicleJourneyAtStopTypeChoice = new VehicleJourneyAtStopTypeChoice();
					VehicleJourneyAtStopTypeChoiceSequence castorVehicleJourneyAtStopTypeChoiceSequence = new VehicleJourneyAtStopTypeChoiceSequence();
					if(vehicleJourneyAtStop.getArrivalTime() != null){
						castorVehicleJourneyAtStopTypeChoiceSequence.setArrivalTime(toCastorTime(vehicleJourneyAtStop.getArrivalTime()));
					}
					if(vehicleJourneyAtStop.getDepartureTime() != null){
						castorVehicleJourneyAtStopTypeChoiceSequence.setDepartureTime(toCastorTime(vehicleJourneyAtStop.getDepartureTime()));
					}
					if(vehicleJourneyAtStop.getWaitingTime() != null){
						castorVehicleJourneyAtStopTypeChoiceSequence.setWaitingTime(toCastorTime(vehicleJourneyAtStop.getWaitingTime()));
					}
					
					VehicleJourneyAtStopTypeChoiceSequence2 castorVehicleJourneyAtStopTypeChoiceSequence2 = new VehicleJourneyAtStopTypeChoiceSequence2();
					if(vehicleJourneyAtStop.getElapseDuration() != null){
						castorVehicleJourneyAtStopTypeChoiceSequence2.setElapseDuration(new Duration(vehicleJourneyAtStop.getElapseDuration().getTime()));
					}
					
					castorVehicleJourneyAtStopTypeChoice.setVehicleJourneyAtStopTypeChoiceSequence(castorVehicleJourneyAtStopTypeChoiceSequence);
					castorVehicleJourneyAtStopTypeChoice.setVehicleJourneyAtStopTypeChoiceSequence2(castorVehicleJourneyAtStopTypeChoiceSequence2);
					castorVehicleJourneyAtStop.setVehicleJourneyAtStopTypeChoice(castorVehicleJourneyAtStopTypeChoice);
					
					castorVehicleJourney.addVehicleJourneyAtStop(castorVehicleJourneyAtStop);
				}
			}
		}				
		return castorVehicleJourney;
	}

}
