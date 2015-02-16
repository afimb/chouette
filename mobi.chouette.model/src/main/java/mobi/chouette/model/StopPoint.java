package mobi.chouette.model;

import javax.persistence.CascadeType;
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
 * Chouette StopPoint : a StopPoint on a route
 * <p/>
 * Neptune mapping : StopPoint <br/>
 * Gtfs mapping : none
 */

@Entity
@Table(name = "stop_points")
@NoArgsConstructor
@ToString(exclude = { "route" })
public class StopPoint extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -4913573673645997423L;

	@Getter
	@Setter
	@GenericGenerator(name = "stop_points_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "stop_points_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@Id
	@GeneratedValue(generator = "stop_points_id_seq")
	@Column(name = "id", nullable = false)
	protected Long id;
	
	/**
	 * position on the route
	 * 
	 * @param position
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "position")
	private Integer position;

	/**
	 * stop area container
	 * 
	 * @param containedInStopArea
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY , cascade = { CascadeType.PERSIST})
	@JoinColumn(name = "stop_area_id")
	private StopArea containedInStopArea;

	/**
	 * set stop area
	 * 
	 * @param stopArea
	 */
	public void setContainedInStopArea(StopArea stopArea) {
		if (this.containedInStopArea != null) {
			this.containedInStopArea.getContainedStopPoints().remove(this);
		}
		this.containedInStopArea = stopArea;
		if (stopArea != null) {
			stopArea.getContainedStopPoints().add(this);
		}
	}

	/**
	 * route
	 * 
	 * @param route
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

	/**
	 * set route
	 * 
	 * @param route
	 */
	public void setRoute(Route route) {
		if (this.route != null) {
			this.route.getStopPoints().remove(this);
		}
		this.route = route;
		if (route != null) {
			route.getStopPoints().add(this);
		}
	}

}
