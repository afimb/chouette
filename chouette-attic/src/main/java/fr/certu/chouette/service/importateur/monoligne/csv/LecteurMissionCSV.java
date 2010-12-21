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
import chouette.schema.JourneyPattern;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurMissionCSV  {
	
    private static final Logger logger = Logger.getLogger( LecteurMissionCSV.class);

	private String                 cleNom;           // "Nom de la mission"
	private String                 cleNomPublic;     // "Nom public de la mission"
	private IIdentificationManager identificationManager;
	
	private Map<String, List<String>> contenuParTitre;
	private int total;
	
	public LecteurMissionCSV() {
		super();
		contenuParTitre = new Hashtable<String, List<String>>();
	}
	
	public void initialiser(int total) {
		contenuParTitre.clear();
		this.total = total;
	}

	public void ajouter(String titre, List<String> contenu) {
		assert contenu.size()==total*2: "total attendu "+(total*2)+", total obtenu "+contenu.size();
		if (isCle( titre))
			contenuParTitre.put(titre, contenu);
	}
	
	public Set<String> getTitresIntrouvables() {
		Set<String> cles = new HashSet<String>();
		cles.add(cleNom);
		cles.add(cleNomPublic);
		Set<String> clesLues = contenuParTitre.keySet();
		cles.removeAll(clesLues);
		return cles;
	}
	
	public List<Mission> lire() {
		List<Mission> missions = new ArrayList<Mission>( total);
		for (int i = 0; i < total; i++)  {
			Mission mission = new Mission();
			missions.add(mission);
			mission.setObjectId(identificationManager.getIdFonctionnel("JourneyPattern", String.valueOf(i*2)));
			mission.setObjectVersion(1);
			mission.setRouteId(identificationManager.getIdFonctionnel("Route", String.valueOf( i*2)));
			List<String> contenuNom = contenuParTitre.get(cleNom);
			List<String> contenuNomPublic = contenuParTitre.get(cleNomPublic);
			mission.setName(contenuNom.get(i*2));
			mission.setPublishedName(contenuNomPublic.get(i*2));
			mission.setCreationTime(new Date());
		}
		return missions;
	}
	
	public List<String[]> ecrire(ChouetteLineDescription chouetteLineDescription, int length, int colonneTitrePartieFixe) {
		List<String[]> donneesMissions = new ArrayList<String[]>();
		String[] donneesMissions1 = new String[length];
		String[] donneesMissions2 = new String[length];
		donneesMissions1[colonneTitrePartieFixe] = getCleNom();
		donneesMissions2[colonneTitrePartieFixe] = getCleNomPublic();
		JourneyPattern[] journeyPatterns = chouetteLineDescription.getJourneyPattern();
		for (int i = 0; i < chouetteLineDescription.getVehicleJourneyCount(); i++) {
			JourneyPattern journeyPattern = null;
			for (int j = 0; j < journeyPatterns.length; j++)
				if (journeyPatterns[j].getObjectId().equals(chouetteLineDescription.getVehicleJourney(i).getJourneyPatternId())) {
					journeyPattern = journeyPatterns[j];
					break;
				}
			if (journeyPattern != null) {
				donneesMissions1[colonneTitrePartieFixe+1+2*i] = journeyPattern.getName();
				donneesMissions2[colonneTitrePartieFixe+1+2*i] = journeyPattern.getPublishedName();
			}
		}
		donneesMissions.add(donneesMissions1);
		donneesMissions.add(donneesMissions2);
		return donneesMissions;
	}
	
	public boolean isCle(String titre) {
		if (titre == null)
			return false;
		return titre.equals(cleNom) || titre.equals( cleNomPublic);
	}
	
	public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
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
}
