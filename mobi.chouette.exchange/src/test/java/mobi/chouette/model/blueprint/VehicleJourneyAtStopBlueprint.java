package mobi.chouette.model.blueprint;

import java.sql.Time;

import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourneyAtStop;

import com.tobedevoured.modelcitizen.annotation.Blueprint;
import com.tobedevoured.modelcitizen.annotation.Default;
import com.tobedevoured.modelcitizen.annotation.Mapped;

@Blueprint(VehicleJourneyAtStop.class)
public class VehicleJourneyAtStopBlueprint
{

//   @Mapped
//   VehicleJourney vehicleJourney;

   @Mapped
   StopPoint stopPoint;

   @Default
   Time departureTime = new Time(2500);

   @Default
   Time arrivalTime = new Time(2800);

}
