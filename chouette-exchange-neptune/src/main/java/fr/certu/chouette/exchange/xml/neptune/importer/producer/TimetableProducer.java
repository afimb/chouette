package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.trident.schema.trident.DayTypeType;
import org.trident.schema.trident.PeriodType;
import org.trident.schema.trident.TimetableType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;

public class TimetableProducer extends
      AbstractModelProducer<Timetable, TimetableType>
{

   @Override
   public Timetable produce(Context context, TimetableType xmlTimetable)
   {
      Timetable timetable = new Timetable();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, timetable, xmlTimetable);

      timetable.setComment(getNonEmptyTrimedString(xmlTimetable.getComment()));

      timetable.setVersion(getNonEmptyTrimedString(xmlTimetable.getVersion()));

      // DayType optional
      if (xmlTimetable.getDayType() != null)
      {
         for (DayTypeType xmlDayType : xmlTimetable.getDayType())
            try
            {
               timetable.addDayType(DayTypeEnum.valueOf(xmlDayType.value()));
            } catch (IllegalArgumentException e)
            {
               // TODO: traiter le cas de non correspondance
            }
      }

      //
      if (xmlTimetable.getCalendarDay() != null)
      {
         for (XMLGregorianCalendar calendarDay : xmlTimetable.getCalendarDay())
         {
            timetable.addCalendarDay(new CalendarDay(getSqlDate(calendarDay),
                  true));
         }
      }

      if (xmlTimetable.getPeriod() != null)
      {
         for (PeriodType xmlPeriod : xmlTimetable.getPeriod())
         {
            timetable
                  .addPeriod(new Period(
                        getSqlDate(xmlPeriod.getStartOfPeriod()),
                        getSqlDate(xmlPeriod.getEndOfPeriod())));
         }
      }

      List<String> vehicleJourneys = new ArrayList<String>(
            xmlTimetable.getVehicleJourneyId());
      xmlTimetable.getVehicleJourneyId().clear();

      Timetable sharedBean = getOrAddSharedData(context, timetable, xmlTimetable);
      if (sharedBean != null)
         timetable = sharedBean;
      xmlTimetable.getVehicleJourneyId().addAll(vehicleJourneys);

      for (String vehicleJourneyId : vehicleJourneys)
      {
         timetable
               .addVehicleJourneyId(getNonEmptyTrimedString(vehicleJourneyId));
      }

      return timetable;
   }

}
