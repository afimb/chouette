package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class AgencyMarshaller extends MarshallerImpl<GtfsAgency> implements
      GtfsConverter
{

   public static enum FIELDS
   {
      agency_id, agency_name, agency_url, agency_timezone, agency_phone, agency_lang;
   };

   public static final String FILENAME = "agency.txt";

   public AgencyMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsAgency bean) throws IOException
   {
      marshal(AGENCY_CONVERTER.to(bean));
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

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new AgencyMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories
            .put(AgencyMarshaller.class.getName(), factory);
   }

}