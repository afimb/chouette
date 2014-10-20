package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;

public class TripParser extends ParserImpl<GtfsTrip>
{

   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, shape_id;
   };

   public static final String FILENAME = "trips.txt";
   public static final String KEY = FIELDS.route_id.name();

   public TripParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsTrip build(GtfsReader reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsTrip bean, GtfsDao dao)
   {
      return true;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new TripParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(TripParser.class.getName(), factory);
   }
}
