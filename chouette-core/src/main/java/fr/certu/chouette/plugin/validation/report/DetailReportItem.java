/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
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
	
}
