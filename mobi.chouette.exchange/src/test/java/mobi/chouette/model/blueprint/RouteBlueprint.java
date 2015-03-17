package mobi.chouette.model.blueprint;

import java.util.List;
import java.util.UUID;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.PTDirectionEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(Route.class)
public class RouteBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:Route:" + UUID.randomUUID();
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
   PTDirectionEnum direction = PTDirectionEnum.North;

   @Default
   String comment = "aller retour";

   @MappedList(target = StopPoint.class, size = 0)
   List<StopPoint> stopPoints;

   @MappedList(target = JourneyPattern.class, size = 0)
   List<JourneyPattern> journeyPatterns;

}
