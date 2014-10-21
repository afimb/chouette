package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripExporter extends ExporterImpl<GtfsTrip> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, shape_id;
   };

   public static final String FILENAME = "trips.txt";

   public TripExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsTrip bean) throws IOException
   {
      export(TRIP_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsTrip> TRIP_CONVERTER = new Converter<String, GtfsTrip>()
   {

      @Override
      public GtfsTrip from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsTrip input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new TripExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(TripExporter.class.getName(), factory);
   }

}
