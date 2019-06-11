/**
 * Projet CHOUETTE
 * <p>
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.GeometryUtil;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.SectionStatusEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * convert JourneyPattern's RouteSections to Shapes
 */
@Log4j
public class GtfsShapeProducer extends AbstractProducer {
	private GeometryFactory factory = new GeometryFactory(new PrecisionModel(10), 4326);

	public GtfsShapeProducer(GtfsExporterInterface exporter) {
		super(exporter);
	}

	private GtfsShape shape = new GtfsShape();

	private Integer maxMetersFromQuay;
	private static final int DEFAULT_MAX_METERS_FROM_QUAY = 100;

	public boolean save(JourneyPattern neptuneObject, String prefix, boolean keepOriginalId) {
		boolean result = true;
		if (neptuneObject.getSectionStatus() != SectionStatusEnum.Completed)
			return false;
		int shapePtSequence = 0;
		int startIndex = 0;
		double distance = 0.0;

		for (RouteSection rs : neptuneObject.getRouteSections()) {
			shape.setShapeId(toGtfsId(neptuneObject.getObjectId(), prefix, keepOriginalId));
			if (rs == null) {
				continue;
			}
			LineString ls = rs.getProcessedGeometry();
			if (isTrue(rs.getNoProcessing()) || rs.getProcessedGeometry() == null)
				ls = rs.getInputGeometry();


			if (ls == null || !isLineStringGoodMatchForQuays(ls, rs.getFromScheduledStopPoint(), rs.getToScheduledStopPoint())) {
				result = false;
				continue;
			}
			// CoordinateSequence cs = ls.getCoordinateSequence();
			Coordinate[] cs = ls.getCoordinates();
			Coordinate prev = cs[0];
			for (int i = startIndex; i < cs.length; i++) {
				// The end Point of a Section is the start Point of the next Section
				// Save the first Points of the first Section and then the other Points (not the first) for all Sections
				shape.setShapePtLon(new BigDecimal(cs[i].x));
				shape.setShapePtLat(new BigDecimal(cs[i].y));
				shape.setShapePtSequence(shapePtSequence++);
				if (i > 0) {
					distance += computeDistance(prev, cs[i]);
					prev = cs[i];
				}
				shape.setShapeDistTraveled(Float.valueOf((float) distance));
				try {
					getExporter().getShapeExporter().export(shape);
				} catch (Exception e) {
					log.warn("export failed for line " + neptuneObject.getObjectId(), e);
					return false;
				}
			}
			startIndex = 1;
		}
		return result;
	}

	private double computeDistance(Coordinate obj1, Coordinate obj2) {
		LineString ls = factory.createLineString(new Coordinate[]{obj1, obj2});
		return ls.getLength() * (Math.PI / 180) * 6378137;
	}


	protected boolean isLineStringGoodMatchForQuays(LineString lineString, ScheduledStopPoint fromSSP, ScheduledStopPoint toSSP) {
		if (lineString != null && lineString.getCoordinates() != null && lineString.getCoordinates().length > 0) {
			Coordinate lineStart = lineString.getCoordinates()[0];
			Coordinate lineEnd = lineString.getCoordinates()[lineString.getCoordinates().length - 1];

			Coordinate from = getCoordinateFromScheduledStopPoint(fromSSP);
			Coordinate to = getCoordinateFromScheduledStopPoint(toSSP);

			if (from == null || to == null) {
				return false;
			}

			double distanceFromStart = GeometryUtil.calculateDistanceInMeters(from.x, from.y, lineStart.x, lineStart.y);
			double distanceFromEnd = GeometryUtil.calculateDistanceInMeters(to.x, to.y, lineEnd.x, lineEnd.y);
			int maxMetersFromQuay = getMaxMetersFromQuay();
			if (maxMetersFromQuay < 0) {
				return true;
			}
			if (distanceFromStart > maxMetersFromQuay || distanceFromEnd > maxMetersFromQuay) {
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

	private int getMaxMetersFromQuay() {
		if (maxMetersFromQuay == null) {
			String maxAsString = System.getProperty("iev.route.section.export.stop.area.distance.max.meters");
			if (maxAsString != null) {
				maxMetersFromQuay = Integer.valueOf(maxAsString);
				log.info("Using configured value for iev.route.section.export.stop.area.distance.max.meters: " + maxMetersFromQuay);
			} else {
				maxMetersFromQuay = DEFAULT_MAX_METERS_FROM_QUAY;
				log.info("No value configured iev.route.section.export.stop.area.distance.max.meters, using default: " + maxMetersFromQuay);
			}
		}
		return maxMetersFromQuay;
	}
}
