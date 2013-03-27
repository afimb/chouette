package fr.certu.chouette.exchange.netex.importer;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.netex.NetexReport;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.DetailReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.report.SheetReportItem;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class NetexImportPlugin implements IImportPlugin<Line> 
{    
    private VTDGen vg = new VTDGen();  
    private static final Logger logger = Logger.getLogger(NetexImportPlugin.class);
    
    @Getter
    private NetexReport report = new NetexReport(NetexReport.KEY.IMPORT);  
    
   @Getter @Setter
   private NetexFileReader netexFileReader;
   
    /**
    * API description for caller
    */
   private FormatDescription   description;
   /**
    * list of allowed file extensions
    */
   private List<String>        allowedExtensions = Arrays.asList(new String[] { "xml", "zip" });
   /**
    * warning and error reporting container
    */
   private SheetReportItem sheet1_1 = new SheetReportItem("Test1_Sheet1", 1);;
   /**
    * warning and error reporting container
    */
   private SheetReportItem sheet1_2 = new SheetReportItem("Test1_Sheet2", 2);
   /**
    * file format reporting
    */
   private SheetReportItem report1_1_1 = new SheetReportItem("Test1_Sheet1_Step1", 1);
   /**
    * data format reporting
    */
   private SheetReportItem report1_2_1 = new SheetReportItem("Test1_Sheet2_Step1", 1);       

   /**
    * Constructor
    */
   public NetexImportPlugin()
   {
      description = new FormatDescription(this.getClass().getName());
      description.setName("NETEX");
      description.setUnzipAllowed(true);
      
      List<ParameterDescription> params = new ArrayList<ParameterDescription>();
      ParameterDescription inputFile = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false, true);
      inputFile.setAllowedExtensions(allowedExtensions);
      params.add(inputFile);

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
    * fr.certu.chouette.plugin.exchange.IImportPlugin#doImport(java.util.List,
    * fr.certu.chouette.plugin.report.ReportHolder)
    */
   @Override
   public List<Line> doImport(List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
   {
      reportContainer.setReport(report);

      String filePath = null;
      String extension = "file extension";

      for (ParameterValue value : parameters)
      {
         if (value instanceof SimpleParameterValue)
         {
            SimpleParameterValue svalue = (SimpleParameterValue) value;
            if (svalue.getName().equalsIgnoreCase("inputFile"))
            {
               filePath = svalue.getFilepathValue();
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
         logger.error("missing argument inputFile");
         throw new IllegalArgumentException("inputFile required");
      }
    
      extension = FilenameUtils.getExtension(filePath).toLowerCase();
     
      if (!allowedExtensions.contains(extension))
      {
         logger.error("invalid argument inputFile " + filePath + ", allowed format : " + Arrays.toString(allowedExtensions.toArray()));
         throw new IllegalArgumentException("invalid file type : " + extension);
      }

      List<Line> lines = new ArrayList<Line>();

      if (extension.equals("xml"))
      {
         logger.info("start import simple file " + filePath);
         Line line = readXmlFile(filePath, report);
         if (line != null)
            lines.add(line);         
      }
      else
      {
         logger.info("start import zip file " + filePath);
         lines = readZipFile(filePath, report);
      }
      
      logger.info("import terminated");
      
      sheet1_1.addItem(report1_1_1);
      sheet1_2.addItem(report1_2_1);
      report.addItem(sheet1_1);
      report.addItem(sheet1_2);
      return lines;
   }
   
   public Line readXmlFile(String filePath, NetexReport netexReport)           
   {   
       Line line = null;
       try {
           InputStream stream;                                
           stream = new FileInputStream(filePath);
           
           line = netexFileReader.readInputStream(stream);
           stream.close();                               
       } catch (java.text.ParseException ex) {
           logger.error(ex.getMessage());            
        } catch (IOException ex) {
           logger.error(ex.getMessage());
       } catch (EncodingException ex) {
           logger.error(ex.getMessage());
       } catch (EOFException ex) {
           logger.error(ex.getMessage());
       } catch (EntityException ex) {
            logger.error(ex.getMessage());           
       } catch (ParseException ex) {
           logger.error(ex.getMessage());
       } catch (XPathParseException ex) {
           logger.error(ex.getMessage());
       } catch (XPathEvalException ex) {
           logger.error(ex.getMessage());
       } catch (NavException ex) {
          logger.error(ex.getMessage()); 
       }
       
       return line;
   }
      
   public List<Line> readZipFile(String filePath, NetexReport netexReport) {
        List<Line> lines = new ArrayList<Line>();    
        Line line;
        boolean ofType1 = false;
        boolean ofType2 = false;
        boolean someOk = false;

        ZipFile zip = null;
        try {
            zip = new ZipFile(filePath);
        } catch (IOException e) {
            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL, filePath);
            report1_1_1.addItem(detailReportItem);
            logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
            return null;
        }

        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();

            // ignore directory without warning
            if (entry.isDirectory()) {
                continue;
            }

            String entryName = entry.getName();
            if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml")) {
                ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_warning", Report.STATE.WARNING,
                        entryName);
                report1_1_1.addItem(detailReportItem);
                logger.info("zip entry " + entryName + " bypassed ; not a XML file");
                continue;
            }

            logger.info("start import zip entry " + entryName);
            
            try {
                InputStream stream = zip.getInputStream(entry);
                
                line = netexFileReader.readInputStream(stream);
                stream.close();
                lines.add(line);
                
                someOk = true;
                report1_1_1.updateStatus(Report.STATE.OK);
                report1_2_1.updateStatus(Report.STATE.OK);
            } catch (IOException e) {
                ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
                        entryName);
                report1_1_1.addItem(detailReportItem);
                logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
                continue;
            } catch (ExchangeRuntimeException e) {
                if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode())) {
                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
                            entryName);
                    report1_1_1.addItem(detailReportItem);
                    report1_1_1.computeDetailItemCount();
                    ofType1 = true;
                } else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name())) {
                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR,
                            entryName);
                    report1_2_1.addItem(detailReportItem);
                    report1_1_1.updateStatus(Report.STATE.OK);
                    ofType2 = true;
                } else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name())) {
                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, entryName);
                    report1_2_1.addItem(detailReportItem);
                    report1_1_1.updateStatus(Report.STATE.OK);
                } else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name())) {
                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
                            entryName);
                    report1_1_1.addItem(detailReportItem);
                    ofType1 = true;
                }
                logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
                continue;
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error(e.getLocalizedMessage());
                continue;
            }

        }


//               boolean ofType1 = false;
//      boolean ofType2 = false;
//      boolean someOk = false;
//         
//         logger.info("start import zip entry " + entryName);
//         
//         try
//         {
//             InputStream stream =  zip.getInputStream(entry);
//            stream.close();
//         }
//         catch (IOException e)
//         {
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
//                  entryName);
//            report1_1_1.addItem(detailReportItem);
//            logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
//            continue;
//         
//            
//            
//            
//            
//         ChouettePTNetworkTypeType rootObject = null;
//         try
//         {
//            rootObject = reader.read(zip, entry, validate);
//            someOk = true;
//            report1_1_1.updateStatus(Report.STATE.OK);
//            report1_2_1.updateStatus(Report.STATE.OK);
//         }
//         catch (ExchangeRuntimeException e)
//         {
//            if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_1_1.addItem(detailReportItem);
//               report1_1_1.computeDetailItemCount();
//               ofType1 = true;
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_2_1.addItem(detailReportItem);
//               report1_1_1.updateStatus(Report.STATE.OK);
//               ofType2 = true;
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, entryName);
//               report1_2_1.addItem(detailReportItem);
//               report1_1_1.updateStatus(Report.STATE.OK);
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_1_1.addItem(detailReportItem);
//               ofType1 = true;
//            }
//            logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
//            continue;
//         }
//         catch (Exception e)
//         {
//            logger.error(e.getLocalizedMessage());
//            continue;
//         }
//         
//         
//         try
//         {
//            Line line = processImport(rootObject, validate, report, entryName,sharedData,optimizeMemory);
//
//            if (line != null)
//            {
//               lines.add(line);
//               report1_1_1.updateStatus(Report.STATE.OK);
//               report1_2_1.updateStatus(Report.STATE.OK);
//            }
//            else
//            {
//               logger.error("zip entry " + entryName + " import failed (build model)");
//            }
//
//         }
//         catch (ExchangeException e)
//         {
//            report1_1_1.updateStatus(Report.STATE.ERROR);
//            logger.error("zip entry " + entryName + " import failed (convert to model)" + e.getLocalizedMessage());
//            continue;
//         }
//         logger.info("zip entry imported");
//      }
//      try
//      {
//         zip.close();
//      }
//      catch (IOException e)
//      {
//         logger.info("cannot close zip file");
//      }
//      if (!ofType1 && ofType2)
//      {
//         report1_1_1.updateStatus(Report.STATE.OK);
//      }
//      else if (ofType1 && !ofType2 && !someOk)
//      {
//         report1_2_1.updateStatus(Report.STATE.UNCHECK);
//      }
//
//      report1_1_1.computeDetailItemCount();
//      report1_2_1.computeDetailItemCount();
//      if (lines.size() == 0)
//      {
//         logger.error("zip import failed (no valid entry)");
//         return null;
//      }
//      else
//      {
//         report1_1_1.updateStatus(Report.STATE.OK);
//      }

        return lines;
    }

   
   
   
//
//   /**
//    * import ZipFile
//    * 
//    * @param filePath
//    *           path to zip File
//    * @param validate
//    *           process XML and XSD format validation
//    * @param report
//    *           report to fill
//    * @param optimizeMemory 
//    * @return list of loaded lines
//    */
//   private List<Line> processZipImport(String filePath, NetexReport report)
//   {
//      NetexFileReader reader = new NetexFileReader();
//      ZipFile zip = null;
//      try
//      {
//         zip = new ZipFile(filePath);
//      }
//      catch (IOException e)
//      {
//         ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL, filePath);
//         report1_1_1.addItem(detailReportItem);
//         logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
//         return null;
//      }
//      List<Line> lines = new ArrayList<Line>();
//      boolean ofType1 = false;
//      boolean ofType2 = false;
//      boolean someOk = false;
//      SharedImportedData sharedData = new SharedImportedData();
//      for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
//      {
//         ZipEntry entry = entries.nextElement();
//
//         // ignore directory without warning
//         if (entry.isDirectory())
//            continue;
//
//         String entryName = entry.getName();
//         if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml"))
//         {
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_warning", Report.STATE.WARNING,
//                  entryName);
//            report1_1_1.addItem(detailReportItem);
//            logger.info("zip entry " + entryName + " bypassed ; not a XML file");
//            continue;
//         }
//         logger.info("start import zip entry " + entryName);
//         
//         try
//         {
//        	InputStream stream =  zip.getInputStream(entry);
//            stream.close();
//         }
//         catch (IOException e)
//         {
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
//                  entryName);
//            report1_1_1.addItem(detailReportItem);
//            logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
//            continue;
//         }
//         ChouettePTNetworkTypeType rootObject = null;
//         try
//         {
//            rootObject = reader.read(zip, entry, validate);
//            someOk = true;
//            report1_1_1.updateStatus(Report.STATE.OK);
//            report1_2_1.updateStatus(Report.STATE.OK);
//         }
//         catch (ExchangeRuntimeException e)
//         {
//            if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_1_1.addItem(detailReportItem);
//               report1_1_1.computeDetailItemCount();
//               ofType1 = true;
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_2_1.addItem(detailReportItem);
//               report1_1_1.updateStatus(Report.STATE.OK);
//               ofType2 = true;
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, entryName);
//               report1_2_1.addItem(detailReportItem);
//               report1_1_1.updateStatus(Report.STATE.OK);
//            }
//            else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
//            {
//               ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                     entryName);
//               report1_1_1.addItem(detailReportItem);
//               ofType1 = true;
//            }
//            logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
//            continue;
//         }
//         catch (Exception e)
//         {
//            logger.error(e.getLocalizedMessage());
//            continue;
//         }
//         try
//         {
//            Line line = processImport(rootObject, validate, report, entryName,sharedData,optimizeMemory);
//
//            if (line != null)
//            {
//               lines.add(line);
//               report1_1_1.updateStatus(Report.STATE.OK);
//               report1_2_1.updateStatus(Report.STATE.OK);
//            }
//            else
//            {
//               logger.error("zip entry " + entryName + " import failed (build model)");
//            }
//
//         }
//         catch (ExchangeException e)
//         {
//            report1_1_1.updateStatus(Report.STATE.ERROR);
//            logger.error("zip entry " + entryName + " import failed (convert to model)" + e.getLocalizedMessage());
//            continue;
//         }
//         logger.info("zip entry imported");
//      }
//      try
//      {
//         zip.close();
//      }
//      catch (IOException e)
//      {
//         logger.info("cannot close zip file");
//      }
//      if (!ofType1 && ofType2)
//      {
//         report1_1_1.updateStatus(Report.STATE.OK);
//      }
//      else if (ofType1 && !ofType2 && !someOk)
//      {
//         report1_2_1.updateStatus(Report.STATE.UNCHECK);
//      }
//
//      report1_1_1.computeDetailItemCount();
//      report1_2_1.computeDetailItemCount();
//      if (lines.size() == 0)
//      {
//         logger.error("zip import failed (no valid entry)");
//         return null;
//      }
//      else
//      {
//         report1_1_1.updateStatus(Report.STATE.OK);
//      }
//      return lines;
//   }
//
//   /**
//    * import simple Neptune file
//    * 
//    * @param filePath
//    *           path to File
//    * @param validate
//    *           process XML and XSD format validation
//    * @param report
//    *           report to fill
//    * @param optimizeMemory 
//    * @return loaded line
//    * @throws ExchangeException
//    */
//   private Line processFileImport(String filePath, NetexReport report) throws ExchangeException
//   {
//      ChouettePTNetworkTypeType rootObject = null;
//      NeptuneFileReader reader = new NeptuneFileReader();
//      try
//      {
//         rootObject = reader.read(filePath, validate);
//         report1_1_1.updateStatus(Report.STATE.OK);
//         report1_2_1.updateStatus(Report.STATE.OK);
//      }
//      catch (ExchangeRuntimeException e)
//      {
//         if (e.getCode().equals(ExchangeExceptionCode.INVALID_XML_FILE.name()))
//         {
//            logger.error("INVALID_XML_FILE " + filePath);
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
//            report1_1_1.addItem(detailReportItem);
//            report1_1_1.computeDetailItemCount();
//            report1_2_1.updateStatus(Report.STATE.UNCHECK);
//         }
//         else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
//         {
//            logger.error("INVALID_NEPTUNE_FILE " + filePath);
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR, filePath);
//            report1_2_1.addItem(detailReportItem);
//            report1_1_1.updateStatus(Report.STATE.OK);
//         }
//         else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name()))
//         {
//            logger.error("INVALID_NEPTUNE_FILE " + filePath);
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, filePath);
//            report1_2_1.addItem(detailReportItem);
//            report1_1_1.updateStatus(Report.STATE.OK);
//         }
//         else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
//         {
//            logger.error("FILE_NOT_FOUND " + filePath);
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
//            report1_1_1.addItem(detailReportItem);
//            report1_2_1.updateStatus(Report.STATE.UNCHECK);
//         }
//         logger.error("File " + filePath + " import failed (read XML) [" + e.getLocalizedMessage() + "]");
//         return null;
//      }
//      catch (Exception e)
//      {
//         // ReportItem detailReportItem = new
//         // DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL,
//         // filePath);
//         // report1_1.addItem(detailReportItem);
//         // report1_1.computeDetailItemCount();
//         // logger.error("import failed ((read XML)) " +
//         // e.getLocalizedMessage());
//         logger.error(e.getLocalizedMessage());
//         return null;
//      }
//      Line line = processImport(rootObject, validate, report, filePath,new SharedImportedData(),optimizeMemory);
//      if (line == null)
//      {
//         logger.error("import failed (build model)");
//         // report.setStatus(Report.STATE.FATAL);
//         // report1_2.updateStatus(Report.STATE.FATAL);
//      }
//      else
//      {
//         report1_1_1.updateStatus(Report.STATE.OK);
//      }
//      return line;
//   }
//
//   /**
//    * process conversion between CASTOR format and CHOUETTE internal format
//    * 
//    * @param rootObject
//    *           container for CASTOR loaded XML file
//    * @param validate
//    *           validate on XSD rules
//    * @param report
//    *           report to fill
//    * @param entryName
//    *           file name for logger purpose
//    * @return builded line
//    * @throws ExchangeException
//    */
//   private Line processImport(ChouettePTNetworkTypeType rootObject, boolean validate, Report report, String entryName,SharedImportedData sharedData,boolean optimizeMemory)
//         throws ExchangeException
//   {
//      if (validate)
//      {
//         try
//         {
//            rootObject.validate();
//         }
//         catch (ValidationException e)
//         {
//            logger.error("import failed for " + entryName + " : Castor validation");
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                  entryName);
//            report1_2_1.addItem(detailReportItem);
//            Throwable t = e;
//            while (t != null)
//            {
//               logger.error(t.getLocalizedMessage());
//               // ReportItem detail2 = new
//               // DetailReportItem("",Report.STATE.ERROR,
//               // t.getLocalizedMessage());
//               // report1_2.addItem(detail2);
//               t = t.getCause();
//            }
//            return null;
//         }
//      }
//      report1_2_1.computeDetailItemCount();
//      ReportItem item = new NeptuneReportItem(NeptuneReportItem.KEY.OK_LINE, Report.STATE.OK, entryName, "");
//      report1_2_1.updateStatus(Report.STATE.OK);
//
//      ModelAssembler modelAssembler = new ModelAssembler();
//
//      Line line = converter.extractLine(rootObject, item);
//      modelAssembler.setLine(line);
//      modelAssembler.setRoutes(converter.extractRoutes(rootObject, item));
//      modelAssembler.setCompanies(converter.extractCompanies(rootObject, item,sharedData));
//      modelAssembler.setPtNetwork(converter.extractPTNetwork(rootObject, item,sharedData));
//      modelAssembler.setJourneyPatterns(converter.extractJourneyPatterns(rootObject, item));
//      modelAssembler.setPtLinks(converter.extractPTLinks(rootObject, item));
//      modelAssembler.setVehicleJourneys(converter.extractVehicleJourneys(rootObject, item,optimizeMemory));
//      modelAssembler.setStopPoints(converter.extractStopPoints(rootObject, item));
//      modelAssembler.setStopAreas(converter.extractStopAreas(rootObject, item,sharedData));
//      modelAssembler.setAreaCentroids(converter.extractAreaCentroids(rootObject, item,sharedData));
//      modelAssembler.setConnectionLinks(converter.extractConnectionLinks(rootObject, item,sharedData));
//      modelAssembler.setTimetables(converter.extractTimetables(rootObject, item,sharedData));
//      modelAssembler.setAccessLinks(converter.extractAccessLinks(rootObject, item,sharedData));
//      modelAssembler.setAccessPoints(converter.extractAccessPoints(rootObject, item,sharedData));
//      modelAssembler.setGroupOfLines(converter.extractGroupOfLines(rootObject, item,sharedData));
//      modelAssembler.setFacilities(converter.extractFacilities(rootObject, item,sharedData));
//      modelAssembler.setTimeSlots(converter.extractTimeSlots(rootObject, item,sharedData));
//      modelAssembler.setRoutingConstraints(converter.extractRoutingConstraints(rootObject, item));
//      modelAssembler.connect();
//
//      rootObject.toString();
//
//      return line;
//   }
//    

    
    
}
