package fr.certu.chouette.exchange.gtfs.refactor.importer;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;

public class TransfersImporter extends ImporterImpl<GtfsTransfer>
{

   public static enum FIELDS
   {
      from_stop_id, to_stop_id, transfer_type, min_transfer_time;
   };

   public static final String FILENAME = "transfers.txt";
   public static final String KEY = FIELDS.from_stop_id.name();

   public TransfersImporter(String name) throws IOException
   {
      super(name, KEY, false);
   }

   @Override
   protected GtfsTransfer build(GtfsIterator _reader, int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean validate(GtfsTransfer bean, GtfsImporter dao)
   {
      return true;
   }

   public static class DefaultImporterFactory extends ImporterFactory
   {
      @Override
      protected Importer create(String name) throws IOException
      {
         return new TransfersImporter(name);
      }
   }

   static
   {
      ImporterFactory factory = new DefaultImporterFactory();
      ImporterFactory.factories.put(TransfersImporter.class.getName(), factory);
   }

}
