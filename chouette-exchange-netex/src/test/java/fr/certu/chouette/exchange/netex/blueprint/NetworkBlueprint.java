package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.PTNetwork;
import java.util.Date;

@Blueprint(PTNetwork.class)
public class NetworkBlueprint {
        
    @Default
    String name = "METRO";
    
    @Default
    int objectVersion = 1;
    
    //TODO : Verify date
    @Default
    Date versionDate = new Date(1234479600);
    
    @Default
    String objectId = "RATP_PIVI:PTNetwork:110";
    
    @Default
    String description = "Réseau de métro de la RATP";
    
    @Default
    String registrationNumber = "110"; 
    
    

}
