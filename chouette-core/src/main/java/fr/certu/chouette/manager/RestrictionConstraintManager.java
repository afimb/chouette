/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.RestrictionConstraint;

/**
 * @author michel
 *
 */
public class RestrictionConstraintManager extends AbstractNeptuneManager<RestrictionConstraint> {

	public RestrictionConstraintManager() {
		super(RestrictionConstraint.class);
	}

}
