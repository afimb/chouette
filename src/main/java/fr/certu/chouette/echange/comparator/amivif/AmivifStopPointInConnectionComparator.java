package fr.certu.chouette.echange.comparator.amivif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopPointInConnection;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparatorException;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifStopPointInConnectionComparator extends AbstractAmivifDataComparator 
{

	public boolean compareData(ExchangeableAmivifLineComparator master)
			throws ComparatorException{		
		
		/**
		PrintWriter p = null;
		try 
		{
			p = new PrintWriter(new File("/home/evelyne/Tmp/chouette-debug-traces.log"));
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    */
		boolean sameConnectionLinks = true;
		
		this.master = master;

		RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

		HashMap<String, String> sourceObjectIdByNaturalKey = new HashMap<String, String>();
		StopPointInConnection[] sourceDataList = source.getStopPointInConnection();
		
		Integer duplicates = 1;
		for (StopPointInConnection sourceCSTP : sourceDataList) 
		{
			String key = buildKey(sourceCSTP);			
			if (sourceObjectIdByNaturalKey.containsKey(key))
			{				
				key += duplicates.toString();				
				//p.println("Duplicate source found : " + sourceCSTP.getObjectId());
				//p.println("Add duplicatekey : " + key);
				duplicates ++;
			}
			sourceObjectIdByNaturalKey.put(key, sourceCSTP.getObjectId());
		}
		
		StopPointInConnection[] targetDataList = target.getStopPointInConnection();
		for (StopPointInConnection targetCSTP : targetDataList) 
		{
			String key = buildKey(targetCSTP);
			String sourceCSTPId = sourceObjectIdByNaturalKey.remove(key);
			
			//check if not corresponding to a duplicate key
			Integer i = 1;
			String searchKey = key;
			while (sourceCSTPId == null && i != duplicates)
			{				
				searchKey = key + i.toString();
				//p.println("search possible duplicate key : " + searchKey);
				sourceCSTPId = sourceObjectIdByNaturalKey.remove(searchKey);
				//debug
				if (sourceCSTPId != null)
				{
					//p.println("=> Duplicate mapped source : " + sourceCSTPId);
				}
				i++;
			}
			
			ChouetteObjectState objectState = null;
			if (sourceCSTPId == null)
			{				
				objectState = new ChouetteObjectState(getMappingKey(), null, targetCSTP.getObjectId());
				sameConnectionLinks = false;
			}
			else 
			{
				objectState = new ChouetteObjectState(getMappingKey(), sourceCSTPId, targetCSTP.getObjectId());
				master.addMappingIds(sourceCSTPId, targetCSTP.getObjectId());
			}
			master.addObjectState(objectState);
		}
		
		// Unmapped source vehicle journeys
		Collection<String> unmappedSourceObjects = sourceObjectIdByNaturalKey.values();
		if (unmappedSourceObjects.size() != 0)
		{
			sameConnectionLinks = false;
			for (String sourceObjectId : unmappedSourceObjects)
			{
				ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceObjectId, null);
				master.addObjectState(objectState);
			}
		}
		
		//p.println("closing descriptor");
		//p.close();
		
		return sameConnectionLinks;
	}
	
	private String buildKey(StopPointInConnection data)
	{
		//String key = data.getName() + "-" + data.getProjectedPoint().getX();
		String key = data.getProjectedPoint().getX() + "-" + data.getProjectedPoint().getY();
		return key;
	}

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
