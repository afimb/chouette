package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;

public class AgencyExporter extends ExporterImpl<GtfsAgency> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang;
   };

   public static final String FILENAME = "agency.txt";

   public AgencyExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsAgency bean) throws IOException
   {
      export(AGENCY_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsAgency> AGENCY_CONVERTER = new Converter<String, GtfsAgency>()
   {

      @Override
      public GtfsAgency from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsAgency input)
      {

         return null;
      }

   };

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new AgencyExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(AgencyExporter.class.getName(), factory);
   }

}