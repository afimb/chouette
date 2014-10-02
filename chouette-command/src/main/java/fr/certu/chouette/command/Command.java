/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.service.geographic.IGeographicService;

/**
 *
 */
/**
 * @author mamadou
 * 
 */
@Log4j
@NoArgsConstructor
public class Command extends AbstractCommand
{
   public static ClassPathXmlApplicationContext applicationContext;

   @Setter
   @Getter
   private EditObjectCommand editObjectCommand;

   @Setter
   @Getter
   private DaoCommand daoCommand;

   @Setter
   @Getter
   private ExchangeCommand exchangeCommand;

   @Setter
   private IGeographicService geographicService;

   @Getter
   @Setter
   private static BeanFactory beanFactory;

   public static Map<String, List<String>> globals = new HashMap<String, List<String>>();;

   public static Map<String, String> shortCuts;

   public static boolean dao = true;

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
      String[] context = { "classpath*:/chouetteContext.xml" };

      if (args.length >= 1)
      {
         if (args[0].equalsIgnoreCase("-help")
               || args[0].equalsIgnoreCase("-h"))
         {
            HelpPrinter.printHelp(locale);
            System.exit(0);
         }

         if (args[0].equalsIgnoreCase("-noDao"))
         {
            List<String> newContext = new ArrayList<String>();
            PathMatchingResourcePatternResolver test = new PathMatchingResourcePatternResolver();
            try
            {
               Resource[] re = test
                     .getResources("classpath*:/chouetteContext.xml");
               for (Resource resource : re)
               {
                  if (!resource.getURL().toString().contains("dao"))
                  {
                     newContext.add(resource.getURL().toString());
                  }
               }
               context = newContext.toArray(new String[0]);
               dao = false;
            } catch (Exception e)
            {

               System.err.println("cannot remove dao : "
                     + e.getLocalizedMessage());
            }
         }
         applicationContext = new ClassPathXmlApplicationContext(context);
         beanFactory = applicationContext.getBeanFactory();
         Command command = (Command) beanFactory.getBean("Command");

         initDao();

         command.execute(args);

         closeDao();

         System.runFinalization();

      } else
      {
         HelpPrinter.printHelp(locale);
      }
   }

   /**
    * @param factory
    */
   public static void closeDao()
   {
      if (dao)
      {
         EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
         EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager
               .unbindResource(entityManagerFactory);
         EntityManager em = holder.getEntityManager();
         EntityTransaction tx = em.getTransaction();
         try
         {
            tx.begin();
            em.flush();
            tx.commit();
         } catch (PersistenceException e)
         {
            tx.rollback();
         }
         EntityManagerFactoryUtils.closeEntityManager(em);
      }
   }

   /**
    * @param factory
    */
   public static void initDao()
   {
      if (dao)
      {
         EntityManagerFactory entityManagerFactory = getEntityManagerFactory();
         EntityManager entityManager = entityManagerFactory
               .createEntityManager();
         TransactionSynchronizationManager.bindResource(entityManagerFactory,
               new EntityManagerHolder(entityManager));
      }
   }

   public static EntityManagerFactory getEntityManagerFactory()
   {
      EntityManagerFactory entityManagerFactory = (EntityManagerFactory) beanFactory
            .getBean("entityManagerFactory");
      return entityManagerFactory;
   }

   public static EntityManager getEntityManager()
   {
      return EntityManagerFactoryUtils
            .getTransactionalEntityManager(getEntityManagerFactory());
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
         commands = CommandParser.parseArgs(args);
      } catch (Exception e1)
      {
         if (getBoolean(globals, "help"))
         {
            HelpPrinter.printHelp(locale);
            return;
         } else
         {
            System.err.println("invalid syntax : " + e1.getMessage());
            log.error(e1.getMessage(), e1);
            return;
         }
      }
      if (getBoolean(globals, "help"))
      {
         HelpPrinter.printHelp(locale);
         return;
      }

      if (getBoolean(globals, "verbose"))
      {
         verbose = true;
         for (String key : globals.keySet())
         {
            System.out.println("global parameters " + key + " : "
                  + Arrays.toString(globals.get(key).toArray()));
         }
      }
      for (String key : globals.keySet())
      {
         log.info("global parameters " + key + " : "
               + Arrays.toString(globals.get(key).toArray()));
      }

      List<NeptuneIdentifiedObject> beans = new ArrayList<NeptuneIdentifiedObject>();
      int commandNumber = 0;
      if (getBoolean(globals, "interactive"))
      {
         String line = "";
         verbose = true;
         BufferedReader in = new BufferedReader(
               new InputStreamReader(System.in));
         String activeObject = getActiveObject(globals);
         while (true)
         {
            try
            {
               System.out.print(activeObject + " (" + beans.size() + ") >");
               line = in.readLine();
               if (line == null)
                  return;
               line = line.trim();
            } catch (Exception e)
            {
               System.err.println("cannot read input");
               log.error("cannot read stdin", e);
               return;
            }
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")
                  || line.equalsIgnoreCase("q"))
               break;
            if (!line.startsWith("#"))
            {
               try
               {
                  CommandArgument command = CommandParser.parseLine(
                        ++commandNumber, line);
                  if (command.getName().equalsIgnoreCase("exec"))
                  {
                     String file = getSimpleString(command.getParameters(),
                           "file");
                     List<CommandArgument> cmds = CommandParser.parseFile(file);
                     int cmdNum = 1;
                     for (CommandArgument cmd : cmds)
                     {
                        commandNumber++;
                        beans = executeCommand(beans, cmdNum++, cmd);
                     }

                  } else
                  {
                     beans = executeCommand(beans, commandNumber, command);
                  }
                  activeObject = getActiveObject(command.getParameters());
               } catch (Exception e)
               {
                  log.error(e.getMessage(), e);
                  System.out.println(e.getMessage());
               }
            }

         }

      } else
      {
         try
         {
            for (CommandArgument command : commands)
            {
               commandNumber++;
               beans = executeCommand(beans, commandNumber, command);
            }
         } catch (Exception e)
         {
            if (getBoolean(globals, "help"))
            {
               HelpPrinter.printHelp(locale);
            } else
            {
               System.err.println("command failed : " + e.getMessage());
               log.error(e.getMessage(), e);
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
         CommandArgument command) throws ChouetteException, Exception
   {
      String name = command.getName();
      Map<String, List<String>> parameters = command.getParameters();
      if (verbose)
      {
         traceCommand(commandNumber, name, parameters);
      }
      log.info("Command " + commandNumber + " : " + name);
      for (String key : parameters.keySet())
      {
         log.info("    parameters " + key + " : "
               + Arrays.toString(parameters.get(key).toArray()));
      }

      if (name.equals("verbose"))
      {
         verbose = !(getBoolean(parameters, "off"));
         return beans;
      }
      if (name.equals("help"))
      {
         String cmd = getSimpleString(parameters, "cmd", "");
         if (cmd.length() > 0)
         {
            HelpPrinter.printCommandDetail(locale, cmd);
         } else
         {
            HelpPrinter.printCommandSyntax(locale, true);
         }
         return beans;
      }
      if (name.equals("lang"))
      {
         if (getBoolean(parameters, "en"))
         {
            locale = Locale.ENGLISH;
         } else if (getBoolean(parameters, "fr"))
         {
            locale = Locale.FRENCH;
         } else
         {
            System.out.println(locale);
         }
         return beans;
      }

      if (name.equals("propagateBarycentre"))
      {
         executePropagateBarycentre();
         return beans;
      }

      INeptuneManager<NeptuneIdentifiedObject> manager = getManager(parameters);
      long tdeb = System.currentTimeMillis();

      if (name.equals("get"))
      {
         beans = daoCommand.executeGet(manager, parameters);
      } else if (name.equals("new"))
      {
         beans = editObjectCommand.executeNew(manager, parameters);
      } else if (name.equals("set"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : setAttribute must follow a reading command");
         editObjectCommand.executeSet(beans, parameters);
      } else if (name.equals("add"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : setAttribute must follow a reading command");
         editObjectCommand.executeAdd(beans, parameters);
      } else if (name.equals("remove"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : setAttribute must follow a reading command");
         editObjectCommand.executeRemove(beans, parameters);
      } else if (name.equals("save"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : save must follow a reading command");
         daoCommand.executeSave(beans, manager, parameters);
      } else if (name.equals("delete"))
      {
         if (beans == null || beans.isEmpty())
         {
            System.out.println("Command " + commandNumber
                  + ": nothing to delete");
         } else
         {
            daoCommand.executeDelete(beans, manager, parameters);
         }
      } else if (name.equals("complete"))
      {
         if (beans == null || beans.isEmpty())
         {
            System.out.println("Command " + commandNumber
                  + ": nothing to complete");
         } else
         {
            executeComplete(beans, manager, parameters);
         }
      } else if (name.equals("getImportFormats"))
      {
         exchangeCommand.executeGetImportFormats(manager, parameters);
      } else if (name.equals("import"))
      {
         beans = exchangeCommand.executeImport(manager, parameters);
      }

      else if (name.equals("print"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : print must follow a reading command");
         executePrint(beans, parameters);
      } else if (name.equals("validate"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : validate must follow a reading command");
         exchangeCommand.executeValidate(beans, manager, parameters);
      } else if (name.equals("getExportFormats"))
      {
         exchangeCommand.executeGetExportFormats(manager, parameters);
      } else if (name.equals("export"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : export must follow a reading command");
         exchangeCommand.executeExport(beans, manager, parameters);
      } else if (name.equals("getDeletionExportFormats"))
      {
         exchangeCommand.executeGetDeletionFormats(manager, parameters);
      } else if (name.equals("exportForDeletion"))
      {
         if (beans == null || beans.isEmpty())
            throw new Exception(
                  "Command "
                        + commandNumber
                        + ": Invalid command sequence : export must follow a reading command");
         exchangeCommand.executeExportDeletion(beans, manager, parameters);
      } else if (name.equals("info"))
      {
         editObjectCommand.executeInfo(manager);
      } else
      {
         throw new Exception("Command " + commandNumber + ": unknown command :"
               + command.getName());
      }
      long tfin = System.currentTimeMillis();
      if (verbose)
      {
         System.out.println("command " + command.getName() + " executed in "
               + getTimeAsString(tfin - tdeb));
      }
      return beans;
   }

   /**
    * @param commandNumber
    * @param name
    * @param parameters
    */
   public void traceCommand(int commandNumber, String name,
         Map<String, List<String>> parameters)
   {
      System.out.println("Command " + commandNumber + " : " + name);
      for (String key : parameters.keySet())
      {
         System.out.println("    parameters " + key + " : "
               + Arrays.toString(parameters.get(key).toArray()));
      }
   }

   private void executePropagateBarycentre()
   {
      geographicService.propagateBarycentre();
   }

   /**
    * @param parameters
    * @return
    */
   public INeptuneManager<NeptuneIdentifiedObject> getManager(
         Map<String, List<String>> parameters)
   {
      String object = null;
      try
      {
         object = getSimpleString(parameters, "object").toLowerCase();
         List<String> objects = new ArrayList<String>();
         objects.add(object);
         globals.put("object", objects);
      } catch (IllegalArgumentException e)
      {
         object = getSimpleString(globals, "object").toLowerCase();
      }
      INeptuneManager<NeptuneIdentifiedObject> manager = managers.get(object);
      if (manager == null)
      {
         throw new IllegalArgumentException("unknown object " + object
               + ", only " + Arrays.toString(managers.keySet().toArray())
               + " are managed");
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
         object = getSimpleString(parameters, "object").toLowerCase();
      } catch (IllegalArgumentException e)
      {
         object = getSimpleString(globals, "object", "xxx").toLowerCase();
      }
      if (!managers.containsKey(object))
      {
         return "unknown object";
      }
      return object;
   }

   /**
    * @param beans
    * @param parameters
    */
   private void executePrint(List<NeptuneIdentifiedObject> beans,
         Map<String, List<String>> parameters)
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
   private void executeComplete(List<NeptuneIdentifiedObject> beans,
         INeptuneManager<NeptuneIdentifiedObject> manager,
         Map<String, List<String>> parameters) throws ChouetteException
   {
      for (NeptuneIdentifiedObject bean : beans)
      {
         manager.completeObject(null, bean);
      }

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

}
