package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import java.util.Date;
import java.util.UUID;

@Blueprint(PTNetwork.class)
public class NetworkBlueprint {
        
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:PTNetwork:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "METRO";
    
    @Default
    int objectVersion = 1;
    
    //TODO : Verify date
    @Default
    Date versionDate = new Date(1234479600);
    
    @Default
    String description = "Réseau de métro de la RATP";
    
    @Default
    String registrationNumber = "110"; 
    
    @Default
    String sourceName = "RATP";
    
    @Default
    String sourceIdentifier = "RATP-KXD";
    
    @Default
    String comment = "Mon réseau";
    
    @Default
    PTNetworkSourceTypeEnum sourceType = PTNetworkSourceTypeEnum.PUBLICTRANSPORT;

}
