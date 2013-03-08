package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import java.math.BigDecimal;

@Blueprint(AreaCentroid.class)
public class AreaCentroidBlueprint {
    
    @Default
    LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;
    
    @Default
    BigDecimal longitude = new BigDecimal( 1);
    
    @Default
    BigDecimal latitude = new BigDecimal( 1);
   
    @Mapped
    Address address;
}
