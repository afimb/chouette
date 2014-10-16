/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.validation;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * @author michel
 * 
 */
public interface ICheckPointPlugin<T extends NeptuneIdentifiedObject>
{
   void check(List<T> beans, JSONObject parameters, PhaseReportItem report, Map<String, Object> context);
}
