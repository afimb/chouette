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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "route_points")
@NoArgsConstructor
@ToString(callSuper = true)
public class RoutePoint extends NeptuneIdentifiedObject {

	private static final long serialVersionUID = -4913573673645997423L;

	@Getter
	@Setter
	@GenericGenerator(name = "route_points_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator",
			parameters = {
					@Parameter(name = "sequence_name", value = "route_points_id_seq"),
					@Parameter(name = "increment_size", value = "100")})
	@GeneratedValue(generator = "route_points_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

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
	 * @param value New value
	 */
	public void setName(String value) {
		name = StringUtils.abbreviate(value, 255);
	}

	/**
	 * whether or not the route point is located on the boarder between two countries
	 *
	 * @param boarderCrossing
	 * New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "boarder_crossing")
	private Boolean boarderCrossing;

	/**
	 * scheduled stop point
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "scheduled_stop_point_id")
	private ScheduledStopPoint scheduledStopPoint;

}
