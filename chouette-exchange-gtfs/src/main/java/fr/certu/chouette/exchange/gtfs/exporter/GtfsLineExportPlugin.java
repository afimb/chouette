package fr.certu.chouette.exchange.gtfs.exporter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.writer.DublinCoreFileWriter;
import fr.certu.chouette.export.metadata.writer.TextFileWriter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * export lines in GTFS GoogleTransit format
 */
@Log4j
public class GtfsLineExportPlugin implements IExportPlugin<Line>
{
   /**
    * describe plugin API
    */
   private FormatDescription description;

   /**
    * build a GtfsLineExportPlugin and fill API description
    */
   public GtfsLineExportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GTFS");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("timeZone", ParameterDescription.TYPE.STRING, false, true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("objectIdPrefix", ParameterDescription.TYPE.STRING, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("metadata", ParameterDescription.TYPE.OBJECT, false, false);
         params.add(param);
      }
      // possible filter in future extension :
      // send only trips for a period, manage colors
      // {
      // ParameterDescription param = new ParameterDescription("startDate",
      // ParameterDescription.TYPE.DATE, false, false);
      // params.add(param);
      // }
      // {
      // ParameterDescription param = new ParameterDescription("endDate",
      // ParameterDescription.TYPE.FILEPATH, false, false);
      // params.add(param);
      // }
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
    * fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List,
    * java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<Line> beans, List<ParameterValue> parameters, ReportHolder reportHolder) throws ChouetteException
   {
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      report.updateStatus(Report.STATE.OK);
      reportHolder.setReport(report);
      String fileName = null;
      TimeZone timeZone = null;
      String objectIdPrefix = null;
      boolean addMetadata = false;
      Metadata metadata = new Metadata();
      // Date startDate = null; // today ??
      // Date endDate = null; // in ten years ??

      if (beans == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.NO_LINE, Report.STATE.ERROR);
         report.addItem(item);
         return;
      }

      boolean error = false;
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("outputFile"))
            {
               fileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("timeZone"))
            {
               timeZone = TimeZone.getTimeZone(svalue.getStringValue());
            }
            else if (svalue.getName().equalsIgnoreCase("objectIdPrefix"))
            {
               objectIdPrefix = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("metadata"))
            {
               addMetadata = true;
               metadata = (Metadata) svalue.getObjectValue();
            }
            else
            {
               GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.UNKNOWN_PARAMETER, Report.STATE.ERROR, svalue.getName());
               report.addItem(item);
               error = true;
            }

         }
      }
      if (fileName == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_PARAMETER, Report.STATE.ERROR, "outputFile");
         report.addItem(item);
         error = true;
      }
      if (timeZone == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_PARAMETER, Report.STATE.ERROR, "timeZone");
         report.addItem(item);
         error = true;
      }

      // stop process if argument error
      if (error)
         return;

      File fic = new File(fileName);

      Path targetDirectory = null;
      try
      {
         targetDirectory = Files.createTempDirectory("gtfs_export_");
      }
      catch (IOException e)
      {
         ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, "/tmp", "cannot create tempdir");
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         log.error("zip import failed (cannot create temp dir)", e);
         return;
      }

      GtfsExporter exporter = null;
      metadata.setDate(Calendar.getInstance());
      metadata.setFormat("text/csv");
      metadata.setTitle("Export GTFS ");
      try
      {
         metadata.setRelation(new URL("https://developers.google.com/transit/gtfs/reference"));
      }
      catch (MalformedURLException e1)
      {
         log.error("problem with https://developers.google.com/transit/gtfs/reference url", e1);
      }
      try
      {
         exporter = new GtfsExporter(targetDirectory.toString());
         NeptuneData neptuneData = new NeptuneData();
         neptuneData.saveLines(beans, exporter, report, objectIdPrefix, objectIdPrefix, timeZone, metadata);
      }
      catch (Exception e)
      {
         log.error("incomplete data", e);
         exporter.dispose();
         try
         {
            FileUtils.deleteDirectory(targetDirectory.toFile());
         }
         catch (IOException e1)
         {
         }
         return;
      }
      exporter.dispose();

      // add metadata if required
      if (addMetadata)
      {
         try
         {
            DublinCoreFileWriter dcWriter = new DublinCoreFileWriter();
            dcWriter.writePlainFile(metadata, targetDirectory.toString());
            TextFileWriter tWriter = new TextFileWriter();
            tWriter.writePlainFile(metadata, targetDirectory.toString());         
         }
         catch (Exception e)
         {
            log.error("fail to produce metadata files ",e);
         }
      }
      
      // compress files to zip
      try
      {
         // create directory if exists
         File dir = fic.getParentFile();
         if (dir != null)
         {
            if (!dir.exists())
               dir.mkdirs();
         }
         // Create the ZIP file
         FileTool.compress(fileName, targetDirectory.toFile());
      }
      catch (IOException e)
      {
         log.error("cannot create zip file", e);
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
         report.addItem(item);
      }
      try
      {
         FileUtils.deleteDirectory(targetDirectory.toFile());
      }
      catch (IOException e1)
      {
      }

   }

}
