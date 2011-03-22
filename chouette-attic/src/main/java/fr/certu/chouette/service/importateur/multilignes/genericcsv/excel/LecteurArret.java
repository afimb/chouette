package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import chouette.schema.types.PTDirectionType;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurArret;

public class LecteurArret implements ILecteurArret {
    
    private static final Logger                            logger                = Logger.getLogger(LecteurArret.class);
    private              IIdentificationManager            identificationManager;// 
    private              List<Itineraire>                  itinerairesDeLigne;
    private              List<Mission>                     missionsDeLigne;
    private              List<Course>                      coursesDeLigne;
    private              Map<Course, List<Horaire>>        horairesParCourse;
    private              Map<Course, List<String>>         arretsPhysiquesParCourse;
    private              List<PositionGeographique>        arretsPhysiques;
    private              int                               counter;
    private              Map<Ligne, List<ArretItineraire>> arretsItinerairesParLigne;
    private              Map<Ligne,Map<String, String>>    itineraireParArret;
    
    @Override
    public Map<Ligne, List<ArretItineraire>> getArretsItinerairesParLigne() {
	return arretsItinerairesParLigne;
    }
    
    @Override
    public Map<Ligne, Map<String, String>> getItineraireParArret() {
	return itineraireParArret;
    }
    
    @Override
    public void init(List<Itineraire> itinerairesDeLigne, List<Mission> missionsDeLigne, List<Course> coursesDeLigne, Map<Course, List<Horaire>> horairesParCourse, Map<Course, List<String>> arretsPhysiquesParCourse, List<PositionGeographique> arretsPhysiques) {
	this.itinerairesDeLigne = itinerairesDeLigne;
	this.missionsDeLigne = missionsDeLigne;
	this.coursesDeLigne = coursesDeLigne;
	this.horairesParCourse = horairesParCourse;
	this.arretsPhysiquesParCourse = arretsPhysiquesParCourse;
	this.arretsPhysiques = arretsPhysiques;
    }
    
    @Override
    public void reinit() {
	this.arretsItinerairesParLigne = new HashMap<Ligne, List<ArretItineraire>>();
	this.itineraireParArret = new HashMap<Ligne, Map<String, String>>();
	this.counter = 0;
    }
    
    @Override
    public void lire(Ligne ligne, Set<Course> coursesAller, Set<Course> coursesRetour, List<PositionGeographique> _arretsPhysiques) {
	logger.debug("Construction des Arrets sur Itinéraire.");
	Map<Itineraire, List<ArretItineraire>> arretsItinerairesParItineraire = new HashMap<Itineraire, List<ArretItineraire>>();
	arretsItinerairesParLigne.put(ligne, new ArrayList<ArretItineraire>());
	itineraireParArret.put(ligne, new HashMap<String, String>());
	if (itinerairesDeLigne != null)
	    for (Itineraire itineraire : itinerairesDeLigne) {
		arretsItinerairesParItineraire.put(itineraire, new ArrayList<ArretItineraire>());
		List<String> arretsPhysiquesDeItineraire = getArretsPhysiques(itineraire);
		if (arretsPhysiquesDeItineraire.size() != arretsPhysiques.size())
		    ; // ERROR
		int position = 0;
		for (int i = 0; i < arretsPhysiques.size(); i++) {
		    //try {
		    if (arretsPhysiquesDeItineraire.get(i).length() > 0) {
			ArretItineraire arretItineraire = new ArretItineraire();
			arretItineraire.setObjectId(identificationManager.getIdFonctionnel("StopPoint", String.valueOf(counter++)));
			if ("AS:StopArea:115".equals(arretsPhysiques.get(i).getObjectId()))
			    ;//logger.error("XXXXXXXXXXXXXXXXXX "+arretsPhysiques.get(i).getName());
			arretItineraire.setContainedIn(arretsPhysiques.get(i).getObjectId());
			//logger.error("ZZZZZZZZZZZZZZZZZ "+arretsPhysiques.get(i).getName());
			arretItineraire.setPosition(position++);
			arretsItinerairesParLigne.get(ligne).add(arretItineraire);
			arretsItinerairesParItineraire.get(itineraire).add(arretItineraire);
			itineraireParArret.get(ligne).put(arretItineraire.getObjectId(), itineraire.getObjectId());
		    }
		    //}
		    //catch(Exception e) {
		    //continue;
		    //}
		}
		logger.debug("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
	    }
	// VERS LECTEURITINERAIRE
	for (Course course : coursesAller) {
	    Itineraire itineraire = getItineraire(course);
	    if (itineraire.getDirection() != null)
		if (itineraire.getDirection().equals(PTDirectionType.R))
		    ; // ERROR
	    itineraire.setDirection(PTDirectionType.A);
	}
	// VERS LECTEURITINERAIRE
	for (Course course : coursesRetour) {
	    Itineraire itineraire = getItineraire(course);
	    if (itineraire.getDirection() != null)
		if (itineraire.getDirection().equals(PTDirectionType.A))
		    ; // ERROR
	    itineraire.setDirection(PTDirectionType.R);
	}
	Map<Itineraire, List<List<Horaire>>> tableauHoraires = getTableauHoraires();
	logger.debug("AFFICHAGE DES TABLEAUX HORAIRES PAR ITINERAIRES");
	for (Itineraire itineraire : itinerairesDeLigne) {
	    logger.debug("Itineraire : "+ itineraire.getObjectId());
	    List<ArretItineraire> larrets = arretsItinerairesParItineraire.get(itineraire);
	    int index = 0;
	    boolean found = false;
	    List<List<Horaire>> llh = tableauHoraires.get(itineraire);
	    for (List<Horaire> lh : llh) {
		for (Horaire h : lh) {
		    h.setStopPointId(larrets.get(index).getObjectId());
		    for (PositionGeographique positionGeographique : arretsPhysiques)
			if (positionGeographique.getObjectId().equals(larrets.get(index).getContainedIn())) {
			    logger.debug("HORAIRE : "+(new SimpleDateFormat("HH:mm")).format(h.getDepartureTime()).toString()+". AU STOP POINT : "+larrets.get(index).getObjectId()+" DE L'ARRET : "+positionGeographique.getName());
			    break;
			}
		    found = true;
		}
		if (found) {
		    found = false;
		    index++;
		}
		logger.debug("...");
	    }
	}
	for (Itineraire itineraire : itinerairesDeLigne) {
	    List<ArretItineraire> larrets = arretsItinerairesParItineraire.get(itineraire);
	    ArretItineraire premierArret = larrets.get(0); 
	    ArretItineraire dernierArret = larrets.get(larrets.size()-1);
	    String itineraireName = "";
	    for (PositionGeographique positionGeographique : arretsPhysiques)
		if (positionGeographique.getObjectId().equals(premierArret.getContainedIn())) {
		    itineraireName = positionGeographique.getName();
		    break;
		}
	    itineraireName = itineraireName + " TO ";
	    for (PositionGeographique positionGeographique : arretsPhysiques)
		if (positionGeographique.getObjectId().equals(dernierArret.getContainedIn())) {
		    itineraireName = itineraireName + positionGeographique.getName();
		    break;
		}
	    itineraire.setName(itineraireName);
	}
	logger.debug("FIN AFFICHAGE DES TABLEAUX HORAIRES PAR ITINERAIRES");
    }
    
    private Map<Itineraire, List<List<Horaire>>> getTableauHoraires() {
	Map<Itineraire, List<List<Horaire>>> tableauHoraires = new HashMap<Itineraire, List<List<Horaire>>>();
	for (Course course : coursesDeLigne) {
	    Itineraire itineraire = getItineraire(course);
	    List<String> _arretsPhysiques = arretsPhysiquesParCourse.get(course);
	    if (tableauHoraires.get(itineraire) == null) {
		tableauHoraires.put(itineraire, new ArrayList<List<Horaire>>());
		for (int i = 0; i < _arretsPhysiques.size(); i++)
		    tableauHoraires.get(itineraire).add(new ArrayList<Horaire>());
	    }
	    List<Horaire> horaires = horairesParCourse.get(course);
	    int horairesCounter = 0;
	    for (int i = 0; i < _arretsPhysiques.size(); i++)
		if (_arretsPhysiques.get(i).length() != 0)
		    tableauHoraires.get(itineraire).get(i).add(horaires.get(horairesCounter++));
	}
	return tableauHoraires;
    }
    
    
    private List<String> getArretsPhysiques(Itineraire itineraire) {
	List<Mission> missions = getMissions(itineraire);
	List<Course> courses = new ArrayList<Course>();
	for (Mission mission : missions)
	    courses.addAll(getCourses(mission));
	List<String> arretsPhysiquesDeItineraire = null;
	for (int i = 0; i < courses.size(); i++) {
	    Course course = courses.get(i);
	    if (i == 0)
		arretsPhysiquesDeItineraire = arretsPhysiquesParCourse.get(course);
	    else
		arretsPhysiquesDeItineraire = merge(arretsPhysiquesDeItineraire, arretsPhysiquesParCourse.get(course));
	}
	return arretsPhysiquesDeItineraire;
    }
    
    private List<String> merge(List<String> list1, List<String> list2) {
	if ((list1 == null) || (list2 == null))
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE); // ERROR
	if (list1.size() != list2.size())
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE); // ERROR
	List<String> list = new ArrayList<String>();
	for (int i = 0; i < list1.size(); i++) {
	    if ((list1.get(i) == null) || (list1.get(i) == null))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE); // ERROR
	    if ((list1.get(i).length() > 0) || (list2.get(i).length() > 0))
		if ((list1.get(i).length() > 0) && (list2.get(i).length() > 0))
		    if (!list1.get(i).equals(list2.get(i)))
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE); // ERROR
		    else
			list.add(list1.get(i));
		else
		    if (list1.get(i).length() > 0)
			list.add(list1.get(i));
		    else
			list.add(list2.get(i));
	    else
		list.add("");
	}
	return list;
    }
    
    private List<Mission> getMissions(Itineraire itineraire) {
	List<Mission> missions = new ArrayList<Mission>();
	for (Mission mission : missionsDeLigne)
	    if (mission.getRouteId().equals(itineraire.getObjectId()))
		missions.add(mission);
	return missions;
    }
    
    private List<Course> getCourses(Mission mission) {
	List<Course> courses = new ArrayList<Course>();
	for (Course course : coursesDeLigne)
	    if (course.getJourneyPatternId().equals(mission.getObjectId()))
		courses.add(course);
	return courses;
	}
    
    private Itineraire getItineraire(Course course) {
	if (course == null)
	    return null;
	String missionObjectId = course.getJourneyPatternId();
	if (missionObjectId == null)
	    return null;
	Mission missionDeCourse = null;
	for (Mission mission : missionsDeLigne)
	    if (missionObjectId.equals(mission.getObjectId())) {
		missionDeCourse = mission;
		break;
	    }
	if (missionDeCourse == null)
	    return null;
	String itineraireObjectId = missionDeCourse.getRouteId();
	if (itineraireObjectId == null)
	    return null;
	for (Itineraire itineraire : itinerairesDeLigne)
	    if (itineraireObjectId.equals(itineraire.getObjectId()))
		return itineraire;
	return null;
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
}
