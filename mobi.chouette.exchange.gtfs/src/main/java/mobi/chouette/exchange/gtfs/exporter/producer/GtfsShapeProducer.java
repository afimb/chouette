/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.math.BigDecimal;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsShape;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.type.SectionStatusEnum;

/**
 * convert JourneyPattern's RouteSections to Shapes
 */
@Log4j
public class GtfsShapeProducer extends AbstractProducer
{
   public GtfsShapeProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsShape shape = new GtfsShape();

   public boolean save(JourneyPattern neptuneObject, ActionReport report, String prefix)
   {
	   boolean result = true;
	   if (neptuneObject.getSectionStatus() != SectionStatusEnum.Completed)
		   return false;
	   int startIndex = 0;
	   for (RouteSection rs : neptuneObject.getRouteSections() ) {
		   shape.setShapeId(toGtfsId(neptuneObject.getObjectId(), prefix));
		   int shapePtSequence = 0;
		   LineString ls = rs.getInputGeometry();
		   if (ls == null)
			   ls = rs.getProcessedGeometry();
		   if (ls == null) {
			   result = false;
			   continue;
		   }
		   CoordinateSequence cs = ls.getCoordinateSequence();
		   for (int i = startIndex; i < cs.size(); i++) {
			   // The end Point of a Section is the start Point of the next Section
			   // Save the first Points of the first Section and then the other Points (not the first) for all Sections
			   shape.setShapePtLat(new BigDecimal(cs.getX(i)));
			   shape.setShapePtLon(new BigDecimal(cs.getY(i)));
			   shape.setShapePtSequence(shapePtSequence++);

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
}
