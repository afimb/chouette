package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author Dryade, Evelyne Zahn
 */
public class VehicleJourneyAndJourneyPatternComparator extends AbstractChouetteDataComparator 
{
    private String secondaryMappingKey;
    
    
	public boolean compareData(IExchangeableLineComparator master) throws ComparatorException
	{
		
		/**
		PrintWriter p = null;
		try
		{
			p = new PrintWriter(new File("/home/evelyne/Tmp/chouette-debug-traces-vj.log"));
		}
	
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		boolean sameVehiculesJourney = true;
		boolean sameJourneyPattern = true;

		this.master = master;

		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();

		List<Course> sourceDataList = source.getCourses();
		List<Course> targetDataList = target.getCourses();

		
		
		ExchangeableLineObjectIdMapper sourceMapper = master.getSourceExchangeMap();
		ExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();
		
		
		// sourceJourneyPatternIdsByStoppointsKey intermediate working structure 
		//between the two main loops for journey pattern's ids mapping
		HashMap<String, String> sourceJourneyPatternIdsByStoppointsKey = new HashMap<String, String>();
		
		// sourceObjectIdBytargetNaturalKey mapping, intermediate working structure 
		//between the two main loops for vehicle journey's ids mapping
		HashMap<String, String> sourceVehiculeJourneyIdsBytargetNaturalKey = new HashMap<String, String>();				
		
		// working structure  
		HashMap<String, String> journeyPatternIds = new HashMap<String, String>();
		
		// duplicates keys in journey patterns can exist
		// this counter "help" to dissociates keys 
		Integer duplicates = 1;				
		for (Course vehiculeJourney : sourceDataList) 
		{
			String vehicleJourneyId = vehiculeJourney.getObjectId();
			String journeyPatternId = vehiculeJourney.getJourneyPatternId();

			String targetVehiculeKey = ((Integer) vehiculeJourney.getNumber()).toString();
			String targetJourneyKey = "";

			List<Horaire> vehiculesJourneyAtStop = sourceMapper.getVehicleJourneyAtStopList(vehicleJourneyId);
			for (Horaire vehiculeAtStop : vehiculesJourneyAtStop) 
			{
				String arrivalTime = vehiculeAtStop.getArrivalTime().toString();
				Integer order = vehiculeAtStop.getVehicleJourneyAtStop().getOrder();
				String sourceStoppointId = vehiculeAtStop.getStopPointId();
				String targetStoppointId = master.getTargetId(sourceStoppointId);

				// build target vehicule key
				targetVehiculeKey += "-" + arrivalTime + "-" + order.toString()+ "-" + targetStoppointId;

				// build journey pattern key
				if (targetJourneyKey.length() != 0) 
				{
					targetJourneyKey += "-";
				}
				targetJourneyKey += targetStoppointId;
			}

			// add source vehicules journeys id by vehicule journey target natural key
			if (sourceVehiculeJourneyIdsBytargetNaturalKey.put(targetVehiculeKey, vehicleJourneyId) != null)
			{
				throw new ComparatorException(ComparatorException.TYPE.DuplicateKey, "Course : " + vehicleJourneyId);
			}

			
			// add source journey patterns id by journey pattern target natural key
			if ( ! journeyPatternIds.containsKey(journeyPatternId))
			{	
				if (sourceJourneyPatternIdsByStoppointsKey.containsKey(targetJourneyKey))					
				{			
					targetJourneyKey += duplicates.toString();
					sourceJourneyPatternIdsByStoppointsKey.put(targetJourneyKey, journeyPatternId);
					//p.println("duplicate mission : " + journeyPatternId);
					//p.println("corresponding target mission : " + targetJourneyKey);
					duplicates ++;
				}
				//add new target JP key
				sourceJourneyPatternIdsByStoppointsKey.put(targetJourneyKey, journeyPatternId);
				
				//add checked JP
				journeyPatternIds.put(journeyPatternId, "checked-unused-field");
			}
		}
		
		//p.println("source journey pattern ids found : " + journeyPatternIds.toString());
		
		// do mapping considering target keys from target list
		//deferred mission object state registering
		List<ChouetteObjectState> missionsStates = new ArrayList<ChouetteObjectState>();
		
		journeyPatternIds = new HashMap<String, String>();	
		for (Course vehiculeJourney : targetDataList) 
		{
			String targetObjectId = vehiculeJourney.getObjectId();
			String journeyPatternId = vehiculeJourney.getJourneyPatternId();

			String targetCourseKey = ((Integer)vehiculeJourney.getNumber()).toString();
			String targetJourneyKey = "";

			List<Horaire> vehiculesJourneyAtStop = targetMapper.getVehicleJourneyAtStopList(targetObjectId);
			for (Horaire vehiculeAtStop : vehiculesJourneyAtStop) 
			{
				String arrivalTime = vehiculeAtStop.getArrivalTime().toString();
				Integer order = vehiculeAtStop.getVehicleJourneyAtStop().getOrder();
				String targetStoppointId = vehiculeAtStop.getStopPointId();

				// build VJ key
				targetCourseKey += "-" + arrivalTime + "-" + order.toString()+ "-" + targetStoppointId;
				
				// build JP key
				if (targetJourneyKey.length() != 0) 
				{
					targetJourneyKey += "-";
				}
				targetJourneyKey += targetStoppointId;
			}

			// Map vehiculesJourney
			ChouetteObjectState objectState = null;
			String sourceObjectId = sourceVehiculeJourneyIdsBytargetNaturalKey.remove(targetCourseKey);
			if (sourceObjectId == null) 
			{
				objectState = new ChouetteObjectState(getSecondaryMappingKey(), null, targetObjectId);
				sameVehiculesJourney = false;
			} 
			else 
			{
				objectState = new ChouetteObjectState(getSecondaryMappingKey(), sourceObjectId, targetObjectId);
				master.addMappingIds(sourceObjectId, targetObjectId);
			}
			master.addObjectState(objectState);

			
			// Map journeyPattern
			if ( ! journeyPatternIds.containsKey(journeyPatternId))
			{
				String sourceJourneyId = sourceJourneyPatternIdsByStoppointsKey.remove(targetJourneyKey);
				Integer i = 1;
				String searchedKey = null;
				while (sourceJourneyId == null && i != duplicates)
				{
					searchedKey = targetJourneyKey + i.toString();
					sourceJourneyId = sourceJourneyPatternIdsByStoppointsKey.remove(searchedKey);
					i ++;
				}
			
				if (sourceJourneyId == null) 
				{
					//p.println("not found source mission : " + journeyPatternId);
					//p.println("corresponding target mission : " + targetJourneyKey);
					objectState = new ChouetteObjectState(getMappingKey(), null, journeyPatternId);
					missionsStates.add(objectState);
					//master.addObjectState(objectState);
					sameJourneyPattern = false;
				} 
				else 
				{
					objectState = new ChouetteObjectState(getMappingKey(), sourceJourneyId, journeyPatternId);
					missionsStates.add(objectState);
					//master.addObjectState(objectState);
					master.addMappingIds(sourceJourneyId, journeyPatternId);
				}
				
				journeyPatternIds.put(journeyPatternId, "checked-unused-field");
			}
		}
		
		//p.println("target journey pattern ids found : " + journeyPatternIds.toString());
		
		// Unmapped source vehicle journeys
		Collection<String> unmappedSourceObjects = sourceVehiculeJourneyIdsBytargetNaturalKey.values();
		if (unmappedSourceObjects.size() != 0) 
		{
			sameVehiculesJourney = false;
			for (String sourceObjectId : unmappedSourceObjects) 
			{
				ChouetteObjectState objectState = new ChouetteObjectState(getSecondaryMappingKey(), sourceObjectId, null);
				master.addObjectState(objectState);
			}
		}

		// Register missions states
		for (ChouetteObjectState missionState : missionsStates) 
		{
			master.addObjectState(missionState);
		}

		// Unmapped source journey patterns
		Collection<String> unmappedSourceJourneyPatterns = sourceJourneyPatternIdsByStoppointsKey.values();
		if (unmappedSourceJourneyPatterns.size() != 0) {
			sameJourneyPattern = false;
			for (String sourceObjectId : unmappedSourceJourneyPatterns) {
				ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceObjectId, null);
				master.addObjectState(objectState);
			}
		}
		
		//p.close();
		return (sameVehiculesJourney && sameJourneyPattern);
	}

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSecondaryMappingKey()
    {
        return secondaryMappingKey;
    }

    public void setSecondaryMappingKey(String secondaryMappingKey)
    {
        this.secondaryMappingKey = secondaryMappingKey;
    }

}
