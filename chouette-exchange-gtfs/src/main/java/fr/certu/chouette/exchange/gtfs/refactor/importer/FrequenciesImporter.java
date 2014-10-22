package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;

public class FrequenciesImporter extends ImporterImpl<GtfsFrequency>
{

   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";
   public static final String KEY = FIELDS.trip_id.name();

   public FrequenciesImporter(String name) throws IOException
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

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new FrequenciesImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(FrequenciesImporter.class.getName(), factory);
   }

}
