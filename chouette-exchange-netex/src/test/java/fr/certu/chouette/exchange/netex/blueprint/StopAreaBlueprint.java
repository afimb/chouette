package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import java.util.List;
import java.util.UUID;

@Blueprint(StopArea.class)
public class StopAreaBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:StopArea:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    int objectVersion = 1;
    
    @Default
    String name = "A"+UUID.randomUUID();
    
    @Default
    String comment = "mon arret "+UUID.randomUUID();
    
    @Default
    String registrationNumber = "C-"+UUID.randomUUID();
    
    @Default
    String nearestTopicName = "POI-"+UUID.randomUUID();
    
    @Default
    int fareCode = 1;

    @Mapped   
    AreaCentroid areaCentroid;  
    
    @Default
    ChouetteAreaEnum areaType = ChouetteAreaEnum.BOARDINGPOSITION;
    
    @Nullable
    @Mapped   
    StopArea parent;   

    @MappedList(target = StopArea.class, size = 0)
    List<StopArea> containedStopAreas;
    
    @MappedList(target = StopPoint.class, size = 0)
    List<StopPoint> containedStopPoints;
      
}
