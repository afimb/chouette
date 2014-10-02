/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.csv.exporter.producer;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.Report;

/**
 * @author michel
 * 
 */
public interface ICSVNeptuneProducer<T extends NeptuneIdentifiedObject>
{
   List<String[]> produce(T o, Report report);
}
