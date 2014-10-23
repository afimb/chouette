package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripByRoute extends TripIndex
{

   public static final String KEY = FIELDS.route_id.name();

   public TripByRoute(String name) throws IOException
   {
      super(name, KEY, false);
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index<GtfsTrip> create(String name) throws IOException
      {
         return new TripByRoute(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(TripByRoute.class.getName(), factory);
   }
}
