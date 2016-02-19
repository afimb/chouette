package mobi.chouette.model;

import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Models the frequencies for journeys in timesheet category.
 * 
 * @author zbouziane
 * @since 3.2.0
 * 
 */
@Entity
@Table(name = "journey_frequencies")
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "vehicleJourney", "timeband" })
public class JourneyFrequency extends NeptuneObject {

	private static final long serialVersionUID = 8361606377991750952L;

	@Getter
	@Setter
	@GenericGenerator(name = "journey_frequencies_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "journey_frequencies_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "journey_frequencies_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * vehicle journey reference <br/>
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_journey_id")
	private VehicleJourney vehicleJourney;

	/**
	 * set vehicle journey reference
	 * 
	 * @param vehicleJourney
	 */
	public void setVehicleJourney(VehicleJourney vehicleJourney) {
		if (this.vehicleJourney != vehicleJourney) {
			if (this.vehicleJourney != null) {
				this.vehicleJourney.getJourneyFrequencies().remove(this);
			}
			this.vehicleJourney = vehicleJourney;
			if (vehicleJourney != null) {
				vehicleJourney.getJourneyFrequencies().add(this);
			}
		}
	}

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "timeband_id")
	private Timeband timeband;

	/**
	 * set time band reference
	 * 
	 * @param timeband
	 *            The new time band of this journey frequency
	 */
	public void setTimeband(Timeband timeband) {
		if (this.timeband != timeband) {
			if (this.timeband != null) {
				this.timeband.getJourneyFrequencies().remove(this);
			}
			this.timeband = timeband;
			if (timeband != null) {
				timeband.getJourneyFrequencies().add(this);
			}
		}
	}

	/**
	 * The scheduled headway interval
	 * 
	 * @param scheduledHeadwayInterval
	 *            The new scheduled headway interval of this journey frequency
	 * @return The scheduled headway interval of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "scheduled_headway_interval", nullable = false)
	private Time scheduledHeadwayInterval;

	/**
	 * The first departure time
	 * 
	 * @param firstDepartureTime
	 *            The new first departure time of this journey frequency
	 * @return The first departure time of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "first_departure_time", nullable = false)
	private Time firstDepartureTime;

	/**
	 * The last departure time
	 * 
	 * @param lastDepartureTime
	 *            The new last departure time of this journey frequency
	 * @return The last departure time of this journey frequency
	 */
	@Getter
	@Setter
	@Column(name = "last_departure_time", nullable = false)
	private Time lastDepartureTime;

	/**
	 * Are the first and last departure times exact or not.
	 */
	@Getter
	@Setter
	@Column(name = "exact_time")
	private Boolean exactTime = false;
}
