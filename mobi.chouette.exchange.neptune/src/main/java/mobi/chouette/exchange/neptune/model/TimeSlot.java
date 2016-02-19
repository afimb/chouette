package mobi.chouette.exchange.neptune.model;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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

   @Getter
	@Setter
	@GenericGenerator(name = "time_slots_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "time_slots_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "time_slots_id_seq")
	@Column(name = "id", nullable = false)
	protected Long id;

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
