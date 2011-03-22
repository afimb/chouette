package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurHoraire;
import fr.certu.chouette.service.importateur.commun.CodeIncident;
import fr.certu.chouette.service.importateur.commun.ServiceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurHoraire extends Lecteur implements ILecteurHoraire {

    private static final Logger                                                  logger                = Logger.getLogger(LecteurHoraire.class);
    private              Map<String, Course>                                     courses;
    private              Map<String, Itineraire>                                 itineraires;
    private              Map<String, PositionGeographique>                       arretsPhysiques;
    private              Set<String>                                             arretsItineraireCode;
    private static final SimpleDateFormat                                        sdf                   = new SimpleDateFormat("HH:mm:ss");
    private              Map<String, ArretItineraire>                            arretsItineraires;
    private              Map<String, Map<ArretItineraire, Map<Course, Horaire>>> ordre;

    @Override
    public void reinit() {
	arretsItineraireCode             = new HashSet<String>();
        arretsItineraires                = new HashMap<String, ArretItineraire>();
        ordre                            = new HashMap<String, Map<ArretItineraire, Map<Course, Horaire>>>();
    }
    
    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return false;
        if (ligneCSV[0] == null)
            return false;
	return ligneCSV[0].trim().equals(getCleCode());
    }
    
    @Override
    public void lire(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length == 0))
            return;
        if (ligneCSV.length != 5)
            throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_HORAIRE, "FATAL01006 : Mauvais nombre de champs dans la section '06'.");
        if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
            throw new ServiceException(CodeIncident.NULL_COURSENAME_HORAIRE, "ERROR06001 : Le second champs de la section '06' doit etre non null.");
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
            throw new ServiceException(CodeIncident.NULL_COURSENAME_HORAIRE, "ERROR06002 : Le troisieme champs de la section '06' doit etre non null.");
	if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() == 0))
            throw new ServiceException(CodeIncident.NULL_ARRETPHYSIQUENAME_HORAIRE, "ERROR06003 : Le quatrieme champs de la section '06' doit etre non null.");
	if ((ligneCSV[4] == null) || (ligneCSV[4].trim().length() == 0))
            throw new ServiceException(CodeIncident.NULL_HORAIRE_HORAIRE, "ERROR06003 : Le cinquieme champs de la section '06' doit etre non null.");
        Course course = courses.get(ligneCSV[1].trim());
	if (course == null)
	    throw new ServiceException(CodeIncident.INVALIDE_COURSENAME_HORAIRE, "ERROR06101 : Le second champs de la section '06' doit etre egal au quatorzieme champs dans une ligne de la section '05'.");
        Itineraire itineraire = itineraires.get(ligneCSV[2].trim());
	if (itineraire == null)
	    throw new ServiceException(CodeIncident.UNKNOWN_ITINERAIRENAME_HORAIRE, "ERROR06102 : Le troisieme champs de la section '06' doit etre egal au quatrieme champs d'une ligne de la section '04'.");
        if (!course.getRouteId().equals(itineraire.getObjectId()))
            throw new ServiceException(CodeIncident.INVALIDE_ITINERAIRE_COURSE, "ERROR06103 : Le couple second et troisieme champs de la section '06' doit etre egal a un couple de quatorzieme et quinzieme champs dans une ligne de la section '05'.");
        PositionGeographique arretPhysique = arretsPhysiques.get(ligneCSV[3].trim());
	if (arretPhysique == null)
	    throw new ServiceException(CodeIncident.INVALIDE_ARRETPHYSIQUENAME_HORAIRE, "ERROR06104 : Le quatrieme champs de la section '06' doit etre egal au deuxieme champs d'une ligne de la section '02'.");
        Date departureTime = null;
	try {
            departureTime = sdf.parse(ligneCSV[4].trim());
        }
        catch (ParseException e) {
            throw new ServiceException(CodeIncident.INVALIDE_DATEDEPART_HORAIRE, "ERROR06104 : Le cinquieme champs de la section '06' doit etre un horaire valide au format 'hh:mm:ss'.");
        }

        String key0 = ligneCSV[1].trim() + ";" + ligneCSV[2].trim() + ";" + ligneCSV[3].trim() + ";" + ligneCSV[4].trim();
        if (!arretsItineraireCode.add(key0))
            return; //Ligne en double dans les sources
        String key1 = ligneCSV[1].trim() + ";" + ligneCSV[2].trim() + ";" + ligneCSV[3].trim();
        String dollars = "";
	while (!arretsItineraireCode.add(key1+dollars))
	    dollars += "$"; //Nouvel arretItineraire sur le couple (itineraire, arretPhysique) : course en boucle
        
        String key2 = ligneCSV[2].trim() + ";" + ligneCSV[3].trim();
        ArretItineraire arretItineraire = arretsItineraires.get(key2 + dollars);
        if (arretItineraire == null) {
            logger.debug("CREATION D'ARRET ITINERAIRE "+key2 + dollars);
            arretItineraire = new ArretItineraire();
            arretItineraire.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "StopPoint", toTrident(key2 + dollars)));
            arretItineraire.setObjectVersion(1);
            arretItineraire.setCreationTime(new Date(System.currentTimeMillis()));
            arretItineraire.setContainedIn(arretPhysique.getObjectId());
            arretItineraire.setName(arretPhysique.getName());
            arretsItineraires.put(key2 + dollars, arretItineraire);
            arretItineraire.setPosition(-1);
        }
        else
            logger.debug("L'ARRET ITINERAIRE "+key2+" EXISTE DEJA.");

        Horaire horaire = new Horaire();
	horaire.setStopPointId(arretItineraire.getObjectId());
        horaire.setVehicleJourneyId(course.getObjectId());
        horaire.setArrivalTime2(departureTime);
        horaire.setDepartureTime2(departureTime);
        horaire.setDepart(false);

        if (ordre.get(key2) == null)
            ordre.put(key2, new HashMap<ArretItineraire, Map<Course, Horaire>>());
        if (ordre.get(key2).get(arretItineraire) == null)
            ordre.get(key2).put(arretItineraire, new HashMap<Course, Horaire>());
        ordre.get(key2).get(arretItineraire).put(course, horaire);
    }
    
    @Override
    public void setCourseParNumber(Map<String, Course> courses) {
	this.courses = courses;
    }
    
    @Override
    public void setItineraireParNumber(Map<String, Itineraire> itineraires) {
	this.itineraires = itineraires;
    }
    
    @Override
    public void setArretsPhysiquesParRegistration(Map<String, PositionGeographique> arretsPhysiques) {
	this.arretsPhysiques = arretsPhysiques;
    }
    
    @Override
    public void completion() {
	arretsItineraireCode.clear();
        arretsItineraires.clear();
    }

    @Override
    public Map<String, Map<ArretItineraire, Map<Course, Horaire>>> getOrdre() {
        return ordre;
    }
}
