/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsCalendarProducer extends AbstractProducer<GtfsCalendar, Timetable>
{
   private static final Logger logger = Logger.getLogger(GtfsCalendarProducer.class);
   private static final long ONE_DAY=3600000*24;
   private static final long HALD_DAY=3600000*12;

   @Override
   public GtfsCalendar produce(Timetable timetable,GtfsReport report)
   {
      GtfsCalendar calendar = new GtfsCalendar();

      String serviceId = timetable.getObjectId();
      calendar.setServiceId(serviceId);
      
      if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty())
      {
         for (DayTypeEnum dayType : timetable.getDayTypes())
         {
            switch (dayType)
            {
            case MONDAY:
               calendar.setMonday(true);
               break;
            case TUESDAY:
               calendar.setTuesday(true);
               break;
            case WEDNESDAY:
               calendar.setWednesday(true);
               break;
            case THURSDAY:
               calendar.setThursday(true);
               break;
            case FRIDAY:
               calendar.setFriday(true);
               break;
            case SATURDAY:
               calendar.setSaturday(true);
               break;
            case SUNDAY:
               calendar.setSunday(true);
               break;
            case WEEKDAY:
               calendar.setMonday(true);
               calendar.setTuesday(true);
               calendar.setWednesday(true);
               calendar.setThursday(true);
               calendar.setFriday(true);
               break;
            case WEEKEND:
               calendar.setSaturday(true);
               calendar.setSunday(true);
               break;
            }
         }
         if (timetable.getPeriods().size() == 1)
         {
            Period period = timetable.getPeriods().get(0);
            calendar.setStartDate(period.getStartDate());
            calendar.setEndDate(period.getEndDate());
         }
         else
         {
            // check if it is better to set all dates independently or a big period with several excluded dates
            // build a list of included dates 
            Set<Date> includedDates = new HashSet<Date>();
            includedDates.addAll(timetable.getCalendarDays()); 

            for (Period period : timetable.getPeriods())
            {
               Date checkedDate = period.getStartDate();
               Date endDate = new Date(period.getEndDate().getTime()+ONE_DAY);
               while (checkedDate.before(endDate))
               {
                  if (!includedDates.contains(checkedDate)) 
                  {
                     if (checkValidDay(checkedDate, calendar)) 
                     {
                        includedDates.add(checkedDate);
                     }
                  }
                  checkedDate = new Date(checkedDate.getTime()+ONE_DAY);
               }
            }

            // build a list of excluded dates (between 2 periods and valid for dayTypes)
            Set<Date> excludedDates = new HashSet<Date>();
            for (int i = 1; i < timetable.getPeriods().size(); i++)
            {
               Date checkedDate = new Date(timetable.getPeriods().get(i-1).getEndDate().getTime()+ONE_DAY);
               Date endDate = new Date(timetable.getPeriods().get(i).getStartDate().getTime());
               while (checkedDate.before(endDate))
               {
                  if (!checkValidDay(checkedDate, calendar)) 
                  {
                     excludedDates.add(checkedDate);
                  }
                  checkedDate = new Date(checkedDate.getTime()+ONE_DAY);
               }
            }

            // take the shorter one
            if (includedDates.size() <= excludedDates.size())
            {
               // create only CalendarDates
               addDates(calendar, includedDates, GtfsCalendarDate.INCLUDED);
            }
            else
            {
               // create a period with excluded dates as CalendarDates
               calendar.setStartDate(timetable.getPeriods().get(0).getStartDate());
               calendar.setEndDate(timetable.getPeriods().get(timetable.getPeriods().size()-1).getEndDate());
               addDates(calendar, excludedDates, GtfsCalendarDate.EXCLUDED);
            }
         }
      }
      else if (timetable.getCalendarDays() != null && !timetable.getCalendarDays().isEmpty())
      {
         Set<Date> includedDates = new HashSet<Date>();
         includedDates.addAll(timetable.getCalendarDays()); 
         addDates(calendar, includedDates, GtfsCalendarDate.INCLUDED);
      }
      else
      {
         logger.warn("timetable "+timetable.getObjectId()+" has no period nor calendarDays : rejected");
         return null;
      }

      return calendar;
   }

   /**
    * @param calendar
    * @param addedDates
    * @param type
    */
   private void addDates(GtfsCalendar calendar, Set<Date> addedDates, int type)
   {
      String serviceId = calendar.getServiceId();
      List<Date> orderedDates = new ArrayList<Date>();
      orderedDates.addAll(addedDates);
      Collections.sort(orderedDates);
      for (Date date : orderedDates)
      {
         GtfsCalendarDate gtfsDate = new GtfsCalendarDate();
         gtfsDate.setCalendar(calendar);
         gtfsDate.setDate(date);
         gtfsDate.setServiceId(serviceId);
         gtfsDate.setExceptionType(type);
         calendar.addCalendarDate(gtfsDate);
      }
   }

   /**
    * @param checkedDate
    * @param dayTypes
    * @return
    */
   private boolean checkValidDay(Date checkedDate, GtfsCalendar calendar )
   {
      boolean valid = false;
      // to avoid timezone 
      Calendar c = Calendar.getInstance();
      java.util.Date aDate = new java.util.Date(checkedDate.getTime()+HALD_DAY);
      c.setTime(aDate);

      switch (c.get(Calendar.DAY_OF_WEEK))
      {
      case Calendar.MONDAY : 
         if (calendar.isMonday() ) valid = true;
         break;
      case Calendar.TUESDAY : 
         if (calendar.isTuesday() ) valid = true;
         break;
      case Calendar.WEDNESDAY : 
         if (calendar.isWednesday()) valid = true;
         break;
      case Calendar.THURSDAY : 
         if (calendar.isThursday() ) valid = true;
         break;
      case Calendar.FRIDAY : 
         if (calendar.isFriday() ) valid = true;
         break;
      case Calendar.SATURDAY : 
         if (calendar.isSaturday()) valid = true;
         break;
      case Calendar.SUNDAY : 
         if (calendar.isSunday() ) valid = true;
         break;
      }
      return valid;
   }

   @Override
   public List<GtfsCalendar> produceAll(Timetable neptuneObject,GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }

}
