package fr.certu.chouette.exchange.gtfs.exporter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.model.neptune.Line;
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
public class GtfsLineExportPlugin implements IExportPlugin<Line>
{
   private static final Logger logger       = Logger.getLogger(GtfsLineExportPlugin.class);
   private static final String GTFS_CHARSET = "UTF-8";

   /**
    * describe plugin API
    */
   private FormatDescription   description;

   // @Setter private String defaultLineColor = "ff007f00";
   /**
    * data converter from neptune objects to Gtfs objects
    */
   @Setter
   private GtfsDataProducer    gtfsDataProducer;

   /**
    * build a GtfsLineExportPlugin and fill API description
    */
   public GtfsLineExportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("GTFS");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false,
               true);
         param.setAllowedExtensions(Arrays.asList(new String[] { "zip" }));
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("timeZone", ParameterDescription.TYPE.STRING, false,
               true);
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
      // manage lines colors : perhaps in Neptune Model
      // {
      // ParameterDescription param = new ParameterDescription("lineColor",
      // ParameterDescription.TYPE.STRING, false, false);
      // params.add(param);
      // }
      // {
      // ParameterDescription param = new ParameterDescription("colorMap",
      // ParameterDescription.TYPE.FILEPATH, false, false);
      // param.setAllowedExtensions(Arrays.asList(new String[]{"txt","tmp"}));
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
   public void doExport(List<Line> beans, List<ParameterValue> parameters, ReportHolder reportHolder)
   throws ChouetteException
   {
      GtfsReport report = new GtfsReport(GtfsReport.KEY.EXPORT);
      report .setStatus(Report.STATE.OK);
      reportHolder.setReport(report);
      String fileName = null;
      TimeZone timeZone = null;
      // Date startDate = null; // today ??
      // Date endDate = null; // in ten years ??
      // String lineColor = defaultLineColor;
      // String colorMapFileName = null;

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
            else
            {
               GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.UNKNOWN_PARAMETER, Report.STATE.ERROR,svalue.getName());
               report.addItem(item);
               error = true;
            }

         }
      }
      if (fileName == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_PARAMETER, Report.STATE.ERROR,"outputFile");
         report.addItem(item);
         error = true;
      }
      if (timeZone == null)
      {
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_PARAMETER, Report.STATE.ERROR,"timeZone");
         report.addItem(item);
         error = true;
      }

      // stop process if argument error
      if (error) return;

      ZipOutputStream out = null;
      try
      {
         // create directory if exists 
         File fic = new File(fileName);
         File dir = fic.getParentFile();
         if (dir != null) 
         {
            if (!dir.exists()) dir.mkdirs();
         }
         // Create the ZIP file
         out = new ZipOutputStream(new FileOutputStream(fileName));
      }
      catch (IOException e) 
      {
         logger.error("cannot create zip file", e);
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
         report.addItem(item);
         return;
      }

      // complete data for export if necessary
//      for (Line line : beans)
//      {
//         line.complete();
//      }

      NeptuneData neptuneData = new NeptuneData();
      neptuneData.populate(beans);

      GtfsData gtfsData = null;
      try
      {
         gtfsData = gtfsDataProducer.produce(neptuneData, timeZone,report);
      }
      catch (GtfsExportException e)
      {
         logger.error("incomplete data");
         return;
      }

      try
      {
         writeFile(out, gtfsData.getAgencies(), "agency.txt", GtfsAgency.header,report);
         writeFile(out, gtfsData.getStops(), "stops.txt", GtfsStop.header,report);
         writeFile(out, gtfsData.getRoutes(), "routes.txt", GtfsRoute.header,report);
         writeFile(out, gtfsData.getTrip(), "trips.txt", GtfsTrip.header,report);
         writeFile(out, gtfsData.getStoptimes(), "stop_times.txt", GtfsStopTime.header,report);
         writeFile(out, gtfsData.getCalendars(), "calendar.txt", GtfsCalendar.header,report);
         writeFile(out, gtfsData.getCalendardates(), "calendar_dates.txt", GtfsCalendarDate.header,report);
         writeFile(out, gtfsData.getFrequencies(), "frequencies.txt", GtfsFrequency.header,report);
         // fare_rules.txt
         // fare_attributes.txt
         // shapes.txt
         // transfers.txt
      }
      catch (GtfsExportException e)
      {
         logger.error("zipEntry failure "+e.getMessage());
      }

      try
      {
         // Complete the ZIP file
         out.close();
      }
      catch (IOException e)
      {
         logger.error("cannot create zip file", e);
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, fileName);
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
   private void writeFile(ZipOutputStream out, List<? extends GtfsBean> gtfsBeans, String entryName, String header,GtfsReport report) throws GtfsExportException
   {
      if (gtfsBeans.isEmpty())
      {
         logger.info(entryName + " is empty, not produced");
         return;
      }
      try
      {
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         OutputStreamWriter writer = new OutputStreamWriter(stream, GTFS_CHARSET);
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
      }
      catch (IOException e)
      {
         logger.error(entryName + " failure "+e.getMessage(),e);
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.FILE_ACCESS, STATE.ERROR, entryName);
         report.addItem(item);
         throw new GtfsExportException(GtfsExportExceptionCode.ERROR, entryName);
      }

   }

}
