package fr.certu.chouette.ihm.transporteur;

import fr.certu.chouette.modele.Transporteur;
import java.util.List;

public class ListTransporteurModel
{

  private List<Transporteur> transporteurs;

  public List<Transporteur> getTransporteurs()
  {
    return transporteurs;
  }

  public void setTransporteurs(List<Transporteur> transporteurs)
  {
    this.transporteurs = transporteurs;
  }
}
