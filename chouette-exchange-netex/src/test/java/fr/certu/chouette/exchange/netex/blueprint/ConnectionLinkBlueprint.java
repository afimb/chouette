package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.UUID;

@Blueprint(ConnectionLink.class)
public class ConnectionLinkBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:SiteConnection:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "Conection Link";   
    
    @Default
    String comment = "Comment Connection Link";   
    
    @Mapped
    StopArea startOfLink;   
    
    @Mapped
    StopArea endOfLink;
    
    @Default
    BigDecimal linkDistance = new BigDecimal(2); 
    
    @Default
    Time defaultDuration = new Time(173335738);
    
    @Default
    Time frequentTravellerDuration = new Time(173335738);
    
    @Default
    Time occasionalTravellerDuration = new Time(173335738);

    @Default
    int objectVersion = 1;
    
    @Default
    ConnectionLinkTypeEnum linkType = ConnectionLinkTypeEnum.MIXED;
    
    @Default
    Time mobilityRestrictedTravellerDuration = new Time(1908888);
    
    @Mapped   
    boolean mobilityRestrictedSuitable = true;    

}
