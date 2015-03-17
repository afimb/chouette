package mobi.chouette.model.blueprint;

import java.util.List;
import java.util.UUID;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(JourneyPattern.class)
public class JourneyPatternBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:JourneyPattern:" + UUID.randomUUID();
      }

   };

   @Default
   int objectVersion = 1;

   @Default
   String name = "101";

   @Default
   String publishedName = "numero: 101";

   @Default
   String registrationNumber = "ST-101";

   @Default
   String comment = "omnibus";

   @MappedList(target = StopPoint.class, size = 0)
   List<StopPoint> stopPoints;

   @MappedList(target = VehicleJourney.class, size = 0)
   List<VehicleJourney> vehicleJourneys;

}
