/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.gui.command;

import java.util.Arrays;

import fr.certu.chouette.plugin.report.ReportItem;

/**
 *
 */
public class GuiReportItem extends ReportItem
{
   public enum KEY
   {
      SAVE_OK, NO_SAVE, SAVE_ERROR, EXCEPTION
   };

   /**
    * 
    */
   public GuiReportItem()
   {

   }

   public GuiReportItem(KEY key, STATE status, Object... args)
   {
      setMessageKey(key.toString());
      updateStatus(status);
      if (args != null)
         setMessageArgs(Arrays.asList(args));
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin
    * .report.ReportItem)
    */
   @Override
   public void addItem(ReportItem item)
   {
      if (getItems() == null)
      {
         super.addItem(item);
      } else
      {
         getItems().add(item);
      }
   }

}
