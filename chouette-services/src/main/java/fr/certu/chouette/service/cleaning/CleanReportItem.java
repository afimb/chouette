/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.service.cleaning;

import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public class CleanReportItem extends ReportItem 
{
	
	public CleanReportItem(String key, String...args){
		setMessageKey(key);
		setStatus(STATE.OK);
		if(!args.equals(""))
			addMessageArgs(args);	
	}
	
	public CleanReportItem(String key, STATE state, String...args){
		setMessageKey(key);
		setStatus(state);
		if(!args.equals(""))
			addMessageArgs(args);	
	}

}
