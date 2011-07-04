package fr.certu.chouette.struts.stoppointOnRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import fr.certu.chouette.struts.GeneriqueAction;


public class StoppointOnRouteAction extends GeneriqueAction implements ModelDriven<StopPoint>, Preparable
{

  private static final Logger log = Logger.getLogger(StoppointOnRouteAction.class);
  public static final String POSITIONS_INVALIDES = "POSITIONS_INVALIDES";
  // Managers
  /*
  private IItineraireManager itineraireManager;
  private ILigneManager ligneManager;
  private IPositionGeographiqueManager positionGeographiqueManager;
  */
  @Setter private INeptuneManager<Line> lineManager;
  @Setter private INeptuneManager<Route> routeManager;
  @Setter private INeptuneManager<StopPoint> stopPointManager;
  @Setter private INeptuneManager<StopArea> stopAreaManager;
  //	Identifiants
  private Long idItineraire;
  private Long idLigne;
  //	liste des arrrêts à déplacer saisie par l'utilisateur
  private Map<Long, Boolean> deplacementsArret;
  // Liste des Arrets dans l'ordre de l'struts
  private String ordreArretItineraire;
  // Liste des arrêts
  private List<StopPoint> arrets;
  private List<StopPoint> arretsModifies;
  // nom et id de l'arrêt saisie par l'utilisateur
  private String nomArretAInserer;
  private String idArretAInserer;
  // Position de l'arrêt sélectionné par l'utilisateur
  private int positionArret;
  // Hashtable permettant d'obtenir l'arrêt physique à partir de id de l'arrêt
  // logique
  private Map<Long, StopArea> arretPhysiqueParIdArret;
  private StopPoint stopPointModel = new StopPoint();
  private String mappedRequest;

  public Long getIdItineraire()
  {
    return idItineraire;
  }

  public void setIdItineraire(Long idItineraire)
  {
    this.idItineraire = idItineraire;
  }

  public void setIdLigne(Long idLigne)
  {
    this.idLigne = idLigne;
  }

  public Long getIdLigne()
  {
    return idLigne;
  }

  /********************************************************
   *                  MODEL + PREPARE                     *
   ********************************************************/
  public StopPoint getModel()
  {
    return stopPointModel;
  }

  public void prepare() throws Exception
  {
    // Initialisation de la liste des arrets d'un itinéraire
	Filter filter = Filter.getNewEqualsFilter("route.id", idItineraire);
    arrets = stopPointManager.getAll(null,filter,DetailLevelEnum.NARROW_DEPENDENCIES);
    
    arretsModifies = new ArrayList<StopPoint>();
    for (StopPoint arret : arrets) 
    {
    	arretsModifies.add((StopPoint) BeanUtils.cloneBean(arret));
	}
    
    arretPhysiqueParIdArret = new HashMap<Long, StopArea>();
    for (StopPoint arret : arrets) 
    {
		Long id = arret.getId();

		StopArea arretPhysique = arret.getContainedInStopArea();
		if (arretPhysique != null) 
			{
			arretPhysiqueParIdArret.put(id, arretPhysique);
			log.debug("Arret "+id+" a pour arrêt physique "+arretPhysique.getName());
			}
		else
		{
		   log.warn("Arret "+id+" n'a pas d'arrêt physique");
		}
	}
  }

  /********************************************************
   *                           CRUD                       *
   ********************************************************/
  @SkipValidation
  public String list()
  {
    log.debug("List of arretSurItineraire");
    return LIST;
  }

  @SkipValidation
  public String cancel()
  {
    addActionMessage(getText("arretSurItineraire.cancel.ok"));
    return REDIRECTLIST;
  }

  @Override
  @SkipValidation
  public String input() throws Exception
  {
    return INPUT;
  }

  public String supprimerArret()
  {
    // Suppression de l'arrêt dans la liste des arrêts modifiés
    arretsModifies.remove(positionArret);
    // Modification des positions des arrêts à partir de celui supprimé dans
    // la liste
    int totalArrets = arretsModifies.size();
    for (int i = positionArret; i < totalArrets; i++)
    {
      arretsModifies.get(i).setPosition(i);
    }

    addActionMessage(getText("arretSurItineraire.delete.ok"));
    // Enregistrement des modifications sur les arrêts de l'itinéraire
    // itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());
    
    return REDIRECTLIST;
  }

  @SkipValidation
  public String insererArret() throws Exception
  {
    if (arretsModifies.isEmpty())
    {
      positionArret = -1;
    }
    if (nomArretAInserer != null && !nomArretAInserer.isEmpty())
    {
      // Création de l'arrêt dans la liste des arrêts modifiés
      StopPoint nouveauArret = new StopPoint();
      nouveauArret.setRoute(getItineraire());
      nouveauArret.setId(System.nanoTime());
      nouveauArret.setName(nomArretAInserer);
      // Dans le cas où l'utilisateur a saisi l'identifiant de l'arrêt physique
      if (idArretAInserer != null && !idArretAInserer.isEmpty())
      {
    	Long id = Long.valueOf(idArretAInserer);
    	StopArea physique = stopAreaManager.getById(id);
        nouveauArret.setContainedInStopArea(physique);
      }
      arretsModifies.add(positionArret + 1, nouveauArret);
      // Modification des positions des arrêts à partir de celui créé dans la liste
      for (int i = positionArret + 1; i < arretsModifies.size(); i++)
      {
        arretsModifies.get(i).setPosition(i);
      }

      addActionMessage(getText("arretSurItineraire.create.ok"));
      // Enregistrement des modifications sur les arrêts de l'itinéraire
      // itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());
    }
    return REDIRECTLIST;
  }

  public String deplacerArret()
  {
    log.debug("Déplacer un arrêt");
    int positionPremierArret = -1;
    StopPoint premierArret = null;
    int positionDeuxiemeArret = -1;
    StopPoint deuxiemeArret = null;
    int nombreDeplacements = 0;
    for (StopPoint arret : arretsModifies)
    {
      if (deplacementsArret.get(arret.getId()))
      {
        nombreDeplacements++;
        if (positionPremierArret == -1)
        {
          positionPremierArret = arret.getPosition();
          premierArret = arret;
        } else if (positionDeuxiemeArret == -1)
        {
          positionDeuxiemeArret = arret.getPosition();
          deuxiemeArret = arret;
        }
      }
    }
    if (nombreDeplacements < 1 || nombreDeplacements > 2)
    {
      return POSITIONS_INVALIDES;
    } else
    {
      premierArret.setPosition(positionDeuxiemeArret);
      deuxiemeArret.setPosition(positionPremierArret);
      
      addActionMessage(getText("arretSurItineraire.move.ok"));
      // Enregistrement des modifications sur les arrêts de l'itinéraire
      // TODO à recoder ! 
      // itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());

      return REDIRECTLIST;
    }
  }

  /********************************************************
   *                        MANAGER                       *
   ********************************************************/
  /*
  public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
  {
    this.positionGeographiqueManager = positionGeographiqueManager;
  }

  public void setItineraireManager(IItineraireManager itineraireManager)
  {
    this.itineraireManager = itineraireManager;
  }

  public void setLigneManager(ILigneManager ligneManager)
  {
    this.ligneManager = ligneManager;
  }
  */

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

  private List<EtatMajArretItineraire> creerListeEtatMajArret()
  {
    Map<Long, StopPoint> arretsParId = StopPoint.mapOnIds(arrets);
    
    Map<Long, StopPoint> arretsModifiesParId = StopPoint.mapOnIds(arretsModifies);
   
    // Création de la liste des états de mise à jour
    List<EtatMajArretItineraire> listeEtatMajArretItineraire = new ArrayList<EtatMajArretItineraire>();
    for (StopPoint arretModifie : arretsModifies)
    {
    	StopPoint arretInitial = arretsParId.get(arretModifie.getId());
      if (arretInitial == null)
      {
        if (arretModifie.getContainedInStopArea() == null)
        {
          listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerCreation(arretModifie.getPosition(), arretModifie.getName()));
        } else
        {
          listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerCreation(arretModifie.getPosition(), arretModifie.getContainedInStopArea().getId()));
        }
      } else if (arretInitial != null && arretInitial.getPosition() != arretModifie.getPosition())
      {
        listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerDeplace(arretModifie.getPosition(), arretModifie.getId()));
      }
    }
    Set<Long> idsArretsDisparus = new HashSet<Long>(arretsParId.keySet());
    idsArretsDisparus.removeAll(arretsModifiesParId.keySet());
    for (Long idArretDisparu : idsArretsDisparus)
    {
      listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerSuppression(idArretDisparu));
    }
    return listeEtatMajArretItineraire;
  }

  public StopArea getArretPhysique(Long idArret)
  {
    return arretPhysiqueParIdArret.get(idArret);
  }

  public List<StopPoint> getArrets()
  {
    return arrets;
  }

  public String getCreerArret()
  {
    return "nouveauArret";
  }

  public Map<Long, Boolean> getDeplacementsArret()
  {
    return deplacementsArret;
  }

  public Route getItineraire() throws Exception
  {
	  
    return routeManager.getById(idItineraire);
  }

  public Line getLigne() throws Exception
  {
    return lineManager.getById(idLigne);
  }

  public String getOrdreArretItineraire()
  {
    return ordreArretItineraire;
  }

  public int getPositionArret()
  {
    return positionArret;
  }

  public int getTotalArrets()
  {
    return arrets.size();
  }

  public void setArretsModifies(List<StopPoint> arretsModifies)
  {
    this.arretsModifies = arretsModifies;
  }

  public void setDeplacementsArret(Map<Long, Boolean> deplacementsArret)
  {
    this.deplacementsArret = deplacementsArret;
  }

  public void setOrdreArretItineraire(String ordreArretItineraire)
  {
    this.ordreArretItineraire = ordreArretItineraire;
  }

  public void setPositionArret(int positionArret)
  {
    this.positionArret = positionArret;
  }

  public String getNomArretAInserer()
  {
    return nomArretAInserer;
  }

  public void setNomArretAInserer(String nomArretAInserer)
  {
    this.nomArretAInserer = nomArretAInserer;
  }

  public String getIdArretAInserer()
  {
    return idArretAInserer;
  }

  public void setIdArretAInserer(String idArretAInserer)
  {
    this.idArretAInserer = idArretAInserer;
  }

}
