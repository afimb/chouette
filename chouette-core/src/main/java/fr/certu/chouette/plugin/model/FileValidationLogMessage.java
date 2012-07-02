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
public class FileValidationLogMessage extends LogMessage
{
   /**
    * 
    */
   private static final long serialVersionUID = -7919264304117782400L;
   
   public FileValidationLogMessage()
   {
      super();
   }

   public FileValidationLogMessage(long validationId,String format,Report report,int position)
   {
      super(validationId,format,report,position);
   }

   public FileValidationLogMessage(long validationId,String format,ReportItem item, String prefix, int position)
   {
      super(validationId,format,item,prefix,position);
   }

}
