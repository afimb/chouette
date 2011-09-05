package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurCourse;

public class LecteurCourse implements ILecteurCourse {
	
    private static final Logger                     logger                = Logger.getLogger(LecteurCourse.class);
    private static final String                     ALLER                 = "ALLER";
    private static final String                     RETOUR                = "RETOUR";
    private static final Set<String>		    PARTICULARITES_VALIDES;
    static {
        PARTICULARITES_VALIDES = new HashSet<String>();
        PARTICULARITES_VALIDES.add("TAD");
    }
    
    private static       int                        len;
    private              Map<Ligne, List<Course>>   coursesParLigne;
    private              Ligne                      ligneEnCours;
    private              List<Course>               coursesEnCours;
    private              Set<Course>                coursesAller;
    private              Set<Course>                coursesRetour;
    private              Set<String>                cellulesNonRenseignees;
    private              Set<String>                titres;
    private              int                        colonneDesTitres;      // 7
    private              IIdentificationManager     identificationManager; //
    private              String                     cleDirection;          // "Direction (ALLER/RETOUR)"
    private              String                     cleCalendrier;         // "Calendriers d'application"
    private              String                     cleParticularite;      // "Particularités"
    private              String                     cleListe;              // "Liste des arrêts"
    private              String                     cleX;                  // "X"
    private              String                     cleY;                  // "Y"
    private              String                     cleLatitude;           // "Latitude"
    private              String                     cleLongitude;          // "Longitude"
    private              String                     cleAdresse;            // "Adresse"
    private              String                     cleCode;               // "Code Postal"
    private              String                     cleZone;               // "Zone"
    private              Map<String, TableauMarche> caldendriersParRef;
	
    @Override
    public List<Course> getCourses() {
	List<Course> allCourses = new ArrayList<Course>();
	for (List<Course> courses : coursesParLigne.values())
	    allCourses.addAll(courses);
	return allCourses;
    }
    
    @Override
    public List<Course> getCourses(Ligne ligne) {
	return coursesParLigne.get(ligne);
    }
    
    @Override
    public List<Course> getCoursesEnCours() {
	return coursesEnCours;
    }
    
    @Override
    public Set<Course> getCoursesAller() {
	return coursesAller;
    }
    
    @Override
    public Set<Course> getCoursesRetour() {
	return coursesRetour;
    }
    
    @Override
    public void reinit() {
	coursesParLigne = new HashMap<Ligne, List<Course>>();
	coursesAller = null;
	coursesRetour = null;
	ligneEnCours = null;
	coursesEnCours = null;
	len = 1;
	titres = new HashSet<String>();
	titres.add(cleDirection);
	titres.add(cleCalendrier);
	titres.add(cleParticularite);
	titres.add(cleListe);
	cellulesNonRenseignees = new HashSet<String>(titres);
    }
    
    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length < colonneDesTitres+1))
	    return false;
	String titre = ligneCSV[colonneDesTitres];
	if (titre == null)
	    return false;
	return titres.contains(titre);
    }
	
    private boolean isTitreNouvelleDonnee(String titre) {
	return cleDirection.equals(titre);
    }
    
    @Override
    public void lire(String[] ligneCSV, Ligne ligne) {
	if (ligneCSV.length < colonneDesTitres+2)
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_COUNT,ligneCSV.length,(colonneDesTitres+2));
	String titre = ligneCSV[colonneDesTitres];
	if (isTitreNouvelleDonnee(titre)) {
	    validerCompletudeDonneeEnCours();
	    cellulesNonRenseignees = new HashSet<String>(titres);
	    coursesAller = new HashSet<Course>();
	    coursesRetour = new HashSet<Course>();
	    ligneEnCours = ligne;
	    coursesEnCours = new ArrayList<Course>();
	    boolean finDeLigne = false;
	    for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
		String value = ligneCSV[i];
		if ((value == null) || (value.trim().length() == 0))
		    finDeLigne = true;
		else {
		    if (finDeLigne)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_POSITION,value);
		    if ((value.trim().equals(ALLER)) || (value.trim().equals(RETOUR)))
			coursesEnCours.add(createCourse(value.trim(), ligne));
		    else
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.VEHICLEJOURNEY_ORIENTATION,value.trim());
		}
	    }
	    if (!coursesEnCours.isEmpty())
		coursesParLigne.put(ligneEnCours, coursesEnCours);
	}
	if (!cellulesNonRenseignees.remove(titre))
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.DUPLICATE_LINE,titre);
	if (cleCalendrier.equals(titre)) {
	    boolean finDeLigne = false;
	    for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
		String value = ligneCSV[i];
		if ((value == null) || (value.trim().length() == 0))
		    finDeLigne = true;
		else {
		    if (finDeLigne)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_POSITION,value);
		    else
			associerCalendriersCourse(value, coursesEnCours, i-(colonneDesTitres+1));
		}
	    }
	}
	if (cleParticularite.equals(titre)) {
	    for (int i = colonneDesTitres+1; i < ligneCSV.length; i++) {
		if (i >= colonneDesTitres + 1 + coursesEnCours.size())
		    break;
		String value = ligneCSV[i];
		associerParticulariteCourse(value, coursesEnCours, i-(colonneDesTitres+1));
	    }
	}
	if (cleListe.equals(titre)) {
	    if (!ligneCSV[0].equals(cleX))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleX);
	    if (!ligneCSV[1].equals(cleY))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleY);
	    if (!ligneCSV[2].equals(cleLatitude))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleLatitude);
	    if (!ligneCSV[3].equals(cleLongitude))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleLongitude);
	    if (!ligneCSV[4].equals(cleAdresse))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleAdresse);
	    if (!ligneCSV[5].equals(cleCode))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleCode);
	    if (!ligneCSV[6].equals(cleZone))
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.MISSING_DATA,cleZone);
	    for (int i = colonneDesTitres+1; i < ligneCSV.length; i++)
		if (ligneCSV[i] != null)
		    if (ligneCSV[i].trim().length() > 0)
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.UNEXPECTED_DATA,ligneCSV[i].trim());
	}
    }
    
    private Course createCourse(String sens, Ligne ligne) {
	logger.debug("NEW VEHICLE JOURNEY");
	Course course = new Course();
	course.setObjectId(identificationManager.getIdFonctionnel("VehicleJourney", ligne.getObjectId().substring(ligne.getObjectId().lastIndexOf(':')+1)+'_'+String.valueOf(len)));
	course.setCreationTime(new Date());
	course.setObjectVersion(1);
	len++;
	if (sens.equals(ALLER))
	    coursesAller.add(course);
	else if (sens.equals(RETOUR))
	    coursesRetour.add(course);
	//course.setComment(comment);
	//course.setCreatorId(creatorId);
	//course.setFacility(facility);
	//course.setId(id);
	//course.setIdItineraire(idItineraire);
	//course.setIdMission(idMission);
	//course.setJourneyPatternId(journeyPatternId);                                   /////////////////
	//course.setNumber(number);
	//course.setPublishedJourneyIdentifier(publishedJourneyIdentifier);
	//course.setPublishedJourneyName(publishedJourneyName);
	//course.setRouteId(routeId);                                                    /////////////////
	//course.setStatusValue(statusValue);
	//course.setTransportMode(transportMode);
	//course.setVehicleTypeIdentifier(vehicleTypeIdentifier);
	return course;
    }
    
    private void associerParticulariteCourse(String value, List<Course> courses, int indiceDeCourse) {
	if (indiceDeCourse >= courses.size())
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.DATA);
	Course course = courses.get(indiceDeCourse);
	String[] aliases = value.split(",");
	StringBuilder particularites = new StringBuilder();
	for (int i = 0; i < aliases.length; i++) {
	    String alias = aliases[i];
	    if (alias.length() > 0){
		if (!PARTICULARITES_VALIDES.contains(alias))
		    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.UNKNOWN_ALIAS,alias);
		particularites.append(alias).append(",");
	    }
	}
	if (particularites.length() > 0 && particularites.lastIndexOf(",") == particularites.length() -1) {
	    particularites.deleteCharAt(particularites.length()-1);
	}
	course.setVehicleTypeIdentifier(particularites.toString());
	logger.debug("Particularites : "+particularites.toString()+". Course : "+course.getObjectId());
    }
	
    private void associerCalendriersCourse(String value, List<Course> courses, int indiceDeCourse) {
	if (indiceDeCourse >= courses.size())
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.TIMETABLE_VEHICLEJOURNEY);
	String vehicleJourneyId = courses.get(indiceDeCourse).getObjectId();
	String[] aliases = value.split(",");
	for (int i = 0; i < aliases.length; i++) {
	    String alias = aliases[i];
	    logger.debug("TM : "+alias+". Course : "+vehicleJourneyId);
	    if (caldendriersParRef.get(alias) == null)
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.UNKNOWN_ALIAS,alias);
	    caldendriersParRef.get(alias).addVehicleJourneyId(vehicleJourneyId);
	    logger.debug("TM : \""+caldendriersParRef.get(alias).getObjectId());
	}
    }
    
    private void validerCompletudeDonneeEnCours() {
	if (ligneEnCours != null)
	    validerCompletude();
    }
	
    @Override
    public void validerCompletude() {
	if (cellulesNonRenseignees.size() > 0)
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.VEHICLEJOURNEY_MISSINGDATA,cellulesNonRenseignees);
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
    
    public void setCleDirection(String cleDirection) {
	this.cleDirection = cleDirection;
    }
    
    public String getCleDirection() {
	return cleDirection;
    }
    
    public void setcleCalendrier(String cleCalendrier) {
	this.cleCalendrier = cleCalendrier;
    }
    
    public String getCleCalendrier() {
	return cleCalendrier;
    }
    
    public void setCleParticularite(String cleParticularite) {
	this.cleParticularite = cleParticularite;
    }
    
    public String getCleParticularite() {
	return cleParticularite;
    }
    
    public void setCleListe(String cleListe) {
	this.cleListe = cleListe;
    }
    
    public String getCleListe() {
	return cleListe;
    }
    
    public int getColonneDesTitres() {
	return colonneDesTitres;
    }
    
    public void setColonneDesTitres(int colonneDesTitres) {
	this.colonneDesTitres = colonneDesTitres;
    }
    
    public Map<String, TableauMarche> getTableauxMarchesParRef() {
	return caldendriersParRef;
    }
    
    @Override
    public void setTableauxMarchesParRef(Map<String, TableauMarche> caldendriersParRef) {
	this.caldendriersParRef = caldendriersParRef;
    }
    
    public void setCleX(String cleX) {
	this.cleX = cleX;
    }
    
    public String getCleX() {
	return cleX;
    }
    
    public void setCleY(String cleY) {
	this.cleY = cleY;
    }
    
    public String getCleY() {
	return cleY;
    }
    
    public void setCleLatitude(String cleLatitude) {
	this.cleLatitude = cleLatitude;
    }
    
    public String getCleLatitude() {
	return cleLatitude;
    }
    
    public void setCleLongitude(String cleLongitude) {
	this.cleLongitude = cleLongitude;
    }
    
    public String getCleLongitude() {
	return cleLongitude;
    }
    
    public void setCleAdresse(String cleAdresse) {
	this.cleAdresse = cleAdresse;
    }
    
    public String getCleAdresse() {
	return cleAdresse;
    }
    
    public void setCleCode(String cleCode) {
	this.cleCode = cleCode;
    }
    
    public String getCleCode() {
	return cleCode;
    }
    
    public void setCleZone(String cleZone) {
	this.cleZone = cleZone;
    }
    
    public String getCleZone() {
	return cleZone;
    }
}
