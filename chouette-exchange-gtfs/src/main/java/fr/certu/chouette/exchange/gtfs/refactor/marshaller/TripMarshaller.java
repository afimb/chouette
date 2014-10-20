package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class TripMarshaller extends MarshallerImpl<GtfsTrip> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, shape_id;
   };

   public static final String FILENAME = "trips.txt";

   public TripMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsTrip bean) throws IOException
   {
      marshal(Trip_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsTrip> Trip_CONVERTER = new Converter<String, GtfsTrip>()
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

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new TripMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(TripMarshaller.class.getName(), factory);
   }

}
