/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.HashMap;
import java.util.Map;

import amivif.schema.AMIVIF_StopPoint_Extension;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopPoint;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifStopPointComparator extends AbstractAmivifDataComparator
{

    /* (non-Javadoc)
     * @see fr.certu.chouette.echange.comparator.amivif.IAmivifDataComparator#compareData(fr.certu.chouette.echange.comparator.ExchangeableAmivifLineComparator)
     */
    public boolean compareData(ExchangeableAmivifLineComparator master)
    throws ServiceException
    {
    	this.master = master;
    	
    	RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

        StopPoint[] sourceDataList = source.getStopPoint();
        StopPoint[] targetDataList = target.getStopPoint();
        
        Map<String,StopPoint> targetDataMap = new HashMap<String, StopPoint>();

        // key = id origin
        for (StopPoint targetData : targetDataList)
        {
            targetDataMap.put(master.getSourceId(targetData.getObjectId()), targetData);
        }

        boolean status = true;

        // here, we only check the data between two structure,
        // missing object has been detected by the Chouette comparator
        for (StopPoint sourceData : sourceDataList)
        {
            ChouetteObjectState state = null;

            StopPoint targetData = targetDataMap.remove(sourceData.getObjectId());
            if (targetData == null)
            {
                // missing target object; let see the next item
                continue;
            }
            
            state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            
            master.addObjectState(state);
            // check the Amivif attributes
            
            AMIVIF_StopPoint_Extension sourceExt = sourceData.getAMIVIF_StopPoint_Extension();
            AMIVIF_StopPoint_Extension targetExt = targetData.getAMIVIF_StopPoint_Extension();
            
            state.addAttributeState("UpFarZone", sourceExt.getUpFarZone(), targetExt.getUpFarZone());
            state.addAttributeState("DownFarZone", sourceExt.getDownFarZone(), targetExt.getDownFarZone());
            
            status = status && state.isIdentical();
        }
        
        return status;   
    }

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
