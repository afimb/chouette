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
public class GtfsStopTime extends GtfsObject implements Serializable
{

   private static final long serialVersionUID = 1L;

   @Getter
   @Setter
   private String tripId;

   @Getter
   @Setter
   private GtfsTime arrivalTime;

   @Getter
   @Setter
   private GtfsTime departureTime;

   @Getter
   @Setter
   private String stopId;

   @Getter
   @Setter
   private Integer stopSequence;

   @Getter
   @Setter
   private String stopHeadsign;

   @Getter
   @Setter
   private PickupType pickupType;

   @Getter
   @Setter
   private DropOffType dropOffType;

   @Getter
   @Setter
   private Float shapeDistTraveled;

   @Getter
   @Setter
   private Integer timepoint;

   // @Override
   // public String toString()
   // {
   // return id + ":" + StopTimeExporter.CONVERTER.to(new Context(),this);
   // }

   public enum DropOffType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }

   public enum PickupType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }
}
