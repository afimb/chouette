package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import fr.certu.chouette.model.neptune.JourneyPattern;
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
    
    @MappedList(target = StopPoint.class, size = 3, ignoreEmpty = false)
    List<StopPoint> stopPoints;
    
    @MappedList(target = JourneyPattern.class, size = 2, ignoreEmpty = false)
    List<JourneyPattern> journeyPatterns;

}
