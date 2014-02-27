package fr.certu.chouette.plugin.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@Entity
@Table(name = "export_log_messages")
public class ExportLogMessage extends LogMessage
{

   private static final long serialVersionUID = -4945073186691401029L;

   public ExportLogMessage(Long exportId, String format, Report report, int position)
   {
      super(exportId, format, report, position);
   }

   public ExportLogMessage(Long exportId, String format, ReportItem item, String prefix, int position)
   {
      super(exportId, format, item, prefix, position);
   }

}
