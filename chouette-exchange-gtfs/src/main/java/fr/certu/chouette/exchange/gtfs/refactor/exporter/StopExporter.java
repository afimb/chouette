package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;

public class StopExporter extends ExporterImpl<GtfsStop> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding;
   };

   public static final String FILENAME = "stops.txt";

   public StopExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsStop bean) throws IOException
   {
      export(STOP_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStop> STOP_CONVERTER = new Converter<String, GtfsStop>()
   {

      @Override
      public GtfsStop from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsStop input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new StopExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(StopExporter.class.getName(), factory);
   }
}
