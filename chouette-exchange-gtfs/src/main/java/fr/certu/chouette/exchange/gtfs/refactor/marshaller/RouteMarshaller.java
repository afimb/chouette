package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class RouteMarshaller extends MarshallerImpl<GtfsRoute> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, Route_id, route_short_name, route_long_name, route_desc, route_type, route_url, route_color, route_text_color;
   };

   public static final String FILENAME = "routes.txt";

   public RouteMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsRoute bean) throws IOException
   {
      marshal(Route_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsRoute> Route_CONVERTER = new Converter<String, GtfsRoute>()
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

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new RouteMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(RouteMarshaller.class.getName(), factory);
   }
}
