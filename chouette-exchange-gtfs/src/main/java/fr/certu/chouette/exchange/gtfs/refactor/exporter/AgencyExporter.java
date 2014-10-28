package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;

public class AgencyExporter extends ExporterImpl<GtfsAgency> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang, agency_fare_url;
   };

   public static final String FILENAME = "agency.txt";

   public AgencyExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());

   }

   @Override
   public void export(GtfsAgency bean) throws IOException
   {
      write(CONVERTER.to(bean));
   }

   public static Converter<String, GtfsAgency> CONVERTER = new Converter<String, GtfsAgency>()
   {

      @Override
      public GtfsAgency from(String input)
      {
         GtfsAgency bean = new GtfsAgency();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setAgencyId(STRING_CONVERTER.from(values.get(i++), false));
         bean.setAgencyName(STRING_CONVERTER.from(values.get(i++), true));
         bean.setAgencyUrl(URL_CONVERTER.from(values.get(i++), true));
         bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(values.get(i++), true));
         bean.setAgencyPhone(STRING_CONVERTER.from(values.get(i++), false));
         bean.setAgencyLang(STRING_CONVERTER.from(values.get(i++), false));
         bean.setAgencyFareUrl(URL_CONVERTER.from(values.get(i++), false));

         return bean;
      }

      @Override
      public String to(GtfsAgency input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getAgencyId()));
         values.add(STRING_CONVERTER.to(input.getAgencyName()));
         values.add(URL_CONVERTER.to(input.getAgencyUrl()));
         values.add(TIMEZONE_CONVERTER.to(input.getAgencyTimezone()));
         values.add(STRING_CONVERTER.to(input.getAgencyPhone()));
         values.add(STRING_CONVERTER.to(input.getAgencyLang()));
         values.add(URL_CONVERTER.to(input.getAgencyFareUrl()));

         result = Tokenizer.untokenize(values);
         return result;
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