package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;

public class FrequencyByTrip extends IndexImpl<GtfsFrequency>
{

   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";
   public static final String KEY = FIELDS.trip_id.name();

   public FrequencyByTrip(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsFrequency build(GtfsIterator _reader, int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsFrequency bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index create(String name) throws IOException
      {
         return new FrequencyByTrip(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(FrequencyByTrip.class.getName(), factory);
   }

}
