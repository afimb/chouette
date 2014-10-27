package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsFrequency bean) throws IOException
   {
      write(CONVERTER.to(bean));
   }

   public static Converter<String, GtfsFrequency> CONVERTER = new Converter<String, GtfsFrequency>()
   {

      @Override
      public GtfsFrequency from(String input)
      {
         GtfsFrequency bean = new GtfsFrequency();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setTripId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setStartTime(GTFSTIME_CONVERTER.from(values.get(i++), true));
         bean.setEndTime(GTFSTIME_CONVERTER.from(values.get(i++), true));
         bean.setHeadwaySecs(INTEGER_CONVERTER.from(values.get(i++), true));
         bean.setExactTimes(BOOLEAN_CONVERTER.from(values.get(i++), false,
               false));

         return bean;
      }

      @Override
      public String to(GtfsFrequency input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getTripId()));
         values.add(GTFSTIME_CONVERTER.to(input.getStartTime()));
         values.add(GTFSTIME_CONVERTER.to(input.getEndTime()));
         values.add(INTEGER_CONVERTER.to(input.getHeadwaySecs()));
         values.add(BOOLEAN_CONVERTER.to(input.getExactTimes()));

         result = Tokenizer.untokenize(values);
         return result;
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
