/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.sql.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor

public class GtfsCalendarDate extends GtfsBean
{
   public static final int INCLUDED = 1;
   public static final int EXCLUDED = 2;
   
   @Getter @Setter private String serviceId;
   @Getter @Setter private Date   date;
   @Getter @Setter private int    exceptionType = 1;
   @Getter @Setter private GtfsCalendar calendar;

   public static final String header = "service_id,date,exception_type";

   
   public String getCSVLine() {
      String csvLine = serviceId + "," + GtfsCalendar.sdf.format(date) + "," +exceptionType;
      return csvLine;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime * result + exceptionType;
      result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
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
      if (!(obj instanceof GtfsCalendarDate)) {
         return false;
      }
      GtfsCalendarDate other = (GtfsCalendarDate) obj;
      if (date == null) {
         if (other.date != null) {
            return false;
         }
      } else if (!date.equals(other.date)) {
         return false;
      }
      if (exceptionType != other.exceptionType) {
         return false;
      }
      if (serviceId == null) {
         if (other.serviceId != null) {
            return false;
         }
      } else if (!serviceId.equals(other.serviceId)) {
         return false;
      }
      return true;
   }

}
