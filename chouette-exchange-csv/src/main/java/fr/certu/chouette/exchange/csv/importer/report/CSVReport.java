/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.importer.report;

import fr.certu.chouette.plugin.report.Report;

/**
 * @author michel
 *
 */
public class CSVReport extends Report 
{
	// declare message report
    public enum KEY {IMPORT};
    

    public CSVReport(KEY key)
    {
    	setOriginKey(key.name());
    }
    

}
