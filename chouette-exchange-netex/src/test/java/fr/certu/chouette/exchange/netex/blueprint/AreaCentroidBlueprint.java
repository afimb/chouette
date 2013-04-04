package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import java.math.BigDecimal;
import java.util.UUID;

@Blueprint(AreaCentroid.class)
public class AreaCentroidBlueprint {
    
    @Default
    LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;
    
    @Default
    BigDecimal longitude = new BigDecimal( 2.373D + ( UUID.randomUUID().getLeastSignificantBits()%100)/1000000);
    
    @Default
    BigDecimal latitude = new BigDecimal( 48.8D + ( UUID.randomUUID().getLeastSignificantBits()%100)/1000000);
   
    @Mapped
    Address address;
    
    @Mapped
    ProjectedPoint projectedPoint;
}
