package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import java.util.List;

@Blueprint(Route.class)
public class RouteBlueprint {
    
    @Default
    String name = "1001101070001";    
    
    @Default
    String publishedName = "7B (PRE-SAINT-GERVAIS &lt;-&gt; LOUIS BLANC)";
    
    @Default
    String objectId = "RATP_PIVI:Route:317452";
    
    @Default
    String wayBack = "A";
    
    @MappedList(target = StopPoint.class, size = 6)
    List<StopPoint> stopPoints;

}
