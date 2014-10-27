package fr.certu.chouette.exchange.gtfs.refactor.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.StopTimeExporter;

// @ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
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

   @Override
   public String toString()
   {
      return id + ":" + StopTimeExporter.CONVERTER.to(this);
   }

   public enum DropOffType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }

   public enum PickupType implements Serializable
   {
      Scheduled, NoAvailable, AgencyCall, DriverCall;
   }
}
