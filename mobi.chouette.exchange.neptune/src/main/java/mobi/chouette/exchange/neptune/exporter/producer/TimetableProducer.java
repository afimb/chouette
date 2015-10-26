package mobi.chouette.exchange.neptune.exporter.producer;

import java.sql.Date;

import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;

import org.trident.schema.trident.DayTypeType;
import org.trident.schema.trident.PeriodType;
import org.trident.schema.trident.TimetableType;

public class TimetableProducer extends
      AbstractJaxbNeptuneProducer<TimetableType, Timetable>
{

   //@Override
   public TimetableType produce(Timetable timetable, boolean addExtension)
   {
      TimetableType jaxbTimetable = tridentFactory.createTimetableType();

      //
      populateFromModel(jaxbTimetable, timetable);

      jaxbTimetable.setComment(getNotEmptyString(timetable.getComment()));
      jaxbTimetable.setVersion(getNotEmptyString(timetable.getVersion()));

      for (Date peculiarDay : timetable.getEffectiveDates())
      {
         if (peculiarDay != null)
         {

            jaxbTimetable.getCalendarDay().add(toCalendar(peculiarDay));
         }
      }

      for (Period period : timetable.getEffectivePeriods())
      {
         if (period != null)
         {
            PeriodType jaxbPeriod = tridentFactory.createPeriodType();
            jaxbPeriod.setStartOfPeriod(toCalendar(period.getStartDate()));
            jaxbPeriod.setEndOfPeriod(toCalendar(period.getEndDate()));
            jaxbTimetable.getPeriod().add(jaxbPeriod);
         }
      }

      if (timetable.getDayTypes() != null)
      {
         for (DayTypeEnum dayType : timetable.getDayTypes())
         {
            if (dayType != null)
            {
               try
               {
                  jaxbTimetable.getDayType().add(
                        DayTypeType.fromValue(dayType.name()));
               } catch (IllegalArgumentException e)
               {
                  // TODO: handle exception
               }
            }
         }
      }

      return jaxbTimetable;
   }

}
