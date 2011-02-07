/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.report;

import fr.certu.chouette.plugin.report.Report;

/**
 * @author michel
 *
 */
public class NeptuneReport extends Report 
{
	// declare message report
    public enum KEY {IMPORT};
    

    public NeptuneReport(KEY key)
    {
    	setOriginKey(key.name());
    }
    

}
