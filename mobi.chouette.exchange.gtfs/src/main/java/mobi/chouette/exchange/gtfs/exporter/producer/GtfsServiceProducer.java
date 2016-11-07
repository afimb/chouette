/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsCalendar;
import mobi.chouette.exchange.gtfs.model.GtfsCalendarDate;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.CopyUtil;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsServiceProducer extends
AbstractProducer
{
   public GtfsServiceProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   GtfsCalendar calendar = new GtfsCalendar();
   GtfsCalendarDate calendarDate = new GtfsCalendarDate();

   public boolean save(List<Timetable> timetables,  String prefix, boolean keepOriginalId)
   {

      Timetable reduced = merge(timetables, prefix,keepOriginalId);

      if (reduced == null) return false;

      String serviceId = toGtfsId(reduced.getObjectId(), prefix, keepOriginalId);

      if (!isEmpty(reduced.getPeriods()))
      {
         clear(calendar);
         for (DayTypeEnum dayType : reduced.getDayTypes())
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

         Period period = reduced.getPeriods().get(0);
         calendar.setStartDate(period.getStartDate());
         calendar.setEndDate(period.getEndDate());

         try
         {
            getExporter().getCalendarExporter().export(calendar);
         }
         catch (Exception e)
         {
            log.error(e.getMessage(),e);
            return false;
         }
      }
      if (!isEmpty(reduced.getCalendarDays()))
      {
         for (CalendarDay day : reduced.getCalendarDays())
         {
            saveDay(serviceId,day);
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
	  Timetable reduced = CopyUtil.copy(timetable);

      // no periods => nothing to reduce
      if (isEmpty(reduced.getPeriods()))
      {
         return reduced;
      }

      // one valid period => nothing to reduce
      if (reduced.getPeriods().size() == 1 && ! isEmpty(reduced.getDayTypes())) 
      {
    	  return reduced;
      }

      // replace all periods as dates
      removePeriods(reduced);

      return reduced;
   }

   private boolean saveDay(String serviceId,CalendarDay day)
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
         log.error(e.getMessage(),e);
         return false;
      }
      return true;

   }

   public Timetable removePeriods(Timetable timetable)
   {
      Set<Date> excludedDates = new HashSet<Date>(
    		  timetable.getExcludedDates());
      Set<Date> includedDates = new HashSet<Date>(
    		  timetable.getPeculiarDates());

      for (Period period : timetable.getPeriods())
      {
         Date checkedDate = period.getStartDate();
         Date endDate = new Date(period.getEndDate().getTime() + Timetable.ONE_DAY);
         while (checkedDate.before(endDate))
         {
            if (!excludedDates.contains(checkedDate)
                  && !includedDates.contains(checkedDate))
            {
               if (checkValidDay(checkedDate, timetable))
               {
                  includedDates.add(new Date(checkedDate.getTime()));
               }
            }
            checkedDate = new Date(checkedDate.getTime() + Timetable.ONE_DAY);
         }
      }
      timetable.getPeriods().clear();
      timetable.setIntDayTypes(Integer.valueOf(0));
      timetable.getCalendarDays().clear();
      for (Date date : includedDates)
      {
         timetable.addCalendarDay(new CalendarDay(date, true));
      }
      Collections.sort(timetable.getCalendarDays());
      return timetable;

   }

   private boolean checkValidDay(Date checkedDate, Timetable timetable)
   {
      boolean valid = false;
      // to avoid timezone
      Calendar c = Calendar.getInstance();
      c.set(Calendar.HOUR_OF_DAY, 12);
      java.util.Date aDate = new java.util.Date(checkedDate.getTime());
      c.setTime(aDate);
      List<DayTypeEnum> dayTypes = timetable.getDayTypes();
      switch (c.get(Calendar.DAY_OF_WEEK))
      {
      case Calendar.MONDAY :
         if (dayTypes.contains(DayTypeEnum.Monday)) valid = true;
         break;
      case Calendar.TUESDAY :
         if (dayTypes.contains(DayTypeEnum.Tuesday)) valid = true;
         break;
      case Calendar.WEDNESDAY :
         if (dayTypes.contains(DayTypeEnum.Wednesday)) valid = true;
         break;
      case Calendar.THURSDAY :
         if (dayTypes.contains(DayTypeEnum.Thursday)) valid = true;
         break;
      case Calendar.FRIDAY :
         if (dayTypes.contains(DayTypeEnum.Friday)) valid = true;
         break;
      case Calendar.SATURDAY :
         if (dayTypes.contains(DayTypeEnum.Saturday)) valid = true;
         break;
      case Calendar.SUNDAY :
         if (dayTypes.contains(DayTypeEnum.Sunday)) valid = true;
         break;
      }
      return valid;
   }


   private Timetable merge(List<Timetable> timetables,String prefix, boolean keepOriginalId)
   {
      Timetable merged = reduce(timetables.get(0));
      if (timetables.size() > 1)
      {
         removePeriods(merged);
         for (int i = 1; i < timetables.size(); i++)
         {
            Timetable reduced = removePeriods(CopyUtil.copy(timetables.get(i)));
            for (CalendarDay day : reduced.getCalendarDays()) {
            	merged.addCalendarDay(day);
			}
            
         }
         merged.setObjectId(prefix+":"+Timetable.TIMETABLE_KEY+":"+key(timetables,prefix,false));
      }
      merged.computeLimitOfPeriods();
      return merged;
   }

   public boolean isValid(Timetable timetable)
   {
      // protection if no valid days
      if (timetable.getDayTypes().isEmpty()) timetable.getPeriods().clear();
      return !timetable.getPeriods().isEmpty() || !timetable.getCalendarDays().isEmpty();
   }

   public String key(List<Timetable> timetables,String prefix, boolean keepOriginalId)
   {
      if (isEmpty(timetables)) return null;
      // remove invalid timetables (no date set) 
      for (Iterator<Timetable> iterator = timetables.iterator(); iterator.hasNext();)
      {
         Timetable timetable = iterator.next();
         if (!isValid(timetable)) iterator.remove();
      }
      if (isEmpty(timetables)) return null;

      Collections.sort(timetables, new TimetableSorter());
      String key = "";
      for (Timetable timetable : timetables)
      {
         key += "-"+toGtfsId(timetable.getObjectId(), prefix, keepOriginalId);
      }
      return key.substring(1);
   }

   private class TimetableSorter implements Comparator<Timetable>
   {
      @Override
      public int compare(Timetable arg0, Timetable arg1)
      {
         return arg0.getObjectId().compareTo(arg1.getObjectId());
      }

   }

}
