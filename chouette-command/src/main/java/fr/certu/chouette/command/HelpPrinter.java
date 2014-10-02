/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.command;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 */
/**
 * @author mamadou
 * 
 */
public abstract class HelpPrinter
{

   /**
    * @param locale
    * 
    */
   public static void printHelp(Locale locale)
   {
      ResourceBundle bundle = null;
      try
      {
         bundle = ResourceBundle.getBundle(HelpPrinter.class.getName(), locale);
      } catch (MissingResourceException e1)
      {
         try
         {
            bundle = ResourceBundle.getBundle(HelpPrinter.class.getName());
         } catch (MissingResourceException e2)
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

   private static String getHelpString(ResourceBundle bundle, String key)
   {
      try
      {
         return bundle.getString(key);
      } catch (Exception e)
      {
         return null;
      }
   }

   private static void printBloc(ResourceBundle bundle, String key,
         String indent)
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
      } while (line != null);
   }

   private static void printCommandDetail(ResourceBundle bundle, String key,
         String indent)
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
   public static void printCommandSyntax(Locale locale, boolean interactive)
   {
      ResourceBundle bundle = null;
      try
      {
         bundle = ResourceBundle.getBundle(HelpPrinter.class.getName(), locale);
      } catch (MissingResourceException e1)
      {
         try
         {
            bundle = ResourceBundle.getBundle(HelpPrinter.class.getName());
         } catch (MissingResourceException e2)
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
      } else
      {
         for (String command : commands)
         {
            printCommandDetail(bundle, command, "   ");
            System.out.println("");
         }
      }

   }

   /**
	 * 
	 */
   public static void printCommandDetail(Locale locale, String command)
   {
      ResourceBundle bundle = null;
      try
      {
         bundle = ResourceBundle.getBundle(HelpPrinter.class.getName(), locale);
      } catch (MissingResourceException e1)
      {
         try
         {
            bundle = ResourceBundle.getBundle(HelpPrinter.class.getName());
         } catch (MissingResourceException e2)
         {
            System.out.println("missing help resource");
            return;
         }
      }
      String lowerCommand = command.toLowerCase();
      printCommandDetail(bundle, lowerCommand, "   ");

   }

}
