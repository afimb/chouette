package fr.certu.chouette.exchange.netex.blueprint;

import fr.certu.chouette.model.neptune.PTNetwork;
import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import java.util.List;

@Blueprint(Line.class)
public class LineBlueprint {
    
    @Default
    String name = "7B";
    
    @Default
    String objectId = "RATP_PIVI:Line:100110107";

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
    
    @MappedList(target = Route.class, size = 2)
    List<Route> routes;

}
