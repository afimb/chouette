package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Chouette StopPoint : a StopPoint on a route
 * <p/>
 * Neptune mapping : StopPoint <br/>
 * Gtfs mapping : none
 */

@Entity
@Table(name = "stop_points")
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"route"})
public class StopPoint extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -4913573673645997423L;

	@Getter
	@Setter
	@GenericGenerator(name = "stop_points_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator",
		parameters = {
			@Parameter(name = "sequence_name", value = "stop_points_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "stop_points_id_seq")
	@Id
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
	 * boarding possibility
	 *
	 * @param forBoarding
	 *           New value
	 * @return The actual value
	 *
	 * @since 2.5.2
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "for_boarding")
	private BoardingPossibilityEnum forBoarding;

	/**
	 * alighting possibility
	 *
	 * @param forAlighting
	 *           New value
	 * @return The actual value
	 *
	 * @since 2.5.2
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "for_alighting")
	private AlightingPossibilityEnum forAlighting;
	/**
	 * route
	 *
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

	/**
	 * scheduled stop point
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	@JoinColumn(name = "scheduled_stop_point_id")
	private ScheduledStopPoint scheduledStopPoint;


	/**
	 * set ScheduledStopPoint
	 *
	 * @param scheduledStopPoint
	 */
	public void setScheduledStopPoint(ScheduledStopPoint scheduledStopPoint) {
		if (this.scheduledStopPoint != null) {
			this.scheduledStopPoint.getStopPoints().remove(this);
		}
		this.scheduledStopPoint = scheduledStopPoint;
		if (scheduledStopPoint != null) {
			scheduledStopPoint.getStopPoints().add(this);
		}
	}


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

	/**
	 * comment : not saved, use for extension
	 *
	 * @param : comment
	 * @return the actual value
	 */
	@Getter
	@Setter
	@Transient
	private String comment;


	/**
	 * current destination display for this stop point
	 *
	 * @param destinationDisplay
	 * New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "destination_display_id")
	private DestinationDisplay destinationDisplay;

}
