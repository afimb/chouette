package fr.certu.chouette.echange.comparator.amivif;

import java.util.HashMap;
import java.util.Map;

import amivif.schema.ConnectionLink;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopPointInConnection;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparatorException;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.modele.PositionGeographique;

/**
 * @author michel
 */
public class AmivifConnectionLinkComparator extends AbstractAmivifDataComparator
{

    public boolean compareData(ExchangeableAmivifLineComparator master)
            throws ServiceException
    {
        this.master = master;
        RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

        AmivifExchangeableLineObjectIdMapper sourceMapper = master.getSourceExchangeMap();
        AmivifExchangeableLineObjectIdMapper targetMapper = master.getTargetExchangeMap();

        ConnectionLink[] sourceDataList = source.getConnectionLink();
        ConnectionLink[] targetDataList = target.getConnectionLink();

        boolean validList = true;
        Map<String,ConnectionLink> sourceDataMap = new HashMap<String, ConnectionLink>();
        Map<String,ConnectionLink> targetDataMap = new HashMap<String, ConnectionLink>();

        // the natural key is the name of the start stopArea + 
        // the name of the end StopArea : checking for unique key
        for (ConnectionLink sourceData : sourceDataList) 
        {
            String key = buildKey(sourceData, sourceMapper);
            if (key == null)
            {
            	ChouetteObjectState objectState = null;
            	objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);       
            	master.addObjectState(objectState);
            }
            else
            {
            	if (sourceDataMap.put(key, sourceData) != null)
            	{
            		// duplicate key : cannot check correctly this data
            		throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"StartStopName+EndStopName in source ConnectionLink");
            	}
            }
        }
        for (ConnectionLink targetData : targetDataList) 
        {
            String key = buildKey(targetData, targetMapper);
            if (key == null)
            {
            	ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), null, targetData.getObjectId());       
            	master.addObjectState(objectState);
            }
            else
            {
            	if (targetDataMap.put(key,targetData) != null)
            	{
            		// duplicate key : cannot check correctly this data
            		throw new ComparatorException(ComparatorException.TYPE.DuplicateKey,"StopStopName+EndStopName in target ConnectionLink");
            	}
            }
        }

        // check all the elements of the source list, 
        // if found in the target map, remove it from the target map
        // at the end of processing, the target map contains only unknown objects in the source list
        for (ConnectionLink sourceData : sourceDataList) 
        {
            String key = buildKey(sourceData, sourceMapper);
            if (key == null)
            {
            	continue;
            }
            ConnectionLink targetData = targetDataMap.remove(key);
            ChouetteObjectState objectState = null;
            if (targetData == null)
            {
                objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), null);       
            }
            else
            {
                objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), targetData.getObjectId());
                objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());
                objectState.addAttributeState("DefaultDuration", sourceData.getDefaultDuration(), targetData.getDefaultDuration());

                master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
            }
            validList = validList && objectState.isIdentical();
            master.addObjectState(objectState);
        }

        // advise for target object not found in source list
        for (ConnectionLink targetData : targetDataMap.values())
        {
            ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), null, targetData.getObjectId());
            master.addObjectState(objectState);
            validList = false;
        }

        return validList;
    }

    private String buildKey(ConnectionLink data, AmivifExchangeableLineObjectIdMapper mapper)
    {
        String startId = data.getStartOfLink();
        String endId = data.getEndOfLink();
        
        String startKey = getKey(startId, mapper);
        String endKey = getKey(endId, mapper);
        if (startKey == null || endKey == null) return null;
        return startKey + "-" + endKey;
    }
    
    private String getKey(String id,
            AmivifExchangeableLineObjectIdMapper mapper)
    {
        PositionGeographique startLocal = mapper.getStopArea(id);
        if (startLocal != null)
        {
            return startLocal.getName();
        }
        else
        {
            StopPointInConnection startInConnection = mapper.getStopPointsInConnection(id);
            if (startInConnection == null)
            {
                return null ;
            }
            return startInConnection.getName() + "-" + startInConnection.getProjectedPoint().getX()
            	+ "-" + startInConnection.getProjectedPoint().getY();
        }
    }

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
