/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.importer.report;

import java.util.ArrayList;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class CSVReportItem extends ReportItem 
{
	public enum KEY {OK_LINE,OK_TIMETABLE,OK_PTNETWORK,OK_COMPANY,
	   MANDATORY_TAG,TIMETABLE_COUNT,LINE_COUNT,END_OF_FILE,BAD_TIMETABLE_PERIODS,INVALID_LINE, STOP_WITHOUT_COORDS, VJ_MISSING_TIMETABLE, FILE_FORMAT, BAD_ID} ;

	public CSVReportItem(KEY key,Report.STATE status, Object... args)
	{
		setStatus(status);
        setMessageKey(key.name());
        addMessageArgs(args);
	}
	
	@Override
   /**
    * add but don't merge item in list
    * 
    * @param item
    *           to add/merge
    */
   public void addItem(ReportItem item)
   {
      if (getItems() == null)
         setItems(new ArrayList<ReportItem>());
      updateStatus(item.getStatus());
      getItems().add(item);
   }
	
	
}
