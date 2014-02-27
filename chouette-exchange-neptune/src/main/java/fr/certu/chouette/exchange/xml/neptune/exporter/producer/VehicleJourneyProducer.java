package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.math.BigInteger;

import org.trident.schema.trident.BoardingAlightingPossibilityType;
import org.trident.schema.trident.ServiceStatusValueType;
import org.trident.schema.trident.TransportModeNameType;
import org.trident.schema.trident.VehicleJourneyAtStopType;
import org.trident.schema.trident.VehicleJourneyType;

import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class VehicleJourneyProducer extends AbstractJaxbNeptuneProducer<VehicleJourneyType, VehicleJourney> {

	@Override
	public VehicleJourneyType produce(VehicleJourney vehicleJourney) {
		VehicleJourneyType jaxbVehicleJourney = tridentFactory.createVehicleJourneyType();
		
		//
		populateFromModel(jaxbVehicleJourney, vehicleJourney);
		
		jaxbVehicleJourney.setComment(getNotEmptyString(vehicleJourney.getComment()));
		jaxbVehicleJourney.setFacility(getNotEmptyString(vehicleJourney.getFacility()));
		jaxbVehicleJourney.setJourneyPatternId(getNonEmptyObjectId(vehicleJourney.getJourneyPattern()));
		jaxbVehicleJourney.setLineIdShortcut(vehicleJourney.getLineIdShortcut());
		if (vehicleJourney.getNumber() != null)
		   jaxbVehicleJourney.setNumber(BigInteger.valueOf(vehicleJourney.getNumber().longValue()));
		jaxbVehicleJourney.setOperatorId(getNonEmptyObjectId(vehicleJourney.getCompany()));
		jaxbVehicleJourney.setPublishedJourneyIdentifier(getNotEmptyString(vehicleJourney.getPublishedJourneyIdentifier()));
		jaxbVehicleJourney.setPublishedJourneyName(getNotEmptyString(vehicleJourney.getPublishedJourneyName()));
		jaxbVehicleJourney.setRouteId(getNonEmptyObjectId(vehicleJourney.getRoute()));
		if(vehicleJourney.getServiceStatusValue() != null)
		{
			ServiceStatusValueEnum serviceStatusValue = vehicleJourney.getServiceStatusValue();
			try {
				jaxbVehicleJourney.setStatusValue(ServiceStatusValueType.fromValue(serviceStatusValue.name()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		jaxbVehicleJourney.setTimeSlotId(getNonEmptyObjectId(vehicleJourney.getTimeSlot()));
		if(vehicleJourney.getTransportMode() != null){
			TransportModeNameEnum transportMode = vehicleJourney.getTransportMode();
			try {
				jaxbVehicleJourney.setTransportMode(TransportModeNameType.fromValue(transportMode.name()));
			} catch (IllegalArgumentException e) {
				// TODO generate report
			}
		}
		jaxbVehicleJourney.setVehicleTypeIdentifier(vehicleJourney.getVehicleTypeIdentifier());
		
		if(vehicleJourney.getVehicleJourneyAtStops() != null){
			for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
				if(vehicleJourneyAtStop != null){
					VehicleJourneyAtStopType jaxbVehicleJourneyAtStop = tridentFactory.createVehicleJourneyAtStopType();
					if(vehicleJourneyAtStop.getBoardingAlightingPossibility() != null){
						BoardingAlightingPossibilityEnum boardingAlightingPossibility = vehicleJourneyAtStop.getBoardingAlightingPossibility();
						try {
							jaxbVehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityType.fromValue(boardingAlightingPossibility.name()));
						} catch (IllegalArgumentException e) {
							// TODO generate report
						}
					}
					jaxbVehicleJourneyAtStop.setConnectingServiceId(vehicleJourneyAtStop.getConnectingServiceId());
					if(vehicleJourneyAtStop.getHeadwayFrequency() != null){
						jaxbVehicleJourneyAtStop.setHeadwayFrequency(toDuration(vehicleJourneyAtStop.getHeadwayFrequency()));
					}
					jaxbVehicleJourneyAtStop.setOrder(BigInteger.valueOf(vehicleJourneyAtStop.getOrder()));
					jaxbVehicleJourneyAtStop.setStopPointId(getNonEmptyObjectId(vehicleJourneyAtStop.getStopPoint()));
					jaxbVehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyObjectId(vehicleJourneyAtStop.getVehicleJourney()));

					if(vehicleJourneyAtStop.getArrivalTime() != null){
						jaxbVehicleJourneyAtStop.setArrivalTime(toCalendar(vehicleJourneyAtStop.getArrivalTime()));
					}
					if(vehicleJourneyAtStop.getDepartureTime() != null){
						jaxbVehicleJourneyAtStop.setDepartureTime(toCalendar(vehicleJourneyAtStop.getDepartureTime()));
					}
					if(vehicleJourneyAtStop.getWaitingTime() != null){
						jaxbVehicleJourneyAtStop.setWaitingTime(toCalendar(vehicleJourneyAtStop.getWaitingTime()));
					}
					
					if(vehicleJourneyAtStop.getElapseDuration() != null){
						jaxbVehicleJourneyAtStop.setElapseDuration(toDuration(vehicleJourneyAtStop.getElapseDuration()));
					}
					
					jaxbVehicleJourney.getVehicleJourneyAtStop().add(jaxbVehicleJourneyAtStop);
				}
			}
		}				
		return jaxbVehicleJourney;
	}

}
