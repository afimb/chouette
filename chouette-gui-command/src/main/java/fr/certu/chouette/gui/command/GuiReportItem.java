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

   /**
    * 
    */
   public GuiReportItem()
   {

   }

   public GuiReportItem(String key, STATE status, Object... args)
   {
      setMessageKey(key);
      setStatus(status);
      if (args != null) setMessageArgs(Arrays.asList(args));
   }
   
   /* (non-Javadoc)
    * @see fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin.report.ReportItem)
    */
   @Override
   public void addItem(ReportItem item)
   {
      if (getItems() == null) 
      {
         super.addItem(item);
      }
      else
      {
         getItems().add(item);
      }
   }

}
