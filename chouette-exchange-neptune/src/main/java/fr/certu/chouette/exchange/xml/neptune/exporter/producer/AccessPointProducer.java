package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.types.LongLatTypeType;
import chouette.schema.types.TypeType;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

public class AccessPointProducer extends AbstractCastorNeptuneProducer<chouette.schema.AccessPoint, AccessPoint>
{
   @Override
   public chouette.schema.AccessPoint produce(AccessPoint accessPoint) {
      chouette.schema.AccessPoint castorAccessPoint = new chouette.schema.AccessPoint();

      //
      populateFromModel(castorAccessPoint, accessPoint);

      castorAccessPoint.setComment(getNotEmptyString(accessPoint.getComment()));
      castorAccessPoint.setName(accessPoint.getName());

      // type
      if(accessPoint.getType() != null){
          AccessPointTypeEnum type = accessPoint.getType();
          try {
             castorAccessPoint.setType(TypeType.fromValue(type.value()));
          } catch (IllegalArgumentException e) {
             // TODO generate report
          }
       }

      // opening/closingTime
      castorAccessPoint.setOpeningTime(toCastorTime(accessPoint.getOpeningTime()));
      castorAccessPoint.setClosingTime(toCastorTime(accessPoint.getClosingTime()));

      
      if (accessPoint.hasAddress())
      {
         chouette.schema.Address castorAddress = new chouette.schema.Address();
         castorAddress.setCountryCode(getNotEmptyString(accessPoint.getCountryCode()));
         castorAddress.setStreetName(getNotEmptyString(accessPoint.getStreetName()));
         castorAccessPoint.setAddress(castorAddress);
      }
      
      castorAccessPoint.setContainedIn(accessPoint.getContainedInStopArea());
      
      if(accessPoint.hasCoordinates())
      {
         LongLatTypeEnum longLatType = accessPoint.getLongLatType();
         try {
            castorAccessPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
            castorAccessPoint.setLatitude(accessPoint.getLatitude());
            castorAccessPoint.setLongitude(accessPoint.getLongitude());
         } catch (IllegalArgumentException e) {
            // TODO generate report
         }
      }
      
      if(accessPoint.hasProjection())
      {
         chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
         castorProjectedPoint.setProjectionType(accessPoint.getProjectionType());
         castorProjectedPoint.setX(accessPoint.getX());
         castorProjectedPoint.setY(accessPoint.getY());
         castorAccessPoint.setProjectedPoint(castorProjectedPoint);
      }
                  

      return castorAccessPoint;
   }

}
