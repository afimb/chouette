package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.StopTimeByTrip.FIELDS;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.DropOffType;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime.PickupType;

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
      super(name, KEY);
   }

   @Override
   protected GtfsAgency build(GtfsIterator reader, int id)
   {
      int i = 0;
      for (FIELDS field : FIELDS.values())
      {
         array[i++] = getField(reader, field.name());
      }

      i = 0;
      bean.setId(id);
      bean.setAgencyId(STRING_CONVERTER.from(array[i++], false));
      bean.setAgencyName(STRING_CONVERTER.from(array[i++], true));
      bean.setAgencyUrl(URL_CONVERTER.from(array[i++], true));
      bean.setAgencyTimezone(TIMEZONE_CONVERTER.from(array[i++], true));
      bean.setAgencyPhone(STRING_CONVERTER.from(array[i++], false));
      bean.setAgencyLang(STRING_CONVERTER.from(array[i++], false));
      bean.setAgencyFareUrl(URL_CONVERTER.from(array[i++], false));

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
