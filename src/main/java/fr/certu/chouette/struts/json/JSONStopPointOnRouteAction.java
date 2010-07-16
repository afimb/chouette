package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONStopPointOnRouteAction extends GeneriqueAction
{

  private IPositionGeographiqueManager positionGeographiqueManager;
  private String stopPointName;
  List<PositionGeographique> stopPoints;

  public String getStopPointName()
  {
    return stopPointName;
  }

  public void setStopPointName(String stopPointName)
  {
    this.stopPointName = stopPointName;
  }

  public Map getStopPoints()
  {
    List<PositionGeographique> stopPointsOnRoute = positionGeographiqueManager.lireArretsPhysiques();

    Map result = new HashMap();
    for (PositionGeographique positionGeographique : stopPointsOnRoute)
    {
      String name = positionGeographique.getName();
      if(name.contains(stopPointName))
      result.put(positionGeographique.getId(), positionGeographique.getName());
    }

    return result;
  }

  public void setStopPoints(List<PositionGeographique> stopPoints)
  {
    this.stopPoints = stopPoints;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }
}
