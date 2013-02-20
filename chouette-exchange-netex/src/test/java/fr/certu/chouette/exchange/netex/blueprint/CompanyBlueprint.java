package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import fr.certu.chouette.model.neptune.Company;

@Blueprint(Company.class)
public class CompanyBlueprint {
    
    @Default
    String name = "RATP";
    
    @Default
    int objectVersion = 1;
    
    @Default
    String objectId = "RATP_PIVI:Company:100";
    
      
    @Default
    String registrationNumber = "100";    

}
