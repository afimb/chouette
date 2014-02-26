/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.certu.chouette.plugin.model.CompilanceCheckResult;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author Michel Etienne
 *
 */
public class CheckPointReportItem extends ReportItem 
{
	public enum SEVERITY  { WARNING ,ERROR ,IMPROVMENT};
	private static final int MAX_DETAIL = 50;

	
	@Getter private int detailItemCount = 0;
	@Getter private SEVERITY severity = SEVERITY.ERROR;
	/**
	 * 
	 */
	public CheckPointReportItem(String key, int order)
	{
		this.setOrder(order);
		setMessageKey(key);
		updateStatus(STATE.UNCHECK);

	}
	/**
	 * 
	 */
	public CheckPointReportItem(String key, int order, STATE status, SEVERITY severity)
	{
		this.setOrder(order);
		setMessageKey(key);
		updateStatus(status);
		this.severity = severity;

	}
	/**
	 * @param locale
	 * @return
	 */
	@Override
	public  String getLocalizedMessage(Locale locale)
	{
		String format = "";
		String message = "";
		try
		{
			ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(),locale);
			format = bundle.getString(getMessageKey());
			message = getMessageKey() + " : "+ format;
		}
		catch (MissingResourceException e1)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
				format = bundle.getString(getMessageKey());
				message = getMessageKey() + " : "+ format;
			}
			catch (MissingResourceException e2)
			{
				message = getMessageKey(); 
			}
		}
		message = message.replaceAll("''", "'");

		return message;
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.plugin.report.Report#addItem(fr.certu.chouette.plugin.report.ReportItem)
	 */
	@Override
	public void addItem(ReportItem item) 
	{
		if (item instanceof DetailReportItem)
		{
			updateStatus(item.getStatus());
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
	
	public CompilanceCheckResult toValidationResult()
	{
		CompilanceCheckResult step = new CompilanceCheckResult();
		step.setCreatedAt(Calendar.getInstance().getTime());
		step.setRuleCode(getMessageKey());
		step.setSeverity(severity.toString().toLowerCase());
		step.setViolationCount(Integer.valueOf(detailItemCount));
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
				array.put(item.toJSON());
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
