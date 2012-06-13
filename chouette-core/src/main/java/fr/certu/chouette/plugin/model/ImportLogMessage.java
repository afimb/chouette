/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.model;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 *
 */
public class ImportLogMessage extends LogMessage
{
   /**
    * 
    */
   private static final long serialVersionUID = -7919264304117782400L;
   
   public ImportLogMessage()
   {
      super();
   }

   public ImportLogMessage(long importId,String format,Report report,int position)
   {
      super(importId,format,report,position);
   }

   public ImportLogMessage(long importId,String format,ReportItem item, String prefix, int position)
   {
      super(importId,format,item,prefix,position);
   }

}
