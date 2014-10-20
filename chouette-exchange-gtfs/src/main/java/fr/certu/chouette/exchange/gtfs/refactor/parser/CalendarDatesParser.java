package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.CalendarDate;

public class CalendarDatesParser extends ParserImpl<CalendarDate>
{

   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarDatesParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected CalendarDate build(GtfsReader _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new CalendarDatesParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(CalendarDatesParser.class.getName(), factory);
   }

}
