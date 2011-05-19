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
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;



/**
 * @author michel
 *
 */
public class VehicleJourneyManager extends AbstractNeptuneManager<VehicleJourney> {

	public VehicleJourneyManager() {
		super(VehicleJourney.class);
	}

	@Override
	protected Logger getLogger() {
		return null;
	}
	@Override
	public void completeObject(User user, VehicleJourney vehicleJourney) {
		Line line = vehicleJourney.getLine();
		if(line != null)
			vehicleJourney.setLineIdShortcut(line.getObjectId());
	}
}
