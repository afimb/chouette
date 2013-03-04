package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;

@Blueprint(AreaCentroid.class)
public class AreaCentroidBlueprint {
    
    @Default
    LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;
   
    
}
