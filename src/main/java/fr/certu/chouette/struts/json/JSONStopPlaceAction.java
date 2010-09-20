package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.ArrayList;
import java.util.List;

public class JSONStopPlaceAction extends GeneriqueAction
{

  private IPositionGeographiqueManager positionGeographiqueManager;
  private Long stopPlaceId;
  List<PositionGeographique> stopAreaChildrens;

  public Long getStopPlaceId()
  {
    return stopPlaceId;
  }

  public void setStopPlaceId(Long stopPlaceId)
  {
    this.stopPlaceId = stopPlaceId;
  }

  public List<PositionGeographique> getStopAreaChildrens()
  {
    if (stopPlaceId != null)
    {
      List<PositionGeographique> stopPlaces = positionGeographiqueManager.getGeoPositionsDirectementContenues(stopPlaceId);
      List<PositionGeographique> stopPlacesWithCoordinates = new ArrayList<PositionGeographique>();

      for (PositionGeographique positionGeographique : stopPlaces)
      {
        if ((positionGeographique.getLongitude() != null && positionGeographique.getLatitude() != null)
                || (positionGeographique.getX() != null && positionGeographique.getY() != null))
        {
          stopPlacesWithCoordinates.add(positionGeographique);
        }
      }

      return stopPlacesWithCoordinates;
    } else
    {
      return null;

    }
  }

  public void setStopAreaChildrens(List<PositionGeographique> stopAreaChildrens)
  {
    this.stopAreaChildrens = stopAreaChildrens;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }
}
