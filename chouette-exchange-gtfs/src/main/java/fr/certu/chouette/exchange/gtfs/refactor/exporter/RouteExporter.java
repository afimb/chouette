package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;

public class RouteExporter extends ExporterImpl<GtfsRoute> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, Route_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
   };

   public static final String FILENAME = "routes.txt";

   public RouteExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsRoute bean) throws IOException
   {
      export(ROUTE_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsRoute> ROUTE_CONVERTER = new Converter<String, GtfsRoute>()
   {

      @Override
      public GtfsRoute from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsRoute input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new RouteExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(RouteExporter.class.getName(), factory);
   }
}
