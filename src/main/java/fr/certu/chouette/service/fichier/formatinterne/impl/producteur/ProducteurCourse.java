package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ProducteurCourse implements IProducteurSpecifique {
	
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurCourse(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		Map<String, Long> resultat = new Hashtable<String, Long>();
		List<Course> courses = echange.getCourses();
		List<Course> coursesNouvelles = new ArrayList<Course>();
		for (Course course : courses) {
			String courseObjectId = course.getObjectId();
			String itineraireObjectId = course.getRouteId();
			String journeyObjectId = course.getJourneyPatternId();
			Long idItineraire = null;
			idItineraire = idParObjectId.get(itineraireObjectId);
			Long idMission = (journeyObjectId != null) ? idParObjectId.get(journeyObjectId) : null;
			Long idCourse = null;
			if ((!incremental) || ((incremental) && (!etatDifference.isObjectIdCourseConnue(courseObjectId)))) {
				idCourse = new Long(fournisseurId.getNouvelId(courseObjectId));
				coursesNouvelles.add(course);
			}
			else
				idCourse = etatDifference.getIdCourseConnue(courseObjectId);
			course.setId(idCourse);
			course.setIdItineraire(idItineraire);
			course.setIdMission(idMission);
			resultat.put(courseObjectId, idCourse);
		}
		List<String[]> contenu = traduire(majIdentification, coursesNouvelles);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierCourse());
		return resultat;
	}
	
	private List<String[]> traduire(final boolean majIdentification, final List<Course> courses) {
		List<String[]> contenu = new ArrayList<String[]>(courses.size());
		for (Course course : courses) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("VehicleJourney", course) : course.getObjectId();
			champs.add(course.getId().toString());
			champs.add(course.getIdItineraire().toString());
			champs.add(gestionFichier.getChamp(course.getIdMission()));
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(course.getObjectVersion()));
			champs.add(gestionFichier.getChamp(course.getCreationTime()));
			champs.add(gestionFichier.getChamp(course.getCreatorId()));
			champs.add(gestionFichier.getChamp(course.getPublishedJourneyName()));
			champs.add(gestionFichier.getChamp(course.getPublishedJourneyIdentifier()));
			champs.add(gestionFichier.getChamp(course.getTransportMode()));
			champs.add(gestionFichier.getChamp(course.getVehicleTypeIdentifier()));
			champs.add(gestionFichier.getChamp(course.getStatusValue()));
			champs.add(gestionFichier.getChamp(course.getFacility()));
			champs.add(gestionFichier.getChamp(course.getNumber()));
			champs.add(gestionFichier.getChamp(course.getComment()));
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
}
