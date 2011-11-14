/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.report;

import java.util.ArrayList;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class GtfsReportItem extends ReportItem 
{
	public enum KEY {OK,NO_LINE,UNKNOWN_PARAMETER,MISSING_PARAMETER,FILE_ACCESS,EMPTY_TIMETABLE,MISSING_DATA,EMPTY_DATA, INVALID_DATA} ;

	public GtfsReportItem(KEY key,Report.STATE status, Object... args)
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
