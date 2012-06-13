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
public class ExportLogMessage extends LogMessage
{

   /**
    * 
    */
   private static final long serialVersionUID = -4945073186691401029L;

   public ExportLogMessage()
   {
      super();
   }
   public ExportLogMessage(long exportId,String format,Report report,int position)
   {
      super(exportId,format,report,position);
   }

   public ExportLogMessage(long exportId,String format,ReportItem item, String prefix, int position)
   {
      super(exportId,format,item,prefix,position);
   }

}
