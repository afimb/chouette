package fr.certu.chouette.exchange.gtfs.export.producer.mock;

import java.awt.Color;
import java.net.URL;
import java.util.Collection;

import fr.certu.chouette.exchange.gtfs.exporter.producer.AbstractProducer;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporterInterface;

public class GtfsDummyProducer extends AbstractProducer
{

   public GtfsDummyProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }
   
   public String toGtfsIdWrapper(String neptuneId, String prefix)
   {
      return toGtfsId(neptuneId, prefix);
   }
   
   public Color getColorWrapper(String color)
   {
      return getColor(color);
   }
   
   public URL getUrlWrapper(String url)
   {
      return getUrl(url);
   }
   
   public String getValueWrapper(String value)
   {
      return getValue(value);
   }

   public boolean isEmptyWrapper(Collection<? extends Object> list) 
   {
      return isEmpty(list);
   }
   
   public boolean isEmptyWrapper(String value)
   {
      return isEmpty(value);
   }
}
