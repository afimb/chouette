package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.CalendarByService.FIELDS;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;

public class CalendarDateByService extends IndexImpl<GtfsCalendarDate>
      implements GtfsConverter
{

   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";
   public static final String KEY = FIELDS.service_id.name();

   private GtfsCalendarDate bean = new GtfsCalendarDate();
   private String[] array = new String[FIELDS.values().length];

   public CalendarDateByService(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsCalendarDate build(GtfsIterator reader, int id)
   {
      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         array[i++] = getField(reader, field.name());
      }

      i = 0;
      bean.setId(id);
      bean.setServiceId(STRING_CONVERTER.from(array[i++], true));
      bean.setDate(DATE_CONVERTER.from(array[i++], true));
      bean.setExceptionType(EXCEPTIONTYPE_CONVERTER.from(array[i++], true));

      return bean;
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
      IndexFactory.factories
            .put(CalendarDateByService.class.getName(), factory);
   }

}
