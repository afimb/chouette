package mobi.chouette.exchange.gtfs.exporter.producer;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import lombok.Getter;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;

public abstract class AbstractProducer
{

   @Getter
   private GtfsExporterInterface exporter;

   public AbstractProducer(GtfsExporterInterface exporter)
   {
      this.exporter = exporter;
   }

   static protected String toGtfsId(String neptuneId, String prefix, boolean keepOriginal)
   {
      if(keepOriginal) {
    	  return neptuneId.replace(':', '.');
      } else {
    	  String[] tokens = neptuneId.split(":");
	      if (tokens[0].equals(prefix))
	         return tokens[2];
	      else
	         return tokens[0] + "." + tokens[2];
	      }
   }

   static protected boolean isEmpty(String s)
   {
      return s == null || s.trim().isEmpty();
   }

   static protected boolean isEmpty(Collection<? extends Object> s)
   {
      return s == null || s.isEmpty();
   }

   static protected String getValue(String s)
   {
      if (isEmpty(s))
         return null;
      else
         return s;

   }

   static protected Color getColor(String s)
   {
      if (isEmpty(s))
         return null;
      else
         return new Color(Integer.parseInt(s, 16));
   }

   static protected URL getUrl(String s)
   {
      if (isEmpty(s))
         return null;
      else
         try
         {
            URL result = new URL(s);
            String protocol = result.getProtocol();
            if (!(protocol.equals("http") || protocol.equals("https")))
            {
               throw new MalformedURLException();
            }
            return result;
         }
         catch (MalformedURLException e)
         {
            // TODO: manage exception
            return null;
         }
   }
   
   static boolean isTrue(Boolean value)
   {
	   return value != null && value;
   }

}
