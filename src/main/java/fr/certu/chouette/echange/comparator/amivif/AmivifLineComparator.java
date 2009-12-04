/**
 * 
 */
package fr.certu.chouette.echange.comparator.amivif;

import java.util.Arrays;
import java.util.Map;

import amivif.schema.Line;
import amivif.schema.RespPTLineStructTimetable;
import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.echange.comparator.ComparatorException;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author michel
 *
 */
public class AmivifLineComparator extends AbstractAmivifDataComparator
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

      // items to check : line ends and route list
      Line sourceData = source.getLine();
      Line targetData = target.getLine();

      ChouetteObjectState state = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), targetData.getObjectId());

      String[] sourceEnds = sourceData.getLineEnd();
      String[] targetEnds = convertToSourceId(targetData.getLineEnd());

      if (targetData.getLineEnd() == null)
      {
         throw new ComparatorException(ComparatorException.TYPE.UnbuildResource, "Amivif target line end for : " + targetData.getObjectId());        	
      }

      // if (!state.addAttributeState("LineEnd", Arrays.toString(sourceEnds), Arrays.toString(targetEnds)))
      {
         Arrays.sort(sourceEnds);
         Arrays.sort(targetEnds);
         state.addAttributeState("unordered LineEnd (order may differs)", Arrays.toString(sourceEnds), Arrays.toString(targetEnds));
      }

      String[] sourceRoutes = sourceData.getRouteId();
      String[] targetRoutes = convertToSourceId(targetData.getRouteId());
      // if (!state.addAttributeState("RouteId", Arrays.toString(sourceRoutes),Arrays.toString(targetRoutes)))
      {
         Arrays.sort(sourceRoutes);
         Arrays.sort(targetRoutes);
         state.addAttributeState("unordered RouteId (order may differs)", Arrays.toString(sourceRoutes), Arrays.toString(targetRoutes));
      }

      master.addObjectState(state);
      return state.isIdentical();
   }

   @Override
   public Map<String, ChouetteObjectState> getStateMap()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
