/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.gui.command;

// import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.model.ExportLogMessage;
import fr.certu.chouette.plugin.model.GuiExport;
import fr.certu.chouette.plugin.model.Referential;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.service.geographic.IGeographicService;

/**
 * 
 * import command : 
 * -c import -o line -inputFile YYYY -importId ZZZ  
 * 
 * export command : 
 * selected objects
 * -c export -o line -format XXX -outputFile YYYY -exportId ZZZ -id list_of_id_separated_by_commas ...
 * all objects 
 * -c export -o line -format XXX -outputFile YYYY -exportId ZZZ 
 * dependency criteria : sample for all lines of one network
 * -c export -o network -format XXX -outputFile YYYY -exportId ZZZ -id list_of_network_id_separated_by_commas
 * 
 * validate command : 
 * from neptune file :
 * -c validate -o line -inputFile YYYY  [-fileFormat TTT] -validationId ZZZ 
 * 
 * from database : 
 * -c validate -o line|network|company -validationId ZZZ [-id list_of_ids_separated_by_commas]
 * 
 */
@NoArgsConstructor
public class ExportCommand
{

	private static final Logger logger = Logger.getLogger(ExportCommand.class);
	public static ClassPathXmlApplicationContext applicationContext;

	@Getter @Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

	@Getter @Setter private IDaoTemplate<Referential> referentialDao;;

	@Getter @Setter private IDaoTemplate<GuiExport> exportDao;

	@Getter @Setter private IDaoTemplate<ExportLogMessage> exportLogMessageDao;

	@Setter private IGeographicService geographicService;

	private Map<String,List<String>> globals = new HashMap<String, List<String>>();


	/**
	 * export command : 
	 * selected objects
	 * -c export -o line -format XXX -outputFile YYYY -exportId ZZZ -id list_of_id_separated_by_commas ...
	 * all objects 
	 * -c export -o line -format XXX -outputFile YYYY -exportId ZZZ 
	 * dependency criteria : sample for all lines of one network
	 * -c export -o network -format XXX -outputFile YYYY -exportId ZZZ -id list_of_network_id_separated_by_commas
	 * 
	 * @param manager
	 * @param parameters
	 * @return 
	 */
	public int executeExport(EntityManager session,
			INeptuneManager<NeptuneIdentifiedObject> manager,
			Map<String, List<String>> parameters) 
	{
		String format = getSimpleString(parameters,"format");
		Long exportId = Long.valueOf(getSimpleString(parameters,"exportid"));

		if (!exportDao.exists(exportId))
		{
			// error export not found
			logger.error("export not found "+exportId);
			return 1;
		}
		GuiExport guiExport = exportDao.get(exportId);
		logger.info("Export data for export id "+exportId);
		logger.info("  type : "+guiExport.getType());
		logger.info("  options : "+guiExport.getOptions());
		logger.info("  references type : "+guiExport.getReferencesType());
		logger.info("  reference ids : "+guiExport.getReferenceIds());

		startProcess(session,guiExport);
		
		Referential referential = guiExport.getReferential();
		logger.info("Referential "+referential.getId());
		logger.info("  name : "+referential.getName());
		logger.info("  slug : "+referential.getSlug());
		logger.info("  projection type : "+referential.getProjectionType());

		String projectionType = null;
		if (referential.getProjectionType() != null && !referential.getProjectionType().isEmpty())
		{
			logger.info("  projection type for export: "+referential.getProjectionType());
			projectionType = referential.getProjectionType();
		}
		// set projection for export (inactive if not set)
		geographicService.switchProjection(projectionType);

		List<Report> reports = new ArrayList<Report>();

		String[] ids = new String[0];
		if (parameters.containsKey("id"))
			ids = getSimpleString(parameters,"id").split(",");
		try
		{
			List<FormatDescription> formats = manager.getExportFormats(null);
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
				String objectName = getActiveObject(parameters);
				List<String> objects = parameters.get("object");
				objects.clear();
				objects.add("line");
				manager = getManager(parameters);
				parameters.remove("id");
				List<String> filter = new ArrayList<String>();
				if (objectName.equals("network"))
				{
					filter.add("ptNetwork.id");
				}
				else if (objectName.equals("company"))
				{
					filter.add("company.id");
				}
				else
				{
					throw new IllegalArgumentException("format "+format+" unavailable for "+objectName);
				}
				if (ids != null && ids.length > 0)
				{
					filter.add("in");
					filter.addAll(Arrays.asList(ids));
					parameters.put("filter", filter);
				}

				return executeExport(session,manager,parameters);

			}

			List<ParameterValue> values = populateParameters(description, parameters);

			ReportHolder holder = new ReportHolder();
			List<NeptuneIdentifiedObject> beans = executeGet(manager, parameters);
			manager.doExport(null, beans, format, values, holder );
			if (holder.getReport() != null)
			{
				Report r = holder.getReport();
				reports.add(r);
			}
		}
		catch (Exception e)
		{
			logger.error("export failed "+e.getMessage(),e);
			GuiReport errorReport = new GuiReport("EXPORT_ERROR",Report.STATE.ERROR);
			GuiReportItem item = new GuiReportItem(GuiReportItem.KEY.EXCEPTION,Report.STATE.ERROR,e.getMessage());
			errorReport.addItem(item);
			reports.add(errorReport);
			saveExportReports(guiExport,format,reports);
			return 1;
		}
		return saveExportReports(guiExport,format,reports);

	}




	/**
	 * @param manager
	 * @param parameters 
	 * @return 
	 * @throws ChouetteException
	 */
	private List<NeptuneIdentifiedObject> executeGet(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters)
			throws ChouetteException
			{
		Filter filter = null;
		if (parameters.containsKey("id"))
		{
			String[] sids = getSimpleString(parameters,"id").split(",");
			List<Long> ids = new ArrayList<Long>();

			for (String id : sids)
			{
				ids.add(Long.valueOf(id));
			}
			filter = Filter.getNewInFilter("id", ids);
		}
		else if (parameters.containsKey("objectid"))
		{
			List<String> sids = parameters.get("objectid");
			filter = Filter.getNewInFilter("objectId", sids);
		}
		else if (parameters.containsKey("filter"))
		{
			List<String> filterArgs = parameters.get("filter");
			if (filterArgs.size() < 2) 
			{
				throw new IllegalArgumentException("invalid syntax for filter ");
			}
			String filterKey = filterArgs.get(0);
			String filterOp = filterArgs.get(1);
			if (filterArgs.size() == 2)
			{
				if (filterOp.equalsIgnoreCase("null") || filterOp.equalsIgnoreCase("isnull"))
				{
					filter = Filter.getNewIsNullFilter(filterKey);
				}
				else 
				{
					throw new IllegalArgumentException(filterOp+" : invalid syntax or not yet implemented");
				}
			}
			else if (filterOp.equalsIgnoreCase("in"))
			{
				if (filterKey.endsWith(".id"))
				{
					List<String> values = filterArgs.subList(2, filterArgs.size());
					List<Long> ids = new ArrayList<Long>();

					for (String id : values)
					{
						ids.add(Long.valueOf(id));
					}
					filter = Filter.getNewInFilter(filterKey, ids );
				}
				else
				{
					List<String> values = filterArgs.subList(2, filterArgs.size());
					filter = Filter.getNewInFilter(filterKey, values );
				}
			}
			else if (filterArgs.size() == 3)
			{
				String value = filterArgs.get(2);
				if (filterOp.equalsIgnoreCase("eq") || filterOp.equals("="))
				{
					filter = Filter.getNewEqualsFilter(filterKey, value);
				}
				else if (filterOp.equalsIgnoreCase("like"))
				{
					filter = Filter.getNewLikeFilter(filterKey, value);
				}
				else 
				{
					throw new IllegalArgumentException(filterOp+" : invalid syntax or not yet implemented");
				}
			}
			else
			{
				throw new IllegalArgumentException(filterOp+" : invalid syntax or not yet implemented");
			}
		}
		else
		{
			filter = Filter.getNewEmptyFilter();
		}

		if (parameters.containsKey("orderby"))
		{
			List<String> orderFields = parameters.get("orderby");

			boolean desc = getBoolean(parameters,"desc");

			if (desc)
			{
				for (String field : orderFields)
				{
					filter.addOrder(FilterOrder.desc(field));
				}
			}
			else
			{
				for (String field : orderFields)
				{
					filter.addOrder(FilterOrder.asc(field));
				}
			}
		}

		String limit = getSimpleString(parameters, "limit","none");
		if (!limit.equalsIgnoreCase("none"))
		{
			filter.addLimit(Integer.parseInt(limit));
		}

		List<NeptuneIdentifiedObject> beans = manager.getAll(null, filter);

		System.out.println("beans count = "+beans.size());
		return beans;
			}



	/**
	 * @param parameters
	 * @return
	 */
	private String getActiveObject(Map<String, List<String>> parameters) 
	{
		String object = null;
		try
		{
			object = getSimpleString(parameters,"object").toLowerCase();
		}
		catch (IllegalArgumentException e)
		{
			object = getSimpleString(globals,"object","xxx").toLowerCase();
		}
		if (!managers.containsKey(object))
		{
			return "unknown object";
		}
		return object;
	}

	/**
	 * @param string
	 * @return
	 */
	private boolean getBoolean(Map<String, List<String>> parameters,String key)
	{
		List<String> values = parameters.get(key);
		if (values == null) return false;
		if (values.size() > 1) throw new IllegalArgumentException("parameter -"+key+" of boolean type must be unique");
		return Boolean.parseBoolean(values.get(0));
	}

	/**
	 * @param parameters
	 * @return
	 */
	private INeptuneManager<NeptuneIdentifiedObject> getManager(Map<String, List<String>> parameters) 
	{
		String object = null;
		try
		{
			object = getSimpleString(parameters,"object").toLowerCase();
			List<String> objects = new ArrayList<String>();
			objects.add(object);
			globals.put("object", objects);
		}
		catch (IllegalArgumentException e)
		{
			object = getSimpleString(globals,"object").toLowerCase();
		}
		INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(object);
		if (manager == null)
		{
			throw new IllegalArgumentException("unknown object "+object+ ", only "+Arrays.toString(managers.keySet().toArray())+" are managed");
		}
		return manager;
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

	/**
	 * @param string
	 * @return
	 */
	private String getSimpleString(Map<String, List<String>> parameters,String key,String defaultValue)
	{
		List<String> values = parameters.get(key);
		if (values == null) return defaultValue;
		if (values.size() > 1) throw new IllegalArgumentException("parameter -"+key+" of String type must be unique");
		return values.get(0);
	}



	@SuppressWarnings("incomplete-switch")
	private List<ParameterValue> populateParameters(FormatDescription description,Map<String, List<String>> parameters,String ... excluded)
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		List<String> excludedParams = Arrays.asList(excluded);
		for (ParameterDescription desc : description.getParameterDescriptions())
		{
			String name = desc.getName();
			String key = name.toLowerCase();
			if (excludedParams.contains(key)) continue;
			List<String> vals = parameters.get(key);
			if (vals == null)
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
					ListParameterValue val = new ListParameterValue(name);
					switch (desc.getType())
					{
					case FILEPATH : val.setFilepathList(vals); break;
					case STRING : val.setStringList(vals); break;
					case FILENAME : val.setFilenameList(vals); break;
					default:
						throw new IllegalArgumentException("parameter -"+name+" unknown type "+desc.getType());
					}
					values.add(val);
					logger.debug("prepare list parameter "+name);
				}
				else
				{
					if (vals.size() != 1)
					{
						throw new IllegalArgumentException("parameter -"+name+" must be unique");
					}
					String simpleval = vals.get(0);

					SimpleParameterValue val = new SimpleParameterValue(name);
					switch (desc.getType())
					{
					case FILEPATH : val.setFilepathValue(simpleval); break;
					case STRING : val.setStringValue(simpleval); break;
					case FILENAME : val.setFilenameValue(simpleval); break;
					case BOOLEAN : val.setBooleanValue(Boolean.parseBoolean(simpleval)); break;
					case INTEGER : val.setIntegerValue(Long.parseLong(simpleval)); break;
					case DATE : val.setDateValue(toCalendar(simpleval));break;
					}
					values.add(val);
					logger.debug("prepare simple parameter "+name);

				}
			}
		}
		return values;      
	}

	private void startProcess(EntityManager session,GuiExport guiExport) 
	{
		guiExport.setStatus("processing");
		guiExport.setUpdatedAt(Calendar.getInstance().getTime());
		exportDao.save(guiExport);
		exportDao.flush();

	}

	private int saveExportReport(GuiExport export, String format,Report report,int position)
	{
		String prefix = report.getOriginKey();
		if (prefix == null || report instanceof ReportItem) prefix = ((ReportItem) report).getMessageKey();
		prefix = format+prefix;
		ExportLogMessage message = new ExportLogMessage(export,format,report,position++);
		exportLogMessageDao.save(message);
		if (report.getItems() != null)
		{
			for (ReportItem item : report.getItems())
			{
				position = saveExportReportItem(export,format,item,prefix,position);
			}
		}
		return position;
	}

	private int saveExportReportItem(GuiExport export, String format,ReportItem item, String prefix, int position)
	{
		ExportLogMessage message = new ExportLogMessage(export,format,item,prefix,position++);
		exportLogMessageDao.save(message);
		if (item.getItems() != null)
		{
			String subPrefix = prefix+"|"+format+item.getMessageKey();
			for (ReportItem child : item.getItems())
			{
				position = saveExportReportItem(export,format,child,subPrefix,position);
			}
		}
		return position;
	}

	private int saveExportReports(GuiExport export, String format,List<Report> reports)
	{
		int position = 1;
		int retVal = 1;
		Filter filter = Filter.getNewEqualsFilter("parent", export);
		List<ExportLogMessage> messages = exportLogMessageDao.select(filter);
		if (messages != null)
		{
			for (ExportLogMessage message : messages)
			{
				if (message.getPosition() >= position)
					position = message.getPosition() + 1;
			}
		}
		for (Report report : reports)
		{
			if (report instanceof GuiReport) 
				position = saveExportReport(export,"",report,position);
			else
				position = saveExportReport(export,format+"_",report,position);
			// return ok when at least one report is ok or warning
			if (report.getStatus().ordinal() <= Report.STATE.WARNING.ordinal()) retVal = 0;
		}
		return retVal;

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
			logger.error("invalid date format : "+ simpleval+" yyyy-MM-dd expected");
			throw new RuntimeException("invalid date format : "+ simpleval+" yyyy-MM-dd expected");
		}

	}


}
