package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.Company;
import java.util.UUID;

@Blueprint(Company.class)
public class CompanyBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:Company:" + UUID.randomUUID();
        }
        
    };   
    
    @Default
    String name = "RATP";
    
    @Default
    int objectVersion = 1;
      
    @Default
    String registrationNumber = "100";    

}
