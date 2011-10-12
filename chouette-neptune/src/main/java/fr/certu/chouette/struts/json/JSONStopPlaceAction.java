package fr.certu.chouette.struts.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.json.data.JSONStopArea;

public class JSONStopPlaceAction extends GeneriqueAction
{
   private static final long serialVersionUID = -1071494830963928442L;
   private static final Logger logger = Logger.getLogger(JSONStopPlaceAction.class);
   
   @Setter private INeptuneManager<StopArea> stopAreaManager;
   @Getter @Setter private Long stopPlaceId;
   List<JSONStopArea> stopAreaChildrens;


   public List<JSONStopArea> getStopAreaChildrens()
   {
      try
      {
         if (stopPlaceId != null)
         {
            StopArea parent = stopAreaManager.getById(stopPlaceId);
            List<StopArea> stopPlaces = parent.getContainedStopAreas();
            List<JSONStopArea> stopPlacesWithCoordinates = new ArrayList<JSONStopArea>();

            for (StopArea positionGeographique : stopPlaces)
            {
               AreaCentroid centroid = positionGeographique.getAreaCentroid();
               if (centroid != null)
               {

                  if ((centroid.getLongitude() != null && centroid.getLatitude() != null)
                        || (centroid.getProjectedPoint() != null && centroid.getProjectedPoint().getX() != null && centroid.getProjectedPoint().getY() != null))
                  {
                     logger.debug(positionGeographique.toString());
                     stopPlacesWithCoordinates.add(new JSONStopArea(positionGeographique));
                  }
               }
            }

            return stopPlacesWithCoordinates;
         }
      }
      catch (ChouetteException e)
      {
         logger.warn("failure "+e.getLocalizedMessage(),e);
         addActionError(e.getLocalizedMessage());
      }
      catch (Exception e)
      {
         logger.warn("failure "+e.getMessage(),e);
         addActionError(e.getMessage());
      }

      return null;
   }

   public void setStopAreaChildrens(List<JSONStopArea> stopAreaChildrens)
   {
      this.stopAreaChildrens = stopAreaChildrens;
   }

}
