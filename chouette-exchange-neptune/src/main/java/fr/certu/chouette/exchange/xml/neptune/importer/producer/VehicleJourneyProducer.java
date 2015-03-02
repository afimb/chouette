package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import lombok.Setter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.trident.schema.trident.VehicleJourneyAtStopType;
import org.trident.schema.trident.VehicleJourneyType;

import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.Footnote;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;

public class VehicleJourneyProducer extends
      AbstractModelProducer<VehicleJourney, VehicleJourneyType>
      implements JsonExtension
{
   @Setter
   private DbVehicleJourneyFactory factory;
   private JSONArray keys;

   @SuppressWarnings("deprecation")
   @Override
   public VehicleJourney produce(Context context, VehicleJourneyType xmlVehicleJourney)
   {
      VehicleJourney vehicleJourney = null;
      if (factory == null)
         vehicleJourney = new VehicleJourney();
      else
         vehicleJourney = factory.getNewVehicleJourney();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, vehicleJourney, xmlVehicleJourney);

      Line line = context.getAssembler().getLine();
      // Comment optional
      parseComment(getNonEmptyTrimedString(xmlVehicleJourney
            .getComment()), vehicleJourney, line);

      // Facility optional
      vehicleJourney.setFacility(getNonEmptyTrimedString(xmlVehicleJourney
            .getFacility()));

      // JourneyPatternId optional
      vehicleJourney
            .setJourneyPatternId(getNonEmptyTrimedString(xmlVehicleJourney
                  .getJourneyPatternId()));

      // Number optional
      if (xmlVehicleJourney.isSetNumber())
         vehicleJourney.setNumber(xmlVehicleJourney.getNumber().longValue());

      // CompanyId optional
      vehicleJourney.setCompanyId(getNonEmptyTrimedString(xmlVehicleJourney
            .getOperatorId()));

      // PublishedJourneyIdentifier optional
      vehicleJourney
            .setPublishedJourneyIdentifier(getNonEmptyTrimedString(xmlVehicleJourney
                  .getPublishedJourneyIdentifier()));

      // PublishedJourneyName optional
      vehicleJourney
            .setPublishedJourneyName(getNonEmptyTrimedString(xmlVehicleJourney
                  .getPublishedJourneyName()));

      // RouteId mandatory
      vehicleJourney.setRouteId(getNonEmptyTrimedString(xmlVehicleJourney
            .getRouteId()));

      // LineIdShortcut
      vehicleJourney
            .setLineIdShortcut(getNonEmptyTrimedString(xmlVehicleJourney
                  .getLineIdShortcut()));

      // ServiceStatusValue optional but ignored in Neptube

      // TimeSlotId optional
      vehicleJourney.setTimeSlotId(getNonEmptyTrimedString(xmlVehicleJourney
            .getTimeSlotId()));

      // TransportMode optional
      if (xmlVehicleJourney.getTransportMode() != null)
      {
         try
         {
            vehicleJourney.setTransportMode(TransportModeNameEnum
                  .valueOf(xmlVehicleJourney.getTransportMode().value()));
         } catch (IllegalArgumentException e)
         {
            // TODO: traiter le cas de non correspondance
         }
      }

      // VehicleTypeIdentifier optional
      vehicleJourney
            .setVehicleTypeIdentifier(getNonEmptyTrimedString(xmlVehicleJourney
                  .getVehicleTypeIdentifier()));

      // VehicleJourneyAtStops [2..w]
      int order = 0;
      for (VehicleJourneyAtStopType xmlVehicleJourneyAtStop : xmlVehicleJourney
            .getVehicleJourneyAtStop())
      {
         VehicleJourneyAtStop vehicleJourneyAtStop = factory
               .getNewVehicleJourneyAtStop();

         // VehicleJourneyId optional
         vehicleJourneyAtStop
               .setVehicleJourneyId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop
                     .getVehicleJourneyId()));
         vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

         if (xmlVehicleJourneyAtStop.getBoardingAlightingPossibility() != null)
         {
            try
            {
               vehicleJourneyAtStop
                     .setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum
                           .valueOf(xmlVehicleJourneyAtStop
                                 .getBoardingAlightingPossibility().value()));
            } catch (IllegalArgumentException e)
            {
               // TODO: traiter le cas de non correspondance
            }
         }

         // ConnectingServiceId
         vehicleJourneyAtStop
               .setConnectingServiceId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop
                     .getConnectingServiceId()));

         // StopPointId mandatory
         vehicleJourneyAtStop
               .setStopPointId(getNonEmptyTrimedString(xmlVehicleJourneyAtStop
                     .getStopPointId()));

         // Order optional
         if (xmlVehicleJourneyAtStop.isSetOrder())
         {
            vehicleJourneyAtStop.setOrder(xmlVehicleJourneyAtStop.getOrder()
                  .longValue());
         } else
         {
            vehicleJourneyAtStop.setOrder(order);
         }
         order++;

         // ([arrivalTime AND] departureTime [AND waitingTime]) XOR
         // elapseDuration
         if (xmlVehicleJourneyAtStop.isSetElapseDuration())
         {
            vehicleJourneyAtStop
                  .setElapseDuration(getTime(xmlVehicleJourneyAtStop
                        .getElapseDuration()));
         } else
         {
            // ArrivalTime optional
            vehicleJourneyAtStop.setArrivalTime(getTime(xmlVehicleJourneyAtStop
                  .getArrivalTime()));

            // DepartureTime mandatory
            vehicleJourneyAtStop
                  .setDepartureTime(getTime(xmlVehicleJourneyAtStop
                        .getDepartureTime()));

            // WaintingTime optional
            vehicleJourneyAtStop.setWaitingTime(getTime(xmlVehicleJourneyAtStop
                  .getWaitingTime()));
         }

         // HeadwayFrequency optional
         if (xmlVehicleJourneyAtStop.getHeadwayFrequency() != null)
         {
            vehicleJourneyAtStop
                  .setHeadwayFrequency(getTime(xmlVehicleJourneyAtStop
                        .getHeadwayFrequency()));
         }

         vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
      }
      vehicleJourney.sortVehicleJourneyAtStops();
      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, vehicleJourney, xmlVehicleJourney);
   }
   
   protected void parseComment(String comment, VehicleJourney vj, Line line)
   {
      if (comment != null && comment.startsWith("{") && comment.endsWith("}"))
      {
         // parse json comment
         JSONObject json = new JSONObject(comment);
         vj.setComment(json.optString(COMMENT,null));

         if (json.has(FOOTNOTE_REFS))
         {
            keys = json.getJSONArray(FOOTNOTE_REFS);
            for (int i = 0; i < keys.length(); i++)
            {
               String key = keys.getString(i);
               for (Footnote footnote : line.getFootnotes())
               {
                  if (footnote.getKey().equals(key))
                  {
                     vj.getFootnotes().add(footnote);
                  }
               }
            }
         }
         if (json.has(FLEXIBLE_SERVICE))
         {
            vj.setFlexibleService(json.getBoolean(FLEXIBLE_SERVICE));
         }
         if (json.has(MOBILITY_RESTRICTION))
         {
            vj.setMobilityRestrictedSuitability(json.getBoolean(MOBILITY_RESTRICTION));
         }
      }
      else
      {
         // normal comment
         vj.setComment(comment);
      }
      
   }

}
