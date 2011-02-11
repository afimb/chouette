/**
 * 
 */
package fr.certu.chouette.validation.report;

import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author zbouziane
 *
 */
public class SheetReportItem extends ReportItem 
{
	
	/**
	 * 
	 */
	public SheetReportItem(String key)
	{
		setMessageKey(key);
		setStatus(STATE.OK);

	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin.report.ReportItem)
	 */
	@Override
	public void addItem(ReportItem item) 
	{
		super.addItem(item);
		int status = getStatus().ordinal();
		int itemStatus = item.getStatus().ordinal();
		if (itemStatus > status) setStatus(item.getStatus());
	}
	
	

}
