package mobi.chouette.exchange.gtfs.model;

import java.io.Serializable;

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
public class GtfsTrip extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String routeId;

   @Getter
   @Setter
   private String serviceId;

   @Getter
   @Setter
   private String tripId;

   @Getter
   @Setter
   private String tripShortName;

   @Getter
   @Setter
   private String tripHeadSign;

   @Getter
   @Setter
   private DirectionType directionId;

   @Getter
   @Setter
   private String blockId;

   @Getter
   @Setter
   private String shapeId;

   @Getter
   @Setter
   private WheelchairAccessibleType wheelchairAccessible;

   @Getter
   @Setter
   private BikesAllowedType bikesAllowed;

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
