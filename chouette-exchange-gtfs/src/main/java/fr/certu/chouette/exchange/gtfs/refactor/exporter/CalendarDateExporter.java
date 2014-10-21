package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

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
   public void export(GtfsCalendarDate bean) throws IOException
   {
      export(CALENDARDATE_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsCalendarDate> CALENDARDATE_CONVERTER = new Converter<String, GtfsCalendarDate>()
   {

      @Override
      public GtfsCalendarDate from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsCalendarDate input)
      {

         return null;
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
