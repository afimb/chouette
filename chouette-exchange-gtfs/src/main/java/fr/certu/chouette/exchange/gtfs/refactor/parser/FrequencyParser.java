package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;

public class FrequencyParser extends ParserImpl<GtfsFrequency>
{

   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";
   public static final String KEY = FIELDS.trip_id.name();

   public FrequencyParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsFrequency build(GtfsReader _reader, int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsFrequency bean, GtfsDao dao)
   {
      return true;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new FrequencyParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(FrequencyParser.class.getName(), factory);
   }

}
