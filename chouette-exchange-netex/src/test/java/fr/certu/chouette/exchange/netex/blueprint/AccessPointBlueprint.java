package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.UUID;

@Blueprint(AccessPoint.class)
public class AccessPointBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:AccessPoint:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "AccessPoint";   
    
    @Default
    String comment = "AccessPoint Comment";   
    
    @Default
    int objectVersion = 1;    
    
    @Default
    LongLatTypeEnum longLatType = LongLatTypeEnum.WGS84;
    
    @Default
    BigDecimal longitude = new BigDecimal( 2.373D + ( UUID.randomUUID().getLeastSignificantBits()%100)/1000000);
    
    @Default
    BigDecimal latitude = new BigDecimal( 48.8D + ( UUID.randomUUID().getLeastSignificantBits()%100)/1000000); 
    
    @Default
    AccessPointTypeEnum type = AccessPointTypeEnum.INOUT;   
    
    @Default
    Time openingTime = new Time(173335738);
    
    @Default
    Time closingTime  = new Time(173335738);        

}
