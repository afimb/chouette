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


public class GtfsTransfer extends GtfsBean
{
   /*
   0 or (empty) - This is a recommended transfer point between two routes.
   1 - This is a timed transfer point between two routes. The departing vehicle is expected to wait for the arriving one, with sufficient time for a passenger to transfer between routes
   2 - This transfer requires a minimum amount of time between arrival and departure to ensure a connection. The time required to transfer is specified by min_transfer_time.
   3 - Transfers are not possible between routes at this location.
*/
   public static enum Type { RECOMMENDED, TIMED, MINIMAL, FORBIDDEN};
   
   
   @Getter @Setter private String fromStopId;
   @Getter @Setter private String toStopId;
   @Getter @Setter private Type transferType;
   @Getter @Setter private GtfsTime minTransferTime  = null;

   public String getCSVLine() {
      String csvLine = fromStopId + "," + toStopId + "," + transferType.ordinal() + ",";
      if (transferType.equals(Type.MINIMAL))
         csvLine += minTransferTime;
      return csvLine;
   }



   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((fromStopId == null) ? 0 : fromStopId.hashCode());
      result = prime * result + ((toStopId == null) ? 0 : toStopId.hashCode());
      result = prime * result + ((transferType == null) ? 0 : transferType.hashCode());
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!super.equals(obj))
      {
         return false;
      }
      if (!(obj instanceof GtfsTransfer))
      {
         return false;
      }
      GtfsTransfer other = (GtfsTransfer) obj;
      if (fromStopId == null)
      {
         if (other.fromStopId != null)
         {
            return false;
         }
      }
      else if (!fromStopId.equals(other.fromStopId))
      {
         return false;
      }
      if (toStopId == null)
      {
         if (other.toStopId != null)
         {
            return false;
         }
      }
      else if (!toStopId.equals(other.toStopId))
      {
         return false;
      }
      if (transferType != other.transferType)
      {
         return false;
      }
      return true;
   }


}
