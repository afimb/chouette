package fr.certu.chouette.exchange.gtfs.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.model.neptune.StopArea;
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
public class GtfsStopAreaExportPlugin implements IExportPlugin<StopArea>
{

   /**
    * describe plugin API
    */
   private FormatDescription description;

   /**
    * build a GtfsLineExportPlugin and fill API description
    */
   public GtfsStopAreaExportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GTFS");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile",
               ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("objectIdPrefix", ParameterDescription.TYPE.STRING, false, false);
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
    * fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List,
    * java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<StopArea> beans, List<ParameterValue> parameters,
         ReportHolder reportHolder) throws ChouetteException
   {
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      report.updateStatus(Report.STATE.OK);
      reportHolder.setReport(report);
      String fileName = null;
      String objectIdPrefix = null;

      if (beans == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.NO_LINE,
               Report.STATE.ERROR);
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
            else if (svalue.getName().equalsIgnoreCase("objectIdPrefix"))
            {
               objectIdPrefix = svalue.getStringValue();
            }
            else
            {
               GtfsReportItem item = new GtfsReportItem(
                     GtfsReportItem.KEY.UNKNOWN_PARAMETER, Report.STATE.ERROR,
                     svalue.getName());
               report.addItem(item);
               error = true;
            }

         }
      }
      if (fileName == null)
      {
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_PARAMETER, Report.STATE.ERROR,
               "outputFile");
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
      try
      {
         exporter = new GtfsExporter(targetDirectory.toString());
         NeptuneData neptuneData = new NeptuneData();
         neptuneData.saveStopAreas(beans, exporter, report, objectIdPrefix);
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
