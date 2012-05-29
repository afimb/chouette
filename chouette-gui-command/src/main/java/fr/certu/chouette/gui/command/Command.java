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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
// import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.Charset;
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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
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
   public static ClassPathXmlApplicationContext applicationContext;

   @Getter @Setter private Map<String,INeptuneManager<NeptuneIdentifiedObject>> managers;

   @Setter private ValidationParameters validationParameters;


   public Map<String,List<String>> globals = new HashMap<String, List<String>>();;

   public static Map<String,String> shortCuts ;

   public boolean verbose = false;

   public static boolean dao = true;

   public static Locale locale = Locale.getDefault();

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

//         if (args[0].equalsIgnoreCase("-noDao"))
//         {
//            List<String> newContext = new ArrayList<String>();
//            PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
//            try
//            {
//               Resource[] re = test.getResources("classpath*:/chouetteContext.xml");
//               for (Resource resource : re)
//               {
//                  if (! resource.getURL().toString().contains("dao"))
//                  {
//                     newContext.add(resource.getURL().toString());
//                  }
//               }
//               context = newContext.toArray(new String[0]);
//               dao = false;
//            } 
//            catch (Exception e) 
//            {
//
//               System.err.println("cannot remove dao : "+e.getLocalizedMessage());
//            }
//         }
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
    * @param factory
    */
   public static void closeDao() {
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
   public static void initDao() 
   {
      if (dao)
      {
         ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
         SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
         Session session = SessionFactoryUtils.getSession(sessionFactory, true);
         TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
      }
   }

   public static void flushDao()
   {
      closeDao();
      initDao();
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

      List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
      int commandNumber = 0;
//      if (getBoolean(globals, "interactive"))
//      {
//         String line = "";
//         verbose = true;
//         BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//         String activeObject = getActiveObject(globals);
//         while (true)
//         {
//            try 
//            {
//               System.out.print(activeObject+" ("+beans.size()+") >");
//               line = in.readLine();
//               if (line == null) return;
//               line = line.trim();
//            } 
//            catch (Exception e) 
//            {
//               System.err.println("cannot read input");
//               logger.error("cannot read stdin",e);
//               return;
//            }
//            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")  || line.equalsIgnoreCase("q")) break;
//            if (!line.startsWith("#")) 
//            {
//               try 
//               {
//                  CommandArgument command = parseLine(++commandNumber, line);
//                  if (command.getName().equalsIgnoreCase("exec"))
//                  {
//                     String file = getSimpleString(command.getParameters(), "file");
//                     List<CommandArgument> cmds = parseFile(file);
//                     int cmdNum = 1;
//                     for (CommandArgument cmd : cmds) 
//                     {
//                        commandNumber++;
//                        beans = executeCommand(beans, cmdNum++, cmd);
//                     }
//
//                  }
//                  else
//                  {	
//                     beans = executeCommand(beans, commandNumber, command);
//                  }
//                  activeObject = getActiveObject(command.getParameters());
//               } 
//               catch (Exception e) 
//               {
//                  logger.error(e.getMessage(),e);
//                  System.out.println(e.getMessage());
//               }
//            }
//
//         }
//
//      }
//      else
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
   public List<NeptuneIdentifiedObject> executeCommand(
         List<NeptuneIdentifiedObject> beans, int commandNumber,
         CommandArgument command) throws ChouetteException, Exception {
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

      INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
      long tdeb = System.currentTimeMillis();

      if (name.equals("get"))
      {
         beans = executeGet(manager,parameters);
      }
      else if (name.equals("save"))
      {
         if (beans == null || beans.isEmpty()) throw new Exception("Command "+commandNumber+": Invalid command sequence : save must follow a reading command");
         executeSave(beans, manager,parameters);
      }
      else if (name.equals("getImportFormats"))
      {
         executeGetImportFormats(manager,parameters);
      }
      else if (name.equals("import"))
      {
         beans = executeImport(manager,parameters);
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
    * @param parameters
    * @return
    */
//   private String getActiveObject(Map<String, List<String>> parameters) 
//   {
//      String object = null;
//      try
//      {
//         object = getSimpleString(parameters,"object").toLowerCase();
//      }
//      catch (IllegalArgumentException e)
//      {
//         object = getSimpleString(globals,"object","xxx").toLowerCase();
//      }
//      if (!managers.containsKey(object))
//      {
//         return "unknown object";
//      }
//      return object;
//   }

   /**
    * @param manager
    * @param parameters
    * @return
    */
   private List<NeptuneIdentifiedObject> executeImport(INeptuneManager<NeptuneIdentifiedObject> manager, Map<String, List<String>> parameters)
   {
      String reportFileName = getSimpleString(parameters, "reportfile", "");
      String reportFormat = getSimpleString(parameters, "reportformat", "txt");
      boolean append = getBoolean(parameters, "append");
      String format = getSimpleString(parameters,"format");
      PrintStream stream = System.out;
      String encoding = Charset.defaultCharset().toString();
      if (!reportFileName.isEmpty())
      {
         try 
         {
            if (reportFormat.equals("json"))
            {
               encoding = "UTF-8";
            }
            stream = new PrintStream(new FileOutputStream(new File(reportFileName), append ), true, encoding);
         } catch (IOException e) 
         {
            System.err.println("cannot open file :"+reportFileName+" "+e.getMessage());
            reportFileName = "";
         }
      }
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
            if (reportFormat.equals("json"))
            {
               stream.println(r.toJSON());
            }
            else
            {
               stream.println(r.getLocalizedMessage());
               printItems(stream,"",r.getItems());
            }

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
      finally
      {
         if (!reportFileName.isEmpty())
         {
            stream.close();
         }
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

   protected void printItems(PrintStream stream, String indent,List<ReportItem> items) 
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
