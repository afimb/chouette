package mobi.chouette.exchange.neptune.exporter.model;

import java.sql.Time;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobi.chouette.model.NeptuneIdentifiedObject;

/**
 * Chouette TimeSlot a peroid for vehicleJourney with frequency
 * <p>
 * Note : this object will be remove soon because it is too close to Neptune
 * model and will not be adaptable to GTFS or NeTEx formats
 * <p/>
 * Neptune mapping : TimeSlot <br/>
 * Gtfs mapping :none <br/>
 */
@NoArgsConstructor
public class TimeSlot extends NeptuneIdentifiedObject
{
   private static final long serialVersionUID = 7510494886757866590L;

   /**
    * name
    * 
    * @return The actual value
    */
   @Getter
   @Setter
   private String name;


   /**
    * start of slot
    * 
    * @param beginningSlotTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Time beginningSlotTime;

   /**
    * end of slot
    * 
    * @param endSlotTime
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Time endSlotTime;

   /**
    * first departure of journey in slot
    * 
    * @param firstDepartureTimeInSlot
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Time firstDepartureTimeInSlot;

   /**
    * last departure of journey in slot
    * 
    * @param lastDepartureTimeInSlot
    *           New value
    * @return The actual value
    */
   @Getter
   @Setter
   private Time lastDepartureTimeInSlot;


}
