package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;

public class CalendarByService extends IndexImpl<GtfsCalendar>
{

   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarByService(String name) throws IOException
   {
      super(name, KEY, false);
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

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index create(String name) throws IOException
      {
         return new CalendarByService(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(CalendarByService.class.getName(), factory);
   }

}
