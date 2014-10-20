package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class CalendarMarshaller extends MarshallerImpl<GtfsCalendar> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      service_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday, start_date, end_date;
   };

   public static final String FILENAME = "calendar.txt";

   public CalendarMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsCalendar bean) throws IOException
   {
      marshal(Calendar_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsCalendar> Calendar_CONVERTER = new Converter<String, GtfsCalendar>()
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

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new CalendarMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(CalendarMarshaller.class.getName(),
            factory);
   }

}
