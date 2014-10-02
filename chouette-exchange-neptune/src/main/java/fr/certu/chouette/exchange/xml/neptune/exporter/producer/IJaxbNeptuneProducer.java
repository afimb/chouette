/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import org.trident.schema.trident.TridentObjectType;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * @author michel
 * 
 */
public interface IJaxbNeptuneProducer<T extends TridentObjectType, U extends NeptuneIdentifiedObject>
{
   T produce(U o);
}
