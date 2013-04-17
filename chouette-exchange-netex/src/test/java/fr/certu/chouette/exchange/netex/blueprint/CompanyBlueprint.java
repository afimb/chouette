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
            return "RATP_PIVI:Operator:" + UUID.randomUUID();
        }
        
    };   
    
    @Default
    String name = "RATP nom long";
    
    @Default
    String shortName = "RATP";
    
    @Default
    String phone = "01.02.03.04.05";
    
    @Default
    String fax = "01.02.03.04.05";
    
    @Default
    String email = "support@ratp.fr";
    
    @Default
    String organisationalUnit = "SIT";
    
    @Default
    String operatingDepartmentName = "Central IV";
    
    @Default
    String code = "RATP-ASD";
    
    @Default
    int objectVersion = 1;
      
    @Default
    String registrationNumber = "100";    

}
