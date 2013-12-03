/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.ArrayList;

import lombok.Getter;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author Michel Etienne
 *
 */
public class CheckPointReportItem extends ReportItem 
{
	private static final int MAX_DETAIL = 50;

	@Getter private int detailItemCount = 0;
	/**
	 * 
	 */
	public CheckPointReportItem(String key, int order)
	{
		this.setOrder(order);
		setMessageKey(key);
		setStatus(STATE.UNCHECK);

	}
	/**
	 * 
	 */
	public CheckPointReportItem(String key, int order, STATE status)
	{
		this.setOrder(order);
		setMessageKey(key);
		setStatus(status);

	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin.report.ReportItem)
	 */
	@Override
	public void addItem(ReportItem item) 
	{
		if (item instanceof DetailReportItem)
		{
			int status = getStatus().ordinal();
			int itemStatus = item.getStatus().ordinal();
			if (itemStatus > status) setStatus(item.getStatus());
			detailItemCount ++;
			if (detailItemCount > MAX_DETAIL) return;
			if (getItems() == null) setItems(new ArrayList<ReportItem>());
			// do not merge items by key
			getItems().add(item);
		}
		else
		{
			throw new IllegalArgumentException("item must be of DetailReportItem type");
		}
	}

	public DetailReportItem getItem(String key)
	{
		for (ReportItem item : getItems()) 
		{
			if (item.getMessageKey().equals(key))
			{
				return (DetailReportItem) item;
			}
		}
		return null;
	}
}
