package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.StopPoint;
import java.util.List;

@Blueprint(JourneyPattern.class)
public class JourneyPatternBlueprint {

    @Default
    int objectVersion = 1;       
    
    @Default
    String objectId = "RATP_PIVI:ServicePattern:514339";       
    
    @Default
    String name = "101";
    
    @Default
    String routeId = "RATP_PIVI:Route:317452";
    
    @MappedList(target = StopPoint.class, size = 3)
    List<StopPoint> stopPoints;
    
    @MappedList(target = VehicleJourney.class, size = 2)
    List<VehicleJourney> vehicleJourneys;

}
