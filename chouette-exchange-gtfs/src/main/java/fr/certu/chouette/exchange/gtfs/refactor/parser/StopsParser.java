package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.Stop;

public class StopsParser extends ParserImpl<Stop>
{

   public static enum FIELDS
   {
      stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding;
   };

   public static final String FILENAME = "stops.txt";
   public static final String KEY = FIELDS.stop_id.name();

   public StopsParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected Stop build(GtfsReader _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new StopsParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(StopsParser.class.getName(), factory);
   }
}
