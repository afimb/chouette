package mobi.chouette.model.blueprint;

import java.util.List;
import java.util.UUID;

import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(Timetable.class)
public class TimetableBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:TimeTable:" + UUID.randomUUID();
      }

   };

   @Default
   String comment = "TT " + UUID.randomUUID();
   
   @MappedList(target = Period.class, size = 0, ignoreEmpty = false)
   List<Period> periods;

   @MappedList(target = CalendarDay.class, size = 0, ignoreEmpty = false)
   List<CalendarDay> calendarDays;

   @Default
   Integer intDayTypes;

}
