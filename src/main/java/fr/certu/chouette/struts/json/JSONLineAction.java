package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.stopArea.StopAreaAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSONLineAction extends GeneriqueAction
{
  private static final Log log = LogFactory.getLog(JSONLineAction.class);
  private ILigneManager ligneManager;
  private Long lineId;
  private IPositionGeographiqueManager positionGeographiqueManager;

  public Long getLineId()
  {
    return lineId;
  }

  public void setLineId(Long lineId)
  {
    this.lineId = lineId;
  }

  public Set<PositionGeographique> getStopPlaces()
  {
	log.error("getStopPlaces");

    List<PositionGeographique> boardingPositionsOnRoute = ligneManager.getArretsPhysiques(lineId);
    Set<PositionGeographique> stopPlaces = new HashSet<PositionGeographique>();

    for (PositionGeographique positionGeographique : boardingPositionsOnRoute)
    {
      if(positionGeographique.idParent != null){
        PositionGeographique stopPlace = positionGeographiqueManager.lire(positionGeographique.idParent);
        if (stopPlace != null)
        {
          stopPlaces.add(stopPlace);
        }
      }
    }
    return stopPlaces;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }

  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }
}
