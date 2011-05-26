/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.manager;

import java.util.ArrayList;
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
	public void saveAll(User user, List<VehicleJourney> vehicleJourneys, boolean propagate) throws ChouetteException 
	{
		super.saveOrUpdateAll(user, vehicleJourneys);

		if(propagate)
		{
			INeptuneManager<Timetable> timetableManager = (INeptuneManager<Timetable>) getManager(Timetable.class);
			INeptuneManager<TimeSlot> timeSlotManager = (INeptuneManager<TimeSlot>) getManager(TimeSlot.class);

			List<Timetable> timetables = new ArrayList<Timetable>();
			List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();

			for (VehicleJourney vehicleJourney : vehicleJourneys) 
			{
				if(vehicleJourney.getTimetables() != null && !timetables.containsAll(vehicleJourney.getTimetables()))
					timetables.addAll(vehicleJourney.getTimetables());	

				TimeSlot timeSlot = vehicleJourney.getTimeSlot();
				if(timeSlot != null && !timeSlots.contains(timeSlot))
					timeSlots.add(timeSlot);
			}
			if(timetables != null)
				timetableManager.saveAll(user, timetables, propagate);
			if(timeSlots != null)
				timeSlotManager.saveAll(user, timeSlots, propagate);
		}
	}
}
