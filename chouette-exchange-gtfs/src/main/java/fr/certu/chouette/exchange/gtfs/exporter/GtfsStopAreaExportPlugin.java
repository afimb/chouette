package fr.certu.chouette.exchange.gtfs.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.exchange.gtfs.model.GtfsExtendedStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;

/**
 * export lines in GTFS GoogleTransit format
 */
public class GtfsStopAreaExportPlugin implements IExportPlugin<StopArea>
{
   private static final Logger logger = Logger
         .getLogger(GtfsStopAreaExportPlugin.class);
   private static final String GTFS_CHARSET = "UTF-8";

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
            } else
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
      ZipOutputStream out = null;
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
         out = new ZipOutputStream(new FileOutputStream(fileName));
      } catch (IOException e)
      {
         logger.error("cannot create zip file", e);
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
         report.addItem(item);
         return;
      }

      NeptuneData neptuneData = new NeptuneData();
      neptuneData.populateStopAreas(beans);

      logger.info("export " + neptuneData.physicalStops.size()
            + " physical stops");
      logger.info("export " + neptuneData.commercialStops.size()
            + " commercial stops");
      logger.info("export " + neptuneData.connectionLinks.size() + " transfers");

      GtfsData gtfsData = null;
      try
      {
         GtfsDataProducer gtfsDataProducer = new GtfsDataProducer();
         gtfsData = gtfsDataProducer.produceStops(neptuneData, report);
      } catch (GtfsExportException e)
      {
         logger.error("incomplete data", e);
         try
         {
            out.close();
         } catch (IOException e1)
         {
         }
         if (fic.exists())
            fic.delete();
         return;
      }

      try
      {
         writeFile(out, gtfsData.getStops(), "stops.txt",
               GtfsExtendedStop.header, report);
         writeFile(out, gtfsData.getTransfer(), "transfers.txt",
               GtfsTransfer.header, report);
      } catch (GtfsExportException e)
      {
         logger.error("zipEntry failure " + e.getMessage(), e);
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
         report.addItem(item);
      }

      try
      {
         // Complete the ZIP file
         out.close();
      } catch (IOException e)
      {
         logger.error("cannot create zip file", e);
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
         report.addItem(item);
      }
   }

   /**
    * write a file in Zip file
    * 
    * @param out
    *           zip file
    * @param gtfsBeans
    *           GTFS objects to write
    * @param entryName
    *           zip entry name (GTFS Filename)
    * @param header
    *           GTFS File header
    */
   private void writeFile(ZipOutputStream out,
         List<? extends GtfsBean> gtfsBeans, String entryName, String header,
         GtfsReport report) throws GtfsExportException
   {
      if (gtfsBeans.isEmpty())
      {
         logger.info(entryName + " is empty, not produced");
         return;
      }
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream,
               GTFS_CHARSET);
         writer.write(header);
         writer.write("\n");
         for (GtfsBean gtfsBean : gtfsBeans)
         {
            writer.write(gtfsBean.getCSVLine());
            writer.write("\n");
         }
         writer.close();
         // Add ZIP entry to output stream.
         ZipEntry entry = new ZipEntry(entryName);
         out.putNextEntry(entry);

         out.write(stream.toByteArray());

         // Complete the entry
         out.closeEntry();
      } catch (IOException e)
      {
         logger.error(entryName + " failure " + e.getMessage(), e);
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, entryName);
         report.addItem(item);
         throw new GtfsExportException(GtfsExportExceptionCode.ERROR, entryName);
      }

   }

}
