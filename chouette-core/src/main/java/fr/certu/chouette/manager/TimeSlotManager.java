/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import fr.certu.chouette.model.neptune.TimeSlot;

/**
 * @author michel
 *
 */
public class TimeSlotManager extends AbstractNeptuneManager<TimeSlot> {

	public TimeSlotManager() {
		super(TimeSlot.class);
	}

}
