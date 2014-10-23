package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;

public class CalendarDateByService extends IndexImpl<GtfsCalendarDate>
{

   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarDateByService(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsCalendarDate build(GtfsIterator _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsCalendarDate bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index create(String name) throws IOException
      {
         return new CalendarDateByService(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(CalendarDateByService.class.getName(),
            factory);
   }

}
