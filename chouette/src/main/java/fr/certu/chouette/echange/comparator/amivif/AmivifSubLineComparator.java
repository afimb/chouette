/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.SubLine;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author Michel ETIENNE
 *
 */
public class AmivifSubLineComparator extends AbstractAmivifDataComparator
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

        SubLine[] sourceDataList = source.getSubLine();
        SubLine[] targetDataList = target.getSubLine();

        Map<String,SubLine> targetDataMap = new HashMap<String, SubLine>();

        // key = registration number
        for (SubLine targetData : targetDataList)
        {
            targetDataMap.put(targetData.getRegistration().getRegistrationNumber(), targetData);
        }

        boolean status = true;

        for (SubLine sourceData : sourceDataList)
        {
            ChouetteObjectState state = null;

            SubLine targetData = targetDataMap.remove(sourceData.getRegistration().getRegistrationNumber());
            if (targetData == null)
            {
                state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);
            }
            else
            {
                state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
            }
            master.addObjectState(state);

            // check name, linename and route list
            state.addAttributeState("SublineName", sourceData.getSublineName(), targetData.getSublineName());
            state.addAttributeState("LineName", sourceData.getLineName(), targetData.getLineName());

            String[] sourceRouteList = sourceData.getRouteId();
            String[] targetRouteList = convertToSourceId(targetData.getRouteId());

            if (state.addAttributeState("Route count", sourceRouteList.length, targetRouteList.length))
            {
                Arrays.sort(sourceRouteList);
                Arrays.sort(targetRouteList);
                state.addAttributeState("Routes", Arrays.toString(sourceRouteList), Arrays.toString(targetRouteList));
            }

            status = status && state.isIdentical();
        }

        for (SubLine targetData : targetDataMap.values().toArray(new SubLine[0]))
        {
            ChouetteObjectState state = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
            master.addObjectState(state);
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
