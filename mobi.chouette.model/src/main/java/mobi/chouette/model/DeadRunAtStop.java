package mobi.chouette.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Chouette DeadRunAtStop : passing time on stops
 * <p/>
 * Neptune mapping : DeadRunAtStop <br/>
 * Gtfs mapping : StopTime <br/>
 */

@Entity
@Table(name = "dead_run_at_stops", uniqueConstraints = @UniqueConstraint(columnNames = {
		"dead_run_id", "stop_point_id" }, name = "index_dead_run_at_stops_on_stop_point_id"))
@NoArgsConstructor
@ToString(callSuper=true, exclude = { "deadRun" })
public class DeadRunAtStop extends NeptuneIdentifiedObject implements JourneyAtStop {

	private static final long serialVersionUID = 194243517715939830L;

	@Getter
	@Setter
	@GenericGenerator(name = "dead_run_at_stops_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "dead_run_at_stops_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "dead_run_at_stops_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	

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
	private LocalTime arrivalTime;

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
	private LocalTime departureTime;

	
	/**
	 * departure day offset
	 * 
	 * @param departureDayOffset
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "departure_day_offset")
	private int departureDayOffset;
	
	
	/**
	 * arrival day offset
	 * 
	 * @param arrivalDayOffset
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "arrival_day_offset")
	private int arrivalDayOffset;
	


	/**
	 * dead run reference <br/>
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dead_run_id")
	private DeadRun deadRun;

	/**
	 * set vehicle journey reference
	 * 
	 * @param deadRun
	 */
	public void setDeadRun(DeadRun deadRun) {
		if (this.deadRun != null) {
			this.deadRun.getDeadRunAtStops().remove(this);
		}
		this.deadRun = deadRun;
		if (deadRun != null) {
			deadRun.getDeadRunAtStops().add(this);
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
