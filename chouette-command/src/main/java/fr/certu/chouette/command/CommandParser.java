/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 *
 */
/**
 * @author mamadou
 *
 */

public abstract class CommandParser
{

	public static List<CommandArgument> parseArgs(String[] args) throws Exception
	{
		Map<String, List<String>> parameters = Command.globals;
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
					String alias = Command.shortCuts.get(key);
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

	public static List<CommandArgument> parseFile(String filename) throws Exception
	{
		File f = new File(filename);
		List<String> lines = FileUtils.readLines(f);
		List<CommandArgument> commands = new ArrayList<CommandArgument>();
		int linenumber=1;
		for (int i = 0; i < lines.size(); i++) 
		{
			String line = lines.get(i).trim();
			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;
			if (!line.isEmpty() && !line.startsWith("#"))
			{
				int number = linenumber++;
				while (line.endsWith("\\"))
				{
					line = line.substring(0, line.length()-1);
					i++;
					if (i < lines.size()) 
						line += lines.get(i).trim();
				}
				CommandArgument command = parseLine(number, line);
				if (command != null)
				{
					if (command.getName().equalsIgnoreCase("include"))
					{

					}
					else
					{
						commands.add(command);
					}
				}

			}
		}
		return commands;

	}

	public static CommandArgument parseLine(int linenumber,String line) throws Exception
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
				String arg = args[i].trim();
				if (arg.isEmpty()) continue;
				if (arg.startsWith("-"))
				{
					String key = arg.substring(1).toLowerCase();
					if (key.length() == 1) 
					{
						String alias = Command.shortCuts.get(key);
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
								if (!args[++i].trim().isEmpty())
									list.add(args[i]);
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

	private static String[] splitLine(int linenumber,String line) throws Exception 
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
