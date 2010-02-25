package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class RouteComparator extends AbstractChouetteDataComparator 
{
    
	/* (non-Javadoc)
	 * @see fr.certu.chouette.echange.comparator.IChouetteDataComparator#compareData(fr.certu.chouette.echange.comparator.IExchangeableLineComparator)
	 */
	public boolean compareData(IExchangeableLineComparator master) throws ServiceException
	{
	    this.master = master;
		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();
		
		Map<String, List<String>> sourceChildsOfRoute = new HashMap<String, List<String>>();
        Map<String, List<String>> targetChildsOfRoute = new HashMap<String, List<String>>();

		populateChildrenMap(source, sourceChildsOfRoute);
        populateChildrenMap(target, targetChildsOfRoute);
		
        List<Itineraire> sourceDataList = source.getItineraires();
        List<Itineraire> targetDataList = target.getItineraires();

	    boolean validList = true;
		Map<String,Itineraire> sourceDataMap = new HashMap<String, Itineraire>();
		Map<String,Itineraire> targetDataMap = new HashMap<String, Itineraire>();
		
		// the natural key is the list of StopPoints identified by their source object id :
		// checking for unique key
		populateDataMap(sourceDataMap, sourceDataList, sourceChildsOfRoute,false);
		populateDataMap(targetDataMap, targetDataList, targetChildsOfRoute,true);
		
		// check all the elements of the source list, 
		// if found in the target map, remove it from the target map
		// at the end of processing, the target map contains only unknown objects in the source list
		for (Itineraire sourceData : sourceDataList) 
		{
		    Itineraire targetData = targetDataMap.remove(buildKey(sourceData, sourceChildsOfRoute,false));
		    ChouetteObjectState objectState = null;
		    if (targetData == null)
		    {
		        objectState= new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);		    
		    }
		    else
		    {
		        objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
		        objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());
                objectState.addAttributeState("Number", sourceData.getNumber(), targetData.getNumber());
                objectState.addAttributeState("Direction", sourceData.getDirection(), targetData.getDirection());
                objectState.addAttributeState("WayBack", sourceData.getWayBack(), targetData.getWayBack());
		        
                master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
		    }
		    validList = validList && objectState.isIdentical();
		    master.addObjectState(objectState);
		}
		
		// advise for target object not found in source list
		for (Itineraire targetData : targetDataMap.values().toArray(new Itineraire[0]))
        {
		    ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
		    master.addObjectState(objectState);
		    validList = false;
        }
		 
		return validList;
	}

    /**
     * fill the dataMap key->route with key builded from the list of StopPoint
     * 
     * @param dataMap
     * @param dataList
     * @param childsOfRoute
     * @param withConversion
     * @throws ServiceException
     */
    private void populateDataMap(Map<String, Itineraire> dataMap,
            List<Itineraire> dataList, Map<String, List<String>> childsOfRoute,
            boolean withConversion) throws ServiceException
    {
        for (Itineraire sourceData : dataList) 
		{
			if (dataMap.put(buildKey(sourceData, childsOfRoute,withConversion),sourceData) != null)
			{
				// duplicate key : cannot check correctly this data
				throw new ServiceException(CodeIncident.COMPARATOR_DUPLICATED_KEY,CodeDetailIncident.LIST,"StopPoint","Route");
			}
		}
    }

    /**
     * build a unique key for a Route : list of StopPoints + PublishedName + Direction
     * 
     * @param data
     * @param childsOfRoute
     * @param withConversion
     * @return
     */
    private String buildKey(Itineraire data,Map<String, List<String>> childsOfRoute,boolean withConversion)
    {
        List<String> stopPoints = childsOfRoute.get(data.getObjectId());
        if (withConversion) stopPoints = convertToSourceId(stopPoints);
        String key = Arrays.toString(stopPoints.toArray())+"-"+data.getPublishedName()+"-"+data.getDirection();
        return key;
    }
    
	/**
	 * populate the map Route id -> list of StopPoint ids using the reverse relation in class ILectureEchange
	 * 
	 * @param data
	 * @param childsOfRoute
	 */
	private void populateChildrenMap(ILectureEchange data,
            Map<String, List<String>> childsOfRoute)
    {
		    List<ArretItineraire> arrets = data.getArrets();
		    
		    for (ArretItineraire arret : arrets)
            {
                String key = arret.getObjectId();
                String value = data.getItineraireArret(key);
                int pos = arret.getPosition();
                List<String> children = childsOfRoute.get(value);
                if (children == null) 
                {
                    children = new ArrayList<String>();
                    childsOfRoute.put(value, children);
                }
                // size the list to at least the position of the stoppoint
                while (children.size() <= pos)
                {
                    children.add("no stoppoint at this rank");
                }
                children.set(pos,key);
            }
		    		    
    }

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }


}
