package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;

public class RoutesImporter extends ImporterImpl<GtfsRoute>
{

   public static enum FIELDS
   {
      route_id, agency_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
   };

   public static final String FILENAME = "routes.txt";
   public static final String KEY = FIELDS.route_id.name();

   public RoutesImporter(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsRoute build(GtfsIterator _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsRoute bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new RoutesImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(RoutesImporter.class.getName(), factory);
   }

}
