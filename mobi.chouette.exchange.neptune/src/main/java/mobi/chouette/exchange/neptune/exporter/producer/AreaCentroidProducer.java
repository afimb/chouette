package mobi.chouette.exchange.neptune.exporter.producer;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.NeptuneChouetteIdGenerator;
import mobi.chouette.exchange.neptune.exporter.NeptuneExportParameters;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.ProjectedPointType;

@Log4j
public class AreaCentroidProducer extends
      AbstractJaxbNeptuneProducer<ChouetteArea.AreaCentroid, StopArea> implements Constant
{

   //@Override
   public ChouetteArea.AreaCentroid produce(Context context, StopArea area, boolean addExtension)
   {
	   
	   NeptuneExportParameters parameters = (NeptuneExportParameters) context.get(CONFIGURATION);
	   NeptuneChouetteIdGenerator neptuneChouetteIdGenerator = (NeptuneChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
	   
      ChouetteArea.AreaCentroid jaxbAreaCentroid = tridentFactory
            .createChouettePTNetworkTypeChouetteAreaAreaCentroid();

      //
      populateFromModel(context, jaxbAreaCentroid, area);

      jaxbAreaCentroid.setObjectId(area.getChouetteId().getCodeSpace()+":AreaCentroid:"+neptuneChouetteIdGenerator.toSpecificFormatId(area.getChouetteId(), parameters.getDefaultCodespace(), area));
      jaxbAreaCentroid.setComment(getNotEmptyString(area.getComment()));
      jaxbAreaCentroid.setName(area.getName());

      if (area.hasAddress())
      {
         AddressType jaxbAddress = tridentFactory.createAddressType();
         jaxbAddress.setCountryCode(getNotEmptyString(area.getCountryCode()));
         jaxbAddress.setStreetName(getNotEmptyString(area.getStreetName()));
         jaxbAreaCentroid.setAddress(jaxbAddress);
      }

      jaxbAreaCentroid.setContainedIn(getNonEmptyObjectId(context, area));

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
			log.error("missing coordinates for StopArea "+neptuneChouetteIdGenerator.toSpecificFormatId(area.getChouetteId(), parameters.getDefaultCodespace(), area)+" "+area.getName());    	  
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
