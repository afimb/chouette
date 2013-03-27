package fr.certu.chouette.exchange.netex.blueprint;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import java.util.List;
import java.util.UUID;

@Blueprint(Route.class)
public class RouteBlueprint {
    
    @Default
    FieldCallback objectId = new FieldCallback() {
        @Override
        public String get( Object model) {
            return "RATP_PIVI:Route:" + UUID.randomUUID();
        }
        
    };  
    
    @Default
    String name = "Lycée > Hopital";    
    
    @Default
    String publishedName = "7B (PRE-SAINT-GERVAIS &lt;-&gt; LOUIS BLANC)";    
    
    @Default
    String wayBack = "A";
    
    @Default
    String number = "1A - par Pont Neuf";
    
    @Default
    PTDirectionEnum direction = PTDirectionEnum.NORTH;
    
    @Default
    String comment = "aller retour";
    
    @MappedList(target = StopPoint.class, size = 0)
    List<StopPoint> stopPoints;
    
    @MappedList(target = JourneyPattern.class, size = 0)
    List<JourneyPattern> journeyPatterns;

}
