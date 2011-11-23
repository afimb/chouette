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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
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
/**
 * @author mamadou
 *
 */
@NoArgsConstructor
public class Command
{

   private static final Logger logger = Logger.getLogger(Command.class);
   private static ClassPathXmlApplicationContext applicationContext;

   private static enum ATTR_CMD {SET_VALUE, ADD_VALUE, REMOVE_VALUE,SET_REF,ADD_REF,REMOVE_REF};

   @Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

   @Setter private ValidationParameters validationParameters;

   @Setter private MigrateSchema migrationTool;
   
   @Setter private CheckObjectId checkObjectId;


   private Map<String,List<String>> globals = new HashMap<String, List<String>>();;

   private static Map<String,String> shortCuts ;

   private boolean verbose = false;

   private static boolean dao = true;

   private static Locale locale = Locale.getDefault();

   static
   {
      shortCuts = new HashMap<String, String>();
      shortCuts.put("c", "command");
      shortCuts.put("h", "help");
      shortCuts.put("o", "object");
      shortCuts.put("f", "file");
      shortCuts.put("i", "interactive");
      shortCuts.put("l", "level");
      shortCuts.put("v", "verbose");
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
               dao = false;
            } 
            catch (Exception e) 
            {

               System.err.println("cannot remove dao : "+e.getLocalizedMessage());
            }
         }
         applicationContext = new ClassPathXmlApplicationContext(context);
         ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
         Command command = (Command) factory.getBean("Command");

         initDao();

         command.execute(args);

         closeDao();
      }
      else
      {
         printHelp();
      }
   }

   /**
    * @param factory
    */
   private static void closeDao() {
      if (dao)
      {
         ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
         SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
         SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
         SessionFactoryUtils.closeSession(sessionHolder.getSession());
      }
   }

   /**
    * @param factory
    */
   private static void initDao() {
      if (dao)
      {
         ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
         SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
         Session session = SessionFactoryUtils.getSession(sessionFactory, true);
         TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
      }
   }

   private static void flushDao()
   {
      closeDao();
      initDao();
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
      if (getBoolean(globals,"migrate_schema"))
      {
         try
         {
            migrationTool.migrate();
         }
         catch (ChouetteException e)
         {
            logger.error("migration failure",e);
            System.err.println("migration failed");
         }
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

      List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
      int commandNumber = 0;
      if (getBoolean(globals, "interactive"))
      {
         String line = "";
         verbose = true;
         BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
         String activeObject = getActiveObject(globals);
         while (true)
         {
            try 
            {
               System.out.print(activeObject+" ("+beans.size()+") >");
               line = in.readLine();
               if (line == null) return;
               line = line.trim();
            } 
            catch (Exception e) 
            {
               System.err.println("cannot read input");
               logger.error("cannot read stdin",e);
               return;
            }
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")  || line.equalsIgnoreCase("q")) break;
            if (!line.startsWith("#")) 
            {
               try 
               {
                  CommandArgument command = parseLine(++commandNumber, line);
                  if (command.getName().equalsIgnoreCase("exec"))
                  {
                     String file = getSimpleString(command.getParameters(), "file");
                     List<CommandArgument> cmds = parseFile(file);
                     int cmdNum = 1;
                     for (CommandArgument cmd : cmds) 
                     {
                        commandNumber++;
                        beans = executeCommand(beans, cmdNum++, cmd);
                     }

                  }
                  else
                  {	
                     beans = executeCommand(beans, commandNumber, command);
                  }
                  activeObject = getActiveObject(command.getParameters());
               } 
               catch (Exception e) 
               {
                  logger.error(e.getMessage(),e);
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
         System.out.println("Command "+commandNumber+" : "+name);
         for (String key : parameters.keySet())
         {
            System.out.println("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
         }
      }
      logger.info("Command "+commandNumber+" : "+name);
      for (String key : parameters.keySet())
      {
         logger.info("    parameters "+key+" : "+ Arrays.toString(parameters.get(key).toArray()));
      }

      if (name.equals("verbose"))
      {
         verbose = !(getBoolean(parameters, "off")) ;
         return beans;
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
         return beans;
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
         return beans;
      }
      
      if (name.equals("checkObjectId"))
      {
         String fileName = getSimpleString(parameters, "sqlfile", "invalid.sql");
         boolean checkType = getBoolean(parameters, "checktype");
         String prefix = getSimpleString(parameters, "objectidprefix", null);
         checkObjectId.checkObjectId(fileName,checkType,prefix);
         return beans;
      }

      INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
      long tdeb = System.currentTimeMillis();

      if (name.equals("get"))
      {
         beans = executeGet(manager,parameters);
      }
      else if (name.equals("new"))
      {
         beans = executeNew(manager,parameters);
      }
      else if (name.equals("set"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : setAttribute must follow a reading command");
         executeSet(beans, parameters);
      }
      else if (name.equals("add"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : setAttribute must follow a reading command");
         executeAdd(beans, parameters);
      }
      else if (name.equals("remove"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : setAttribute must follow a reading command");
         executeRemove(beans, parameters);
      }
      else if (name.equals("save"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : save must follow a reading command");
         executeSave(beans, manager,parameters);
      }
      else if (name.equals("delete"))
      {
         if (beans == null || beans.isEmpty()) 
         {
            System.out.println("Command "+commandNumber+": nothing to delete");
         }
         else
         {
            executeDelete(beans, manager,parameters);
         }
      }
      else if (name.equals("complete"))
      {
         if (beans == null || beans.isEmpty()) 
         {
            System.out.println("Command "+commandNumber+": nothing to complete");
         }
         else
         {
            executeComplete(beans, manager, parameters);
         }
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
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : print must follow a reading command");
         executePrint(beans,parameters);
      }
      else if (name.equals("validate"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : validate must follow a reading command");
         executeValidate(beans,manager,parameters);
      }
      else if (name.equals("getExportFormats"))
      {
         executeGetExportFormats(manager,parameters);
      }
      else if (name.equals("export"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : export must follow a reading command");
         executeExport(beans,manager,parameters);
      }
      else if (name.equals("getDeletionExportFormats"))
      {
         executeGetDeletionFormats(manager,parameters);
      }
      else if (name.equals("exportForDeletion"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : export must follow a reading command");
         executeExportDeletion(beans,manager,parameters);
      }
      else if (name.equals("info"))
      {
         executeInfo(manager);
      }
      else if (name.equals("setValidationParameters"))
      {
         executeSetValidationParameters(parameters);
      }
      else if (name.equals("showValidationParameters"))
      {
         executeShowValidationParameters();
      }
      else if (name.equals("infoValidationParameters"))
      {
         executeInfoValidationParameters();
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
      return beans;
   }


   private void executeShowValidationParameters() 
   {
      if (validationParameters == null)
      {
         System.out.println("no validationParameters defined ; use setValidationParameters to initialize it");
      }
      else
      {
         System.out.println(validationParameters);
      }

   }

   private void executeSetValidationParameters(Map<String, List<String>> parameters) 
   {
      for (String key : parameters.keySet()) 
      {
         String value = getSimpleString(parameters,key);
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

   private void executeInfoValidationParameters() throws Exception
   {
      try
      {
         Class<?> c = validationParameters.getClass();
         Field[] fields =  c.getDeclaredFields();
         for (Field field : fields) 
         {
            if (field.getName().equals("test3_2_Polygon")) continue;
            int m = field.getModifiers();
            if (Modifier.isPrivate(m) && !Modifier.isStatic(m) )
            {
               printField(c,field,"");
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }

   }
   /**
    * @param beans
    * @param manager
    * @param parameters
    */
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
            throw new IllegalArgumentException("format "+format+" unavailable, check command getExportFormats for list ");
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
                  throw new IllegalArgumentException("parameter -"+name+" is required, check command getExportFormats for list ");
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
                     throw new IllegalArgumentException("parameter -"+name+" must be unique, check command getExportFormats for list ");
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
               }
            }
         }

         ReportHolder holder = new ReportHolder();
         manager.doExport(null, beans, format, values, holder );
         PrintStream stream = System.out;
         if (holder.getReport() != null)
         {
            Report r = holder.getReport();
            stream.println(r.getLocalizedMessage());
            printItems(stream,"",r.getItems());
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
   /**
    * convert date string to calendar
    * @param simpleval
    * @return
    */
   private Calendar toCalendar(String simpleval)
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
    * @param beans
    * @param manager
    * @param parameters
    */
   private void executeExportDeletion(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) 
   {
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
            printItems(System.out,"",r.getItems());
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

   /**
    * @param manager
    * @param parameters
    * @throws ChouetteException
    */
   private void executeGetExportFormats(
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) 
   throws ChouetteException 
   {

      List<FormatDescription> formats = manager.getExportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
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

   /**
    * @param manager
    * @param parameters
    * @return
    */
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
                  case DATE : val.setDateValue(toCalendar(simpleval));break;
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
            printItems(System.out,"",r.getItems());

         }
         if (beans == null || beans.isEmpty())
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
      String fileName = getSimpleString(parameters, "file", "");
      boolean append = getBoolean(parameters, "append");

      Report valReport = manager.validate(null, beans, validationParameters);
      PrintStream stream = System.out;
      if (!fileName.isEmpty())
      {
         try 
         {
            stream = new PrintStream(new FileOutputStream(new File(fileName), append));
         } catch (FileNotFoundException e) 
         {
            System.err.println("cannot open file :"+fileName);
            fileName = "";
         }
      }

      stream.println(valReport.getLocalizedMessage());
      printItems(stream,"",valReport.getItems());
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
      stream.println("Bilan : "+nbOK+" tests ok, "+nbWARN+" warnings, "+nbERROR+" erreurs, "+nbUNCHECK+" non effectués");
      if (!fileName.isEmpty())
      {
         stream.close();
      }
   }

   private void printItems(PrintStream stream, String indent,List<ReportItem> items) 
   {
      if (items == null) return;
      for (ReportItem item : items) 
      {
         stream.println(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
         printItems(stream,indent+"   ",item.getItems());
      }

   }

   private void executeGetImportFormats(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters) throws ChouetteException
   {

      List<FormatDescription> formats = manager.getImportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
      }

   }

   private void executeGetDeletionFormats(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters) throws ChouetteException
   {

      List<FormatDescription> formats = manager.getDeleteExportFormats(null);
      for (FormatDescription formatDescription : formats)
      {
         System.out.println(formatDescription.toString(locale));
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
      flushDao();
      Filter filter = null;
      if (parameters.containsKey("id"))
      {
         List<String> sids = parameters.get("id");
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
         else if (filterArgs.size() == 4)
         {
            throw new IllegalArgumentException(filterOp+" : invalid syntax or not yet implemented");
         }
         else
         {
            if (filterOp.equalsIgnoreCase("in"))
            {
               List<String> values = filterArgs.subList(2, filterArgs.size());
               filter = Filter.getNewInFilter(filterKey, values );
            }
            else
            {
               throw new IllegalArgumentException(filterOp+" : invalid syntax or not yet implemented");
            }
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

      String limit = getSimpleString(parameters, "limit","10");
      if (limit.equalsIgnoreCase("none"))
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
    * @param beans
    * @param parameters 
    */
   private void executePrint(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) 
   {
      String slevel = getSimpleString(parameters, "level", "0");
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
         boolean propagate = getBoolean(parameters, "propagate");
         boolean slow = getBoolean(parameters, "slow");
         List<NeptuneIdentifiedObject> oneBean = new ArrayList<NeptuneIdentifiedObject>();
         oneBean.add(bean);
         manager.saveAll(null, oneBean, propagate, !slow);
      }

      //		boolean propagate = getBoolean(parameters, "propagate");
      //		boolean slow = getBoolean(parameters, "slow");
      //		manager.saveAll(null, beans, propagate, !slow);
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
      boolean propagate = getBoolean(parameters, "propagate");
      /*
		for (NeptuneIdentifiedObject bean : beans) 
		{
			Filter filter = Filter.getNewEqualsFilter("id", bean.getId());
			manager.removeAll(null, filter);
		}
       */

      manager.removeAll(null, beans,propagate);
      beans.clear();
   }

   /**
    * @param beans
    * @param manager
    * @param parameters 
    * @throws ChouetteException
    */
   private void executeComplete(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager, 
         Map<String, List<String>> parameters)
   throws ChouetteException 
   {
      for (NeptuneIdentifiedObject bean : beans) 
      {
         manager.completeObject(null, bean);
      }

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



   /**
    * @param beans
    * @param parameters 
    * @throws Exception 
    */
   private void executeSet(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
   {
      updateAttribute("SET", beans, parameters);
   }

   /**
    * @param beans
    * @param parameters 
    * @throws Exception 
    */
   private void executeAdd(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
   {
      updateAttribute("ADD", beans, parameters);
   }
   /**
    * @param beans
    * @param parameters 
    * @throws Exception 
    */
   private void executeRemove(List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
   {
      updateAttribute("REMOVE", beans, parameters);
   }


   /**
    * @param cmd
    * @param beans
    * @param parameters
    * @throws Exception
    */
   private void updateAttribute(String cmd,List<NeptuneIdentifiedObject> beans, Map<String, List<String>> parameters) throws Exception 
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
      List<String> args = parameters.get("attr");
      if (args != null)
      {
         if (args.isEmpty())
         {
            throw new Exception ("command set -attr : missing arguments : name value");
         }
         String attrname = args.get(0);
         String value = null;
         if (args.size() > 1)
         {
            value = args.get(1);
         }
         ATTR_CMD c = ATTR_CMD.valueOf(cmd+"_VALUE");
         followAttribute(c, bean,attrname, value);
      }
      else
      {
         args = parameters.get("ref");
         if (args == null)
         {
            throw new Exception ("command set must have -attr or -ref argument");
         }
         if (args.isEmpty())
         {
            throw new Exception ("command set -ref : missing arguments : ref objectId");
         }
         String attrname = args.get(0);
         String value = null;
         if (args.size() > 1)
         {
            value = args.get(1);
         }
         ATTR_CMD c = ATTR_CMD.valueOf(cmd+"_REF");
         followAttribute(c, bean,attrname, value);
      }

   }
   /**
    * @param beans
    * @param parameters 
    * @throws Exception 
    */
   private void executeInfo(INeptuneManager<NeptuneIdentifiedObject> manager) throws Exception 
   {
      Object object = manager.getNewInstance(null);
      printFields(object,"");


   }

   /**
    * @param object
    * @throws Exception
    */
   private void printFields(Object object,String indent) throws Exception 
   {
      try
      {
         Class<?> c = object.getClass();
         Field[] fields = c.getSuperclass().getDeclaredFields();
         for (Field field : fields) 
         {
            int m = field.getModifiers();
            if (Modifier.isPrivate(m) && !Modifier.isStatic(m))
            {
               printField(c,field,indent);
            }


         }

         fields = c.getDeclaredFields();
         for (Field field : fields) 
         {
            int m = field.getModifiers();
            if (Modifier.isPrivate(m) && !Modifier.isStatic(m))
            {
               printField(c,field,indent);
            }


         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
   }

   /**
    * @param objectType
    * @param field
    * @param indent
    * @throws Exception
    */
   private void printField(Class<?> objectType, Field field,String indent) throws Exception
   {
      String fieldName = field.getName().toLowerCase();
      if (fieldName.equals("importeditems")) return;
      if (fieldName.endsWith("id") || fieldName.endsWith("ids"))
      {
         if (!fieldName.equals("objectid") && !fieldName.equals("creatorid") && !fieldName.equals("areacentroid"))
            return;
      }
      if (findAccessor(objectType, field.getName(), "get", false) == null 
            && findAccessor(objectType, field.getName(), "is", false) == null )	
      {
         return;
      }
      Class<?> type = field.getType();

      if (type.isPrimitive())
      {
         System.out.print(indent+"- "+field.getName());
         System.out.print(" : type "+type.getName());
         if (findAccessor(objectType, field.getName(), "set", false) == null)	
         {
            System.out.print(" (readonly)");
         }
      }
      else
      {
         if (type.getSimpleName().equals("List"))
         {
            String name = field.getName();
            name = name.substring(0,name.length()-1);
            ParameterizedType ptype = (ParameterizedType) field.getGenericType();
            Class<?> itemType = (Class<?>) ptype.getActualTypeArguments()[0];
            System.out.print(indent+"- "+name);
            System.out.print(" : collection of type "+itemType.getSimpleName());
            if (findAccessor(objectType, name, "add", false) != null)	
            {
               System.out.print(" (add allowed)");
            }
            if (findAccessor(objectType, name, "remove", false) != null)	
            {
               System.out.print(" (remove allowed)");
            }
            type = itemType;
         }
         else
         {
            System.out.print(indent+"- "+field.getName());
            System.out.print(" : type "+type.getSimpleName());
            if (findAccessor(objectType, field.getName(), "set", false) == null)	
            {
               System.out.print(" (readonly)");
            }
         }
      }
      System.out.println("");
      if (!type.isPrimitive())
         printFieldDetails(type, indent);
   }

   /**
    * @param itemType
    * @param indent
    * @throws Exception
    */
   private void printFieldDetails(Class<?> itemType, String indent)
   throws Exception 
   {
      String itemName = itemType.getName();
      if (itemName.startsWith("fr.certu.chouette.model.neptune.type."))
      {
         if (itemName.endsWith("Enum"))
         {
            Field[] fields = itemType.getDeclaredFields();
            System.out.print(indent+"     ");

            String text = "";
            for (Field field : fields) 
            {
               int m = field.getModifiers();
               if (Modifier.isPublic(m) && Modifier.isStatic(m) && Modifier.isFinal(m))
               {
                  Object instance = field.get(null);
                  String name = instance.toString();
                  if (text.length() + name.length() > 79)
                  {
                     System.out.print(text+"\n"+indent+"     ");
                     text = "";
                  }
                  text += name+" ";
               }
            }
            System.out.println(text);
         }
         else
         {
            Object instance = itemType.newInstance();
            printFields(instance, indent+"     ");
         }
      }
      else if (itemName.startsWith("fr.certu.chouette.model.neptune."))
      {
         Object instance = itemType.newInstance();
         if (instance instanceof NeptuneIdentifiedObject)
         {
            String simpleName = itemType.getSimpleName();
            if (simpleName.equals("AreaCentroid"))
            {
               printFields(instance, indent+"     ");
            }
         }
         else
         {
            printFields(instance, indent+"     ");
         }


      }
   }


   /**
    * 
    * @param object
    * @param bean 
    * @param attrname
    * @param value
    * @throws Exception
    */
   private void followAttribute(ATTR_CMD cmd, Object object, String attrname,
         String value) 
   throws Exception
   {
      if (attrname.contains("."))
      {
         Class<?> type = object.getClass();
         String basename = attrname.substring(0,attrname.indexOf("."));
         Object target = null;
         if (basename.endsWith("]"))
         {
            String srank = basename.substring(basename.indexOf("[")+1, basename.indexOf("]"));
            basename = basename.substring(0, basename.indexOf("["));
            if (srank.equalsIgnoreCase("new"))
            {
               Method add = findAdder(type, basename);
               target = add.getParameterTypes()[0].newInstance();
               add.invoke(object, target);
            }
            else 
            {
               Method getter= findGetter(type, basename+"s");
               List<?> collection = (List<?>) getter.invoke(object);
               if (collection == null || collection.isEmpty()) 
               {
                  throw new Exception("empty collection "+basename);
               }
               if (srank.equalsIgnoreCase("last"))
               {
                  target = collection.get(collection.size()-1);
               }
               else
               {
                  int rank = Integer.parseInt(srank);
                  if (rank < 0 || rank >= collection.size())
                  {
                     throw new Exception("index "+rank+" out of collection bounds "+collection.size());
                  }
                  target = collection.get(rank);
               }
            }
         }
         else
         {
            Method getter = findGetter(type, basename);
            target = getter.invoke(object);
            if (target == null)
            {
               Class<?> targetType = getter.getReturnType();
               target = targetType.newInstance();
               Method setter = findSetter(type, basename);
               setter.invoke(object, target);
            }
         }
         attrname = attrname.substring(attrname.indexOf(".")+1);
         followAttribute(cmd, target, attrname, value);
      }
      else
      {
         switch (cmd)
         {
         case SET_VALUE : setAttribute(object, attrname, value); break;
         case ADD_VALUE : addAttribute(object, attrname, value); break;
         case REMOVE_VALUE : removeAttribute(object, attrname, value); break;
         case SET_REF : setReference(object, attrname, value); break;
         case ADD_REF : addReference(object, attrname, value); break;
         case REMOVE_REF : removeReference(object, attrname, value); break;
         }

      }

   }

   private void removeAttribute(Object object, String attrname, String value) throws Exception 
   {
      Class<?> beanClass = object.getClass();
      Method adder = findAdder(beanClass,attrname);
      Class<?> type = adder.getParameterTypes()[0];
      if (type.getName().startsWith("fr.certu.chouette.model.neptune") &&
            !type.getName().startsWith("Enum"))
      {
         type = Integer.TYPE;
      }
      else
      {

      }
      Method remover = findRemover(beanClass, attrname,type);
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
      remover.invoke(object, arg);

   }

   private void addAttribute(Object object, String attrname, String value) throws Exception 
   {
      Class<?> beanClass = object.getClass();
      Method adder = findAdder(beanClass, attrname);
      Class<?> type = adder.getParameterTypes()[0];
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
      adder.invoke(object, arg);

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
    * @param object
    * @param refName
    * @param objectId
    * @throws Exception
    */
   private void setReference(Object object,String refName, String objectId) throws Exception 
   {
      Class<?> beanClass = object.getClass();
      Method method = findSetter(beanClass, refName);
      updateReference(object, objectId, method);
   }

   /**
    * @param object
    * @param refName
    * @param objectId
    * @throws Exception
    */
   private void addReference(Object object,String refName, String objectId) throws Exception 
   {
      Class<?> beanClass = object.getClass();
      Method method = findAdder(beanClass, refName);
      updateReference(object, objectId, method);
   }

   /**
    * @param object
    * @param refName
    * @param objectId
    * @throws Exception
    */
   private void removeReference(Object object,String refName, String objectId) throws Exception 
   {
      Class<?> beanClass = object.getClass();
      Method method = findRemover(beanClass, refName,String.class);
      updateReference(object, objectId, method);
   }

   /**
    * @param object
    * @param objectId
    * @param setter
    * @throws Exception
    */
   private void updateReference(Object object, String objectId, Method method)
   throws Exception {
      Class<?> type = method.getParameterTypes()[0];

      String typeName = type.getSimpleName().toLowerCase();
      INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(typeName);
      if (manager == null)
      {
         throw new Exception("unknown object "+typeName+ ", only "+Arrays.toString(managers.keySet().toArray())+" are managed");
      }
      Filter filter = Filter.getNewEqualsFilter("objectId", objectId);
      NeptuneIdentifiedObject reference = manager.get(null, filter);
      if (reference != null) 
      {
         method.invoke(object, reference);
      }
      else
      {
         throw new Exception(typeName+" with ObjectId = "+objectId+" does not exists");
      }
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
   /**
    * @param beanClass
    * @param attribute
    * @return
    * @throws Exception
    */
   private Method findGetter(
         Class<?> beanClass, String attribute)
   throws Exception 
   {
      return findAccessor(beanClass, attribute, "get",true);
   }

   /**
    * @param beanClass
    * @param attribute
    * @return
    * @throws Exception
    */
   private Method findAdder(
         Class<?> beanClass, String attribute)
   throws Exception {
      return findAccessor(beanClass, attribute, "add",true);
   }

   /**
    * @param beanClass
    * @param attribute
    * @return
    * @throws Exception
    */
   private Method findRemover(
         Class<?> beanClass, String attribute, Class<?> argType)
   throws Exception 
   {
      String methodName = "remove"+attribute;
      Method[] methods = beanClass.getMethods();
      Method accessor = null;
      for (Method method : methods) 
      {
         if (method.getName().equalsIgnoreCase(methodName))
         {
            Class<?> parmType = method.getParameterTypes()[0];
            if (argType.equals(parmType))
            {
               accessor = method;
               break;
            }
         }
      }
      if (accessor == null)
      {
         throw new Exception("unknown accessor remove for attribute "+attribute+" for object "+beanClass.getName()+" with argument type = "+argType.getSimpleName()+", process stopped ");
      }
      return accessor;
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
            String arg = args[i].trim();
            if (arg.isEmpty()) continue;
            if (arg.startsWith("-"))
            {
               String key = arg.substring(1).toLowerCase();
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



}
