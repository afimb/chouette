package fr.certu.chouette.ihm.reseau;

import fr.certu.chouette.modele.Reseau;
import java.util.List;

public class ListReseauModel extends SharedReseauModel
{

  private List<Reseau> reseaux;

  public List<Reseau> getReseaux()
  {
    return reseaux;
  }

  public void setReseaux(List<Reseau> reseaux)
  {
    this.reseaux = reseaux;
  }
}
