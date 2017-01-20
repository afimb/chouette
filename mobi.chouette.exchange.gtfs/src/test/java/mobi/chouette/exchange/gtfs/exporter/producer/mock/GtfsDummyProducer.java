package mobi.chouette.exchange.gtfs.exporter.producer.mock;

import java.awt.Color;
import java.net.URL;
import java.util.Collection;

import mobi.chouette.exchange.gtfs.exporter.producer.AbstractProducer;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;

public class GtfsDummyProducer extends AbstractProducer
{

   public GtfsDummyProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }
   
   public String toGtfsIdWrapper(String neptuneId, String prefix, boolean keepOriginalId)
   {
      return toGtfsId(neptuneId, prefix, keepOriginalId);
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
