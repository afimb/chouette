/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

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
		setStatus(state);
		this.location = location;
		this.objectId = objectId;
		this.args = args;

	}
	
	public DetailReportItem(String key, String objectId, STATE state,ReportLocation location)
	{
		
		setMessageKey("detail_"+key.replaceAll("-","_").toLowerCase());
		setStatus(state);
		this.location = location;
		this.objectId = objectId;
		this.args = null;

	}

	public DetailReportItem(String key,STATE state,ReportLocation location, Map<String,Object> args)
	{
		setMessageKey("detail_"+key.replaceAll("-","_").toLowerCase());
		setStatus(state);
		this.location = location;
		this.objectId = null;
		this.args = args;

	}
	   public JSONObject toJSON()
	   {
		   
		  JSONObject json = new JSONObject();
		  
		  if (objectId != null)
			  json.put("objectId", objectId);
		  json.put("location", location.toJSON());
		  json.put("messageKey", getMessageKey());
		  if (args != null)
		      json.put("messageArgs", args);
			
// 	      StringBuilder builder = new StringBuilder();

//	      builder.append(indent+"{\n");
//	      builder.append(indent+"  \"objectRef\":\"");
//	      builder.append(getMessageKey()+"\"");
////	      builder.append("\",\n"+indent+"  \"status\":\"");
////	      builder.append(getStatus());
////	      builder.append("\",\n"+indent+"  \"order\":");
////	      builder.append(getOrder());
//	      builder.append(",\n"+indent+"  \"location\":");
//	      
//	      builder.append(getLocation().toJSON());
//	      
//	      
//	      builder.append("\n"+indent+"}");
//	      if (!last) builder.append(",");
//	      builder.append("\n");

	      return json;
	   }
	
}
