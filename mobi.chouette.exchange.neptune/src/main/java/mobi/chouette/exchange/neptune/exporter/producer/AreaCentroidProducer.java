package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

@Log4j
public class AreaCentroidProducer extends
      AbstractJaxbNeptuneProducer<ChouetteArea.AreaCentroid, StopArea>
{

   //@Override
   public ChouetteArea.AreaCentroid produce(StopArea area, boolean addExtension)
   {
      ChouetteArea.AreaCentroid jaxbAreaCentroid = tridentFactory
            .createChouettePTNetworkTypeChouetteAreaAreaCentroid();

      //
      populateFromModel(jaxbAreaCentroid, area);

      jaxbAreaCentroid.setObjectId(area.getChouetteId().getCodeSpace()+":AreaCentroid:"+area.getChouetteId().getObjectId());
      jaxbAreaCentroid.setComment(getNotEmptyString(area.getComment()));
      jaxbAreaCentroid.setName(area.getName());

      if (area.hasAddress())
      {
         AddressType jaxbAddress = tridentFactory.createAddressType();
         jaxbAddress.setCountryCode(getNotEmptyString(area.getCountryCode()));
         jaxbAddress.setStreetName(getNotEmptyString(area.getStreetName()));
         jaxbAreaCentroid.setAddress(jaxbAddress);
      }

      jaxbAreaCentroid.setContainedIn(getNonEmptyObjectId(area));

      if (area.hasCoordinates())
      {
         LongLatTypeEnum longLatType = area.getLongLatType();
         jaxbAreaCentroid.setLatitude(area.getLatitude());
         jaxbAreaCentroid.setLongitude(area.getLongitude());
         try
         {
            jaxbAreaCentroid.setLongLatType(LongLatTypeType
                  .fromValue(longLatType.name()));
         } catch (IllegalArgumentException e)
         {
            // TODO generate report
         }
      }
      else
      {
			log.error("missing coordinates for StopArea "+area.getChouetteId().getObjectId()+" "+area.getName());    	  
    	  // longitude/latitude mmandatory
          jaxbAreaCentroid.setLatitude(BigDecimal.ZERO);
          jaxbAreaCentroid.setLongitude(BigDecimal.ZERO);
          jaxbAreaCentroid.setLongLatType(LongLatTypeType.WGS_84);
      }

      if (area.hasProjection())
      {
         ProjectedPointType jaxbProjectedPoint = tridentFactory
               .createProjectedPointType();
         jaxbProjectedPoint.setProjectionType(area.getProjectionType());
         jaxbProjectedPoint.setX(area.getX());
         jaxbProjectedPoint.setY(area.getY());
         jaxbAreaCentroid.setProjectedPoint(jaxbProjectedPoint);
      }

      return jaxbAreaCentroid;
   }

}
