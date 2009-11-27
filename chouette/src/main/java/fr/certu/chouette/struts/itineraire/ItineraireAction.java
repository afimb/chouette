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
import java.util.HashMap;

public class ItineraireAction extends GeneriqueAction implements ModelDriven<Itineraire>, Preparable
{

  private static final Log log = LogFactory.getLog(ItineraireAction.class);
  private static ILigneManager lineManager;
  private Itineraire itineraryModel = new Itineraire();
  private static IItineraireManager itineraryManager;
  private Long idItinerary;
  private Long lineId;
  private String mappedRequest;
  private String sensAller = "A";
  private String sensRetour = "R";
  private String idRetour;

  public Long getIdItineraire()
  {
    return idItinerary;
  }

  public void setIdItineraire(Long idItinerary)
  {
    this.idItinerary = idItinerary;
  }

  public Long getIdLigne()
  {
    return lineId;
  }

  public void setIdLigne(Long lineId)
  {
    this.lineId = lineId;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  @Override
  public Itineraire getModel()
  {
    return itineraryModel;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdItineraire());
    if (getIdItineraire() == null)
    {
      itineraryModel = new Itineraire();
    }
    else
    {
      itineraryModel = itineraryManager.lire(getIdItineraire());
    }
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    //	Récupération des itinéraires pour un identifiant de ligne donnée
    List<Itineraire> itinerairesLigne = lineManager.getItinerairesLigne(getIdLigne());
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

    this.request.put("itineraires", itineraires);
    log.debug("List of itineraries");
    return LIST;
  }

  @SkipValidation
  public String add()
  {
    // TODO : Le virer grâce a OGNL
    List<Itineraire> itinerairesSansItineraireEdite = lineManager.getItinerairesLigne(itineraryModel.getIdLigne());

    //	Suppression dans la liste des itinéraires de celui étant édité
    for (Itineraire itineraire : itinerairesSansItineraireEdite)
    {
      if (itineraire.getId().equals(itineraryModel.getId()))
      {
        itinerairesSansItineraireEdite.remove(itineraire);
        break;
      }
    }

    this.request.put("itinerairesSansItineraireEdite", itinerairesSansItineraireEdite);
    setMappedRequest(SAVE);
    return EDIT;
  }

  public String save()
  {
    itineraryManager.creer(itineraryModel);

    if (itineraryModel.getIdRetour() != null)
    {
      //	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
      if (itineraryModel.getIdRetour().equals("-1"))
      {
        itineraryManager.dissocierItineraire(itineraryModel.getId());
      }
      else
      {
        itineraryManager.dissocierItineraire(itineraryModel.getId());
        itineraryManager.associerItineraire(itineraryModel.getId(), new Long(itineraryModel.getIdRetour()));
      }
    }

    setMappedRequest(SAVE);
    addActionMessage(getText("itineraire.create.ok"));
    log.debug("Create itinerary with id : " + itineraryModel.getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String edit()
  {
    // TODO : Le virer grâce a OGNL
    List<Itineraire> itinerairesSansItineraireEdite = lineManager.getItinerairesLigne(itineraryModel.getIdLigne());

    //	Suppression dans la liste des itinéraires de celui étant édité
    for (Itineraire itineraire : itinerairesSansItineraireEdite)
    {
      if (itineraire.getId().equals(itineraryModel.getId()))
      {
        itinerairesSansItineraireEdite.remove(itineraire);
        break;
      }
    }

    this.request.put("itinerairesSansItineraireEdite", itinerairesSansItineraireEdite);
    setMappedRequest(UPDATE);
    return EDIT;
  }

  public String update()
  {
    itineraryManager.modifier(itineraryModel);

    if (itineraryModel.getIdRetour() != null)
    {
      //	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
      if (itineraryModel.getIdRetour().equals("-1"))
      {
        itineraryManager.dissocierItineraire(itineraryModel.getId());
      }
      else
      {
        itineraryManager.dissocierItineraire(itineraryModel.getId());
        itineraryManager.associerItineraire(itineraryModel.getId(), new Long(itineraryModel.getIdRetour()));
      }
    }

    setMappedRequest(UPDATE);
    addActionMessage(getText("itineraire.update.ok"));
    log.debug("Update itinerary with id : " + itineraryModel.getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    itineraryManager.supprimer(getModel().getId());
    addActionMessage(getText("itineraire.delete.ok"));
    log.debug("Delete itinerary with id : " + getModel().getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("itineraire.cancel.ok"));
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setItineraireManager(IItineraireManager itineraryManager)
  {
    ItineraireAction.itineraryManager = itineraryManager;
  }

  public void setLigneManager(ILigneManager lineManager)
  {
    ItineraireAction.lineManager = lineManager;
  }

  /********************************************************
   *                   METHODE ACTION                     *
   ********************************************************/
  // this prepares command for button on initial screen write
  public void setMappedRequest(String actionMethod)
  {
    this.mappedRequest = actionMethod;
  }

  // when invalid, the request parameter will restore command action
  public void setActionMethod(String method)
  {
    this.mappedRequest = method;
  }

  public String getActionMethod()
  {
    return mappedRequest;
  }

  public String creerItineraireRetour()
  {
    itineraryManager.creerItineraireRetour(itineraryModel.getId());
    addActionMessage(getText("itineraire.retour.ok"));
    return SUCCESS;
  }

  public boolean isArretsVide(Long idItineraire)
  {
    return itineraryManager.getArretsItineraire(idItineraire).isEmpty();
  }

  public String getSensItineraire()
  {
    if (itineraryModel != null && itineraryModel.getWayBack() != null)
    {
      return itineraryModel.getWayBack().toString();
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

  public String getIdRetour()
  {
    //log.debug("this.itineraire : " + this.itineraire);
    if (this.itineraryModel.getIdRetour() == null)
    {
      return "-1";
    }
    else
    {
      return this.itineraryModel.getIdRetour().toString();
    }
  }

  public void setIdRetour(String idRetour)
  {
    this.idRetour = idRetour;
  }
}
