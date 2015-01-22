package mobi.chouette.model;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.StringUtils;

/**
 * Chouette TimeSlot a peroid for vehicleJourney with frequency
 * <p>
 * Note : this object will be remove soon because it is too close to Neptune
 * model and will not be adaptable to GTFS or NeTEx formats
 * <p/>
 * Neptune mapping : TimeSlot <br/>
 * Gtfs mapping :none <br/>
 */
@Entity
@Table(name = "time_slots")
@NoArgsConstructor
@ToString
@Log4j
public class TimeSlot extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = 7510494886757866590L;

	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name")
	private String name;

	/**
	 * set name <br/>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setName(String value) {
		name = StringUtils.abbreviate(value, 255);
	}

	/**
	 * start of slot
	 * 
	 * @param beginningSlotTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "beginning_slot_time")
	private Time beginningSlotTime;

	/**
	 * end of slot
	 * 
	 * @param endSlotTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "end_slot_time")
	private Time endSlotTime;

	/**
	 * first departure of journey in slot
	 * 
	 * @param firstDepartureTimeInSlot
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "first_departure_time_in_slot")
	private Time firstDepartureTimeInSlot;

	/**
	 * last departure of journey in slot
	 * 
	 * @param lastDepartureTimeInSlot
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "last_departure_time_in_slot")
	private Time lastDepartureTimeInSlot;

}
