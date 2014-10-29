package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;

public class AgencyById extends IndexImpl<GtfsAgency> implements GtfsConverter
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang, agency_fare_url;
   };

   public static final String FILENAME = "agency.txt";
   public static final String KEY = FIELDS.agency_id.name();

   private GtfsAgency bean = new GtfsAgency();
   private String[] array = new String[FIELDS.values().length];

   public AgencyById(String name) throws IOException
   {
      super(name, KEY, "default", true);
   }

   @Override
   protected GtfsAgency build(GtfsIterator reader, Context context)
   {
      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         array[i++] = getField(reader, field.name());
      }

      i = 0;
      int id = (int) context.get(Context.ID);
      bean.setId(id);
      bean.setAgencyId(STRING_CONVERTER.from(context, FIELDS.agency_id,
            array[i++], "default", false));
      bean.setAgencyName(STRING_CONVERTER.from(context, FIELDS.agency_name,
            array[i++], true));
      bean.setAgencyUrl(URL_CONVERTER.from(context, FIELDS.agency_url,
            array[i++], true));
      bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(context,
            FIELDS.agency_timezone, array[i++], true));
      bean.setAgencyPhone(STRING_CONVERTER.from(context, FIELDS.agency_phone,
            array[i++], false));
      bean.setAgencyLang(STRING_CONVERTER.from(context, FIELDS.agency_lang,
            array[i++], false));
      bean.setAgencyFareUrl(URL_CONVERTER.from(context, FIELDS.agency_fare_url,
            array[i++], false));

      return bean;
   }

   @Override
   public boolean validate(GtfsAgency bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends IndexFactory
   {
      @Override
      protected Index create(String name) throws IOException
      {
         return new AgencyById(name);
      }
   }

   static
   {
      IndexFactory factory = new DefaultImporterFactory();
      IndexFactory.factories.put(AgencyById.class.getName(), factory);
   }

}
