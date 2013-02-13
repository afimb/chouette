package fr.certu.chouette.exchange.netex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Setter;
import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Line;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.plugin.exchange.xml.exception.*;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IExportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

import chouette.schema.ChouettePTNetworkTypeType;

import fr.certu.chouette.exchange.netex.NetexReport;
import fr.certu.chouette.exchange.netex.NetexFileWriter;

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
      ParameterDescription startDate = new ParameterDescription("startDate", ParameterDescription.TYPE.DATE, false, false);
      params.add(startDate);
      ParameterDescription endDate = new ParameterDescription("endDate", ParameterDescription.TYPE.DATE, false, false);
      params.add(endDate);

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
   public void doExport(List<Line> beans, List<ParameterValue> parameters, ReportHolder reportContainer)
   throws ChouetteException
   {
       reportContainer.setReport(report);
       
       String fileName = null;

       if (beans == null)
           {
               throw new IllegalArgumentException("no beans to export");
           }
       Date startDate = null; 
       Date endDate = null; 

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
                                       logger.warn("outputFile changed as FILEPATH type");
                                       fileName = svalue.getFilenameValue();
                                   }
                           }
                       else if (svalue.getName().equalsIgnoreCase("startDate"))
                           {
                               Calendar c = svalue.getDateValue();
                               if (c != null)
                                   startDate = new Date(c.getTime().getTime());
                           }
                       else if (svalue.getName().equalsIgnoreCase("endDate"))
                           {
                               Calendar c = svalue.getDateValue();
                               if (c != null)
                                   endDate = new Date(c.getTime().getTime());
                           }
                   }
           }
       if (fileName == null)
           {
               throw new IllegalArgumentException("outputFile required");
           }

       String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

       if (beans.size() > 1 && fileExtension.equals("xml"))
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
               createXmlFile(fileName, beans, startDate, endDate);
           }
       else
           {

               try
                   {
                       createZipFile(fileName, beans, startDate, endDate);
                   }
               catch (IOException e)
                   {
                       logger.error("cannot create zip file", e);
                       throw new ExchangeRuntimeException(ExchangeExceptionCode.ERR_XML_WRITE, e);
                   }
           }

   }

   private File createXmlFile(String filename, List<Line> beans, Date startDate, Date endDate)        
   {
       Line line = beans.get(0);
       //ChouettePTNetworkTypeType rootObject = exportLine(line,startDate,endDate);
       //if (rootObject != null)
           {    
               logger.info("exporting "+line.getName()+" ("+line.getObjectId()+")");
               netexFileWriter.write(beans, filename);
               NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE,Report.STATE.OK , line.getName(), line.getObjectId());
               report.addItem(item);
           }
           return new File("");
       // else
       //     {
       //         logger.info("no vehiclejourneys for line "+line.getName()+" ("+line.getObjectId()+"): not exported");
       //         NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE,Report.STATE.ERROR , line.getName(), line.getObjectId());
       //         report.addItem(item);
       //     }
   }

   private ZipOutputStream createZipFile(String fileName, List<Line> beans, Date startDate, Date endDate) throws FileNotFoundException, IOException
   {
       // Create the ZIP file
       ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(fileName));

       // Compress the files
       for (Iterator<Line> iterator = beans.iterator(); iterator.hasNext();)
           {
               Line line = iterator.next();
               iterator.remove();

               //ChouettePTNetworkTypeType rootObject = exportLine(line,startDate,endDate);
               //if (rootObject != null)
                   {    
                       logger.info("exporting "+line.getName()+" ("+line.getObjectId()+")");

                       String name = line.getObjectId().split(":")[2];

                       ByteArrayOutputStream stream = new ByteArrayOutputStream();
                       netexFileWriter.write(beans, stream);

                       // Add ZIP entry to zipFileput stream.
                       ZipEntry entry = new ZipEntry(name + ".xml");
                       zipFile.putNextEntry(entry);

                       zipFile.write(stream.toByteArray());

                       // Complete the entry
                       zipFile.closeEntry();
                       NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EXPORTED_LINE,Report.STATE.OK , line.getName(), line.getObjectId());
                       report.addItem(item);
                   }
               // else
               //     {
               //         logger.info("no vehiclejourneys for line "+line.getName()+" ("+line.getObjectId()+"): not exported");
               //         NetexReportItem item = new NetexReportItem(NetexReportItem.KEY.EMPTY_LINE,Report.STATE.WARNING , line.getName(), line.getObjectId());
               //         report.addItem(item);
               //     }
               System.gc();

           }

       // Complete the ZIP file
       zipFile.close();
       return zipFile;
   }


}
