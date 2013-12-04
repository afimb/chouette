/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

import java.util.List;

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
	/**
	 * 
	 */
	public DetailReportItem(String key,STATE state,ReportLocation location)
	{
		setMessageKey(key);
		setStatus(state);
		this.location = location;

	}
	   public String toJSON(String indent,boolean last)
	   {
	      StringBuilder builder = new StringBuilder();

	      builder.append(indent+"{\n");
	      builder.append(indent+"  \"messagekey\":\"");
	      builder.append(getMessageKey());
	      builder.append("\",\n"+indent+"  \"status\":\"");
	      builder.append(getStatus());
	      builder.append("\",\n"+indent+"  \"order\":");
	      builder.append(getOrder());
	      builder.append(",\n"+indent+"  \"location\":");
	      
	      builder.append(getLocation().toJSON());
	      
	      
	      builder.append("\n"+indent+"}");
	      if (!last) builder.append(",");
	      builder.append("\n");

	      return builder.toString();
	   }
	
}
