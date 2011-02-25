package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import chouette.schema.types.PTDirectionType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurItineraire;

public class LecteurItineraire implements ILecteurItineraire {
	
    private static final Logger                       logger                 = Logger.getLogger(LecteurItineraire.class);
    private              IIdentificationManager       identificationManager; // 
    private              int                          counter;
    private              Map<Ligne, List<Itineraire>> itineraires;
    private              Map<String, Itineraire>      itinerairesParCode;
    
    @Override
    public void reinit() {
	counter = 1;
	itineraires = new HashMap<Ligne, List<Itineraire>>();
	init();
    }
    
    public void init() {
	itinerairesParCode = new HashMap<String, Itineraire>();
    }
    
    @Override
    public Map<Ligne, List<Itineraire>> getItineraires() {
	return itineraires;
    }
    
    public Map<String, Itineraire> getItineraireParCode() {
	return itinerairesParCode;
    }
    
    @Override
    public void lire(List<Course> courses, Set<Course> coursesAller, Set<Course> coursesRetour, Map<String, Mission> missions, Ligne ligne) {
	init();
	itineraires.put(ligne, new ArrayList<Itineraire>());
	String[] codes = missions.keySet().toArray(new String[0]);
	Set<String> maxCodes = new HashSet<String>();
	for (int i = 0; i < codes.length; i++) {
	    String code1 = codes[i];
	    boolean isMax = true;
	    for (int j = 0; j < codes.length; j++)
		if (i != j) {
		    String code2 = codes[j];
		    if (subCode(code1, code2) == -1) {
			isMax = false;
			break;
		    }
		}
	    if (isMax)
		maxCodes.add(code1);
	}
	for (String code : maxCodes) {
	    Itineraire itineraire = new Itineraire();
	    //itineraire.setDirection(direction);
	    itineraire.setObjectId(identificationManager.getIdFonctionnel("ChouetteRoute", String.valueOf(counter++)));
	    logger.debug("Creation itineraire : "+itineraire.getObjectId());
	    logger.debug("\tAjout mission : "+code);
	    missions.get(code).setRouteId(itineraire.getObjectId());
	    itineraires.get(ligne).add(itineraire);
	    itinerairesParCode.put(code, itineraire);
	    for (int j = 0; j < codes.length; j++) {
		String code2 = codes[j];
		if (!code.equals(code2))
		    if (subCode(code2, code) == -1)
			if (missions.get(code2).getRouteId() == null) {
			    logger.debug("\tAjout mission : "+code2);
			    missions.get(code2).setRouteId(itineraire.getObjectId());
			}
	    }
	}
	for (Itineraire itineraire : itineraires.get(ligne))
	    for (int j = 0; j < codes.length; j++) {
		String code2 = codes[j];
		for (Course course : courses) {
		    logger.debug("XXX Course \""+course.getObjectId()+"\" Mission \""+course.getJourneyPatternId()+"\"");
		    if (course.getJourneyPatternId().equals(missions.get(code2).getObjectId()) &&
			missions.get(code2).getRouteId().equals(itineraire.getObjectId())) {
			if (itineraire.getDirection() == null)
			    for (Course cr : coursesAller)
				if (cr == course) {
				    itineraire.setDirection(PTDirectionType.A);
				    break;
				}
			if (itineraire.getDirection() == null)
			    for (Course cr : coursesRetour)
				if (cr == course) {
				    itineraire.setDirection(PTDirectionType.A);
				    break;
				}
			course.setRouteId(itineraire.getObjectId());
			logger.debug("YYY Itineraire : \""+itineraire.getObjectId()+"\" Mission : \""+
				     missions.get(code2).getObjectId()+"\" Course : \""+course.getObjectId()+"\"");
		    }
		}
	    }
    }
    
    private int subCode(String code1, String code2) {
	if ((code1 != null) && (code2 != null) && (code1.length() == code2.length())) {
	    boolean inf = false;
	    boolean sup = false;
	    for (int i = 0; i < code1.length(); i++)
		if (code1.charAt(i) < code2.charAt(i)) {
		    if (sup)
			return 0;
		    inf = true;
		}
		else if (code1.charAt(i) > code2.charAt(i)) {
		    if (inf)
			return 0;
		    sup = true;
		}
	    if (inf)
		return -1;
	    if (sup)
		return 1;
	}
	return 0;
    }
    
    public IIdentificationManager getIdentificationManager() {
	return identificationManager;
    }
    
    public void setIdentificationManager(IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
