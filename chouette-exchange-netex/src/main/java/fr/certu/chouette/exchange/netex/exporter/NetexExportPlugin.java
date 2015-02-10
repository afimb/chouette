package fr.certu.chouette.exchange.netex.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FilenameUtils;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.netex.NetexReport;
import fr.certu.chouette.exchange.netex.NetexReportItem;
import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.model.NeptuneObjectPresenter;
import fr.certu.chouette.export.metadata.writer.DublinCoreFileWriter;
import fr.certu.chouette.export.metadata.writer.TextFileWriter;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

/**
 * Export lines in Netex format
 */
@Log4j
public class NetexExportPlugin implements IExportPlugin<Line>
{

   private FormatDescription description;
   private NetexReport report = new NetexReport(NetexReport.KEY.EXPORT);
   /**
    * list of allowed file extensions
    */
   private List<String> allowedExtensions = Arrays.asList(new String[] { "xml", "zip" });

   @Getter
   @Setter
   private NetexFileWriter netexFileWriter;

   /**
    * Export lines in Netex format
    */
   public NetexExportPlugin()
   {
      report.updateStatus(Report.STATE.OK);

      description = new FormatDescription(this.getClass().getName());
      description.setName("NETEX");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();

      {
         ParameterDescription param = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false, true);
         param.setAllowedExtensions(allowedExtensions);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("projectionType", ParameterDescription.TYPE.STRING, false, false);
         params.add(param);
      }
      {
         ParameterDescription param = new ParameterDescription("metadata", ParameterDescription.TYPE.OBJECT, false, false);
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
   public void doExport(List<Line> lines, List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
   {
      boolean addMetadata = false;
      Metadata metadata = new Metadata();
      reportContainer.setReport(report);

      String fileName = null;
      String projectionType = null;

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("outputFile"))
            {
               fileName = svalue.getFilepathValue();
               if (fileName == null)
               {
                  log.warn("outputFile changed as FILEPATH type");
                  fileName = svalue.getFilenameValue();
               }
               else if (svalue.getName().equalsIgnoreCase("projectionType"))
               {
                  projectionType = svalue.getStringValue();
               }
               else if (svalue.getName().equalsIgnoreCase("metadata"))
               {
                  addMetadata = true;
                  metadata = (Metadata) svalue.getObjectValue();
               }
            }
         }
      }

      if (lines == null)
      {
         throw new IllegalArgumentException("No beans to export");
      }
      if (fileName == null)
      {
         throw new IllegalArgumentException("outputFile required");
      }

      String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

      if (lines.size() > 1 && fileExtension.equals("xml"))
      {
         throw new IllegalArgumentException("cannot export multiple lines in one XML file");
      }

      File outputFile = new File(fileName);
      if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists())
      {
         outputFile.getParentFile().mkdirs();
      }

      if (fileExtension.equals("xml"))
      {
         createXmlFile(fileName, lines.get(0), projectionType);
      }
      else
      {
         createZipFile(fileName, lines, projectionType, metadata,addMetadata);
      }

   }

   private File createXmlFile(String filename, Line line, String projectionType)
   {
      log.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");
      File xmlFile = null;
      // Complete datas for all neptune objects
      line.complete();

      if (line.getVehicleJourneys() == null || line.getVehicleJourneys().isEmpty())
      {
         log.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
         NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getName(), line.getObjectId());
         report.addItem(item);
      }
      else
      {
         // apply projection if asked
         for (AccessPoint ap: line.getAccessPoints())
         {
            ap.toProjection(projectionType);
         }
         for (StopArea sa: line.getStopAreas())
         {
           sa.toProjection(projectionType);
         }
         try
         {
            xmlFile = netexFileWriter.writeXmlFile(line, filename);
            NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
            report.addItem(item);
         }
         catch (DatatypeConfigurationException exception)
         {
            log.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
            NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
            report.addItem(item);
         }
         catch (IOException exception)
         {
            log.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
            NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
            report.addItem(item);
         }
      }

      return xmlFile;
   }

   private ZipOutputStream createZipFile(String fileName, List<Line> lines, String projectionType, Metadata metadata, boolean addMetadata)
   {
      ZipOutputStream zipFile = null;
      metadata.setDate(Calendar.getInstance());
      metadata.setFormat("application/xml");
      metadata.setTitle("Export NeTEx ");
      try
      {
         metadata.setRelation(new URL("http://www.chouette.mobi/pourquoi-chouette/convertir-des-donnees/"));
      }
      catch (MalformedURLException e1)
      {
         log.error("problem with http://www.chouette.mobi/pourquoi-chouette/convertir-des-donnees/ url", e1);
      }

      try
      {
         zipFile = new ZipOutputStream(new FileOutputStream(fileName));
         // Compress the files
         for (Iterator<Line> it = lines.iterator(); it.hasNext();)
         {
            Line line = it.next();
            // Hack to remove line and free memory
            it.remove();
            // Complete datas for all neptune objects
            line.complete();

            log.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");

            if (line.getVehicleJourneys() == null || line.getVehicleJourneys().isEmpty())
            {
               log.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
               NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getName(), line.getObjectId());
               report.addItem(item);
            }
            else
            {
               try
               {
                  // Add ZIP entry to zipFile stream.
                  String entryName = line.objectIdSuffix() + ".xml";
                  netexFileWriter.writeZipEntry(line, entryName, zipFile);
                  NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
                  report.addItem(item);
                  metadata.getResources().add(metadata.new Resource( entryName,
                        NeptuneObjectPresenter.getName(line.getPtNetwork()), NeptuneObjectPresenter.getName(line)));
                  for (Timetable tm : line.getTimetables())
                  {
                     metadata.getTemporalCoverage().update(tm.getStartOfPeriod(), tm.getEndOfPeriod());
                  }
                  for (StopArea stop : line.getStopAreas())
                  {
                     if (stop.hasCoordinates())
                        metadata.getSpatialCoverage().update(stop.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
                  }
                  
               }
               catch (DatatypeConfigurationException exception)
               {
                  log.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
                  NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
                  report.addItem(item);
               }
               catch (IOException exception)
               {
                  log.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
                  NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
                  report.addItem(item);
               }
            }
         }
         System.gc();

         // write metadata
         if (addMetadata)
         {
            try
            {
               DublinCoreFileWriter dcWriter = new DublinCoreFileWriter();
               dcWriter.writeZipEntry(metadata, zipFile);
               TextFileWriter tWriter = new TextFileWriter();
               tWriter.writeZipEntry(metadata, zipFile);
            }
            catch (DatatypeConfigurationException e)
            {
               log.error("unable to produce metadata",e);
            }
         }

         // Complete the ZIP file
         zipFile.close();
      }
      catch (IOException exception)
      {
         log.error("Impossible to create zip file : " + exception);
         NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR);
         report.addItem(item);
      }

      return zipFile;
   }
}
