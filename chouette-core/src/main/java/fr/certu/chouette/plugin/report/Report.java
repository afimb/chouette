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

public abstract class Report 
{
	public enum STATE {OK, WARNING, ERROR} ;
	@Getter @Setter private String originKey;
	@Getter @Setter private STATE status;
	@Getter @Setter private List<ReportItem> items;

	public void addItem(ReportItem item)
	{
		if (items == null) items= new ArrayList<ReportItem>();
		items.add(item);
	}	


}
