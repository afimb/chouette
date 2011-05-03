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

/**
 * @author michel
 *
 */
public interface IValidationPlugin<T extends NeptuneIdentifiedObject>
{
    ValidationStepDescription getDescription();
    
    List<ValidationClassReportItem> doValidate(List<T> beans, ValidationParameters parameters);
}
