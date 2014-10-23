package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripByService extends TripIndex
{

   public static final String KEY = FIELDS.service_id.name();

   public TripByService(String name) throws IOException
   {
      super(name, KEY, false);
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index<GtfsTrip> create(String name) throws IOException
      {
         return new TripByService(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(TripByService.class.getName(), factory);
   }
}
