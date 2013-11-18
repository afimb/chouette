package fr.certu.chouette.exchange.netex.blueprint;

import java.util.Date;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;

import fr.certu.chouette.model.neptune.Period;

@Blueprint(Period.class)
public class PeriodBlueprint {    
    @Default
    Date startDate;           

    @Default
    Date endDate;   
}
