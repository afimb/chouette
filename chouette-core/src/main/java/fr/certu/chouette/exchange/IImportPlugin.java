/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

/**
 * @author michel
 *
 * @param <T>
 */
public interface IImportPlugin<T extends NeptuneIdentifiedObject> extends IExchangePlugin
{

	/**
	 * import data
	 * 
	 * @param parameters
	 * @return
	 * @throws ExchangeException
	 */
	List<T> doImport(List<ParameterValue> parameters) throws ExchangeException;
}
