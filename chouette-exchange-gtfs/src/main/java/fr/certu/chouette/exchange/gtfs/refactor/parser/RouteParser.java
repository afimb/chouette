package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;

public class RouteParser extends ParserImpl<GtfsRoute>
{

   public static enum FIELDS
   {
      route_id, agency_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
   };

   public static final String FILENAME = "routes.txt";
   public static final String KEY = FIELDS.route_id.name();

   public RouteParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsRoute build(GtfsReader _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new RouteParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(RouteParser.class.getName(), factory);
   }

}
