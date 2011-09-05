package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurHoraire;

public class LecteurHoraire implements ILecteurHoraire {
    
    private static final Logger                     logger                 = Logger.getLogger(LecteurHoraire.class);
    private static final SimpleDateFormat           sdf                    = new SimpleDateFormat("HH:mm");
    private              int                        colonneDesTitres;      // 7
    private              IIdentificationManager     identificationManager; // 
    private              List<Course>               coursesEnCours;
    private              Map<Course, List<Horaire>> horaires;
    private              Map<Course, List<String>>  arretsPhysiques;      
    
    @Override
    public Map<Course, List<Horaire>> getHoraires() {
	return horaires;
    }
    
    @Override
    public Map<Course, List<String>> getArretsPhysiques() {
	return arretsPhysiques;
    }
    
    @Override
    public void reinit() {
	horaires = new HashMap<Course, List<Horaire>>();
	init();
    }
    
    @Override
    public void init() {
	arretsPhysiques = new HashMap<Course, List<String>>();
    }
    
    @Override
    public void lire(String[] ligneCSV, List<Course> courses) {
	coursesEnCours = courses;
	boolean arretSansHoraire = true;
	for (int i = colonneDesTitres + 1; i < ligneCSV.length; i++) {
	    if (coursesEnCours.size() <= i-(colonneDesTitres+1))
		return;
	    Course course = coursesEnCours.get(i-(colonneDesTitres+1));
	    if (course == null)
		throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.TIMETABLE_VEHICLEJOURNEY);
	    if (arretsPhysiques.get(course) == null)
		arretsPhysiques.put(course, new ArrayList<String>());
	    if (ligneCSV[i] != null)
		if (ligneCSV[i].trim().length() > 0) {
		    Date date = null;
		    try {
			date = sdf.parse(ligneCSV[i]);
		    }
		    catch(ParseException e) {
			String liText = "";
			for (int j = 0; j < ligneCSV.length; j++)
			    liText += ligneCSV[j];
			throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.DATE_TYPE,liText); // Invalide format ...
		    }
		    arretSansHoraire = false;
		    logger.debug("COURSE : "+(i-(colonneDesTitres+1))+", HORAIRE : "+sdf.format(date));
		    Horaire horaire = new Horaire();
		    //horaire.setArrivalTime(date);
		    horaire.setDepartureTime(date);
		    horaire.setVehicleJourneyId(course.getObjectId());
		    if (horaires.get(course) == null)
			horaires.put(course, new ArrayList<Horaire>());
		    horaires.get(course).add(horaire);
		    arretsPhysiques.get(course).add(ligneCSV[colonneDesTitres]);
		}
		else
		    arretsPhysiques.get(course).add("");
	    else
		arretsPhysiques.get(course).add("");
	}
	if (ligneCSV.length < (colonneDesTitres+1+coursesEnCours.size()))
	    for (int i = ligneCSV.length; i < (colonneDesTitres+1+coursesEnCours.size()); i++) {
		Course course = coursesEnCours.get(i-(colonneDesTitres+1));
		if (course == null)
		    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.TIMETABLE_VEHICLEJOURNEY);
		if (arretsPhysiques.get(course) == null)
		    arretsPhysiques.put(course, new ArrayList<String>());
		arretsPhysiques.get(course).add("");
	    }
	if (arretSansHoraire)
	    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.STOPPOINT_MISSINGPASSINGTIME,ligneCSV[colonneDesTitres]);
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
    
    public int getColonneDesTitres() {
	return colonneDesTitres;
    }
    
    public void setColonneDesTitres(int colonneDesTitres) {
	this.colonneDesTitres = colonneDesTitres;
    }
}
