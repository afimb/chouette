/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
import org.trident.schema.trident.ChouettePTNetworkType;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.neptune.ChouettePTNetworkHolder;
import fr.certu.chouette.neptune.JaxbNeptuneFileConverter;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeException;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ValidationReport;

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
   private static final Logger logger = Logger
         .getLogger(XMLNeptuneImportLinePlugin.class);
   /**
    * transcoder from CASTOR format to CHOUETTE internal format
    */
   @Getter
   @Setter
   private NeptuneConverter converter;
   /**
    * API description for caller
    */
   private FormatDescription description;
   /**
    * list of allowed file extensions
    */
   private List<String> allowedExtensions = Arrays.asList(new String[] { "xml",
   "zip" });

   private JaxbNeptuneFileConverter reader;

   /**
    * Constructor
    */
   public XMLNeptuneImportLinePlugin() throws Exception
   {
      reader = new JaxbNeptuneFileConverter();
      description = new FormatDescription(this.getClass().getName());
      description.setName("NEPTUNE");
      description.setUnzipAllowed(true);
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("inputFile",
               ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(Arrays
               .asList(new String[] { "xml", "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("fileFormat",
               ParameterDescription.TYPE.STRING, false, "file extension");
         param.setAllowedExtensions(Arrays
               .asList(new String[] { "xml", "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("validate",
               ParameterDescription.TYPE.BOOLEAN, false, "true");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription(
               "optimizeMemory", ParameterDescription.TYPE.BOOLEAN, false,
               "true");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription(
               "sharedImportedData", ParameterDescription.TYPE.OBJECT, false,
               false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription(
               "unsharedImportedData", ParameterDescription.TYPE.OBJECT, false,
               false);
         params.add(param);
      }

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
   public List<Line> doImport(List<ParameterValue> parameters,
         ReportHolder importReport, ReportHolder validationReport)
               throws ChouetteException
               {

      String filePath = null;
      boolean validate = true;
      String extension = "file extension";
      boolean optimizeMemory = true;
      SharedImportedData sharedData = new SharedImportedData();
      UnsharedImportedData unsharedData = new UnsharedImportedData();

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
            } else if (svalue.getName().equalsIgnoreCase("fileFormat"))
            {
               extension = svalue.getStringValue().toLowerCase();
            } else if (svalue.getName().equalsIgnoreCase("validate"))
            {
               validate = svalue.getBooleanValue().booleanValue();
            } else if (svalue.getName().equalsIgnoreCase("optimizeMemory"))
            {
               optimizeMemory = svalue.getBooleanValue().booleanValue();
            } else if (svalue.getName().equalsIgnoreCase("sharedImportedData"))
            {
               Object obj = svalue.getObjectValue();
               if (obj instanceof SharedImportedData)
               {
                  sharedData = (SharedImportedData) obj;
               } else
               {
                  throw new IllegalArgumentException("unexpected argument type"
                        + svalue.getName() + " " + obj.getClass().getName());
               }
            } else if (svalue.getName()
                  .equalsIgnoreCase("unsharedImportedData"))
            {
               Object obj = svalue.getObjectValue();
               if (obj instanceof UnsharedImportedData)
               {
                  unsharedData = (UnsharedImportedData) obj;
               } else
               {
                  throw new IllegalArgumentException("unexpected argument type"
                        + svalue.getName() + " " + obj.getClass().getName());
               }
            } else
            {
               throw new IllegalArgumentException("unexpected argument "
                     + svalue.getName());
            }
         } else
         {
            throw new IllegalArgumentException("unexpected argument "
                  + value.getName());
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
         logger.error("invalid argument inputFile " + filePath
               + ", allowed format : "
               + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      Report iReport = null;
      if (importReport.getReport() != null)
      {
         iReport = importReport.getReport();
      } else
      {
         iReport = new ExchangeReport(ExchangeReport.KEY.IMPORT,
               description.getName());
      }

      Report vReport = null;
      if (validationReport.getReport() != null)
      {
         vReport = validationReport.getReport();
      } else
      {
         vReport = new ValidationReport();
      }

      List<Line> lines = null;

      if (extension.equals("xml"))
      {
         // simple file processing
         File f = new File(filePath);
         ReportItem fileReportItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE, Report.STATE.OK, f.getName());
         logger.info("start import simple file " + filePath);
         Line line = processFileImport(filePath, validate, fileReportItem,
               vReport, optimizeMemory, sharedData, unsharedData);
         if (line != null)
         {
            lines = new ArrayList<Line>();
            lines.add(line);
         }
         iReport.addItem(fileReportItem);
      } else
      {
         // zip file processing
         logger.info("start import zip file " + filePath);
         File f = new File(filePath);
         ReportItem zipReportItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.ZIP_FILE, Report.STATE.OK, f.getName());
         lines = processZipImport(filePath, validate, zipReportItem, vReport,
               optimizeMemory, sharedData, unsharedData);
         iReport.addItem(zipReportItem);
      }
      logger.info("import terminated");

      validationReport.setReport(vReport);
      importReport.setReport(iReport);

      return lines;
               }

   /**
    * import ZipFile
    * 
    * @param filePath
    *           path to zip File
    * @param validate
    *           process XML and XSD format validation
    * @param importReport
    *           report to fill
    * @param optimizeMemory
    * @param unsharedData
    * @param sharedData
    * @return list of loaded lines
    */
   private List<Line> processZipImport(String filePath, boolean validate,
         Report importReport, Report validationReport, boolean optimizeMemory,
         SharedImportedData sharedData, UnsharedImportedData unsharedData)
         {
      ZipFile zip = null;
      try
      {
         Charset encoding = FileTool.getZipCharset(filePath);
         if (encoding == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  filePath, "unknown encoding");
            importReport.addItem(item);
            importReport.updateStatus(Report.STATE.ERROR);
            logger.error("zip import failed (unknown encoding)");
            return null;
         }
         zip = new ZipFile(filePath);
      } catch (IOException e)
      {
         // report for save
         ReportItem fileErrorItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR,
               e.getLocalizedMessage());
         importReport.addItem(fileErrorItem);
         // log
         logger.error("zip import failed (cannot open zip)"
               + e.getLocalizedMessage());
         return null;
      }
      List<Line> lines = new ArrayList<Line>();
      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries
            .hasMoreElements();)
      {
         ZipEntry entry = entries.nextElement();

         // ignore directory without warning
         if (entry.isDirectory())
            continue;

         String entryName = entry.getName();
         if (entry.getName().contains("metadata_chouette"))
         {
            // report for save
            ReportItem fileReportItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_IGNORED, Report.STATE.OK,
                  entryName);
            importReport.addItem(fileReportItem);
            // log
            logger.info("zip entry " + entryName + " bypassed ; metadata file");
            continue;
         }
         else if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml"))
         {
            // report for save
            ReportItem fileReportItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_IGNORED, Report.STATE.OK,
                  entryName);
            importReport.addItem(fileReportItem);
            // log
            logger.info("zip entry " + entryName + " bypassed ; not a XML file");
            continue;
         }
         logger.info("start import zip entry " + entryName);
         ReportItem fileReportItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE, Report.STATE.OK, entryName);
         importReport.addItem(fileReportItem);
         try
         {
            InputStream stream = zip.getInputStream(entry);
            stream.close();
         } catch (IOException e)
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  e.getLocalizedMessage());
            fileReportItem.addItem(errorItem);
            // log
            logger.error("zip entry " + entryName
                  + " import failed (get entry)" + e.getLocalizedMessage());
            continue;
         }
         ChouettePTNetworkHolder holder = null;
         try
         {
            holder = reader.read(zip, entry, validate);
            validationReport.addItem(holder.getReport());
         } catch (ExchangeRuntimeException e)
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  e.getLocalizedMessage());
            fileReportItem.addItem(errorItem);
            // log
            logger.error("zip entry " + entryName + " import failed (read XML)"
                  + e.getLocalizedMessage());
            continue;
         } catch (Exception e)
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  e.getLocalizedMessage());
            fileReportItem.addItem(errorItem);
            // log
            logger.error(e.getLocalizedMessage());
            continue;
         }
         try
         {
            Context context = new Context(entryName,fileReportItem,null,sharedData, unsharedData, null
                  , optimizeMemory,null);
            Line line = processImport(context, holder, validate, validationReport);

            if (line != null)
            {
               lines.add(line);
            } else
            {
               logger.error("zip entry " + entryName
                     + " import failed (build model)");
            }

         } catch (ExchangeException e)
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  e.getLocalizedMessage());
            fileReportItem.addItem(errorItem);
            // log
            logger.error("zip entry " + entryName
                  + " import failed (convert to model)"
                  + e.getLocalizedMessage());
            continue;
         }
         logger.info("zip entry imported");
      }
      try
      {
         zip.close();
      } catch (IOException e)
      {
         logger.info("cannot close zip file");
      }
      if (lines.size() == 0)
      {
         logger.error("zip import failed (no valid entry)");
         return null;
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
    * @param importReport
    *           report to fill
    * @param optimizeMemory
    * @param unsharedData
    * @param sharedData
    * @return loaded line
    * @throws ExchangeException
    */
   private Line processFileImport(String filePath, boolean validate,
         ReportItem importReport, Report validationReport,
         boolean optimizeMemory, SharedImportedData sharedData,
         UnsharedImportedData unsharedData) throws ExchangeException
         {
      ChouettePTNetworkHolder holder = null;
      try
      {
         holder = reader.read(filePath, validate);
         validationReport.addItem(holder.getReport());
      } catch (ExchangeRuntimeException e)
      {
         // report for save
         ReportItem errorItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
               e.getLocalizedMessage());
         importReport.addItem(errorItem);
         // log
         logger.error("File " + filePath + " import failed (read XML) ["
               + e.getLocalizedMessage() + "]");
         return null;
      } catch (Exception e)
      {
         // report for save
         ReportItem errorItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
               e.getLocalizedMessage());
         importReport.addItem(errorItem);
         // log
         logger.error(e.getLocalizedMessage());
         return null;
      }
      Context context = new Context(filePath,importReport,null,sharedData, unsharedData, null
            , optimizeMemory,null);
      Line line = processImport(context, holder, validate, validationReport);
      if (line == null)
      {
         logger.error("import failed (build model)");
      }
      return line;
         }

   /**
    * process conversion between JAXB format and CHOUETTE internal format
    * 
    * @param rootObject
    *           container for JAXB loaded XML file
    * @param validate
    *           validate on XSD rules
    * @param validationReport 
    * @param importReport
    *           report to fill
    * @param entryName
    *           file name for logger purpose
    * @return builded line
    * @throws ExchangeException
    */
   private Line processImport(Context context, ChouettePTNetworkHolder holder, boolean validate, Report validationReport) throws ExchangeException
   {
      ChouettePTNetworkType rootObject = holder.getChouettePTNetwork();
      ReportItem importReport = context.getImportReport();
      if (validate || rootObject == null)
      {
         if (rootObject == null)
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.VALIDATION_ERROR, Report.STATE.ERROR,
                  "");
            importReport.addItem(errorItem);
            importReport.setMessageKey(ExchangeReportItem.KEY.FILE_ERROR
                  .toString());
            logger.error("no rootObject produced");
            return null;
         }
         if (holder.getReport().getStatus().ordinal() >= Report.STATE.ERROR
               .ordinal())
         {
            // report for save
            ReportItem errorItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.VALIDATION_ERROR, Report.STATE.ERROR,
                  "");
            importReport.addItem(errorItem);
            importReport.setMessageKey(ExchangeReportItem.KEY.FILE_ERROR
                  .toString());
            logger.error("level 1 validation returns status "
                  + holder.getReport().getStatus());
            logger.error(holder.getReport().toJSON());
            return null;
         }

      }

      // report for save
      ReportItem importItem = new ExchangeReportItem(
            ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK);

      PhaseReportItem validationItem = new PhaseReportItem(
            PhaseReportItem.PHASE.TWO);

      // process Line
      // forward phase2 validation
      ModelAssembler modelAssembler = new ModelAssembler(context);

      Level2Validator validator = new Level2Validator(context.getSourceFile(), validationItem);
      context.setValidator(validator);
      context.setAssembler(modelAssembler);
      context.setValidationReport(validationItem);
      context.setImportReport(importItem);
      try
      {

         Line line = converter.extractLine(context, rootObject);
         // should be made in converter.extractLine
         importItem.addMessageArgs(line.getName());

         modelAssembler.setLine(line);
         modelAssembler.setRoutes(converter.extractRoutes(context, rootObject));
         modelAssembler.setCompanies(converter.extractCompanies(context, rootObject));
         modelAssembler.setPtNetwork(converter.extractPTNetwork(context, rootObject));
         modelAssembler.setJourneyPatterns(converter.extractJourneyPatterns(context, rootObject));
         modelAssembler.setPtLinks(converter.extractPTLinks(context, rootObject));
         modelAssembler.setVehicleJourneys(converter.extractVehicleJourneys(context, rootObject));
         modelAssembler.setStopPoints(converter.extractStopPoints(context, rootObject));
         modelAssembler.setStopAreas(converter.extractStopAreas(context, rootObject));
         modelAssembler.setAreaCentroids(converter.extractAreaCentroids(context, rootObject));
         modelAssembler.setConnectionLinks(converter.extractConnectionLinks(context, rootObject));
         modelAssembler.setTimetables(converter.extractTimetables(context, rootObject));
         modelAssembler.setAccessLinks(converter.extractAccessLinks(context, rootObject));
         modelAssembler.setAccessPoints(converter.extractAccessPoints(context, rootObject));
         modelAssembler.setGroupOfLines(converter.extractGroupOfLines(context, rootObject));
         modelAssembler.setFacilities(converter.extractFacilities(context, rootObject));
         modelAssembler.setTimeSlots(converter.extractTimeSlots(context, rootObject));
         modelAssembler.setRoutingConstraints(converter.extractRoutingConstraints(context, rootObject));
         validator.validate();

         validationItem.refreshStatus(); // check why this is needed

         validationReport.addItem(validationItem);
         validationReport.refreshStatus();
         // check if validator failed !
         logger.info("validation status = "
               + validationItem.getStatus().toString());

         if (!validationItem.getStatus().equals(Report.STATE.ERROR))
         {
            modelAssembler.connect();
            // report objects count
            {
               ExchangeReportItem countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.ROUTE_COUNT, Report.STATE.OK,
                     modelAssembler.getRoutes().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,
                     Report.STATE.OK, modelAssembler.getJourneyPatterns().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,
                     Report.STATE.OK, modelAssembler.getVehicleJourneys().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.STOP_AREA_COUNT, Report.STATE.OK,
                     modelAssembler.getStopAreas().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,
                     Report.STATE.OK, modelAssembler.getConnectionLinks().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.ACCES_POINT_COUNT, Report.STATE.OK,
                     modelAssembler.getAccessPoints().size());
               importItem.addItem(countItem);
               countItem = new ExchangeReportItem(
                     ExchangeReportItem.KEY.TIME_TABLE_COUNT, Report.STATE.OK,
                     modelAssembler.getTimetables().size());
               importItem.addItem(countItem);
            }
            context.getImportReport().addItem(importItem);
            return line;
         }

         ReportItem errorItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.VALIDATION_ERROR, Report.STATE.ERROR, "");
         importReport.addItem(errorItem);
         importReport.setMessageKey(ExchangeReportItem.KEY.FILE_ERROR.toString());
         logger.error("level 2 validation failed");
      }
      finally
      {
         context.setImportReport(importReport);
      }

      return null;
   }

}
