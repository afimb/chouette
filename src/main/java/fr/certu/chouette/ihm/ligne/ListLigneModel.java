package fr.certu.chouette.ihm.ligne;

import fr.certu.chouette.modele.Ligne;
import java.util.List;

public class ListLigneModel extends SharedLigneModel
{

  private Long idReseau;
  private Long idTransporteur;
  private String nomLigne;
  private List<Ligne> lignes;

  public Long getIdReseau()
  {
    return idReseau;
  }

  public void setIdReseau(Long idReseau)
  {
    this.idReseau = idReseau;
  }

  public Long getIdTransporteur()
  {
    return idTransporteur;
  }

  public void setIdTransporteur(Long idTransporteur)
  {
    this.idTransporteur = idTransporteur;
  }

  public String getNomLigne()
  {
    return nomLigne;
  }

  public void setNomLigne(String nomLigne)
  {
    this.nomLigne = nomLigne;
  }

  public List<Ligne> getLignes()
  {
    return lignes;
  }

  public void setLignes(List<Ligne> lignes)
  {
    this.lignes = lignes;
  }
}
