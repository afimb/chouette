package fr.certu.chouette.ihm;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajArretItineraire;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

@SuppressWarnings({"serial", "unchecked", "unused", "static-access"})
public class ArretSurItineraireAction extends GeneriqueAction implements RequestAware, Preparable {
	
	private static final Log                             log                         = LogFactory.getLog(ArretSurItineraireAction.class);
	public  static final String                          POSITIONS_INVALIDES         = "POSITIONS_INVALIDES";
	// Managers
	private static       IItineraireManager              itineraireManager;
	private static       ILigneManager                   ligneManager;
	private static       IPositionGeographiqueManager    positionGeographiqueManager;
	//	Identifiants
	private              Long                            idItineraire;
	private              Long                            idLigne; 
	//	liste des arrrêts à déplacer saisie par l'utilisateur
	private              Map<Long, Boolean>              deplacementsArret;
	// Liste des Arrets dans l'ordre de l'IHM
	private              String                          ordreArretItineraire;
	// action demandée par l'utilisateur sur un submit
	private              String                          action;
	// Liste des arrêts
	private              List<ArretItineraire>           arrets;
	private              List<ArretItineraire>           arretsModifies;
	// nom et id de l'arrêt saisie par l'utilisateur
	private              String                          nomArretAInserer;
	private              String                          idArretAInserer;
	// Position de l'arrêt sélectionné par l'utilisateur
	private              int                             positionArret;
	// Hashtable permettant d'obtenir l'arrêt physique à partir de id de l'arrêt
	// logique
	private              Map<Long, PositionGeographique> arretPhysiqueParIdArret;	
	//	Requete
	private              Map                             request;
	
	public String cancel() {
		addActionMessage(getText("arretSurItineraire.cancel.ok"));
		return SUCCESS;
	}
	
	@SkipValidation
	public String insererArret() {
		if (arretsModifies.isEmpty())
			positionArret = -1;
		if (nomArretAInserer != null && !nomArretAInserer.isEmpty()) {
			// Création de l'arrêt dans la liste des arrêts modifiés
			ArretItineraire nouveauArret = new ArretItineraire();
			nouveauArret.setIdItineraire(idItineraire);
			nouveauArret.setId(System.nanoTime());
			nouveauArret.setName(nomArretAInserer);
			// Dans le cas où l'utilisateur a saisi l'identifiant de l'arrêt physique
			if (idArretAInserer != null && !idArretAInserer.isEmpty())
				nouveauArret.setIdPhysique(Long.valueOf(idArretAInserer));
			arretsModifies.add(positionArret + 1, nouveauArret);
			// Modification des positions des arrêts à partir de celui créé dans la liste
			for (int i = positionArret + 1; i < arretsModifies.size(); i++)
				arretsModifies.get(i).setPosition(i);
			// Enregistrement des modifications sur les arrêts de l'itinéraire
			itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());
		}
		return ActionSupport.SUCCESS;
	}
	
	private List<EtatMajArretItineraire> creerListeEtatMajArret() {
		Map<Long, ArretItineraire> arretsParId = new Hashtable<Long, ArretItineraire>();
		for (ArretItineraire arret : arrets)
			arretsParId.put(arret.getId(), arret);
		Map<Long, ArretItineraire> arretsModifiesParId = new Hashtable<Long, ArretItineraire>();
		for (ArretItineraire arret : arretsModifies)
			arretsModifiesParId.put(arret.getId(), arret);
		// Création de la liste des états de mise à jour
		List<EtatMajArretItineraire> listeEtatMajArretItineraire = new ArrayList<EtatMajArretItineraire>();
		for (ArretItineraire arretModifie : arretsModifies) {
			ArretItineraire arretInitial = arretsParId.get(arretModifie.getId());
			if (arretInitial == null)
				if (arretModifie.getIdPhysique() == null)
					listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerCreation(arretModifie.getPosition(), arretModifie.getName()));
				else
					listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerCreation(arretModifie.getPosition(), arretModifie.getIdPhysique()));
			else if (arretInitial != null && arretInitial.getPosition() != arretModifie.getPosition())
				listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerDeplace(arretModifie.getPosition(), arretModifie.getId()));
		}
		Set<Long> idsArretsDisparus = new HashSet<Long>(arretsParId.keySet());
		idsArretsDisparus.removeAll(arretsModifiesParId.keySet());
		for (Long idArretDisparu : idsArretsDisparus)
			listeEtatMajArretItineraire.add(EtatMajArretItineraire.creerSuppression(idArretDisparu));
		return listeEtatMajArretItineraire;
	}
	
	public String deplacerArret() {
		log.debug("Déplacer un arrêt");
		int positionPremierArret = -1;
		ArretItineraire premierArret = null;
		int positionDeuxiemeArret = -1;
		ArretItineraire deuxiemeArret = null;
		int nombreDeplacements = 0;
		for (ArretItineraire arret : arretsModifies) {
			if(deplacementsArret.get(arret.getId())) {
				nombreDeplacements++;
				if(positionPremierArret == -1) {
					positionPremierArret = arret.getPosition();
					premierArret = arret;
				}
				else if(positionDeuxiemeArret == -1) {
					positionDeuxiemeArret = arret.getPosition();
					deuxiemeArret = arret;
				}
			}
		}
		if(nombreDeplacements < 1 || nombreDeplacements > 2)
			return POSITIONS_INVALIDES;
		else {
			premierArret.setPosition(positionDeuxiemeArret);
			deuxiemeArret.setPosition(positionPremierArret);
			// Enregistrement des modifications sur les arrêts de l'itinéraire
			itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());
			return ActionSupport.SUCCESS;
		}
	}
	
	public PositionGeographique getArretPhysique(Long idArret) {
		return arretPhysiqueParIdArret.get(idArret);
	}
	
	public List<ArretItineraire> getArrets() {
		return arrets;
	}	
	
	public String getCreerArret() {
		return "nouveauArret";
	}
	
	public Map<Long, Boolean> getDeplacementsArret() {
		return deplacementsArret;
	}
	
	public Long getIdItineraire() {
		return idItineraire;
	}
	
	public Long getIdLigne() {
		return idLigne;
	}
	
	public Itineraire getItineraire() {
		return itineraireManager.lire(idItineraire);
	}
	
	public String getJsonArrets() {
		StringBuffer resultat = new StringBuffer( "{");
		PositionGeographique dernier = null;		
		List<PositionGeographique> arretsPhysiques = positionGeographiqueManager.lireArretsPhysiques();
		if ( arretsPhysiques.size()>0)
			dernier = arretsPhysiques.remove( arretsPhysiques.size()-1);		
		for (PositionGeographique arretPhysique : arretsPhysiques) {
			resultat.append( "\"");
			if (arretPhysique.getName() != null)
				resultat.append(arretPhysique.getName());
			resultat.append("(");
			if (arretPhysique.getCountryCode() != null)
				resultat.append(arretPhysique.getCountryCode());
			if(arretPhysique.getStreetName() != null) {
				if(arretPhysique.getCountryCode() != null)
					resultat.append(", ");
				resultat.append(arretPhysique.getStreetName());
			}
			if(arretPhysique.getObjectId() != null) {
				if (arretPhysique.getCountryCode() != null || arretPhysique.getStreetName() != null)
					resultat.append(", ");
				resultat.append(arretPhysique.getObjectId());
			}
			resultat.append(")\": ");
			resultat.append(arretPhysique.getId());
			resultat.append(",");		
		}
		if ( dernier!=null) {
			resultat.append( "\"");
			if (dernier.getName() != null)
				resultat.append(dernier.getName());
			resultat.append("(");
			if (dernier.getCountryCode() != null)
				resultat.append(dernier.getCountryCode());
			if (dernier.getStreetName() != null) {
				if (dernier.getCountryCode() != null)
					resultat.append(", ");
				resultat.append(dernier.getStreetName());
			}
			if (dernier.getObjectId() != null) {
				if (dernier.getCountryCode() != null || dernier.getStreetName() != null)
					resultat.append(", ");
				resultat.append(dernier.getObjectId());
			}
			resultat.append(")\": ");
			resultat.append(dernier.getId());
		}		
		resultat.append("}");
		return resultat.toString();
	}
	
	public Ligne getLigne() {
		return ligneManager.lire(idLigne);
	}
	
	public String getOrdreArretItineraire() {
		return ordreArretItineraire;
	}
	
	public int getPositionArret() {
		return positionArret;
	}
	
	public int getTotalArrets() {
		return arrets.size();
	}
	
	@Override
	public String input() throws Exception {
		return SUCCESS;
	}
	
	@SkipValidation
	public String list() {
		return SUCCESS;
	}
	
	public void prepare() throws Exception {
		// Initialisation de la liste des arrets d'un itinéraire
		arrets = itineraireManager.getArretsItineraire(idItineraire);
		arretsModifies = ArretItineraire.dupliquer(arrets);
		arretPhysiqueParIdArret = positionGeographiqueManager.getArretPhysiqueParIdArret(arrets);
		//	Mise à disposition si la liste des arrets physiques est vide pour le Javascript
		request.put("arretsVide", (arrets == null || arrets.isEmpty()));
		//	Mise à disposition de la liste de tous les arrets physiques sous format JSON pour le Javascript
		request.put("jsonArrets", getJsonArrets());
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setArretsModifies(List<ArretItineraire> arretsModifies) {
		this.arretsModifies = arretsModifies;
	}
	
	public void setDeplacementsArret(Map<Long, Boolean> deplacementsArret) {
		this.deplacementsArret = deplacementsArret;
	}
	
	public void setIdItineraire(Long idItineraire) {
		this.idItineraire = idItineraire;
	}
	
	public void setIdLigne(Long idLigne) {
		this.idLigne = idLigne;
	}
	
	public void setItineraireManager(IItineraireManager itineraireManager) {
		this.itineraireManager = itineraireManager;
	}
	
	public void setLigneManager(ILigneManager ligneManager) {
		ArretSurItineraireAction.ligneManager = ligneManager;
	}
	
	public void setOrdreArretItineraire(String ordreArretItineraire) {
		this.ordreArretItineraire = ordreArretItineraire;
	}
	
	public void setPositionArret(int positionArret) {
		this.positionArret = positionArret;
	}

	public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) {
		ArretSurItineraireAction.positionGeographiqueManager = positionGeographiqueManager;
	}
	
	public Map getRequest() {
		return request;
	}
	
	public void setRequest(Map request) {
		this.request = request;
	}
	
	public String supprimerArret() {
		// Suppression de l'arrêt dans la liste des arrêts modifiés
		arretsModifies.remove(positionArret);
		// Modification des positions des arrêts à partir de celui supprimé dans
		// la liste
		int totalArrets = arretsModifies.size();
		for (int i = positionArret; i < totalArrets; i++)
			arretsModifies.get(i).setPosition(i);
		// Enregistrement des modifications sur les arrêts de l'itinéraire
		itineraireManager.modifierArretsItineraire(idItineraire, creerListeEtatMajArret());
		return SUCCESS;
	}
	
	public String getNomArretAInserer() {
		return nomArretAInserer;
	}
	
	public void setNomArretAInserer(String nomArretAInserer) {
		this.nomArretAInserer = nomArretAInserer;
	}
	
	public String getIdArretAInserer() {
		return idArretAInserer;
	}
	
	public void setIdArretAInserer(String idArretAInserer) {
		this.idArretAInserer = idArretAInserer;
	}
}
