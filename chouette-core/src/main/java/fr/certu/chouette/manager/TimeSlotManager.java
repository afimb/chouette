/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.TimeSlot;

/**
 * @author michel
 *
 */
public class TimeSlotManager extends AbstractNeptuneManager<TimeSlot> {

	private static final Logger logger = Logger.getLogger(TimeSlotManager.class); 

	public TimeSlotManager() {
		super(TimeSlot.class,TimeSlot.TIMESLOT_KEY);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
