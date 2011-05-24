/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;



/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class VehicleJourneyManager extends AbstractNeptuneManager<VehicleJourney> {

	private INeptuneManager<Timetable> timetableManager = (INeptuneManager<Timetable>) getManager(Timetable.class);
	private INeptuneManager<TimeSlot> timeSlotManager = (INeptuneManager<TimeSlot>) getManager(TimeSlot.class);

	public VehicleJourneyManager() {
		super(VehicleJourney.class);
	}

	@Override
	protected Logger getLogger() {
		return null;
	}
	
	@Override
	public void completeObject(User user, VehicleJourney vehicleJourney) 
	{
		Line line = vehicleJourney.getLine();
		if(line != null)
			vehicleJourney.setLineIdShortcut(line.getObjectId());
	} 

	@Override
	public void save(User user, VehicleJourney vehicleJourney, boolean propagate) throws ChouetteException 
	{
		super.save(user, vehicleJourney, propagate);

		if(propagate)
		{
			List<Timetable> timetables = vehicleJourney.getTimetables();
			if(timetables != null)
				timetableManager.saveAll(user, timetables, propagate);
			TimeSlot timeSlot = vehicleJourney.getTimeSlot();
			if(timeSlot != null)
				timeSlotManager.save(user, timeSlot, propagate);
		}
	}
}
