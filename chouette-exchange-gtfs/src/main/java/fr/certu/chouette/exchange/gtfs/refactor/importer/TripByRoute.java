package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripByRoute extends IndexImpl<GtfsTrip>
{

   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, shape_id;
   };

   public static final String FILENAME = "trips.txt";
   public static final String KEY = FIELDS.route_id.name();

   private GtfsTrip bean = new GtfsTrip();

   public TripByRoute(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsTrip build(GtfsIterator reader, int id)
   {
      return bean;
   }

   @Override
   public boolean validate(GtfsTrip bean, GtfsImporter dao)
   {
      return true;
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
