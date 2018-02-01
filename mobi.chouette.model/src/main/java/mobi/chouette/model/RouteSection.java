package mobi.chouette.model;

import java.math.BigDecimal;

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

import com.vividsolutions.jts.geom.LineString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * Chouette Route Section : geographic route between to stop areas
 * <p/>
 * Neptune mapping : no <br/>
 * Gtfs mapping : used for shapes
 * 
 * @since 3.2.0
 * 
 */

@Entity
@Table(name = "route_sections")
@NoArgsConstructor
@ToString
public class RouteSection extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 8490105295077539089L;

	@Getter
	@Setter
	@GenericGenerator(name = "route_sections_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "route_sections_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "route_sections_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * length in meters
	 * 
	 * @param distance
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "distance")
	private BigDecimal distance;

	/**
	 * <br/>
	 * 
	 * <ul>
	 * <li>true if</li>
	 * <li>false if</li>
	 * </ul>
	 * 
	 * @param noProcessing
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "no_processing")
	private Boolean noProcessing = false;

	@Getter
	@Setter
	@Column(name = "input_geometry")
	@Type(type = "org.hibernate.spatial.GeometryType")
	private LineString inputGeometry;

	@Getter
	@Setter
	@Column(name = "processed_geometry")
	@Type(type = "org.hibernate.spatial.GeometryType")
	private LineString processedGeometry;

	/**
	 * Scheduled stop point at start of section.
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	@JoinColumn(name = "from_scheduled_stop_point_id")
	private ScheduledStopPoint fromScheduledStopPoint;


	/**
	 * Scheduled stop point at end of section
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	@JoinColumn(name = "to_scheduled_stop_point_id")
	private ScheduledStopPoint toScheduledStopPoint;

}
