package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import java.sql.Date;

import org.trident.schema.trident.DayTypeType;
import org.trident.schema.trident.PeriodType;
import org.trident.schema.trident.TimetableType;

import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class TimetableProducer extends
      AbstractJaxbNeptuneProducer<TimetableType, Timetable>
{

   @Override
   public TimetableType produce(Timetable timetable)
   {
      TimetableType castorTimetable = tridentFactory.createTimetableType();

      //
      populateFromModel(castorTimetable, timetable);

      castorTimetable.setComment(getNotEmptyString(timetable.getComment()));
      castorTimetable.setVersion(timetable.getVersion());

      for (Date peculiarDay : timetable.getPeculiarDates())
      {
         if (peculiarDay != null)
         {

            castorTimetable.getCalendarDay().add(toCalendar(peculiarDay));
         }
      }

      for (Period period : timetable.getEffectivePeriods())
      {
         if (period != null)
         {
            PeriodType castorPeriod = tridentFactory.createPeriodType();
            castorPeriod.setStartOfPeriod(toCalendar(period.getStartDate()));
            castorPeriod.setEndOfPeriod(toCalendar(period.getEndDate()));
            castorTimetable.getPeriod().add(castorPeriod);
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
                  castorTimetable.getDayType().add(
                        DayTypeType.fromValue(dayType.name()));
               } catch (IllegalArgumentException e)
               {
                  // TODO: handle exception
               }
            }
         }
      }
      if (timetable.getVehicleJourneys() != null)
      {
         for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys())
         {
            castorTimetable.getVehicleJourneyId().add(
                  getNonEmptyObjectId(vehicleJourney));
         }
      }

      return castorTimetable;
   }

}
