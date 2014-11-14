package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import lombok.Getter;

import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;

public abstract class AbstractProducer
{

   @Getter
   private GtfsExporter exporter;

   public AbstractProducer(GtfsExporter exporter)
   {
      this.exporter = exporter;
   }

   static protected String toGtfsId(String neptuneId, String prefix)
   {
      String[] tokens = neptuneId.split(":");
      if (tokens[0].equals(prefix))
         return tokens[2];
      else
         return tokens[0]+"."+tokens[2];
   }

   static boolean isEmpty(String s)
   {
      return s == null || s.trim().isEmpty();
   }

   static boolean isEmpty(Collection<? extends Object> s)
   {
      return s == null || s.isEmpty();
   }

   static String  getValue(String s)
   {
      if (isEmpty(s))
         return null;
      else 
         return s;

   }

   static Color getColor(String s)
   {
      if (isEmpty(s))
         return null;
      else 
         return new Color(Integer.parseInt(s, 16));
   }

   static URL getUrl(String s)
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

}
