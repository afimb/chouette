package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;

public class StopTimeExporter extends ExporterImpl<GtfsStopTime> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
   };

   public static final String FILENAME = "stop_times.txt";

   public StopTimeExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsStopTime bean) throws IOException
   {
      export(STOPTIME_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStopTime> STOPTIME_CONVERTER = new Converter<String, GtfsStopTime>()
   {

      @Override
      public GtfsStopTime from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsStopTime input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new StopTimeExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(StopTimeExporter.class.getName(), factory);
   }

}
