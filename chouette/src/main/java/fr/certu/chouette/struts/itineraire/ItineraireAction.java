package fr.certu.chouette.struts.itineraire;

import com.opensymphony.xwork2.ModelDriven;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;

public class ItineraireAction extends GeneriqueAction implements ModelDriven, Preparable
{

  private static final Log log = LogFactory.getLog(ItineraireAction.class);
  //	Manager
  private static IItineraireManager itineraireManager;
  private static ILigneManager ligneManager;
  private RouteModel itineraireModel = new RouteModel();

  public String creerItineraireRetour()
  {
    itineraireManager.creerItineraireRetour(itineraireModel.getIdItineraire());
    addActionMessage(getText("itineraire.retour.ok"));
    return SUCCESS;
  }

  public String cancel()
  {
    addActionMessage(getText("itineraire.cancel.ok"));
    return SUCCESS;
  }

  public String delete()
  {
    itineraireManager.supprimer(itineraireModel.getIdItineraire());
    addActionMessage(getText("itineraire.delete.ok"));
    return SUCCESS;
  }

  @SkipValidation
  public String edit()
  {
    List<Itineraire> itinerairesSansItineraireEdite = ligneManager.getItinerairesLigne(itineraireModel.getIdLigne());

    //	Suppression dans la liste des itinéraires de celui étant édité
    for (Itineraire itineraire : itinerairesSansItineraireEdite)
    {
      if (itineraire.getId().equals(itineraireModel.getIdItineraire()))
      {
        itinerairesSansItineraireEdite.remove(itineraire);
        break;
      }
    }

    itineraireModel.setItinerairesSansItineraireEdite(itinerairesSansItineraireEdite);

    return INPUT;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  public boolean isArretsVide(Long idItineraire)
  {
    return itineraireManager.getArretsItineraire(idItineraire).isEmpty();
  }

  /**
   * Renvoit la liste des itinéraires classés avec au début les itinéraires par couple aller
   * et retour et ensuite les itinéraires sans retour
   * @return liste des itinéraires classés
   */
  @SkipValidation
  public String list()
  {
    //	Récupération des itinéraires pour un identifiant de ligne donnée
    List<Itineraire> itinerairesLigne = ligneManager.getItinerairesLigne(itineraireModel.getIdLigne());
    //	Liste des itinéraires classés
    List<Itineraire> itineraires = new ArrayList<Itineraire>();
    //	Liste de tous les ids de retour de la liste des itinéraires initiale
    List<Long> idsRetour = new ArrayList<Long>();


    Map<Long, Itineraire> itineraireParIdItineraire = new Hashtable<Long, Itineraire>();

    for (Itineraire itineraire : itinerairesLigne)
    {
      itineraireParIdItineraire.put(itineraire.getId(), itineraire);
    }


    for (Itineraire itineraire : itinerairesLigne)
    {
      if (itineraire.getIdRetour() != null)
      {
        // Si l'idRetour de l'itinéraire est déjà compris dans la liste des idsRetour on continue la boucle
        if (idsRetour.contains(itineraire.getId()))
        {
          continue;
        }
        //	Ajout de l'idRetour a la liste des idsRetour pour ne pas les prendre en compte car ajout dans les itineraires classes de l'itinéraire aller et retour
        idsRetour.add(itineraire.getIdRetour());

        //	Ajout de l'itinéraire aller et retour en début de liste
        if (itineraire.getWayBack().equals("A"))
        {
          //	Si l'itinéraire retour se trouve dans la liste on l'ajoute
          if (itineraireParIdItineraire.get(itineraire.getIdRetour()) != null)
          {
            itineraires.add(0, itineraireParIdItineraire.get(itineraire.getIdRetour()));
          }
          itineraires.add(0, itineraire);
        }
        else
        {
          itineraires.add(0, itineraire);
          //	Si l'itinéraire retour se trouve dans la liste on l'ajoute
          if (itineraireParIdItineraire.get(itineraire.getIdRetour()) != null)
          {
            itineraires.add(0, itineraireParIdItineraire.get(itineraire.getIdRetour()));
          }
        }
      }
      else
      {
        //	Ajout d'un itineraire sans idRetour à la fin de la liste
        itineraires.add(itineraire);
      }
    }

    itineraireModel.setItineraires(itineraires);

    return SUCCESS;
  }

  @SkipValidation
  public void prepare() throws Exception
  {
    if (itineraireModel.getIdItineraire() != null)
    {
      itineraireModel.setItineraire(itineraireManager.lire(itineraireModel.getIdItineraire()));
    }
  }

  public void setItineraireManager(IItineraireManager itineraireManager)
  {
    this.itineraireManager = itineraireManager;
  }

  public void setLigneManager(ILigneManager lineManager)
  {
    this.ligneManager = lineManager;
  }

  public String update()
  {
    log.debug("update : " + itineraireModel.getItineraire());
    Itineraire itineraire = itineraireModel.getItineraire();
    if (itineraire == null)
    {
      return INPUT;
    }

    // ré-affecter l'identifiant de la ligne sur l'itinéraire
    //itineraire.setIdLigne(idLigne);

    if (itineraire.getId() == null)
    {
      itineraireManager.creer(itineraire);
      if (itineraireModel.getIdRetour() != null)
      {
        //	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
        if (itineraireModel.getIdRetour().equals("-1"))
        {
          itineraireManager.dissocierItineraire(itineraire.getId());
        }
        else
        {
          itineraireManager.dissocierItineraire(itineraire.getId());
          itineraireManager.associerItineraire(itineraire.getId(), new Long(itineraireModel.getIdRetour()));
        }
      }
      addActionMessage(getText("itineraire.create.ok"));
    }
    else
    {
      itineraireManager.modifier(itineraire);
      if (itineraireModel.getIdRetour() != null)
      {
        //	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
        if (itineraireModel.getIdRetour().equals("-1"))
        {
          itineraireManager.dissocierItineraire(itineraire.getId());
        }
        else
        {
          itineraireManager.dissocierItineraire(itineraire.getId());
          itineraireManager.associerItineraire(itineraire.getId(), new Long(itineraireModel.getIdRetour()));
        }
      }
      addActionMessage(getText("itineraire.update.ok"));
    }

    return SUCCESS;
  }

  public Object getModel()
  {
    return itineraireModel;
  }
}
