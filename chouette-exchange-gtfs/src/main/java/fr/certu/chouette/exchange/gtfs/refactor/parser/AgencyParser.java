package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;

public class AgencyParser extends ParserImpl<GtfsAgency>
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang;
   };

   public static final String FILENAME = "agency.txt";
   public static final String KEY = FIELDS.agency_id.name();

   public AgencyParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsAgency build(GtfsReader _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsAgency bean, GtfsDao dao)
   {
      return true;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new AgencyParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(AgencyParser.class.getName(), factory);
   }

}
