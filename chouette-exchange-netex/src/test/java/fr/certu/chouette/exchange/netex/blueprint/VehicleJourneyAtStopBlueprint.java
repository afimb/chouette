package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.sql.Time;

@Blueprint(VehicleJourneyAtStop.class)
public class VehicleJourneyAtStopBlueprint {             
        
    @Mapped   
    VehicleJourney vehicleJourney;
        
    @Mapped   
    StopPoint stopPoint;
    
    @Default
    Time departureTime = new Time(2500);    
    
    @Default
    Time arrivalTime = new Time(2800);

}
