package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.annotation.Nullable;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import java.util.List;

@Blueprint(StopArea.class)
public class StopAreaBlueprint {
    
    @Default
    int objectVersion = 1;
    
    @Default
    String objectId = "RATP_PIVI:RoutePoint:317452A0A5246063";
    
    @Default
    String name = "A";
    
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
