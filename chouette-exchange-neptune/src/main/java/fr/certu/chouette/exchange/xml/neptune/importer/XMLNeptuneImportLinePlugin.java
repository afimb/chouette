/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.ValidationException;

import chouette.schema.ChouettePTNetworkTypeType;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeException;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeExceptionCode;
import fr.certu.chouette.exchange.xml.neptune.exception.ExchangeRuntimeException;
import fr.certu.chouette.exchange.xml.neptune.report.NeptuneReportItem;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.DetailReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.report.SheetReportItem;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;

/**
 * Import Plugin for Neptune Line format
 * <p/>
 * accept XML or ZIP format<br/>
 * for ZIP format, will return separate instances of shared objects , one in
 * each Line hierarchy
 * 
 */
public class XMLNeptuneImportLinePlugin implements IImportPlugin<Line>
{
   private static final Logger logger            = Logger.getLogger(XMLNeptuneImportLinePlugin.class);
   /**
    * transcoder from CASTOR format to CHOUETTE internal format
    */
   @Getter
   @Setter
   private NeptuneConverter    converter;
   /**
    * API description for caller
    */
   private FormatDescription   description;
   /**
    * list of allowed file extensions
    */
   private List<String>        allowedExtensions = Arrays.asList(new String[] { "xml", "zip" });
   /**
    * warning and error reporting container
    */
   private ReportItem          sheet1_1;
   /**
    * file format reporting
    */
   private SheetReportItem     report1_1;
   /**
    * data format reporting
    */
   private SheetReportItem     report1_2;

   /**
    * Constructor
    */
   public XMLNeptuneImportLinePlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("NEPTUNE");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      ParameterDescription param1 = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false, true);
      param1.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
      params.add(param1);
      ParameterDescription param2 = new ParameterDescription("fileFormat", ParameterDescription.TYPE.STRING, false,
            "file extension");
      param2.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
      params.add(param2);
      ParameterDescription param3 = new ParameterDescription("validate", ParameterDescription.TYPE.BOOLEAN, false,
            "false");
      params.add(param3);
      description.setParameterDescriptions(params);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.exchange.IImportPlugin#doImport(java.util.List,
    * fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public List<Line> doImport(List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
   {
      ValidationClassReportItem category1 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.ONE);
      sheet1_1 = new SheetReportItem("Test1_Sheet1", 1);
      report1_1 = new SheetReportItem("Test1_Sheet1_Step1", 1);
      report1_2 = new SheetReportItem("Test1_Sheet1_Step2", 2);
      sheet1_1.addItem(report1_1);
      sheet1_1.addItem(report1_2);
      category1.addItem(sheet1_1);
      reportContainer.setReport(category1);

      String filePath = null;
      boolean validate = false;
      String extension = "file extension";
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equals("inputFile"))
            {
               filePath = svalue.getFilepathValue();
            }
            else if (svalue.getName().equals("fileFormat"))
            {
               extension = svalue.getStringValue().toLowerCase();
            }
            else if (svalue.getName().equals("validate"))
            {
               validate = svalue.getBooleanValue().booleanValue();
            }
            else
            {
               throw new IllegalArgumentException("unexpected argument " + svalue.getName());
            }
         }
         else
         {
            throw new IllegalArgumentException("unexpected argument " + value.getName());
         }
      }
      if (filePath == null)
      {
         logger.error("missing argument inputFile");
         throw new IllegalArgumentException("inputFile required");
      }

      if (extension.equals("file extension"))
      {
         extension = FilenameUtils.getExtension(filePath).toLowerCase();
      }
      if (!allowedExtensions.contains(extension))
      {
         logger.error("invalid argument inputFile " + filePath + ", allowed format : "
               + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      /*
       * Report report = new NeptuneReport(NeptuneReport.KEY.IMPORT);
       * report.setStatus(Report.STATE.OK); reportContainer.setReport(report);
       */

      List<Line> lines = null;

      if (extension.equals("xml"))
      {
         // simple file processing
         logger.info("start import simple file " + filePath);
         Line line = processFileImport(filePath, validate, category1);
         if (line != null)
         {
            lines = new ArrayList<Line>();
            lines.add(line);
         }
      }
      else
      {
         // zip file processing
         logger.info("start import zip file " + filePath);
         lines = processZipImport(filePath, validate, category1);
      }
      logger.info("import terminated");
      return lines;
   }

   /**
    * import ZipFile
    * 
    * @param filePath
    *           path to zip File
    * @param validate
    *           process XML and XSD format validation
    * @param report
    *           report to fill
    * @return list of loaded lines
    */
   private List<Line> processZipImport(String filePath, boolean validate, Report report)
   {
      NeptuneFileReader reader = new NeptuneFileReader();
      ZipFile zip = null;
      try
      {
         zip = new ZipFile(filePath);
      }
      catch (IOException e)
      {
         ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL, filePath);
         report1_1.addItem(detailReportItem);
         logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
         return null;
      }
      List<Line> lines = new ArrayList<Line>();
      boolean ofType1 = false;
      boolean ofType2 = false;
      boolean someOk = false;
      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
      {
         ZipEntry entry = entries.nextElement();

         // ignore directory without warning
         if (entry.isDirectory())
            continue;

         String entryName = entry.getName();
         if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml"))
         {
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_warning", Report.STATE.WARNING,
                  entryName);
            report1_1.addItem(detailReportItem);
            logger.info("zip entry " + entryName + " bypassed ; not a XML file");
            continue;
         }
         logger.info("start import zip entry " + entryName);
         InputStream stream = null;
         try
         {
            stream = zip.getInputStream(entry);
         }
         catch (IOException e)
         {
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
                  entryName);
            report1_2.addItem(detailReportItem);
            logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
            continue;
         }
         ChouettePTNetworkTypeType rootObject = null;
         try
         {
            rootObject = reader.read(stream, entryName, validate);
            someOk = true;
            report1_1.updateStatus(Report.STATE.OK);
            report1_2.updateStatus(Report.STATE.OK);
         }
         catch (ExchangeRuntimeException e)
         {
            if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode()))
            {
               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
                     entryName);
               report1_1.addItem(detailReportItem);
               report1_1.computeDetailItemCount();
               ofType1 = true;
            }

            if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
            {
               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
                     entryName);
               report1_2.addItem(detailReportItem);
               ofType2 = true;
            }
            if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
            {
               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
                     entryName);
               report1_1.addItem(detailReportItem);
               ofType1 = true;
            }
            logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
            continue;
         }
         catch (Exception e)
         {
            logger.error(e.getLocalizedMessage());
            continue;
         }
         try
         {
            Line line = processImport(rootObject, validate, report, entryName);

            if (line != null)
            {
               lines.add(line);
               report1_1.updateStatus(Report.STATE.OK);
            }
            else
            {
               logger.error("zip entry " + entryName + " import failed (build model)");
            }

         }
         catch (ExchangeException e)
         {
            report1_1.updateStatus(Report.STATE.ERROR);
            logger.error("zip entry " + entryName + " import failed (convert to model)" + e.getLocalizedMessage());
            continue;
         }
         logger.info("zip entry imported");
      }
      if (!ofType1 && ofType2)
      {
         report1_1.updateStatus(Report.STATE.OK);
      }
      else if (ofType1 && !ofType2 && !someOk)
      {
         report1_2.updateStatus(Report.STATE.UNCHECK);
      }

      report1_1.computeDetailItemCount();
      report1_2.computeDetailItemCount();
      if (lines.size() == 0)
      {
         logger.error("zip import failed (no valid entry)");
         return null;
      }
      else
      {
         report1_1.updateStatus(Report.STATE.OK);
      }
      return lines;
   }

   /**
    * import simple Neptune file
    * 
    * @param filePath
    *           path to File
    * @param validate
    *           process XML and XSD format validation
    * @param report
    *           report to fill
    * @return loaded line
    * @throws ExchangeException
    */
   private Line processFileImport(String filePath, boolean validate, Report report) throws ExchangeException
   {
      ChouettePTNetworkTypeType rootObject = null;
      NeptuneFileReader reader = new NeptuneFileReader();
      try
      {
         rootObject = reader.read(filePath, validate);
         report1_1.updateStatus(Report.STATE.OK);
         report1_2.updateStatus(Report.STATE.OK);
      }
      catch (ExchangeRuntimeException e)
      {
         if (e.getCode().equals(ExchangeExceptionCode.INVALID_XML_FILE.name()))
         {
            logger.error("INVALID_XML_FILE " + filePath);
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
            report1_1.addItem(detailReportItem);
            report1_1.computeDetailItemCount();
            report1_2.updateStatus(Report.STATE.UNCHECK);
         }
         if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
         {
            logger.error("INVALID_NEPTUNE_FILE " + filePath);
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR, filePath);
            report1_2.addItem(detailReportItem);
            report1_1.updateStatus(Report.STATE.OK);
         }
         if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
         {
            logger.error("FILE_NOT_FOUND " + filePath);
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
            report1_1.addItem(detailReportItem);
            report1_2.updateStatus(Report.STATE.UNCHECK);
         }
         logger.error("File " + filePath + " import failed (read XML) [" + e.getLocalizedMessage() + "]");
         return null;
      }
      catch (Exception e)
      {
         /*
          * ReportItem item = new
          * NeptuneReportItem(NeptuneReportItem.KEY.FILE_ERROR
          * ,Report.STATE.ERROR,filePath,e.getLocalizedMessage());
          * report.addItem(item); report.setStatus(Report.STATE.FATAL);
          */
         // ReportItem detailReportItem = new
         // DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL,
         // filePath);
         // report1_1.addItem(detailReportItem);
         // report1_1.computeDetailItemCount();
         // logger.error("import failed ((read XML)) " +
         // e.getLocalizedMessage());
         logger.error(e.getLocalizedMessage());
         return null;
      }
      Line line = processImport(rootObject, validate, report, filePath);
      if (line == null)
      {
         logger.error("import failed (build model)");
         // report.setStatus(Report.STATE.FATAL);
         // report1_2.updateStatus(Report.STATE.FATAL);
      }
      else
      {
         report1_1.updateStatus(Report.STATE.OK);
      }
      return line;
   }

   /**
    * process conversion between CASTOR format and CHOUETTE internal format
    * 
    * @param rootObject
    *           container for CASTOR loaded XML file
    * @param validate
    *           validate on XSD rules
    * @param report
    *           report to fill
    * @param entryName
    *           file name for logger purpose
    * @return builded line
    * @throws ExchangeException
    */
   private Line processImport(ChouettePTNetworkTypeType rootObject, boolean validate, Report report, String entryName)
         throws ExchangeException
   {
      if (validate)
      {
         try
         {
            rootObject.validate();
         }
         catch (ValidationException e)
         {
            logger.error("import failed for " + entryName + " : Castor validation");
            /*
             * ReportItem item = new
             * NeptuneReportItem(NeptuneReportItem.KEY.VALIDATION_ERROR
             * ,Report.STATE.ERROR,entryName); report.addItem(item);
             */
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
                  entryName);
            report1_2.addItem(detailReportItem);
            Throwable t = e;
            while (t != null)
            {
               /*
                * ReportItem subItem = new
                * NeptuneReportItem(NeptuneReportItem.KEY
                * .VALIDATION_CAUSE,Report.STATE.ERROR,t.getLocalizedMessage());
                * item.addItem(subItem);
                */
               logger.error(t.getLocalizedMessage());
               // ReportItem detail2 = new
               // DetailReportItem("",Report.STATE.ERROR,
               // t.getLocalizedMessage());
               // report1_2.addItem(detail2);
               t = t.getCause();
            }
            return null;
         }
      }
      report1_2.computeDetailItemCount();
      ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.OK_LINE, Report.STATE.OK, entryName, "");
      report1_2.updateStatus(Report.STATE.OK);

      ModelAssembler modelAssembler = new ModelAssembler();

      Line line = converter.extractLine(rootObject, item);
      modelAssembler.setLine(line);
      modelAssembler.setRoutes(converter.extractRoutes(rootObject, item));
      modelAssembler.setCompanies(converter.extractCompanies(rootObject, item));
      modelAssembler.setPtNetwork(converter.extractPTNetwork(rootObject, item));
      modelAssembler.setJourneyPatterns(converter.extractJourneyPatterns(rootObject, item));
      modelAssembler.setPtLinks(converter.extractPTLinks(rootObject, item));
      modelAssembler.setVehicleJourneys(converter.extractVehicleJourneys(rootObject, item));
      modelAssembler.setStopPoints(converter.extractStopPoints(rootObject, item));
      modelAssembler.setStopAreas(converter.extractStopAreas(rootObject, item));
      modelAssembler.setAreaCentroids(converter.extractAreaCentroids(rootObject, item));
      modelAssembler.setConnectionLinks(converter.extractConnectionLinks(rootObject, item));
      modelAssembler.setTimetables(converter.extractTimetables(rootObject, item));

      modelAssembler.setAccessLinks(converter.extractAccessLinks(rootObject, item));
      modelAssembler.setAccessPoints(converter.extractAccessPoints(rootObject, item));
      modelAssembler.setGroupOfLines(converter.extractGroupOfLines(rootObject, item));
      modelAssembler.setFacilities(converter.extractFacilities(rootObject, item));
      modelAssembler.setTimeSlots(converter.extractTimeSlots(rootObject, item));
      modelAssembler.setRestrictionConstraints(converter.extractRestrictionConstraints(rootObject, item));
      modelAssembler.connect();

      ReportItem item2 = new NeptuneReportItem(NeptuneReportItem.KEY.OK_LINE, Report.STATE.OK, entryName,
            line.getName());
      item2.addAll(item.getItems());
      // report.addItem(item2);

      rootObject.toString();

      return line;
   }
}
