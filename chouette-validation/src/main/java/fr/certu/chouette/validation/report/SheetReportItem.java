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
	private static final int MAX_DETAIL = 4;
	
	@Getter private int order;
	
	private int detailItemCount = 0;
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
		if (item instanceof DetailReportItem)
		{
			detailItemCount ++;
			if (detailItemCount > MAX_DETAIL) return;
		}
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
	
	public void computeDetailItemCount()
	{
		if (detailItemCount > MAX_DETAIL)
		{
			DetailReportItem item = new DetailReportItem("TooMuchDetails",getStatus(),Integer.toString(detailItemCount- MAX_DETAIL));
			super.addItem(item);
		}
	}

}
