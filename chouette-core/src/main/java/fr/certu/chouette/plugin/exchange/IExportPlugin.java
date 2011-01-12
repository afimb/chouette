/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.ReportHolder;

public interface IExportPlugin<T extends NeptuneIdentifiedObject> extends IExchangePlugin
{

	/**
	 * export data
	 * 
	 * @param beans
	 * @param parameters
	 * @param report
	 * @throws ExchangeException
	 */
	void doExport(List<T> beans,List<ParameterValue> parameters,ReportHolder report) throws ExchangeException;

}
