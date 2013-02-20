package fr.certu.chouette.exchange.netex.blueprint;

import fr.certu.chouette.model.neptune.PTNetwork;
import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import java.util.List;

@Blueprint(Line.class)
public class LineBlueprint {
    
    @Default
    String name = "Line";
    
    @Mapped   
    PTNetwork ptNetwork;
    
    @Mapped   
    Company company;
    
    @MappedList(target = Route.class, size = 2)
    List<Route> routes;

}
