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
	public enum KEY {OK_LINE,OK_TIMETABLE,OK_PTNETWORK,OK_COMPANY,FILE_ERROR,
	   VALIDATION_ERROR,VALIDATION_CAUSE,FILE_IGNORED, PARSE_OBJECT, 
	   MANDATORY_TAG, UNKNOWN_ENUM, EMPTY_TAG, TIMETABLE_COUNT,LINE_COUNT, END_OF_FILE} ;

	public CSVReportItem(KEY key,Report.STATE status, String... args)
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
