package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class CalendarDateMarshaller extends MarshallerImpl<GtfsCalendarDate>
      implements GtfsConverter
{
   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";

   public CalendarDateMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsCalendarDate bean) throws IOException
   {
      marshal(CALENDARDATE_CONVERTER.to(bean));
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

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected GtfsMarshaller create(String path) throws IOException
      {
         return new CalendarDateMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(CalendarDateMarshaller.class.getName(),
            factory);
   }

}
