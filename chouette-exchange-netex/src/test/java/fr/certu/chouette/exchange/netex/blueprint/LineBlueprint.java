package fr.certu.chouette.exchange.netex.blueprint;

import fr.certu.chouette.model.neptune.PTNetwork;
import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import java.util.List;
import java.util.UUID;

@Blueprint(Line.class)
public class LineBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:Line:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "7B";   
    
    @Default
    String number = "7Bis";   
    
    @Default
    String publishedName = "Mairie d Issy porte d Orleans";   
    
    @Default
    String comment = "Extension à partir de juin";   

    @Default
    int objectVersion = 1;
    
    @Default
    TransportModeNameEnum transportModeName = TransportModeNameEnum.METRO;
    
    @Default
    String registrationNumber = "100110107";
    
    @Mapped   
    PTNetwork ptNetwork;
    
    @Mapped   
    Company company;
    
    @MappedList(target = Route.class, size = 0)
    List<Route> routes;
    
    @MappedList(target = GroupOfLine.class, size = 0)
    List<GroupOfLine> groupOfLines;

}
