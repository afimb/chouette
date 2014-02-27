/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.gui.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * 
 * import command : -c import -o line -inputFile YYYY -importId ZZZ
 * 
 * export command : selected objects -c export -o line -format XXX -outputFile
 * YYYY -exportId ZZZ -id list_of_id_separated_by_commas ... all objects -c
 * export -o line -format XXX -outputFile YYYY -exportId ZZZ dependency criteria
 * : sample for all lines of one network -c export -o network -format XXX
 * -outputFile YYYY -exportId ZZZ -id list_of_network_id_separated_by_commas
 * 
 * validate command : from neptune file : -c validate -o line -inputFile YYYY
 * [-fileFormat TTT] -validationId ZZZ
 * 
 * from database : -c validate -o line|network|company -validationId ZZZ [-id
 * list_of_ids_separated_by_commas]
 * 
 */
@NoArgsConstructor
public class Command extends AbstractCommand
{

   private static final Logger logger = Logger.getLogger(Command.class);
   // public static ClassPathXmlApplicationContext applicationContext;
   @Getter @Setter
   private static BeanFactory beanFactory;

   /**
    *
    */
   public static void printHelp()
   {
      ResourceBundle bundle = null;
      try
      {
         bundle = ResourceBundle.getBundle(Command.class.getName(), locale);
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

      printBloc(bundle, "Header", "");

      printBloc(bundle, "Option", "   ");

      System.out.println("");

      String[] commands = getHelpString(bundle, "Commands").split(" ");
      for (String command : commands)
      {
         printCommandDetail(bundle, command, "   ");
         System.out.println("");
      }

      printBloc(bundle, "Footer", "");
   }

   @Getter
   @Setter
   private ImportCommand importCommand;

   @Getter
   @Setter
   private ExportCommand exportCommand;

   @Getter
   @Setter
   private ValidateCommand validateCommand;

   private static String getHelpString(ResourceBundle bundle, String key)
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

   private static void printBloc(ResourceBundle bundle, String key, String indent)
   {
      // print options
      String line = null;
      int rank = 1;
      do
      {
         line = getHelpString(bundle, key + rank);
         if (line != null)
         {
            System.out.println(indent + line);
            printBloc(bundle, key + rank + "_", indent + "   ");
         }
         rank++;
      }
      while (line != null);
   }

   private static void printCommandDetail(ResourceBundle bundle, String key, String indent)
   {
      // print command
      String line = getHelpString(bundle, key);
      if (line == null)
      {
         System.out.println("-- unknown command : " + key);
         return;
      }
      System.out.println(indent + line);
      printBloc(bundle, key + "_", indent + "   ");
      line = getHelpString(bundle, key + "_n");
      if (line != null)
      {
         System.out.println(indent + "   " + line);
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
         bundle = ResourceBundle.getBundle(Command.class.getName(), locale);
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
      printCommandDetail(bundle, lowerCommand, "   ");

   }

   /**
    * 
    */
   private static void printCommandSyntax(boolean interactive)
   {
      ResourceBundle bundle = null;
      try
      {
         bundle = ResourceBundle.getBundle(Command.class.getName(), locale);
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

      String[] commands = getHelpString(bundle, "Commands").split(" ");
      if (interactive)
      {
         for (String command : commands)
         {
            String line = getHelpString(bundle, command);
            System.out.println("   " + line);
         }
      }
      else
      {
         for (String command : commands)
         {
            printCommandDetail(bundle, command, "   ");
            System.out.println("");
         }
      }

   }

   public Map<String, List<String>> globals = new HashMap<String, List<String>>();

   public static Map<String, String> shortCuts;

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
   public static void initDao()
   {
      EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
      EntityManager entityManager = entityManagerFactory.createEntityManager();
      TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
   }

   /**
    * @param factory
    */
   public static void closeDao()
   {
      EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
      EntityManagerHolder holder = (EntityManagerHolder)
            TransactionSynchronizationManager.unbindResource(entityManagerFactory);
      EntityManager em = holder.getEntityManager();
      EntityTransaction tx = em.getTransaction();
      try
      {
         tx.begin();
         em.flush();
         tx.commit();
      }
      catch (PersistenceException e)
      {
         tx.rollback();
      }      
      EntityManagerFactoryUtils.closeEntityManager(em);
   }

   public static EntityManagerFactory getEntityManagerFactory()
   {
      EntityManagerFactory entityManagerFactory = (EntityManagerFactory) beanFactory.getBean("entityManagerFactory");
      return entityManagerFactory;
   }

   public static EntityManager getEntityManager()
   {
      return EntityManagerFactoryUtils.getTransactionalEntityManager(getEntityManagerFactory());
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      // pattern partially work
      String[] context = { "classpath*:/chouetteContext.xml" };

      if (args.length >= 1)
      {
         if (args[0].equalsIgnoreCase("-help") || args[0].equalsIgnoreCase("-h"))
         {
            printHelp();
            System.exit(0);
         }
         int code = 0;
         Command command = null;
         try
         {
            ClassPathXmlApplicationContext applicationContext =  new ClassPathXmlApplicationContext(context);
            applicationContext.registerShutdownHook();
            ConfigurableBeanFactory factory = applicationContext.getBeanFactory();
            command = (Command) factory.getBean("Command");
            beanFactory = applicationContext.getBeanFactory();
            initDao();
         }
         catch (Exception ex)
         {
            logger.fatal("global failure", ex);
            System.exit(2);
         }
         catch (Error e)
         {
            logger.fatal("global error", e);
            System.exit(2);
         }

         try
         {
            code = command.execute(args);
         }
         catch (Exception ex)
         {
            logger.fatal("global failure", ex);
            code = 2;
         }
         catch (Error e)
         {
            logger.fatal("global error", e);
            code = 2;
         }
         command = null;
         try
         {
            closeDao();
         }
         catch (Exception ex)
         {
            logger.fatal("global failure", ex);
            System.exit(2);
         }
         catch (Error e)
         {
            logger.fatal("global error", e);
            System.exit(2);
         }

         System.exit(code);
      }
      else
      {
         printHelp();
      }
   }

   /**
    * @param args
    */
   public int execute(String[] args)
   {
      List<CommandArgument> commands = null;
      try
      {
         commands = parseArgs(args);
      }
      catch (Exception e1)
      {
         if (getBoolean(globals, "help"))
         {
            printHelp();
            return 0;
         }
         else
         {
            System.err.println("invalid syntax : " + e1.getMessage());
            logger.error(e1.getMessage(), e1);
            return 1;
         }
      }
      if (getBoolean(globals, "help"))
      {
         printHelp();
         return 0;
      }

      for (String key : globals.keySet())
      {
         logger.info("global parameters " + key + " : " + Arrays.toString(globals.get(key).toArray()));
      }

      int commandNumber = 0;
      int code = 0;
      try
      {
         for (CommandArgument command : commands)
         {
            commandNumber++;
            code = executeCommand(commandNumber, command);
         }
      }
      catch (Exception e)
      {
         System.out.println("command failed : " + e.getMessage());
         logger.error(e.getMessage(), e);
         return 1;
      }
      return code;

   }

   /**
    * @param beans
    * @param commandNumber
    * @param command
    * @return
    * @throws ChouetteException
    * @throws Exception
    */
   private int executeCommand(
         int commandNumber,
         CommandArgument command) throws Exception
         {
      String name = command.getName();
      Map<String, List<String>> parameters = command.getParameters();
      logger.info("Command " + commandNumber + " : " + name);
      for (String key : parameters.keySet())
      {
         logger.info("    parameters " + key + " : " + Arrays.toString(parameters.get(key).toArray()));
      }

      if (name.equals("help"))
      {
         String cmd = getSimpleString(parameters, "cmd", "");
         if (cmd.length() > 0)
         {
            printCommandDetail(cmd);
         }
         else
         {
            printCommandSyntax(true);
         }
         return 0;
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
         return 0;
      }

      long tdeb = System.currentTimeMillis();

      if (name.equals("import"))
      {
         int code = importCommand.executeImport(getEntityManager(), parameters);
         if (code > 0)
         {
            logger.error("   command failed with code " + code);
            return code;
         }
      }
      else if (name.equals("validate"))
      {
         int code = validateCommand.executeValidate(getEntityManager(), parameters);
         if (code > 0)
         {
            logger.error("   command failed with code " + code);
            return code;
         }
      }
      else if (name.equals("export"))
      {
         // old fashioned interface
         INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
         int code = exportCommand.executeExport(manager, parameters);
         if (code > 0)
         {
            logger.error("   command failed with code " + code);
            return code;
         }
      }
      else
      {
         throw new Exception("Command " + commandNumber + ": unknown command :" + command.getName());
      }
      long tfin = System.currentTimeMillis();
      logger.info("    command " + command.getName() + " executed in " + getTimeAsString(tfin - tdeb));
      return 0;
         }

   /**
    * @param string
    * @return
    */
   private boolean getBoolean(Map<String, List<String>> parameters, String key)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         return false;
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key + " of boolean type must be unique");
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
         object = getSimpleString(parameters, "object").toLowerCase();
         List<String> objects = new ArrayList<String>();
         objects.add(object);
         globals.put("object", objects);
      }
      catch (IllegalArgumentException e)
      {
         object = getSimpleString(globals, "object").toLowerCase();
      }
      INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(object);
      if (manager == null)
      {
         throw new IllegalArgumentException("unknown object " + object + ", only "
               + Arrays.toString(managers.keySet().toArray()) + " are managed");
      }
      return manager;
   }

   /**
    * @param string
    * @return
    */
   private String getSimpleString(Map<String, List<String>> parameters, String key)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         throw new IllegalArgumentException("parameter -" + key + " of String type is required");
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key + " of String type must be unique");
      return values.get(0);
   }

   /**
    * @param string
    * @return
    */
   private String getSimpleString(Map<String, List<String>> parameters, String key, String defaultValue)
   {
      List<String> values = parameters.get(key);
      if (values == null)
         return defaultValue;
      if (values.size() > 1)
         throw new IllegalArgumentException("parameter -" + key + " of String type must be unique");
      return values.get(0);
   }

   /**
    * convert a duration in millisecond to literal
    * 
    * the returned format depends on the duration : <br>
    * if duration > 1 hour, format is HH h MM m SS s <br>
    * else if duration > 1 minute , format is MM m SS s <br>
    * else if duration > 1 second , format is SS s <br>
    * else (duration < 1 second) format is LLL ms
    * 
    * @param duration
    *           the duration to convert
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
         res += hour + " h " + min + " m " + sec + " s ";
      else if (min > 0)
         res += min + " m " + sec + " s ";
      else if (sec > 0)
         res += sec + " s ";
      res += milli + " ms";
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
               if (alias != null)
                  key = alias;
            }
            if (key.equals("command"))
            {
               if (i == args.length - 1)
               {
                  throw new Exception("missing command name");
               }
               String name = args[++i];
               if (name.startsWith("-"))
               {
                  throw new Exception("missing command name before " + name);
               }
               command = new CommandArgument(name);
               parameters = command.getParameters();
               commands.add(command);
            }
            else
            {
               if (parameters.containsKey(key))
               {
                  throw new Exception("duplicate parameter : -" + key);
               }
               List<String> list = new ArrayList<String>();

               if (i == args.length - 1 || isOption(args[i + 1]))
               {
                  list.add("true");
               }
               else
               {
                  while ((i + 1) < args.length && !isOption(args[i + 1]))
                  {
                     list.add(args[++i]);
                  }
               }
               parameters.put(key, list);
            }
         }
      }

      return commands;
   }

   private boolean isOption(String arg)
   {
      if (arg.length() < 2)
         return false;
      return arg.startsWith("-") && !Character.isDigit(arg.charAt(1));
   }

}
