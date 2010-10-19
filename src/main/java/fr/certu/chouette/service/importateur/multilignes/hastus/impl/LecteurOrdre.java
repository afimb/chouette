package fr.certu.chouette.service.importateur.multilignes.hastus.impl;

import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;
import java.util.Map;
import fr.certu.chouette.service.importateur.multilignes.hastus.ILecteurOrdre;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurOrdre extends Lecteur implements ILecteurOrdre {
    
    private static final Logger                                                  logger                                = Logger.getLogger(LecteurOrdre.class);
    private              Map<String, Map<ArretItineraire, Map<Course, Horaire>>> ordre;
    private              Map<String, Itineraire>                                 itineraires;
    private              Map<String, PositionGeographique>                       arretsPhysiques;
    private              Set<String>                                             lignes;
    private              Map<String, Integer>                                    invalideOrdre;
    
    @Override
    public void reinit() {
        lignes = new HashSet<String>();
        invalideOrdre = new HashMap<String, Integer>();
    }
    
    @Override
    public void lire(String[] ligneCSV) {
	if ((ligneCSV == null) || (ligneCSV.length == 0))
	    return;
	if (ligneCSV.length != 4)
	    throw new ServiceException(CodeIncident.INVALIDE_LONGUEUR_ORDRE, "FATAL01007 : Mauvais nombre de champs dans la section '07'.");
	if ((ligneCSV[1] == null) || (ligneCSV[1].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ITINERAIRENAME_ORDRE, "ERROR07001 : Le second champs de la section '07' doit etre non null.");
	if ((ligneCSV[2] == null) || (ligneCSV[2].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ARRETNAME_ORDRE, "ERROR07002 : Le troisieme champs de la section '07' doit etre non null.");
	if ((ligneCSV[3] == null) || (ligneCSV[3].trim().length() == 0))
	    throw new ServiceException(CodeIncident.NULL_ORDRE_ORDRE, "ERROR07003 : Le quatrieme champs de la section '07' doit etre non null.");
        String key = ligneCSV[1].trim() + ";" + ligneCSV[2].trim() + ";" + ligneCSV[3].trim();
        if (!lignes.add(key))
            return;

        Itineraire itineraire = itineraires.get(ligneCSV[1].trim());
	if (itineraire == null)
	    throw new ServiceException(CodeIncident.UNKNOWN_ITINERAIRENAME_ORDRE, "ERROR07101 : Le second champs de la section '07' doit etre egal au quatrieme champs d'une ligne de la section '04'.");
        PositionGeographique arretPhysique = arretsPhysiques.get(ligneCSV[2].trim());
	if (arretPhysique == null)
	    throw new ServiceException(CodeIncident.INVALIDE_ARRETPHYSIQUENAME_ORDRE, "ERROR07102 : Le troisieme champs de la section '07' doit etre egal au deuxieme champs d'une ligne de la section '02'.");
        int position = -1;
	try {
            position = Integer.parseInt(ligneCSV[3].trim());
            if (position < 0)
                throw new NumberFormatException(""+position);
        }
        catch (NumberFormatException e) {
            throw new ServiceException(CodeIncident.INVALIDE_POSITION_ORDRE, "ERROR07103 : Le quatrieme champs de la section '07' doit etre un entier non negatif.");
        }

        key = ligneCSV[1].trim() + ";" + ligneCSV[2].trim();
        Map<ArretItineraire, Map<Course, Horaire>> horaireCourseArretItineraire = ordre.get(key);
        if ((horaireCourseArretItineraire == null) || (horaireCourseArretItineraire.isEmpty()))
            throw new ServiceException(CodeIncident.INVALIDE_ITINERAIRENAME_ARRETPHYSIQUENAME_ORDRE, "ERROR07104 : Le couple second et troisieme champs de la section '07' doit etre egal a un couple troisieme et quatrieme champs d'une ligne de la section '06'.");
        Set<ArretItineraire> arretsIt = horaireCourseArretItineraire.keySet();
        boolean positionNotSet = true;
        for (ArretItineraire arretIt : arretsIt)
            if (arretIt.getPosition() == -1) {
                arretIt.setPosition(position);
                positionNotSet = false;
                break;
            }
        if (positionNotSet) {
            invalideOrdre.put(key, new Integer(position));
            throw new ServiceException(CodeIncident.INVALIDE_ITINERAIRENAME_ARRETPHYSIQUENAME_ORDRE, "ERROR07104 : Le nombre de triplets distincts second, troisieme  et quatrieme champs de la section '07' doit etre egal au nombre maximal de triplets distincts troisieme, quatrieme et cinquieme champs pour un meme second achamps champs d'une ligne de la section '06'.");
        }
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
    public void setOrdre(Map<String, Map<ArretItineraire, Map<Course, Horaire>>> ordre) {
        this.ordre = ordre;
    }

    @Override
    public void completion() {
        lignes.clear();
        lignes = null;
        for (String key : ordre.keySet()) {
            Map<ArretItineraire, Map<Course, Horaire>> horaireCourseArretItineraire = ordre.get(key);
            Set<ArretItineraire> arretsIts = horaireCourseArretItineraire.keySet();
            if (arretsIts.size() == 1) {
                for (ArretItineraire arretIt : arretsIts)
                    if (arretIt.getPosition() == 0) {
                        Map<Course, Horaire> coursesHoraires = horaireCourseArretItineraire.get(arretIt);
                        Set<Course> courses = coursesHoraires.keySet();
                        for (Course course : courses) {
                            Horaire horaire = coursesHoraires.get(course);
                            horaire.setDepart(true);
                        }
                    }
            }
            else if (arretsIts.size() == 2) {
                for (ArretItineraire arretIt1 : arretsIts) {
                    for (ArretItineraire arretIt2 : arretsIts) {
                        if ((arretIt1 != arretIt2) && (arretIt1.getObjectId().startsWith(arretIt2.getObjectId()))) {
                            if ((arretIt1.getPosition() >= 0) && (arretIt1.getPosition() < arretIt2.getPosition())) {
                                boolean switchPositions = false;
                                Map<Course, Horaire> coursesHoraires1 = horaireCourseArretItineraire.get(arretIt1);
                                Map<Course, Horaire> coursesHoraires2 = horaireCourseArretItineraire.get(arretIt2);
                                Set<Course> courses1 = coursesHoraires1.keySet();
                                Set<Course> courses2 = coursesHoraires1.keySet();
                                for (Course course1 : courses1) {
                                    Horaire horaire1 = coursesHoraires1.get(course1);
                                    Horaire horaire2 = coursesHoraires2.get(course1);
                                    if ((horaire1 != null) && (horaire2 != null))
                                        if (horaire1.getArrivalTime().after(horaire2.getArrivalTime()))
                                            switchPositions = true;
                                        else
                                            if ((horaire1.getArrivalTime().before(horaire2.getArrivalTime())) && (switchPositions))
                                                throw new ServiceException(CodeIncident.INVALIDE_POSITION_ORDRE, "ERROR07105 : L'ordre dans la section '07' est incompatible avec l'horaire de la section '06'.");//ERROR
                                }
                                if (switchPositions) {//SWITCH
                                    int position = arretIt1.getPosition();
                                    arretIt1.setPosition(arretIt2.getPosition());
                                    arretIt2.setPosition(position);
                                }
                                if (arretIt1.getPosition() == 0) {// setDepart(true);
                                    for (Course course1 : courses1) {
                                        Horaire horaire1 = coursesHoraires1.get(course1);
                                        horaire1.setDepart(true);
                                    }
                                }
                                if (arretIt2.getPosition() == 0) {// setDepart(true);
                                    for (Course course2 : courses2) {
                                        Horaire horaire2 = coursesHoraires2.get(course2);
                                        horaire2.setDepart(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String msg = "";
        for (String key1 : invalideOrdre.keySet()) {
            int position = invalideOrdre.get(key1).intValue();
            String subKey = key1.substring(0, key1.indexOf(";")+1);
            for (String key2 : ordre.keySet()) {
                if (key2.startsWith(subKey)) {
                    Map<ArretItineraire, Map<Course, Horaire>> horaireCourseArretItineraire = ordre.get(key2);
                    Set<ArretItineraire> arretsIts = horaireCourseArretItineraire.keySet();
                    for (ArretItineraire arretIt : arretsIts)
                        if (arretIt.getPosition() > position) {
                            arretIt.setPosition(arretIt.getPosition()-1);
                            if (msg.length() > 0)
                                msg += "\n             ";
                            msg += "Le texte \"07;"+key2+";"+(arretIt.getPosition()+1)+"\" est remplace par \"07;"+key2+";"+arretIt.getPosition()+"\".";
                        }
                }
            }
        }
        if (msg.length() > 0)
            throw new ServiceException(CodeIncident.INVALIDE_ITINERAIRENAME_ARRETPHYSIQUENAME_ORDRE, "ERROR07104 : "+msg+".");
    }
}
