package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouetteRoute;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurItineraireCSV {
	private static final String ALLER = "ALLER";
	private static final Logger       logger = Logger.getLogger( LecteurItineraireCSV.class);

	private String                    cleCommentaire;    // "Commentaire de l'itin�raire"
	private String                    cleNom;            // "Nom de l'itin�raire"
	private String                    cleNomPublic;      // "Nom public de l'itin�raire"
	private String                    cleSens;         // "Direction (ALLER/RETOUR)"
	private IIdentificationManager    identificationManager;
	
	private Map<String, List<String>> contenuParTitre;
	private int                       total;
	
	public LecteurItineraireCSV() {
		super();
		contenuParTitre = new Hashtable<String, List<String>>();
	}
	
	public void initialiser(int total) {
		contenuParTitre.clear();
		this.total = total;
	}

	public void ajouter( String titre, List<String> contenu) {
		assert contenu.size()==total*2: "total attendu "+(total*2)+", total obtenu "+contenu.size();
		logger.debug("titre:"+titre);
		if (isCle(titre)) {
			logger.debug("titre:"+titre+", contenu="+contenu);
			contenuParTitre.put(titre, contenu);
		}
	}
	
	public Set<String> getTitresIntrouvables() {
		Set<String> cles = new HashSet<String>();
		cles.add(cleCommentaire);
		cles.add(cleNom);
		cles.add(cleNomPublic);
		cles.add(cleSens);
		Set<String> clesLues = contenuParTitre.keySet();
		cles.removeAll( clesLues);
		return cles;
	}
	
	public List<Itineraire> lire() {
		List<Itineraire> itineraires = new ArrayList<Itineraire>(total);
		for (int i = 0; i < total; i++) {
			Itineraire itineraire = new Itineraire();
			itineraires.add(itineraire);
			itineraire.setObjectId(identificationManager.getIdFonctionnel("Route", String.valueOf( i*2)));
			itineraire.setObjectVersion(1);
			List<String> contenuCommentaire = contenuParTitre.get(cleCommentaire);
			List<String> contenuNom = contenuParTitre.get(cleNom);
			List<String> contenuNomPublic = contenuParTitre.get(cleNomPublic);
			List<String> contenuSens = contenuParTitre.get(cleSens);
			itineraire.setComment(contenuCommentaire.get(i*2));
			itineraire.setName(contenuNom.get(i*2));
			itineraire.setPublishedName(contenuNomPublic.get( i*2));
			itineraire.setWayBack( ALLER.equals(contenuSens.get(i*2))?"A":"R");
			itineraire.setCreationTime(new Date());
		}
		return itineraires;
	}
	
	public List<String[]> ecrire(ChouetteLineDescription chouetteLineDescription, int length, int colonneTitrePartieFixe) {
		List<String[]> donneesIteneraires = new ArrayList<String[]>();
		String[] donneesIteneraires1 = new String[length];
		String[] donneesIteneraires2 = new String[length];
		String[] donneesIteneraires3 = new String[length];
		String[] donneesIteneraires4 = new String[length];
		donneesIteneraires1[colonneTitrePartieFixe] = getCleNom();
		donneesIteneraires2[colonneTitrePartieFixe] = getCleNomPublic();
		donneesIteneraires3[colonneTitrePartieFixe] = getCleCommentaire();
		donneesIteneraires4[colonneTitrePartieFixe] = getCleSens();
		ChouetteRoute[] chouetteRoutes = chouetteLineDescription.getChouetteRoute();
		for (int i = 0; i < chouetteLineDescription.getVehicleJourneyCount(); i++) {
			ChouetteRoute chouetteRoute = null;
			for (int j = 0; j < chouetteRoutes.length; j++)
				if (chouetteRoutes[j].getObjectId().equals(chouetteLineDescription.getVehicleJourney(i).getRouteId())) {
					chouetteRoute = chouetteRoutes[j];
					break;
				}
			if (chouetteRoute != null) {
				donneesIteneraires1[colonneTitrePartieFixe+1+2*i] = chouetteRoute.getName();
				donneesIteneraires2[colonneTitrePartieFixe+1+2*i] = chouetteRoute.getPublishedName();
				donneesIteneraires3[colonneTitrePartieFixe+1+2*i] = chouetteRoute.getComment();
				if (chouetteRoute.getRouteExtension() != null)
					if (chouetteRoute.getRouteExtension().getWayBack() != null)
						if (chouetteRoute.getRouteExtension().getWayBack().startsWith("A"))
							donneesIteneraires4[colonneTitrePartieFixe+1+2*i] = "ALLER";
						else if (chouetteRoute.getRouteExtension().getWayBack().startsWith("R"))
							donneesIteneraires4[colonneTitrePartieFixe+1+2*i] = "RETOUR";
			}
		}
		donneesIteneraires.add(donneesIteneraires1);
		donneesIteneraires.add(donneesIteneraires2);
		donneesIteneraires.add(donneesIteneraires3);
		donneesIteneraires.add(donneesIteneraires4);
		return donneesIteneraires;
	}
	
	public boolean isCle(String titre) {
		if (titre == null)
			return false;
		return titre.equals(cleCommentaire) || titre.equals(cleNom) || titre.equals(cleNomPublic) || titre.equals(cleSens);
	}

	public void setCleCommentaire(String cleCommentaire) {
		this.cleCommentaire = cleCommentaire;
	}
	
	public String getCleCommentaire() {
		return cleCommentaire;
	}

	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
	
	public IIdentificationManager getIdentificationManager() {
		return identificationManager;
	}

	public void setCleNom(String cleNom) {
		this.cleNom = cleNom;
	}
	
	public String getCleNom() {
		return cleNom;
	}

	public void setCleNomPublic(String cleNomPublic) {
		this.cleNomPublic = cleNomPublic;
	}
	
	public String getCleNomPublic() {
		return cleNomPublic;
	}

	public String getCleSens() {
		return cleSens;
	}

	public void setCleSens(String cleSens) {
		this.cleSens = cleSens;
	}
}
