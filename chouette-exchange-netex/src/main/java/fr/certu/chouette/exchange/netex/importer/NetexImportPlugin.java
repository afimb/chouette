package fr.certu.chouette.exchange.netex.importer;

import java.io.File;
import java.io.FileInputStream;
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

import com.ximpleware.VTDGen;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class NetexImportPlugin implements IImportPlugin<Line>
{
   private VTDGen vg = new VTDGen();
   private static final Logger logger = Logger
         .getLogger(NetexImportPlugin.class);

   @Getter
   @Setter
   private NetexFileReader netexFileReader;

   /**
    * API description for caller
    */
   private FormatDescription description;
   /**
    * list of allowed file extensions
    */
   private List<String> allowedExtensions = Arrays.asList(new String[] { "xml",
         "zip" });

   /**
    * Constructor
    */
   public NetexImportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("NETEX");
      description.setUnzipAllowed(true);

      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      ParameterDescription inputFile = new ParameterDescription("inputFile",
            ParameterDescription.TYPE.FILEPATH, false, true);
      inputFile.setAllowedExtensions(allowedExtensions);
      params.add(inputFile);

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
      Report iReport = null;
      if (importReport.getReport() != null)
      {
         iReport = importReport.getReport();
      } else
      {
         iReport = new ExchangeReport(ExchangeReport.KEY.IMPORT,
               description.getName());
      }

      String filePath = null;
      String extension = "file extension";

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
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

      extension = FilenameUtils.getExtension(filePath).toLowerCase();

      if (!allowedExtensions.contains(extension))
      {
         logger.error("invalid argument inputFile " + filePath
               + ", allowed format : "
               + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      List<Line> lines = new ArrayList<Line>();

      if (extension.equals("xml"))
      {
         logger.info("start import simple file " + filePath);
         Line line = readXmlFile(filePath, iReport);
         if (line != null)
            lines.add(line);
      } else
      {
         logger.info("start import zip file " + filePath);
         lines = readZipFile(filePath, iReport);
      }

      logger.info("import terminated");

      return lines;
   }

   public Line readXmlFile(String filePath, Report report)
   {
      Line line = null;
      File f = new File(filePath);
      ReportItem fileReportItem = new ExchangeReportItem(
            ExchangeReportItem.KEY.FILE, Report.STATE.OK, f.getName());
      report.addItem(fileReportItem);
      try
      {
         InputStream stream;
         stream = new FileInputStream(filePath);

         line = netexFileReader.readInputStream(stream, report);
         ReportItem importItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK);
         report.addItem(importItem);
         importItem.addMessageArgs(line.getName());
         line.complete();
         ExchangeReportItem countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.ROUTE_COUNT, Report.STATE.OK, line
                     .getRoutes().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT, Report.STATE.OK,
               line.getJourneyPatterns().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT, Report.STATE.OK,
               line.getVehicleJourneys().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.STOP_AREA_COUNT, Report.STATE.OK, line
                     .getStopAreas().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.CONNECTION_LINK_COUNT, Report.STATE.OK,
               line.getConnectionLinks().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.ACCES_POINT_COUNT, Report.STATE.OK, line
                     .getAccessPoints().size());
         importItem.addItem(countItem);
         countItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.TIME_TABLE_COUNT, Report.STATE.OK, line
                     .getTimetables().size());
         importItem.addItem(countItem);
         stream.close();
      } catch (Exception ex)
      {
         // report for save
         ReportItem errorItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
               ex.getLocalizedMessage());
         fileReportItem.addItem(errorItem);
         // log
         logger.error(ex.getMessage(), ex);
      }

      return line;
   }

   public List<Line> readZipFile(String filePath, Report report)
   {
      List<Line> lines = new ArrayList<Line>();
      Line line;

      ZipFile zip = null;
      try
      {
         Charset encoding = FileTool.getZipCharset(filePath);
         if (encoding == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR,
                  filePath, "unknown encoding");
            report.addItem(item);
            report.updateStatus(Report.STATE.ERROR);
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
         report.addItem(fileErrorItem);
         // log
         logger.error("zip import failed (cannot open zip)"
               + e.getLocalizedMessage());
         return null;
      }

      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries
            .hasMoreElements();)
      {
         ZipEntry entry = entries.nextElement();

         // ignore directory without warning
         if (entry.isDirectory())
         {
            continue;
         }

         String entryName = entry.getName();
         if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml"))
         {
            // report for save
            ReportItem fileReportItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.FILE_IGNORED, Report.STATE.OK,
                  entryName);
            report.addItem(fileReportItem);
            // log
            logger.info("zip entry " + entryName + " bypassed ; not a XML file");
            continue;
         }

         logger.info("start import zip entry " + entryName);
         ReportItem fileReportItem = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE, Report.STATE.OK, entryName);
         report.addItem(fileReportItem);
         try
         {
            InputStream stream = zip.getInputStream(entry);

            line = netexFileReader.readInputStream(stream, fileReportItem);
            ReportItem importItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK);
            report.addItem(importItem);
            importItem.addMessageArgs(line.getName());
            line.complete();
            ExchangeReportItem countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.ROUTE_COUNT, Report.STATE.OK, line
                        .getRoutes().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,
                  Report.STATE.OK, line.getJourneyPatterns().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,
                  Report.STATE.OK, line.getVehicleJourneys().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.STOP_AREA_COUNT, Report.STATE.OK, line
                        .getStopAreas().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,
                  Report.STATE.OK, line.getConnectionLinks().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.ACCES_POINT_COUNT, Report.STATE.OK,
                  line.getAccessPoints().size());
            importItem.addItem(countItem);
            countItem = new ExchangeReportItem(
                  ExchangeReportItem.KEY.TIME_TABLE_COUNT, Report.STATE.OK,
                  line.getTimetables().size());
            importItem.addItem(countItem);
            stream.close();
            lines.add(line);
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
            logger.error(e.getMessage(), e);
            logger.error(e.getLocalizedMessage());
            continue;
         }

      }

      if (zip != null)
      {
         try
         {
            zip.close();
         } catch (IOException e)
         {
            logger.warn("fail to close zip " + e.getLocalizedMessage());
         }
      }

      return lines;
   }

}
