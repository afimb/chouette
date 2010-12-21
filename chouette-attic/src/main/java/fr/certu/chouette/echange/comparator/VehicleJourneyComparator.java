package fr.certu.chouette.echange.comparator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;

/**
 * @author Dryade, Evelyne Zahn
 */
public class VehicleJourneyComparator extends AbstractChouetteDataComparator 
{

	private static final Log logger = LogFactory.getLog(VehicleJourneyComparator.class);

	public boolean compareData(IExchangeableLineComparator master) throws ComparatorException
	{


		this.master = master;

		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();

		List<Course> sourceDataList = source.getCourses();
		List<Course> targetDataList = target.getCourses();

		ExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();

		Map<String,Course> targetDataMap = new HashMap<String, Course>();

		// the natural key is source journeypattern and the start time :
		// checking for unique key
		Integer duplicates = populateDataMap(targetDataMap, targetDataList, targetMapper,true);

		// check all the elements of the source list, 
		// if found in the target map, remove it from the target map
		// at the end of processing, the target map contains only unknown objects in the source list
		boolean validList = true;
		for (Course sourceData : sourceDataList) 
		{
			//check if not corresponding to a duplicate key
			String key = buildKey(sourceData,master.getSourceExchangeMap(),false);
			Course targetData = targetDataMap.remove(key);
			{
				Integer i = 1;
				String searchKey = key;
				while (targetData == null && i != duplicates)
				{				
					searchKey = key +"-"+ i.toString();
					targetData = targetDataMap.remove(searchKey);
					i++;
				}
			}

			ChouetteObjectState objectState = null;
			if (targetData == null)
			{
				logger.debug("no target for "+sourceData.getObjectId()+"( "+key+")");
				objectState= new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);		    
			}
			else
			{
				objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
				/*
				List<Horaire> sourceStops = sourceMapper.getVehicleJourneyAtStopList(sourceData.getObjectId());
				List<Horaire> targetStops = targetMapper.getVehicleJourneyAtStopList(targetData.getObjectId());
				for (int i = 0; i < sourceStops.size(); i++)
				{
					objectState.addAttributeState("ArrivalTime("+i+")", timeToString(sourceStops.get(i).getArrivalTime()),
							                                            timeToString(targetStops.get(i).getArrivalTime()));
				}
                */
				master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());

			}
			validList = validList && objectState.isIdentical();
			master.addObjectState(objectState);
		}

		// advise for target object not found in source list
		for (Course targetData : targetDataMap.values().toArray(new Course[0]))
		{
			String key = buildKey(targetData,master.getTargetExchangeMap(),true);
			logger.debug("no source for "+targetData.getObjectId()+"( "+key+")");
			ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
			master.addObjectState(objectState);
			validList = false;
		}

		return validList;

	}

	private Integer populateDataMap(Map<String, Course> dataMap,
			List<Course> dataList,
			ExchangeableLineObjectIdMapper mapper, boolean withConversion) 
	{
		Integer duplicates = 1;
		for (Course data : dataList) 
		{
			String key = buildKey(data,mapper,withConversion);
			if (dataMap.containsKey(key))
			{
				Integer i = 1;
				String dupKey = key ;
				while (dataMap.containsKey(dupKey))
				{
					dupKey = key + "-"+i.toString();	
					i++;
				}
				if (i > duplicates) duplicates = i;
				key = dupKey;
			}
			dataMap.put(key, data);
		}
		return duplicates;
	}

	private String buildKey(Course data, ExchangeableLineObjectIdMapper mapper,
			boolean withConversion) 
	{
		String key = data.getJourneyPatternId();
		if (withConversion) key = master.getSourceId(key);
		if (key == null) 
		{
			logger.debug("no JourneyPatternId for "+data.getObjectId()+"( "+data.getJourneyPatternId()+")");
		}
		List<Horaire> stops = mapper.getVehicleJourneyAtStopList(data.getObjectId());
		for (Horaire stop : stops) 
		{
			key+= "-"+timeToString(stop.getArrivalTime());
		}
		
		return key;
	}

	@Override
	public Map<String, ChouetteObjectState> getStateMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private static final SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
	private static String timeToString(Date time)
	{
		return format.format(time);
	}


}
