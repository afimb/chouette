package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.util.Date;

@Blueprint(Period.class)
public class PeriodBlueprint {    
    @Default
    Date startDate;           

    @Default
    Date endDate;   
}
