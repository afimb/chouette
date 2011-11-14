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
		setStatus(STATE.UNCHECK);

	}
	
	public DetailReportItem(String key, STATE state, Object...args){
		setMessageKey(key);
		setStatus(state);
		if(!args.equals(""))
			addMessageArgs(args);	
	}
	
	public DetailReportItem(STATE state){
		setStatus(state);
	}
}
