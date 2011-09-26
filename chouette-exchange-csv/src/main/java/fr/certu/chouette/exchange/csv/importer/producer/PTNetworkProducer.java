package fr.certu.chouette.exchange.csv.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.model.neptune.PTNetwork;

public class PTNetworkProducer extends AbstractModelProducer<PTNetwork>
{

   private static final Logger logger               = Logger.getLogger(PTNetworkProducer.class);
   public static final String  PTNETWORK_NAME_TITLE = "Nom du réseau";
   public static final String  CODE_TITLE           = "Code Réseau";
   public static final String  DESCRIPTION_TITLE    = "Description du réseau";

   @Override
   public PTNetwork produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix) throws ExchangeException
   {
      PTNetwork ptNetwork = new PTNetwork();
      if (firstLine[TITLE_COLUMN].equals(PTNETWORK_NAME_TITLE))
      {
         ptNetwork.setName(firstLine[TITLE_COLUMN + 1]);
      }
      else
      {
         return null;
      }
      try
      {
         ptNetwork.setRegistrationNumber(loadStringParam(csvReader, CODE_TITLE));
         ptNetwork.setDescription(loadStringParam(csvReader, DESCRIPTION_TITLE));
         ptNetwork.setObjectId(objectIdPrefix + ":" + PTNetwork.PTNETWORK_KEY + ":" + ptNetwork.getName());
      }
      catch (ExchangeException e)
      {
         logger.error("CSV reading failed", e);
         throw new ExchangeException(ExchangeExceptionCode.INVALID_CSV_FILE, e.getMessage());
      }
      return ptNetwork;
   }

}
