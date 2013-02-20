package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import java.math.BigDecimal;

@Blueprint(StopPoint.class)
public class StopPointBlueprint {
    
    @Default
    int objectVersion = 1;
    
    @Default
    String objectId = "RATP_PIVI:RoutePoint:317452A0A5246063";
    
    @Default
    String name = "A";

    @Mapped   
    StopArea containedInStopArea;    
    
    @Default
    BigDecimal longitude = new BigDecimal("1.27");
    
    @Default
    BigDecimal latitude = new BigDecimal("2.27");
    
    @Default
    int position = 1;
}
