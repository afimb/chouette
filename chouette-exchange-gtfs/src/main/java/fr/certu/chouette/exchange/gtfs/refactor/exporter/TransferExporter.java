package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsConverter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;

public class TransferExporter extends ExporterImpl<GtfsTransfer> implements
      GtfsConverter
{
   public static enum FIELDS
   {
      from_stop_id, to_stop_id, transfer_type, min_transfer_time;
   };

   public static final String FILENAME = "transfers.txt";

   public TransferExporter(String name) throws IOException
   {
      super(name);
   }

   @Override
   public void export(GtfsTransfer bean) throws IOException
   {
      export(TRANSFER_CONVERTER.to(bean));
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

   public static class DefaultExporterFactory extends ExporterFactory
   {

      @Override
      protected Exporter create(String path) throws IOException
      {
         return new TransferExporter(path);
      }
   }

   static
   {
      ExporterFactory factory = new DefaultExporterFactory();
      ExporterFactory.factories.put(TransferExporter.class.getName(), factory);
   }

}
