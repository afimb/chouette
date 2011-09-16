package fr.certu.chouette.struts.stoppointOnRoute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.filter.FilterOrder;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.struts.GeneriqueAction;


public class StoppointOnRouteAction extends GeneriqueAction implements ModelDriven<StopPoint>, Preparable
{
	private static final long serialVersionUID = 343087639329801584L;
	private static final Logger log = Logger.getLogger(StoppointOnRouteAction.class);
	public static final String POSITIONS_INVALIDES = "POSITIONS_INVALIDES";
	// Managers
	@Setter private INeptuneManager<Line> lineManager;
	@Setter private INeptuneManager<Route> routeManager;
	@Setter private INeptuneManager<StopPoint> stopPointManager;
	@Setter private INeptuneManager<StopArea> stopAreaManager;

	//	Identifiants
	@Getter @Setter private Long idItineraire;
	@Getter @Setter private Long idLigne;
	//	liste des arrêts à déplacer saisie par l'utilisateur
	@Getter @Setter private Map<Long, Boolean> deplacementsArret;
	// Liste des Arrets dans l'ordre de l'struts
	@Getter @Setter private String ordreArretItineraire;
	// Liste des arrêts
	@Getter private List<StopPoint> arrets;
	// private List<StopPoint> arretsModifies;
	// nom et id de l'arrêt saisie par l'utilisateur
	@Getter @Setter private String nomArretAInserer;
	@Getter @Setter private String idArretAInserer;
	// Position de l'arrêt sélectionné par l'utilisateur
	@Getter @Setter private int positionArret;
	// Hashtable permettant d'obtenir l'arrêt physique à partir de id de l'arrêt
	// logique
	private Map<Long, StopArea> arretPhysiqueParIdArret;
	private StopPoint stopPointModel = new StopPoint();
	@Setter private String mappedRequest;

	private Line line;
	private Route route;

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
		filter.addOrder(FilterOrder.asc("position"));

		arrets = stopPointManager.getAll(null,filter);

		arretPhysiqueParIdArret = new HashMap<Long, StopArea>();
		for (StopPoint arret : arrets) 
		{
			Long id = arret.getId();

			StopArea arretPhysique = arret.getContainedInStopArea();
			if (arretPhysique != null) 
			{
				arretPhysiqueParIdArret.put(id, arretPhysique);
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

	public String supprimerArret() throws Exception
	{
		Route route = getItineraire();
		route.removeStopPointAt(positionArret);
		routeManager.update(null, route);
		
		addActionMessage(getText("arretSurItineraire.delete.ok"));

		return REDIRECTLIST;
	}

	@SkipValidation
	public String insererArret() throws Exception
	{
		Route route = getItineraire();
		if (route.getStopPoints().isEmpty())
		{
			positionArret = -1;
		}
		if (nomArretAInserer != null && !nomArretAInserer.isEmpty())
		{
			// Création de l'arrêt dans la liste des arrêts modifiés
			String prefix = route.getObjectId().split(":")[0];
			StopPoint nouveauArret = new StopPoint();
			nouveauArret.setObjectId(prefix);
			nouveauArret.setRoute(route);
			nouveauArret.setName(nomArretAInserer);
			// Dans le cas où l'utilisateur a saisi l'identifiant de l'arrêt physique
			if (idArretAInserer != null && !idArretAInserer.isEmpty())
			{
				Long id = Long.valueOf(idArretAInserer);
				StopArea physique = stopAreaManager.getById(id);
				nouveauArret.setContainedInStopArea(physique);
			}
			else
			{
				StopArea physique = new StopArea();
				physique.setObjectId(prefix);
				physique.setName(nomArretAInserer);
				physique.setAreaType(ChouetteAreaEnum.BOARDINGPOSITION);
				stopAreaManager.addNew(null, physique);
				nouveauArret.setContainedInStopArea(physique);
			}
			stopPointManager.addNew(null, nouveauArret);
			route.addStopPointAt(positionArret + 1, nouveauArret);
			routeManager.update(null, route);

			addActionMessage(getText("arretSurItineraire.create.ok"));
		}
		return REDIRECTLIST;
	}

	public String deplacerArret() throws Exception
	{
		log.debug("Déplacer un arrêt");
		int positionPremierArret = -1;
		StopPoint premierArret = null;
		int positionDeuxiemeArret = -1;
		StopPoint deuxiemeArret = null;
		int nombreDeplacements = 0;
		for (StopPoint arret : arrets)
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
			Route route = getItineraire();
			route.swapStopPoints(premierArret, deuxiemeArret);
			// Enregistrement des modifications sur les arrêts de l'itinéraire
			routeManager.update(null, route);
			

			addActionMessage(getText("arretSurItineraire.move.ok"));

			return REDIRECTLIST;
		}
	}


	/********************************************************
	 *                   METHOD ACTION                      *
	 ********************************************************/

	// when invalid, the request parameter will restore command action
	public void setActionMethod(String method)
	{
		this.mappedRequest = method;
	}

	public String getActionMethod()
	{
		return mappedRequest;
	}

	public StopArea getArretPhysique(Long idArret)
	{
		return arretPhysiqueParIdArret.get(idArret);
	}

	public String getCreerArret()
	{
		return "nouveauArret";
	}

	public Route getItineraire() throws Exception
	{
        if (route == null) route = routeManager.getById(idItineraire);
		return route;
	}

	public Line getLigne() throws Exception
	{
		if (line == null) line = lineManager.getById(idLigne);
		return line;
	}


	public int getTotalArrets()
	{
		return arrets.size();
	}

}
