/**
 * 
 */
package fr.certu.chouette.validation.report;

import lombok.Getter;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author zbouziane
 *
 */
public class SheetReportItem extends ReportItem implements Comparable<ReportItem>
{
	@Getter private int order;
	/**
	 * 
	 */
	public SheetReportItem(String key, int order)
	{
		setMessageKey(key);
		setStatus(STATE.UNCHECK);

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

	@Override
	public int compareTo(ReportItem item) 
	{
		if (item instanceof SheetReportItem)
		{
			SheetReportItem sitem = (SheetReportItem) item;
			return order-sitem.order;
		}
		return super.compareTo(item);
		
	}
	

}
