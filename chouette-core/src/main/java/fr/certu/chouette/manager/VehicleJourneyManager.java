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
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
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
	public void completeObject(User user, VehicleJourney vehicleJourney) throws ChouetteException
	{
		Line line = vehicleJourney.getLine();
		if(line != null)
			vehicleJourney.setLineIdShortcut(line.getObjectId());
		
		List<VehicleJourneyAtStop> vjass = vehicleJourney.getVehicleJourneyAtStops();
		for (int i = 0; i < vjass.size(); i++)
		{
			VehicleJourneyAtStop vjas = vjass.get(i);
			vjas.setOrder(i+1);
			vjas.setVehicleJourney(vehicleJourney);
			vjas.setVehicleJourneyId(vehicleJourney.getObjectId());
		}
	} 

	@Override
	public void saveAll(User user, List<VehicleJourney> vehicleJourneys, boolean propagate) throws ChouetteException 
	{
		super.saveAll(user, vehicleJourneys,propagate);

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
			if(!timetables.isEmpty())
				timetableManager.saveAll(user, timetables, propagate);
			if(!timeSlots.isEmpty())
				timeSlotManager.saveAll(user, timeSlots, propagate);
		}
	}
}
