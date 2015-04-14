package mobi.chouette.model.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Envelope;

@Log4j
public class CoordinateUtil
{
   private static Map<String, MathTransform> map = new HashMap<String, MathTransform>();

   private static MathTransform createMathTransform(String source, String target)
         throws Exception
   {
      String key = source + "-" + target;
      MathTransform transform = map.get(key);
      if (transform == null)
      {
         CoordinateReferenceSystem sourceCRS = CRS.decode(source);
         CoordinateReferenceSystem targetCRS = CRS.decode(target);
         transform = CRS.findMathTransform(sourceCRS, targetCRS);
         map.put(key, transform);
      }
      return transform;
   }
   
   private static Exception ex;
   
   public static Exception getLastException()
   {
	   Exception val = ex;
	   ex = null;
	   return val;
   }

   public static Coordinate transform(String source, String target, Coordinate p)
   {
      Coordinate result = null;
      try
      {
         MathTransform transform = createMathTransform(source, target);
         Envelope envelope = new Envelope(p.x.doubleValue(), p.x.doubleValue(),
               p.y.doubleValue(), p.y.doubleValue());
         Envelope geometry = JTS.transform(envelope, transform);

         result = new Coordinate(new BigDecimal(geometry.getMinX()),
               new BigDecimal(geometry.getMinY()));

      } catch (Exception e)
      {
    	  ex = e;
         log.error("fail to convert from " + source + " to " + target
               + " projected point " + p + " : " + e.getMessage());
      }

      return result;
   }

}
