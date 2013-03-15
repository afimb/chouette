package fr.certu.chouette.exchange.netex.blueprint;

import com.google.inject.internal.Nullable;
import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.util.List;
import java.util.UUID;

@Blueprint(VehicleJourney.class)
public class VehicleJourneyBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:VehicleJourney:" + UUID.randomUUID();
        }
        
    };     
    
    @Default
    String publishedJourneyName = "1001101070001"; 
    
    @Nullable
    @Mapped
    JourneyPattern journeyPattern;    
    
    @Nullable
    @Mapped
    Route route;    
    
    @MappedList(target = VehicleJourneyAtStop.class, size = 0)
    List<VehicleJourneyAtStop> vehicleJourneyAtStops;    
    
    @MappedList(target = Timetable.class, size = 0)
    List<Timetable> timetables;    

}
