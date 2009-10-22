package fr.certu.chouette.ihm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.PrincipalAware;
import org.apache.struts2.interceptor.PrincipalProxy;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;

public class ItineraireAction extends GeneriqueAction implements Preparable
{
	private static int					cpt	= 0;

	private static final Log			log	= LogFactory.getLog(ItineraireAction.class);
	
	//	Manager
	private static IItineraireManager	itineraireManager;
	private static ILigneManager		ligneManager;

	private List<Itineraire>			itineraires;

	private Itineraire					itineraire;

	private Long						idItineraire;
	private Long						idLigne;
	private String						idRetour;
	
	private String 						sensAller = "A";
	private String 						sensRetour = "R";
	
	/**
	 * 
	 */
	public String creerItineraireRetour()
	{
		itineraireManager.creerItineraireRetour( idItineraire);

		addActionMessage(getText("itineraire.retour.ok"));

		return SUCCESS;
	}
	
	public String cancel()
	{
		addActionMessage(getText("itineraire.cancel.ok"));
		return SUCCESS;
	}

	/**
	 * Renvoit la liste des itinéraires classés avec au début les itinéraires par couple aller et retour et ensuite les itinéraires sans retour
	 * @return liste des itinéraires classés
	 */
	private List<Itineraire> classementItinerairesAllerRetour()
	{
		//	Récupération des itinéraires pour un identifiant de ligne donnée
		List<Itineraire> itinerairesLigne = ligneManager.getItinerairesLigne(idLigne);
		//	Liste des itinéraires classés	
		List<Itineraire> itinerairesClasses = new ArrayList<Itineraire>();
		//	Liste de tous les ids de retour de la liste des itinéraires initiale
		List<Long> idsRetour = new ArrayList<Long>();
		
		
		Map<Long, Itineraire> itineraireParIdItineraire = new Hashtable<Long, Itineraire>();
		
		for (Itineraire itineraire : itinerairesLigne)
		{
			itineraireParIdItineraire.put(itineraire.getId(), itineraire);
		}
		
		
		for (Itineraire itineraire : itinerairesLigne)
		{
			if(itineraire.getIdRetour() != null)
			{
				// Si l'idRetour de l'itinéraire est déjà compris dans la liste des idsRetour on continue la boucle 
				if(idsRetour.contains(itineraire.getId()))
				{
					continue;
				}
				//	Ajout de l'idRetour a la liste des idsRetour pour ne pas les prendre en compte car ajout dans les itineraires classes de l'itinéraire aller et retour
				idsRetour.add(itineraire.getIdRetour());
				
				//	Ajout de l'itinéraire aller et retour en début de liste 
				if(itineraire.getWayBack().equals("A"))
				{
					//	Si l'itinéraire retour se trouve dans la liste on l'ajoute
					if(itineraireParIdItineraire.get(itineraire.getIdRetour()) != null)
					{
						itinerairesClasses.add(0, itineraireParIdItineraire.get(itineraire.getIdRetour()));
					}
					itinerairesClasses.add(0, itineraire);		
				}
				else
				{
					itinerairesClasses.add(0, itineraire);
					//	Si l'itinéraire retour se trouve dans la liste on l'ajoute
					if(itineraireParIdItineraire.get(itineraire.getIdRetour()) != null)
					{
						itinerairesClasses.add(0, itineraireParIdItineraire.get(itineraire.getIdRetour()));
					}					
				}
			}
			else
			{
				//	Ajout d'un itineraire sans idRetour à la fin de la liste
				itinerairesClasses.add(itineraire);
			}
		}
		
		return itinerairesClasses;
	}
	
	public String delete()
	{
		itineraireManager.supprimer(idItineraire);
		addActionMessage(getText("itineraire.delete.ok"));
		return SUCCESS;
	}	

	@SkipValidation
	public String edit()
	{
		return INPUT;
	}

	public Long getIdLigne()
	{
		return idLigne;
	}

	public String getIdRetour()
	{
		log.debug("this.itineraire : " + this.itineraire);
		if(this.itineraire.getIdRetour() == null)
			return "-1";
		else	
			return this.itineraire.getIdRetour().toString();
	}

	public Itineraire getItineraire()
	{
		return itineraire;
	}

	public List<Itineraire> getItineraires()
	{
		return itineraires;
	}
	
	/**
	 * Retourne la liste des itinéraires de la ligne en supprimant celui qui est en train d'être édité
	 * @return
	 */
	public List<Itineraire> getItinerairesSansItineraireEdite()
	{
		List<Itineraire> itinerairesSansItineraireEdite = ligneManager.getItinerairesLigne(idLigne);

		//	Suppression dans la liste des itinéraires de celui étant édité		
		for (Itineraire itineraire : itinerairesSansItineraireEdite)
		{
			if(itineraire.getId().equals(this.idItineraire))
			{
				itinerairesSansItineraireEdite.remove(itineraire);
				break;
			}
			
		}
		
		return itinerairesSansItineraireEdite;
	}	

	public Ligne getLigne()
	{
		return ligneManager.lire(idLigne);
	}

	public String getSensItineraire()
	{
		if(itineraire != null && itineraire.getWayBack() != null)
			return itineraire.getWayBack().toString();
		else 
			return sensAller;
	}
	
	public Map<String, String> getSensItineraires()
	{
		Map<String, String> sens = new HashMap<String, String>();
		sens.put(sensRetour, sensRetour);
		sens.put(sensAller, sensAller);		
		return sens;
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

	@SkipValidation
	public String list()
	{	
		// Récupération des itinéraires pour un identifiant de ligne donnée
		itineraires = classementItinerairesAllerRetour();

		return SUCCESS;
	}

	@SkipValidation
	public void prepare() throws Exception
	{
		log.debug("prepare itineraire");
		
		if (idItineraire != null)
		{
			itineraire = itineraireManager.lire(idItineraire);
		}
	}

	public void setIdItineraire(Long idItineraire)
	{
		this.idItineraire = idItineraire;
	}

	public void setIdLigne(Long idLigne)
	{
		this.idLigne = idLigne;
	}
	
	public void setIdRetour(String idRetour)
	{
		this.idRetour = idRetour;
	}

	public void setItineraire(Itineraire itineraire)
	{
		log.debug("set itineraire : itineraire = " + itineraire.toString());
		this.itineraire = itineraire;
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
		log.debug("update : " + itineraire);
		if (itineraire == null) { return INPUT; }
		
		// ré-affecter l'identifiant de la ligne sur l'itinéraire
		itineraire.setIdLigne(idLigne);
		
		if ( itineraire.getId()==null)
		{
			itineraireManager.creer(itineraire);
			if(this.idRetour != null)
			{
				//	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
				if(this.idRetour.equals("-1"))
				{
					itineraireManager.dissocierItineraire(idItineraire);
				}
				else
				{
					itineraireManager.dissocierItineraire(idItineraire);
					itineraireManager.associerItineraire( itineraire.getId(), new Long(this.idRetour));
				}
			}
			addActionMessage(getText("itineraire.create.ok"));
		}
		else
		{
			itineraireManager.modifier(itineraire);
			if(this.idRetour != null)
			{			
				//	Si exitence d'un idRetour on associe cet itinéraire avec son itinéraire retour
				if(this.idRetour.equals("-1"))
				{
					itineraireManager.dissocierItineraire(idItineraire);
				}
				else
				{
					itineraireManager.dissocierItineraire(idItineraire);
					itineraireManager.associerItineraire( itineraire.getId(), new Long(this.idRetour));
				}
			}
			addActionMessage(getText("itineraire.update.ok"));
		}
		
		return SUCCESS;
	}
}