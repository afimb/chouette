/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.HashMap;
import java.util.Map;

import amivif.schema.AMIVIF_StopArea_Extension;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.StopArea;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifStopAreaComparator extends AbstractAmivifDataComparator
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

        StopArea[] sourceDataList = source.getStopArea();
        StopArea[] targetDataList = target.getStopArea();
        
        Map<String,StopArea> targetDataMap = new HashMap<String, StopArea>();

        // key = id origin
        for (StopArea targetData : targetDataList)
        {
            targetDataMap.put(master.getSourceId(targetData.getObjectId()), targetData);
        }

        boolean status = true;

        // here, we only check the data between two structure,
        // missing object has been detected by the Chouette comparator
        for (StopArea sourceData : sourceDataList)
        {
            ChouetteObjectState state = null;

            StopArea targetData = targetDataMap.remove(sourceData.getObjectId());
            if (targetData == null)
            {
                // missing target object; let see the next item
                continue;
            }
            
            state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            
            master.addObjectState(state);
            // check the Amivif attributes
            
            AMIVIF_StopArea_Extension sourceExt = sourceData.getAMIVIF_StopArea_Extension();
            AMIVIF_StopArea_Extension targetExt = targetData.getAMIVIF_StopArea_Extension();
            
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
