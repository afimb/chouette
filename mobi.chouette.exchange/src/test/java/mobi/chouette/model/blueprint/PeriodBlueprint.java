package mobi.chouette.model.blueprint;

import java.util.Date;

import mobi.chouette.model.Period;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;

@Blueprint(Period.class)
public class PeriodBlueprint
{
   @Default
   Date startDate;

   @Default
   Date endDate;
}
