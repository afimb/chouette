package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurHoraire;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurHoraire implements ILecteurHoraire {
    
    private static final Logger                            logger                 = Logger.getLogger(LecteurHoraire.class);
    private              int                               counter;
    private              IIdentificationManager            identificationManager; // 
    private              String                            cleCode;               // "06"
    private              String                            hastusCode;            // "HastusTUR"
    private              String                            special;               // "SPECIAL"
    private              String                            space;                 // "SPACE"
    private              Map<String, Course>               courseParNom;
    private              Map<String, Mission>              missionParNom;
    private              Map<String, Itineraire>           itineraireParNom;
    private              Map<String, PositionGeographique> arretsPhysiquesParNom;
    private              Map<Itineraire, Map<String, ArretItineraire>> arretsItineraireParItineraire;
    private              Set<String>                       arretsItineraireCode;
    private              Map<String, List<Horaire>>        listHorairesParRegistrationLigne;
    private static final SimpleDateFormat                  sdf                    = new SimpleDateFormat("HH:mm:ss");
    //private              boolean                           firstHalf              = true;
    private              Set<String>                       ensembleCoursesAvecHoraire;
    private              Map<String, String>               objectIdParParentObjectId;
    
    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return false;
	return ligneCSV[0].equals(getCleCode());
    }
    
    private String containedIn = "";
    private Horaire lastHoraire = null;
    private ArretItineraire lastArretItineraire = null;
    
    public void lire(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return;
	if (ligneCSV.length != 5)
	    throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_HORAIRE, "La longeur des lignes dans \"Horaire\" est 5 : "+ligneCSV.length);
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_COURSENAME_HORAIRE, "Le nom de la \"Course\" dans un \"Horaire\" ne doit pas être null.");
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_COURSENAME_HORAIRE, "Le nom de la \"Mission\" dans un \"Horaire\" ne doit pas être null.");
	Course course = courseParNom.get(ligneCSV[2].trim()+"-"+ligneCSV[1].trim());
	if (course == null)
	    throw new ServiceException(CodeIncident.INVALIDE_COURSENAME_HORAIRE, "Il n'y a pas de calendreir d'application pour la course numero \""+ligneCSV[1].trim()+"\" pour le parcours \""+ligneCSV[2].trim()+"\"");
	if (ligneCSV[2].trim().lastIndexOf('-') <= 0)
	    throw new ServiceException(CodeIncident.INVALIDE_COURSENAME_HORAIRE, "Le nom de la \"Course\" dans un \"Horaire\" est invalide : "+ligneCSV[2].trim());
	if (listHorairesParRegistrationLigne.get(ligneCSV[2].trim().substring(0,ligneCSV[2].trim().lastIndexOf('-'))) == null)
	    listHorairesParRegistrationLigne.put(ligneCSV[2].trim().substring(0,ligneCSV[2].trim().lastIndexOf('-')), new ArrayList<Horaire>());
	Itineraire itineraire = itineraireParNom.get(ligneCSV[2].trim());
	if (itineraire == null)
	    throw new ServiceException(CodeIncident.UNKNOWN_ITINERAIRENAME_HORAIRE, "Le nom de l'\"Itineraire\" dans un \"Horaire\" ne doit pas être inconnu : "+ligneCSV[2].trim());
	if (arretsItineraireParItineraire.get(itineraire) == null)
	    arretsItineraireParItineraire.put(itineraire, new HashMap<String, ArretItineraire>());
	Mission mission = missionParNom.get(ligneCSV[2].trim());
	if (mission == null) {
	    mission = new Mission();
	    mission.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "JourneyPattern", toTrident(ligneCSV[2].trim())));
	    mission.setObjectVersion(1);
	    mission.setCreationTime(new Date(System.currentTimeMillis()));
	    mission.setName(ligneCSV[2].trim());
	    mission.setRouteId(itineraire.getObjectId());
	    mission.setComment("Mission du parcours type "+ligneCSV[2].trim());
	    mission.setPublishedName("Mission_"+ligneCSV[2].trim());
	    missionParNom.put(mission.getName(), mission);
	}
	course.setJourneyPatternId(mission.getObjectId());
	course.setRouteId(itineraire.getObjectId());
	
	if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ARRETPHYSIQUENAME_HORAIRE, "Le nom de l'\"ArretPhysique\" dans un \"Horaire\" ne doit pas être null.");
	PositionGeographique arretPhysique = arretsPhysiquesParNom.get(ligneCSV[3].trim());
	if (arretPhysique == null)
	    throw new ServiceException(CodeIncident.INVALIDE_ARRETPHYSIQUENAME_HORAIRE, "Le nom de l'\"ArretPhysique\" dans un \"Horaire\" doit correspondre à un \"ArretPhysique\" : "+ligneCSV[3].trim());
	
	if (!ensembleCoursesAvecHoraire.contains(course.getObjectId())) {
	    containedIn = "";
	    lastHoraire = null;
	    lastArretItineraire = null;
	}
	
	boolean isContigu = false;
	if (objectIdParParentObjectId.get(containedIn) != null && objectIdParParentObjectId.get(arretPhysique.getObjectId()) != null)
	    if (objectIdParParentObjectId.get(arretPhysique.getObjectId()).equals(objectIdParParentObjectId.get(containedIn)))
		isContigu = true;
	
	String key = ligneCSV[1].trim()+";"+ligneCSV[2].trim()+";"+ligneCSV[3].trim(); 
	String dolars = "";
	while (arretsItineraireCode.contains(key+dolars))
	    dolars += "$";
	arretsItineraireCode.add(key+dolars);
	
	ArretItineraire arretItineraire = null;
	if (isContigu) {
	    arretItineraire = lastArretItineraire;
	    String st = itineraire.getObjectId();
	    st = st.substring(st.lastIndexOf(':')+1);
	    st = st + "-" + arretPhysique.getName()+dolars;
	    arretItineraire.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "StopPoint", toTrident(st)));
	    arretItineraire.setObjectVersion(1);
	    arretItineraire.setCreationTime(new Date(System.currentTimeMillis()));
	    arretItineraire.setContainedIn(arretPhysique.getObjectId());
	    arretItineraire.setName(arretPhysique.getName()+"_"+counter);
	}
	else {
	    arretItineraire = arretsItineraireParItineraire.get(itineraire).get(arretPhysique.getName()+dolars);
	    if (arretItineraire == null) {
		arretItineraire = new ArretItineraire();
		String st = itineraire.getObjectId();
		st = st.substring(st.lastIndexOf(':')+1);
		st = st + "-" + arretPhysique.getName()+dolars;
		arretItineraire.setObjectId(identificationManager.getIdFonctionnel(hastusCode, "StopPoint", toTrident(st)));
		arretItineraire.setObjectVersion(1);
		arretItineraire.setCreationTime(new Date(System.currentTimeMillis()));
		arretItineraire.setContainedIn(arretPhysique.getObjectId());
		arretItineraire.setName(arretPhysique.getName()+"_"+counter);
		int position = arretsItineraireParItineraire.get(itineraire).size();
		arretItineraire.setPosition(position);
		arretsItineraireParItineraire.get(itineraire).put(arretPhysique.getName()+dolars, arretItineraire);
	    }
	}
	
	Horaire horaire = null;
	if (isContigu)
	    horaire = lastHoraire;
	else
	    horaire = new Horaire();
	horaire.setStopPointId(arretItineraire.getObjectId());
	
	if ((ligneCSV[4] == null) || (ligneCSV[4].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_HORAIRE_HORAIRE, "L'\"horaire\" dans un \"Horaire\" ne doit pas être null.");
	try {
	    Date departureTime = sdf.parse(ligneCSV[4].trim());
	    if (isContigu)
		horaire.setArrivalTime2(horaire.getDepartureTime());
	    horaire.setDepartureTime2(departureTime);
	}
	catch (ParseException e) {
	    throw new ServiceException(CodeIncident.INVALIDE_DATEDEPART_HORAIRE, "La date de départ est invalide : "+ligneCSV[4].trim());
	}
	horaire.setVehicleJourneyId(course.getObjectId());
	if (ensembleCoursesAvecHoraire.add(course.getObjectId()))
	    horaire.setDepart(true);
	else
	    if (!isContigu)
		horaire.setDepart(false);
	if (!isContigu)
	    listHorairesParRegistrationLigne.get(ligneCSV[2].trim().substring(0,ligneCSV[2].trim().lastIndexOf('-'))).add(horaire);
	
	containedIn = arretPhysique.getObjectId();
	lastArretItineraire = arretItineraire;
	lastHoraire = horaire;
    }
    
    private String toTrident(String str) {
	if ((str == null) || (str.length() == 0))
	    return "";
	String result = "";
	for (int i = 0; i < str.length(); i++)
	    if (('a' <= str.charAt(i)) && (str.charAt(i) <= 'z') ||
		('A' <= str.charAt(i)) && (str.charAt(i) <= 'Z') ||
		('0' <= str.charAt(i)) && (str.charAt(i) <= '9'))
		result += str.charAt(i);
	    else if ((str.charAt(i) == ' ') || (str.charAt(i) == '\t'))
		result += space;
	    else
		result += special;
	return result;
    }
    
    public void reinit() {
	missionParNom = new HashMap<String, Mission>();
	arretsItineraireParItineraire = new HashMap<Itineraire, Map<String, ArretItineraire>>();
	listHorairesParRegistrationLigne = new HashMap<String, List<Horaire>>();
	arretsItineraireCode = new HashSet<String>();
	ensembleCoursesAvecHoraire = new HashSet<String>();
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
    
    public String getCleCode() {
	return cleCode;
    }
    
    public void setCleCode(String cleCode) {
	this.cleCode = cleCode;
    }
    
    public int getCounter() {
	return counter;
    }
    
    public void setCounter(int counter) {
	this.counter = counter;
    }
    
    public String getHastusCode() {
	return hastusCode;
    }
    
    public void setHastusCode(String hastusCode) {
	this.hastusCode = hastusCode;
    }
    
    public void setCourseParNom(Map<String, Course> courseParNom) {
	this.courseParNom = courseParNom;
    }
    
    public void setItineraireParNom(Map<String, Itineraire> itineraireParNom) {
	this.itineraireParNom = itineraireParNom;
    }
    
    public void setArretsPhysiquesParNom(Map<String, PositionGeographique> arretsPhysiquesParNom) {
	this.arretsPhysiquesParNom = arretsPhysiquesParNom;
    }
    
    public Map<Itineraire, Map<String, ArretItineraire>> getArretsItineraireParItineraire() {
	return arretsItineraireParItineraire;
    }
    
    public Map<String, Mission> getMissionParNom() {
	return missionParNom;
    }
    
    public Map<String, List<Horaire>> getListHorairesParRegistrationLigne() {
	return listHorairesParRegistrationLigne;
    }
    
    public List<Horaire> getListHorairesParRegistrationLigne(String registrationLigne) {
	return listHorairesParRegistrationLigne.get(registrationLigne);
    }
    
    public void completion() {
	arretsItineraireCode.clear();
	//arretsPhysiquesParNom.clear();
    }
    
    public String getSpecial() {
	return special;
    }
    
    public void setSpecial(String special) {
	this.special = special;
    }
    
    public String getSpace() {
	return space;
    }
    
    public void setSpace(String space) {
	this.space = space;
    }
    
    public void setObjectIdParParentObjectId(Map<String, String> objectIdParParentObjectId) {
	this.objectIdParParentObjectId = objectIdParParentObjectId;
    }
}
