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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.model.ExportLogMessage;
import fr.certu.chouette.plugin.model.GuiExport;
import fr.certu.chouette.plugin.model.GuiImport;
import fr.certu.chouette.plugin.model.GuiValidation;
import fr.certu.chouette.plugin.model.Organisation;
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
public class Command
{

	private static final Logger logger = Logger.getLogger(Command.class);
	public static ClassPathXmlApplicationContext applicationContext;

	/**
	 *
	 */
	public static void printHelp()
	{
		ResourceBundle bundle = null;
		try
		{
			bundle = ResourceBundle.getBundle(Command.class.getName(),locale);
		}
		catch (MissingResourceException e1)
		{
			try
			{
				bundle = ResourceBundle.getBundle(Command.class.getName());
			}
			catch (MissingResourceException e2)
			{
				System.out.println("missing help resource");
				return;
			}
		}

		printBloc(bundle,"Header","");

		printBloc(bundle,"Option","   ");

		System.out.println("");

		String[] commands = getHelpString(bundle,"Commands").split(" ");
		for (String command : commands) 
		{
			printCommandDetail(bundle,command,"   ");
			System.out.println("");
		}

		printBloc(bundle,"Footer","");
	}

	@Getter @Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;


	@Setter private IDaoTemplate<Organisation> organisationDao;;

	@Setter private IDaoTemplate<Referential> referentialDao;;

	@Setter private IDaoTemplate<GuiImport> importDao;;

	@Setter private IDaoTemplate<GuiExport> exportDao;

	@Setter private IDaoTemplate<ExportLogMessage> exportLogMessageDao;

	@Setter private IDaoTemplate<GuiValidation> validationDao;

	@Setter private IGeographicService geographicService;

	private static String getHelpString(ResourceBundle bundle,String key)
	{
		try
		{
			return bundle.getString(key);
		}
		catch (Exception e) 
		{
			return null;
		}
	}

	private static void printBloc(ResourceBundle bundle,String key,String indent)
	{ 
		// print  options
		String line = null;
		int rank = 1;
		do 
		{
			line = getHelpString(bundle,key+rank);
			if (line != null)
			{
				System.out.println(indent+line);
				printBloc(bundle,key+rank+"_",indent+"   ");
			}
			rank++;
		} while (line != null);
	}

	private static void printCommandDetail(ResourceBundle bundle,String key,String indent)
	{ 
		// print  command
		String line = getHelpString(bundle,key); 
		if (line == null)
		{
			System.out.println("-- unknown command : "+key);
			return;
		}
		System.out.println(indent+line);
		printBloc(bundle,key+"_",indent+"   ");
		line = getHelpString(bundle,key+"_n"); 
		if (line != null)
		{
			System.out.println(indent+"   "+line);
		}

	}

	/**
	 * 
	 */
	private static void printCommandDetail(String command) 
	{
		ResourceBundle bundle = null;
		try
		{
			bundle = ResourceBundle.getBundle(Command.class.getName(),locale);
		}
		catch (MissingResourceException e1)
		{
			try
			{
				bundle = ResourceBundle.getBundle(Command.class.getName());
			}
			catch (MissingResourceException e2)
			{
				System.out.println("missing help resource");
				return;
			}
		}
		String lowerCommand = command.toLowerCase();
		printCommandDetail(bundle,lowerCommand,"   ");


	}

	/**
	 * 
	 */
	private static void printCommandSyntax(boolean interactive) 
	{
		ResourceBundle bundle = null;
		try
		{
			bundle = ResourceBundle.getBundle(Command.class.getName(),locale);
		}
		catch (MissingResourceException e1)
		{
			try
			{
				bundle = ResourceBundle.getBundle(Command.class.getName());
			}
			catch (MissingResourceException e2)
			{
				System.out.println("missing help resource");
				return;
			}
		}

		String[] commands = getHelpString(bundle,"Commands").split(" ");
		if (interactive)
		{
			for (String command : commands) 
			{
				String line = getHelpString(bundle, command);
				System.out.println("   "+line);
			}
		}
		else
		{
			for (String command : commands) 
			{
				printCommandDetail(bundle,command,"   ");
				System.out.println("");
			}
		}

	}


	public Map<String,List<String>> globals = new HashMap<String, List<String>>();

	public static Map<String,String> shortCuts ;

	public boolean verbose = false;

	public static Locale locale = Locale.getDefault();

	static
	{
		shortCuts = new HashMap<String, String>();
		shortCuts.put("c", "command");
		shortCuts.put("h", "help");
		shortCuts.put("o", "object");
		shortCuts.put("v", "verbose");
	}

	private static Session session = null;

	/**
	 * @param factory
	 */
	public static void closeDao() 
	{
		session.flush();
		ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
		SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(sessionHolder.getSession());

	}
	/**
	 * @param factory
	 */
	public static void initDao() 
	{
		ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
		SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
		session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// pattern partially work
		String[] context = {"classpath*:/chouetteContext.xml"};

		if (args.length >= 1) 
		{
			if (args[0].equalsIgnoreCase("-help") ||  args[0].equalsIgnoreCase("-h") )
			{
				printHelp();
				System.exit(0);
			}

			applicationContext = new ClassPathXmlApplicationContext(context);
			ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
			Command command = (Command) factory.getBean("Command");

			initDao();

			command.execute(args);

			closeDao();

			System.runFinalization();

		}
		else
		{
			printHelp();
		}
	}


	/**
	 * @param args
	 */
	public void execute(String[] args)
	{
		List<CommandArgument> commands = null;
		try 
		{
			commands = parseArgs(args);
		} 
		catch (Exception e1) 
		{
			if (getBoolean(globals,"help"))
			{
				printHelp();
				return;
			}
			else
			{
				System.err.println("invalid syntax : "+e1.getMessage());
				logger.error(e1.getMessage(),e1);
				System.exit(1);
			}
		}
		if (getBoolean(globals,"help"))
		{
			printHelp();
			return;
		}

		if (getBoolean(globals,"verbose"))
		{
			verbose = true;
			for (String key : globals.keySet())
			{
				System.out.println("global parameters "+key+" : "+ Arrays.toString(globals.get(key).toArray()));
			}
		}
		for (String key : globals.keySet())
		{
			logger.info("global parameters "+key+" : "+ Arrays.toString(globals.get(key).toArray()));
		}

		int commandNumber = 0;

		try
		{
			for (CommandArgument command : commands) 
			{
				commandNumber++;
				executeCommand(commandNumber, command);
			}
		}
		catch (Exception e)
		{
			if (getBoolean(globals,"help"))
			{
				printHelp();
			}
			else
			{
				System.out.println("command failed : "+e.getMessage());
				logger.error(e.getMessage(),e);
				System.exit(1);
			}
		}


	}

	/**
	 * @param beans
	 * @param commandNumber
	 * @param command
	 * @return
	 * @throws ChouetteException
	 * @throws Exception
	 */
	public void executeCommand(
			int commandNumber,
			CommandArgument command) throws Exception 
			{
		String name = command.getName();
		Map<String, List<String>> parameters = command.getParameters();
		if (verbose)
		{
			traceCommand(commandNumber, name, parameters);
		}
		logger.info("Command "+commandNumber+" : "+name);
		for (String key : parameters.keySet())
		{
			logger.info("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
		}

		if (name.equals("verbose"))
		{
			verbose = !(getBoolean(parameters, "off")) ;
			return ;
		}
		if (name.equals("help"))
		{
			String cmd = getSimpleString(parameters, "cmd","");
			if (cmd.length() > 0)
			{
				printCommandDetail(cmd);
			}
			else
			{
				printCommandSyntax(true);
			}
			return;
		}
		if (name.equals("lang"))
		{
			if (getBoolean(parameters, "en"))
			{
				locale = Locale.ENGLISH;
			}
			else if (getBoolean(parameters, "fr"))
			{
				locale = Locale.FRENCH;
			}
			else
			{
				System.out.println(locale);
			}
			return;
		}

		long tdeb = System.currentTimeMillis();

		if (name.equals("import"))
		{
			int code = executeImport(parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else if (name.equals("validate"))
		{
			int code = executeValidate(parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else if (name.equals("export"))
		{
			// old fashioned interface
			INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
			int code = executeExport(manager,parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else
		{
			throw new Exception("Command "+commandNumber+": unknown command :" +command.getName());
		}
		long tfin = System.currentTimeMillis();
		if (verbose)
		{
			System.out.println("command "+command.getName()+" executed in "+getTimeAsString(tfin-tdeb));
		}
		logger.info("    command "+command.getName()+" executed in "+getTimeAsString(tfin-tdeb));
		return;
			}

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
	private int executeExport(
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

		Referential referential = referentialDao.get(guiExport.getReferentialId());
		logger.info("Referential "+guiExport.getReferentialId());
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

				return executeExport(manager,parameters);

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
			GuiReportItem item = new GuiReportItem("EXCEPTION",Report.STATE.ERROR,e.getMessage());
			errorReport.addItem(item);
			reports.add(errorReport);
			saveExportReports(exportId,format,reports);
			return 1;
		}
		saveExportReports(exportId,format,reports);
		return 0;
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

		if (verbose)
		{
			int count = 0;
			for (NeptuneIdentifiedObject bean : beans)
			{
				if (count > 10) 
				{
					System.out.println(" ... ");
					break;
				}
				count++;
				System.out.println(bean.getName()+" : ObjectId = "+bean.getObjectId());
			}
		}
		System.out.println("beans count = "+beans.size());
		return beans;
			}



	/**
	 * import command :  
	 * -c import -id ZZZ 
	 * @param manager
	 * @param parameters
	 * @return
	 */
	private int executeImport(Map<String, List<String>> parameters)
	{
		Report importReport = null;
		ValidationReport validationReport = null;
		boolean save = true;
		List<Long> savedIds = new ArrayList<Long>();

		INeptuneManager<NeptuneIdentifiedObject> manager = getManagers().get("line");
		// check if import exists and accept unzip before call
		Long importId = Long.valueOf(getSimpleString(parameters,"id","0"));
		// TODO remove when Rails is updated
		if (importId.equals(Long.valueOf(0)))
		{
			importId = Long.valueOf(getSimpleString(parameters,"importid"));
		}
		if (!importDao.exists(importId))
		{
			// error import not found
			logger.error("import not found "+importId);
			return 1;
		}
		GuiImport guiImport = importDao.get(importId);
		logger.info("Import data for import id "+importId);
		logger.info("  format  : "+guiImport.getFormat());
		logger.info("  no save : "+guiImport.isNoSave());
		logger.info("  options : "+guiImport.getParameters());
		String inputFile = getSimpleString(parameters,"inputfile");

		save = !guiImport.isNoSave();

		JSONObject options = guiImport.getParameters();
		if (options == null) options = new JSONObject();

		String format = guiImport.getFormat().toUpperCase(); // TODO : check values 

		Referential referential = referentialDao.get(guiImport.getReferentialId());
		logger.info("Referential "+guiImport.getReferentialId());
		logger.info("  name : "+referential.getName());
		logger.info("  slug : "+referential.getSlug());

		String projectionType = null;
		if (referential.getProjectionType() != null && !referential.getProjectionType().isEmpty())
		{
			logger.info("  projection type for import: "+referential.getProjectionType());
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
				validationReport = new ValidationReport();
				ReportHolder validationHolder = new ReportHolder();
				validationHolder.setReport(validationReport);

				SimpleParameterValue inputFileParam = new SimpleParameterValue("inputFile");
				values.add(inputFileParam);
				// unzip files , import and save contents 
				ZipFile zip = null;
				File temp = null;
				File tempRep = new File(FileUtils.getTempDirectory(),"massImport"+importId);
				if (!tempRep.exists()) tempRep.mkdirs();
				try
				{

					zip = new ZipFile(inputFile);
					ReportItem zipReportItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_FILE,Report.STATE.OK,zip.getName());
					importHolder.setReport(zipReportItem);
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
							logger.error("entry "+entry.getName()+" ignored, unknown extension");
							continue;
						}
						InputStream stream = null;
						try
						{
							stream = zip.getInputStream(entry);
						}
						catch (IOException e)
						{
							logger.error("entry "+entry.getName()+" cannot read");
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
						if (verbose) System.out.println("import file "+entry.getName());
						logger.info("import file "+entry.getName());
						inputFileParam.setFilepathValue(temp.getAbsolutePath());
						List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,importHolder,validationHolder);
						// save
						if (save && beans != null && !beans.isEmpty())
						{

							for (NeptuneIdentifiedObject bean : beans)
							{
								if (verbose)
								{
									System.out.println("save "+bean.getName()+" ("+bean.getObjectId()+")");
								}
								logger.info("save "+bean.getName()+" ("+bean.getObjectId()+")");
								// check all stopareas
								if (bean instanceof Line)
								{
									Line line = (Line) bean;
									checkProjection(line);
								}
							}
							try
							{
								manager.saveAll(null, beans, true, true);
								for (NeptuneIdentifiedObject bean : beans)
								{
									GuiReportItem item = new GuiReportItem("SAVE_OK",Report.STATE.OK,bean.getName());
									importReport.addItem(item);
									savedIds.add(bean.getId());
								}
							}
							catch (Exception e) 
							{
								logger.error("fail to save data :"+e.getMessage(),e);
								for (NeptuneIdentifiedObject bean : beans)
								{
									GuiReportItem item = new GuiReportItem("SAVE_ERROR",Report.STATE.ERROR,bean.getName(),filter_chars
											(e.getMessage()));
									importReport.addItem(item);
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
						logger.info("cannot close zip file");
					}
				}
				catch (IOException e)
				{
					ReportItem fileErrorItem = new ExchangeReportItem(ExchangeReportItem.KEY.ZIP_ERROR,Report.STATE.ERROR,e.getLocalizedMessage());
					importReport.addItem(fileErrorItem);

					saveImportReports(guiImport, importReport, validationReport);
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
						logger.warn("temporary directory "+tempRep.getAbsolutePath()+" could not be deleted");
					}
				}

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
				if (beans != null)
				{
					logger.info("imported Lines "+beans.size());

					if (save)
					{
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
								logger.info("save  Line "+bean.getName());
								manager.saveAll(null, oneBean, true, true);
								GuiReportItem item = new GuiReportItem("SAVE_OK",Report.STATE.OK,bean.getName());
								importReport.addItem(item);
								savedIds.add(bean.getId());
							}
							catch (Exception e) 
							{
								logger.error("save failed "+e.getMessage(),e);
								GuiReportItem item = new GuiReportItem("SAVE_ERROR",Report.STATE.ERROR,bean.getName(),e.getMessage());
								importReport.addItem(item);
							}
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
			System.out.println("import failed "+msg);
			logger.error("import failed "+msg,e);
			if (importReport == null)
			{
				importReport = new ExchangeReport(ExchangeReport.KEY.IMPORT, format);
			}
			GuiReportItem item = new GuiReportItem("EXCEPTION",Report.STATE.ERROR,msg);
			importReport.addItem(item);
			saveImportReports(guiImport,importReport,validationReport);

			return 1;
		}

		// launch phase3 validation if required and possible
		if (save && !savedIds.isEmpty() && guiImport.getValidationTask() != null)
		{
			logger.info("processing phase 3 validation on "+savedIds.size()+" lines");
			// launch validation on objects
			Filter filter = Filter.getNewInFilter("id", savedIds);
			try 
			{
				List<NeptuneIdentifiedObject> beans = manager.getAll(null, filter);
				if (beans == null || beans.isEmpty())
				{
					logger.error("cannot read previously saved objects :"+Arrays.deepToString(savedIds.toArray()));

				}
				else
				{
					PhaseReportItem phaseReport = new PhaseReportItem(PhaseReportItem.PHASE.THREE);
					validationReport.addItem(phaseReport);
					manager.validate(null, beans, guiImport.getValidationTask().getParameters(), phaseReport, true);
				}
			} 
			catch (ChouetteException e) 
			{
				logger.error("cannot read previously saved objects",e);
			}
		}


		saveImportReports(guiImport,importReport,validationReport);
		return (0);

	}

	private void saveImportReports(GuiImport guiImport, Report ireport, ValidationReport vreport) 
	{
		//logger.info("import report = "+ireport.toJSON().toString(3));

		if (guiImport.getValidationTask() != null)
		{
			if (vreport != null && vreport.getItems() != null)
			{
				// logger.info("validation report = "+vreport.toJSON().toString(3));
				switch (vreport.getStatus())
				{
				case WARNING:
				case ERROR:
				case FATAL:
					guiImport.getValidationTask().setStatus("nok");
					break;
				case OK:
					guiImport.getValidationTask().setStatus("ok");
					break;
				case UNCHECK:
					guiImport.getValidationTask().setStatus("na");
					break;
				}
				guiImport.getValidationTask().addAllSteps(vreport.toValidationResults());
			}
		}
		guiImport.setResult(ireport.toJSON());
		importDao.save(guiImport);
		session.flush();
	}

	private Object filter_chars(String message) 
	{
		if (message == null) return "";
		return message.replaceAll("\t", "").replaceAll("\"", "'");
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
	 * validate command from database : 
	 * -c validation -o line|network|company -validationId ZZZ
	 *
	 * @param parameters 
	 * @return 
	 * @throws ChouetteException
	 */
	private int executeValidate(
			Map<String, List<String>> parameters)
					throws ChouetteException 
					{
		INeptuneManager<NeptuneIdentifiedObject> manager = getManagers().get("line");
		Long validationId = Long.valueOf(getSimpleString(parameters,"id"));
		if (!validationDao.exists(validationId))
		{
			// error validation not found
			logger.error("compilanceCheckTask not found "+validationId);
			return 1;
		}
		GuiValidation compilanceCheckTask = validationDao.get(validationId);

		// read parameters
		JSONObject validationParameters = compilanceCheckTask.getParameters();

		// read object type
		String objectType = extractObjectType(compilanceCheckTask.getReferencesType().toLowerCase());

		String idstring = compilanceCheckTask.getReferenceIds();
		String[] ids = idstring.split(",");
		List<Long> checkIds = new ArrayList<>();
		for (String id : ids) 
		{
			checkIds.add(Long.valueOf(id));
		}

		List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
		if (!objectType.startsWith("line"))
		{
			INeptuneManager<NeptuneIdentifiedObject> loadManager = getManagers().get(objectType);
			if (loadManager == null)
			{
				logger.error("object type "+objectType+" not found "+validationId);
				return 1;
			}
			Filter filter = Filter.getNewInFilter("id", ids);
			List<NeptuneIdentifiedObject> containerBeans = loadManager.getAll(null,filter);
			Set<NeptuneIdentifiedObject> beanSet = new HashSet<NeptuneIdentifiedObject>();
			for (NeptuneIdentifiedObject container : containerBeans) 
			{
				if (objectType.equals("network"))
				{
					PTNetwork network = (PTNetwork) container;
					beanSet.addAll(network.getLines());
				}
				else if (objectType.equals("company"))
				{
					Company company = (Company) container;
					beanSet.addAll(company.getLines());
				}
				else if (objectType.equals("groupofline"))
				{
					GroupOfLine group = (GroupOfLine) container;
					beanSet.addAll(group.getLines());
				}
				else
				{
					logger.error("object type "+objectType+" not managed "+validationId);
				}
			}
			beans.addAll(beanSet);
		}
		else
		{
			Filter filter = Filter.getNewInFilter("id", ids);
			beans = manager.getAll(null,filter);

		}

		PhaseReportItem valReport = new PhaseReportItem(PhaseReportItem.PHASE.THREE);
		if (beans != null && !beans.isEmpty())
		{
			manager.validate(null, beans, validationParameters,valReport);
		}

		// save report
		if (valReport != null)
		{
				saveValidationReport(compilanceCheckTask,valReport);
		}

		return 0;
					}


	private String extractObjectType(String type) 
	{
		// type shall begin by chouette::
		if (type.startsWith("chouette::")) return type.substring(10);

		return type;
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
	public boolean getBoolean(Map<String, List<String>> parameters,String key)
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
	public INeptuneManager<NeptuneIdentifiedObject> getManager(Map<String, List<String>> parameters) 
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
	public String getSimpleString(Map<String, List<String>> parameters,String key)
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
	public String getSimpleString(Map<String, List<String>> parameters,String key,String defaultValue)
	{
		List<String> values = parameters.get(key);
		if (values == null) return defaultValue;
		if (values.size() > 1) throw new IllegalArgumentException("parameter -"+key+" of String type must be unique");
		return values.get(0);
	}


	/**
	 * convert a duration in millisecond to literal
	 *
	 * the returned format depends on the duration :
	 * <br>if duration > 1 hour, format is HH h MM m SS s
	 * <br>else if duration > 1 minute , format is MM m SS s
	 * <br>else if duration > 1 second , format is SS s
	 * <br>else (duration < 1 second) format is LLL ms
	 *
	 * @param duration the duration to convert
	 * @return the duration
	 */
	private String getTimeAsString(long duration)
	{
		long d = duration;
		long milli = d % 1000;
		d /= 1000;
		long sec = d % 60;
		d /= 60;
		long min = d % 60;
		d /= 60;
		long hour = d;

		String res = "";
		if (hour > 0)
			res += hour+" h "+min+" m "+sec + " s " ;
		else if (min > 0)
			res += min+" m "+sec + " s " ;
		else if (sec > 0)
			res += sec + " s " ;
		res += milli + " ms" ;
		return res;
	}



	private List<CommandArgument> parseArgs(String[] args) throws Exception
	{
		Map<String, List<String>> parameters = globals;
		List<CommandArgument> commands = new ArrayList<CommandArgument>();
		CommandArgument command = null;
		if (args.length == 0)
		{
			List<String> list = new ArrayList<String>();
			list.add("true");
			parameters.put("help", list);
		}
		for (int i = 0; i < args.length; i++)
		{
			if (isOption(args[i]))
			{
				String key = args[i].substring(1).toLowerCase();
				if (key.length() == 1) 
				{
					String alias = shortCuts.get(key);
					if (alias != null) key = alias;
				}
				if (key.equals("command")) 
				{
					if (i == args.length -1) 
					{
						throw new Exception("missing command name");
					}
					String name = args[++i];
					if (name.startsWith("-"))
					{
						throw new Exception("missing command name before "+name);
					}
					command = new CommandArgument(name);
					parameters = command.getParameters();
					commands.add(command);
				}
				else
				{
					if (parameters.containsKey(key))
					{
						throw new Exception("duplicate parameter : -"+key);
					}
					List<String> list = new ArrayList<String>();

					if (i == args.length -1 || isOption(args[i+1]))
					{
						list.add("true");
					}
					else
					{
						while ((i+1) < args.length && !isOption(args[i+1]))
						{
							list.add(args[++i]);
						}
					}
					parameters.put(key,list);
				}
			}
		}

		return commands;
	}

	private boolean isOption(String arg)
	{
		if (arg.length() < 2) return false;
		return arg.startsWith("-") && !Character.isDigit(arg.charAt(1));
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

	@SuppressWarnings("incomplete-switch")
	private List<ParameterValue> populateParametersFromJSON(FormatDescription description,JSONObject options,String ... excluded)
	{
		List<ParameterValue> values = new ArrayList<ParameterValue>();
		List<String> excludedParams = Arrays.asList(excluded);
		for (ParameterDescription desc : description.getParameterDescriptions())
		{
			String name = desc.getName();
			String key = name.toLowerCase();
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
					logger.debug("prepare list parameter "+name);
				}
				else
				{
					if (options.optJSONArray(key) != null)
					{
						throw new IllegalArgumentException("parameter -"+name+" must be unique");
					}


					SimpleParameterValue val = new SimpleParameterValue(name);
					switch (desc.getType())
					{
					case FILEPATH : val.setFilepathValue(options.getString(key)); break;
					case STRING : val.setStringValue(options.getString(key)); break;
					case FILENAME : val.setFilenameValue(options.getString(key)); break;
					case BOOLEAN : val.setBooleanValue(options.getBoolean(key)); break;
					case INTEGER : val.setIntegerValue(options.getLong(key)); break;
					case DATE : val.setDateValue(toCalendar(options.getString(key)));break;
					}
					values.add(val);
					logger.debug("prepare simple parameter "+name);

				}
			}
		}
		return values;      
	}

	private int saveExportReport(Long exportId, String format,Report report,int position)
	{
		String prefix = report.getOriginKey();
		if (prefix == null || report instanceof ReportItem) prefix = ((ReportItem) report).getMessageKey();
		prefix = format+prefix;
		ExportLogMessage message = new ExportLogMessage(exportId,format,report,position++);
		exportLogMessageDao.save(message);
		if (report.getItems() != null)
		{
			for (ReportItem item : report.getItems())
			{
				position = saveExportReportItem(exportId,format,item,prefix,position);
			}
		}
		return position;
	}

	private int saveExportReportItem(Long exportId, String format,ReportItem item, String prefix, int position)
	{
		ExportLogMessage message = new ExportLogMessage(exportId,format,item,prefix,position++);
		exportLogMessageDao.save(message);
		if (item.getItems() != null)
		{
			String subPrefix = prefix+"|"+format+item.getMessageKey();
			for (ReportItem child : item.getItems())
			{
				position = saveExportReportItem(exportId,format,child,subPrefix,position);
			}
		}
		return position;
	}

	private void saveExportReports(Long exportId, String format,List<Report> reports)
	{
		int position = 1;
		Filter filter = Filter.getNewEqualsFilter("parentId", exportId);
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
				position = saveExportReport(exportId,"",report,position);
			else
				position = saveExportReport(exportId,format+"_",report,position);
		}

	}

	private void saveValidationReport(GuiValidation compilanceCheckTask, PhaseReportItem vreport)
	{
		if (vreport != null && vreport.getItems() != null)
		{
			switch (vreport.getStatus())
			{
			case WARNING:
			case ERROR:
			case FATAL:
				compilanceCheckTask.setStatus("nok");
				break;
			case OK:
				compilanceCheckTask.setStatus("ok");
				break;
			case UNCHECK:
				compilanceCheckTask.setStatus("na");
				break;
			}
			compilanceCheckTask.addAllSteps(vreport.toValidationResults());
			validationDao.save(compilanceCheckTask);
			session.flush();
		}
	
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



	/**
	 * @param commandNumber
	 * @param name
	 * @param parameters
	 */
	public void traceCommand(int commandNumber, String name, Map<String, List<String>> parameters)
	{
		System.out.println("Command "+commandNumber+" : "+name);
		for (String key : parameters.keySet())
		{
			System.out.println("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
		}
	}


}
