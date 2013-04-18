package fr.certu.chouette.exchange.netex.importer;

import com.ximpleware.VTDGen;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
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

	@Getter @Setter
	private NetexFileReader netexFileReader;

	//   @Getter
	//   private NetexReport report;

	/**
	 * API description for caller
	 */
	private FormatDescription   description;
	/**
	 * list of allowed file extensions
	 */
	private List<String>        allowedExtensions = Arrays.asList(new String[] { "xml", "zip" });
	//   /**
	//    * warning and error reporting container
	//    */
	//   private SheetReportItem sheet1_1 = new SheetReportItem("Test1_Sheet1", 1);;
	//   /**
	//    * warning and error reporting container
	//    */
	//   private SheetReportItem sheet1_2 = new SheetReportItem("Test1_Sheet2", 2);
	//   /**
	//    * file format reporting
	//    */
	//   private SheetReportItem report1_1_1 = new SheetReportItem("Test1_Sheet1_Step1", 1);
	//   /**
	//    * data format reporting
	//    */
	//   private SheetReportItem report1_2_1 = new SheetReportItem("Test1_Sheet2_Step1", 1);       

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
		ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT, description.getName());
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

//      sheet1_1.addItem(report1_1_1);
//      sheet1_2.addItem(report1_2_1);
//      report.addItem(sheet1_1);
//      report.addItem(sheet1_2);
		return lines;
	}

	public Line readXmlFile(String filePath, ExchangeReport report)           
	{   
		Line line = null;
		File f = new File(filePath);
		ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE,Report.STATE.OK,f.getName());
		report.addItem(fileReportItem);
		try {
			InputStream stream;                                
			stream = new FileInputStream(filePath);

			line = netexFileReader.readInputStream(stream,report);
			stream.close();                               
//		} catch (java.text.ParseException ex) {
//			logger.error(ex.getMessage());            
//		} catch (IOException ex) {
//			logger.error(ex.getMessage());
//		} catch (EncodingException ex) {
//			logger.error(ex.getMessage());
//		} catch (EOFException ex) {
//			logger.error(ex.getMessage());
//		} catch (EntityException ex) {
//			logger.error(ex.getMessage());           
//		} catch (ParseException ex) {
//			logger.error(ex.getMessage());
//		} catch (XPathParseException ex) {
//			logger.error(ex.getMessage());
//		} catch (XPathEvalException ex) {
//			logger.error(ex.getMessage());
//		} catch (NavException ex) {
//			logger.error(ex.getMessage()); 
		} catch (Exception ex) {
			// report for save
			ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,ex.getLocalizedMessage());
			fileReportItem.addItem(errorItem);
			// log
			logger.error(ex.getMessage()); 
		}

		return line;
	}

	public List<Line> readZipFile(String filePath, ExchangeReport report) {
		List<Line> lines = new ArrayList<Line>();    
		Line line;
//        boolean ofType1 = false;
//        boolean ofType2 = false;
//        boolean someOk = false;

		ZipFile zip = null;
		try {
			zip = new ZipFile(filePath);
		} catch (IOException e) {
			// report for validation
//            ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL, filePath);
//            report1_1_1.addItem(detailReportItem);
			// report for save
			ReportItem fileErrorItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
			report.addItem(fileErrorItem);
			// log
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
				// report for validation
//                ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_warning", Report.STATE.WARNING,
//                        entryName);
//                report1_1_1.addItem(detailReportItem);
				// report for save
				ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_IGNORED,Report.STATE.OK,entryName);
				report.addItem(fileReportItem);
				// log
				logger.info("zip entry " + entryName + " bypassed ; not a XML file");
				continue;
			}

			logger.info("start import zip entry " + entryName);
			ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE,Report.STATE.OK,entryName);
			report.addItem(fileReportItem);
			try {
				InputStream stream = zip.getInputStream(entry);

				line = netexFileReader.readInputStream(stream,fileReportItem);
				stream.close();
				lines.add(line);
//                someOk = true;
//                report1_1_1.updateStatus(Report.STATE.OK);
//                report1_2_1.updateStatus(Report.STATE.OK);
			} catch (IOException e) {
				// report for validation
//                ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
//                        entryName);
//                report1_1_1.addItem(detailReportItem);
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
				continue;
			} catch (ExchangeRuntimeException e) {
//                if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode())) {
//                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                            entryName);
//                    report1_1_1.addItem(detailReportItem);
//                    report1_1_1.computeDetailItemCount();
//                    ofType1 = true;
//                } else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name())) {
//                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR,
//                            entryName);
//                    report1_2_1.addItem(detailReportItem);
//                    report1_1_1.updateStatus(Report.STATE.OK);
//                    ofType2 = true;
//                } else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name())) {
//                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, entryName);
//                    report1_2_1.addItem(detailReportItem);
//                    report1_1_1.updateStatus(Report.STATE.OK);
//                } else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name())) {
//                    ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
//                            entryName);
//                    report1_1_1.addItem(detailReportItem);
//                    ofType1 = true;
//                }
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
				continue;
			} catch (Exception e) {
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error(e.getMessage());
				logger.error(e.getLocalizedMessage());
				continue;
			}

		}

		if (zip != null)
		{
			try 
			{
				zip.close();
			} 
			catch (IOException e) 
			{
				logger.warn("fail to close zip "+e.getLocalizedMessage());
			}
		}

		return lines;
	}


}
