package fr.certu.chouette.struts.stopArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import chouette.schema.types.ChouetteAreaType;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.struts.enumeration.EnumerationApplication;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.util.HashMap;
import org.springframework.context.ApplicationContext;

public class StopAreaAction extends GeneriqueAction implements ModelDriven<PositionGeographique>, Preparable
{

  private static final Log log = LogFactory.getLog(StopAreaAction.class);
  private IReseauManager reseauManager;
  private IPositionGeographiqueManager positionGeographiqueManager;
  private ILigneManager ligneManager;
  private PositionGeographique model = new PositionGeographique();
  private String mappedRequest;
  private Long idPositionGeographique;
  //	Gestion des zones
  private PositionGeographique searchCriteria;
  private List<PositionGeographique> children;
  private PositionGeographique father;
  private Long idChild;
  private Long idFather;
  private String authorizedType;
  private List<Itineraire> itineraires;
  private Map<Long, Ligne> ligneParIdItineraire;
  private Map<Long, Reseau> reseauParIdLigne;
  private Map<Long, Boolean> presenceItineraireParIdPhysique;
  //	Paramètre permettant la gestion des redirections pour les méthodes modifier ArretPhysique, redirection vers :
  //	- liste des horaires de passage
  //	- ou liste des arrêts physiques
  private String actionSuivante;
  // Numéro de la page actuelle pour la navigation parmi les différentes
  // courses
  private Integer page;
  //	Chaine de caractere implémenté pour complété les retours des actions fait par struts
  private String nomArret = null;
  private String codeInsee = null;
  private Long idReseau = null;
  private List<Reseau> reseaux;
  private Map<Long, Reseau> reseauParId;
  private Long idItineraire;
  private Long idLigne;
  //	Type de position Geographique
  private static final String ARRETPHYSIQUE = "/boardingPosition";
  private static final String ZONE = "/stopPlace";
  private Long idArretDestination = null;
  private String nomArretDestination = null;
  private Long idArretSource = null;
  private String boardingPositionName = "";

  public Long getIdItineraire()
  {
    return idItineraire;
  }

  public void setIdItineraire(Long idItineraire)
  {
    this.idItineraire = idItineraire;
  }

  public Long getIdLigne()
  {
    return idLigne;
  }

  public void setIdLigne(Long idLigne)
  {
    this.idLigne = idLigne;
  }

  public void setIdPositionGeographique(Long idPositionGeographique)
  {
    this.idPositionGeographique = idPositionGeographique;
  }

  public Long getIdPositionGeographique()
  {
    return idPositionGeographique;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  public PositionGeographique getModel()
  {
    return model;
  }

  public void prepare() throws Exception
  {
    log.debug("Prepare with id : " + getIdPositionGeographique());
    if (getIdPositionGeographique() == null)
    {
      model = new PositionGeographique();
    } else
    {
      model = positionGeographiqueManager.lire(getIdPositionGeographique());

    }

    // Chargement des réseaux
    reseaux = reseauManager.lire();
    reseauParId = new Hashtable<Long, Reseau>();
    for (Reseau reseau : reseaux)
    {
      reseauParId.put(reseau.getId(), reseau);
    }

    if (idPositionGeographique == null)
    {
      return;
    }

    //	Création des zones filles et parentes
    children = positionGeographiqueManager.getGeoPositionsDirectementContenues(idPositionGeographique);

    if (model.getIdParent() != null)
    {
      father = positionGeographiqueManager.lire(model.getIdParent());
    }

    // Création de la liste des itinéraires
    itineraires = positionGeographiqueManager.getItinerairesArretPhysique(idPositionGeographique);

    ligneParIdItineraire = new Hashtable<Long, Ligne>();
    // Création de la liste des identifiants de lignes (pas de doublons)
    Collection<Long> idsLignes = new HashSet<Long>();
    for (Itineraire itineraire : itineraires)
    {
      if (itineraire.getIdLigne() != null)
      {
        idsLignes.add(itineraire.getIdLigne());
      }
    }
    // Création de la liste des lignes à partir de la liste des identfiants de lignes
    List<Ligne> lignes = ligneManager.getLignes(idsLignes);

    // Création d'une map liant id Ligne -> Objet Ligne
    Map<Long, Ligne> ligneParId = new Hashtable<Long, Ligne>();
    for (Ligne ligne : lignes)
    {
      ligneParId.put(ligne.getId(), ligne);
    }
    // Création d'une hashtable liant id Itineraire -> Objet Ligne
    for (Itineraire itineraire : itineraires)
    {
      if (itineraire.getIdLigne() != null)
      {
        Ligne ligne = ligneParId.get(itineraire.getIdLigne());
        ligneParIdItineraire.put(itineraire.getId(), ligne);
      }
    }

    reseauParIdLigne = new Hashtable<Long, Reseau>();
    // Création d'une hashtable liant id Ligne -> Objet Reseau
    for (Ligne ligne : lignes)
    {
      if (ligne.getIdReseau() != null)
      {
        Reseau reseau = reseauParId.get(ligne.getIdReseau());
        reseauParIdLigne.put(ligne.getId(), reseau);
      }
    }
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    // Récupération du namespace pour basculer sur des arrèts physiques ou zones
    String namespace = ActionContext.getContext().getActionInvocation().getProxy().getNamespace();
    log.debug("namespace :  " + namespace);
    List<ChouetteAreaType> areaTypes = new ArrayList<ChouetteAreaType>();

    if (ARRETPHYSIQUE.equals(namespace))
    {
      areaTypes.add(ChouetteAreaType.QUAY);
      areaTypes.add(ChouetteAreaType.BOARDINGPOSITION);
    } else
    {
      areaTypes.add(ChouetteAreaType.STOPPLACE);
      areaTypes.add(ChouetteAreaType.COMMERCIALSTOPPOINT);
    }

    if (nomArret != null)
    {
      if ("".equals(nomArret.trim()))
      {
        nomArret = null;
      }
    }
    if (codeInsee != null)
    {
      if ("".equals(codeInsee.trim()))
      {
        codeInsee = null;
      }
    }

    List<PositionGeographique> positionGeographiques = positionGeographiqueManager.lire(nomArret, codeInsee, idReseau, areaTypes);
    request.put("positionGeographiques", positionGeographiques);
    log.debug("List of stopArea");
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
    positionGeographiqueManager.creer(model);
    if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
    {
      addActionMessage(getText("arretPhysique.create.ok"));
      log.debug("Create boardingPosition with id : " + model.getId());
    } else
    {
      addActionMessage(getText("zone.create.ok"));
      log.debug("Create stopPlace with id : " + model.getId());
    }
    setIdPositionGeographique(model.getId());
    setMappedRequest(UPDATE);
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String edit()
  {
    setMappedRequest(UPDATE);
    return EDIT;
  }

  public String update()
  {

    positionGeographiqueManager.modifier(model);
    if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
    {
      addActionMessage(getText("arretPhysique.update.ok"));
      log.debug("Update boardingPosition with id : " + model.getId());
    } else
    {
      addActionMessage(getText("zone.update.ok"));
      log.debug("Update stopPlace with id : " + model.getId());
    }

    setMappedRequest(UPDATE);
    return REDIRECTEDIT;
  }

  public String delete()
  {
    positionGeographiqueManager.supprimer(model.getId());
    if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
    {
      addActionMessage(getText("arretPhysique.delete.ok"));
      log.debug("Delete boardingPosition with id : " + model.getId());
    } else
    {
      addActionMessage(getText("zone.delete.ok"));
      log.debug("Delete stopPlace with id : " + model.getId());
    }

    return REDIRECTLIST;
  }

  @SkipValidation
  public String cancel()
  {
    if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
    {
      addActionMessage(getText("arretPhysique.cancel.ok"));
    } else
    {
      addActionMessage(getText("zone.cancel.ok"));
    }
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  @SkipValidation
  public String fusionnerArrets()
  {
    if (idArretSource == null || idArretDestination == null)
    {
      addActionError(getText("arretPhysique.merge.ko"));
    } else
    {
      addActionMessage(getText("arretPhysique.merge.ok"));
      positionGeographiqueManager.fusionnerPositionsGeographiques(idArretSource, idArretDestination);
    }
    return REDIRECTLIST;
  }

  @SkipValidation
  public String search()
  {
    if (idPositionGeographique != null)
    {
      if ("addChild".equals(getActionSuivante()))
      {
        switch (model.getAreaType())
        {
          case STOPPLACE:
            authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_ALL;
            break;

          case COMMERCIALSTOPPOINT:
            authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_QB;
            break;

          case BOARDINGPOSITION:
          case QUAY:
            break;
        }
      } else if ("addFather".equals(getActionSuivante()))
      {
        switch (model.getAreaType())
        {
          case STOPPLACE:
            authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
            break;

          case COMMERCIALSTOPPOINT:
            authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
            break;

          case BOARDINGPOSITION:
          case QUAY:
            authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_CS;
            break;
        }
      }
    }
    return SEARCH;
  }

  @SkipValidation
  public String searchResults()
  {
    List<PositionGeographique> positionGeographiquesResultat = new ArrayList<PositionGeographique>();

    // Clause areaType
    Collection<String> areas = new HashSet<String>();
    if (searchCriteria.getAreaType() != null)
    {
      areas.add(searchCriteria.getAreaType().toString());
    } else
    {
      if (EnumerationApplication.AUTHORIZEDTYPESET_C.equals(authorizedType))
      {
        areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
      } else if (EnumerationApplication.AUTHORIZEDTYPESET_CS.equals(authorizedType))
      {
        areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
        areas.add(ChouetteAreaType.STOPPLACE.toString());
      } else if (EnumerationApplication.AUTHORIZEDTYPESET_QB.equals(authorizedType))
      {
        areas.add(ChouetteAreaType.QUAY.toString());
        areas.add(ChouetteAreaType.BOARDINGPOSITION.toString());
      } else if (EnumerationApplication.AUTHORIZEDTYPESET_S.equals(authorizedType))
      {
        areas.add(ChouetteAreaType.STOPPLACE.toString());
      }
    }

    IClause searchClause = new AndClause(
            ScalarClause.newIlikeClause("name", searchCriteria.getName()),
            ScalarClause.newIlikeClause("countryCode", searchCriteria.getCountryCode()),
            VectorClause.newInClause("areaType", areas));

    positionGeographiquesResultat = positionGeographiqueManager.select(searchClause);

    request.put("positionGeographiquesResultat", positionGeographiquesResultat);
    return SEARCH;
  }

  @SkipValidation
  public String removeChildFromParent()
  {
    if (idChild != null)
    {
      positionGeographiqueManager.dissocierGeoPositionParente(idChild);
    }
    return REDIRECTEDIT;
  }

  @SkipValidation
  public String addChild()
  {
    if (idChild != null && idPositionGeographique != null)
    {
      positionGeographiqueManager.associerGeoPositions(idPositionGeographique, idChild);
    }

    return REDIRECTEDIT;
  }

  @SkipValidation
  public String addFather()
  {
    if (idFather != null && idPositionGeographique != null)
    {
      positionGeographiqueManager.associerGeoPositions(idFather, idPositionGeographique);
    }

    return REDIRECTEDIT;
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }

  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }

  public void setReseauManager(IReseauManager reseauManager)
  {
    this.reseauManager = reseauManager;
  }

  /********************************************************
   *                   METHOD ACTION                      *
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

  /********************************************************
   *                   LIST FILTER                        *
   ********************************************************/
  public String getCodeInsee()
  {
    return codeInsee;
  }

  public void setCodeInsee(String codeInsee)
  {
    this.codeInsee = codeInsee;
  }

  public Long getIdReseau()
  {
    return idReseau;
  }

  public void setIdReseau(Long idReseau)
  {
    this.idReseau = idReseau;
  }

  public String getNomArret()
  {
    return nomArret;
  }

  public void setNomArret(String nomArret)
  {
    this.nomArret = nomArret;
  }

  /********************************************************
   *                   SEARCH FILTER                      *
   ********************************************************/
  public void setSearchCriteria(PositionGeographique searchCriteria)
  {
    this.searchCriteria = searchCriteria;
  }

  public PositionGeographique getSearchCriteria()
  {
    return searchCriteria;
  }

  /********************************************************
   *                   TYPE                               *
   ********************************************************/
  public String getTypePositionGeographique()
  {

    if (model.getId() != null)
    {
      if (model.getAreaType() == ChouetteAreaType.QUAY
              || model.getAreaType() == ChouetteAreaType.BOARDINGPOSITION)
      {
        return ARRETPHYSIQUE;
      } else
      {
        return ZONE;
      }
    } else
    {
      // Récupération du namespace pour basculer sur des arrèts physiques ou zones
      return ActionContext.getContext().getActionInvocation().getProxy().getNamespace();
    }
  }

  public String getAuthorizedType()
  {
    return authorizedType;
  }

  public void setAuthorizedType(String authorizedType)
  {
    this.authorizedType = authorizedType;
  }

  /********************************************************
   *                   OTHER METHODS                      *
   ********************************************************/
  public void setPage(int page)
  {
    this.page = page;
  }

  public String getActionSuivante()
  {
    return actionSuivante;
  }

  public List<PositionGeographique> getChildren()
  {
    return children;
  }

  public PositionGeographique getFather()
  {
    return father;
  }

  public Long getIdChild()
  {
    return idChild;
  }

  public Long getIdFather()
  {
    return idFather;
  }

  public Long getIdZone()
  {
    return idPositionGeographique;
  }

  public List<Itineraire> getItineraires()
  {
    return itineraires;
  }

  public String getLiaisonItineraire(Long idPhysique)
  {
    return presenceItineraireParIdPhysique.get(idPhysique).booleanValue() ? "hidden" : "visible";
  }

  public Ligne getLigne(Long idItineraire)
  {
    return ligneParIdItineraire.get(idItineraire);
  }

  public Reseau getReseau(Long idLigne)
  {
    return reseauParIdLigne.get(idLigne);
  }

  public void setActionSuivante(String actionSuivante)
  {
    this.actionSuivante = actionSuivante;
  }

  public void setChildren(List<PositionGeographique> children)
  {
    this.children = children;
  }

  public void setFather(PositionGeographique father)
  {
    this.father = father;
  }

  public void setIdChild(Long idChild)
  {
    this.idChild = idChild;
  }

  public void setIdFather(Long idFather)
  {
    this.idFather = idFather;
  }

  public String getNomArretDestination()
  {
    return nomArretDestination;
  }

  public void setNomArretDestination(String nomArretDestination)
  {
    this.nomArretDestination = nomArretDestination;
  }

  public Long getIdArretDestination()
  {
    return idArretDestination;
  }

  public void setIdArretDestination(Long idArretDestination)
  {
    this.idArretDestination = idArretDestination;
  }

  public Long getIdArretSource()
  {
    return idArretSource;
  }

  public void setIdArretSource(Long idArretSource)
  {
    this.idArretSource = idArretSource;
  }

  public List<Reseau> getReseaux()
  {
    return reseaux;
  }

  public void setReseaux(List<Reseau> reseaux)
  {
    this.reseaux = reseaux;
  }

  /********************************************************
   *              AJAX AUTOCOMPLETE                       *
   ********************************************************/
  @SkipValidation
  public String ajaxBoardingPositions()
  {
    List<PositionGeographique> boardingPositions = positionGeographiqueManager.lireArretsPhysiques();

    List<PositionGeographique> boardingPositionsAfterFilter = new ArrayList<PositionGeographique>();
    for (PositionGeographique boardingPosition : boardingPositions)
    {
      String name = boardingPosition.getName();
      if (name.contains(boardingPositionName))
      {
        boardingPositionsAfterFilter.add(boardingPosition);
      }
    }

    request.put("boardingPositions", boardingPositionsAfterFilter);
    return AUTOCOMPLETE;
  }

  public String getBoardingPositionName()
  {
    return boardingPositionName;
  }

  public void setBoardingPositionName(String boardingPositionName)
  {
    this.boardingPositionName = boardingPositionName;
  }
}