/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.model;

import java.util.Calendar;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.NeptuneObject;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 *
 */
public class ImportLogMessage extends NeptuneObject
{
   /**
    * 
    */
   private static final long serialVersionUID = -7919264304117782400L;

   @Getter @Setter private long importId;
   @Getter @Setter private String key;
   @Getter @Setter private String arguments = null;
   @Getter @Setter private String severity;
   @Getter @Setter private int position;
   @Getter @Setter private Date createdAt;
   @Getter @Setter private Date updatedAt;

   public ImportLogMessage()
   {
      createdAt = Calendar.getInstance().getTime();
      updatedAt = createdAt;
   }

   public ImportLogMessage(Report report,int position)
   {
      this();
      this.key = report.getOriginKey();
      this.position = position;
      this.severity = report.getStatus().name();
   }

   public ImportLogMessage(ReportItem item, String prefix, int position)
   {
      this();
      this.key = prefix+"|"+item.getMessageKey();
      if (! item.getMessageArgs().isEmpty() )
      {
         int size = item.getMessageArgs().size();
         for (int i = 0; i < size ;i++)
         {
            StringBuilder b = new StringBuilder("{");
            b.append("\"");
            b.append(i);
            b.append("\" => \"");
            b.append(item.getMessageArgs().get(i));
            b.append("\"");
            if (i == size -1) 
            {
               b.append("}"); 
            }
            else
            {
               b.append(","); 
            }
         }
      }
      this.position = position;
      this.severity = item.getStatus().name();
   }

}
