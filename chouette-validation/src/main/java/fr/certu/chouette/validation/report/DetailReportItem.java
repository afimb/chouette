/**
 * 
 */
package fr.certu.chouette.validation.report;

import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author zbouziane
 *
 */
public class DetailReportItem extends ReportItem 
{
	
	/**
	 * 
	 */
	public DetailReportItem(String key)
	{
		setMessageKey(key);
		setStatus(STATE.OK);

	}
	

}
