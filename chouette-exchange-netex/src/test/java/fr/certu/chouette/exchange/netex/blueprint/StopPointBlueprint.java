package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import java.math.BigDecimal;
import java.util.UUID;

@Blueprint(StopPoint.class)
public class StopPointBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:StopPoint:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    int objectVersion = 1;   
    
    @Default
    String name = "A";

    @Nullable
    @Mapped   
    StopArea containedInStopArea;    
    
    @Default
    BigDecimal longitude = new BigDecimal("1.27");
    
    @Default
    BigDecimal latitude = new BigDecimal("2.27");
    
    @Default
    int position = 1;
    
}
