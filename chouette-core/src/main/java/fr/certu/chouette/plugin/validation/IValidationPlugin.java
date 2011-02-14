/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.validation;

import java.util.List;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * @author michel
 *
 */
public interface IValidationPlugin<T extends NeptuneIdentifiedObject>
{
    ValidationStepDescription getDescription();
    
    ReportItem doValidate(List<T> beans);
}
