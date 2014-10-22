package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;

public class AgencyImporter extends ImporterImpl<GtfsAgency>
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang;
   };

   public static final String FILENAME = "agency.txt";
   public static final String KEY = FIELDS.agency_id.name();

   public AgencyImporter(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected GtfsAgency build(GtfsIterator _reader, int id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsAgency bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new AgencyImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(AgencyImporter.class.getName(), factory);
   }

}
