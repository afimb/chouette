package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class JourneyPatternComparator extends AbstractChouetteDataComparator 
{
	private static final Log logger = LogFactory.getLog(JourneyPatternComparator.class);
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.echange.comparator.IChouetteDataComparator#compareData(fr.certu.chouette.echange.comparator.IExchangeableLineComparator)
	 */
	public boolean compareData(IExchangeableLineComparator master) throws ServiceException
	{
		this.master = master;
		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();

		Map<String, List<String>> sourceChildsOfJourneyPattern = new HashMap<String, List<String>>();
		Map<String, List<String>> targetChildsOfJourneyPattern = new HashMap<String, List<String>>();

		List<Mission> sourceDataList = source.getMissions();
		List<Mission> targetDataList = target.getMissions();

		populateChildrenMap(master.getSourceExchangeMap(), sourceDataList, sourceChildsOfJourneyPattern);
		populateChildrenMap(master.getTargetExchangeMap(), targetDataList, targetChildsOfJourneyPattern);

		boolean validList = true;
		// Map<String,Mission> sourceDataMap = new HashMap<String, Mission>();
		Map<String,Mission> targetDataMap = new HashMap<String, Mission>();

		// the natural key is the list of StopPoints identified by their source object id :
		// checking for unique key
		// populateDataMap(sourceDataMap, sourceDataList, sourceChildsOfJourneyPattern,master.getSourceExchangeMap(),false);
		populateDataMap(targetDataMap, targetDataList, targetChildsOfJourneyPattern,master.getTargetExchangeMap(),true);

		// check all the elements of the source list, 
		// if found in the target map, remove it from the target map
		// at the end of processing, the target map contains only unknown objects in the source list
		for (Mission sourceData : sourceDataList) 
		{
			String key = buildKey(sourceData, sourceChildsOfJourneyPattern,master.getSourceExchangeMap(),false);
			Mission targetData = targetDataMap.remove(key);
			ChouetteObjectState objectState = null;
			if (targetData == null)
			{
				if (key.startsWith("[]"))
				{
					if (isVerbose())
					   logger.debug("no target for "+sourceData.getObjectId()+"( "+key+") ignored, cause : no vehicleJourney");
				}
				else
				{
					logger.debug("no target for "+sourceData.getObjectId()+"( "+key+")");
					objectState= new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);	
				}
			}
			else
			{
				objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
				objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());

				master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
			}
			// skip history if ignored
			if (objectState != null)
			{
				validList = validList && objectState.isIdentical();
				master.addObjectState(objectState);
			}
		}

		// advise for target object not found in source list
		for (Mission targetData : targetDataMap.values().toArray(new Mission[0]))
		{
			String key = buildKey(targetData,targetChildsOfJourneyPattern,master.getTargetExchangeMap(),true);
			if (key.startsWith("[]"))
			{
				if (isVerbose())
				   logger.debug("no source for "+targetData.getObjectId()+"( "+key+") but ignored , cause : no vehicleJourney");				
			}
			else
			{
				logger.debug("no source for "+targetData.getObjectId()+"( "+key+")");
				ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
				master.addObjectState(objectState);
				validList = false;
			}
		}

		return validList;
	}

	/**
	 * fill the dataMap key->journeyPattern with key builded from the list of StopPoint
	 * 
	 * @param dataMap
	 * @param dataList
	 * @param childsOfRoute
	 * @param withConversion
	 * @param mapper 
	 * @throws ServiceException
	 */
	private void populateDataMap(Map<String, Mission> dataMap,
			List<Mission> dataList, Map<String, List<String>> childsOfJourneyPattern,
			ExchangeableLineObjectIdMapper mapper, boolean withConversion) throws ServiceException
			{
		for (Mission sourceData : dataList) 
		{
			String key = buildKey(sourceData, childsOfJourneyPattern,mapper,withConversion);
			Mission oldSourceData = dataMap.put(key,sourceData);
			if (oldSourceData  != null)
			{
				// duplicate key : cannot check correctly this data
				if (key.startsWith("[]"))
				{
					if (isVerbose())
					   logger.debug("duplicate key = "+key+" between "+sourceData.getObjectId()+" and "+oldSourceData.getObjectId()+" ignored, cause : no vehicleJourney");
				}
				else
				{
					logger.error("duplicate key = "+key+" between "+sourceData.getObjectId()+" and "+oldSourceData.getObjectId());
					throw new ServiceException(CodeIncident.COMPARATOR_DUPLICATED_KEY,CodeDetailIncident.DEFAULT,"JourneyPatternKey",sourceData.getObjectId());
				}
			}
		}
			}

	/**
	 * build a unique key for a JourneyPattern : list of StopPoints + Name + VehicleJourneyCount + route_direction
	 * 
	 * @param data
	 * @param childsOfRoute
	 * @param withConversion
	 * @return
	 */
	private String buildKey(Mission data,Map<String, List<String>> childsOfJourney,ExchangeableLineObjectIdMapper mapper ,boolean withConversion)
	{
		List<String> stopPoints = childsOfJourney.get(data.getObjectId());
		List<String> timetables = mapper.getTimetableIdList(data.getObjectId());
		if (timetables == null) 
		{
			timetables = new ArrayList<String>();
		}

		if (withConversion) 
		{
			stopPoints = convertToSourceId(stopPoints);
			timetables = convertToSourceId(timetables);
		}
		String[] timetableArray = timetables.toArray(new String[0]);
		Arrays.sort(timetableArray);
		List<Course> vehicleJourneys = mapper.getVehicleJourneyList(data.getObjectId());
		int vehicleJourneyCount = 0;
		if (vehicleJourneys != null) vehicleJourneyCount = vehicleJourneys.size();
		Itineraire route = mapper.getRoute(data.getRouteId());

		String key = Arrays.toString(stopPoints.toArray())+"-"+data.getName()+"-"+vehicleJourneyCount+"-"+route.getWayBack()+"-"+Arrays.toString(timetableArray);
		if (isVerbose() && timetables.size() == 0) 
		{
			logger.debug("no timetable for "+(withConversion?"target":"source")+" journeyPatternId "+data.getObjectId()+" key = "+key);
		}
		return key;
	}

	/**
	 * populate the map Route id -> list of StopPoint ids using the reverse relation in class ILectureEchange
	 * 
	 * @param mapper
	 * @param childsOfRoute
	 */
	private void populateChildrenMap(ExchangeableLineObjectIdMapper mapper, List<Mission> journeyPatterns,
			Map<String, List<String>> childsOfJourneyPattern)
	{
		for (Mission journeyPattern : journeyPatterns) 
		{
			String key = journeyPattern.getObjectId();
			List<Horaire> stopList = mapper.getVehicleJourneyAtStopListOfJourneyPattern(key);
			List<String> stopIdList = new ArrayList<String>();
			if (stopList != null)
			{
				for (Horaire stopTime : stopList) 
				{
					stopIdList.add(stopTime.getStopPointId());
				}
			}
			childsOfJourneyPattern.put(key, stopIdList);
		}
	}

	@Override
	public Map<String, ChouetteObjectState> getStateMap()
	{
		// TODO Auto-generated method stub
		return null;
	}


}
