/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsServiceProducer extends
AbstractProducer
{
   public GtfsServiceProducer(GtfsExporter exporter)
   {
      super(exporter);
   }

   private static final Logger logger = Logger
         .getLogger(GtfsServiceProducer.class);
   private static final long ONE_DAY = 3600000 * 24;

   GtfsCalendar calendar = new GtfsCalendar();
   GtfsCalendarDate calendarDate = new GtfsCalendarDate();

   public boolean save(Timetable timetable, GtfsReport report, String prefix)
   {

      Timetable reduced = reduce(timetable);

      if (reduced == null) return false;

      String serviceId = toGtfsId(reduced.getObjectId(), prefix);

      if (reduced.getPeriods() != null && !reduced.getPeriods().isEmpty())
      {
         clear(calendar);
         for (DayTypeEnum dayType : timetable.getDayTypes())
         {
            switch (dayType)
            {
            case Monday:
               calendar.setMonday(true);
               break;
            case Tuesday:
               calendar.setTuesday(true);
               break;
            case Wednesday:
               calendar.setWednesday(true);
               break;
            case Thursday:
               calendar.setThursday(true);
               break;
            case Friday:
               calendar.setFriday(true);
               break;
            case Saturday:
               calendar.setSaturday(true);
               break;
            case Sunday:
               calendar.setSunday(true);
               break;
            case WeekDay:
               calendar.setMonday(true);
               calendar.setTuesday(true);
               calendar.setWednesday(true);
               calendar.setThursday(true);
               calendar.setFriday(true);
               break;
            case WeekEnd:
               calendar.setSaturday(true);
               calendar.setSunday(true);
               break;
            default:
               // nothing to do
            }
         }
         calendar.setServiceId(serviceId);

         Period period = timetable.getPeriods().get(0);
         calendar.setStartDate(period.getStartDate());
         calendar.setEndDate(period.getEndDate());

         try
         {
            getExporter().getCalendarExporter().export(calendar);
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
         }
      }
      if (timetable.getCalendarDays() != null
            && !timetable.getCalendarDays().isEmpty())
      {
         for (CalendarDay day : reduced.getCalendarDays())
         {
            saveDay(serviceId,day,report);
         }
      }

      return true;
   }


   private void clear(GtfsCalendar c)
   {
      c.setMonday(false);
      c.setTuesday(false);
      c.setWednesday(false);
      c.setThursday(false);
      c.setFriday(false);
      c.setSaturday(false);
      c.setSunday(false);
   }

   private Timetable reduce(Timetable timetable)
   {

      // no periods => nothing to reduce
      if (timetable.getPeriods().isEmpty()) 
      {
         return timetable;
      }

      // one valid period => nothing to reduce
      if (timetable.getPeriods().size() == 1 && ! timetable.getDayTypes().isEmpty()) return timetable;

      // no valid days

      return timetable;
   }

   private boolean saveDay(String serviceId,CalendarDay day,GtfsReport report)
   {

      calendarDate.setDate(day.getDate());
      calendarDate.setServiceId(serviceId);
      calendarDate.setExceptionType(day.getIncluded() ? GtfsCalendarDate.ExceptionType.Added: GtfsCalendarDate.ExceptionType.Removed);
      try
      {
         getExporter().getCalendarDateExporter().export(calendarDate);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }
      return true;

   }


   public Timetable merge(List<Timetable> timetables)
   {
      Timetable merged = new Timetable();

      return merged;
   }

   public boolean isValid(Timetable timetable)
   {
      // protection if no valid days
      if (timetable.getDayTypes().isEmpty()) timetable.getPeriods().clear();
      return !timetable.getPeriods().isEmpty() || !timetable.getCalendarDays().isEmpty();

   }

   public String key(List<Timetable> timetables)
   {
      return "";
   }


}

//public void bidon()
//{
//   // GTFS can't use multiple periods, converted as single dates
//   Set<Date> excludedDates = new HashSet<Date>(
//         timetable.getExcludedDates());
//   Set<Date> includedDates = new HashSet<Date>(
//         timetable.getPeculiarDates());
//
//   for (Period period : timetable.getPeriods())
//   {
//      Date checkedDate = period.getStartDate();
//      Date endDate = new Date(period.getEndDate().getTime() + ONE_DAY);
//      while (checkedDate.before(endDate))
//      {
//         if (!excludedDates.contains(checkedDate)
//               && !includedDates.contains(checkedDate))
//         {
//            if (checkValidDay(checkedDate, calendar))
//            {
//               includedDates.add(checkedDate);
//            }
//         }
//         checkedDate = new Date(checkedDate.getTime() + ONE_DAY);
//      }
//   }
//
//   // create only included CalendarDates
//   addDates(calendar, includedDates, GtfsCalendarDate.ExceptionType.Added);
//}
//} else if (timetable.getCalendarDays() != null
//      && !timetable.getCalendarDays().isEmpty())
//{
//   addDates(calendar, timetable.getCalendarDays());
//} else
//{
//   logger.warn("timetable " + timetable.getObjectId()
//         + " has no period nor calendarDays : rejected");
//   return false;
//}
//
//if (!calendar.getCalendarDates().isEmpty())
//{
//   Collections.sort(calendar.getCalendarDates());
//}
//
//return true;
//}

