/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.io.File;
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
import org.exolab.castor.xml.ValidationException;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.IImportPlugin;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeException;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import fr.certu.chouette.plugin.report.DetailReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.report.SheetReportItem;
import fr.certu.chouette.plugin.validation.ValidationClassReportItem;

/**
 * Import Plugin for Neptune Line format
 * <p/>
 * accept XML or ZIP format<br/>
 * for ZIP format, will return separate instances of shared objects , one in
 * each Line hierarchy
 * 
 */
public class XMLNeptuneImportLinePlugin implements IImportPlugin<Line>
{
	private static final Logger logger            = Logger.getLogger(XMLNeptuneImportLinePlugin.class);
	/**
	 * transcoder from CASTOR format to CHOUETTE internal format
	 */
	@Getter
	@Setter
	private NeptuneConverter    converter;
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
	private SheetReportItem          sheet1_1;
	/**
	 * warning and error reporting container
	 */
	private SheetReportItem          sheet1_2;
	/**
	 * file format reporting
	 */
	private SheetReportItem     report1_1_1;
	/**
	 * data format reporting
	 */
	private SheetReportItem     report1_2_1;

	/**
	 * Constructor
	 */
	public XMLNeptuneImportLinePlugin()
	{
		description = new FormatDescription(this.getClass().getName());
		description.setName("NEPTUNE");
		description.setUnzipAllowed(true);
		List<ParameterDescription> params = new ArrayList<ParameterDescription>();
		{
			ParameterDescription param = new ParameterDescription("inputFile", ParameterDescription.TYPE.FILEPATH, false, true);
			param.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("fileFormat", ParameterDescription.TYPE.STRING, false,
					"file extension");
			param.setAllowedExtensions(Arrays.asList(new String[] { "xml", "zip" }));
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("validate", ParameterDescription.TYPE.BOOLEAN, false,
					"false");
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("optimizeMemory", ParameterDescription.TYPE.BOOLEAN,false , "false");
			params.add(param);
		}
		{
			ParameterDescription param = new ParameterDescription("reportForSave", ParameterDescription.TYPE.BOOLEAN, false,
					"false");
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
	 * fr.certu.chouette.plugin.exchange.IImportPlugin#doImport(java.util.List,
	 * fr.certu.chouette.plugin.report.ReportHolder)
	 */
	@Override
	public List<Line> doImport(List<ParameterValue> parameters, ReportHolder reportContainer) throws ChouetteException
	{

		String filePath = null;
		boolean validate = false;
		boolean reportForSave = false;
		String extension = "file extension";
		boolean optimizeMemory = false;

		for (ParameterValue value : parameters)
		{
			if (value instanceof SimpleParameterValue)
			{
				SimpleParameterValue svalue = (SimpleParameterValue) value;
				if (svalue.getName().equalsIgnoreCase("inputFile"))
				{
					filePath = svalue.getFilepathValue();
				}
				else if (svalue.getName().equalsIgnoreCase("fileFormat"))
				{
					extension = svalue.getStringValue().toLowerCase();
				}
				else if (svalue.getName().equalsIgnoreCase("validate"))
				{
					validate = svalue.getBooleanValue().booleanValue();
				}
				else if (svalue.getName().equalsIgnoreCase("optimizeMemory")) 
				{
					optimizeMemory = svalue.getBooleanValue().booleanValue();
				}
				else if (svalue.getName().equalsIgnoreCase("reportForSave"))
				{
					reportForSave = svalue.getBooleanValue().booleanValue();
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

		if (extension.equals("file extension"))
		{
			extension = FilenameUtils.getExtension(filePath).toLowerCase();
		}
		if (!allowedExtensions.contains(extension))
		{
			logger.error("invalid argument inputFile " + filePath + ", allowed format : "
					+ Arrays.toString(allowedExtensions.toArray()));
			throw new IllegalArgumentException("invalid file type : " + extension);
		}

		Report report = new ExchangeReport(ExchangeReport.KEY.IMPORT, description.getName());
		Report category1 = new ValidationClassReportItem(ValidationClassReportItem.CLASS.ONE);
		sheet1_1 = new SheetReportItem("Test1_Sheet1", 1);
		sheet1_2 = new SheetReportItem("Test1_Sheet2", 2);
		report1_1_1 = new SheetReportItem("Test1_Sheet1_Step1", 1);
		report1_2_1 = new SheetReportItem("Test1_Sheet2_Step1", 1);

		List<Line> lines = null;

		if (extension.equals("xml"))
		{
			// simple file processing
			File f = new File(filePath);
			ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE,Report.STATE.OK,f.getName());
			logger.info("start import simple file " + filePath);
			Line line = processFileImport(filePath, validate, fileReportItem, optimizeMemory);
			if (line != null)
			{
				lines = new ArrayList<Line>();
				lines.add(line);
			}
			report.addItem(fileReportItem);
		}
		else
		{
			// zip file processing
			logger.info("start import zip file " + filePath);
			File f = new File(filePath);
			ReportItem zipReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE,Report.STATE.OK,f.getName());
			lines = processZipImport(filePath, validate, zipReportItem,optimizeMemory);
			report.addItem(zipReportItem);
		}
		logger.info("import terminated");
		if (!reportForSave)
		{
			// must be set at last to merge sub items
			sheet1_1.addItem(report1_1_1);
			sheet1_2.addItem(report1_2_1);
			category1.addItem(sheet1_1);
			category1.addItem(sheet1_2);
			reportContainer.setReport(category1);
		}
		else
		{
			reportContainer.setReport(report);
		}
		return lines;
	}

	/**
	 * import ZipFile
	 * 
	 * @param filePath
	 *           path to zip File
	 * @param validate
	 *           process XML and XSD format validation
	 * @param report
	 *           report to fill
	 * @param optimizeMemory 
	 * @return list of loaded lines
	 */
	private List<Line> processZipImport(String filePath, boolean validate, Report report, boolean optimizeMemory)
	{
		NeptuneFileReader reader = new NeptuneFileReader();
		ZipFile zip = null;
		try
		{
			zip = new ZipFile(filePath);
		}
		catch (IOException e)
		{
			// report for validation
			ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL, filePath);
			report1_1_1.addItem(detailReportItem);
			// report for save
			ReportItem fileErrorItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
            report.addItem(fileErrorItem);
			// log
			logger.error("zip import failed (cannot open zip)" + e.getLocalizedMessage());
			return null;
		}
		List<Line> lines = new ArrayList<Line>();
		boolean ofType1 = false;
		boolean ofType2 = false;
		boolean someOk = false;
		SharedImportedData sharedData = new SharedImportedData();
		for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
		{
			ZipEntry entry = entries.nextElement();

			// ignore directory without warning
			if (entry.isDirectory())
				continue;

			String entryName = entry.getName();
			if (!FilenameUtils.getExtension(entryName).toLowerCase().equals("xml"))
			{
				// report for validation
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step0_warning", Report.STATE.WARNING,
						entryName);
				report1_1_1.addItem(detailReportItem);
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
			try
			{
				InputStream stream =  zip.getInputStream(entry);
				stream.close();
			}
			catch (IOException e)
			{
				// report for validation
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step2_error", Report.STATE.ERROR,
						entryName);
				report1_1_1.addItem(detailReportItem);
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error("zip entry " + entryName + " import failed (get entry)" + e.getLocalizedMessage());
				continue;
			}
			ChouettePTNetworkTypeType rootObject = null;
			try
			{
				rootObject = reader.read(zip, entry, validate);
				someOk = true;
				report1_1_1.updateStatus(Report.STATE.OK);
				report1_2_1.updateStatus(Report.STATE.OK);
			}
			catch (ExchangeRuntimeException e)
			{
				if (ExchangeExceptionCode.INVALID_XML_FILE.name().equals(e.getCode()))
				{
					ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
							entryName);
					report1_1_1.addItem(detailReportItem);
					ofType1 = true;
				}
				else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
				{
					ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR,
							entryName);
					report1_2_1.addItem(detailReportItem);
					report1_1_1.updateStatus(Report.STATE.OK);
					ofType2 = true;
				}
				else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name()))
				{
					ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, entryName);
					report1_2_1.addItem(detailReportItem);
					report1_1_1.updateStatus(Report.STATE.OK);
					ofType2 = true;
				}
				else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
				{
					ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
							entryName);
					report1_1_1.addItem(detailReportItem);
					ofType1 = true;
				}
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error("zip entry " + entryName + " import failed (read XML)" + e.getLocalizedMessage());
				continue;
			}
			catch (Exception e)
			{
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error(e.getLocalizedMessage());
				continue;
			}
			try
			{
				Line line = processImport(rootObject, validate, fileReportItem, entryName,sharedData,optimizeMemory);

				if (line != null)
				{
					lines.add(line);
					report1_1_1.updateStatus(Report.STATE.OK);
					report1_2_1.updateStatus(Report.STATE.OK);
				}
				else
				{
					logger.error("zip entry " + entryName + " import failed (build model)");
				}

			}
			catch (ExchangeException e)
			{
				report1_1_1.updateStatus(Report.STATE.ERROR);
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				fileReportItem.addItem(errorItem);
				// log
				logger.error("zip entry " + entryName + " import failed (convert to model)" + e.getLocalizedMessage());
				continue;
			}
			logger.info("zip entry imported");
		}
		try
		{
			zip.close();
		}
		catch (IOException e)
		{
			logger.info("cannot close zip file");
		}
		if (!ofType1 && ofType2)
		{
			report1_1_1.updateStatus(Report.STATE.OK);
		}
		else if (ofType1 && !ofType2 && !someOk)
		{
			report1_2_1.updateStatus(Report.STATE.UNCHECK);
		}

		report1_1_1.computeDetailItemCount();
		report1_2_1.computeDetailItemCount();
		if (lines.size() == 0)
		{
			logger.error("zip import failed (no valid entry)");
			return null;
		}
		else
		{
			report1_1_1.updateStatus(Report.STATE.OK);
		}
		return lines;
	}

	/**
	 * import simple Neptune file
	 * 
	 * @param filePath
	 *           path to File
	 * @param validate
	 *           process XML and XSD format validation
	 * @param report
	 *           report to fill
	 * @param optimizeMemory 
	 * @return loaded line
	 * @throws ExchangeException
	 */
	private Line processFileImport(String filePath, boolean validate, Report report, boolean optimizeMemory) throws ExchangeException
	{
		ChouettePTNetworkTypeType rootObject = null;
		NeptuneFileReader reader = new NeptuneFileReader();
		try
		{
			rootObject = reader.read(filePath, validate);
			report1_1_1.updateStatus(Report.STATE.OK);
			report1_2_1.updateStatus(Report.STATE.OK);
		}
		catch (ExchangeRuntimeException e)
		{
			if (e.getCode().equals(ExchangeExceptionCode.INVALID_XML_FILE.name()))
			{
				logger.error("INVALID_XML_FILE " + filePath);
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
				report1_1_1.addItem(detailReportItem);
				report1_1_1.computeDetailItemCount();
				report1_2_1.updateStatus(Report.STATE.UNCHECK);
			}
			else if (e.getCode().equals(ExchangeExceptionCode.INVALID_NEPTUNE_FILE.name()))
			{
				logger.error("INVALID_NEPTUNE_FILE " + filePath);
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_error", Report.STATE.ERROR, filePath);
				report1_2_1.addItem(detailReportItem);
				report1_1_1.updateStatus(Report.STATE.OK);
			}
			else if (e.getCode().equals(ExchangeExceptionCode.INVALID_ENCODING.name()))
			{
				logger.error("INVALID_NEPTUNE_FILE " + filePath);
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet2_Step1_encoding", Report.STATE.ERROR, filePath);
				report1_2_1.addItem(detailReportItem);
				report1_1_1.updateStatus(Report.STATE.OK);
			}
			else if (e.getCode().equals(ExchangeExceptionCode.FILE_NOT_FOUND.name()))
			{
				logger.error("FILE_NOT_FOUND " + filePath);
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR, filePath);
				report1_1_1.addItem(detailReportItem);
				report1_2_1.updateStatus(Report.STATE.UNCHECK);
			}
			// report for save
			ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
			report.addItem(errorItem);
			// log
			logger.error("File " + filePath + " import failed (read XML) [" + e.getLocalizedMessage() + "]");
			return null;
		}
		catch (Exception e)
		{
			// ReportItem detailReportItem = new
			// DetailReportItem("Test1_Sheet1_Step0_fatal", Report.STATE.FATAL,
			// filePath);
			// report1_1.addItem(detailReportItem);
			// report1_1.computeDetailItemCount();
			// logger.error("import failed ((read XML)) " +
			// e.getLocalizedMessage());

			// report for save
			ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
			report.addItem(errorItem);
			// log
			logger.error(e.getLocalizedMessage());
			return null;
		}
		Line line = processImport(rootObject, validate, report, filePath,new SharedImportedData(),optimizeMemory);
		if (line == null)
		{
			logger.error("import failed (build model)");
			// report.setStatus(Report.STATE.FATAL);
			// report1_2.updateStatus(Report.STATE.FATAL);
		}
		else
		{
			report1_1_1.updateStatus(Report.STATE.OK);
		}
		return line;
	}

	/**
	 * process conversion between CASTOR format and CHOUETTE internal format
	 * 
	 * @param rootObject
	 *           container for CASTOR loaded XML file
	 * @param validate
	 *           validate on XSD rules
	 * @param report
	 *           report to fill
	 * @param entryName
	 *           file name for logger purpose
	 * @return builded line
	 * @throws ExchangeException
	 */
	private Line processImport(ChouettePTNetworkTypeType rootObject, boolean validate, Report report, String entryName,SharedImportedData sharedData,boolean optimizeMemory)
			throws ExchangeException
			{
		if (validate)
		{
			try
			{
				rootObject.validate();
			}
			catch (ValidationException e)
			{
				// report for validation
				logger.error("import failed for " + entryName + " : Castor validation "+e.getLocalizedMessage());
				ReportItem detailReportItem = new DetailReportItem("Test1_Sheet1_Step1_error", Report.STATE.ERROR,
						entryName);
				report1_2_1.addItem(detailReportItem);
				
				// report for save
				ReportItem errorItem = new ExchangeReportItem(ExchangeReportItem.KEY.VALIDATION_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
				report.addItem(errorItem);
             				
				Throwable t = e.getCause();
				while (t != null)
				{
					// report for save
					ReportItem causeItem = new ExchangeReportItem(ExchangeReportItem.KEY.VALIDATION_CAUSE,Report.STATE.ERROR,t.getLocalizedMessage());
					errorItem.addItem(causeItem);
					// log
					logger.error(t.getLocalizedMessage());
					
					// ReportItem detail2 = new
					// DetailReportItem("",Report.STATE.ERROR,
					// t.getLocalizedMessage());
					// report1_2.addItem(detail2);
					t = t.getCause();
				}
				return null;
			}
		}
		// report for validation
		report1_2_1.computeDetailItemCount();
		report1_2_1.updateStatus(Report.STATE.OK);

		// report for save
		ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.IMPORTED_LINE, Report.STATE.OK);
		report.addItem(item);
		
		// process Line
		ModelAssembler modelAssembler = new ModelAssembler(item);

		Line line = converter.extractLine(rootObject, item);
		// should be made in converter.extractLine
		item.addMessageArgs(line.getPublishedName());
		
		modelAssembler.setLine(line);
		modelAssembler.setRoutes(converter.extractRoutes(rootObject, item));
		modelAssembler.setCompanies(converter.extractCompanies(rootObject, item,sharedData));
		modelAssembler.setPtNetwork(converter.extractPTNetwork(rootObject, item,sharedData));
		modelAssembler.setJourneyPatterns(converter.extractJourneyPatterns(rootObject, item));
		modelAssembler.setPtLinks(converter.extractPTLinks(rootObject, item));
		modelAssembler.setVehicleJourneys(converter.extractVehicleJourneys(rootObject, item,optimizeMemory));
		modelAssembler.setStopPoints(converter.extractStopPoints(rootObject, item));
		modelAssembler.setStopAreas(converter.extractStopAreas(rootObject, item,sharedData));
		modelAssembler.setAreaCentroids(converter.extractAreaCentroids(rootObject, item,sharedData));
		modelAssembler.setConnectionLinks(converter.extractConnectionLinks(rootObject, item,sharedData));
		modelAssembler.setTimetables(converter.extractTimetables(rootObject, item,sharedData));
		modelAssembler.setAccessLinks(converter.extractAccessLinks(rootObject, item,sharedData));
		modelAssembler.setAccessPoints(converter.extractAccessPoints(rootObject, item,sharedData));
		modelAssembler.setGroupOfLines(converter.extractGroupOfLines(rootObject, item,sharedData));
		modelAssembler.setFacilities(converter.extractFacilities(rootObject, item,sharedData));
		modelAssembler.setTimeSlots(converter.extractTimeSlots(rootObject, item,sharedData));
		modelAssembler.setRoutingConstraints(converter.extractRoutingConstraints(rootObject, item));
		modelAssembler.connect();
		// report objects count
		{
			ExchangeReportItem countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ROUTE_COUNT,Report.STATE.OK,modelAssembler.getRoutes().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.JOURNEY_PATTERN_COUNT,Report.STATE.OK,modelAssembler.getJourneyPatterns().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.VEHICLE_JOURNEY_COUNT,Report.STATE.OK,modelAssembler.getVehicleJourneys().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.STOP_AREA_COUNT,Report.STATE.OK,modelAssembler.getStopAreas().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.CONNECTION_LINK_COUNT,Report.STATE.OK,modelAssembler.getConnectionLinks().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.ACCES_POINT_COUNT,Report.STATE.OK,modelAssembler.getAccessPoints().size());
            item.addItem(countItem);
			countItem = new ExchangeReportItem(ExchangeReportItem.KEY.TIME_TABLE_COUNT,Report.STATE.OK,modelAssembler.getTimetables().size());
            item.addItem(countItem);
		}

		rootObject.toString();

		return line;
			}
}
