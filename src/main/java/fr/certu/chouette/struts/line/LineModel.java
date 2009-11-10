package fr.certu.chouette.struts.line;

import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import java.util.List;
import java.util.Map;

public class LineModel
{

  private Long networkId;
  private Long companyId;
  private String lineName;
  private List<Ligne> lines;
  private List<Reseau> networks;
  private Map<Long, Reseau> networkById;
  private List<Transporteur> companies;
  private Map<Long, Transporteur> companyById;
  private Ligne line;
  private Long lineId;

  public Ligne getLigne()
  {
    return line;
  }

  public void setLigne(Ligne ligne)
  {
    this.line = ligne;
  }

  public Long getIdLigne()
  {
    return lineId;
  }

  public void setIdLigne(Long idLigne)
  {
    this.lineId = idLigne;
  }

  public List<Reseau> getReseaux()
  {
    return networks;
  }

  public void setReseaux(List<Reseau> reseaux)
  {
    this.networks = reseaux;
  }

  public Map<Long, Reseau> getReseauParId()
  {
    return networkById;
  }

  public void setReseauParId(Map<Long, Reseau> reseauParId)
  {
    this.networkById = reseauParId;
  }

  public List<Transporteur> getTransporteurs()
  {
    return companies;
  }

  public void setTransporteurs(List<Transporteur> transporteurs)
  {
    this.companies = transporteurs;
  }

  public Map<Long, Transporteur> getTransporteurParId()
  {
    return companyById;
  }

  public void setTransporteurParId(Map<Long, Transporteur> transporteurParId)
  {
    this.companyById = transporteurParId;
  }

  public Long getIdReseau()
  {
    return networkId;
  }

  public void setIdReseau(Long idReseau)
  {
    this.networkId = idReseau;
  }

  public Long getIdTransporteur()
  {
    return companyId;
  }

  public void setIdTransporteur(Long idTransporteur)
  {
    this.companyId = idTransporteur;
  }

  public String getNomLigne()
  {
    return lineName;
  }

  public void setNomLigne(String nomLigne)
  {
    this.lineName = nomLigne;
  }

  public List<Ligne> getLignes()
  {
    return lines;
  }

  public void setLignes(List<Ligne> lignes)
  {
    this.lines = lignes;
  }

}
