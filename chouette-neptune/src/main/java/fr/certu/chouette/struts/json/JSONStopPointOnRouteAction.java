package fr.certu.chouette.struts.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;

public class JSONStopPointOnRouteAction extends GeneriqueAction
{
   private static final long serialVersionUID = 1186464748780172832L;
   @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Getter @Setter private String stopPointName;
   @Setter List<StopArea> stopPoints; // TODO a supprimer ?
   private User user = null;


   @SuppressWarnings("rawtypes")
   public Map getStopPoints()
   {

      try
      {
         List<StopArea> stopPointsOnRoute = stopAreaManager.getAll(user , StopArea.physicalStopsFilter);


         Map<Long,String> result = new HashMap<Long,String>();
         for (StopArea positionGeographique : stopPointsOnRoute)
         {
            String name = positionGeographique.getName();
            if(name.contains(stopPointName))
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
