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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.model.ExportLogMessage;
import fr.certu.chouette.plugin.model.FileValidationLogMessage;
import fr.certu.chouette.plugin.model.ImportLogMessage;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.service.geographic.IGeographicService;

/**
 * 
 * 
 * import command :  ( -fileFormat utilisé si l'extension du fichier n'est pas représentative du format)
 * -c import -o line -format XXX -inputFile YYYY [-fileFormat TTT] -importId ZZZ ... 
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

	@Setter private ValidationParameters validationParameters;

	@Setter private IDaoTemplate<ImportLogMessage> importLogMessageDao;;

	@Setter private IDaoTemplate<ExportLogMessage> exportLogMessageDao;

	@Setter private IDaoTemplate<FileValidationLogMessage> fileValidationLogMessageDao;

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


	/**
	 * @param factory
	 */
	public static void closeDao() 
	{

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
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
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

		INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
		long tdeb = System.currentTimeMillis();

		if (name.equals("import"))
		{
			int code = executeImport(manager,parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else if (name.equals("validate"))
		{
			int code = executeValidate(manager,parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else if (name.equals("export"))
		{
			int code = executeExport(manager,parameters);
			if (code > 0)
			{
				logger.error("   command failed with code "+code);
				System.exit(code);
			}
		}
		else if (name.equals("exportForDeletion"))
		{
			int code = executeExportDeletion(manager,parameters);
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
		long exportId = Long.parseLong(getSimpleString(parameters,"exportid"));

		List<Report> reports = new ArrayList<Report>();
		// GuiReport loadReport = new GuiReport("LOAD",Report.STATE.OK);
		
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
				if (ids != null)
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
			System.out.println("export failed "+e.getMessage());
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
	 * @param beans
	 * @param manager
	 * @param parameters
	 * @return 
	 */
	private int executeExportDeletion(
			INeptuneManager<NeptuneIdentifiedObject> manager,
			Map<String, List<String>> parameters) 
	{
		List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();

		String format = getSimpleString(parameters,"format");
		try
		{
			List<FormatDescription> formats = manager.getDeleteExportFormats(null);
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
				throw new IllegalArgumentException("format "+format+" unavailable, check command getDeletionExportFormats for list ");
			}


			List<ParameterValue> values = new ArrayList<ParameterValue>();
			for (ParameterDescription desc : description.getParameterDescriptions())
			{
				String name = desc.getName();
				String key = name.toLowerCase();
				List<String> vals = parameters.get(key);
				if (vals == null)
				{
					if (desc.isMandatory())
					{
						throw new IllegalArgumentException("parameter -"+name+" is required, check command getDeletionExportFormats for list ");
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
					}
					else
					{
						if (vals.size() != 1)
						{
							throw new IllegalArgumentException("parameter -"+name+" must be unique, check command getDeletionExportFormats for list ");
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
									 default:
										 throw new IllegalArgumentException("parameter -"+name+" unknown type "+desc.getType());
									}
									values.add(val);
					}
				}
			}

			ReportHolder holder = new ReportHolder();
			manager.doExportDeleted(null, beans, format, values, holder );
			if (holder.getReport() != null)
			{
				Report r = holder.getReport();
				System.out.println(r.getLocalizedMessage());
				// printItems(System.out,"",r.getItems());
			}
		}
		catch (ChouetteException e)
		{
			logger.error(e.getMessage());

			Throwable caused = e.getCause();
			while (caused != null)
			{
				logger.error("caused by "+ caused.getMessage());
				caused = caused.getCause();
			}
			throw new RuntimeException("export failed, see details in log");
		}
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
	 * import command :  ( -fileFormat utilisé si l'extension du fichier n'est pas représentative du format)
	 * -c import -o line -format XXX -inputFile YYYY [-fileFormat TTT] -importId ZZZ ... 
	 * @param manager
	 * @param parameters
	 * @return
	 */
	private int executeImport(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters)
	{
		parameters.put("reportforsave", Arrays.asList(new String[] {"true"} ));
		// parameters.put("validate",Arrays.asList(new String[]{"true"})); // force validation if possible
		
		GuiReport saveReport = new GuiReport("SAVE",Report.STATE.OK);
		Report importReport = null;

		List<Report> reports = new ArrayList<Report>();
		// check if import exists and accept unzip before call
		String format = getSimpleString(parameters,"format");
		String inputFile = getSimpleString(parameters,"inputfile");
		// String fileFormat = getSimpleString(parameters,"fileformat","");
		String srid = getSimpleString(parameters,"srid","");
		if (!srid.isEmpty())
		{
			geographicService.switchProjection(srid);
		}

		long importId = Long.parseLong(getSimpleString(parameters,"importid"));
		int beanCount = 0;

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
			List<ParameterValue> values = populateParameters(description,parameters,"inputfile","fileformat");
			if (zipped && description.isUnzipAllowed())
			{
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
						temp = new File(tempRep, entry.getName());
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
						ReportHolder holder = new ReportHolder();
						List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,holder);
						if (holder.getReport() != null)
						{
							if (importReport == null) 
							{
								importReport = holder.getReport();
								reports.add(importReport);
							}
							else
							{
								importReport.addAll(holder.getReport().getItems());
							}

						}
						// save
						if (beans != null && !beans.isEmpty())
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
									beanCount++;
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
					//reports.add(saveReport);
					System.out.println("import failed "+e.getMessage());
					logger.error("import failed "+e.getMessage(),e);
					saveImportReports(importId,format,reports);
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
//				if (!fileFormat.isEmpty())
//				{
//					SimpleParameterValue fileFormatParam = new SimpleParameterValue("fileFormat");
//					fileFormatParam.setStringValue(fileFormat);
//					values.add(fileFormatParam);
//				}
				// surround with try catch 
				ReportHolder holder = new ReportHolder();
				List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,holder);
				if (holder.getReport() != null)
				{
					importReport = holder.getReport();
					reports.add(holder.getReport());
				}
				logger.info("imported Lines "+beans.size());


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
						saveReport.addItem(item);
						beanCount++;
					}
					catch (Exception e) 
					{
						logger.error("save failed "+e.getMessage(),e);
						GuiReportItem item = new GuiReportItem("SAVE_ERROR",Report.STATE.ERROR,bean.getName(),e.getMessage());
						saveReport.addItem(item);
					}
				}
			}
		}
		catch (Exception e)
		{
			// fill report with error
			if (saveReport.getItems() != null  && !saveReport.getItems().isEmpty())
				reports.add(saveReport);
			String msg = e.getMessage();
			if (msg == null) msg = e.getClass().getName();
			System.out.println("import failed "+msg);
			logger.error("import failed "+msg,e);
			GuiReport errorReport = new GuiReport("IMPORT_ERROR",Report.STATE.ERROR);
			GuiReportItem item = new GuiReportItem("EXCEPTION",Report.STATE.ERROR,msg);
			errorReport.addItem(item);
			reports.add(errorReport);
			saveImportReports(importId,format,reports);

			return 1;
		}
		if (saveReport.getItems() != null  && !saveReport.getItems().isEmpty())
			reports.add(saveReport);
		saveImportReports(importId,format,reports);
		return (beanCount == 0?1:0);

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
	 * @param parameters
	 */
	 private void executeSetValidationParameters(Map<String, List<String>> parameters) 
	 {
		 for (String key : parameters.keySet()) 
		 {
			 if (key.toLowerCase().startsWith("test") || key.toLowerCase().startsWith("projection"))
			 {
				 String value = "";
				 try
				 { 
					 value = getSimpleString(parameters,key);
				 }
				 catch (Exception ex)
				 {
					 List<String> values = parameters.get(key);
					 for (String item : values)
					 {
						 value += item+" ";
					 }
				 }
				 if (validationParameters == null) validationParameters = new ValidationParameters();
				 try 
				 {
					 setAttribute(validationParameters, key, value);
				 } 
				 catch (Exception e) 
				 {
					 logger.error(e.getMessage());
					 System.err.println("unknown or unvalid parameter " + key);
				 }  
			 }
		 }
	 }

	 /**
	  * validate command : 
	  * from neptune file :
	  * -c validate -o line -inputFile YYYY  [-fileFormat TTT] -validateId ZZZ 
	  * 
	  * from database : 
	  * -c validate -o line|network|company -validateId ZZZ [-id list_of_ids_separated_by_commas]
	  *
	  * @param manager
	  * @param parameters 
	  * @return 
	  * @throws ChouetteException
	  */
	 private int executeValidate(
			 INeptuneManager<NeptuneIdentifiedObject> manager, 
			 Map<String, List<String>> parameters)
					 throws ChouetteException 
					 {
		 long validationId = Long.parseLong(getSimpleString(parameters,"validationid"));

		 List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();

		 String inputFile = getSimpleString(parameters,"inputfile","");
		 ReportHolder holder = new ReportHolder();
		 if (!inputFile.isEmpty())
		 {
			 parameters.put("validate",Arrays.asList(new String[]{"true"}));
			 beans = neptuneImport(manager,parameters,holder);
		 }
		 else
		 {
			 beans = executeGet(manager,parameters);
		 }

		 Report valReport = null;
		 if (beans != null && !beans.isEmpty())
		 {
			 executeSetValidationParameters(parameters);
			 valReport = manager.validate(null, beans, validationParameters);
		 }

		 // merge reports
		 if (holder.getReport() != null)
		 {
			 Report importReport = holder.getReport();
			 saveFileValidationReport(validationId,importReport);
		 }

		 // save report
		 if (valReport != null)
		 {
			 for (ReportItem item : valReport.getItems())
			 {
				 saveFileValidationReport(validationId,item);
			 }
		 }

		 return 0;
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


	 private List<NeptuneIdentifiedObject> neptuneImport(INeptuneManager<NeptuneIdentifiedObject> manager,
			 Map<String, List<String>> parameters, ReportHolder holder) throws ChouetteException
			 {
		 String format = "NEPTUNE";
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
		 List<ParameterValue> values = populateParameters(description,parameters);
		 List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,holder);
		 return beans;
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

	 private int saveExportReport(long exportId, String format,Report report,int position)
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

	 private int saveExportReportItem(long exportId, String format,ReportItem item, String prefix, int position)
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

	 private void saveExportReports(long exportId, String format,List<Report> reports)
	 {
		 int position = 1;
		 Filter filter = Filter.getNewEqualsFilter("parentId", Long.valueOf(exportId));
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

	 private void saveFileValidationReport(long validationId, Report report)
	 {
		 int position = 1;
		 Filter filter = Filter.getNewEqualsFilter("parentId", Long.valueOf(validationId));
		 List<FileValidationLogMessage> messages = fileValidationLogMessageDao.select(filter);
		 if (messages != null)
		 {
			 for (FileValidationLogMessage message : messages)
			 {
				 if (message.getPosition() >= position)
					 position = message.getPosition() + 1;
			 }
		 }
		 FileValidationLogMessage message = new FileValidationLogMessage(validationId,"",report,position++);
		 fileValidationLogMessageDao.save(message);
		 if (report.getItems() != null)
		 {
			 //         String prefix = report.getOriginKey();
			 //         if (prefix == null || prefix.isEmpty())
			 //         {
			 //            prefix = ((ReportItem) report).getMessageKey();
			 //         }
			 for (ReportItem item : report.getItems())
			 {
				 position = saveFileValidationReportItem(validationId,item,"",position);
			 }
		 }

	 }

	 private int saveFileValidationReportItem(long validationId, ReportItem item, String prefix, int position)
	 {
		 FileValidationLogMessage message = new FileValidationLogMessage(validationId,"",item,"",position++);
		 fileValidationLogMessageDao.save(message);
		 if (item.getItems() != null)
		 {
			 //         String subPrefix = prefix+"|"+item.getMessageKey();
			 for (ReportItem child : item.getItems())
			 {
				 position = saveFileValidationReportItem(validationId,child,"",position);
			 }
		 }
		 return position;
	 }

	 private int saveImportReport(long importId, String format,Report report,int position)
	 {
		 String prefix = report.getOriginKey();
		 if (prefix == null && report instanceof ReportItem) prefix = ((ReportItem) report).getMessageKey();
		 prefix = format+prefix;
		 ImportLogMessage message = new ImportLogMessage(importId,format,report,position++);
		 importLogMessageDao.save(message);
		 if (report.getItems() != null)
		 {
			 for (ReportItem item : report.getItems())
			 {
				 position = saveImportReportItem(importId,format,item,prefix,position);
			 }
		 }
		 return position;
	 }

	 private int saveImportReportItem(long importId, String format,ReportItem item, String prefix, int position)
	 {
		 ImportLogMessage message = new ImportLogMessage(importId,format,item,prefix,position++);
		 importLogMessageDao.save(message);
		 if (item.getItems() != null)
		 {
			 String subPrefix = prefix+"|"+format+item.getMessageKey();
			 for (ReportItem child : item.getItems())
			 {
				 position = saveImportReportItem(importId,format,child,subPrefix,position);
			 }
		 }
		 return position;
	 }

	 private int saveImportReports(long importId, String format, List<Report> reports)
	 {
		 int position = 1;
		 Filter filter = Filter.getNewEqualsFilter("parentId", Long.valueOf(importId));
		 List<ImportLogMessage> messages = importLogMessageDao.select(filter);
		 if (messages != null)
		 {
			 for (ImportLogMessage message : messages)
			 {
				 if (message.getPosition() >= position)
					 position = message.getPosition() + 1;
			 }
		 }
		 return saveImportReports(importId,format,position,reports);
	 }

	 
	 private int saveImportReports(long importId, String format, int position, List<Report> reports)
	 {
		 for (Report report : reports)
		 {
			 if (report instanceof GuiReport || report instanceof ExchangeReport) 
				 position = saveImportReport(importId,"", report,position);
			 else
				 position = saveImportReport(importId,format+"_", report,position);

		 }
		 return position;

	 }

	 /**
	  * convert date string to calendar
	  * @param simpleval
	  * @return
	  */
	 protected Calendar toCalendar(String simpleval)
	 {
		 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		 try
		 {
			 Date d = sdf.parse(simpleval);
			 Calendar c = Calendar.getInstance();
			 c.setTime(d);
			 return c;
		 }
		 catch (ParseException e)
		 {
			 logger.error("invalid date format : "+ simpleval+" dd/MM/yyyy expected");
			 throw new RuntimeException("invalid date format : "+ simpleval+" dd/MM/yyyy expected");
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
	 /**
	  * @param object
	  * @param attrname
	  * @param value
	  * @throws Exception
	  */
	 private void setAttribute(Object object, String attrname, String value) throws Exception 
	 {
		 String name = attrname.toLowerCase();
		 if (name.equals("id")) 
		 {
			 throw new Exception("non writable attribute id for any object , process stopped ");
		 }
		 if (!name.equals("objectid") && !name.equals("creatorid") && !name.equals("areacentroid")&& name.endsWith("id")) 
		 {
			 throw new Exception("non writable attribute "+attrname+" use setReference instand , process stopped ");
		 }
		 Class<?> beanClass = object.getClass();
		 Method setter = findSetter(beanClass, attrname);
		 Class<?> type = setter.getParameterTypes()[0];
		 if (type.isArray() || type.getSimpleName().equals("List"))
		 {
			 throw new Exception("list attribute "+attrname+" for object "+beanClass.getName()+" must be update with (add/remove)Attribute, process stopped ");
		 }
		 Object arg = null;
		 if (type.isEnum())
		 {
			 arg = toEnum(type,value);
		 }
		 else if (type.isPrimitive())
		 {
			 arg = toPrimitive(type,value);
		 }
		 else
		 {
			 arg = toObject(type,value);
		 }
		 setter.invoke(object, arg);
	 }

	 /**
	  * @param beanClass
	  * @param attribute
	  * @return
	  * @throws Exception
	  */
	 private Method findSetter(
			 Class<?> beanClass, String attribute)
					 throws Exception {
		 return findAccessor(beanClass, attribute, "set",true);
	 }
	 /**
	  * @param beanClass
	  * @param attribute
	  * @return
	  * @throws Exception
	  */
	 private Method findAccessor(
			 Class<?> beanClass, String attribute, String prefix, boolean ex)
					 throws Exception {
		 String methodName = prefix+attribute;
		 Method[] methods = beanClass.getMethods();
		 Method accessor = null;
		 for (Method method : methods) 
		 {
			 if (method.getName().equalsIgnoreCase(methodName))
			 {
				 accessor = method;
				 break;
			 }
		 }
		 if (ex && accessor == null)
		 {
			 throw new Exception("unknown accessor "+prefix+" for attribute "+attribute+" for object "+beanClass.getName()+", process stopped ");
		 }
		 return accessor;
	 }
	 protected Object toObject(Class<?> type, String value) throws Exception 
	 {
		 if (value == null) return null;
		 String name = type.getSimpleName();
		 if (name.equals("String")) return value;
		 if (name.equals("Long")) return Long.valueOf(value);
		 if (name.equals("Boolean")) return Boolean.valueOf(value);
		 if (name.equals("Integer")) return Integer.valueOf(value);
		 if (name.equals("Float")) return Float.valueOf(value);
		 if (name.equals("Double")) return Double.valueOf(value);
		 if (name.equals("BigDecimal")) return BigDecimal.valueOf(Double.parseDouble(value));
		 if (name.equals("Date")) 
		 {
			 DateFormat dateFormat = null;
			 if (value.contains("-") && value.contains(":"))
			 {
				 dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			 }
			 else if (value.contains("-") )
			 {
				 dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 }
			 else if ( value.contains(":"))
			 {
				 dateFormat = new SimpleDateFormat("HH:mm:ss");
			 }
			 else
			 {
				 throw new Exception("unable to convert "+value+" to Date");
			 }
			 Date date = dateFormat.parse(value);
			 return date;
		 }
		 if (name.equals("Time")) 
		 {
			 DateFormat dateFormat = null;
			 if ( value.contains(":"))
			 {
				 dateFormat = new SimpleDateFormat("H:m:s");
			 }
			 else
			 {
				 throw new Exception("unable to convert "+value+" to Time");
			 }
			 Date date = dateFormat.parse(value);
			 Time time = new Time(date.getTime());
			 return time;
		 }

		 throw new Exception("unable to convert String to "+type.getCanonicalName());
	 }

	 protected Object toPrimitive(Class<?> type, String value) throws Exception 
	 {
		 if (value == null) throw new Exception("primitive type "+type.getName()+" cannot be set to null");
		 String name = type.getName();
		 if (name.equals("long")) return Long.valueOf(value);
		 if (name.equals("boolean")) return Boolean.valueOf(value);
		 if (name.equals("int")) return Integer.valueOf(value);
		 if (name.equals("float")) return Float.valueOf(value);
		 if (name.equals("double")) return Double.valueOf(value);
		 throw new Exception("unable to convert String to "+type.getName());
	 }

	 protected Object toEnum(Class<?> type, String value) throws Exception 
	 {
		 Method m = type.getMethod("fromValue", String.class);
		 return m.invoke(null, value);
	 }



}
