package fr.certu.chouette.exchange.netex.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;


import fr.certu.chouette.exchange.netex.NetexReport;
import fr.certu.chouette.exchange.netex.NetexReportItem;

/**
 *  Export lines in Netex format
 */
public class NetexExportPlugin implements IExportPlugin<Line>
{

   private static final Logger       logger = Logger.getLogger(NetexExportPlugin.class);
   private FormatDescription         description;
   private NetexFileWriter           netexFileWriter = new NetexFileWriter();
   private NetexReport               report = new NetexReport(NetexReport.KEY.EXPORT);

   /**
    * Export lines in Netex format
    */
   public NetexExportPlugin()
   {
      report.setStatus(Report.STATE.OK);
       
      description = new FormatDescription(this.getClass().getName());
      description.setName("NETEX");
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      
      ParameterDescription outputFile = new ParameterDescription("outputFile", ParameterDescription.TYPE.FILEPATH, false, true);
      outputFile.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
      params.add(outputFile);

      description.setParameterDescriptions(params);
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.plugin.exchange.IExchangePlugin#getDescription()
    */
   @Override
   public FormatDescription getDescription()
   {
      return description;
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.plugin.exchange.IExportPlugin#doExport(java.util.List, java.util.List, fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public void doExport(List<Line> lines, List<ParameterValue> parameters, ReportHolder reportContainer)
   throws ChouetteException
   {
        reportContainer.setReport(report);

        String fileName = null;
        
        for (ParameterValue value : parameters) {
            if (value instanceof SimpleParameterValue) {
                SimpleParameterValue svalue = (SimpleParameterValue) value;
                if (svalue.getName().equalsIgnoreCase("outputFile")) {
                    fileName = svalue.getFilepathValue();
                    if (fileName == null) {
                        logger.warn("outputFile changed as FILEPATH type");
                        fileName = svalue.getFilenameValue();
                    }
                }
            }
        }
        
        if (lines == null) {
            throw new IllegalArgumentException("No beans to export");
        }
        if (fileName == null) {
            throw new IllegalArgumentException("outputFile required");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        if (lines.size() > 1 && fileExtension.equals("xml")) {
            throw new IllegalArgumentException("cannot export multiple lines in one XML file");
        }

        File outputFile = new File(fileName);
        if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        if (fileExtension.equals("xml")) {
            createXmlFile(fileName, lines.get(0));
        } else {
            createZipFile(fileName, lines);           
        }

    }

    private File createXmlFile(String filename, Line line) {
        logger.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");
        File xmlFile = null;
        
        if (line.getVehicleJourneys() == null || line.getVehicleJourneys().isEmpty()) {
            logger.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
            NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getName(), line.getObjectId());
            report.addItem(item);
        } else {
            try {
                xmlFile = netexFileWriter.writeXmlFile(line, filename);
                NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
                report.addItem(item);
            } catch (IOException exception) {
                logger.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
                NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
                report.addItem(item);
            }
        }

        return xmlFile;
    }

    private ZipOutputStream createZipFile(String fileName, List<Line> lines)
    {
        ZipOutputStream zipFile = null;
        try {
            zipFile = new ZipOutputStream(new FileOutputStream(fileName));
            // Compress the files
            for (int i = 0; i < lines.size(); i++) {
                Line line = lines.get(i);           
                
                logger.info("exporting " + line.getName() + " (" + line.getObjectId() + ")");

                if (line.getVehicleJourneys() == null || line.getVehicleJourneys().isEmpty()) {
                    logger.info("no vehiclejourneys for line " + line.getName() + " (" + line.getObjectId() + "): not exported");
                    NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR, line.getName(), line.getObjectId());
                    report.addItem(item);
                } else {
                    try {
                        // Add ZIP entry to zipFile stream.
                        String entryName = line.objectIdSuffix() + ".xml";
                        netexFileWriter.writeZipEntry(line, entryName, zipFile);
                        NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE, Report.STATE.OK, line.getName(), line.getObjectId());
                        report.addItem(item);
                    } catch (IOException exception) {
                        logger.error("Impossible to create xml file for line " + line.getName() + " : " + exception);
                        NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR, line.getName(), line.getObjectId());
                        report.addItem(item);
                    }
                }
            }
            System.gc();
            
            // Complete the ZIP file
            zipFile.close();            
        } catch (IOException exception) {
            logger.error("Impossible to create zip file : " + exception);
            NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.FILE_ERROR, Report.STATE.ERROR);
            report.addItem(item);
        }
        
        return zipFile;
    }
}
