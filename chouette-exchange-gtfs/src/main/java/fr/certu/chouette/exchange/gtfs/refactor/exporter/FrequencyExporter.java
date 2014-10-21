package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;

public class FrequencyExporter extends ExporterImpl<GtfsFrequency> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      trip_id, start_time, end_time, headway_secs, exact_times;
   };

   public static final String FILENAME = "frequencies.txt";

   public FrequencyExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsFrequency bean) throws IOException
   {
      export(FREQUENCY_CONVERTER.to(bean));
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

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new FrequencyExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(FrequencyExporter.class.getName(), factory);
   }
}
