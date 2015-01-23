package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import lombok.extern.log4j.Log4j;

import org.json.JSONObject;

import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.AlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.BoardingPossibilityEnum;

@Log4j
public class StopPointProducer
extends
AbstractModelProducer<StopPoint, org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint>
implements JsonExtension
{
   @Override
   public StopPoint produce(Context context,
         org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.StopPoint xmlStopPoint)
   {
      StopPoint stopPoint = new StopPoint();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, stopPoint, xmlStopPoint);

      // Name mandatory
      stopPoint.setName(getNonEmptyTrimedString(xmlStopPoint.getName()));

      // Comment optional

      parseComment(getNonEmptyTrimedString(xmlStopPoint.getComment()),stopPoint);

      // LongLatType mandatory but ignored in chouette

      // Latitude mandatory but ignored in chouette

      // Longitude mandatory but ignored in chouette

      // ContainedInStopArea
      stopPoint.setContainedInStopAreaId(getNonEmptyTrimedString(xmlStopPoint
            .getContainedIn()));

      // LineIdShortcut optional
      stopPoint.setLineIdShortcut(getNonEmptyTrimedString(xmlStopPoint
            .getLineIdShortcut()));

      // PtNetworkShortcut optional : correct old fashioned form
      String ptNetworkId = getNonEmptyTrimedString(xmlStopPoint
            .getPtNetworkIdShortcut());
      if (ptNetworkId != null && ptNetworkId.contains(":PTNetwork:"))
      {
         ptNetworkId = ptNetworkId.replace(":PTNetwork:", ":"
               + PTNetwork.PTNETWORK_KEY + ":");
      }
      stopPoint.setPtNetworkIdShortcut(ptNetworkId);

      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, stopPoint, xmlStopPoint);
   }
   
   protected void parseComment(String comment, StopPoint point)
   {
      if (comment != null && comment.startsWith("{") && comment.endsWith("}"))
      {
         // parse json comment
         JSONObject json = new JSONObject(comment);
         if (json.has(ROUTING_CONSTRAINTS))
         {
            JSONObject rc = json.getJSONObject(ROUTING_CONSTRAINTS);
            if (rc.has(BOARDING))
            {
               try
               {
                  BoardingPossibilityEnum forBoarding = BoardingPossibilityEnum.valueOf(rc.getString(BOARDING));
                  point.setForBoarding(forBoarding );
               }
               catch (IllegalArgumentException e)
               {
                  log.error("unknown value "+rc.getString(BOARDING)+" for boarding");
               }
            }
            if (rc.has(ALIGHTING))
            {
               try
               {
                  AlightingPossibilityEnum forAlighting = AlightingPossibilityEnum.valueOf(rc.getString(ALIGHTING));
                  point.setForAlighting(forAlighting );
               }
               catch (IllegalArgumentException e)
               {
                  log.error("unknown value "+rc.getString(ALIGHTING)+" for alighting");
               }
            }
         }
      }
   }

}
