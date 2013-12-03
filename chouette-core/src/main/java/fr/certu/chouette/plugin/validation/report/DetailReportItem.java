/**
 * 
 */
package fr.certu.chouette.plugin.validation.report;

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
	
}
