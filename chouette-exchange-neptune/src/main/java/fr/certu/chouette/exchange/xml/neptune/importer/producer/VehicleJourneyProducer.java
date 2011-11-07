package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.sql.Time;

import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence;
import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.report.ReportItem;

public class VehicleJourneyProducer extends AbstractModelProducer<VehicleJourney, chouette.schema.VehicleJourney> 
{

	@Override
	public VehicleJourney produce(chouette.schema.VehicleJourney xmlVehicleJourney,ReportItem report,SharedImportedData sharedData) 
	{
		VehicleJourney vehicleJourney = new VehicleJourney();

		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(vehicleJourney, xmlVehicleJourney, report);

		// Comment optional
		vehicleJourney.setComment(getNonEmptyTrimedString(xmlVehicleJourney.getComment()));

		// Facility optional
		vehicleJourney.setFacility(getNonEmptyTrimedString(xmlVehicleJourney.getFacility()));

		// JourneyPatternId optional
		vehicleJourney.setJourneyPatternId(getNonEmptyTrimedString(xmlVehicleJourney.getJourneyPatternId()));

		// Number optional
		vehicleJourney.setNumber(xmlVehicleJourney.getNumber());

		// CompanyId optional
		vehicleJourney.setCompanyId(getNonEmptyTrimedString(xmlVehicleJourney.getOperatorId()));

		// PublishedJourneyIdentifier optional
		vehicleJourney.setPublishedJourneyIdentifier(getNonEmptyTrimedString(xmlVehicleJourney.getPublishedJourneyIdentifier()));

		// PublishedJourneyName optional
		vehicleJourney.setPublishedJourneyName(getNonEmptyTrimedString(xmlVehicleJourney.getPublishedJourneyName()));

		// RouteId mandatory
		vehicleJourney.setRouteId(getNonEmptyTrimedString(xmlVehicleJourney.getRouteId()));

		// LineIdShortcut
		vehicleJourney.setLineIdShortcut(getNonEmptyTrimedString(xmlVehicleJourney.getLineIdShortcut()));

		// ServiceStatusValue optional
		if(xmlVehicleJourney.getStatusValue() != null){
			try{
				vehicleJourney.setServiceStatusValue(ServiceStatusValueEnum.fromValue(xmlVehicleJourney.getStatusValue().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}

		// TimeSlotId optional
		vehicleJourney.setTimeSlotId(getNonEmptyTrimedString(xmlVehicleJourney.getTimeSlotId()));

		// TransportMode optional
		if(xmlVehicleJourney.getTransportMode() != null){
			try{
				vehicleJourney.setTransportMode(TransportModeNameEnum.fromValue(xmlVehicleJourney.getTransportMode().value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}

		// VehicleTypeIdentifier optional
		vehicleJourney.setVehicleTypeIdentifier(getNonEmptyTrimedString(xmlVehicleJourney.getVehicleTypeIdentifier()));

		// VehicleJourneyAtStops [2..w]
		for(chouette.schema.VehicleJourneyAtStop  xmlVehicleJourneyAtStop : xmlVehicleJourney.getVehicleJourneyAtStop()){
			VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();

			// VehicleJourneyId optional
			vehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getVehicleJourneyId()));
			vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

			if (xmlVehicleJourneyAtStop.getBoardingAlightingPossibility() != null) {
				try{
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.fromValue(xmlVehicleJourneyAtStop.getBoardingAlightingPossibility().value()));
				}
				catch (IllegalArgumentException e) 
				{
					// TODO: traiter le cas de non correspondance
				}
			}

			// ConnectingServiceId
			vehicleJourneyAtStop.setConnectingServiceId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getConnectingServiceId()));

			// StopPointId mandatory
			vehicleJourneyAtStop.setStopPointId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getStopPointId()));

			// Order optional
			vehicleJourneyAtStop.setOrder(xmlVehicleJourneyAtStop.getOrder());

			// ([arrivalTime AND] departureTime [AND waitingTime]) XOR elapseDuration
			if(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice() != null){
				VehicleJourneyAtStopTypeChoiceSequence xmlVehicleJourneyAtStopTypeChoiceSequence = xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence();

				if(xmlVehicleJourneyAtStopTypeChoiceSequence != null)
				{
					// ArrivalTime optional
					vehicleJourneyAtStop.setArrivalTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getArrivalTime()));

					// DepartureTime mandatory
					vehicleJourneyAtStop.setDepartureTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getDepartureTime()));

					// WaintingTime optional
					vehicleJourneyAtStop.setWaitingTime(getTime(xmlVehicleJourneyAtStopTypeChoiceSequence.getWaitingTime()));
				}

				// ElapseDuration mandatory
				if(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2() != null 
						&& xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration() != null)
				{
					vehicleJourneyAtStop.setElapseDuration(new Time(xmlVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration().toLong()));
				}
			}

			// HeadwayFrequency optional
			if(xmlVehicleJourneyAtStop.getHeadwayFrequency() != null){
				vehicleJourneyAtStop.setHeadwayFrequency(new Time(xmlVehicleJourneyAtStop.getHeadwayFrequency().toLong()));
			}

			vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
		}
        vehicleJourney.sortVehicleJourneyAtStops();
		return vehicleJourney;
	}

}
