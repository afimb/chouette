package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class StopTimeMarshaller extends MarshallerImpl<GtfsStopTime> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      trip_id, stop_id, stop_sequence, arrival_time, departure_time, stop_headsign, pickup_type, drop_off_type, shape_dist_traveled
   };

   public static final String FILENAME = "stop_times.txt";

   public StopTimeMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsStopTime bean) throws IOException
   {
      marshal(STOPTIME_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStopTime> STOPTIME_CONVERTER = new Converter<String, GtfsStopTime>()
   {

      @Override
      public GtfsStopTime from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsStopTime input)
      {

         return null;
      }

   };

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new StopTimeMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(StopTimeMarshaller.class.getName(),
            factory);
   }

}
