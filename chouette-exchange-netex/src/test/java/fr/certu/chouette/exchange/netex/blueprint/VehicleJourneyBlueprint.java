package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.util.List;

@Blueprint(VehicleJourney.class)
public class VehicleJourneyBlueprint {
    
    @Default
    String publishedJourneyName = "1001101070001";           

}
