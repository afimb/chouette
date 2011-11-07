/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public interface IModelProducer <T extends NeptuneIdentifiedObject, U extends TridentObjectTypeType> 
{
    T produce(U o,ReportItem report,SharedImportedData sharedData);
}
