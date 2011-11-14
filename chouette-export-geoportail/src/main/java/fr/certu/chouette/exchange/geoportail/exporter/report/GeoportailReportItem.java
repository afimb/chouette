/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.geoportail.exporter.report;

import java.util.ArrayList;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class GeoportailReportItem extends ReportItem 
{
	public enum KEY {OK,NO_NETWORK,TOO_MANY_NETWORKS,MISSING_PARAMETER,
	   UNKNOWN_PARAMETER,INVALID_EXTENSION,FILE_ACCESS,NO_STOPAREAS,NO_ACCESSPOINTS,MISSING_RESSOURCE} ;

	public GeoportailReportItem(KEY key,Report.STATE status, Object... args)
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
