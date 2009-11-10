package fr.certu.chouette.struts.itineraire;

import altibus.schema.Ligne;
import fr.certu.chouette.modele.Itineraire;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteModel
{

  private Long idItineraire;
  private Long idLigne;
  private String idRetour;
  private List<Itineraire> itineraires;
  private Itineraire itineraire;
  private Ligne ligne;
  private String sensAller = "A";
  private String sensRetour = "R";
  private List<Itineraire> itinerairesSansItineraireEdite;

  public Long getIdLigne()
  {
    return idLigne;
  }

  public void setIdLigne(Long idLigne)
  {
    this.idLigne = idLigne;
  }

  public String getIdRetour()
  {
    //log.debug("this.itineraire : " + this.itineraire);
    if (this.itineraire.getIdRetour() == null)
    {
      return "-1";
    }
    else
    {
      return this.itineraire.getIdRetour().toString();
    }
  }

  public void setIdRetour(String idRetour)
  {
    this.idRetour = idRetour;
  }

  public Itineraire getItineraire()
  {
    return itineraire;
  }

  public void setItineraire(Itineraire itineraire)
  {
    this.itineraire = itineraire;
  }

  public List<Itineraire> getItineraires()
  {
    return itineraires;
  }

  public void setItineraires(List<Itineraire> itineraires)
  {
    this.itineraires = itineraires;
  }

  /**
   * Retourne la liste des itinéraires de la ligne en supprimant celui qui est en train d'être édité
   * @return
   */
  public List<Itineraire> getItinerairesSansItineraireEdite()
  {
    return itinerairesSansItineraireEdite;
  }

  public void setItinerairesSansItineraireEdite(List<Itineraire> itinerairesSansItineraireEdite)
  {
    this.itinerairesSansItineraireEdite = itinerairesSansItineraireEdite;
  }

  public void setLigne(Ligne ligne)
  {
    this.ligne = ligne;
  }

  public Ligne getLigne()
  {
    return ligne;
  }

  public String getSensItineraire()
  {
    if (itineraire != null && itineraire.getWayBack() != null)
    {
      return itineraire.getWayBack().toString();
    }
    else
    {
      return sensAller;
    }
  }

  public Map<String, String> getSensItineraires()
  {
    Map<String, String> sens = new HashMap<String, String>();
    sens.put(sensRetour, sensRetour);
    sens.put(sensAller, sensAller);
    return sens;
  }

  public void setIdItineraire(Long idItineraire)
  {
    this.idItineraire = idItineraire;
  }

  public Long getIdItineraire()
  {
    return idItineraire;
  }
}
