package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.types.LongLatTypeType;
import chouette.schema.types.TypeType;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

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

      
      Address address = accessPoint.getAddress();
      if(accessPoint.getAddress() != null){
         chouette.schema.Address castorAddress = new chouette.schema.Address();
         castorAddress.setCountryCode(getNotEmptyString(address.getCountryCode()));
         castorAddress.setStreetName(getNotEmptyString(address.getStreetName()));
         castorAccessPoint.setAddress(castorAddress);
      }
      
      castorAccessPoint.setContainedIn(accessPoint.getContainedInStopArea());
      castorAccessPoint.setLatitude(accessPoint.getLatitude());
      castorAccessPoint.setLongitude(accessPoint.getLongitude());
      
      if(accessPoint.getLongLatType() != null){
         LongLatTypeEnum longLatType = accessPoint.getLongLatType();
         try {
            castorAccessPoint.setLongLatType(LongLatTypeType.fromValue(longLatType.value()));
         } catch (IllegalArgumentException e) {
            // TODO generate report
         }
      }
      
      ProjectedPoint projectedPoint = accessPoint.getProjectedPoint();
      if(projectedPoint != null){
         chouette.schema.ProjectedPoint castorProjectedPoint = new chouette.schema.ProjectedPoint();
         castorProjectedPoint.setProjectionType(projectedPoint.getProjectionType());
         castorProjectedPoint.setX(projectedPoint.getX());
         castorProjectedPoint.setY(projectedPoint.getY());
         castorAccessPoint.setProjectedPoint(castorProjectedPoint);
      }
                  

      return castorAccessPoint;
   }

}
