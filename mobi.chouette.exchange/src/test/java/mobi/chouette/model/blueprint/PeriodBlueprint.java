package mobi.chouette.model.blueprint;

import mobi.chouette.model.Period;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import org.joda.time.LocalDate;

@Blueprint(Period.class)
public class PeriodBlueprint
{
   @Default
   LocalDate startDate;

   @Default
   LocalDate endDate;
}
