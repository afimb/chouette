package fr.certu.chouette.echange.comparator.amivif;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopPointInConnection;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparatorException;

/**
 * @author michel
 *
 */
public class AmivifStopPointInConnectionComparator extends AbstractAmivifDataComparator 
{
  private static final Log logger = LogFactory.getLog(AmivifStopPointInConnectionComparator.class);

	public boolean compareData(ExchangeableAmivifLineComparator master)
			throws ComparatorException{		
		
		boolean sameConnectionLinks = true;
		
		this.master = master;

		RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

		HashMap<String, StopPointInConnection> sourceObjectByNaturalKey = new HashMap<String, StopPointInConnection>();
		StopPointInConnection[] sourceDataList = source.getStopPointInConnection();
		
		Integer duplicates = 1;
		for (StopPointInConnection sourceCSTP : sourceDataList) 
		{
			String key = buildKey(sourceCSTP);			
			if (sourceObjectByNaturalKey.containsKey(key))
			{				
				Integer i = 1;
				String dupKey = key ;
				while (sourceObjectByNaturalKey.containsKey(dupKey))
				{
					dupKey = key + "-"+i.toString();	
				   i++;
				}
				if (i > duplicates) duplicates = i;
				key = dupKey;
			}
			sourceObjectByNaturalKey.put(key, sourceCSTP);
		}
				
		StopPointInConnection[] targetDataList = target.getStopPointInConnection();
		for (StopPointInConnection targetCSTP : targetDataList) 
		{
			String key = buildKey(targetCSTP);
			StopPointInConnection sourceCSTP = sourceObjectByNaturalKey.remove(key);
			
			//check if not corresponding to a duplicate key
			Integer i = 1;
			String searchKey = key;
			while (sourceCSTP == null && i != duplicates)
			{				
				searchKey = key +"-"+ i.toString();
				sourceCSTP = sourceObjectByNaturalKey.remove(searchKey);
				i++;
			}
			
			ChouetteObjectState objectState = null;
			if (sourceCSTP == null)
			{				
        logger.debug("no source for targetId : " + targetCSTP.getObjectId() + " key = " + key);
				objectState = new ChouetteObjectState(getMappingKey(), null, targetCSTP.getObjectId());
				sameConnectionLinks = false;
			}
			else 
			{
				objectState = new ChouetteObjectState(getMappingKey(), sourceCSTP.getObjectId(), targetCSTP.getObjectId());
				master.addMappingIds(sourceCSTP.getObjectId(), targetCSTP.getObjectId());
			}
			master.addObjectState(objectState);
		}
		
		// Unmapped source vehicle journeys
		Collection<StopPointInConnection> unmappedSourceObjects = sourceObjectByNaturalKey.values();
		if (unmappedSourceObjects.size() != 0)
		{
			sameConnectionLinks = false;
			for (StopPointInConnection sourceObject : unmappedSourceObjects)
			{
        String key = buildKey(sourceObject);
        logger.debug("no target for sourceId : " + sourceObject.getObjectId() + " key = " + key);
				ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceObject.getObjectId(), null);
				master.addObjectState(objectState);
			}
		}
		
		
		return sameConnectionLinks;
	}
	
	private String buildKey(StopPointInConnection data)
	{
		//String key = data.getName() + "-" + data.getProjectedPoint().getX();
		String key = data.getName() + "-" 
		           + data.getProjectedPoint().getX() + "-" 
		           + data.getProjectedPoint().getY() + "-" 
		           + data.getAddress().getStreetName() + "-" 
		           + data.getAddress().getPostalCode();
		return key;
	}

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
