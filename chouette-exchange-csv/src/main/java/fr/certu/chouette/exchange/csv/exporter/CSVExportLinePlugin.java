package fr.certu.chouette.exchange.csv.exporter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.csv.exporter.producer.CompanyProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.LineProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.csv.exporter.producer.TimetableProducer;
import fr.certu.chouette.exchange.csv.exporter.report.CSVReport;
import fr.certu.chouette.exchange.csv.exporter.report.CSVReportItem;
import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.model.NeptuneObjectPresenter;
import fr.certu.chouette.export.metadata.model.Metadata.Resource;
import fr.certu.chouette.export.metadata.writer.DublinCoreFileWriter;
import fr.certu.chouette.export.metadata.writer.TextFileWriter;
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

@Log4j
public class CSVExportLinePlugin implements IExportPlugin<Line>
{

   @Setter
   TimetableProducer timetableProducer;
   @Setter
   PTNetworkProducer ptNetworkProducer;
   @Setter
   CompanyProducer companyProducer;
   @Setter
   LineProducer lineProducer;

   private FormatDescription description;

   public CSVExportLinePlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("CSV");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      ParameterDescription param1 = new ParameterDescription("outputFile",
            ParameterDescription.TYPE.FILEPATH, false, true);
      param1.setAllowedExtensions(Arrays.asList(new String[] { "csv", "zip" }));
      params.add(param1);
      {
         ParameterDescription param = new ParameterDescription("metadata", ParameterDescription.TYPE.OBJECT, false, false);
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
   public void doExport(List<Line> beans, List<ParameterValue> parameters,
         ReportHolder reportContainer) throws ChouetteException
         {
      boolean addMetadata = false;
      Metadata metadata = new Metadata();
      String fileName = null;
      CSVReport report = new CSVReport(CSVReport.KEY.EXPORT);
      report.updateStatus(Report.STATE.OK);
      reportContainer.setReport(report);

      if (beans == null || beans.size() == 0)
      {
         throw new IllegalArgumentException("no beans to export");
      }

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equals("outputFile"))
            {
               fileName = svalue.getFilepathValue();
            }
            else if (svalue.getName().equalsIgnoreCase("metadata"))
            {
               addMetadata = true;
               metadata = (Metadata) svalue.getObjectValue();
            }

         }
      }
      if (fileName == null)
      {
         throw new IllegalArgumentException("outputFile required");
      }

      if (beans.size() > 1)
      {
         if (fileName.toLowerCase().endsWith(".csv"))
            throw new IllegalArgumentException(
                  "cannot export multiple lines in simple csv file");
      }
      if (fileName.toLowerCase().endsWith(".zip"))
      {
         metadata.setDate(Calendar.getInstance());
         metadata.setFormat("application/xml");
         metadata.setTitle("Export Neptune ");
         try
         {
            metadata.setRelation(new URL("http://www.chouette.mobi"));
         }
         catch (MalformedURLException e1)
         {
            log.error("problem with http://www.chouette.mobi url", e1);
         }
         // Create the ZIP file
         try
         {

            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                  fileName));
            for (Line line : beans)
            {
               if (line.getRoutes().isEmpty())
                  continue;
               if (line.getRoutes().size() > 2)
               {
                  CSVReportItem item = new CSVReportItem(
                        CSVReportItem.KEY.TOO_MUCH_ROUTES,
                        Report.STATE.WARNING, line.getName());
                  report.addItem(item);
                  log.error("cannot export " + line.getName()
                        + " too much routes (> 2)");
                  continue;
               }
               line.complete();
               ByteArrayOutputStream stream = new ByteArrayOutputStream();
               try
               {
                  exportLine(new OutputStreamWriter(stream, "UTF-8"), line,
                        report,metadata);

                  // Add ZIP entry to output stream.
                  ZipEntry entry = new ZipEntry(line.getName() + "_"
                        + line.getId() + ".csv");
                  out.putNextEntry(entry);

                  out.write(stream.toByteArray());
                  CSVReportItem item = new CSVReportItem(
                        CSVReportItem.KEY.OK_LINE, Report.STATE.OK,
                        line.getName());
                  report.addItem(item);

                  // Complete the entry
                  out.closeEntry();
                  metadata.getResources().add(metadata.new Resource(line.getName() + "_" + line.getId() + ".csv", 
                        NeptuneObjectPresenter.getName(line.getPtNetwork()), NeptuneObjectPresenter.getName(line)));
               } catch (IOException e)
               {
                  log.error("export failed ", e);
               }
            }

            // write metadata
            if (addMetadata)
            {
               DublinCoreFileWriter dcWriter = new DublinCoreFileWriter();
               dcWriter.writeZipEntry(metadata, out);
               TextFileWriter tWriter = new TextFileWriter();
               tWriter.writeZipEntry(metadata, out);
            }

            out.close();
         } catch (Exception e)
         {
            log.error("export failed ", e);
         }

      } else
      {
         Line line = beans.get(0);
         try
         {
            line.complete();
            exportLine(new OutputStreamWriter(new FileOutputStream(fileName),
                  "UTF-8"), line, report, metadata);
         } catch (IOException e)
         {
            log.error("export failed ", e);
         }
      }
         }

   private void exportLine(Writer writer, Line line, Report report, Metadata metadata)
   {
      List<Timetable> timetables = line.getTimetables();

      try
      {
         CSVWriter csvWriter = new CSVWriter(writer, ';',
               CSVWriter.NO_QUOTE_CHARACTER);
         for (Timetable timetable : timetables)
         {

            csvWriter.writeAll(timetableProducer.produce(timetable, report));
            csvWriter.writeNext(new String[0]);
            metadata.getTemporalCoverage().update(timetable.getStartOfPeriod(), timetable.getEndOfPeriod());
         }

         for (StopArea stopArea : line.getStopAreas())
         {
            if (stopArea.hasCoordinates())
               metadata.getSpatialCoverage().update(stopArea.getLongitude().doubleValue(), stopArea.getLatitude().doubleValue());

         }
         csvWriter.writeAll(ptNetworkProducer.produce(line.getPtNetwork(),
               report));
         csvWriter.writeNext(new String[0]);

         csvWriter.writeAll(companyProducer.produce(line.getCompany(), report));
         csvWriter.writeNext(new String[0]);

         csvWriter.writeAll(lineProducer.produce(line, report));
         csvWriter.writeNext(new String[0]);

         csvWriter.close();
      } catch (IOException e)
      {
         log.error("export failed ", e);
      }
   }

}
