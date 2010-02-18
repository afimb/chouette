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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author michel
 *
 */
public class AmivifPtLinkComparator extends AbstractAmivifDataComparator
{
   private static final Log logger = LogFactory.getLog(AmivifPtLinkComparator.class);

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
      Integer duplicates = 1;

      // key = source startID+sourceEndId 
      for (PTLink targetData : targetDataList)
      {
         String start = master.getSourceId(targetData.getStartOfLink());
         String end =  master.getSourceId(targetData.getEndOfLink());
         String key = start+"-"+end;
         if (targetDataMap.containsKey(key))
         {
            Integer i = 1;
            String dupKey = key;
            while (targetDataMap.containsKey(dupKey))
            {
               dupKey = key + "-" + i.toString();
               i ++;
            }
            if (i > duplicates) duplicates = i;
            key = dupKey;
         }
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
         Integer i = 1;
         String searchKey = key;
         while ((targetData == null) && (i != duplicates))
         {
            searchKey = key + "-" + i.toString();
            targetData = (PTLink)targetDataMap.remove(searchKey);
            i = Integer.valueOf(i.intValue() + 1);
         }
         if (targetData == null)
         {
            logger.debug("target not found for " + sourceData.getObjectId() + " : key = " + key);
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
         String start = master.getSourceId(targetData.getStartOfLink());
         String end = master.getSourceId(targetData.getEndOfLink());
         String key = start + "-" + end;
         logger.debug("source not found for " + targetData.getObjectId() + " : key = " + key);
         ChouetteObjectState state = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
         master.addObjectState(state);
      }
      return status;   
   }

}
