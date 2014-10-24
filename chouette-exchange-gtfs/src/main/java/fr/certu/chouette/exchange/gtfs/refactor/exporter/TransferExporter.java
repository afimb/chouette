package fr.certu.chouette.exchange.gtfs.refactor.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
   public void writeHeader() throws IOException
   {
      write(FIELDS.values());
   }

   @Override
   public void export(GtfsTransfer bean) throws IOException
   {
      write(TRANSFER_CONVERTER.to(bean));
   }

   public static Converter<String, GtfsTransfer> TRANSFER_CONVERTER = new Converter<String, GtfsTransfer>()
   {

      @Override
      public GtfsTransfer from(String input)
      {
         GtfsTransfer bean = new GtfsTransfer();
         List<String> values = Tokenizer.tokenize(input);

         int i = 0;
         bean.setFromStopId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setToStopId(STRING_CONVERTER.from(values.get(i++), true));
         bean.setTransferType(TRANSFERTYPE_CONVERTER.from(values.get(i++), true));
         bean.setMinTransferTime(INTEGER_CONVERTER.from(values.get(i++), false));

         return bean;
      }

      @Override
      public String to(GtfsTransfer input)
      {
         String result = null;
         List<String> values = new ArrayList<String>();
         values.add(STRING_CONVERTER.to(input.getFromStopId()));
         values.add(STRING_CONVERTER.to(input.getToStopId()));
         values.add(TRANSFERTYPE_CONVERTER.to(input.getTransferType()));
         values.add(INTEGER_CONVERTER.to(input.getMinTransferTime()));

         result = Tokenizer.untokenize(values);
         return result;
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
