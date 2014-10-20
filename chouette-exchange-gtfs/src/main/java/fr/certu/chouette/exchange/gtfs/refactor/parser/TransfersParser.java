package fr.certu.chouette.exchange.gtfs.refactor.parser;

import java.io.IOException;

import fr.certu.chouette.exchange.gtfs.refactor.model.Transfer;

public class TransfersParser extends ParserImpl<Transfer>
{

   public static enum FIELDS
   {
      from_stop_id, to_stop_id, transfer_type, min_transfer_time;
   };

   public static final String FILENAME = "transfers.txt";
   public static final String KEY = FIELDS.from_stop_id.name();

   public TransfersParser(String name) throws IOException
   {
      super(name, KEY);
   }

   @Override
   protected Transfer build(GtfsReader _reader, int line)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public static class DefaultParserFactory extends ParserFactory
   {
      @Override
      protected GtfsParser create(String name) throws IOException
      {
         return new TransfersParser(name);
      }
   }

   static
   {
      ParserFactory factory = new DefaultParserFactory();
      ParserFactory.factories.put(TransfersParser.class.getName(), factory);
   }

}
