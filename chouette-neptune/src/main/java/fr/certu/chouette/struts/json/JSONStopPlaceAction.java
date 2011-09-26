package fr.certu.chouette.struts.json;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;

public class JSONStopPlaceAction extends GeneriqueAction
{
   private static final long serialVersionUID = -1071494830963928442L;
   @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Getter @Setter private Long stopPlaceId;
   List<StopArea> stopAreaChildrens;
   private User user = null;


   public List<StopArea> getStopAreaChildrens()
   {
      try
      {
         if (stopPlaceId != null)
         {
            Filter filter = Filter.getNewEqualsFilter(StopArea.PARENTSTOPAREA+"."+StopArea.ID, stopPlaceId);
            List<StopArea> stopPlaces;
            stopPlaces = stopAreaManager.getAll(user ,filter);
            List<StopArea> stopPlacesWithCoordinates = new ArrayList<StopArea>();

            for (StopArea positionGeographique : stopPlaces)
            {
               AreaCentroid centroid = positionGeographique.getAreaCentroid();
               if (centroid != null)
               {

                  if ((centroid.getLongitude() != null && centroid.getLatitude() != null)
                        || (centroid.getProjectedPoint() != null && centroid.getProjectedPoint().getX() != null && centroid.getProjectedPoint().getY() != null))
                  {
                     stopPlacesWithCoordinates.add(positionGeographique);
                  }
               }
            }

            return stopPlacesWithCoordinates;
         }
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
      }

      return null;
   }

   public void setStopAreaChildrens(List<StopArea> stopAreaChildrens)
   {
      this.stopAreaChildrens = stopAreaChildrens;
   }

}
