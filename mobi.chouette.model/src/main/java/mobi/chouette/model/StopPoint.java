package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@ManyToOne(fetch = FetchType.LAZY)
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
