package fr.certu.chouette.exchange.csv.importer.producer;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.exchange.csv.importer.report.CSVReportItem;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.Report;

public class CompanyProducer extends AbstractModelProducer<Company>
{

   private static final Logger logger             = Logger.getLogger(CompanyProducer.class);
   public static final String  COMPANY_NAME_TITLE = "Nom de l'entreprise de transport";
   public static final String  CODE_TITLE         = "Code Transporteur";
   public static final String  SHORT_NAME_TITLE   = "Nom court";
   public static final String  DESCRIPTION_TITLE  = "Description du transporteur";
   public static final String  ZIPCODE_TITLE      = "Code postal";
   public static final String  PHONE_TITLE        = "Téléphone";
   public static final String  FAX_TITLE          = "Fax";
   public static final String  EMAIL_TITLE        = "Email";

   @Override
   public Company produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix, Report report)
         throws ExchangeException
   {
      Company company = new Company();
      if (firstLine[TITLE_COLUMN].equals(COMPANY_NAME_TITLE))
      {
         company.setName(firstLine[TITLE_COLUMN + 1]);
      }
      else
      {
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR,
               firstLine[TITLE_COLUMN] +"<>"+ COMPANY_NAME_TITLE);
         report.addItem(reportItem);
         return null;
      }
      try
      {
         company.setRegistrationNumber(loadStringParam(csvReader, CODE_TITLE));
         company.setShortName(loadStringParam(csvReader, SHORT_NAME_TITLE));
         company.setOrganisationalUnit(loadStringParam(csvReader, DESCRIPTION_TITLE));
         company.setCode(loadStringParam(csvReader, ZIPCODE_TITLE));
         company.setPhone(loadStringParam(csvReader, PHONE_TITLE));
         company.setFax(loadStringParam(csvReader, FAX_TITLE));
         company.setEmail(loadStringParam(csvReader, EMAIL_TITLE));
         company.setObjectId(objectIdPrefix + ":" + Company.COMPANY_KEY + ":" + toIdString(company.getShortName()));
         if (!NeptuneIdentifiedObject.checkObjectId(company.getObjectId()))
         {
            CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.BAD_ID, Report.STATE.ERROR, company.getName(), company.getObjectId());
            report.addItem(reportItem);
            return null;
         }
      }
      catch (ExchangeException e)
      {
         logger.error("CSV reading failed", e);
         CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.MANDATORY_TAG, Report.STATE.ERROR,
               e.getLocalizedMessage());
         report.addItem(reportItem);
         return null;
      }
      CSVReportItem reportItem = new CSVReportItem(CSVReportItem.KEY.OK_COMPANY, Report.STATE.OK, company.getName());
      report.addItem(reportItem);
      return company;
   }

}
