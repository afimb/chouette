/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.GroupOfLine;
import java.util.UUID;

@Blueprint(GroupOfLine.class)
public class GroupOfLineBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:GroupOfLine:" + UUID.randomUUID();
        }
        
    };   
    
    @Default
    String name = "Noctilien";
    
    @Default
    int objectVersion = 1;
      
    @Default
    String comment = "Réseau de nuit";    
    
}
