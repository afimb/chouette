package fr.certu.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.FileTool;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

public class GtfsImportStopAreaPlugin implements IImportPlugin<StopArea>
{
   private static final Logger log = Logger.getLogger(GtfsImportStopAreaPlugin.class);
   private FormatDescription description;
   @Setter
   private String dbDirectory = "/tmp";

   private List<String> allowedExtensions = Arrays.asList(new String[] { "zip" });

   public GtfsImportStopAreaPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GTFS");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("fileFormat", ParameterDescription.TYPE.STRING, false, "file extension");
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("objectIdPrefix", ParameterDescription.TYPE.STRING, false, true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("incremental", ParameterDescription.TYPE.STRING, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("maxDistanceForCommercial", ParameterDescription.TYPE.INTEGER, false, "10");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("ignoreLastWord", ParameterDescription.TYPE.BOOLEAN, false, "false");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("ignoreEndChars", ParameterDescription.TYPE.INTEGER, false, "0");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("maxDistanceForConnectionLink", ParameterDescription.TYPE.INTEGER, false, "50");
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("optimizeMemory", ParameterDescription.TYPE.BOOLEAN, false, "false");
         params.add(param);
      }

      description.setParameterDescriptions(params);
   }

   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   @Override
   public List<StopArea> doImport(List<ParameterValue> parameters, ReportHolder importReport, ReportHolder validationReport) throws ChouetteException
   {
      String filePath = null;
      String objectIdPrefix = null;
      double maxDistanceForCommercialStop = 10;
      double maxDistanceForConnectionLink = 50;
      boolean ignoreLastWord = false;
      ;
      int ignoreEndCharacters = 0;
      String incrementalPrefix = "";
      String extension = "file extension";
      boolean stopFound = false;
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
            }
            else if (svalue.getName().equals("fileFormat"))
            {
               extension = svalue.getStringValue().toLowerCase();
            }
            else if (svalue.getName().equalsIgnoreCase("objectIdPrefix"))
            {
               objectIdPrefix = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("incremental"))
            {
               incrementalPrefix = svalue.getStringValue();
            }
            else if (svalue.getName().equalsIgnoreCase("maxDistanceForCommercial"))
            {
               maxDistanceForCommercialStop = (double) svalue.getIntegerValue().doubleValue();
            }
            else if (svalue.getName().equalsIgnoreCase("ignoreLastWord"))
            {
               ignoreLastWord = svalue.getBooleanValue().booleanValue();
            }
            else if (svalue.getName().equalsIgnoreCase("optimizeMemory"))
            {
               // ignored
            }
            else if (svalue.getName().equalsIgnoreCase("ignoreEndChars"))
            {
               ignoreEndCharacters = svalue.getIntegerValue().intValue();
            }
            else if (svalue.getName().equalsIgnoreCase("maxDistanceForConnectionLink"))
            {
               maxDistanceForConnectionLink = (double) svalue.getIntegerValue().doubleValue();
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
         log.error("missing argument zipFile");
         throw new IllegalArgumentException("zipFile required");
      }

      if (objectIdPrefix == null)
      {
         log.error("missing argument objectIdPrefix");
         throw new IllegalArgumentException("objectIdPrefix required");
      }

      if (extension.equals("file extension"))
      {
         extension = FilenameUtils.getExtension(filePath).toLowerCase();
      }
      if (!allowedExtensions.contains(extension))
      {
         log.error("invalid argument inputFile " + filePath + ", allowed format : " + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, description.getName());
      report.updateStatus(Report.STATE.OK);
      importReport.setReport(report);

      Path targetDirectory = null;
      try
      {
         targetDirectory = Files.createTempDirectory("gtfs_import_");
      }
      catch (IOException e)
      {
         ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, filePath, "cannot create tempdir");
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         log.error("zip import failed (cannot create temp dir)", e);
         return null;
      }

      try
      {
         Charset encoding = FileTool.getZipCharset(filePath);
         if (encoding == null)
         {
            ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, filePath, "unknown encoding");
            report.addItem(item);
            report.updateStatus(Report.STATE.ERROR);
            log.error("zip import failed (unknown encoding)");
            return null;
         }

         FileTool.uncompress(filePath, targetDirectory.toFile());
      }
      catch (IOException e)
      {
         ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, filePath, e.getLocalizedMessage());
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         log.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
         try
         {
            FileUtils.deleteDirectory(targetDirectory.toFile());
         }
         catch (IOException e2)
         {
            // TODO Auto-generated catch block
         }
         return null;
      }
      GtfsImporter importer = new GtfsImporter(targetDirectory.toString());
      try
      {
         GtfsChecker checker = new GtfsChecker();
         boolean ok = checker.check(importer, report, false);
         // try
         // {
         // importer.getStopById();
         // stopFound = true;
         // } catch (GtfsException e)
         // {
         // ReportItem item = new ExchangeReportItem(
         // ExchangeReportItem.KEY.ZIP_MISSING_ENTRY, Report.STATE.ERROR,
         // "stops.txt", filePath);
         // report.addItem(item);
         // report.updateStatus(Report.STATE.ERROR);
         // log.error("zip import failed (missing entry stops.txt)",e);
         // ok = false;
         // } catch (Exception e)
         // {
         // ReportItem item = new ExchangeReportItem(
         // ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR,
         // "stops.txt", filePath, e.getLocalizedMessage());
         // report.addItem(item);
         // report.updateStatus(Report.STATE.ERROR);
         // log.error(
         // "zip import failed (cannot read stops.txt)"
         // + e.getLocalizedMessage(), e);
         // ok = false;
         //
         // }
         // try
         // {
         // importer.getTransferByFromStop();
         // } catch (GtfsException e)
         // {
         // // not mandatory
         // } catch (Exception e)
         // {
         // ReportItem item = new ExchangeReportItem(
         // ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR,
         // "transfers.txt", filePath, e.getLocalizedMessage());
         // report.addItem(item);
         // report.updateStatus(Report.STATE.ERROR);
         // log.error(
         // "zip import failed (cannot read transfers.txt)"
         // + e.getLocalizedMessage(), e);
         // ok = false;
         //
         // }
         // if (!stopFound)
         // {
         // ReportItem item = new ExchangeReportItem(
         // ExchangeReportItem.KEY.ZIP_MISSING_ENTRY, Report.STATE.ERROR,
         // "stops.txt", filePath);
         // report.addItem(item);
         // report.updateStatus(Report.STATE.ERROR);
         // log.error("zip import failed (missing entry stops.txt)");
         // ok = false;
         // }
         if (ok)
         {
            NeptuneConverter converter = new NeptuneConverter(importer);
            AbstractModelProducer.setPrefix(objectIdPrefix);
            AbstractModelProducer.setIncrementalPrefix(incrementalPrefix);

            // stopareas
            List<StopArea> commercials = new ArrayList<StopArea>();
            List<StopArea> areas = new ArrayList<StopArea>();
            Map<String, StopArea> mapStopAreasByStopId = new HashMap<String, StopArea>();
            converter.convertStopAreas(report, areas, commercials, mapStopAreasByStopId, maxDistanceForCommercialStop, ignoreLastWord, ignoreEndCharacters);

            // ConnectionLinks
            List<ConnectionLink> links = new ArrayList<ConnectionLink>();
            converter.convertConnectionLink(report, links, commercials, mapStopAreasByStopId, maxDistanceForConnectionLink);

            // report objects count
            {
               ExchangeReportItem countItem = new ExchangeReportItem(ExchangeReportItem.KEY.LINE_COUNT, Report.STATE.OK, 0);
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ROUTE_COUNT, Report.STATE.OK, 0);
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT, Report.STATE.OK, 0);
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT, Report.STATE.OK, 0);
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.STOP_AREA_COUNT, Report.STATE.OK, areas.size());
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT, Report.STATE.OK, links.size());
               report.addItem(countItem);
               countItem = new ExchangeReportItem(ExchangeReportItem.KEY.TIME_TABLE_COUNT, Report.STATE.OK, 0);
               report.addItem(countItem);
            }
            return areas;
         }
         else
         {
            return new ArrayList<StopArea>();
         }
      }
      catch (Exception e)
      {
         // unexpected pb
         log.error("fatal error :" + e.getMessage(), e);
         return new ArrayList<StopArea>();
      }
      finally
      {
         if (importer != null)
         {
            importer.dispose();
         }
         try
         {
            FileUtils.deleteDirectory(targetDirectory.toFile());
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
         }

      }

   }

}
