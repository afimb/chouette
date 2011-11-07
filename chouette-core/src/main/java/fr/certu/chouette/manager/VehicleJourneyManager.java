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
import org.springframework.transaction.annotation.Transactional;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;



/**
 * @author michel
 *
 */
@SuppressWarnings("unchecked")
public class VehicleJourneyManager extends AbstractNeptuneManager<VehicleJourney> 
{
	private static final Logger logger = Logger.getLogger(VehicleJourneyManager.class);

	public VehicleJourneyManager()
	{
		super(VehicleJourney.class,VehicleJourney.VEHICLEJOURNEY_KEY);
	}

	@Override
	protected Logger getLogger() 
	{
		return logger;
	}

	@Override
	public void completeObject(User user, VehicleJourney vehicleJourney) throws ChouetteException
	{
		vehicleJourney.complete();
	} 
	@Transactional
	@Override
	public void saveAll(User user, List<VehicleJourney> vehicleJourneys, boolean propagate,boolean fast) throws ChouetteException 
	{

		if(propagate)
		{
			INeptuneManager<Company> companyManager = (INeptuneManager<Company>) getManager(Company.class);
			INeptuneManager<Timetable> timetableManager = (INeptuneManager<Timetable>) getManager(Timetable.class);
			INeptuneManager<TimeSlot> timeSlotManager = (INeptuneManager<TimeSlot>) getManager(TimeSlot.class);

			List<Timetable> timetables = new ArrayList<Timetable>();
			List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
			List<Company> companies = new ArrayList<Company>();

			for (VehicleJourney vehicleJourney : vehicleJourneys) 
			{
				mergeCollection(timetables,vehicleJourney.getTimetables());
				addIfMissingInCollection(timeSlots,vehicleJourney.getTimeSlot());
				addIfMissingInCollection(companies,vehicleJourney.getCompany());
			}
			if(!timetables.isEmpty())
				timetableManager.saveAll(user, timetables, propagate,fast);
			if(!timeSlots.isEmpty())
				timeSlotManager.saveAll(user, timeSlots, propagate,fast);
			if(!companies.isEmpty())
				companyManager.saveAll(user, companies, propagate,fast);
		}

		for (VehicleJourney vehicleJourney : vehicleJourneys) 
		{
			vehicleJourney.sortVehicleJourneyAtStops();
		}

		super.saveAll(user, vehicleJourneys,propagate,fast);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.AbstractNeptuneManager#addNew(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
	 */
	@Transactional
	@Override
	public VehicleJourney addNew(User user, VehicleJourney vehicleJourney)
	throws ChouetteException 
	{
		vehicleJourney.sortVehicleJourneyAtStops();
		return super.addNew(user, vehicleJourney);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.AbstractNeptuneManager#update(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
	 */
	@Transactional
	@Override
	public void update(User user, VehicleJourney vehicleJourney) throws ChouetteException 
	{
		vehicleJourney.sortVehicleJourneyAtStops();
		super.update(user, vehicleJourney);
	}


	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.AbstractNeptuneManager#save(fr.certu.chouette.model.user.User, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject, boolean)
	 */
	@Transactional
	@Override
	public void save(User user, VehicleJourney vehicleJourney, boolean propagate)
	throws ChouetteException 
	{
		vehicleJourney.sortVehicleJourneyAtStops();
		super.save(user, vehicleJourney, propagate);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.manager.AbstractNeptuneManager#saveOrUpdateAll(fr.certu.chouette.model.user.User, java.util.List)
	 */
	@Transactional
	@Override
	public void saveOrUpdateAll(User user, List<VehicleJourney> vehicleJourneys)
	throws ChouetteException 
	{
		for (VehicleJourney vehicleJourney : vehicleJourneys) 
		{
			vehicleJourney.sortVehicleJourneyAtStops();
		}
		super.saveOrUpdateAll(user, vehicleJourneys);
	}


}
