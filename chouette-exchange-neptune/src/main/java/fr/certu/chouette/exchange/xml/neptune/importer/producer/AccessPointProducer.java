package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.PTAccessPointType;
import org.trident.schema.trident.ProjectedPointType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

/**
 * 
 * @author mamadou keira
 * 
 */
public class AccessPointProducer extends
      AbstractModelProducer<AccessPoint, PTAccessPointType>
{

   @Override
   public AccessPoint produce(Context context,
         PTAccessPointType xmlAccessPoint)
   {
      AccessPoint accessPoint = new AccessPoint();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, accessPoint, xmlAccessPoint);
      // Name optional
      accessPoint.setName(getNonEmptyTrimedString(xmlAccessPoint.getName()));
      // Comment optional
      accessPoint.setComment(getNonEmptyTrimedString(xmlAccessPoint
            .getComment()));
      // ContainedIn
      accessPoint.setContainedInStopArea(getNonEmptyTrimedString(xmlAccessPoint
            .getContainedIn()));
      // Address optional
      AddressType xmlAddress = xmlAccessPoint.getAddress();
      if (xmlAddress != null)
      {
         accessPoint.setCountryCode(getNonEmptyTrimedString(xmlAddress
               .getCountryCode()));
         accessPoint.setStreetName(getNonEmptyTrimedString(xmlAddress
               .getStreetName()));
      }
      // LongLatType mandatory
      if (xmlAccessPoint.getLongLatType() != null)
      {
         try
         {
            accessPoint.setLongLatType(LongLatTypeEnum.valueOf(xmlAccessPoint
                  .getLongLatType().value()));
         } catch (IllegalArgumentException e)
         {
            // TODO: handle exception
            e.printStackTrace();
         }
      }
      // Latitude mandatory
      accessPoint.setLatitude(xmlAccessPoint.getLatitude());
      // Longitude mandatory
      accessPoint.setLongitude(xmlAccessPoint.getLongitude());
      // ProjectedPoint optional
      ProjectedPointType xmlProjectedPoint = xmlAccessPoint.getProjectedPoint();
      if (xmlProjectedPoint != null)
      {
         accessPoint.setX(xmlProjectedPoint.getX());
         accessPoint.setY(xmlProjectedPoint.getY());
         accessPoint.setProjectionType(xmlProjectedPoint.getProjectionType());
      }
      // OpenningTime optional
      if (xmlAccessPoint.getOpeningTime() != null)
         accessPoint.setOpeningTime(getTime(xmlAccessPoint.getOpeningTime()));
      // ClosingTime optional
      if (xmlAccessPoint.getClosingTime() != null)
         accessPoint.setClosingTime(getTime(xmlAccessPoint.getClosingTime()));
      // Type optional
      if (xmlAccessPoint.getType() != null)
      {
         try
         {
            accessPoint.setType(AccessPointTypeEnum.valueOf(xmlAccessPoint
                  .getType()));
         } catch (IllegalArgumentException e)
         {
            // TODO: handle exception
            e.printStackTrace();
         }
      }
      if (xmlAccessPoint.isSetLiftAvailability())
         accessPoint.setLiftAvailable(xmlAccessPoint.isLiftAvailability());

      // MobilityRestrictedSuitability optional
      if (xmlAccessPoint.isSetMobilityRestrictedSuitability())
         accessPoint.setMobilityRestrictedSuitable(xmlAccessPoint
               .isMobilityRestrictedSuitability());

      // StairsAvailability optional
      if (xmlAccessPoint.isSetStairsAvailability())
         accessPoint.setStairsAvailable(xmlAccessPoint.isStairsAvailability());

      AccessPoint sharedBean = getOrAddSharedData(context, accessPoint, xmlAccessPoint);
      if (sharedBean != null)
         return sharedBean;

      return accessPoint;
   }

}
