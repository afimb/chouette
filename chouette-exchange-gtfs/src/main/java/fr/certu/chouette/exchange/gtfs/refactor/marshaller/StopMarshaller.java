package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class StopMarshaller extends MarshallerImpl<GtfsStop> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type, parent_station, stop_timezone, wheelchair_boarding;
   };

   public static final String FILENAME = "stops.txt";

   public StopMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsStop bean) throws IOException
   {
      marshal(Stop_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsStop> Stop_CONVERTER = new Converter<String, GtfsStop>()
   {

      @Override
      public GtfsStop from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsStop input)
      {

         return null;
      }

   };

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new StopMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(StopMarshaller.class.getName(), factory);
   }
}
