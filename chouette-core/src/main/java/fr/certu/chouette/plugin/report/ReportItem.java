/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.report;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public abstract class ReportItem extends Report
{
	@Getter @Setter private String messageKey;
	@Getter @Setter private List<Object> messageArgs;

	public void addMessageArg(Object arg)
	{
		if (messageArgs == null) messageArgs= new ArrayList<Object>();
		messageArgs.add(arg);
	}	
	
}
