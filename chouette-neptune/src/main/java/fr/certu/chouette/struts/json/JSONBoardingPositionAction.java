package fr.certu.chouette.struts.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;

public class JSONBoardingPositionAction extends GeneriqueAction
{

   private static final long            serialVersionUID = -1975801282206724821L;
   @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Getter @Setter private String                       boardingPositionName;
   private User user = null;


   @SuppressWarnings("rawtypes")
   public Map getBoardingPositions()
   {
      try
      {
         List<StopArea> boardingPositionsOnRoute;
         Filter filter = Filter.getNewAndFilter(StopArea.physicalStopsFilter,
               Filter.getNewIgnoreCaseLikeFilter(StopArea.NAME, boardingPositionName));
         boardingPositionsOnRoute = stopAreaManager.getAll(user ,filter);

         Map<Long,String> result = new HashMap<Long,String>();
         for (StopArea positionGeographique : boardingPositionsOnRoute)
         {
            // String name = positionGeographique.getName();
            //  if (name.contains(boardingPositionName))
            result.put(positionGeographique.getId(), positionGeographique.getName());
         }

         return result;
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
         return null;
      }
   }

}
