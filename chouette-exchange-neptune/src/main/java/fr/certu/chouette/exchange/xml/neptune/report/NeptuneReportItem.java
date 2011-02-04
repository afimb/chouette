/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.report;

import fr.certu.chouette.plugin.report.ReportItem;


/**
 * @author michel
 *
 */
public class NeptuneReportItem extends ReportItem 
{
	public enum KEY {OK_LINE,FILE_ERROR,VALIDATION_ERROR,VALIDATION_CAUSE} ;

	public NeptuneReportItem(KEY key,String... args)
	{
       setMessageKey(key.name());
       addMessageArgs(args);
	}
}
