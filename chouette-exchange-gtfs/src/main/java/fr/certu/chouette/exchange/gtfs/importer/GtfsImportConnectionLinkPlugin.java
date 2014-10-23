package fr.certu.chouette.exchange.gtfs.importer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsException;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Index;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
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

public class GtfsImportConnectionLinkPlugin implements
      IImportPlugin<ConnectionLink>
{
   private static final Logger logger = Logger
         .getLogger(GtfsImportConnectionLinkPlugin.class);
   private FormatDescription description;
   @Setter
   private String dbDirectory = "/tmp";

   private List<String> allowedExtensions = Arrays
         .asList(new String[] { "zip" });

   /**
    * Connection producer from GtfsTransfer
    */
   @Setter
   private INeptuneManager<StopArea> stopAreaManager;

   public GtfsImportConnectionLinkPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GTFS");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("inputFile",
               ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("fileFormat",
               ParameterDescription.TYPE.STRING, false, "file extension");
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription(
               "objectIdPrefix", ParameterDescription.TYPE.STRING, false, true);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription(
               "optimizeMemory", ParameterDescription.TYPE.BOOLEAN, false,
               "false");
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
   public List<ConnectionLink> doImport(List<ParameterValue> parameters,
         ReportHolder importReport, ReportHolder validationReport)
         throws ChouetteException
   {
      String filePath = null;
      String objectIdPrefix = null;
      String extension = "file extension";
      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
            } else if (svalue.getName().equals("fileFormat"))
            {
               extension = svalue.getStringValue().toLowerCase();
            } else if (svalue.getName().equalsIgnoreCase("objectIdPrefix"))
            {
               objectIdPrefix = svalue.getStringValue().toLowerCase();
            } else if (svalue.getName().equalsIgnoreCase("optimizeMemory"))
            {
               // unsued value
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
         logger.error("missing argument zipFile");
         throw new IllegalArgumentException("zipFile required");
      }

      if (objectIdPrefix == null)
      {
         logger.error("missing argument objectIdPrefix");
         throw new IllegalArgumentException("objectIdPrefix required");
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

      Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            description.getName());
      report.updateStatus(Report.STATE.OK);
      importReport.setReport(report);

      Path targetDirectory = null;
      try
      {
         targetDirectory = Files.createTempDirectory("gtfs_import_");
      } catch (IOException e)
      {
         ReportItem item = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, filePath,
               "cannot create tempdir");
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         logger.error("zip import failed (cannot create temp dir)", e);
         return null;
      }
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

         FileTool.uncompress(filePath, targetDirectory.toFile());
      } catch (IOException e)
      {
         ReportItem item = new ExchangeReportItem(
               ExchangeReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, filePath,
               e.getLocalizedMessage());
         report.addItem(item);
         report.updateStatus(Report.STATE.ERROR);
         logger.error("zip import failed (cannot open zip)"
               + e.getLocalizedMessage());
         return null;
      }
      GtfsImporter importer = new GtfsImporter(targetDirectory.toString());
      try
      {
         boolean ok = true;
         Index<GtfsTransfer> transferImporter = null;
         try
         {
            transferImporter = importer.getTransferByToStop();
         } catch (GtfsException e)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.ZIP_MISSING_ENTRY, Report.STATE.ERROR,
                  "transfer.txt", filePath);
            report.addItem(item);
            report.updateStatus(Report.STATE.ERROR);
            logger.error("zip import failed (missing entry transfer.txt)");
            ok = false;
         } catch (Exception e)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.ZIP_ERROR, Report.STATE.ERROR,
                  "transfers.txt", filePath, e.getLocalizedMessage());
            report.addItem(item);
            report.updateStatus(Report.STATE.ERROR);
            logger.error(
                  "zip import failed (cannot read transfers.txt)"
                        + e.getLocalizedMessage(), e);
            ok = false;

         }
         if (ok)
         {
            ConnectionLinkProducer connectionLinkProducer = new ConnectionLinkProducer();
            List<StopArea> areas = stopAreaManager.getAll(null);

            if (areas.size() == 0)
            {
               logger.warn("no area for connection link ");
               return new ArrayList<ConnectionLink>();

            }
            Map<String, StopArea> areaByKey = NeptuneIdentifiedObject
                  .mapOnObjectIds(areas);

            List<ConnectionLink> links = new ArrayList<ConnectionLink>();
            List<ConnectionLink> excludedLinks = new ArrayList<ConnectionLink>();
            AbstractModelProducer.setPrefix(objectIdPrefix);

            for (GtfsTransfer transfer : transferImporter)
            {
               ConnectionLink link = connectionLinkProducer.produce(transfer,
                     null);

               link.setStartOfLinkId(objectIdPrefix + ":"
                     + StopArea.STOPAREA_KEY + ":" + link.getStartOfLinkId());
               link.setEndOfLinkId(objectIdPrefix + ":" + StopArea.STOPAREA_KEY
                     + ":" + link.getEndOfLinkId());

               if ("FORBIDDEN".equals(link.getName()))
               {
                  excludedLinks.add(link);
               } else
               {
                  link.setStartOfLink(areaByKey.get(link.getStartOfLinkId()));
                  link.setEndOfLink(areaByKey.get(link.getEndOfLinkId()));
                  if (link.getStartOfLink() == null)
                  {
                     // report missing link
                     logger.warn("missing start of link "
                           + link.getStartOfLinkId());
                     continue;
                  }
                  if (link.getEndOfLink() == null)
                  {
                     // report missing link
                     logger.warn("missing end of link " + link.getEndOfLinkId());
                     continue;
                  }
                  link.setName("from " + link.getStartOfLink().getName()
                        + " to " + link.getEndOfLink().getName());
                  links.add(link);
               }
            }
            return links;
         } else
         {
            return new ArrayList<ConnectionLink>();
         }
      } finally
      {
         importer.dispose();
      }

   }

}
