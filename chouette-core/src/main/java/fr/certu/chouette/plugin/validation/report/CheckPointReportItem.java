/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.plugin.model.GuiValidationStep;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author Michel Etienne
 *
 */
public class CheckPointReportItem extends ReportItem 
{
	public enum SEVERITY  { WARNING ,ERROR ,IMPROVMENT};
	private static final int MAX_DETAIL = 50;

	
	@Getter private long detailItemCount = 0;
	@Getter private SEVERITY severity = SEVERITY.ERROR;
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
	public CheckPointReportItem(String key, int order, STATE status, SEVERITY severity)
	{
		this.setOrder(order);
		setMessageKey(key);
		setStatus(status);
		this.severity = severity;

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
	
	public GuiValidationStep toValidationResult()
	{
		GuiValidationStep step = new GuiValidationStep();
		step.setCreatedAt(Calendar.getInstance().getTime());
		step.setRuleCode(getMessageKey());
		step.setSeverity(severity.toString().toLowerCase());
		step.setViolationCount(Long.valueOf(detailItemCount));
		switch (getStatus()) 
		{
		case UNCHECK:
			step.setStatus("na");
			break;
		case WARNING:
		case ERROR:
		case FATAL:
			step.setStatus("nok");
			break;
		case OK:
			step.setStatus("ok");
			break;
		}
		if (getItems() != null)
		{
			JSONArray array = new JSONArray();
			for (ReportItem item : getItems()) 
			{
				array.put(item.toJSONObject());
			}
			if (array.length() > 0)
			{
				JSONObject detail = new JSONObject();
				detail.put("detail",array);
				step.setDetail(detail);
			}
		}
		
		
		
		return step;
	}
}
