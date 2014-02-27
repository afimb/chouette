package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import lombok.Setter;

import org.trident.schema.trident.VehicleJourneyAtStopType;
import org.trident.schema.trident.VehicleJourneyType;

import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class VehicleJourneyProducer extends AbstractModelProducer<VehicleJourney, VehicleJourneyType> 
{
   @Setter private DbVehicleJourneyFactory factory;

   @Override
   public VehicleJourney produce(String sourceFile,VehicleJourneyType xmlVehicleJourney,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) 
   {
      VehicleJourney vehicleJourney = null;
      if (factory == null)
         vehicleJourney = new VehicleJourney();
      else
         vehicleJourney = factory.getNewVehicleJourney();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(vehicleJourney, xmlVehicleJourney, importReport);

      // Comment optional
      vehicleJourney.setComment(getNonEmptyTrimedString(xmlVehicleJourney.getComment()));

      // Facility optional
      vehicleJourney.setFacility(getNonEmptyTrimedString(xmlVehicleJourney.getFacility()));

      // JourneyPatternId optional
      vehicleJourney.setJourneyPatternId(getNonEmptyTrimedString(xmlVehicleJourney.getJourneyPatternId()));

      // Number optional
      if (xmlVehicleJourney.isSetNumber())
         vehicleJourney.setNumber(xmlVehicleJourney.getNumber().longValue());

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
            vehicleJourney.setServiceStatusValue(ServiceStatusValueEnum.valueOf(xmlVehicleJourney.getStatusValue().value()));
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
            vehicleJourney.setTransportMode(TransportModeNameEnum.valueOf(xmlVehicleJourney.getTransportMode().value()));
         }
         catch (IllegalArgumentException e) 
         {
            // TODO: traiter le cas de non correspondance
         }
      }

      // VehicleTypeIdentifier optional
      vehicleJourney.setVehicleTypeIdentifier(getNonEmptyTrimedString(xmlVehicleJourney.getVehicleTypeIdentifier()));

      // VehicleJourneyAtStops [2..w]
      int order = 0;
      for(VehicleJourneyAtStopType  xmlVehicleJourneyAtStop : xmlVehicleJourney.getVehicleJourneyAtStop()){
         VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();

         // VehicleJourneyId optional
         vehicleJourneyAtStop.setVehicleJourneyId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop.getVehicleJourneyId()));
         vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

         if (xmlVehicleJourneyAtStop.getBoardingAlightingPossibility() != null) {
            try{
               vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.valueOf(xmlVehicleJourneyAtStop.getBoardingAlightingPossibility().value()));
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
         if (xmlVehicleJourneyAtStop.isSetOrder())
         {
             vehicleJourneyAtStop.setOrder(xmlVehicleJourneyAtStop.getOrder().longValue());
         }
         else
         {
        	 vehicleJourneyAtStop.setOrder(order);
         }
         order ++;

         // ([arrivalTime AND] departureTime [AND waitingTime]) XOR elapseDuration
         if(xmlVehicleJourneyAtStop.isSetElapseDuration())
         {
             vehicleJourneyAtStop.setElapseDuration(getTime(xmlVehicleJourneyAtStop.getElapseDuration()));        	 
         }
         else
         {
               // ArrivalTime optional
               vehicleJourneyAtStop.setArrivalTime(getTime(xmlVehicleJourneyAtStop.getArrivalTime()));

               // DepartureTime mandatory
               vehicleJourneyAtStop.setDepartureTime(getTime(xmlVehicleJourneyAtStop.getDepartureTime()));

               // WaintingTime optional
               vehicleJourneyAtStop.setWaitingTime(getTime(xmlVehicleJourneyAtStop.getWaitingTime()));
         }

         // HeadwayFrequency optional
         if(xmlVehicleJourneyAtStop.getHeadwayFrequency() != null){
            vehicleJourneyAtStop.setHeadwayFrequency(getTime(xmlVehicleJourneyAtStop.getHeadwayFrequency()));
         }

         vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
      }
      vehicleJourney.sortVehicleJourneyAtStops();
      return vehicleJourney;
   }


}
