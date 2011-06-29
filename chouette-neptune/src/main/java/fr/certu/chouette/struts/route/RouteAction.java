package fr.certu.chouette.struts.route;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.struts.GeneriqueAction;

public class RouteAction extends GeneriqueAction implements ModelDriven<Itineraire>, Preparable
{

  private static final Log log = LogFactory.getLog(RouteAction.class);
  private ILigneManager lineManager;
  private Itineraire itineraryModel = new Itineraire();
  private IItineraireManager itineraryManager;
  private Long idItinerary;
  private Long lineId;
  private String mappedRequest;
  private String idRetour;
  private List<Itineraire> itinerairesSansItineraireEdite;

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

    if (getIdLigne() != null)
    {
      // TODO : Le virer grâce a OGNL
      itinerairesSansItineraireEdite = lineManager.getItinerairesLigne(getIdLigne());
      //	Suppression dans la liste des itinéraires de celui étant édité
      for (Itineraire itineraire : itinerairesSansItineraireEdite)
      {
        if (itineraire.getId().equals(itineraryModel.getId()))
        {
          itinerairesSansItineraireEdite.remove(itineraire);
          break;
        }
      }
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
    log.debug("List of route");
    return LIST;
  }

  @SkipValidation
  public String add()
  {
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
    log.debug("Create route with id : " + itineraryModel.getId());
    return REDIRECTLIST;
  }

  @SkipValidation
  public String edit()
  {
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
    log.debug("Update route with id : " + itineraryModel.getId());
    return REDIRECTLIST;
  }

  public String delete()
  {
    itineraryManager.supprimer(getModel().getId());
    addActionMessage(getText("itineraire.delete.ok"));
    log.debug("Delete route with id : " + getModel().getId());
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

  @SkipValidation
  public String search()
  {
    return SEARCH;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setItineraireManager(IItineraireManager itineraryManager)
  {
    this.itineraryManager = itineraryManager;
  }

  public void setLigneManager(ILigneManager lineManager)
  {
    this.lineManager = lineManager;
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

  public List<Itineraire> getItinerairesSansItineraireEdite()
  {
    return itinerairesSansItineraireEdite;
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
      return getText("route.direction.aller");
    }
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

  public String getLineName()
  {
    log.debug("lineId : " + lineId);
    if(lineId != null)
      return lineManager.lire(lineId).getName();
    else
      return "";
  }
}
