package fr.certu.chouette.struts.route;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.struts.GeneriqueAction;

public class RouteAction extends GeneriqueAction implements ModelDriven<Route>, Preparable
{

	private static final Logger log = Logger.getLogger(RouteAction.class);
	@Setter private INeptuneManager<Line> lineManager;
	@Setter private INeptuneManager<Route> routeManager;
	@Setter private INeptuneManager<StopPoint> stopPointManager;
	private Route routeModel = new Route();
	private Long routeId;
	private Long lineId;
	private Line line;
	private String mappedRequest;
	private String oppositeRouteId;
	private List<Route> itinerairesSansItineraireEdite; // TODO rename

	public Long getIdItineraire()
	{
		return routeId;
	}

	public void setIdItineraire(Long idItinerary)
	{
		this.routeId = idItinerary;
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
	public Route getModel()
	{
		return routeModel;
	}

	public void prepare() throws Exception
	{
		log.debug("Prepare with id : " + getIdItineraire());
		if (getIdItineraire() == null)
		{
			routeModel = new Route();
		}
		else
		{
			Filter filter = Filter.getNewEqualsFilter("id", getIdItineraire());
			routeModel = routeManager.get(null,filter,DetailLevelEnum.ATTRIBUTE);
		}

		if (getIdLigne() != null)
		{
			// TODO : Le virer grâce a OGNL
			Filter filter = Filter.getNewEqualsFilter("line.id", lineId);
			itinerairesSansItineraireEdite = routeManager.getAll(null,filter,DetailLevelEnum.ATTRIBUTE);
			//	Suppression dans la liste des itinéraires de celui étant édité
			for (Route itineraire : itinerairesSansItineraireEdite)
			{
				if (itineraire.getId().equals(routeModel.getId()))
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
	public String list() throws Exception
	{
		if(lineId != null)
		{
			Filter filter = Filter.getNewEqualsFilter("id", lineId);
			line = null;
			try 
			{
				line =  lineManager.get(null, filter, DetailLevelEnum.ATTRIBUTE);
			} catch (ChouetteException e) 
			{
				log.error(e.getLocalizedMessage(),e);
			}
		}
		else
		{
			line = null;
		}
		
		//	Récupération des itinéraires pour un identifiant de ligne donnée
		Filter filter = Filter.getNewEqualsFilter("line.id", lineId);
		
		List<Route> itinerairesLigne = routeManager.getAll(null,filter,DetailLevelEnum.ATTRIBUTE);
		//	Liste des itinéraires classés
		List<Route> itineraires = new ArrayList<Route>();
		//	Liste de tous les ids de retour de la liste des itinéraires initiale
		List<Long> idsRetour = new ArrayList<Long>();


		Map<Long, Route> itineraireParIdItineraire = Route.mapOnIds(itinerairesLigne);

		for (Route itineraire : itinerairesLigne)
		{
			itineraireParIdItineraire.put(itineraire.getId(), itineraire);
		}


		for (Route itineraire : itinerairesLigne)
		{
			if (itineraire.getOppositeRouteId() != null)
			{
				// Si l'idRetour de l'itinéraire est déjà compris dans la liste des idsRetour on continue la boucle
				if (idsRetour.contains(itineraire.getId()))
				{
					continue;
				}
				//	Ajout de l'idRetour a la liste des idsRetour pour ne pas les prendre en compte car ajout dans les itineraires classes de l'itinéraire aller et retour
				idsRetour.add(itineraire.getOppositeRouteId());

				//	Ajout de l'itinéraire aller et retour en début de liste
				if (itineraire.getWayBack().equals("A"))
				{
					//	Si l'itinéraire retour se trouve dans la liste on l'ajoute
					if (itineraireParIdItineraire.get(itineraire.getOppositeRouteId()) != null)
					{
						itineraires.add(0, itineraireParIdItineraire.get(itineraire.getOppositeRouteId()));
					}
					itineraires.add(0, itineraire);
				}
				else
				{
					itineraires.add(0, itineraire);
					//	Si l'itinéraire retour se trouve dans la liste on l'ajoute
					if (itineraireParIdItineraire.get(itineraire.getOppositeRouteId()) != null)
					{
						itineraires.add(0, itineraireParIdItineraire.get(itineraire.getOppositeRouteId()));
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

	public String save() throws Exception
	{
		
		// link to line : 
		if (routeModel.getLine() == null)
		   routeModel.setLine(line);
		routeManager.addNew(null,routeModel);

		/*
		if (routeModel.getOppositeRouteId() != null)
		{
			//	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
			if (routeModel.getOppositeRouteId().equals("-1"))
			{
				routeManager.dissocierItineraire(routeModel.getId());
			}
			else
			{
				routeManager.dissocierItineraire(routeModel.getId());
				routeManager.associerItineraire(routeModel.getId(), new Long(routeModel.getIdRetour()));
			}
		}
		*/

		setMappedRequest(SAVE);
		addActionMessage(getText("itineraire.create.ok"));
		log.debug("Create route with id : " + routeModel.getId());
		return REDIRECTLIST;
	}

	@SkipValidation
	public String edit()
	{
		setMappedRequest(UPDATE);
		return EDIT;
	}

	public String update() throws Exception
	{
		routeManager.update(null, routeModel);

		/*
		if (routeModel.getIdRetour() != null)
		{
			//	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
			if (routeModel.getIdRetour().equals("-1"))
			{
				routeManager.dissocierItineraire(routeModel.getId());
			}
			else
			{
				routeManager.dissocierItineraire(routeModel.getId());
				routeManager.associerItineraire(routeModel.getId(), new Long(routeModel.getIdRetour()));
			}
		}
		*/

		setMappedRequest(UPDATE);
		addActionMessage(getText("itineraire.update.ok"));
		log.debug("Update route with id : " + routeModel.getId());
		return REDIRECTLIST;
	}

	public String delete() throws Exception
	{
		routeManager.remove(null,getModel(),false);
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
//	public void setItineraireManager(IItineraireManager itineraryManager)
//	{
//		this.routeManager = itineraryManager;
//	}
//
//	public void setLigneManager(ILigneManager lineManager)
//	{
//		this.lineManager = lineManager;
//	}

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

	public List<Route> getItinerairesSansItineraireEdite()
	{
		return itinerairesSansItineraireEdite;
	}

	public String creerItineraireRetour()
	{
		// routeManager.creerItineraireRetour(routeModel.getId());
		addActionMessage(getText("itineraire.retour.ok"));
		return SUCCESS;
	}

	public boolean isArretsVide(Long idItineraire) throws Exception
	{
		Filter filter = Filter.getNewEqualsFilter("route.id", idItineraire);
		return stopPointManager.getAll(null,filter,DetailLevelEnum.ATTRIBUTE).isEmpty();
	}

	public String getSensItineraire()
	{
		if (routeModel != null && routeModel.getWayBack() != null)
		{
			return routeModel.getWayBack().toString();
		}
		else
		{
			return getText("route.direction.aller");
		}
	}

	public String getIdRetour()
	{
		//log.debug("this.itineraire : " + this.itineraire);
		if (this.routeModel.getOppositeRouteId() == null)
		{
			return "-1";
		}
		else
		{
			return this.routeModel.getOppositeRouteId().toString();
		}
	}

	public void setIdRetour(String idRetour)
	{
		this.oppositeRouteId = idRetour;
	}

	public String getLineName()
	{
		log.debug("lineId : " + lineId);
		if(line != null)
		{
			return line.getName();
		}
		else
			return "";
	}
}
