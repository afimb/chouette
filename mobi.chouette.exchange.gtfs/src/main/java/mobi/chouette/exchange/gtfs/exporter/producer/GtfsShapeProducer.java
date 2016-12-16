/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.math.BigDecimal;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.type.SectionStatusEnum;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * convert JourneyPattern's RouteSections to Shapes
 */
@Log4j
public class GtfsShapeProducer extends AbstractProducer
{
   private	GeometryFactory factory = new GeometryFactory(new PrecisionModel(10), 4326);

   public GtfsShapeProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsShape shape = new GtfsShape();

   public boolean save(JourneyPattern neptuneObject,  String prefix, boolean keepOriginalId)
   {
	   boolean result = true;
	   if (neptuneObject.getSectionStatus() != SectionStatusEnum.Completed)
		   return false;
	   int shapePtSequence = 0;
	   int startIndex = 0;
	   float distance = (float) 0.0;

	   for (RouteSection rs : neptuneObject.getRouteSections() ) {
		   shape.setShapeId(toGtfsId(neptuneObject.getObjectId(), prefix, keepOriginalId));
		   if (rs == null)
		   {
		      continue;
		   }
		   LineString ls = rs.getProcessedGeometry();
		   if (isTrue(rs.getNoProcessing()) || rs.getProcessedGeometry() == null)
		    ls = rs.getInputGeometry();
		   if (ls == null) {
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
			   if (i > 0)
			   {
				   distance += (float) computeDistance(prev,cs[i]);
				   prev = cs[i];
			   }
			   shape.setShapeDistTraveled(Float.valueOf(distance));
			   try {
				   getExporter().getShapeExporter().export(shape);
			   } catch (Exception e) {
				   log.warn("export failed for line "+neptuneObject.getObjectId(),e);
				   return false;
			   }
		   }
		   startIndex = 1;
	   }
	   return result;
   }
   
	private double computeDistance(Coordinate obj1, Coordinate obj2) {
		LineString ls = factory.createLineString(new Coordinate[]{obj1,obj2});
		return ls.getLength() * (Math.PI / 180) * 6378137;
	}

}
