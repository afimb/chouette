package fr.certu.chouette.struts.stopArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

import chouette.schema.types.ChouetteAreaType;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.common.ChouetteRuntimeException;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.enumeration.EnumerationApplication;

@SuppressWarnings("serial")
public class StopAreaAction extends GeneriqueAction implements ModelDriven<StopArea>, Preparable
{

	private static final Log log = LogFactory.getLog(StopAreaAction.class);
	@Getter @Setter private INeptuneManager<PTNetwork> networkManager;
	@Setter @Getter private INeptuneManager<StopArea> stopAreaManager;
	@Getter @Setter private INeptuneManager<Line> lineManager;
	@Getter @Setter private INeptuneManager<Route> routeManager;
	@Setter @Getter private INeptuneManager<StopPoint> stopPointManager;
	private StopArea model = new StopArea();
	private String mappedRequest;
	private Long idPositionGeographique;
	//	Gestion des zones
	private StopArea searchCriteria;
	private List<StopArea> children;
	private StopArea father;
	private Long idChild;
	private Long idFather;
	private String authorizedType;
	private List<Route> itineraires = new ArrayList<Route>();
	private Map<Long, Line> ligneParIdItineraire;
	private Map<Long, PTNetwork> reseauParIdLigne;
	private Map<Long, Boolean> presenceItineraireParIdPhysique;
	//	Paramètre permettant la gestion des redirections pour les méthodes modifier ArretPhysique, redirection vers :
	//	- liste des horaires de passage
	//	- ou liste des arrêts physiques
	private String actionSuivante;
	// Numéro de la page actuelle pour la navigation parmi les différentes
	// courses
	// private Integer page;
	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	private String nomArret = null;
	private String codeInsee = null;
	private Long idReseau = null;
	private List<PTNetwork> reseaux;
	private Map<Long, PTNetwork> reseauParId;
	private Long idItineraire;
	private Long idLigne;
	//	Type de position Geographique
	private static final String ARRETPHYSIQUE = "/boardingPosition";
	private static final String ZONE = "/stopPlace";
	private Long idArretDestination = null;
	private String nomArretDestination = null;
	private Long idArretSource = null;
	private String boardingPositionName = "";
	// private Integer START_INDEX_AJAX_LIST = 0;
	// private Integer END_INDEX_AJAX_LIST = 10;
	    private static String actionMsg = null;
    private static String actionErr = null;    


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
	public StopArea getModel()
	{
		return model;
	}

	public void prepare() throws Exception
	{
		log.debug("Prepare with id : " + getIdPositionGeographique());
		if (getIdPositionGeographique() == null)
		{
			model = new StopArea();
		} else
		{
			model = stopAreaManager.get(null, Filter.getNewEqualsFilter("id",getIdPositionGeographique()));

		}

		// Chargement des réseaux
		reseaux = networkManager.getAll(null);
		reseauParId = new Hashtable<Long, PTNetwork>();
		for (PTNetwork reseau : reseaux)
		{
			reseauParId.put(reseau.getId(), reseau);
		}

		if (idPositionGeographique == null)
		{
			return;
		}

		//	Création des zones filles et parentes
		children = stopAreaManager.getAll(null, Filter.getNewEqualsFilter("parentStopArea.id",idPositionGeographique ));

		if (model.getParentStopArea() != null)
		{
			father = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", model.getParentStopArea().getId()));
		}

		// Création de la liste des itinéraires
		//itineraires = positionGeographiqueManager.getItinerairesArretPhysique(idPositionGeographique);
		//Getting stopPoints by stopArea
		List<StopPoint> stopPoints = stopPointManager.getAll(null, 
				Filter.getNewEqualsFilter("containedInStopArea.id",idPositionGeographique));
//
//		List<String> objectIds = StopPoint.extractObjectIds(stopPoints);
//		
//		Filter clauseIn =  Filter.getNewInFilter("", objectIds);
//		itineraires.addAll(routeManager.getAll(null, clauseIn, level));
		for (StopPoint stopPoint : stopPoints) 
		{
			//Route route = routeManager.get(null, Filter.getNewEqualsFilter("stopPoint.id", stopPoint.getId()), level);
			Route route = stopPoint.getRoute();
			if(route != null && !itineraires.contains(route))
				itineraires.add(route );			
		}
		
		ligneParIdItineraire = new Hashtable<Long, Line>();
		// Création de la liste des identifiants de lignes (pas de doublons)
		// Collection<Long> idsLignes = new HashSet<Long>();
		List<Line> lignes = new ArrayList<Line>();
		for (Route itineraire : itineraires)
		{
			Line line = itineraire.getLine();
			if (line != null)
			{
				lignes.add(lineManager.get(null, Filter.getNewEqualsFilter("id",line.getId() )));
			}
		}
		
		// Création d'une map liant id Ligne -> Objet Ligne
		Map<Long, Line> ligneParId = new Hashtable<Long, Line>();
		for (Line ligne : lignes)
		{
			ligneParId.put(ligne.getId(), ligne);
		}
		// Création d'une hashtable liant id Itineraire -> Objet Ligne
		for (Route itineraire : itineraires)
		{
			Line line = itineraire.getLine(); 
			if ( line != null)
			{
				Line ligne = ligneParId.get(line.getId());
				ligneParIdItineraire.put(itineraire.getId(), ligne);
			}
		}

		reseauParIdLigne = new Hashtable<Long, PTNetwork>();
		// Création d'une hashtable liant id Ligne -> Objet Reseau
		for (Line ligne : lignes)
		{
			PTNetwork network = ligne.getPtNetwork();
			if (network != null)
			{
				PTNetwork reseau = reseauParId.get(network.getId());
				reseauParIdLigne.put(ligne.getId(), reseau);
			}
		}
	}

	/********************************************************
	 *                           CRUD                       
	 * @throws ChouetteException *
	 ********************************************************/
	@SuppressWarnings("unchecked")
   @SkipValidation
	public String list() throws ChouetteException
	{
		// Récupération du namespace pour basculer sur des arrèts physiques ou zones
		String namespace = ActionContext.getContext().getActionInvocation().getProxy().getNamespace();
		log.debug("namespace :  " + namespace);
		List<ChouetteAreaEnum> areaTypes = new ArrayList<ChouetteAreaEnum>();

		if (ARRETPHYSIQUE.equals(namespace))
		{
			areaTypes.add(ChouetteAreaEnum.QUAY);
			areaTypes.add(ChouetteAreaEnum.BOARDINGPOSITION);
		} else
		{
			areaTypes.add(ChouetteAreaEnum.STOPPLACE);
			areaTypes.add(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
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
	
		Filter filterOr = Filter.getNewOrFilter(
				Filter.getNewEqualsFilter("areaType", areaTypes.get(0).value()),
				Filter.getNewEqualsFilter("areaType", areaTypes.get(1).value()));
		
		Filter filter = Filter.getNewAndFilter(
				Filter.getNewLikeFilter("name",nomArret), 
				Filter.getNewEqualsFilter("countryCode", codeInsee),
				//Filter.getNewEqualsFilter("", idReseau),
				filterOr);
				
				
		List<StopArea> positionGeographiques = stopAreaManager.getAll(null, filter);
		request.put("positionGeographiques", positionGeographiques);
		log.debug("List of stopArea");
        if (actionMsg != null) {
            addActionMessage(actionMsg);
            actionMsg = null;
        }
        if (actionErr != null) {
            addActionError(actionErr);
            actionErr = null;
        }
		return LIST;
	}

	@SkipValidation
	public String add()
	{
		setMappedRequest(SAVE);
		return EDIT;
	}

	public String save() throws ChouetteException
	{
		stopAreaManager.addNew(null, model);
		if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
		{
            actionMsg = getText("arretPhysique.create.ok");
			log.debug("Create boardingPosition with id : " + model.getId());
		} else
		{
            actionMsg = getText("zone.create.ok");
			log.debug("Create stopPlace with id : " + model.getId());
		}
		setIdPositionGeographique(model.getId());
		setMappedRequest(UPDATE);
		return REDIRECTEDIT;
	}

	@SkipValidation
	public String edit()
	{
		// Get namspace to know if it's a stop place or a boarding position
		String namespace = ActionContext.getContext().getActionInvocation().getProxy().getNamespace();

		if (ZONE.equals(namespace))
		{
			for (StopArea positionGeographique : children)
			{
				AreaCentroid areaCentroid = positionGeographique.getAreaCentroid();
				if(areaCentroid != null)
				{
					ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
					if (!((areaCentroid.getLongitude() != null && areaCentroid.getLatitude() != null)
							|| (projectedPoint != null)))
					{
                    actionMsg = getText("stopplace.children.nocoordinates");
						break;
					}
				}
				
			}
		}

		setMappedRequest(UPDATE);
        if (actionMsg != null) {
            addActionMessage(actionMsg);
            actionMsg = null;
        }
        if (actionErr != null) {
            addActionError(actionErr);
            actionErr = null;
        }
		return EDIT;
	}

	public String update() throws ChouetteException
	{
		stopAreaManager.update(null, model);
		if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
		{
            actionMsg = getText("arretPhysique.update.ok");
			log.debug("Update boardingPosition with id : " + model.getId());
		} else
		{
            actionMsg = getText("zone.update.ok");
			log.debug("Update stopPlace with id : " + model.getId());
		}

		setMappedRequest(UPDATE);
		return REDIRECTEDIT;
	}

	public String delete() throws ChouetteException
	{
		try
		{
		stopAreaManager.remove(null, model, false);
	}
	catch (ChouetteRuntimeException e)
	{
            actionErr = getText("arretPhysique.used");
            return REDIRECTLIST;
        }
		if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
		{
            actionMsg = getText("arretPhysique.delete.ok");
			log.debug("Delete boardingPosition with id : " + model.getId());
		} else
		{
            actionMsg = getText("zone.delete.ok");
			log.debug("Delete stopPlace with id : " + model.getId());
		}

		return REDIRECTLIST;
	}

	@SkipValidation
	public String cancel()
	{
		if (getTypePositionGeographique().equals(ARRETPHYSIQUE))
		{
            actionMsg = getText("arretPhysique.cancel.ok");
		} else
		{
            actionMsg = getText("zone.cancel.ok");
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
            actionErr = getText("arretPhysique.merge.ko");
		} else
		{
            actionMsg = getText("arretPhysique.merge.ok");
            // TODO 
			//positionGeographiqueManager.fusionnerPositionsGeographiques(idArretSource, idArretDestination);
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

	@SuppressWarnings("unchecked")
   @SkipValidation
	public String searchResults() throws ChouetteException
	{
		List<StopArea> positionGeographiquesResultat = new ArrayList<StopArea>();

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
			} else if (EnumerationApplication.AUTHORIZEDTYPESET_ALL.equals(authorizedType))
			{
				areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
				areas.add(ChouetteAreaType.STOPPLACE.toString());
				areas.add(ChouetteAreaType.BOARDINGPOSITION.toString());
				areas.add(ChouetteAreaType.QUAY.toString());
			}
		}
		
		AreaCentroid areaCentroid = searchCriteria.getAreaCentroid();
		if(areaCentroid != null)
		{
			Address address = areaCentroid.getAddress();
			
			Filter filter1 = Filter.getNewLikeFilter("name",searchCriteria.getName()),
			 	   filter2 = Filter.getNewInFilter("areaType", (List<String>)areas), 
			 	   filter3 = null;
			if(address != null) 
			{
				filter3 = Filter.getNewLikeFilter("countryCode",address.getCountryCode());
			}
			Filter filter = Filter.getNewAndFilter(
					filter1,
					filter2,
					filter3);
			positionGeographiquesResultat = stopAreaManager.getAll(null, filter);
		}
		
		request.put("positionGeographiquesResultat", positionGeographiquesResultat);
		return SEARCH;
	}

	@SkipValidation
	public String removeChildFromParent() throws ChouetteException
	{
		if (idChild != null)
		{
			StopArea child = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", idChild));
			if(child != null)
			{
				child.setParentStopArea(null);
				stopAreaManager.update(null, child);	
			}
		}
		return REDIRECTEDIT;
	}

	@SkipValidation
	public String addChild() throws ChouetteException
	{
		if (idChild != null && idPositionGeographique != null)
		{
			StopArea child = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", idChild));
			StopArea parent = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", idPositionGeographique));
			if(child != null)
			{
				child.setParentStopArea(parent);
				stopAreaManager.update(null, child);
			}
			
		}

		return REDIRECTEDIT;
	}

	@SkipValidation
	public String addFather() throws ChouetteException
	{
		if (idFather != null && idPositionGeographique != null)
		{
			StopArea stopArea = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", idPositionGeographique));
			StopArea father = stopAreaManager.get(null, Filter.getNewEqualsFilter("id", idFather));
			if(stopArea != null) 
			{
				stopArea.setParentStopArea(father);
				stopAreaManager.update(null, stopArea);
			}
		}

		return REDIRECTEDIT;
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
	public void setSearchCriteria(StopArea searchCriteria)
	{
		this.searchCriteria = searchCriteria;
	}

	public StopArea getSearchCriteria()
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
			if (model.getAreaType() == ChouetteAreaEnum.QUAY
					|| model.getAreaType() == ChouetteAreaEnum.BOARDINGPOSITION)
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
//	public void setPage(int page)
//	{
//		this.page = page;
//	}

	public String getActionSuivante()
	{
		return actionSuivante;
	}

	public List<StopArea> getChildren()
	{
		return children;
	}

	public StopArea getFather()
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

	public List<Route> getItineraires()
	{
		return itineraires;
	}

	public String getLiaisonItineraire(Long idPhysique)
	{
		return presenceItineraireParIdPhysique.get(idPhysique).booleanValue() ? "hidden" : "visible";
	}

	public Line getLigne(Long idItineraire)
	{
		return ligneParIdItineraire.get(idItineraire);
	}

	public PTNetwork getReseau(Long idLigne)
	{
		return reseauParIdLigne.get(idLigne);
	}

	public void setActionSuivante(String actionSuivante)
	{
		this.actionSuivante = actionSuivante;
	}

	public void setChildren(List<StopArea> children)
	{
		this.children = children;
	}

	public void setFather(StopArea father)
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

	public List<PTNetwork> getReseaux()
	{
		return reseaux;
	}

	public void setReseaux(List<PTNetwork> reseaux)
	{
		this.reseaux = reseaux;
	}

	/********************************************************
	 *              AJAX AUTOCOMPLETE                       
	 * @throws ChouetteException *
	 ********************************************************/
	@SuppressWarnings("unchecked")
   @SkipValidation
	public String ajaxBoardingPositions() throws ChouetteException {
		//List<PositionGeographique> boardingPositions = positionGeographiqueManager.lireArretsPhysiques();
		List<StopArea> boardingPositions = stopAreaManager.getAll(null, Filter.getNewOrFilter(
				Filter.getNewEqualsFilter("areaType",ChouetteAreaEnum.QUAY ),
				Filter.getNewEqualsFilter("areaType",ChouetteAreaEnum.BOARDINGPOSITION)));
		
		// Filter boarding position with the name in request
		int count = 0;
		List<StopArea> boardingPositionsAfterFilter = new ArrayList<StopArea>();
		for (StopArea boardingPosition : boardingPositions) 
		{
			String countryName = "" , streetName = "";
			AreaCentroid areaCentroid = boardingPosition.getAreaCentroid();
			if(areaCentroid != null)
			{
				Address address = areaCentroid.getAddress();
				if(address != null) {
					countryName = address.getCountryCode();
					streetName = address.getStreetName();
				}
			}
			String name = boardingPosition.getName() + " " + 
						  countryName + " " + 
						  streetName + " " + 
						  boardingPosition.getObjectId();
			if (name.contains(boardingPositionName)) {
				boardingPositionsAfterFilter.add(boardingPosition);
				count++;
				if (count >= 10)
					break;
			}
		}
		request.put("boardingPositions", boardingPositionsAfterFilter);
		return AUTOCOMPLETE;
	}

	public String getBoardingPositionName() {
		return boardingPositionName;
	}

	public void setBoardingPositionName(String boardingPositionName) {
		this.boardingPositionName = boardingPositionName;
	}
}
