package mobi.chouette.model.blueprint;

import java.util.List;
import java.util.UUID;

import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.TransportModeNameEnum;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.MappedList;
import com.tobedevoured.modelcitizen.field.FieldCallback;

@SuppressWarnings("deprecation")
@Blueprint(VehicleJourney.class)
public class VehicleJourneyBlueprint
{

   @Default
   FieldCallback objectId = new FieldCallback()
   {
      @Override
      public String get(Object model)
      {
         return "TEST:VehicleJourney:" + UUID.randomUUID();
      }

   };

   @Default
   String publishedJourneyName = "nom " + UUID.randomUUID().toString();

   @Default
   String publishedJourneyIdentifier = "nom court "
         + UUID.randomUUID().toString();

   @Default
   String comment = "comment " + UUID.randomUUID().toString();

   @Default
   TransportModeNameEnum transportMode = TransportModeNameEnum.Bus;

   @Default
   Long number = Long.valueOf(5);

//   @Nullable
//   @Mapped
//   JourneyPattern journeyPattern;
//
//   @Nullable
//   @Mapped
//   Route route;
//
//   @Nullable
//   @Mapped
//   Company company;

   @MappedList(target = VehicleJourneyAtStop.class, size = 0)
   List<VehicleJourneyAtStop> vehicleJourneyAtStops;

   @MappedList(target = Timetable.class, size = 0)
   List<Timetable> timetables;

}
