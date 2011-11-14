package fr.certu.chouette.exchange.csv.importer.producer;

import java.util.Calendar;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.exchange.csv.importer.report.CSVReportItem;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.report.Report;

public class PTNetworkProducer extends AbstractModelProducer<PTNetwork>
{

   private static final Logger logger               = Logger.getLogger(PTNetworkProducer.class);
   public static final String  PTNETWORK_NAME_TITLE = "Nom du réseau";
   public static final String  CODE_TITLE           = "Code Réseau";
   public static final String  DESCRIPTION_TITLE    = "Description du réseau";

   @Override
   public PTNetwork produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix, Report report) throws ExchangeException
   {
      PTNetwork ptNetwork = new PTNetwork();
      if (firstLine[TITLE_COLUMN].equals(PTNETWORK_NAME_TITLE))
      {
         ptNetwork.setName(firstLine[TITLE_COLUMN + 1]);
      }
      else
      {
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR,firstLine[TITLE_COLUMN]+"<>" + PTNETWORK_NAME_TITLE );
         report.addItem(reportItem);
         return null;
      }
      try
      {
         ptNetwork.setRegistrationNumber(loadStringParam(csvReader, CODE_TITLE));
         ptNetwork.setDescription(loadStringParam(csvReader, DESCRIPTION_TITLE));
         ptNetwork.setVersionDate(Calendar.getInstance().getTime());
         ptNetwork.setObjectId(objectIdPrefix + ":" + PTNetwork.PTNETWORK_KEY + ":" + ptNetwork.getName());
         if (!NeptuneIdentifiedObject.checkObjectId(ptNetwork.getObjectId()))
         {
            CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, ptNetwork.getName(), ptNetwork.getObjectId());
            report.addItem(reportItem);
            return null;
         }
      }
      catch (ExchangeException e)
      {
         logger.error("CSV reading failed", e);
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR, e.getLocalizedMessage());
         report.addItem(reportItem);
         return null;
      }
      CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.OK_PTNETWORK, Report.STATE.OK, ptNetwork.getName(),ptNetwork.getDescription());
      report.addItem(reportItem);
      return ptNetwork;
   }

}
