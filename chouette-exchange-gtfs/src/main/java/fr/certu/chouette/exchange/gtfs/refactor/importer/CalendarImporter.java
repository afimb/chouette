package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;

public class CalendarImporter extends ImporterImpl<GtfsCalendar>
{

   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarImporter(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsCalendar build(GtfsIterator reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsCalendar bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new CalendarImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(CalendarImporter.class.getName(), factory);
   }

}
