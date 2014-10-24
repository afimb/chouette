package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;

public class CalendarDateExporter extends ExporterImpl<GtfsCalendarDate>
      implements GtfsConverter
{
   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";

   public CalendarDateExporter(String name) throws IOException
   {
      super(name);
   }
   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }
   @Override
   public void export(GtfsCalendarDate bean) throws IOException
   {
      write(CALENDARDATE_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsCalendarDate> CALENDARDATE_CONVERTER = new Converter<String, GtfsCalendarDate>()
   {

      @Override
      public GtfsCalendarDate from(String input)
      {
         GtfsCalendarDate bean = new GtfsCalendarDate();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setServiceId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setDate(DATE_CONVERTER.from(values.get(i++), true));
         bean.setExceptionType(EXCEPTIONTYPE_CONVERTER.from(values.get(i++),
               true));

         return bean;
      }

      @Override
      public String to(GtfsCalendarDate input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getServiceId()));
         values.add(DATE_CONVERTER.to(input.getDate()));
         values.add(EXCEPTIONTYPE_CONVERTER.to(input.getExceptionType()));

         result = Tokenizer.untokenize(values);
         return result;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new CalendarDateExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(CalendarDateExporter.class.getName(),
            factory);
   }

}
