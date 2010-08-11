package fr.certu.chouette.struts.json;

import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.HashSet;
import java.util.Set;

public class JSONConnectionLinkAction extends GeneriqueAction
{

  private Long connectionLinkId;
  private ICorrespondanceManager connectionLinkManager;
  private IPositionGeographiqueManager positionGeographiqueManager;

  public Long getConnectionLinkId()
  {
    return connectionLinkId;
  }

  public void setConnectionLinkId(Long connectionLinkId)
  {
    this.connectionLinkId = connectionLinkId;
  }

  public Set<PositionGeographique> getStopPlaces()
  {
    Correspondance connectionLink = connectionLinkManager.lire(connectionLinkId);
    Set<PositionGeographique> stopPlaces = new HashSet<PositionGeographique>();
    if (connectionLink.getIdDepart() != null)
    {
      stopPlaces.add(positionGeographiqueManager.lire(connectionLink.getIdDepart()));
    }
    if (connectionLink.getIdArrivee() != null)
    {
      stopPlaces.add(positionGeographiqueManager.lire(connectionLink.getIdArrivee()));
    }
    return stopPlaces;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setCorrespondanceManager(ICorrespondanceManager connectionLinkManager)
  {
    this.connectionLinkManager = connectionLinkManager;
  }

  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }
}
