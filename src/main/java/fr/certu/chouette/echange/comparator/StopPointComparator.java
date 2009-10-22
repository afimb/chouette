package fr.certu.chouette.echange.comparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.ThrowsAdvice;

import chouette.schema.StopPoint;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class StopPointComparator extends AbstractChouetteDataComparator 
{
	     
	public boolean compareData(IExchangeableLineComparator master) throws Exception
	{
	    this.master = master;
		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();
		
		ExchangeableLineObjectIdMapper sourceMapper = master.getSourceExchangeMap();
		ExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();
		
        List<ArretItineraire> sourceDataList = source.getArrets();
        List<ArretItineraire> targetDataList = target.getArrets();

        if (sourceDataList == null || sourceDataList.size() == 0) 
        {
        	throw new ComparatorException(ComparatorException.TYPE.UnbuildResource,"ILectureEchange source getArrets");
        }
        if (targetDataList == null || targetDataList.size() == 0) 
        {
        	throw new ComparatorException(ComparatorException.TYPE.UnbuildResource,"ILectureEchange target getArrets");
        }
	    
        boolean validList = true;
		
		Map<String,ArretItineraire> targetDataMap = new HashMap<String, ArretItineraire>();

		for (ArretItineraire targetData : targetDataList) 
		{
            String key = buildKey(targetData, targetMapper);
            //String key = parent.getName()+"-"+targetData.getPosition()+"-"+parent.getStreetName();
            if (targetDataMap.put(key,targetData) != null)
			{
				// duplicate key : cannot check correctly this data
				throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"StopAreaName+Position in target StopPoint");
			}
		}

        
		// check all the elements of the source list, 
		// if found in the target map, remove it from the target map
		// at the end of processing, the target map contains only unknown objects in the source list
		for (ArretItineraire sourceData : sourceDataList) 
		{
			String parentId = sourceData.getContainedIn();
		    PositionGeographique sourceParent = sourceMapper.getStopArea(parentId);		    
		    ArretItineraire targetData = targetDataMap.remove(buildKey(sourceData, sourceMapper));
		    ChouetteObjectState objectState = null;
		    if (targetData == null)
		    {
		        //throw new ServiceException(CodeIncident.COMPARATOR_UNVAILABLE_RESOURCE,"No target stoppoint");
		    	objectState= new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);		    
		    }
		    else
		    {
		    	String targetParentId = targetData.getContainedIn();		        
		        PositionGeographique targetParent = targetMapper.getStopArea(targetParentId);
		        
		    	objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
		        objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());
	
		        objectState.addAttributeState("ParentName", sourceParent.getName(), targetParent.getName());
		        objectState.addAttributeState("ParentLatitude", sourceParent.getLatitude(), targetParent.getLatitude());
		        objectState.addAttributeState("ParentLongitude", sourceParent.getLongitude(), targetParent.getLongitude());
		        objectState.addAttributeState("Position", sourceData.getPosition(), targetData.getPosition());
		        
                master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
		    }
		    validList = validList && objectState.isIdentical();
		    master.addObjectState(objectState);
		}
		
		// advise for target object not found in source list
		for (ArretItineraire targetData : targetDataMap.values().toArray(new ArretItineraire[0]))
        {
		    ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
		    master.addObjectState(objectState);
		    validList = false;
        }
		return validList;
	}
	
	private String buildKey(ArretItineraire data, ExchangeableLineObjectIdMapper mapper)
    {
		String parentId = data.getContainedIn();
	    PositionGeographique parent = mapper.getStopArea(parentId);
		return parent.getName()+"-"+parent.getLatitude()+"-"+parent.getLongitude()+"-"+data.getPosition();
    }

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
