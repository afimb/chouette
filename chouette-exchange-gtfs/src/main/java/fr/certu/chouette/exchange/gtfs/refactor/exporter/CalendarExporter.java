package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;

public class CalendarExporter extends ExporterImpl<GtfsCalendar> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";

   public CalendarExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsCalendar bean) throws IOException
   {
      export(CALENDAR_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsCalendar> CALENDAR_CONVERTER = new Converter<String, GtfsCalendar>()
   {

      @Override
      public GtfsCalendar from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsCalendar input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new CalendarExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(CalendarExporter.class.getName(), factory);
   }

}
