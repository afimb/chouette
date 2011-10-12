/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor
public class GtfsStopTime extends GtfsBean implements Comparable<GtfsStopTime>
{

   @Getter @Setter private String tripId;
   @Getter @Setter private GtfsTime   arrivalTime;
   @Getter @Setter private GtfsTime   departureTime;
   @Getter @Setter private String stopId;
   @Getter @Setter private int    stopSequence;
   // optional items
   @Getter @Setter private String stopHeadsign = null;
   @Getter @Setter private int    pickupType = 0;
   @Getter @Setter private int    dropOffType = 0;
   @Getter @Setter private double shapeDistTraveled = (double)-1;
   @Getter @Setter private GtfsTrip trip;
   @Getter @Setter private GtfsStop stop;

   public static final String header = "trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,shape_dist_traveled";
   
   public String getCSVLine() {
      String csvLine = tripId + ",";
      if (arrivalTime == null)
         arrivalTime = departureTime;
      if (arrivalTime != null)
         csvLine += arrivalTime;
      csvLine += ",";
      if (departureTime == null)
         departureTime = arrivalTime;
      if (departureTime != null)
         csvLine += departureTime;
      csvLine += ",";
      csvLine += stopId + ",";
      csvLine += stopSequence + ",";
      if (stopHeadsign != null)
         csvLine += stopHeadsign;
      csvLine += "," ; // + pickupType + "," + dropOffType + "," ;
      if (shapeDistTraveled >= 0)
         csvLine += shapeDistTraveled;
      return csvLine;
   }


   @Override
   public int compareTo(GtfsStopTime o) 
   {
      return stopSequence - o.getStopSequence();
   }



   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((stopId == null) ? 0 : stopId.hashCode());
      result = prime * result + stopSequence;
      result = prime * result + ((tripId == null) ? 0 : tripId.hashCode());
      return result;
   }


   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (!(obj instanceof GtfsStopTime)) {
         return false;
      }
      GtfsStopTime other = (GtfsStopTime) obj;
      if (stopId == null) {
         if (other.stopId != null) {
            return false;
         }
      } else if (!stopId.equals(other.stopId)) {
         return false;
      }
      if (stopSequence != other.stopSequence) {
         return false;
      }
      if (tripId == null) {
         if (other.tripId != null) {
            return false;
         }
      } else if (!tripId.equals(other.tripId)) {
         return false;
      }
      return true;
   }

   public GtfsStopTime copy()
   {
      GtfsStopTime copy = new GtfsStopTime();
      copy.arrivalTime = this.arrivalTime;
      copy.departureTime = this.departureTime;
      copy.dropOffType = this.dropOffType;
      copy.pickupType = this.pickupType;
      copy.shapeDistTraveled = this.shapeDistTraveled;
      copy.stopHeadsign = this.stopHeadsign;
      copy.stopId = this.stopId;
      copy.stopSequence = this.stopSequence;
      return copy;
   }

}
