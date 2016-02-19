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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette VehicleJourneyAtStop : passing time on stops
 * <p/>
 * Neptune mapping : VehicleJourneyAtStop <br/>
 * Gtfs mapping : StopTime <br/>
 */

@Entity
@Table(name = "vehicle_journey_at_stops", uniqueConstraints = @UniqueConstraint(columnNames = {
		"vehicle_journey_id", "stop_point_id" }, name = "index_vehicle_journey_at_stops_on_stop_point_id"))
@NoArgsConstructor
@ToString(callSuper=true, exclude = { "vehicleJourney" })
public class VehicleJourneyAtStop extends NeptuneObject {

	private static final long serialVersionUID = 194243517715939830L;

	@Getter
	@Setter
	@GenericGenerator(name = "vehicle_journey_at_stops_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "vehicle_journey_at_stops_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "vehicle_journey_at_stops_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * connecting Service Id
	 * 
	 * @param connectingServiceId
	 *            New value
	 * @return The actual value
	 */
	@Deprecated
	@Getter
	@Setter
	@Transient
	// @Column(name = "connecting_service_id")
	private String connectingServiceId;

	/**
	 * not saved, boarding alighting possibility
	 * 
	 * @param boardingAlightingPossibility
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
//	@Enumerated(EnumType.STRING)
	@Transient
//	@Column(name = "boarding_alighting_possibility")
	private BoardingAlightingPossibilityEnum boardingAlightingPossibility;

	/**
	 * arrival time
	 * 
	 * @param arrivalTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "arrival_time")
	private Time arrivalTime;

	/**
	 * departure time
	 * 
	 * @param departureTime
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "departure_time")
	private Time departureTime;

	/*
	 * waiting time
	 * 
	 * @param waitingTime
	 *            New value
	 * @return The actual value
	 */
	//@Deprecated
	//@Getter
	//@Setter
	//@Transient
	// @Column(name = "waiting_time")
	//private Time waitingTime;

	/*
	 * elapse duration <br/>
	 * for vehicle journey with time slots<br/>
	 * definition should change in next release
	 * 
	 * @param elapseDuration
	 *            New value
	 * @return The actual value
	 */
	//@Deprecated
	//@Getter
	//@Setter
	// @Column(name = "elapse_duration")
	//@Transient
	//private Time elapseDuration;

	/*
	 * headway frequnecy <br/>
	 * for vehicle journey with time slots<br/>
	 * field should move to vehicleJourney in next release
	 * 
	 * @param headwayFrequency
	 *            New value
	 * @return The actual value
	 */
	//@Deprecated
	//@Getter
	//@Setter
	//@Column(name = "headway_frequency")
	//@Transient
	//private Time headwayFrequency;

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
		if (this.vehicleJourney != null) {
			this.vehicleJourney.getVehicleJourneyAtStops().remove(this);
		}
		this.vehicleJourney = vehicleJourney;
		if (vehicleJourney != null) {
			vehicleJourney.getVehicleJourneyAtStops().add(this);
		}
	}

	/**
	 * stop point reference <br/>
	 * 
	 * @param stopPoint
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stop_point_id")
	private StopPoint stopPoint;

}
