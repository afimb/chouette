package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import chouette.schema.ChouetteLineDescription;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class LecteurCourseCSV {
	
	private static final Logger       logger = Logger.getLogger( LecteurCourseCSV.class);
	
	private String                    cleCommentaire;                // "Commentaire sur la course"
	private IIdentificationManager    identificationManager;

	private Map<String, List<String>> contenuParTitre;
	private int                       total;
	
	public LecteurCourseCSV() {
		super();
		contenuParTitre = new Hashtable<String, List<String>>();
	}
	
	public void initialiser(int total) {
		contenuParTitre.clear();
		this.total = total;
	}
	
	public void ajouter(String titre, List<String> contenu) {
		assert contenu.size() == total * 2: "total attendu "+(total*2)+", total obtenu "+contenu.size();
		if (isCle(titre))
			contenuParTitre.put(titre, contenu);
	}
	
	public List<Course> lire() {
		List<Course> courses = new ArrayList<Course>(total);
		for (int i = 0; i < total; i++) {
			Course course = new Course();
			courses.add(course);
			course.setObjectId(identificationManager.getIdFonctionnel("VehicleJourney", String.valueOf(i*2)));
			course.setObjectVersion(1);
			course.setRouteId(identificationManager.getIdFonctionnel("Route", String.valueOf(i*2)));
			course.setJourneyPatternId(identificationManager.getIdFonctionnel("JourneyPattern", String.valueOf(i*2)));
			List<String> contenu = contenuParTitre.get(cleCommentaire);
			course.setComment(contenu.get(i*2));
			course.setCreationTime(new Date());
		}
		return courses;
	}
	
	public String[] ecrire(ChouetteLineDescription chouetteLineDescription, int length, int colonneTitrePartieFixe) {
		String[] donneesCours = new String[length];
		donneesCours[colonneTitrePartieFixe] = getCleCommentaire();
		for (int i = 0; i < chouetteLineDescription.getVehicleJourneyCount(); i++)
			donneesCours[colonneTitrePartieFixe+1+2*i] = chouetteLineDescription.getVehicleJourney(i).getComment();
		return donneesCours;
	}
	
	public boolean isCle(String titre) {
		if (titre == null)
			return false;
		return titre.equals(cleCommentaire);
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
}
