package fr.certu.chouette.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import chouette.schema.types.ChouetteAreaType;

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

public class PositionGeographiqueAction extends GeneriqueAction implements Preparable, RequestAware {
	
	private static final Log				log	= LogFactory.getLog(PositionGeographiqueAction.class);

	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	String LOADEDIT = "loadedit";
	
	//	Type de position Geographique
	private static final String ARRETPHYSIQUE = "arretPhysique"; 
	private static final String ZONE = "zone"; 

	//	Manager de l'Action
	private IPositionGeographiqueManager				positionGeographiqueManager;
	private ILigneManager			ligneManager;
	private IReseauManager			reseauManager;
	
	//	Requete
	private Map<String, Object> request;
	private Long							idPositionGeographique;
	
	//	Gestion des zones
	private PositionGeographique 			positionGeographique;
	
	private PositionGeographique 			criteria;
	private List<PositionGeographique>		positionGeographiques;
	private List<PositionGeographique>		positionGeographiquesResultat;
	private List<PositionGeographique>		children;
	private PositionGeographique			father;
	private Long							idChild;
	private Long							idFather;
	

	private String							childAreaName;

	private String							fatherAreaName;

	private Collection<String> 				authorizedAreas ;
	
	private String 							authorizedType;
	
	private List<Itineraire>				itineraires;
	
	private Map<Long, Ligne>				ligneParIdItineraire;	
	
	private Map<Long, Reseau>				reseauParIdLigne;

	private Map<Long, Boolean>				presenceItineraireParIdPhysique;
	
	//	Définition du type de position géographique 
	private String 							typePositionGeographique;	
	
	//	Paramètre permettant la gestion des redirections pour les méthodes modifier ArretPhysique, redirection vers : 
	//	- liste des horaires de passage
	//	- ou liste des arrêts physiques
	private String							actionSuivante;
	
	// Numéro de la page actuelle pour la navigation parmi les différentes
	// courses
	private Integer page;
	
	//	Chaine de caractere implémenté pour complété les retours des actions fait par struts
	String CREATEANDEDIT = "createAndEdit";
	
	String nomArret = null;
	String codeInsee = null;
	Long idReseau = null;
	
	private List<Reseau> reseaux;
	private Map<Long, Reseau> reseauParId;
	
	private Long idItineraire;
	private Long idLigne;
	
	public void setPage(int page)
	{
		this.page = page;
	}	
	
	@SkipValidation
	public String addChild()
	{	
		if (idChild != null && idPositionGeographique!= null) 
			positionGeographiqueManager.associerGeoPositions(idPositionGeographique, idChild);

		return LOADEDIT;
	}
	
	@SkipValidation
	public String addFather()
	{	

		if (idFather != null && idPositionGeographique != null) 
			positionGeographiqueManager.associerGeoPositions(idFather, idPositionGeographique);

		return LOADEDIT;
	}
	
	public String cancel()
	{
		if(typePositionGeographique.equals(ARRETPHYSIQUE))
			addActionMessage(getText("arretPhysique.cancel.ok"));
		else
			addActionMessage(getText("zone.cancel.ok"));
		return SUCCESS;
	}

	public String delete()
	{
		positionGeographiqueManager.supprimer(idPositionGeographique);
		if(typePositionGeographique.equals(ARRETPHYSIQUE))
			addActionMessage(getText("arretPhysique.delete.ok"));
		else
			addActionMessage(getText("zone.delete.ok"));
		
		return SUCCESS;
	}

	public String edit()
	{	
		return INPUT;
	}
	
	public String getJsonArrets()
	{
		StringBuffer resultat = new StringBuffer( "{");
		PositionGeographique dernier = null;		
		List<PositionGeographique> arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();

		if ( arretsPhysiques.size()>0)
			dernier = arretsPhysiques.remove( arretsPhysiques.size()-1);		
		
		for (PositionGeographique arretPhysique : arretsPhysiques)
		{
			resultat.append( "\"");
			if(arretPhysique.getName() != null)
				resultat.append(arretPhysique.getName());
			resultat.append("(");
			if(arretPhysique.getCountryCode() != null)
				resultat.append(arretPhysique.getCountryCode());
			
			if(arretPhysique.getStreetName() != null)
			{
				if(arretPhysique.getCountryCode() != null)
					resultat.append(", ");
				resultat.append(arretPhysique.getStreetName());
			}
					
			if(arretPhysique.getObjectId() != null)
			{
				if(arretPhysique.getCountryCode() != null || arretPhysique.getStreetName() != null)
					resultat.append(", ");
				resultat.append(arretPhysique.getObjectId());
			}
			
			resultat.append(")\": ");
			resultat.append(arretPhysique.getId());
			resultat.append(",");		
		}
		if ( dernier!=null)
		{
			resultat.append( "\"");
			if(dernier.getName() != null)
				resultat.append(dernier.getName());
			resultat.append("(");
			if(dernier.getCountryCode() != null)
				resultat.append(dernier.getCountryCode());
			
			if(dernier.getStreetName() != null)
			{
				if(dernier.getCountryCode() != null)
					resultat.append(", ");
				resultat.append(dernier.getStreetName());
			}
					
			if(dernier.getObjectId() != null)
			{
				if(dernier.getCountryCode() != null || dernier.getStreetName() != null)
					resultat.append(", ");
				resultat.append(dernier.getObjectId());
			}
			
			resultat.append(")\": ");
			resultat.append(dernier.getId());
		}		
		resultat.append("}");
		
		return resultat.toString();
	}
	
	public String getActionSuivante()
	{
		return actionSuivante;
	}

	public Collection<String> getAuthorizedAreas() {
		return authorizedAreas;
	}

	public String getAuthorizedType() {
		return authorizedType;
	}

	public String getChildAreaName() {
		return childAreaName;
	}

	public List<PositionGeographique> getChildren() {
		return children;
	}

	public PositionGeographique getCriteria() {
		return criteria;
	}

	public PositionGeographique getFather() {
		return father;
	}

	public String getFatherAreaName() {
		return fatherAreaName;
	}

	public Long getIdChild() {
		return idChild;
	}

	public Long getIdFather() {
		return idFather;
	}
	
	public Long getIdPositionGeographique() {
		return idPositionGeographique;
	}
	
	  public Long getIdZone() {
		return idPositionGeographique;
	}
	  
	public List<Itineraire> getIti()
	{
		return itineraires;
	}	
	
	private String getJsonPositionGeographiques()
	{
		String resultat = "{";

		if(positionGeographique.getAreaType().equals(ChouetteAreaType.BOARDINGPOSITION) || positionGeographique.getAreaType().equals(ChouetteAreaType.QUAY))
			positionGeographiques = positionGeographiqueManager.lireArretsPhysiques();
		else	
			positionGeographiques = positionGeographiqueManager.lireZones();
		
		for (PositionGeographique positionGeographique : positionGeographiques)
		{
			if (positionGeographiques.indexOf(positionGeographique) == positionGeographiques.size() - 1) {
				resultat += "\"" + positionGeographique.getName() + "\"" + ": " + positionGeographique.getId();
			} else {
				resultat += "\"" + positionGeographique.getName() + "\"" + ": " + positionGeographique.getId() + ",";
			}
		}
		resultat += "}";
		//log.debug("resultat : " + resultat);
		return resultat;
	}		
	
	public String getLiaisonItineraire(Long idPhysique)
	{
		return presenceItineraireParIdPhysique.get(idPhysique).booleanValue() ? "hidden" : "visible";
	}
	
	public Ligne getLigne(Long idItineraire)
	{
		return ligneParIdItineraire.get(idItineraire);
	}

	public PositionGeographique getPositionGeographique() {
		return positionGeographique;
	}

	public List<PositionGeographique> getPositionGeographiquesResultat() {
		return positionGeographiquesResultat;
	}
	
	public List<PositionGeographique> getPositionGeographiques() {
		return positionGeographiques;
	}
	
	public Reseau getReseau(Long idLigne)
	{
		return reseauParIdLigne.get(idLigne);
	}
	
	public String getTypePositionGeographique()
	{
		if (positionGeographique != null && positionGeographique.getId() != null)
		{
			if (positionGeographique.getAreaType().getType() == ChouetteAreaType.QUAY_TYPE 
					|| positionGeographique.getAreaType().getType() == ChouetteAreaType.BOARDINGPOSITION_TYPE)
				return ARRETPHYSIQUE;
			else
				return ZONE;
		}
		else
			return typePositionGeographique;
	}
	
	@Override
	public String input() throws Exception
	{		
		return INPUT;
	}

	public String listOLD() {
		
		if(typePositionGeographique == null) {
			
			addActionError("Le type de position géographique n'a pas été défini");
			positionGeographiques = null;
			
		} else if(typePositionGeographique.equals(ARRETPHYSIQUE)) {
			
			positionGeographiques = positionGeographiqueManager.lireArretsPhysiques();
			presenceItineraireParIdPhysique = positionGeographiqueManager.getPresenceItineraireParPhysiqueId();
			
		} else positionGeographiques = positionGeographiqueManager.lireZones();
		
		// Mise à disposition de la liste de tous les arrets physiques sous format JSON pour le Javascript
		request.put("jsonArrets", getJsonArrets());
		
		return SUCCESS;
	}


	Long idArretDestination = null;
	String nomArretDestination = null;
	
	Long idArretSource = null;
	
	@SkipValidation
	public String fusionnerArrets() {
		
		positionGeographiqueManager.fusionnerPositionsGeographiques(idArretSource, idArretDestination);
		
		return SUCCESS;
	}
	
	public void prepare() throws Exception {
		
		// Chargement des réseaux
		reseaux = reseauManager.lire();
		reseauParId = new Hashtable<Long, Reseau>();
		for (Reseau reseau : reseaux) 
		{
			reseauParId.put( reseau.getId(), reseau);
		}
		
		if (idPositionGeographique == null) return;

		// Récupération de la position Geographique (arret physique ou zone)				
 		positionGeographique = positionGeographiqueManager.lire(idPositionGeographique);
		
		//	Création des zones filles et parentes
		children = positionGeographiqueManager.getGeoPositionsDirectementContenues(idPositionGeographique);
		if (positionGeographique.getIdParent() != null)
			father = positionGeographiqueManager.lire(positionGeographique.getIdParent());
		
		// Création de la liste des itinéraires
		itineraires = positionGeographiqueManager.getItinerairesArretPhysique(idPositionGeographique);

		ligneParIdItineraire = new Hashtable<Long, Ligne>();
		// Création de la liste des identifiants de lignes (pas de doublons)
		Collection<Long> idsLignes = new HashSet<Long>();
		for (Itineraire itineraire : itineraires)
		{
			if (itineraire.getIdLigne() != null) idsLignes.add(itineraire.getIdLigne());
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
		// Création de la liste des identifiants de reseaux (pas de doublons)
		Collection<Long> idsReseaux = new HashSet<Long>();
		for (Ligne ligne : lignes)
		{
			if (ligne.getIdReseau() != null) idsReseaux.add(ligne.getIdReseau());
		}
		// Création de la liste des reseaux à partir de la liste des identfiants
		// de reseaux
		List<Reseau> reseaux = reseauManager.getReseaux(idsReseaux);

		// Création d'une map liant id Reseau -> Objet Reseau
		Map<Long, Reseau> reseauParId = new Hashtable<Long, Reseau>();
		for (Reseau reseau : reseaux)
		{
			reseauParId.put(reseau.getId(), reseau);
		}

		// Création d'une hashtable liant id Ligne -> Objet Reseau
		for (Ligne ligne : lignes)
		{
			if (ligne.getIdReseau() != null)
			{
				Reseau reseau = reseauParId.get(ligne.getIdReseau());
				reseauParIdLigne.put(ligne.getId(), reseau);
			}
		}
		
		//	Mise en requete de la liste des AreaStops
		request.put("jsonPositionGeographiques", getJsonPositionGeographiques());
	}

	@SkipValidation
	public String removeChildFromParent()
	{	
		if (idChild != null) {
			positionGeographiqueManager.dissocierGeoPositionParente(idChild);
		}
		return LOADEDIT;
	} 

	@SkipValidation
	public String search()
	{
		if (idPositionGeographique != null) {
			positionGeographique = positionGeographiqueManager.lire(idPositionGeographique);
			authorizedAreas = new HashSet<String>();
			
			if ("addChild".equals(getActionSuivante())) {
				switch (positionGeographique.getAreaType().getType()) {
					case ChouetteAreaType.STOPPLACE_TYPE:
						authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_ALL;
						break;
					
					case ChouetteAreaType.COMMERCIALSTOPPOINT_TYPE:
						authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_QB;
						break;
						
					case ChouetteAreaType.BOARDINGPOSITION_TYPE:
					case ChouetteAreaType.QUAY_TYPE:
						break;
				}
			}
			else if ("addFather".equals(getActionSuivante())) {
				switch (positionGeographique.getAreaType().getType()) {
					case ChouetteAreaType.STOPPLACE_TYPE:
						authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
						break;
					
					case ChouetteAreaType.COMMERCIALSTOPPOINT_TYPE:
						authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_S;
						break;
						
					case ChouetteAreaType.BOARDINGPOSITION_TYPE:
					case ChouetteAreaType.QUAY_TYPE:
						authorizedType = EnumerationApplication.AUTHORIZEDTYPESET_CS;
						break;
				}
			}
		}
		return SUCCESS;
	}

	@SkipValidation
	public String list() {
		
		if (typePositionGeographique == null) return SUCCESS;

		List <ChouetteAreaType> areaTypes = new ArrayList <ChouetteAreaType> ();
		
		if (ARRETPHYSIQUE.equals(typePositionGeographique)) {
			
			areaTypes.add(ChouetteAreaType.QUAY);
			areaTypes.add(ChouetteAreaType.BOARDINGPOSITION);
			
		} else {
			
			areaTypes.add(ChouetteAreaType.STOPPLACE);
			areaTypes.add(ChouetteAreaType.COMMERCIALSTOPPOINT);
			
		}
		
		if (nomArret != null)
			if ("".equals(nomArret.trim()))
				nomArret = null;
		if (codeInsee != null)
			if ("".equals(codeInsee.trim()))
				codeInsee = null;
		
		positionGeographiques = positionGeographiqueManager.lire(nomArret, codeInsee, idReseau, areaTypes);
		
		request.put("jsonArrets", getJsonArrets());
		
		return SUCCESS;
	}
	
	@SkipValidation
	public String searchResults() {
		
		if(typePositionGeographique == null) {
			
			addActionError("Le type de position géographique n'a pas été défini");
			positionGeographiquesResultat = null;
			
		} else if(typePositionGeographique.equals(ARRETPHYSIQUE)) {
			
			positionGeographiquesResultat = positionGeographiqueManager.lireArretsPhysiques();
			presenceItineraireParIdPhysique = positionGeographiqueManager.getPresenceItineraireParPhysiqueId();
			
		} else {
			
			// Clause areaType
			Collection<String> areas = new HashSet<String>();
			if (criteria.getAreaType() != null) areas.add( criteria.getAreaType().toString());
			else {
				
				if (EnumerationApplication.AUTHORIZEDTYPESET_C.equals(authorizedType)) {
					
					areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
					
				} else if (EnumerationApplication.AUTHORIZEDTYPESET_CS.equals(authorizedType)) {
					
					areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
					areas.add(ChouetteAreaType.STOPPLACE.toString());
					
				} else if (EnumerationApplication.AUTHORIZEDTYPESET_QB.equals(authorizedType)) {
					
					areas.add(ChouetteAreaType.QUAY.toString());
					areas.add(ChouetteAreaType.BOARDINGPOSITION.toString());
					
				} else if (EnumerationApplication.AUTHORIZEDTYPESET_S.equals(authorizedType)) {
					
					areas.add(ChouetteAreaType.STOPPLACE.toString());
				} 
			}
			
			IClause searchClause = new AndClause (
					ScalarClause.newIlikeClause("name", criteria.getName()),
					ScalarClause.newIlikeClause("countryCode", criteria.getCountryCode()),
					VectorClause.newInClause("areaType", areas)
			);
			
			positionGeographiquesResultat = positionGeographiqueManager.select(searchClause);
		}
			
		return "results";
	}

	public void setActionSuivante(String actionSuivante)
	{
		this.actionSuivante = actionSuivante;
	}

	public void setAuthorizedAreas(Collection<String> authorizedAreas) {
		this.authorizedAreas = authorizedAreas;
	}

	public void setAuthorizedType(String authorizedType) {
		this.authorizedType = authorizedType;
	}

	public void setChildAreaName(String childAreaName) {
		this.childAreaName = childAreaName;
	}

	public void setChildren(List<PositionGeographique> children) {
		this.children = children;
	}
	
	public void setCriteria(PositionGeographique criteria) {
		this.criteria = criteria;
	}
	
	public void setFather(PositionGeographique father) {
		this.father = father;
	}
	
	public void setFatherAreaName(String fatherAreaName) {
		this.fatherAreaName = fatherAreaName;
	}

	public void setIdChild(Long idChild) {
		this.idChild = idChild;
	}

	public void setIdFather(Long idFather) {
		this.idFather = idFather;
	}

	public void setIdPositionGeographique(Long idPositionGeographique) {
		this.idPositionGeographique = idPositionGeographique;
	}

	public void setLigneManager(ILigneManager ligneManager)
	{
		this.ligneManager = ligneManager;
	}

	public void setPositionGeographique(PositionGeographique positionGeographique) {
		this.positionGeographique = positionGeographique;
	}

	public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager)
	{
		this.positionGeographiqueManager = positionGeographiqueManager;
	}

	public void setPositionGeographiques(List<PositionGeographique> positionGeographiques) {
		this.positionGeographiques = positionGeographiques;
	}

	public void setRequest(Map request)
	{
		this.request = request;
	}

	public void setReseauManager(IReseauManager reseauManager)
	{
		this.reseauManager = reseauManager;
	}

	public void setTypePositionGeographique(String typePositionGeographique)
	{
		this.typePositionGeographique = typePositionGeographique;
	}
	
	public String update()
	{					
		log.debug("update : " + positionGeographique.getId());
		if (positionGeographique == null) { return INPUT; }
		
		if ( positionGeographique.getId() == null)
		{
			positionGeographiqueManager.creer(positionGeographique);
			if(typePositionGeographique.equals(ARRETPHYSIQUE))
				addActionMessage(getText("arretPhysique.create.ok"));
			else
				addActionMessage(getText("zone.create.ok"));
		}
		else
		{
			positionGeographiqueManager.modifier(positionGeographique);
			if(typePositionGeographique.equals(ARRETPHYSIQUE))
				addActionMessage(getText("arretPhysique.update.ok"));
			else
				addActionMessage(getText("zone.update.ok"));
		}			
		return INPUT;
	}
	
	public String createAndEdit()
	{					
		log.debug("update : " + positionGeographique.getId());
		if (positionGeographique == null) { return INPUT; }
		
		if ( positionGeographique.getId() == null)
		{
			positionGeographiqueManager.creer(positionGeographique);
			if(typePositionGeographique.equals(ARRETPHYSIQUE))
				addActionMessage(getText("arretPhysique.create.ok"));
			else
				addActionMessage(getText("zone.create.ok"));
		}
		else
			return INPUT;	
		
		return CREATEANDEDIT;
	}


	public String getNomArretDestination() {
		return nomArretDestination;
	}

	public void setNomArretDestination(String nomArretDestination) {
		this.nomArretDestination = nomArretDestination;
	}

	public Long getIdArretDestination() {
		return idArretDestination;
	}

	public void setIdArretDestination(Long idArretDestination) {
		this.idArretDestination = idArretDestination;
	}

	public Long getIdArretSource() {
		return idArretSource;
	}

	public void setIdArretSource(Long idArretSource) {
		this.idArretSource = idArretSource;
	}

	public String getNomArret() {
		return nomArret;
	}

	public void setNomArret(String nomArret) {
		this.nomArret = nomArret;
	}

	public List<Reseau> getReseaux() {
		return reseaux;
	}

	public void setReseaux(List<Reseau> reseaux) {
		this.reseaux = reseaux;
	}

	public String getCodeInsee() {
		return codeInsee;
	}

	public void setCodeInsee(String codeInsee) {
		this.codeInsee = codeInsee;
	}

	public Long getIdReseau() {
		return idReseau;
	}

	public void setIdReseau(Long idReseau) {
		this.idReseau = idReseau;
	}
	
	public Long getIdItineraire() {
		return idItineraire;
	}

	public void setIdItineraire(Long idItineraire) {
		this.idItineraire = idItineraire;
	}

	public Long getIdLigne() {
		return idLigne;
	}

	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}	
}
