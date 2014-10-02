/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.sql.Date;
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
public class GtfsCalendar extends GtfsBean
{
   @Getter
   @Setter
   private String serviceId;
   @Getter
   @Setter
   private boolean monday = false;
   @Getter
   @Setter
   private boolean tuesday = false;
   @Getter
   @Setter
   private boolean wednesday = false;
   @Getter
   @Setter
   private boolean thursday = false;
   @Getter
   @Setter
   private boolean friday = false;
   @Getter
   @Setter
   private boolean saturday = false;
   @Getter
   @Setter
   private boolean sunday = false;
   @Getter
   @Setter
   private Date startDate = null;
   @Getter
   @Setter
   private Date endDate = null;

   @Getter
   @Setter
   private List<GtfsCalendarDate> calendarDates = new ArrayList<GtfsCalendarDate>();

   public static final String header = "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date";

   public String getCSVLine()
   {
      String csvLine = toCSVString(serviceId) + ",";
      csvLine += (monday ? "1," : "0,");
      csvLine += (tuesday ? "1," : "0,");
      csvLine += (wednesday ? "1," : "0,");
      csvLine += (thursday ? "1," : "0,");
      csvLine += (friday ? "1," : "0,");
      csvLine += (saturday ? "1," : "0,");
      csvLine += (sunday ? "1," : "0,");
      csvLine += sdf.format(startDate);
      csvLine += ",";
      csvLine += sdf.format(endDate);
      return csvLine;
   }

   public boolean hasPeriod()
   {
      return startDate != null && endDate != null;
   }

   public boolean hasDates()
   {
      return !calendarDates.isEmpty();
   }

   public void addCalendarDate(GtfsCalendarDate calendarDate)
   {
      if (!calendarDates.contains(calendarDate))
      {
         calendarDates.add(calendarDate);
         calendarDate.setCalendar(this);
      }
   }

   public String toString()
   {
      String retVal = "";
      if (hasPeriod())
         retVal += getCSVLine();
      if (hasDates())
      {
         if (!retVal.isEmpty())
            retVal += "\n";
         for (GtfsCalendarDate date : calendarDates)
         {
            retVal += date.getCSVLine() + "\n";
         }
      }

      return retVal;
   }

   @Override
   public boolean isValid()
   {
      boolean ret = true;
      if (serviceId == null)
      {
         addMissingData("service_id");
         ret = false;
      }
      // if fileLineNumber = 0, calendar created from only calendar_dates
      if (fileLineNumber != 0)
      {
         // Monday ... Sunday not really checked
         if (!monday && !tuesday && !wednesday && !thursday && !friday
               && !saturday && !sunday)
         {
            addMissingData("monday");
            addMissingData("tuesday");
            addMissingData("wednesday");
            addMissingData("thursday");
            addMissingData("friday");
            addMissingData("saturday");
            addMissingData("sunday");
            ret = false;
         }
         if (startDate == null)
         {
            addMissingData("start_date");
            ret = false;
         }
         if (endDate == null)
         {
            addMissingData("end_date");
            ret = false;
         }
      }
      return ret;
   }
}
