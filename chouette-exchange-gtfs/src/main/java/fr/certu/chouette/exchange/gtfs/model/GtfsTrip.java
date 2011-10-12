/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 *
 * @author zbouziane
 */
@NoArgsConstructor


public class GtfsTrip extends GtfsBean
{
   /**
    * travel in one direction
    */
   public static final int OUTBOUND = 0; 
   /**
    * travel in the opposite direction
    */
   public static final int INBOUND = 1;
   @Getter @Setter private String routeId;
   @Getter @Setter private String serviceId;
   @Getter @Setter private String tripId;
   @Getter @Setter private String tripHeadsign  = null;
   @Getter @Setter private String tripShortName = null;
   @Getter @Setter private int    directionId = 0;
   @Getter @Setter private String blockId = null; // sans interet
   @Getter @Setter private String shapeId = null; 

   @Getter @Setter private GtfsRoute route;
   @Getter @Setter private GtfsCalendar calendar;
   @Getter @Setter private List<GtfsFrequency> frequencies = new ArrayList<GtfsFrequency>();

   @Getter @Setter private List<GtfsStopTime> stopTimes = new ArrayList<GtfsStopTime>();

   public static final String header = "route_id,service_id,trip_id,trip_headsign,trip_short_name,direction_id,shape_id";
   
   public String getCSVLine() {
      String csvLine = routeId + "," + serviceId + "," + tripId + ",";
      if (tripHeadsign != null)
         csvLine += tripHeadsign;
      csvLine += ",";
      if (tripShortName != null)
         csvLine += tripShortName;
      csvLine += "," + directionId + ",";
//      if (blockId != null)
//         csvLine += blockId;
//      csvLine += ",";
      if (shapeId != null)
         csvLine += shapeId;
      return csvLine;
   }

   public void addStopTime(GtfsStopTime time)
   {
      if (!stopTimes.contains(time)) 
      {
         int index = -1;
         for (int i = 0; i < stopTimes.size(); i++)
         {
            GtfsStopTime localTime = stopTimes.get(i);
            if (localTime.getStopSequence() > time.getStopSequence())
            {
               stopTimes.add(i, time);
               index = i;
               break;
            }
         }
         if (index == -1) stopTimes.add(time);
         time.setTrip(this);
      }
   }

   public void addFrequency(GtfsFrequency frequency)
   {
      if (!frequencies.contains(frequency)) 
      {
         frequencies.add(frequency);
         frequency.setTrip(this);
      }
   }



   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
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
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof GtfsTrip)) {
         return false;
      }
      GtfsTrip other = (GtfsTrip) obj;
      if (tripId == null) {
         if (other.tripId != null) {
            return false;
         }
      } else if (!tripId.equals(other.tripId)) {
         return false;
      }
      return true;
   }


}
