package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.json.JSONObject;
import org.trident.schema.trident.ChouettePTNetworkType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

public class StopPointProducer extends
   AbstractJaxbNeptuneProducer<ChouettePTNetworkType.ChouetteLineDescription.StopPoint, StopPoint>
   implements JsonExtension
{

   @Override
   public ChouettePTNetworkType.ChouetteLineDescription.StopPoint produce(
         StopPoint stopPoint, boolean addExtension)
   {
      ChouettePTNetworkType.ChouetteLineDescription.StopPoint jaxbStopPoint = tridentFactory
            .createChouettePTNetworkTypeChouetteLineDescriptionStopPoint();

      //
      populateFromModel(jaxbStopPoint, stopPoint);

      jaxbStopPoint.setComment(buildComment(stopPoint,addExtension));
      jaxbStopPoint.setName(stopPoint.getName());
      jaxbStopPoint.setLineIdShortcut(stopPoint.getLineIdShortcut());

      StopArea area = stopPoint.getContainedInStopArea();
      
      

      jaxbStopPoint.setContainedIn(getNonEmptyObjectId(stopPoint
            .getContainedInStopArea()));
      jaxbStopPoint.setLatitude(area.getLatitude());
      jaxbStopPoint.setLongitude(area.getLongitude());

      if (area.getLongLatType() != null)
      {
         LongLatTypeEnum longLatType = area.getLongLatType();
         try
         {
            jaxbStopPoint.setLongLatType(LongLatTypeType.fromValue(longLatType
                  .name()));
         } catch (IllegalArgumentException e)
         {
            // TODO generate report
         }
      }

      if (area.hasProjection())
      {
         ProjectedPointType jaxbProjectedPoint = tridentFactory
               .createProjectedPointType();
         jaxbProjectedPoint.setProjectionType(area.getProjectionType());
         jaxbProjectedPoint.setX(area.getX());
         jaxbProjectedPoint.setY(area.getY());
         jaxbStopPoint.setProjectedPoint(jaxbProjectedPoint);
      }
      jaxbStopPoint.setPtNetworkIdShortcut(stopPoint.getPtNetworkIdShortcut());

      return jaxbStopPoint;
   }

   protected String buildComment(StopPoint stopPoint, boolean addExtension)
   {
      if (!addExtension) return null;
      JSONObject jsonComment = new JSONObject();
      JSONObject jsonRC = new JSONObject();
      if (stopPoint.getForBoarding() != null)
      {
         jsonRC.put(BOARDING, stopPoint.getForBoarding().name());
      }
      if (stopPoint.getForAlighting() != null)
      {
         jsonRC.put(ALIGHTING, stopPoint.getForAlighting().name());
      }
      if (jsonRC.length() == 0)
      {
         return null;
      }
      jsonComment.put(ROUTING_CONSTRAINTS, jsonRC);
      return jsonComment.toString();
   }


}
