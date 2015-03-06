package mobi.chouette.model.blueprint;

import java.util.Date;

import mobi.chouette.model.CalendarDay;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;

@Blueprint(CalendarDay.class)
public class CalendarDayBlueprint
{
   @Default
   Date date;

   @Default
   Boolean included = Boolean.TRUE;
}
