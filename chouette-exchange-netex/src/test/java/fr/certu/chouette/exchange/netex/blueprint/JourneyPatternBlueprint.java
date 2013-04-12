package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.StopPoint;
import java.util.List;
import java.util.UUID;

@Blueprint(JourneyPattern.class)
public class JourneyPatternBlueprint {

    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:JourneyPattern:" + UUID.randomUUID();
        }
        
    };   
        
    @Default
    int objectVersion = 1;       
    
    @Default
    String name = "101";
    
    @Default
    String publishedName = "numero: 101";
    
    @Default
    String registrationNumber = "ST-101";
    
    @Default
    String comment = "omnibus";
    
    @Default
    String routeId = "RATP_PIVI:Route:317452";
    
    @MappedList(target = StopPoint.class, size = 0)
    List<StopPoint> stopPoints;
    
    @MappedList(target = VehicleJourney.class, size = 0)
    List<VehicleJourney> vehicleJourneys;

}
