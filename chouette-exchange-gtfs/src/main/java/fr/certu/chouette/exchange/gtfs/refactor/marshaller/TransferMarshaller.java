package fr.certu.chouette.exchange.gtfs.refactor.marshaller;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.parser.GtfsConverter;

public class TransferMarshaller extends MarshallerImpl<GtfsTransfer> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      from_stop_id, to_stop_id, transfer_type, min_transfer_time;
   };

   public static final String FILENAME = "transfers.txt";

   public TransferMarshaller(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void marshal(GtfsTransfer bean) throws IOException
   {
      marshal(TRANSFER_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsTransfer> TRANSFER_CONVERTER = new Converter<String, GtfsTransfer>()
   {

      @Override
      public GtfsTransfer from(String input)
      {

         return null;
      }

      @Override
      public String to(GtfsTransfer input)
      {

         return null;
      }

   };

   public static class DefaultMarshallerFactory extends MarshallerFactory
   {

      @Override
      protected Marshaller create(String path) throws IOException
      {
         return new TransferMarshaller(path);
      }
   }

   static
   {
      MarshallerFactory factory = new DefaultMarshallerFactory();
      MarshallerFactory.factories.put(TransferMarshaller.class.getName(),
            factory);
   }

}
