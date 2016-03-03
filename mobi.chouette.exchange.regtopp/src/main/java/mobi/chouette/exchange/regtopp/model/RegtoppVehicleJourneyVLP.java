package mobi.chouette.exchange.regtopp.model;

import java.io.Serializable;

import org.beanio.annotation.Field;
import org.beanio.annotation.Record;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Record(minOccurs = 1)
public class RegtoppVehicleJourneyVLP extends RegtoppObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer administrationCode;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer counter;

   @Getter
   @Setter
   @Field(length = 6)
   private Integer vehicleJourneyId;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer dayCodeId;

   @Getter
   @Setter
   @Field(length = 2)
   private Integer sequenceNumberTrip;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer lineId;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer tripId;

   @Getter
   @Setter
   @Field(length = 5)
   private Integer id;


  
   @AllArgsConstructor
   public enum DirectionType implements Serializable
   {
      Outbound, Inbound;

   }

   @AllArgsConstructor
   public enum BikesAllowedType implements Serializable
   {
      NoInformation, Allowed, NoAllowed;

   }

   @AllArgsConstructor
   public enum WheelchairAccessibleType implements Serializable
   {
      NoInformation, Allowed, NoAllowed;

   }
}
