package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class FrequencyMarshaller extends MarshallerImpl<GtfsFrequency>
      implements GtfsConverter
{
   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";

   public FrequencyMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsFrequency bean) throws IOException
   {
      marshal(FREQUENCY_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsFrequency> FREQUENCY_CONVERTER = new Converter<String, GtfsFrequency>()
   {

      @Override
      public GtfsFrequency from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsFrequency input)
      {

         return null;
      }

   };

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new FrequencyMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(FrequencyMarshaller.class.getName(),
            factory);
   }
}
