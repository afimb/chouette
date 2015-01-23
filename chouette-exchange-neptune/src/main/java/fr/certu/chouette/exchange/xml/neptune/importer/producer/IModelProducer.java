/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.TridentObjectType;

import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * @author michel
 * 
 */
public interface IModelProducer<T extends NeptuneIdentifiedObject, U extends TridentObjectType>
{
   T produce(Context context, U o);
}
