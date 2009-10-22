/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.HashMap;
import java.util.Map;

import amivif.schema.PTLink;
import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifPtLinkComparator extends AbstractAmivifDataComparator
{

    /* (non-Javadoc)
     * @see fr.certu.chouette.echange.comparator.amivif.IAmivifDataComparator#compareData(fr.certu.chouette.echange.comparator.ExchangeableAmivifLineComparator)
     */

    public boolean compareData(ExchangeableAmivifLineComparator master) throws ServiceException
    {
    	this.master = master;
    	
    	RespPTLineStructTimetable source = master.getAmivifSource();
        RespPTLineStructTimetable target = master.getAmivifTarget();

        PTLink[] sourceDataList = source.getPTLink();
        PTLink[] targetDataList = target.getPTLink();

        Map<String,PTLink> targetDataMap = new HashMap<String, PTLink>();

        // key = source startID+sourceEndId 
        for (PTLink targetData : targetDataList)
        {
            String start = master.getSourceId(targetData.getStartOfLink());
            String end =  master.getSourceId(targetData.getEndOfLink());
            String key = start+"-"+end;
            targetDataMap.put(key, targetData);
        }

        boolean status = true;

        for (PTLink sourceData : sourceDataList)
        {
            ChouetteObjectState state = null;
            String start = sourceData.getStartOfLink();
            String end = sourceData.getEndOfLink();
            String key = start+"-"+end;

            PTLink targetData = targetDataMap.remove(key);
            if (targetData == null)
            {
                state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);
            }
            else
            {
                state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            }
            
            state.addAttributeState("LinkDistance", sourceData.getLinkDistance(), targetData.getLinkDistance());
            
            master.addObjectState(state);
            status = status && state.isIdentical();
        }
        
        for (PTLink targetData : targetDataMap.values().toArray(new PTLink[0]))
        {
            ChouetteObjectState state = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
            master.addObjectState(state);
        }
        return status;   
    }

}
