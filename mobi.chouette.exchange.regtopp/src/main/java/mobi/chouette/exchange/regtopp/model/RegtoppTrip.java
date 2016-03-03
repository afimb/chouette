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
public class RegtoppTrip extends RegtoppObject implements Serializable
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
   @Field(length = 4)
   private Integer lineNumber;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer tripNumber;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer typeOfTraffic;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer dayCodeRef;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer companyCode;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer remark1Ref;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer remark2Ref;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer destinationCode;

   @Getter
   @Setter
   @Field(length = 8)
   private String lineNumberVisible;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer direction;

   @Getter
   @Setter
   @Field(length = 2)
   private Integer tripType;

   @Getter
   @Setter
   @Field(length = 4)
   private Integer departureTime;

   @Getter
   @Setter
   @Field(length = 3)
   private Integer numStops;

   @Getter
   @Setter
   @Field(length = 7)
   private Integer firstStopRef;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer parcelService;

   @Getter
   @Setter
   @Field(length = 1)
   private Integer priceCode;


   // @Override
   // public String toString()
   // {
   // return id + ":" + TripExporter.CONVERTER.to(new Context(),this);
   // }

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
