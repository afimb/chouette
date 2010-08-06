package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.HashMap;
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
    return positionGeographiqueManager.getGeoPositionsDirectementContenues(stopPlaceId);
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
