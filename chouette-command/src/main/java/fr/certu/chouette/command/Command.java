/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.plugin.exchange.FormatDescription;
import fr.certu.chouette.plugin.exchange.ListParameterValue;
import fr.certu.chouette.plugin.exchange.ParameterDescription;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.Report.STATE;
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.ValidationParameters;

/**
 *
 */
@NoArgsConstructor
public class Command
{

	private static final Logger logger = Logger.getLogger(Command.class);
	private static ClassPathXmlApplicationContext applicationContext;


	@Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

	@Setter private ValidationParameters validationParameters;

	private Map<String,List<String>> globals = new HashMap<String, List<String>>();;

	private static Map<String,String> shortCuts ;

	private boolean verbose = false;

	static
	{
		shortCuts = new HashMap<String, String>();
		shortCuts.put("c", "command");
		shortCuts.put("h", "help");
		shortCuts.put("o", "object");
		shortCuts.put("f", "file");
		shortCuts.put("i", "interactive");
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

			if (args[0].equalsIgnoreCase("-noDao"))
			{
				List<String> newContext = new ArrayList<String>();
				PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
				try
				{
					Resource[] re = test.getResources("classpath*:/chouetteContext.xml");
					for (Resource resource : re)
					{
						if (! resource.getURL().toString().contains("dao"))
						{
							newContext.add(resource.getURL().toString());
						}
					}
					context = newContext.toArray(new String[0]);

				} 
				catch (Exception e) 
				{

					System.err.println("cannot remove dao : "+e.getLocalizedMessage());
				}
			}
			applicationContext = new ClassPathXmlApplicationContext(context);
			ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
			Command command = (Command) factory.getBean("Command");
			command.execute(args);
		}
		else
		{
			printHelp();
		}
	}

	/**
	 * @param args
	 */
	private void execute(String[] args)
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
				return;
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

		List<NeptuneIdentifiedObject> beans = null;
		int commandNumber = 0;
		if (getBoolean(globals, "interactive"))
		{
			String line = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String activeObject = getActiveObject(globals);
			while (true)
			{
				try 
				{
					System.out.print(activeObject+" >");
					line = in.readLine().trim();
				} 
				catch (IOException e) 
				{
					System.err.println("cannot read stdin");
					logger.error("cannot read stdin",e);
					return;
				}
				if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;
				if (line.equalsIgnoreCase("help")) 
				{
					printCommandSyntax(true);
				}
				else
				{
					try 
					{
						CommandArgument command = parseLine(++commandNumber, line);
						beans = executeCommand(beans, commandNumber, command);
						activeObject = getActiveObject(command.getParameters());
					} 
					catch (Exception e) 
					{
						System.out.println(e.getMessage());
					}
				}
			}

		}
		else
		{
			try
			{
				for (CommandArgument command : commands) 
				{
					commandNumber++;
					beans = executeCommand(beans, commandNumber, command);
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
					System.err.println("command failed : "+e.getMessage());
					logger.error(e.getMessage(),e);
				}
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
	private List<NeptuneIdentifiedObject> executeCommand(
			List<NeptuneIdentifiedObject> beans, int commandNumber,
			CommandArgument command) throws ChouetteException, Exception {
		String name = command.getName();
		Map<String, List<String>> parameters = command.getParameters();
		if (verbose)
		{
			System.out.println("Command "+name);
			for (String key : parameters.keySet())
			{
				System.out.println("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
			}
		}
		logger.info("Command "+name);
		for (String key : parameters.keySet())
		{
			logger.info("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
		}

		INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);

		if (name.equals("get"))
		{
			beans = executeGet(manager,parameters);
		}
		else if (name.equals("new"))
		{
			beans = executeNew(manager,parameters);
		}
		else if (name.equals("setAttribute"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : setAttribute must follow a reading command");
			executeSetAttribute(beans, parameters);
		}
		else if (name.equals("setReference"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : setReference must follow a reading command");
			executeSetReference(beans, parameters);
		}
		else if (name.equals("save"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : save must follow a reading command");
			executeSave(beans, manager,parameters);
		}
		else if (name.equals("delete"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : save must follow a reading command");
			executeDelete(beans, manager,parameters);
			beans = null;
		}
		else if (name.equals("getImportFormats"))
		{
			executeGetImportFormats(manager,parameters);
		}
		else if (name.equals("import"))
		{
			beans = executeImport(manager,parameters);
		}
		else if (name.equals("print"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : print must follow a reading command");
			executePrint(beans,parameters);
		}
		else if (name.equals("validate"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : validate must follow a reading command");
			executeValidate(beans,manager,parameters);
		}
		else if (name.equals("getExportFormats"))
		{
			executeGetExportFormats(manager,parameters);
		}
		else if (name.equals("export"))
		{
			if (beans == null) throw new Exception("Command "+commandNumber+": Invalid command sequence : export must follow a reading command");
			executeExport(beans,manager,parameters);
		}
		else
		{
			throw new Exception("Command "+commandNumber+": unknown command :" +command);
		}
		return beans;
	}


	private void executeExport(List<NeptuneIdentifiedObject> beans,
			INeptuneManager<NeptuneIdentifiedObject> manager,
			Map<String, List<String>> parameters) 
	{
		String format = getSimpleString(parameters,"format");
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
				throw new IllegalArgumentException("format "+format+" unavailable, check command getImportFormats for list ");
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
						throw new IllegalArgumentException("parameter -"+name+" is required, check command getImportFormats for list ");
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
						}
						values.add(val);
					}
					else
					{
						if (vals.size() != 1)
						{
							throw new IllegalArgumentException("parameter -"+name+" must be unique, check command getImportFormats for list ");
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
						}
						values.add(val);
					}
				}
			}

			ReportHolder holder = new ReportHolder();
			manager.doExport(null, beans, format, values, holder );
			if (holder.getReport() != null)
			{
				Report r = holder.getReport();
				System.out.println(r.getLocalizedMessage());
				printItems("",r.getItems());
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
	}

	private void executeGetExportFormats(
			INeptuneManager<NeptuneIdentifiedObject> manager,
			Map<String, List<String>> parameters) 
	throws ChouetteException 
	{

		List<FormatDescription> formats = manager.getExportFormats(null);
		for (FormatDescription formatDescription : formats)
		{
			System.out.println(formatDescription);
		}


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

	private List<NeptuneIdentifiedObject> executeImport(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters)
	{
		String format = getSimpleString(parameters,"format");
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
				throw new IllegalArgumentException("format "+format+" unavailable, check command getImportFormats for list ");
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
						throw new IllegalArgumentException("parameter -"+name+" is required, check command getImportFormats for list ");
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
						}
						values.add(val);
					}
					else
					{
						if (vals.size() != 1)
						{
							throw new IllegalArgumentException("parameter -"+name+" must be unique, check command getImportFormats for list ");
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
						}
						values.add(val);
					}
				}
			}

			ReportHolder holder = new ReportHolder();
			List<NeptuneIdentifiedObject> beans = manager.doImport(null, format, values,holder);
			if (holder.getReport() != null)
			{
				Report r = holder.getReport();
				System.out.println(r.getLocalizedMessage());
				printItems("",r.getItems());

			}
			if (beans == null )
			{
				System.out.println("import failed");
			}

			else
			{
				System.out.println("beans count = "+beans.size());
			}

			return beans;

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
			throw new RuntimeException("import failed , see log for details");
		}


	}

	/**
	 * @param beans
	 * @param manager
	 * @param parameters 
	 * @throws ChouetteException
	 */
	private void executeValidate(List<NeptuneIdentifiedObject> beans,
			INeptuneManager<NeptuneIdentifiedObject> manager, 
			Map<String, List<String>> parameters)
	throws ChouetteException 
	{
		Report valReport = manager.validate(null, beans, validationParameters);
		System.out.println(valReport.getLocalizedMessage());
		printItems("",valReport.getItems());
		int nbUNCHECK = 0;
		int nbOK = 0;
		int nbWARN = 0;
		int nbERROR = 0;
		int nbFATAL = 0;
		for (ReportItem item1  : valReport.getItems()) // Categorie
		{
			for (ReportItem item2 : item1.getItems()) // fiche
			{
				for (ReportItem item3 : item2.getItems()) //test
				{
					STATE status = item3.getStatus();
					switch (status)
					{
					case UNCHECK : nbUNCHECK++; break;
					case OK : nbOK++; break;
					case WARNING : nbWARN++; break;
					case ERROR : nbERROR++; break;
					case FATAL : nbFATAL++; break;
					}

				}

			}
		}
		System.out.println("Bilan : "+nbOK+" tests ok, "+nbWARN+" warnings, "+nbERROR+" erreurs, "+nbUNCHECK+" non effectués");
	}

	private void printItems(String indent,List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			System.out.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			printItems(indent+"   ",item.getItems());
		}

	}

	private void executeGetImportFormats(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters) throws ChouetteException
	{

		List<FormatDescription> formats = manager.getImportFormats(null);
		for (FormatDescription formatDescription : formats)
		{
			System.out.println(formatDescription);
		}


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
			List<String> sids = parameters.get("id");
			List<Long> ids = new ArrayList<Long>();

			for (String id : sids)
			{
				// Filter filter = Filter.getNewEqualsFilter("id", Long.valueOf(id));
				ids.add(Long.valueOf(id));
				// System.out.println("search for id "+Long.valueOf(id)+ "("+id+")");
				// NeptuneBean bean = manager.get(null, filter, NeptuneBeanManager.DETAIL_LEVEL.ATTRIBUTE);
				// System.out.println(bean);
			}
			filter = Filter.getNewInFilter("id", ids);
		}
		else if (parameters.containsKey("objectid"))
		{
			List<String> sids = parameters.get("objectid");
			filter = Filter.getNewInFilter("objectId", sids);
		}
		else if (parameters.containsKey("filterkey"))
		{
			String filterKey = getSimpleString(parameters,"filterkey");
			String filterOp = getSimpleString(parameters,"filterop","eq");
			if (filterOp.equalsIgnoreCase("eq"))
			{
				String value = getSimpleString(parameters,"filterval");
				filter = Filter.getNewEqualsFilter(filterKey, value);
			}
			else
			{
				throw new IllegalArgumentException("filterOp "+filterOp+" not yet implemented");
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

		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		if (parameters.containsKey("level"))
		{
			String slevel = getSimpleString(parameters,"level");
			if (slevel.equalsIgnoreCase("narrow"))
			{
				level = DetailLevelEnum.NARROW_DEPENDENCIES;
			}
			else if (slevel.equalsIgnoreCase("structure"))
			{
				level = DetailLevelEnum.STRUCTURAL_DEPENDENCIES;
			}
		}

		List<NeptuneIdentifiedObject> beans = manager.getAll(null, filter, level);

		System.out.println("beans count = "+beans.size());
		return beans;
	}

	/**
	 * @param beans
	 * @param parameters 
	 */
	private void executePrint(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) 
	{
		String slevel = getSimpleString(parameters, "level", "99");
		int level = Integer.parseInt(slevel);
		for (NeptuneObject bean : beans)
		{
			System.out.println(bean.toString("", level));
		}
	}

	/**
	 * @param beans
	 * @param manager
	 * @param parameters 
	 * @throws ChouetteException
	 */
	private void executeSave(List<NeptuneIdentifiedObject> beans,
			INeptuneManager<NeptuneIdentifiedObject> manager, 
			Map<String, List<String>> parameters)
	throws ChouetteException 
	{
		for (NeptuneIdentifiedObject bean : beans) 
		{
			manager.update(null, bean);
		}

	}

	/**
	 * @param beans
	 * @param manager
	 * @param parameters 
	 * @throws ChouetteException
	 */
	private void executeDelete(List<NeptuneIdentifiedObject> beans,
			INeptuneManager<NeptuneIdentifiedObject> manager, 
			Map<String, List<String>> parameters)
	throws ChouetteException 
	{
		manager.removeAll(null, beans,false);
		beans.clear();
	}

	/**
	 * @param beans
	 * @param manager
	 * @param parameters 
	 * @throws ChouetteException
	 */
	private List<NeptuneIdentifiedObject> executeNew(
			INeptuneManager<NeptuneIdentifiedObject> manager, 
			Map<String, List<String>> parameters)
			throws ChouetteException 
			{

		NeptuneIdentifiedObject bean = 	manager.getNewInstance(null);
		List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
		beans.add(bean);
		return beans;

			}


	private void executeSetReference(List<NeptuneIdentifiedObject> beans,
			Map<String, List<String>> parameters) throws Exception {
		if (beans.size() == 0)
		{
			throw new Exception("no bean to update, process stopped ");
		}
		if (beans.size() > 1)
		{
			throw new Exception("multiple beans to update, process stopped ");
		}
		NeptuneIdentifiedObject bean = beans.get(0);
		String name = getSimpleString(parameters, "attrname");
		Class<? extends NeptuneIdentifiedObject> beanClass = bean.getClass();
		Method setter = findSetter(beanClass, name);
		Class<?> type = setter.getParameterTypes()[0];

		String typeName = type.getSimpleName().toLowerCase();
		INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(typeName);
		if (manager == null)
		{
			throw new Exception("unknown object "+typeName+ ", only "+Arrays.toString(managers.keySet().toArray())+" are managed");
		}
		String objectId = getSimpleString(parameters, "objectid");
		Filter filter = Filter.getNewEqualsFilter("objectId", objectId);
		NeptuneIdentifiedObject reference = manager.get(null, filter, DetailLevelEnum.ATTRIBUTE);
		setter.invoke(bean, reference);

	}

	/**
	 * @param beans
	 * @param parameters 
	 * @throws Exception 
	 */
	private void executeSetAttribute(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
	{
		if (beans.size() == 0)
		{
			throw new Exception("no bean to update, process stopped ");
		}
		if (beans.size() > 1)
		{
			throw new Exception("multiple beans to update, process stopped ");
		}
		NeptuneIdentifiedObject bean = beans.get(0);
		String attrname = getSimpleString(parameters, "attrname");
		String value = getSimpleString(parameters, "value",null);
		followAttribute(bean,attrname, value);

	}

	private void followAttribute(Object object, String attrname,
			String value) throws Exception
			{
		if (attrname.contains("."))
		{
			Class<?> type = object.getClass();
			String basename = attrname.substring(0,attrname.indexOf("."));
			Method getter = findGetter(type, basename);
			Object target = getter.invoke(object);
			if (target == null)
			{
				Class<?> targetType = getter.getReturnType();
				target = targetType.newInstance();
				Method setter = findSetter(type, basename);
				setter.invoke(object, target);
			}
			attrname = attrname.substring(attrname.indexOf(".")+1);
			followAttribute(target, attrname, value);
		}
		else
		{
			setAttribute(object, attrname, value);
		}

			}

	/**
	 * @param object
	 * @param attrname
	 * @param value
	 * @throws Exception
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private void setAttribute(Object object, String attrname,
			String value) throws Exception {
		if (attrname.equalsIgnoreCase("id")) 
		{
			throw new Exception("non writable attribute id for any object , process stopped ");
		}
		if (!attrname.toLowerCase().equals("objectid") && attrname.toLowerCase().endsWith("id")) 
		{
			throw new Exception("non writable attribute "+attrname+" use setReference instand , process stopped ");
		}
		Class<?> beanClass = object.getClass();
		Method setter = findSetter(beanClass, attrname);
		Class<?> type = setter.getParameterTypes()[0];
		//		System.out.println(type.getCanonicalName());
		//		System.out.println("array = "+type.isArray());
		//		System.out.println("primitive = "+type.isPrimitive());
		//		System.out.println("enum = "+type.isEnum());
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
		String setterName = "set"+attribute;
		Method[] methods = beanClass.getMethods();
		Method setter = null;
		for (Method method : methods) 
		{
			if (method.getName().equalsIgnoreCase(setterName))
			{
				setter = method;
				break;
			}
		}
		if (setter == null)
		{
			throw new Exception("unknown attribute "+attribute+" for object "+beanClass.getName()+", process stopped ");
		}
		return setter;
	}
	/**
	 * @param beanClass
	 * @param attribute
	 * @return
	 * @throws Exception
	 */
	private Method findGetter(
			Class<?> beanClass, String attribute)
	throws Exception {
		String getterName = "get"+attribute;
		Method[] methods = beanClass.getMethods();
		Method getter = null;
		for (Method method : methods) 
		{
			if (method.getName().equalsIgnoreCase(getterName))
			{
				getter = method;
				break;
			}
		}
		if (getter == null)
		{
			throw new Exception("unknown attribute "+attribute+" for object "+beanClass.getName()+", process stopped ");
		}
		return getter;
	}


	private Object toObject(Class<?> type, String value) throws Exception 
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

		throw new Exception("unable to convert String to "+type.getCanonicalName());
	}

	private Object toPrimitive(Class<?> type, String value) throws Exception 
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

	private Object toEnum(Class<?> type, String value) throws Exception 
	{
		Method m = type.getMethod("fromValue", String.class);
		return m.invoke(null, value);
	}

	/**
	 *
	 */
	private static void printHelp()
	{
		System.out.println("Arguments : ");
		System.out.println("  -h(elp) for general syntax ");
		System.out.println("  -verbose for processing traces ");
		System.out.println("  -noDao to invalidate database access (MUST BE FIRST ARGUMENT) ");
		System.out.println("  -i(nteractive) switch to interactive mode");
		System.out.println("  -o(bject) neptuneObjectName (default object for commands)");
		System.out.println("  -f(ile) [fileName] : read commands in file");
		System.out.println("                       only one command by line");
		System.out.println("                       -c(ommand) argument is implicit");
		System.out.println("                       arguments with whitespaces must be doublequoted");
		System.out.println("  -c(ommand) [commandName] : see below");
		printCommandSyntax(false);
		System.out.println("\nNotes: ");
		System.out.println("    -c(ommand) can be chained : new occurence of command must be followed by it's specific argument");
		System.out.println("               commands are executed in argument order ");
		System.out.println("               last returned objects of reading commands are send to command wich needs objects as imput");
		System.out.println("    -o(bject) argument may be added for each command to switch object types, switch is conserved on further commands");
	}

	/**
	 * 
	 */
	private static void printCommandSyntax(boolean interactive) 
	{
		System.out.println("     delete : delete from database last readed Neptune objects");
		System.out.println("\n     export : write Neptune Objects to file");
		System.out.println("        -format formatName : format name");
		System.out.println("        launch getExportFormats for other parameters");
		System.out.println("\n     get : read Neptune Object from database");
		System.out.println("        -id [value+] : object technical id ");
		System.out.println("        -objectId [value+] : object neptune id ");
		System.out.println("        -level [attribute|narrow|full] : detail level (default = attribute)");
		System.out.println("        -orderBy [value+] : sort fields ");
		System.out.println("        -asc|-desc sort order (default = asc) ");
		System.out.println("\n     getExportFormats : print available export formats and arguments");
		System.out.println("\n     getImportFormats : print available import formats and arguments");
		System.out.println("\n     import : read Neptune Objects from file");
		System.out.println("        -format formatName : format name");
		System.out.println("        launch getImportFormats for other parameters");
		System.out.println("\n     new : create a new instance from scratch");
		System.out.println("\n     print : print previously readed Neptune Objects");
		System.out.println("        -level level : deep level for recursive print (default = 99)");
		System.out.println("\n     setAttribute : set value for a single cardinality attribute");
		System.out.println("        -attrname attributeName : name of the single cardinality atomic attribute to set");
		System.out.println("        -value newValue : new value to set (may be empty to unset)");
		System.out.println("                          if value is a date, it must be in one of these 3 formats :");
		System.out.println("                               yyyy-MM-dd");		
		System.out.println("                               yyyy-MM-dd_HH:mm:ss");		
		System.out.println("                               HH:mm:ss");		
		System.out.println("\n     setReference : set reference to another existing NeptuneObject");
		System.out.println("        -name attributeName : Neptune reference to set");
		System.out.println("        -objectId referenceId : NeptuneId of the Neptune Object to refer");
		System.out.println("\n     save : save last readed Neptune objects");
		System.out.println("\n     validate : launch validation process on previously readed NeptuneObject");
		if (interactive)
		   System.out.println("\n\n     exit or quit : terminate interactive session");
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
			if (args[i].startsWith("-"))
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
				else if (key.equals("file")) 
				{
					if (i == args.length -1) 
					{
						throw new Exception("missing filename");
					}
					String name = args[++i];
					if (name.startsWith("-"))
					{
						throw new Exception("missing filename before "+name);
					}
					commands.addAll(parseFile(name));

				}
				else
				{
					if (parameters.containsKey(key))
					{
						throw new Exception("duplicate parameter : -"+key);
					}
					List<String> list = new ArrayList<String>();

					if (i == args.length -1 || args[i+1].startsWith("-"))
					{
						list.add("true");
					}
					else
					{
						while ((i+1 < args.length && !args[i+1].startsWith("-")))
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

	@SuppressWarnings("unchecked")
	private List<CommandArgument> parseFile(String filename) throws Exception
	{
		File f = new File(filename);
		List<String> lines = FileUtils.readLines(f);
		List<CommandArgument> commands = new ArrayList<CommandArgument>();
		int linenumber=1;
		for (String line : lines) 
		{
			line = line.trim();
			if (!line.isEmpty() && !line.startsWith("#"))
			{
				CommandArgument command = parseLine(linenumber++, line);
				if (command != null)
					commands.add(command);
			}
		}
		return commands;

	}

	private CommandArgument parseLine(int linenumber,String line) throws Exception
	{
		CommandArgument command = null;
		String[] args = splitLine(linenumber,line);
		if (args.length == 0)
		{
			return null;
		}

		if (linenumber==1 && args[0].startsWith("-"))
		{
			parseArgs(args);
		}
		else
		{
			command = new CommandArgument(args[0]);
			Map<String, List<String>> parameters = command.getParameters();
			for (int i = 1; i < args.length; i++)
			{
				if (args[i].startsWith("-"))
				{
					String key = args[i].substring(1).toLowerCase();
					if (key.length() == 1) 
					{
						String alias = shortCuts.get(key);
						if (alias != null) key = alias;
					}
					if (key.equals("command")) 
					{
						throw new Exception("Line "+linenumber+": multiple command on one line is forbidden");					
					}
					else
					{
						if (parameters.containsKey(key))
						{
							throw new Exception("Line "+linenumber+": duplicate parameter : -"+key);
						}
						List<String> list = new ArrayList<String>();

						if (i == args.length -1 || args[i+1].startsWith("-"))
						{
							list.add("true");
						}
						else
						{
							while ((i+1 < args.length && !args[i+1].startsWith("-")))
							{
								list.add(args[++i]);
							}
						}
						parameters.put(key,list);
					}
				}
				else
				{
					throw new Exception("Line "+linenumber+": unexpected argument outside a key : "+args[i]);
				}
			}
		}

		return command;
	}

	private String[] splitLine(int linenumber,String line) throws Exception 
	{
		String[] args1 = line.split(" ");
		if (!line.contains("\"")) return args1;
		List<String>  args = new ArrayList<String>();
		String assembly = null;
		boolean quote = false;
		for (int i = 0; i < args1.length; i++)
		{
			if (quote)
			{
				assembly+=" "+args1[i];
				if (assembly.endsWith("\""))
				{
					quote = false;
					args.add(assembly.substring(1,assembly.length()-1));
				}
			}
			else if (args1[i].startsWith("\""))
			{
				if (args1[i].endsWith("\""))
				{
					args.add(args1[i].substring(1,args1[i].length()-1));
				}
				else
				{
					quote = true;
					assembly = args1[i];
				}
			}
			else
			{
				args.add(args1[i]);
			}
		}
		if (quote) throw new Exception("Line "+linenumber+": missing ending doublequote");
		return args.toArray(new String[0]);
	}

}
