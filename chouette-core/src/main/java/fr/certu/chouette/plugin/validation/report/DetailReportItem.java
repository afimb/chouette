/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

import org.json.JSONObject;

import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author zbouziane
 *
 */
public class DetailReportItem extends ReportItem 
{
	private @Getter @Setter ReportLocation location;
	private @Getter @Setter String objectId;
	private @Getter @Setter Map<String,Object> args; 
	/**
	 * 
	 */
	public DetailReportItem(String key, String objectId, STATE state,ReportLocation location, Map<String,Object> args)
	{

		setMessageKey("detail_"+key.replaceAll("-","_").toLowerCase());
		updateStatus(state);
		this.location = location;
		this.objectId = objectId;
		this.args = args;

	}

	public DetailReportItem(String key, String objectId, STATE state,ReportLocation location)
	{

		setMessageKey("detail_"+key.replaceAll("-","_").toLowerCase());
		updateStatus(state);
		this.location = location;
		this.objectId = objectId;
		this.args = null;

	}

	public DetailReportItem(String key,STATE state,ReportLocation location, Map<String,Object> args)
	{
		setMessageKey("detail_"+key.replaceAll("-","_").toLowerCase());
		updateStatus(state);
		this.location = location;
		this.objectId = null;
		this.args = args;

	}
	public JSONObject toJSON()
	{

		JSONObject json = new JSONObject();

		if (args == null) args = new HashMap<String, Object>();
		if (objectId != null)
		{
			json.put("objectId", objectId);
			if (!args.containsKey("objectId"))
			{
				args.put("objectId", objectId);
			}
		}
		json.put("location", location.toJSON());
		json.put("messageKey", getMessageKey());
		if (!args.isEmpty())
			json.put("messageArgs", args);


		return json;
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
			message = populate(format);
		}
		catch (MissingResourceException e1)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
				format = bundle.getString(getMessageKey());
				message = populate(format);
			}
			catch (MissingResourceException e2)
			{
				message = getMessageKey(); 
				if (args != null) message += " : "+Arrays.toString(args.values().toArray());
			}
		}
		if (location != null)
		{
			message += "\n                    "+location.toString();
		}

		return message;
	}

	private String populate(String template)
	{
		String message = template;
		for (String key : args.keySet()) 
		{
			String value = args.get(key).toString();

			message = expand(message, key, value);
		}
		message = expand(message, "objectId", getObjectId());
		message = message.replaceAll("''", "'");
		return message;
	}

	/**
	 * @param message
	 * @param key
	 * @return
	 */
	private String expand(String message, String key, String value) 
	{
		String mKey = "%\\{"+key+"\\}";
		message = message.replaceAll(mKey, value);
		return message;
	}

}
