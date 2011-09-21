/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.neptune.importer.producer;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public interface IModelProducer <T extends NeptuneIdentifiedObject> 
{
    T produce(CSVReader csvReader, String[] firstLine);
}
