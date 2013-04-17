package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.UUID;

@Blueprint(AccessLink.class)
public class AccessLinkBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:AccessLink:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "AccessLink";
    
    @Default
    int objectVersion = 1;    
    
    @Default
    String startOfLinkId = "StopPlace1";      

    @Default
    String endOfLinkId = "StopPlace2";
    
    @Default
    ConnectionLinkTypeEnum linkType = ConnectionLinkTypeEnum.MIXED;
    
    @Default
    LinkOrientationEnum linkOrientation = LinkOrientationEnum.ACCESSPOINT_TO_STOPAREA;
    
    @Default
    BigDecimal linkDistance = new BigDecimal(2); 
    
    @Default
    Time defaultDuration = new Time(173335738);
    
    @Default
    Time frequentTravellerDuration = new Time(173335738);
    
    @Default
    Time occasionalTravellerDuration = new Time(173335738);
            
    @Mapped   
    boolean mobilityRestrictedSuitable = true;       

}
