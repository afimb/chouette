package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.ProjectedPointType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.exchange.xml.neptune.model.Address;
import fr.certu.chouette.exchange.xml.neptune.model.AreaCentroid;
import fr.certu.chouette.exchange.xml.neptune.model.ProjectedPoint;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

public class AreaCentroidProducer
      extends
      AbstractModelProducer<AreaCentroid, org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.AreaCentroid>
{
   @Override
   public AreaCentroid produce(Context context,
         org.trident.schema.trident.ChouettePTNetworkType.ChouetteArea.AreaCentroid xmlAreaCentroid)
   {
      AreaCentroid areaCentroid = new AreaCentroid();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, areaCentroid, xmlAreaCentroid);

      // Name mandatory
      areaCentroid.setName(getNonEmptyTrimedString(xmlAreaCentroid.getName()));

      // Comment optional
      areaCentroid.setComment(getNonEmptyTrimedString(xmlAreaCentroid
            .getComment()));

      // LongLatType mandatory
      if (xmlAreaCentroid.getLongLatType() != null)
      {
         try
         {
            areaCentroid.setLongLatType(LongLatTypeEnum.valueOf(xmlAreaCentroid
                  .getLongLatType().value()));
         } catch (IllegalArgumentException e)
         {
            // TODO: traiter le cas de non correspondance
            e.printStackTrace();
         }
      }

      // Latitude mandatory
      areaCentroid.setLatitude(xmlAreaCentroid.getLatitude());

      // Longitude mandatory
      areaCentroid.setLongitude(xmlAreaCentroid.getLongitude());

      // ContainedInStopAreaId
      areaCentroid
            .setContainedInStopAreaId(getNonEmptyTrimedString(xmlAreaCentroid
                  .getContainedIn()));

      // Address optional
      AddressType xmlAddress = xmlAreaCentroid.getAddress();
      if (xmlAddress != null)
      {
         Address address = new Address();
         address.setCountryCode(getNonEmptyTrimedString(xmlAddress
               .getCountryCode()));
         address.setStreetName(getNonEmptyTrimedString(xmlAddress
               .getStreetName()));
         areaCentroid.setAddress(address);
      }

      // ProjectedPoint optional
      ProjectedPointType xmlProjectedPoint = xmlAreaCentroid
            .getProjectedPoint();
      if (xmlProjectedPoint != null)
      {
         ProjectedPoint projectedPoint = new ProjectedPoint();
         projectedPoint.setX(xmlProjectedPoint.getX());
         projectedPoint.setY(xmlProjectedPoint.getY());
         projectedPoint
               .setProjectionType(xmlProjectedPoint.getProjectionType());
         areaCentroid.setProjectedPoint(projectedPoint);
      }
      AreaCentroid sharedBean = getOrAddSharedData(context, areaCentroid, xmlAreaCentroid);
      if (sharedBean != null)
         return sharedBean;

      return areaCentroid;
   }

}
