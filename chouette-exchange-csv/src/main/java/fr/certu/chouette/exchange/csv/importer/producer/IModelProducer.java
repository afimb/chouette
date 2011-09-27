/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.importer.producer;

import fr.certu.chouette.exchange.csv.exception.ExchangeException;
import fr.certu.chouette.exchange.csv.importer.ChouetteCsvReader;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.Report;

/**
 * @author michel
 *
 */
public interface IModelProducer <T extends NeptuneIdentifiedObject> 
{
    T produce(ChouetteCsvReader csvReader, String[] firstLine, String objectIdPrefix, Report report) throws ExchangeException;
}
