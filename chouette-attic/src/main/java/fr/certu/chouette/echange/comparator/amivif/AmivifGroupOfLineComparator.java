/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import amivif.schema.GroupOfLine;
import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifGroupOfLineComparator extends AbstractAmivifDataComparator
{
   private static final Log logger = LogFactory.getLog(AmivifGroupOfLineComparator.class);

   /* (non-Javadoc)
    * @see fr.certu.chouette.echange.comparator.amivif.IAmivifDataComparator#compareData(fr.certu.chouette.echange.comparator.ExchangeableAmivifLineComparator)
    */

   public boolean compareData(ExchangeableAmivifLineComparator master) throws ServiceException
   {
      this.master = master;

      RespPTLineStructTimetable source = master.getAmivifSource();
      RespPTLineStructTimetable target = master.getAmivifTarget();

      GroupOfLine[] sourceDataList = source.getGroupOfLine();
      GroupOfLine[] targetDataList = target.getGroupOfLine();

      Map<String,GroupOfLine> targetDataMap = new HashMap<String, GroupOfLine>();

      // key = name
      for (GroupOfLine targetData : targetDataList)
      {
         targetDataMap.put(targetData.getName(), targetData);
      }

      boolean status = true;

      for (GroupOfLine sourceData : sourceDataList)
      {
         ChouetteObjectState state = null;

         GroupOfLine targetData = targetDataMap.remove(sourceData.getName());
         if (targetData == null)
         {
            logger.debug("target not found for " + sourceData.getObjectId() + " : key = " + sourceData.getName());
            state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),null);
         }
         else
         {
            state = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());
         }
         master.addObjectState(state);
         status = status && state.isIdentical();
      }

      for (GroupOfLine targetData : targetDataMap.values().toArray(new GroupOfLine[0]))
      {
         logger.debug("source not found for " + targetData.getObjectId() + " : key = " + targetData.getName());
         ChouetteObjectState state = new ChouetteObjectState(getMappingKey(),null,targetData.getObjectId());
         master.addObjectState(state);
      }
      return status;   
   }

}
