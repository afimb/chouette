/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.RestrictionConstraint;
import fr.certu.chouette.model.user.User;

/**
 * @author michel
 *
 */
public class RestrictionConstraintManager extends AbstractNeptuneManager<RestrictionConstraint> {

	private static final Logger logger = Logger.getLogger(RestrictionConstraintManager.class); 

	public RestrictionConstraintManager() {
		super(RestrictionConstraint.class,RestrictionConstraint.STOPAREA_KEY);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
	@Override
	public void completeObject(User user, RestrictionConstraint constraint) {
		Line line =constraint.getLine();
		if(line  != null)
			constraint.setLineIdShortCut(line.getObjectId());
	}
}
