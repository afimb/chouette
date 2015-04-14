package mobi.chouette.exchange.neptune.exporter.producer;

import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.type.AccessPointTypeEnum;
import mobi.chouette.model.type.LongLatTypeEnum;

import org.trident.schema.trident.AddressType;
import org.trident.schema.trident.LongLatTypeType;
import org.trident.schema.trident.PTAccessPointType;
import org.trident.schema.trident.ProjectedPointType;

public class AccessPointProducer extends
      AbstractJaxbNeptuneProducer<PTAccessPointType, AccessPoint>
{
   // @Override
   public PTAccessPointType produce(AccessPoint accessPoint, boolean addExtension)
   {
      PTAccessPointType jaxbAccessPoint = tridentFactory
            .createPTAccessPointType();

      //
      populateFromModel(jaxbAccessPoint, accessPoint);

      jaxbAccessPoint.setComment(getNotEmptyString(accessPoint.getComment()));
      jaxbAccessPoint.setName(accessPoint.getName());

      // type
      if (accessPoint.getType() != null)
      {
         AccessPointTypeEnum type = accessPoint.getType();
         jaxbAccessPoint.setType(type.name());
      }

      // opening/closingTime
      jaxbAccessPoint.setOpeningTime(toCalendar(accessPoint.getOpeningTime()));
      jaxbAccessPoint.setClosingTime(toCalendar(accessPoint.getClosingTime()));

      if (accessPoint.hasAddress())
      {
         AddressType castorAddress = tridentFactory.createAddressType();
         castorAddress.setCountryCode(getNotEmptyString(accessPoint
               .getCountryCode()));
         castorAddress.setStreetName(getNotEmptyString(accessPoint
               .getStreetName()));
         jaxbAccessPoint.setAddress(castorAddress);
      }

      jaxbAccessPoint.setContainedIn(accessPoint.getContainedIn().getObjectId());

      if (accessPoint.hasCoordinates())
      {
         LongLatTypeEnum longLatType = accessPoint.getLongLatType();
         try
         {
            jaxbAccessPoint.setLongLatType(LongLatTypeType
                  .fromValue(longLatType.name()));
            jaxbAccessPoint.setLatitude(accessPoint.getLatitude());
            jaxbAccessPoint.setLongitude(accessPoint.getLongitude());
         } catch (IllegalArgumentException e)
         {
            // TODO generate report
         }
      }

      if (accessPoint.hasProjection())
      {
         ProjectedPointType jaxbProjectedPoint = tridentFactory
               .createProjectedPointType();
         jaxbProjectedPoint.setProjectionType(accessPoint.getProjectionType());
         jaxbProjectedPoint.setX(accessPoint.getX());
         jaxbProjectedPoint.setY(accessPoint.getY());
         jaxbAccessPoint.setProjectedPoint(jaxbProjectedPoint);
      }

      return jaxbAccessPoint;
   }

}
