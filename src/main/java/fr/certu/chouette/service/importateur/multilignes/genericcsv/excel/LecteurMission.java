package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurMission;

public class LecteurMission implements ILecteurMission {
	
    private static final Logger                            logger                 = Logger.getLogger(LecteurMission.class);
    private              IIdentificationManager            identificationManager; // 
    private              Map<String, Mission>              missions;
    private              Map<Ligne, List<Mission>>         missionsParLigne;
    private static       int                               counter;
    
    @Override
    public Map<Ligne, List<Mission>> getMissions() {
	return missionsParLigne;
    }
    
    @Override
    public Map<String, Mission> getMissionByCode() {
	return missions;
    }
    
    @Override
    public void reinit() {
	missionsParLigne = new HashMap<Ligne, List<Mission>>();
	init();
	counter = 0;
    }
    
    public void init() {
	missions = new HashMap<String, Mission>();
    }
	
    @Override
    public void lire(Map<Course, List<String>> arretsPhysiquesParCourse, Ligne ligne) {
	logger.debug("CREATION DES MISSIONS.");
	init();
	if (missionsParLigne.get(ligne) == null)
	    missionsParLigne.put(ligne, new ArrayList<Mission>());
	Set<Course> courses = arretsPhysiquesParCourse.keySet();
	for (Course course : courses) {
	    List<String> arretsPhysiques = arretsPhysiquesParCourse.get(course);
	    String code = "";
	    for (String arretPhysique : arretsPhysiques)
		if (arretPhysique.length() == 0)
		    code += "0";
		else
		    code += "1";
	    Mission mission = missions.get(code);
	    if (mission == null) {
		mission = new Mission();
		missionsParLigne.get(ligne).add(mission);
		mission.setObjectId(identificationManager.getIdFonctionnel("JourneyPattern", String.valueOf(counter++)));
		missions.put(code, mission);
	    }
	    course.setJourneyPatternId(mission.getObjectId());
	}
	logger.debug("FIN DE CREATION DES MISSIONS.");
    }
    
    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
	return true;
    }
    
    @Override
    public void validerCompletude() {
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
	this.identificationManager = identificationManager;
    }
}
