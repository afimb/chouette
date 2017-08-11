package mobi.chouette.model.blueprint;

import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;
import org.joda.time.LocalTime;

@Blueprint(VehicleJourneyAtStop.class)
public class VehicleJourneyAtStopBlueprint
{

//   @Mapped
//   VehicleJourney vehicleJourney;

   @Mapped
   StopPoint stopPoint;

   @Default
   LocalTime departureTime = new LocalTime(2500);

   @Default
   LocalTime arrivalTime = new LocalTime(2800);

}
