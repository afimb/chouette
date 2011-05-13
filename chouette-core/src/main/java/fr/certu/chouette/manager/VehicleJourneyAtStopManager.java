package fr.certu.chouette.manager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.user.User;

public class VehicleJourneyAtStopManager extends AbstractNeptuneManager<VehicleJourneyAtStop> {

	public VehicleJourneyAtStopManager() {
		super(VehicleJourneyAtStop.class);
	}
	
	@Override
	public void remove(User user,VehicleJourneyAtStop vehicleJourneyAtStop,boolean propagate) throws ChouetteException
	{
		
	}

}
