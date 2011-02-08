/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * @author michel
 *
 */
public interface IModelProducer <T extends NeptuneIdentifiedObject, U extends TridentObjectTypeType> 
{
    T produce(U o);
}
