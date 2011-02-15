/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.TridentObjectTypeType;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * @author michel
 *
 */
public interface ICastorNeptuneProducer <T extends TridentObjectTypeType, U extends NeptuneIdentifiedObject> 
{
    T produce(U o);
}
