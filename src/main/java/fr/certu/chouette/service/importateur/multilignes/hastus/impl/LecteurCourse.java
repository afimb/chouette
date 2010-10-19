package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurCourse;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class LecteurCourse extends Lecteur implements ILecteurCourse {
    
    private static final Logger                            logger                  = Logger.getLogger(LecteurCourse.class);
    private              Map<String, Ligne>                ligneParRegistration;   /// Ligne par registration (LecteurLigne)
    private              Map<String, PositionGeographique> zones;                  /// PositionGeographique (non arrêt physique) par registrationNumber
    private              Map<String, Itineraire>           itineraireParNumber;    /// Itineraire par number (<Ligne.registrationNumber>-<integer>)
    private              Map<Course, Ligne>                ligneParCourse;         /// Ligne par Course
    private              Map<String, Course>               courseParNumber;        ///
    private              Map<String, TableauMarche>        tableauMarcheParValeur; /// 
    private              Map<Ligne,List<TableauMarche>>    tableauxMarchesParLigne;///
    private static final SimpleDateFormat                  sdf1                    = new SimpleDateFormat("ddMMyy");
    private static final SimpleDateFormat                  sdf2                    = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat                  sdf3                    = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void reinit() {
	ligneParCourse          = new HashMap<Course, Ligne>();
	tableauMarcheParValeur  = new HashMap<String, TableauMarche>();
	courseParNumber         = new HashMap<String, Course>();
	tableauxMarchesParLigne = new HashMap<Ligne, List<TableauMarche>>();
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
	if (ligneCSV.length != 18)
	    throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_COURSE, "FATAL01005 : Mauvais nombre de champs dans la section '05'.");
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_COURSE, "ERROR05001 : Le second champs de la section '05' doit etre non null.");
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_LIGNE_COURSE, "ERROR05002 : Le troisieme champs de la section '05' doit etre non null.");
	if ((ligneCSV[10] == null) || (ligneCSV[10].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_JOURSVALIDITE_COURSE, "ERROR05003 : Le onzieme champs de la section '05' doit etre non null.");
	if ((ligneCSV[11] == null) || (ligneCSV[11].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_DATEDEBUT_COURSE, "ERROR05004 : Le trezieme champs de la section '05' doit etre non null.");
	if ((ligneCSV[12] == null) || (ligneCSV[12].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_DATEFIN_COURSE, "ERROR05005 : Le quatorzieme champs de la section '05' doit etre non null.");
	if ((ligneCSV[13] == null) || (ligneCSV[13].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_NAME_COURSE, "ERROR05006 : Le quinzieme champs de la section '05' doit etre non null.");
	if ((ligneCSV[14] == null) || (ligneCSV[14].trim().length() == 0))
            throw new ServiceException(CodeIncident.NULL_ITINERAIRE_COURSE, "ERROR05007 : Le seizieme champs de la section '05' doit etre non null.");
        int number = -1;
        try {
            number = Integer.parseInt(ligneCSV[13].trim());
        }
        catch(NumberFormatException e) {
            throw new ServiceException(CodeIncident.NULL_NAME_COURSE, "ERROR05006 : Le quinzieme champs de la section '05' doit etre un entier.");
        }
        Ligne ligne = ligneParRegistration.get(ligneCSV[2].trim());
	if (ligne == null)
	    throw new ServiceException(CodeIncident.INVALIDE_LIGNE_COURSE, "ERROR05101 : Le troisieme champs de la section '05' doit etre egal au sixieme (ou huitieme si non nul) champs d'une ligne de la section '03'.");
        Itineraire itineraire = itineraireParNumber.get(ligneCSV[14].trim());
        if (itineraire == null)
	    throw new ServiceException(CodeIncident.INVALIDE_ITINERAIRE_COURSE, "ERROR05102 : Le quinzieme champs de la section '05' doit etre egal au quatrieme champs d'une ligne de la section '04'. "+ligneCSV[14].trim());

        if ((ligneCSV[3] != null) && (ligneCSV[3].trim().length() > 0))
            ;//Numero de parcours type
        if ((ligneCSV[4] != null) && (ligneCSV[4].trim().length() > 0))
            if (zones.get(ligneCSV[4].trim()) == null)
                ;//arret de depart
        if ((ligneCSV[5] != null) && (ligneCSV[5].trim().length() > 0))
            ;//horaire de depart de au premier arret
        if ((ligneCSV[6] != null) && (ligneCSV[6].trim().length() > 0))
            ;//00
        if ((ligneCSV[7] != null) && (ligneCSV[7].trim().length() > 0))
            ;//horaire d'arrivee de au premier arret
        if ((ligneCSV[8] != null) && (ligneCSV[8].trim().length() > 0))
            ;//00
        if ((ligneCSV[9] != null) && (ligneCSV[9].trim().length() > 0))
            if (zones.get(ligneCSV[9].trim()) == null)
                ;//arret de depart
	String joursValides = ligneCSV[10].trim();
	if (!joursValides.matches("[01]+"))
	    throw new ServiceException(CodeIncident.INVALIDE_JOURSVALIDITE_COURSE, "ERROR05105 : Le onzieme champs de la section '05' est compose exclusivement de '0' et de '1'.");
        int length = joursValides.length();
        int firstIndex = joursValides.indexOf('1');
        if (firstIndex < 0)
            throw new ServiceException(CodeIncident.INVALIDE_JOURSVALIDITE_COURSE, "ERROR05105 : Le onzieme champs de la section '05' contient au moins une occurence de '1'.");
        int lastIndex = joursValides.lastIndexOf('1');
        String value = joursValides.substring(firstIndex, lastIndex+1);
	Date debut = null;
	Date fin = null;
	try {
	    debut = sdf2.parse(ligneCSV[11].trim());
	}
	catch(ParseException pe) {
	    throw new ServiceException(CodeIncident.INVALIDE_DATEDEBUT_COURSE, "ERROR05103 : Le douzieme champs de la section '05' doit etre une date valide au format 'jj/mm/aaa'.");
	}
	try {
	    fin = sdf2.parse(ligneCSV[12].trim());
	}
	catch(ParseException pe) {
	    throw new ServiceException(CodeIncident.INVALIDE_DATEFIN_COURSE, "ERROR05103 : Le treizieme champs de la section '05' doit etre une date valide au format 'jj/mm/aaa'.");
	}
	if (debut.after(fin))
	    throw new ServiceException(CodeIncident.INVALIDE_DATES_COURSE, "ERROR05104 : Le douzieme champs de la section '05' doit etre une date posterieure a la date du treizieme champs.");
        Calendar cal = Calendar.getInstance();
        cal.setTime(debut);
        cal.add(Calendar.DAY_OF_YEAR, firstIndex);
        debut = cal.getTime();
        cal = Calendar.getInstance();
        cal.setTime(fin);
        cal.add(Calendar.DAY_OF_YEAR,  (lastIndex+1)-length);
        fin = cal.getTime();
        int len1 = value.length();
        int len2 = numberOfDaysBetween(debut, fin);
        if (len1 != len2)
	    throw new ServiceException(CodeIncident.INVALIDE_DATES_COURSE, "ERROR05105 : Le onzieme champs de la section '05' doit etre de longueur egal au nombre de jours entre la date douzieme champs et la date du treizieme champs. "+len1+" != "+len2);

        BigInteger bigInt = new BigInteger(value, 2);
	String key = sdf1.format(debut)+bigInt.toString(10)+sdf1.format(fin);
        TableauMarche tableauMarche = tableauMarcheParValeur.get(key);
	if (tableauMarche == null) {
	    tableauMarche = new TableauMarche();
	    tableauMarcheParValeur.put(key, tableauMarche);
	    tableauMarche.setObjectVersion(1);
	    tableauMarche.setCreationTime(new Date(System.currentTimeMillis()));
	    tableauMarche.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "Timetable", key));
	    tableauMarche.setComment("FROM "+sdf3.format(debut)+" TO "+sdf3.format(fin));
	    tableauMarche.ajoutDate(debut);
            cal = Calendar.getInstance();
	    cal.setTime(debut);
	    for (int i = 1; i < value.length(); i++) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date aDate = cal.getTime();
		if (value.charAt(i) == '1')
		    tableauMarche.ajoutDate(aDate);
	    }
	}

        logger.debug("CREATION DE COURSES "+ligneCSV[1].trim()+" : "+ligneCSV[13].trim()+" ("+ligneCSV[14].trim()+").");
        Course course = new Course();
        course.setNumber(number);
        course.setPublishedJourneyName(ligneCSV[1].trim());
        course.setObjectId(getIdentificationManager().getIdFonctionnel(getHastusCode(), "VehicleJourney", toTrident(ligneCSV[1].trim()+"-"+ligneCSV[14].trim()+"-"+ligneCSV[13].trim())));
        course.setObjectVersion(1);
        course.setCreationTime(new Date(System.currentTimeMillis()));
        boolean invalideTransportMode = false;
        course.setTransportMode(ligne.getTransportModeName());
        if ((ligneCSV[15] == null) || (ligneCSV[15].trim().length() <= 0))
            invalideTransportMode = true;
        else if(ligneCSV[15].trim().equals("Autocar"))
            course.setTransportMode(TransportModeNameType.COACH);
        else if(ligneCSV[15].trim().equals("Avion"))
            course.setTransportMode(TransportModeNameType.AIR);
        else if(ligneCSV[15].trim().equals("Bac"))
            course.setTransportMode(TransportModeNameType.WATERBORNE);
        else if(ligneCSV[15].trim().equals("Bus"))
            course.setTransportMode(TransportModeNameType.BUS);
        else if(ligneCSV[15].trim().equals("Ferry"))
            course.setTransportMode(TransportModeNameType.FERRY);
        else if(ligneCSV[15].trim().equals("Marche à pied"))
            course.setTransportMode(TransportModeNameType.WALK);
        else if(ligneCSV[15].trim().equals("Métro"))
            course.setTransportMode(TransportModeNameType.METRO);
        else if(ligneCSV[15].trim().equals("Navette"))
            course.setTransportMode(TransportModeNameType.SHUTTLE);
        else if(ligneCSV[15].trim().equals("RER"))
            course.setTransportMode(TransportModeNameType.LOCALTRAIN);
        else if(ligneCSV[15].trim().equals("TAXI"))
            course.setTransportMode(TransportModeNameType.TAXI);
        else if(ligneCSV[15].trim().equals("TER"))
            course.setTransportMode(TransportModeNameType.LOCALTRAIN);
        else if(ligneCSV[15].trim().equals("Train"))
            course.setTransportMode(TransportModeNameType.TRAIN);
        else if(ligneCSV[15].trim().equals("Train grande ligne"))
            course.setTransportMode(TransportModeNameType.LONGDISTANCETRAIN);
        else if(ligneCSV[15].trim().equals("Tramway"))
            course.setTransportMode(TransportModeNameType.TRAMWAY);
        else if(ligneCSV[15].trim().equals("Trolleybus"))
            course.setTransportMode(TransportModeNameType.TROLLEYBUS);
        else if(ligneCSV[15].trim().equals("Voiture particulière"))
            course.setTransportMode(TransportModeNameType.PRIVATEVEHICLE);
        else if(ligneCSV[15].trim().equals("Autre"))
            course.setTransportMode(TransportModeNameType.OTHER);
        else
            invalideTransportMode = true;
        if ((ligneCSV[16] != null) && (ligneCSV[16].trim().length() > 0))
            if ("TAD".equals(ligneCSV[16].trim()))
                course.setVehicleTypeIdentifier("TAD");
        if ((ligneCSV[17] != null) && (ligneCSV[17].trim().length() > 0))
            course.setComment(ligneCSV[17].trim());
        course.setRouteId(itineraire.getObjectId());
        addCourse(course, ligne, tableauMarche);
        addCourseToTableauMarche(course, ligne, tableauMarche);
        if (invalideTransportMode)
            throw new ServiceException(CodeIncident.INVALIDE_TRANSPORTMODE_LIGNE, "ERROR05201 : Le seizieme champs de la section '05' doit etre parmi \"Autocar\", \"Avion\", \"Bac\", \"Bus\", \"Ferry\", \"Marche à pied\", \"Métro\", \"Navette\", \"RER\", \"Taxi\", \"TER\", \"Train\", \"Train grande ligne\", \"Tramway\", \"Trolleybus\", \"Voiture particulière\", \"Vélo\", \"Autre\".");
    }

    private void addCourse(Course course, Ligne ligne, TableauMarche tableauMarche) {
        Course tmpCourse = courseParNumber.get(""+course.getNumber());
        if (tmpCourse != null)
            if (theSameCourse(course, tmpCourse, ligne))
                return;
            else
                throw new ServiceException(CodeIncident.DUPLICATE_NAME_ITINERAIRE, "ERROR05202 : Deux lignes quelconques de la section '05' sont soit identiques soit avec deux deuxièmes champs distincts.");
        courseParNumber.put(""+course.getNumber(), course);
        ligneParCourse.put(course, ligne);
    }

    private void addCourseToTableauMarche(Course course, Ligne ligne, TableauMarche tableauMarche) {
        for (int i = 0; i < tableauMarche.getVehicleJourneyIdCount(); i++)
            if (tableauMarche.getVehicleJourneyId(i).equals(course.getObjectId()))
                return;
        if (tableauxMarchesParLigne.get(ligne) == null)
            tableauxMarchesParLigne.put(ligne, new ArrayList<TableauMarche>());
        tableauMarche.addVehicleJourneyId(course.getObjectId());
        if (!tableauxMarchesParLigne.get(ligne).contains(tableauMarche))
            tableauxMarchesParLigne.get(ligne).add(tableauMarche);
    }

    private boolean theSameCourse(Course course1, Course course2, Ligne ligne) {
        if ((course1 == null) || (course2 == null))
            if ((course1 == null) && (course2 == null))
                return true;
            else
                return false;
        
        if (course1.getNumber() != course2.getNumber())
            return false;

        if (((course1.getPublishedJourneyName() == null) || (course2.getPublishedJourneyName() == null)) &&
            ((course1.getPublishedJourneyName() != null) || (course2.getPublishedJourneyName() != null)))
            return false;
        if (course1.getPublishedJourneyName() != null)
            if (!course1.getPublishedJourneyName().equals(course2.getPublishedJourneyName()))
                return false;

        if (((course1.getVehicleTypeIdentifier() == null) || (course2.getVehicleTypeIdentifier() == null)) &&
            ((course1.getVehicleTypeIdentifier() != null) || (course2.getVehicleTypeIdentifier() != null)))
            return false;
        if (course1.getVehicleTypeIdentifier() != null)
            if (!course1.getVehicleTypeIdentifier().equals(course2.getVehicleTypeIdentifier()))
                return false;

        if (((course1.getRouteId() == null) || (course2.getRouteId() == null)) &&
            ((course1.getRouteId() != null) || (course2.getRouteId() != null)))
            return false;
        if (course1.getRouteId() != null)
            if (!course1.getRouteId().equals(course2.getRouteId()))
                return false;

        if (((course1.getComment() == null) || (course2.getComment() == null)) &&
            ((course1.getComment() != null) || (course2.getComment() != null)))
            return false;
        if (course1.getComment() != null)
            if (!course1.getComment().equals(course2.getComment()))
                return false;

        if (((course1.getTransportMode() == null) || (course2.getTransportMode() == null)) &&
            ((course1.getTransportMode() != null) || (course2.getTransportMode() != null)))
            return false;
        if (course1.getTransportMode() != null)
            if (course1.getTransportMode().compareTo(course2.getTransportMode()) != 0)
                return false;

        if (ligneParCourse.get(course2) != ligne)
            return false;

        return true;
    }

    private int numberOfDaysBetween(Date debut, Date fin) {
        int number = -1;
        if ((debut == null) || (fin == null))
            return number;
        if (debut.after(fin))
            return number;
        number++;//TODO . Probleme du nombre de jours entre deux dates
        Calendar cal = Calendar.getInstance();
        cal.setTime(debut);
        Date tmp = cal.getTime();
        while (!tmp.after(fin)) {
            number++;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            tmp = cal.getTime();
        }
        return number;
    }

    @Override
    public void setLigneParRegistration(Map<String, Ligne> ligneParRegistration) {
        this.ligneParRegistration = ligneParRegistration;
    }

    @Override
    public void setZones(Map<String, PositionGeographique> zones) {
        this.zones = zones;
    }

    @Override
    public void setItineraireParNumber(Map<String, Itineraire> itineraireParNumber) {
	this.itineraireParNumber = itineraireParNumber;
    }
    
    @Override
    public Map<Course, Ligne> getLigneParCourse() {
        return ligneParCourse;
    }

    @Override
    public Map<String, Course> getCourseParNumber() {
        return courseParNumber;
    }
    
    @Override
    public List<TableauMarche> getTableauxMarches(Ligne ligne) {
        return tableauxMarchesParLigne.get(ligne);
    }

    @Override
    public void completion() {
    }
}
