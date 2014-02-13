/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.exchange.report;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class LimitedExchangeReportItem extends ReportItem 
{

	public enum KEY {
		NETWORK_ANALYSE,
		COMPANY_ANALYSE,
		GROUP_OF_LINES_ANALYSE,
		LINE_ANALYSE,
		ROUTE_ANALYSE,
		STOP_ANALYSE,
		VEHICLE_JOURNEY_ANALYSE,
		TIME_TABLE_ANALYSE,
		CONNECTION_LINK_ANALYSE,
		COUNT_MORE_ITEMS
	} ;

	@Setter private int maxCount = 5; 

	private int count;

	public LimitedExchangeReportItem(ExchangeReportItem.KEY key,Report.STATE status, Object... args)
	{
		updateStatus(status);
		setMessageKey(key.name());
		addMessageArgs(args);
	}

	public LimitedExchangeReportItem(LimitedExchangeReportItem.KEY key,Report.STATE status, Object... args)
	{
		updateStatus(status);
		setMessageKey(key.name());
		addMessageArgs(args);
	}

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
		if (count < maxCount)
			getItems().add(item);
		count++;
	}

	@Override
	public List<ReportItem> getItems() 
	{
		if (count > maxCount)
		{
			List<ReportItem> items = new ArrayList<ReportItem>();
			items.addAll(super.getItems());
			ReportItem item = new LimitedExchangeReportItem(KEY.COUNT_MORE_ITEMS, STATE.OK, Integer.valueOf(count));
			items.add(item);
			return items;
		}			
		return super.getItems();
	}



}
