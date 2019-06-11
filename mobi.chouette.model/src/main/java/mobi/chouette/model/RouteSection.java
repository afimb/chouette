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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.GeometryUtil;

import com.vividsolutions.jts.geom.Coordinate;
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
 */

@Entity
@Table(name = "route_sections")
@NoArgsConstructor
@ToString
@Log4j
public class RouteSection extends NeptuneIdentifiedObject {
	private static final long serialVersionUID = 8490105295077539089L;

	@Getter
	@Setter
	@GenericGenerator(name = "route_sections_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "route_sections_id_seq"),
			@Parameter(name = "increment_size", value = "100")})
	@GeneratedValue(generator = "route_sections_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * length in meters
	 *
	 * @param distance
	 * New value
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
	 * New value
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
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "from_scheduled_stop_point_id")
	private ScheduledStopPoint fromScheduledStopPoint;


	/**
	 * Scheduled stop point at end of section
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "to_scheduled_stop_point_id")
	private ScheduledStopPoint toScheduledStopPoint;

	@Transient
	private Integer maxMetersFromQuay;
	private static final int DEFAULT_MAX_METERS_FROM_QUAY = 100;


	private int getMaxMetersFromQuay() {
		if (maxMetersFromQuay == null) {
			String maxAsString = System.getProperty("iev.route.section.stop.area.distance.max.meters");
			if (maxAsString != null) {
				maxMetersFromQuay = Integer.valueOf(maxAsString);
				log.info("Using configured value for iev.route.section.stop.area.distance.max.meters: " + maxMetersFromQuay);
			} else {
				maxMetersFromQuay = DEFAULT_MAX_METERS_FROM_QUAY;
				log.info("No value configured iev.route.section.export.stop.area.distance.max.meters, using default: " + maxMetersFromQuay);
			}
		}
		return maxMetersFromQuay;
	}

	public boolean isRouteSectionValid() {
		LineString geometry;
		if (getNoProcessing()) {
			geometry = getInputGeometry();
		} else {
			geometry = getProcessedGeometry();
		}

		if (geometry != null && geometry.getCoordinates() != null && geometry.getCoordinates().length > 0) {
			Coordinate lineStart = geometry.getCoordinates()[0];
			Coordinate lineEnd = geometry.getCoordinates()[geometry.getCoordinates().length - 1];

			Coordinate from = getCoordinateFromScheduledStopPoint(fromScheduledStopPoint);
			Coordinate to = getCoordinateFromScheduledStopPoint(toScheduledStopPoint);

			if (from == null || to == null) {
				return false;
			}

			double distanceFromStart = GeometryUtil.calculateDistanceInMeters(from.x, from.y, lineStart.x, lineStart.y);
			double distanceFromEnd = GeometryUtil.calculateDistanceInMeters(to.x, to.y, lineEnd.x, lineEnd.y);
			int maxMeters=getMaxMetersFromQuay();
			if (maxMeters < 0) {
				return true;
			}
			if (distanceFromStart > maxMeters || distanceFromEnd > maxMeters) {
				return false;
			}
		}
		return true;
	}

	private Coordinate getCoordinateFromScheduledStopPoint(ScheduledStopPoint scheduledStopPoint) {
		if (scheduledStopPoint == null || scheduledStopPoint.getContainedInStopAreaRef() == null) {
			return null;
		}
		StopArea stopArea = scheduledStopPoint.getContainedInStopAreaRef().getObject();
		if (stopArea == null || stopArea.getLongitude() == null || stopArea.getLatitude() == null) {
			return null;
		}
		return new Coordinate(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());
	}

}
