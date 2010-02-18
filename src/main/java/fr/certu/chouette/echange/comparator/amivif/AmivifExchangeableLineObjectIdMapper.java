/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;


import java.util.HashMap;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.comparator.ExchangeableLineObjectIdMapper;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopPointInConnection;


public class AmivifExchangeableLineObjectIdMapper extends ExchangeableLineObjectIdMapper
{
    private HashMap<String, StopPointInConnection> stopPointsInConnectionByIdMap;
    
	public AmivifExchangeableLineObjectIdMapper(RespPTLineStructTimetable line, ILectureEchange commonLine)
    {
        super(commonLine);
		// map of StopArea in connection
    	stopPointsInConnectionByIdMap = new HashMap<String, StopPointInConnection>();
        StopPointInConnection[] externalStoppoints = line.getStopPointInConnection(); 
        
        for (StopPointInConnection stopPointInConnection : externalStoppoints) 
        {        	
        	stopPointsInConnectionByIdMap.put(stopPointInConnection.getObjectId(), stopPointInConnection);
		}
    }
	
	public HashMap<String, StopPointInConnection> getStopPointsInConnectionByIdMap() 
    {
		return stopPointsInConnectionByIdMap;
	}

    public StopPointInConnection getStopPointsInConnection(String id)
    {
        return stopPointsInConnectionByIdMap.get(id);
    }

}

