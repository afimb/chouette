package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;

public class CalendarDateParser extends ParserImpl<GtfsCalendarDate>
{

   public static enum FIELDS
   {
      service_id, date, exception_type;
   };

   public static final String FILENAME = "calendar_dates.txt";
   public static final String KEY = FIELDS.service_id.name();

   public CalendarDateParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsCalendarDate build(GtfsReader _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new CalendarDateParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(CalendarDateParser.class.getName(), factory);
   }

}
