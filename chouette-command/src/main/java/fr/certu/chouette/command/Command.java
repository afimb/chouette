/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NoArgsConstructor;
import lombok.Setter;

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
import fr.certu.chouette.plugin.report.ReportHolder;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 *
 */
@NoArgsConstructor
public class Command
{

	private static ClassPathXmlApplicationContext applicationContext;

	@Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

	private Map<String,List<String>> parameters;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// pattern partially work
		String[] context = {"classpath*:/chouetteContext.xml"};
//		String[] context = {"classpath*:/ApplicationContext.xml",
//				"classpath*:/modules/managers.xml",
//				"classpath*:/modules/neptune_exchange.xml",
//				"classpath*:/modules/hibernate.xml"};
		
		PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
		try
		{
			Resource[] re = test.getResources("classpath*:/chouetteContext.xml");
			System.out.println("nb res = "+re.length);
			for (Resource resource : re)
			{
				System.out.println(resource.getURI().toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		applicationContext = new ClassPathXmlApplicationContext(context);
		ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
		Command command = (Command) factory.getBean("Command");
		command.execute(args);
	}

	/**
	 * @param args
	 */
	private void execute(String[] args)
	{
		parseArgs(args);

		for (String key : parameters.keySet())
		{
			System.out.println("parameter "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
		}

		try
		{
			String object = getSimpleString("object");
			if (getBoolean("help"))
			{
				printHelp();
			}
			INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(object);
			if (manager == null)
			{
				throw new IllegalArgumentException("unknown object "+object+ ", only "+Arrays.toString(managers.keySet().toArray())+" are managed");
			}
			String command = getSimpleString("command");
			if (command.equals("get"))
			{
				executeGet(manager);
			}
			else if (command.equals("getImportFormats"))
			{
				executeGetImportFormats(manager);
			}
			else if (command.equals("import"))
			{
				executeImport(manager);
			}
			else
			{
				System.out.println("invalid command :" +command);
			}

		}
		catch (Exception e)
		{
			if (getBoolean("help"))
			{
				printHelp();
			}
			else
			{
				System.err.println(e.getMessage());
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}

	}

	private void executeImport(INeptuneManager<NeptuneIdentifiedObject> manager)
	{
		String format = getSimpleString("format");
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
				printItems(r.getItems());
				
			}
			if (beans == null )
			{
				System.out.println("import failed");
			}

			else
			{
				System.out.println("beans count = "+beans.size());
				for (NeptuneObject bean : beans)
				{
					System.out.println(bean.toString("", 99));
				}
			}

		}
		catch (ChouetteException e)
		{
			System.err.println(e.getMessage());
			
			Throwable caused = e.getCause();
			while (caused != null)
			{
				System.err.println("caused by "+ caused.getMessage());
				caused = caused.getCause();
			}
		}


	}

	private void printItems(List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			System.out.println(item.getLocalizedMessage());
			printItems(item.getItems());
		}
		
	}

	private void executeGetImportFormats(INeptuneManager<NeptuneIdentifiedObject> manager)
	{
		try
		{
			List<FormatDescription> formats = manager.getImportFormats(null);
			for (FormatDescription formatDescription : formats)
			{
				System.out.println(formatDescription);
			}
		}
		catch (ChouetteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param manager
	 * @throws ChouetteException
	 */
	private void executeGet(INeptuneManager<NeptuneIdentifiedObject> manager)
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
		else
		{
			filter = Filter.getNewEmptyFilter();
		}

		if (parameters.containsKey("orderby"))
		{
			List<String> orderFields = parameters.get("orderby");

			boolean desc = getBoolean("desc");

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
			String slevel = getSimpleString("level");
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
		for (NeptuneObject bean : beans)
		{
			System.out.println(bean.toString("", 99));
		}

	}


	/**
	 *
	 */
	private void printHelp()
	{
		System.out.println("Arguments : ");
		System.out.println("  -help for general syntax ");
		System.out.println("  -object [name] [options]  ");
		System.out.println("  options : ");
		System.out.println("     -help for specific options upon object ");
		System.out.println("     -command [commandName] : get, getImportFormats, import");
		System.out.println("     get : ");
		System.out.println("        -id [value+] : object technical id ");
		System.out.println("        -objectId [value+] : object neptune id ");
		System.out.println("        -level [attribute|narrow|full] : detail level (default = attribute)");
		System.out.println("        -orderBy [value+] : sort fields ");
		System.out.println("        -asc|-desc sort order (default = asc) ");
		System.out.println("     import : ");
		System.out.println("        -format formatName : format name");
		System.out.println("        launch getImportFormats for other parameters");

	}

	/**
	 * @param string
	 * @return
	 */
	private String getSimpleString(String key)
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
	private boolean getBoolean(String key)
	{
		List<String> values = parameters.get(key);
		if (values == null) return false;
		if (values.size() > 1) throw new IllegalArgumentException("parameter -"+key+" of boolean type must be unique");
		return Boolean.parseBoolean(values.get(0));
	}

	private void parseArgs(String[] args)
	{
		parameters = new HashMap<String, List<String>>();
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
				if (parameters.containsKey(key))
				{
					System.err.println("duplicate parameter : -"+key);
					System.exit(2);
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

}
