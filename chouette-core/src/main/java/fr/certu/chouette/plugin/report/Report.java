/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * report for Import/export/validation trace
 * <p>
 * 
 * 
 * 
 * may be used in every case where reporting is required
 * 
 */

public abstract class Report
{
   /**
    * available values for report status
    */
   public enum STATE
   {
      UNCHECK, OK, WARNING, ERROR, FATAL
   };

   /**
    * report originKey
    */
   @Getter
   @Setter
   private String           originKey;
   /**
    * report status
    */
   @Getter
   @Setter
   private STATE            status;
   /**
    * reportItem list
    */
   @Getter
   @Setter
   private List<ReportItem> items;

   /**
    * add or merge item in list
    * <p>
    * if item's messageKey is already present, subItems lists will be merged
    * 
    * @param item
    *           to add/merge
    */
   public void addItem(ReportItem item)
   {
      if (items == null)
         items = new ArrayList<ReportItem>();
      String messageKey = item.getMessageKey();
      for (ReportItem it : items)
      {
         if (it.getMessageKey() != null && it.getMessageKey().equals(messageKey))
         {
            if (item.getItems() != null)
            {
               for (ReportItem sub : item.getItems())
               {
                  it.addItem(sub);
               }
            }
            return;
         }
      }
      items.add(item);
   }

   /**
    * add a list of ReportItems to list
    * 
    * @param itemsToAdd
    *           list of items
    */
   public void addAll(List<ReportItem> itemsToAdd)
   {
      if (itemsToAdd == null)
         return;
      if (items == null)
         items = new ArrayList<ReportItem>();
      for (ReportItem it : itemsToAdd)
      {
         addItem(it);
      }
   }

   /**
    * update status only if worst than previous one
    * 
    * @param statusToApply
    *           new status
    */
   public void updateStatus(STATE statusToApply)
   {
      if (status == null || status.ordinal() < statusToApply.ordinal())
      {
         status = statusToApply;
      }

   }

   /**
    * get report message for default Locale
    * 
    * @return report message
    */
   public final String getLocalizedMessage()
   {
      return getLocalizedMessage(Locale.getDefault());
   }

   /**
    * get report message for a specified Locale
    * <p>
    * if no message available for locale, default locale is assumed
    * 
    * @param locale
    *           asked locale
    * @return report message
    */
   public String getLocalizedMessage(Locale locale)
   {
      String message = "";
      try
      {
         ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(), locale);
         message = bundle.getString(getOriginKey());
      }
      catch (MissingResourceException e1)
      {
         try
         {
            ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
            message = bundle.getString(getOriginKey());
         }
         catch (MissingResourceException e2)
         {
            message = getOriginKey();
         }
      }

      return message;
   }

   /**
    * pretty print a report in a stream
    * 
    * @param stream
    *           target stream
    * @param report
    *           report to print
    * @param closeOnExit
    *           close stream after print
    */
   public static void print(PrintStream stream, Report report, boolean closeOnExit)
   {
      stream.println(report.getLocalizedMessage());
      printItems(stream, "", report.getItems());
      if (closeOnExit)
         stream.close();

   }

   /**
    * pretty print recursively reportItems
    * 
    * @param stream
    *           target stream
    * @param indent
    *           indentation
    * @param items
    *           items to print
    */
   private static void printItems(PrintStream stream, String indent, List<ReportItem> items)
   {
      if (items == null)
         return;
      for (ReportItem item : items)
      {
         stream.println(indent + item.getStatus().name() + " : " + item.getLocalizedMessage());
         printItems(stream, indent + "   ", item.getItems());
      }

   }

}
