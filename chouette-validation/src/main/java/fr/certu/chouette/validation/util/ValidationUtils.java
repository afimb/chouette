package fr.certu.chouette.validation.util;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.validation.report.DetailReportItem;

/**
 * 
 * @author mamadou keira
 *
 */
public class ValidationUtils {
	public static ReportItem addDetail(String key, Report.STATE state, String...args){
		ReportItem detailReportItem = new DetailReportItem(key);
		detailReportItem.setStatus(state);
		if(!args.equals(""))
			detailReportItem.addMessageArgs(args);
		return detailReportItem;		
	}	
}
