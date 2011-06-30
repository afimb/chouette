/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Timetable;

/**
 * @author michel
 *
 */
public class TimetableManager extends AbstractNeptuneManager<Timetable> {
	private static final Logger logger = Logger.getLogger(TimetableManager.class); 

	public TimetableManager() {
		super(Timetable.class,Timetable.TIMETABLE_KEY);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}
}
