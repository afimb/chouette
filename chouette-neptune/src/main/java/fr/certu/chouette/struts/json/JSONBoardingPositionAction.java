package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONBoardingPositionAction extends GeneriqueAction
{

  private IPositionGeographiqueManager positionGeographiqueManager;
  private String boardingPositionName;

  public String getBoardingPositionName()
  {
    return boardingPositionName;
  }

  public void setBoardingPositionName(String boardingPositionName)
  {
    this.boardingPositionName = boardingPositionName;
  }

  public Map getBoardingPositions()
  {
    List<PositionGeographique> boardingPositionsOnRoute = positionGeographiqueManager.lireArretsPhysiques();

    Map result = new HashMap();
    for (PositionGeographique positionGeographique : boardingPositionsOnRoute)
    {
      String name = positionGeographique.getName();
      if(name.contains(boardingPositionName))
      result.put(positionGeographique.getId(), positionGeographique.getName());
    }

    return result;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }
}
