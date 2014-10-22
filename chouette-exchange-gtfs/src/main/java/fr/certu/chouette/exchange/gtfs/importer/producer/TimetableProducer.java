package fr.certu.chouette.exchange.gtfs.importer.producer;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.report.Report;

public class TimetableProducer extends
      AbstractModelProducer<Timetable, GtfsCalendar>
{
   private static Logger logger = Logger.getLogger(TimetableProducer.class);

   @Override
   public Timetable produce(GtfsCalendar gtfsCalendar, Report report)
   {
      Timetable timetable = new Timetable();

      // objectId, objectVersion, creatorId, creationTime
      timetable.setObjectId(composeIncrementalObjectId(Timetable.TIMETABLE_KEY,
            gtfsCalendar.getServiceId(), logger));

      if (gtfsCalendar.getMonday())
         timetable.addDayType(DayTypeEnum.Monday);
      if (gtfsCalendar.getTuesday())
         timetable.addDayType(DayTypeEnum.Tuesday);
      if (gtfsCalendar.getWednesday())
         timetable.addDayType(DayTypeEnum.Wednesday);
      if (gtfsCalendar.getThursday())
         timetable.addDayType(DayTypeEnum.Thursday);
      if (gtfsCalendar.getFriday())
         timetable.addDayType(DayTypeEnum.Friday);
      if (gtfsCalendar.getSaturday())
         timetable.addDayType(DayTypeEnum.Saturday);
      if (gtfsCalendar.getSunday())
         timetable.addDayType(DayTypeEnum.Sunday);

      if (gtfsCalendar.getStartDate() != null
            && gtfsCalendar.getEndDate() != null)
      {
         Period period = new Period();
         period.setStartDate(gtfsCalendar.getStartDate());
         period.setEndDate(gtfsCalendar.getEndDate());
         timetable.addPeriod(period);
      } else
      {
         // logger.info("service without period "+gtfsCalendar.getServiceId());
      }

      List<Period> periods = timetable.getPeriods();
      if (periods != null)
         Collections.sort(periods, new PeriodSorter());
      buildComment(timetable);
      return timetable;
   }

   public void addDate(Timetable timetable, GtfsCalendarDate date)
   {
      timetable.addCalendarDay(new CalendarDay(date.getDate(), date
          .getExceptionType() != GtfsCalendarDate.ExceptionType.Removed));
   }
   
   /**
    * produce a comment with first date, end date and maybe applicable days
    * 
    * @param timetable
    */
   public void buildComment(Timetable timetable)
   {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
      String monday = (timetable.getDayTypes().contains(DayTypeEnum.Monday)) ? "Mo"
            : "..";
      String tuesday = (timetable.getDayTypes().contains(DayTypeEnum.Tuesday)) ? "Tu"
            : "..";
      String wednesday = (timetable.getDayTypes()
            .contains(DayTypeEnum.Wednesday)) ? "We" : "..";
      String thursday = (timetable.getDayTypes().contains(DayTypeEnum.Thursday)) ? "Th"
            : "..";
      String friday = (timetable.getDayTypes().contains(DayTypeEnum.Friday)) ? "Fr"
            : "..";
      String saturday = (timetable.getDayTypes().contains(DayTypeEnum.Saturday)) ? "Sa"
            : "..";
      String sunday = (timetable.getDayTypes().contains(DayTypeEnum.Sunday)) ? "Su"
            : "..";

      Date firstDate = null;
      Date lastDate = null;
      if (timetable.getPeriods() != null && !timetable.getPeriods().isEmpty())
      {
         for (Period period : timetable.getPeriods())
         {
            if (firstDate == null || period.getStartDate().before(firstDate))
               firstDate = period.getStartDate();
            if (lastDate == null || period.getEndDate().after(lastDate))
               lastDate = period.getEndDate();
         }
      }
      if (timetable.getCalendarDays() != null
            && !timetable.getCalendarDays().isEmpty())
      {
         Calendar cal = Calendar.getInstance();
         for (Date date : timetable.getPeculiarDates())
         {
            cal.setTime(date);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
               monday = "Mo";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
               tuesday = "Tu";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
               wednesday = "We";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
               thursday = "Th";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
               friday = "Fr";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
               saturday = "Sa";
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
               sunday = "Su";
            if (firstDate == null || date.before(firstDate))
               firstDate = date;
            if (lastDate == null || date.after(lastDate))
               lastDate = date;
         }
      }

      // security if timetable is empty
      if (firstDate != null && lastDate != null)
      {
         String comment = "From " + format.format(firstDate) + " to "
               + format.format(lastDate) + " : " + monday + tuesday + wednesday
               + thursday + friday + saturday + sunday;
         timetable.setComment(comment);
      } else
      {
         timetable.setComment("Empty timetable");
      }
   }

   private class PeriodSorter implements Comparator<Period>
   {

      @Override
      public int compare(Period o1, Period o2)
      {

         return o1.getStartDate().compareTo(o2.getStartDate());
      }

   }
}
