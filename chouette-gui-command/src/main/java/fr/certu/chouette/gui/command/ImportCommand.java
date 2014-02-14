/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.gui.command;

// import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.model.ImportTask;
import fr.certu.chouette.plugin.model.Referential;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
import fr.certu.chouette.plugin.validation.report.ValidationReport;
import fr.certu.chouette.service.geographic.IGeographicService;

/**
 * 
 * import command : 
 * -c import -Id ZZZ  
 * 
 */
@NoArgsConstructor
@Log4j
public class ImportCommand extends AbstractCommand
{

	@Getter @Setter private IDaoTemplate<Referential> referentialDao;;

	@Getter @Setter private IDaoTemplate<ImportTask> importDao;;

	@Setter private IGeographicService geographicService;

	/**
	 * import command :  
	 * -c import -id ZZZ 
	 * @param managers
	 * @param parameters
	 * @return
	 */
	public int executeImport(Session session,Map<String, List<String>> parameters)
	{
		Report importReport = null;
		ValidationReport validationReport = null;
		boolean save = true;
		List<Long> savedIds = new ArrayList<Long>();

		INeptuneManager<NeptuneIdentifiedObject> manager = managers.get("line");
		// check if import exists and accept unzip before call
		Long importId = Long.valueOf(getSimpleString(parameters,"id"));
		if (!importDao.exists(importId))
		{
			// error import not found
			log.error("import not found "+importId);
			return 1;
		}
		ImportTask importTask = importDao.get(importId);
		log.info("Import data for import id "+importId);
		log.info("  options : "+importTask.getParameters());

		startProcess(session, importTask);

		JSONObject options = importTask.getParameters();
		if (options == null) 
		{
			log.error("import without parameters "+importId);
			return 1;
		}
		String format = options.getString("format").toUpperCase();
		String inputFile = options.getString("file_path");
		if (!options.has("input_file"))
		{
			options.put("input_file", inputFile); // for import compatibility
			log.info("  options : "+options);
		}

		save = !options.getBoolean("no_save");

		Referential referential = referentialDao.get(importTask.getReferentialId());
		log.info("Referential "+importTask.getReferentialId());
		log.info("  name : "+referential.getName());
		log.info("  slug : "+referential.getSlug());

		String projectionType = null;
		if (referential.getProjectionType() != null && !referential.getProjectionType().isEmpty())
		{
			log.info("  projection type for import: "+referential.getProjectionType());
			projectionType = referential.getProjectionType();
			parameters.put("srid", Arrays.asList(new String[]{projectionType}));
		}
		// set projection for import (inactive if not set)
		geographicService.switchProjection(projectionType);

		boolean zipped = (inputFile.toLowerCase().endsWith(".zip"));

		try
		{
			List<FormatDescription> formats = manager.getImportFormats(null);
			FormatDescription description = null;

			for (FormatDescription formatDescription : formats)
			{
				if (formatDescription.getName().equalsIgnoreCase(format))
				{
					description=formatDescription;
					break;
				}
			}
			if (description == null)
			{
				throw new IllegalArgumentException("format "+format+" unavailable");
			}
			List<String> suffixes = new ArrayList<String>();
			for (ParameterDescription desc : description.getParameterDescriptions())
			{
				if (desc.getName().equalsIgnoreCase("inputfile"))
				{
					suffixes = desc.getAllowedExtensions();
					break;
				}
			}
			List<ParameterValue> values = populateParametersFromJSON(description,options,"inputfile","fileformat");
			if (zipped && description.isUnzipAllowed())
			{
				importReport = new ExchangeReport(ExchangeReport.KEY.IMPORT, format);
				ReportHolder importHolder = new ReportHolder();
				importHolder.setReport(importReport);
				validationReport = new ValidationReport();
				ReportHolder validationHolder = new ReportHolder();
				validationHolder.setReport(validationReport);

				int code = importZipEntries(session, save, savedIds, manager,
						importTask, format, inputFile, suffixes, values,
						importHolder, validationHolder) ;
				importReport = importHolder.getReport();
				validationReport = (ValidationReport) validationHolder.getReport();
                if (code > 0) return code; // import fails
			}
			else
			{
				SimpleParameterValue inputFileParam = new SimpleParameterValue("inputFile");
				inputFileParam.setFilepathValue(inputFile);
				values.add(inputFileParam);
				// surround with try catch 
				ReportHolder importHolder = new ReportHolder();
				ReportHolder validationHolder = new ReportHolder();
				List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,importHolder,validationHolder);

				if (importHolder.getReport() != null)
				{
					importReport = importHolder.getReport();
				}
				if (validationHolder.getReport() != null)
				{
					validationReport = (ValidationReport) validationHolder.getReport();
				}
				if (beans != null && !beans.isEmpty())
				{
					log.info("imported Lines "+beans.size());
					if (save)
					{
						saveBeans(manager, beans, savedIds, importReport);
					}
					else
					{
						for (NeptuneIdentifiedObject bean : beans) 
						{
							GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.NO_SAVE,Report.STATE.OK,bean.getName());
							importReport.addItem(item);
						}

					}
				}
			}
		}
		catch (Exception e)
		{
			// fill report with error
			String msg = e.getMessage();
			if (msg == null) msg = e.getClass().getName();
			log.error("import failed "+msg,e);
			if (importReport == null)
			{
				importReport = new ExchangeReport(ExchangeReport.KEY.IMPORT, format);
			}
			GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.EXCEPTION,Report.STATE.ERROR,msg);
			importReport.addItem(item);
			saveImportReports(session,importTask,importReport,validationReport);

			return 1;
		}

		// launch phase3 validation if required and possible
		if (save && !savedIds.isEmpty() && importTask.getCompilanceCheckTask() != null)
		{
			validateLevel3(manager, savedIds, importTask.getCompilanceCheckTask().getParameters(), validationReport);
		}

		saveImportReports(session,importTask,importReport,validationReport);
		return (0);

	}

	/**
	 * @param session
	 * @param save
	 * @param savedIds
	 * @param manager
	 * @param importTask
	 * @param format
	 * @param inputFile
	 * @param suffixes
	 * @param values
	 * @param importHolder
	 * @param validationHolder
	 * @throws ChouetteException
	 */
	private int importZipEntries(Session session, boolean save,
			List<Long> savedIds,
			INeptuneManager<NeptuneIdentifiedObject> manager,
			ImportTask importTask, String format, String inputFile,
			List<String> suffixes, List<ParameterValue> values,
			ReportHolder importHolder, ReportHolder validationHolder)
			throws ChouetteException 
			{
		SimpleParameterValue inputFileParam = new SimpleParameterValue("inputFile");
		values.add(inputFileParam);
		
		ReportHolder zipHolder = new ReportHolder();
		SharedImportedData sharedData = new SharedImportedData();
		UnsharedImportedData unsharedData = new UnsharedImportedData();
		SimpleParameterValue sharedDataParam = new SimpleParameterValue("sharedImportedData");
		sharedDataParam.setObjectValue(sharedData);
		values.add(sharedDataParam);
		SimpleParameterValue unsharedDataParam = new SimpleParameterValue("unsharedImportedData");
		unsharedDataParam.setObjectValue(unsharedData);
		values.add(unsharedDataParam);

		// unzip files , import and save contents 
		ZipFile zip = null;
		File temp = null;
		File tempRep = new File(FileUtils.getTempDirectory(),"massImport"+importTask.getId());
		if (!tempRep.exists()) tempRep.mkdirs();
		File zipFile = new File(inputFile);
		ReportItem zipReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE,Report.STATE.OK,zipFile.getName());
		try
		{

			zip = new ZipFile(inputFile);
			zipHolder.setReport(zipReportItem);
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();)
			{
				ZipEntry entry = entries.nextElement();

				if (entry.isDirectory())
				{
					File dir = new File(tempRep, entry.getName());
					dir.mkdirs();
					continue;
				}
				if (!FilenameUtils.isExtension(entry.getName().toLowerCase(),suffixes))
				{
					ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_IGNORED,Report.STATE.OK,FilenameUtils.getName(entry.getName()));
					zipReportItem.addItem(fileReportItem);
					log.info("entry "+entry.getName()+" ignored, unknown extension");
					continue;
				}
				InputStream stream = null;
				try
				{
					stream = zip.getInputStream(entry);
				}
				catch (IOException e)
				{
					ReportItem fileReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.FILE_ERROR,Report.STATE.WARNING,FilenameUtils.getName(entry.getName()));
					zipReportItem.addItem(fileReportItem);
					log.error("entry "+entry.getName()+" cannot read",e);
					continue;
				}
				byte[] bytes = new byte[4096];
				int len = stream.read(bytes);
				temp = new File(tempRep.getAbsolutePath()+"/"+entry.getName());
				FileOutputStream fos = new FileOutputStream(temp);
				while (len > 0)
				{
					fos.write(bytes, 0, len);
					len = stream.read(bytes);
				}
				fos.close();

				// import
				log.info("import file "+entry.getName());
				inputFileParam.setFilepathValue(temp.getAbsolutePath());
				List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,zipHolder,validationHolder);
				if (beans != null && !beans.isEmpty())
				{
					// save
					if (save)
					{
						saveBeans(manager, beans, savedIds, importHolder.getReport());
					}
					else
					{
						for (NeptuneIdentifiedObject bean : beans)
						{
							GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.NO_SAVE,Report.STATE.OK,bean.getName());
							importHolder.getReport().addItem(item);
						}

					}
				}
				temp.delete();
			}        
			try
			{
				zip.close();
			}
			catch (IOException e)
			{
				log.info("cannot close zip file");
			}
			importHolder.getReport().addItem(zipReportItem);
		}
		catch (IOException e)
		{
			log.error("IO error",e);
			ReportItem fileErrorItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
			zipReportItem.addItem(fileErrorItem);
			importHolder.getReport().addItem(zipReportItem);
			saveImportReports(session,importTask, importHolder.getReport(), validationHolder.getReport());
			return 1;
		}
		finally
		{
			try
			{
				FileUtils.deleteDirectory(tempRep);
			}
			catch (IOException e) 
			{
				log.warn("temporary directory "+tempRep.getAbsolutePath()+" could not be deleted");
			}
		}
		return 0;
	}

	/**
	 * @param manager
	 * @param savedIds
	 * @param importTask
	 * @param validationReport
	 */
	private void validateLevel3(
			INeptuneManager<NeptuneIdentifiedObject> manager,
			List<Long> savedIds, JSONObject parameters,
			ValidationReport validationReport) {
		
		if (parameters != null)
		{
			log.info("processing phase 3 validation on "+savedIds.size()+" lines");
			// launch validation on objects
			Filter filter = Filter.getNewInFilter("id", savedIds);
			try 
			{
				List<NeptuneIdentifiedObject> beans = manager.getAll(null, filter);
				if (beans == null || beans.isEmpty())
				{
					log.error("cannot read previously saved objects :"+Arrays.deepToString(savedIds.toArray()));

				}
				else
				{
					PhaseReportItem phaseReport = new PhaseReportItem(PhaseReportItem.PHASE.THREE);
					validationReport.addItem(phaseReport);
					manager.validate(null, beans, parameters, phaseReport, true);
				}
			} 
			catch (Exception e) 
			{
				log.error("cannot read previously saved objects",e);
			}
		}
	}

	/**
	 * @param manager
	 * @param beans
	 * @param savedIds
	 * @param importReport
	 */
	private void saveBeans(INeptuneManager<NeptuneIdentifiedObject> manager,
			List<NeptuneIdentifiedObject> beans, List<Long> savedIds,
			Report importReport) {
		for (NeptuneIdentifiedObject bean : beans) 
		{
			if (bean instanceof Line)
			{
				Line line = (Line) bean;
				checkProjection(line);
			}
			List<NeptuneIdentifiedObject> oneBean = new ArrayList<NeptuneIdentifiedObject>();
			oneBean.add(bean);
			try
			{
				log.info("save  Line "+bean.getName());
				manager.saveAll(null, oneBean, true, true);
				GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.SAVE_OK,Report.STATE.OK,bean.getName());
				importReport.addItem(item);
				savedIds.add(bean.getId());
			}
			catch (Exception e) 
			{
				log.error("save failed "+e.getMessage(),e);
				GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.SAVE_ERROR,Report.STATE.ERROR,bean.getName(),e.getMessage());
				importReport.addItem(item);
			}
		}
	}

	private void startProcess(Session session,ImportTask importTask) 
	{
		importTask.setStatus("processing");
		importTask.setUpdatedAt(Calendar.getInstance().getTime());
		if (importTask.getCompilanceCheckTask() != null)
		{
			importTask.getCompilanceCheckTask().setStatus("processing");
			importTask.getCompilanceCheckTask().setUpdatedAt(Calendar.getInstance().getTime());
		}
		importDao.save(importTask);
		session.flush();

	}


	private void saveImportReports(Session session,ImportTask importTask, Report ireport, Report vreport) 
	{
		// log.info("import report = "+ireport.toJSON().toString(3));
		ImportReportToJSONConverter converter = new ImportReportToJSONConverter(ireport);

		if (importTask.getCompilanceCheckTask() != null)
		{
			if (vreport != null && vreport.getItems() != null)
			{
				switch (vreport.getStatus())
				{
				case WARNING:
				case ERROR:
				case FATAL:
					importTask.getCompilanceCheckTask().setStatus("nok");
					break;
				case OK:
					importTask.getCompilanceCheckTask().setStatus("ok");
					break;
				case UNCHECK:
					importTask.getCompilanceCheckTask().setStatus("na");
					break;
				}
				importTask.getCompilanceCheckTask().addAllResults(((ValidationReport)vreport).toValidationResults());
				importTask.getCompilanceCheckTask().setUpdatedAt(Calendar.getInstance().getTime());
			}
			else
			{
				log.error("validation report null or empty");
			}
		}
		importTask.setResult(converter.toJSONObject());
		importTask.setUpdatedAt(Calendar.getInstance().getTime());
		importDao.save(importTask);
		session.flush();
	}

	private void checkProjection(Line line)
	{
		for (Route route : line.getRoutes())
		{
			for (StopPoint point : route.getStopPoints())
			{
				checkProjection(point.getContainedInStopArea());
			}
		}

	}

	private void checkProjection(StopArea area)
	{
		if (area == null) return;
		if (area.getAreaCentroid() != null)
		{
			AreaCentroid centroid = area.getAreaCentroid();
			if (centroid.getLongLatType() == null)
			{
				geographicService.convertToWGS84(area);
			}
		}
		checkProjection(area.getParent());

	}

	/**
	 * @param string
	 * @return
	 */
	private String getSimpleString(Map<String, List<String>> parameters,String key)
	{
		List<String> values = parameters.get(key);
		if (values == null) throw new IllegalArgumentException("parameter -"+key+" of String type is required");
		if (values.size() > 1) throw new IllegalArgumentException("parameter -"+key+" of String type must be unique");
		return values.get(0);
	}

	@SuppressWarnings("incomplete-switch")
	private List<ParameterValue> populateParametersFromJSON(FormatDescription description,JSONObject options,String ... excluded)
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		List<String> excludedParams = Arrays.asList(excluded);
		for (ParameterDescription desc : description.getParameterDescriptions())
		{
			String name = desc.getName();
			String key = name.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
			if (excludedParams.contains(key)) continue;
			// 


			if (!options.has(key))
			{
				if (desc.isMandatory())
				{
					throw new IllegalArgumentException("parameter -"+name+" is required");
				}
			}
			else
			{

				if (desc.isCollection())
				{
					JSONArray vals =  options.getJSONArray(key);
					ListParameterValue val = new ListParameterValue(name);
					switch (desc.getType())
					{
					case FILEPATH : val.fillFilepathList(vals); break;
					case STRING : val.fillStringList(vals); break;
					case FILENAME : val.fillFilenameList(vals); break;
					default:
						throw new IllegalArgumentException("parameter -"+name+" unknown type "+desc.getType());
					}
					values.add(val);
				}
				else
				{
					//					if (options.optJSONObject(key) == null) // will be a JSONArray
					//					{
					//						throw new IllegalArgumentException("parameter -"+name+" must be unique");
					//					}

					SimpleParameterValue val = new SimpleParameterValue(name);
					switch (desc.getType())
					{
					case FILEPATH : val.setFilepathValue(options.getString(key)); break;
					case STRING : val.setStringValue(options.getString(key)); break;
					case FILENAME : val.setFilenameValue(options.getString(key)); break;
					case BOOLEAN : val.setBooleanValue(!options.getString(key).equals("0")); break;
					case INTEGER : val.setIntegerValue(options.getLong(key)); break;
					case DATE : val.setDateValue(toCalendar(options.getString(key)));break;
					}
					values.add(val);

				}
			}
		}
		return values;      
	}

	/**
	 * convert date string to calendar
	 * @param simpleval
	 * @return
	 */
	private Calendar toCalendar(String simpleval)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			Date d = sdf.parse(simpleval);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		}
		catch (ParseException e)
		{
			log.error("invalid date format : "+ simpleval+" yyyy-MM-dd expected");
			throw new RuntimeException("invalid date format : "+ simpleval+" yyyy-MM-dd expected");
		}

	}


}
