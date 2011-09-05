package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LecteurCSVPrincipal implements ILecteurPrincipal {

    private static final Logger logger = Logger.getLogger(LecteurCSVPrincipal.class);
    private static final String JeuCaracteres = "UTF-8";//ISO-8859-1";
    private String repertoire;         // "."
    private char separateur;         // ';'
    private int colonneDesTitres;   // 7
    private ILecteurCalendrier lecteurCalendrier;  // 
    private ILecteurReseau lecteurReseau;      // 
    private ILecteurTransporteur lecteurTransporteur;// 
    private ILecteurLigne lecteurLigne;       // 
    private ILecteurCourse lecteurCourse;      // 
    private ILecteurHoraire lecteurHoraire;     // 
    private ILecteurZone lecteurZone;        // 
    private ILecteurMission lecteurMission;     // 
    private ILecteurItineraire lecteurItineraire;  // 
    private ILecteurArret lecteurArret;       // 
    private String logFileName;        //
    private ResourceBundle bundle;

    @Override
    public void lire(String nom) {
        lireCheminFichier(getCheminfichier(nom));
    }

    @Override
    public List<ILectureEchange> getLecturesEchange() {
        List<Ligne> lignes = lecteurLigne.getLignes();
        List<ILectureEchange> lecturesEchange = new ArrayList<ILectureEchange>();
        if ((lignes == null) || (lignes.isEmpty())) {
            return lecturesEchange;
        }
        List<TableauMarche> tableauxMarche = new ArrayList<TableauMarche>(lecteurCalendrier.getTableauxMarchesParRef().values());
        Reseau reseau = lecteurReseau.getReseau();
        Transporteur transporteur = lecteurTransporteur.getTransporteur();
        for (Ligne ligne : lignes) {
            LectureEchange lectureEchange = new LectureEchange();
            lectureEchange.setReseau(reseau);
            lectureEchange.setTransporteur(transporteur);
            lectureEchange.setLigne(ligne);
            // Les Zones et les arrets physique  // SE RESTREINDRE AUX ZONES / ARRETS PHYSIQUE DE LA LIGNE
            List<String> objectIdZonesGeneriques = new ArrayList<String>();
            lectureEchange.setZonesCommerciales(lecteurZone.getZones(ligne));
            lectureEchange.setArretsPhysiques(lecteurZone.getArretsPhysiques(ligne));
            if (lectureEchange.getZonesCommerciales() != null)///????
            {
                for (PositionGeographique zone : lectureEchange.getZonesCommerciales()) {
                    objectIdZonesGeneriques.add(zone.getObjectId());
                }
            } else {
                logger.error("La ligne " + ligne.getName() + " : " + ligne.getNumber() + " n'a pas de zones comerciales.");
                continue;
            }
            if (lectureEchange.getArretsPhysiques() != null)///????
            {
                for (PositionGeographique arretPhysique : lectureEchange.getArretsPhysiques()) {
                    objectIdZonesGeneriques.add(arretPhysique.getObjectId());
                }
            } else {
                logger.error("La ligne " + ligne.getName() + " : " + ligne.getNumber() + " n'a pas d'arrêts physiques.");
                continue;
            }
            lectureEchange.setObjectIdZonesGeneriques(objectIdZonesGeneriques);
            lectureEchange.setZoneParenteParObjectId(lecteurZone.getZoneParenteParObjectId(ligne));
            //for (String key : lectureEchange.getZoneParenteParObjectId().keySet())
            //logger.error("HHHHHH "+ligne.getPublishedName()+"\t:\t"+lectureEchange.getZoneParenteParObjectId().get(key)+"\t:\t"+key);
            if (lectureEchange.getZonesCommerciales() != null)///????
            {
                for (PositionGeographique zone : lectureEchange.getZonesCommerciales()) {
                    logger.debug("ZONES : " + zone.getName());
                }
            } else {
                continue;
            }
            if (lectureEchange.getArretsPhysiques() != null)///????
            {
                for (PositionGeographique arretPhysique : lectureEchange.getArretsPhysiques()) {
                    logger.debug("ARRETPHYSIQUE : " + arretPhysique.getName());
                }
            } else {
                continue;
            }
            // Missions
            lectureEchange.setMissions(lecteurMission.getMissions().get(ligne));
            // COURSES
            List<Course> courses = lecteurCourse.getCourses(ligne);
            lectureEchange.setCourses(courses);
            // Horaires et TABLEAUX DE MARCHE
            List<Horaire> horaires = new ArrayList<Horaire>();
            List<TableauMarche> _tableauxMarche = new ArrayList<TableauMarche>();
            Set<TableauMarche> _tableauxM = new HashSet<TableauMarche>();
            if (courses == null) {
                continue;
            }
            for (Course course : courses) {
                if (lecteurHoraire.getHoraires() != null) {
                    if (lecteurHoraire.getHoraires().get(course) != null) {
                        horaires.addAll(lecteurHoraire.getHoraires().get(course));
                    }
                }
                for (TableauMarche tableauMarche : tableauxMarche) {
                    for (int i = 0; i < tableauMarche.getVehicleJourneyIdCount(); i++) {
                        if (tableauMarche.getVehicleJourneyId(i).equals(course.getObjectId())) {
                            if (_tableauxM.add(tableauMarche)) {
                                _tableauxMarche.add(tableauMarche);
                            }
                        }
                    }
                }
            }
            lectureEchange.setHoraires(horaires);
            lectureEchange.setTableauxMarche(_tableauxMarche);
            // ITINERAIRES
            lectureEchange.setItineraires(lecteurItineraire.getItineraires().get(ligne));
            // ARRETS ITINERAIRES
            List<ArretItineraire> arretsItineraires = lecteurArret.getArretsItinerairesParLigne().get(ligne);
            lectureEchange.setArrets(arretsItineraires);
            lectureEchange.setItineraireParArret(lecteurArret.getItineraireParArret().get(ligne));

            lecturesEchange.add(lectureEchange);
        }
        return lecturesEchange;
    }

    private void initialisation() {
        bundle = ResourceBundle.getBundle("importCSV");
        lecteurCalendrier.reinit(bundle);
        lecteurReseau.reinit(bundle);
        lecteurTransporteur.reinit(bundle);
        lecteurLigne.reinit(bundle);
        lecteurCourse.reinit();
        lecteurHoraire.reinit();
        lecteurZone.reinit(bundle);
        lecteurMission.reinit();
        lecteurItineraire.reinit();
        lecteurArret.reinit();
    }

    @Override
    public void lireCheminFichier(String chemin) {
        logger.debug("READING CSV FILES FROM PATH : " + chemin);
        File inputFile = null;
        InputStreamReader inputStreamReader = null;
        CSVReader lecteur = null;
        FileWriter fw = null; //To write log infos into
        int counter = 0;
        int lineNumber = 0;
        String[] ligneCSV = null; //Actual line
        try {
            initialisation();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
            logFileName = logFileName + "_" + sdf.format(Calendar.getInstance().getTime());
            logger.debug("Log file : " + logFileName);
            fw = new FileWriter(logFileName, false);
            fw.write("##############################################################################################################\n");
            fw.write("# READING CSV DATA FROM \"" + chemin + "\" #\n");
            fw.write("##############################################################################################################\n");
            inputFile = new File(chemin);
            if (!inputFile.exists()) {
                ServiceException serviceException = new ServiceException(bundle, CodeIncident.FATAL00001, CodeDetailIncident.GENERIC_CSV_FILE_NOT_FOUND, chemin);
                fw.write(serviceException.getMessage() + "\n");
                fw.flush();
                throw serviceException;
            }
            if (!inputFile.isFile()) {
                ServiceException serviceException = new ServiceException(bundle, CodeIncident.FATAL00001, CodeDetailIncident.GENERIC_CSV_INVALIDE_FILE_TYPE, chemin);
                fw.write(serviceException.getMessage() + "\n");
                fw.flush();
                throw serviceException;
            }
            if (!inputFile.canRead()) {
                ServiceException serviceException = new ServiceException(bundle, CodeIncident.FATAL00001, CodeDetailIncident.GENERIC_CSV_INVALIDE_FILE_PARAMS, chemin);
                fw.write(serviceException.getMessage() + "\n");
                fw.flush();
                throw serviceException;
            }
            inputStreamReader = new InputStreamReader(new FileInputStream(inputFile), JeuCaracteres);
            lecteur = new CSVReader(inputStreamReader, separateur);
            ligneCSV = lecteur.readNext();
            while (ligneCSV != null) {
                try {
                    lineNumber++;
                    String _lineNumber = "" + lineNumber;
                    int len = 6;
                    while (_lineNumber.length() > len) {
                        len += 6;
                    }
                    while (_lineNumber.length() <= len) {
                        _lineNumber = "0" + _lineNumber;
                    }
                    if (lecteurCalendrier.isTitreReconnu(ligneCSV)) {
                        if (counter != 0) {
                            throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.GENERIC_CSV_INVALIDE_TIMETABLE_POSITION, _lineNumber);
                        }
                        lecteurCalendrier.lire(ligneCSV, _lineNumber);
                    } else if (lecteurReseau.isTitreReconnu(ligneCSV)) {
                        if (counter == 0) {
                            lecteurCalendrier.validerCompletude();
                            counter++;
                        }
                        if (counter == 1) {
                            lecteurReseau.lire(ligneCSV, _lineNumber);
                        } else {
                            throw new ServiceException(bundle, CodeIncident.FATAL00003, CodeDetailIncident.GENERIC_CSV_INVALIDE_NETWORK_POSITION, _lineNumber);
                        }
                    } else if (lecteurTransporteur.isTitreReconnu(ligneCSV)) {
                        if (counter == 1) {
                            lecteurReseau.validerCompletude();
                            counter++;
                        }
                        if (counter == 2) {
                            lecteurTransporteur.lire(ligneCSV, _lineNumber);
                        } else {
                            throw new ServiceException(bundle, CodeIncident.FATAL00004, CodeDetailIncident.GENERIC_CSV_INVALIDE_COMPANY_POSITION, _lineNumber);
                        }
                    } else if (lecteurLigne.isTitreReconnu(ligneCSV)) {
                        if (counter == 2) {
                            lecteurTransporteur.validerCompletude();
                            counter++;
                        }
                        if (counter == 3) {
                            lecteurLigne.lire(ligneCSV, _lineNumber);
                        } else {
                            throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.LINE_POSITION);
                        }
                    } else if (lecteurCourse.isTitreReconnu(ligneCSV)) {
                        if (counter == 3) {
                            //lecteurTransporteur.validerCompletude();
                            lecteurCourse.setTableauxMarchesParRef(lecteurCalendrier.getTableauxMarchesParRef());
                            counter++;
                        }
                        if (counter == 4) {
                            lecteurCourse.lire(ligneCSV, lecteurLigne.getLigneEnCours());
                        } else
                            ;//
                    } else if ((ligneCSV == null) || (ligneCSV.length == 0)
                            || ((ligneCSV.length == 1) && ((ligneCSV[0] == null) || (ligneCSV[0].trim().length() == 0)))
                            || ((ligneCSV.length > colonneDesTitres) && ((ligneCSV[colonneDesTitres] == null) || (ligneCSV[colonneDesTitres].trim().length() == 0)))) {
                        if (counter == 5) {
                            /*
                            logger.error("LIGNE "+lecteurLigne.getLigneEnCours().getResistrationNumber());
                            int coNum = 0;
                            for (Course course : lecteurHoraire.getArretsPhysiques().keySet()) {
                            coNum++;
                            logger.error("\tCOURSE "+coNum);
                            for (String st : lecteurHoraire.getArretsPhysiques().get(course))
                            if (st.length() > 0)
                            logger.error("\t\t "+st);
                            }
                             */
                            counter = 3;
                            lecteurMission.lire(lecteurHoraire.getArretsPhysiques(), lecteurLigne.getLigneEnCours());
                            lecteurItineraire.lire(lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()), lecteurCourse.getCoursesAller(), lecteurCourse.getCoursesRetour(), lecteurMission.getMissionByCode(), lecteurLigne.getLigneEnCours());
                            lecteurArret.init(lecteurItineraire.getItineraires().get(lecteurLigne.getLigneEnCours()),
                                    lecteurMission.getMissions().get(lecteurLigne.getLigneEnCours()),
                                    lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()),
                                    lecteurHoraire.getHoraires(),
                                    lecteurHoraire.getArretsPhysiques(),
                                    lecteurZone.getArretsPhysiques());
                            //logger.error("LIGNE "+lecteurLigne.getLigneEnCours().getPublishedName());
                            //for (PositionGeographique st : lecteurZone.getArretsPhysiques())
                            //;//logger.error("\t"+st.getName());
                            lecteurArret.lire(lecteurLigne.getLigneEnCours(), lecteurCourse.getCoursesAller(), lecteurCourse.getCoursesRetour(), lecteurZone.getArretsPhysiques());
                            //;// VERIFIER QUE TOUTE LA LIGNE EST VIDE
                        }
                    } else {
                        if (counter == 4) {
                            lecteurCourse.validerCompletude();
                            lecteurZone.init();
                            lecteurHoraire.init();
                            counter++;
                        }
                        if (counter == 5) {
                            lecteurZone.lire(lecteurLigne.getLigneEnCours(), ligneCSV);
                            lecteurHoraire.lire(ligneCSV, lecteurCourse.getCourses(lecteurLigne.getLigneEnCours()));
                        } else {
                            String ligneText = "";
                            for (int i = 0; i < ligneCSV.length; i++) {
                                ligneText += ligneCSV[i] + ";";
                            }
                            throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.INVALID_SYNTAX, ligneText);
                        }
                    }
                } catch (Exception e) {
                    fw.flush();
                    ;
                }
                ligneCSV = lecteur.readNext();
            }
        } catch (MissingResourceException e) { // No resource bundle for "importCSV" can be found
            e.printStackTrace();
        } catch (IllegalArgumentException e) { // The pattern "yyyy_MM_dd__HH_mm_ss" is invalid
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) { // If 'JeuCaracteres' is not supported)
            e.printStackTrace();
        } catch (IOException e) { // if the file 'logFileName' exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason (new FileWriter)
            e.printStackTrace();
            // Or if an IO error occur (write)
            // Or if bad things happen during the read (readNext)
        } catch (NullPointerException e) { // If the 'chemin' is null
            e.printStackTrace();
        } catch (SecurityException e) { // If a security manager exists and its SecurityManager.checkRead(java.lang.String) method denies read access to the file or directory
            e.printStackTrace();
        } //catch (FileNotFoundException e) {
        //throw new ServiceException(CodeIncident.ERR_CSV_NON_TROUVE,  e);
        //}
        catch (ServiceException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e, lineNumber);
        } finally {
            if (lecteur != null) {
                try {
                    lecteur.close();
                } catch (Exception e) {
                    logger.error("Echec cloture du fichier " + chemin + ", " + e.getMessage(), e);
                }
            }
        }
    }

    private String getCheminfichier(String nom) {
        return repertoire + File.separator + nom;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(String repertoire) {
        this.repertoire = repertoire;
    }

    public char getSeparateur() {
        return separateur;
    }

    public void setSeparateur(char separateur) {
        this.separateur = separateur;
    }

    public int getColonneDesTitres() {
        return colonneDesTitres;
    }

    public void setColonneDesTitres(int colonneDesTitres) {
        this.colonneDesTitres = colonneDesTitres;
    }

    public ILecteurCalendrier getLecteurCalendrier() {
        return lecteurCalendrier;
    }

    public void setLecteurCalendrier(ILecteurCalendrier lecteurCalendrier) {
        this.lecteurCalendrier = lecteurCalendrier;
    }

    public ILecteurReseau getLecteurReseau() {
        return lecteurReseau;
    }

    public void setLecteurReseau(ILecteurReseau lecteurReseau) {
        this.lecteurReseau = lecteurReseau;
    }

    public ILecteurTransporteur getLecteurTransporteur() {
        return lecteurTransporteur;
    }

    public void setLecteurTransporteur(ILecteurTransporteur lecteurTransporteur) {
        this.lecteurTransporteur = lecteurTransporteur;
    }

    public ILecteurLigne getLecteurLigne() {
        return lecteurLigne;
    }

    public void setLecteurLigne(ILecteurLigne lecteurLigne) {
        this.lecteurLigne = lecteurLigne;
    }

    public ILecteurCourse getLecteurCourse() {
        return lecteurCourse;
    }

    public void setLecteurCourse(ILecteurCourse lecteurCourse) {
        this.lecteurCourse = lecteurCourse;
    }

    public ILecteurHoraire getLecteurHoraire() {
        return lecteurHoraire;
    }

    public void setLecteurHoraire(ILecteurHoraire lecteurHoraire) {
        this.lecteurHoraire = lecteurHoraire;
    }

    public ILecteurZone getLecteurZone() {
        return lecteurZone;
    }

    public void setLecteurZone(ILecteurZone lecteurZone) {
        this.lecteurZone = lecteurZone;
    }

    public ILecteurMission getLecteurMission() {
        return lecteurMission;
    }

    public void setLecteurMission(ILecteurMission lecteurMission) {
        this.lecteurMission = lecteurMission;
    }

    public ILecteurItineraire getLecteurItineraire() {
        return lecteurItineraire;
    }

    public void setLecteurItineraire(ILecteurItineraire lecteurItineraire) {
        this.lecteurItineraire = lecteurItineraire;
    }

    public ILecteurArret getLecteurArret() {
        return lecteurArret;
    }

    public void setLecteurArret(ILecteurArret lecteurArret) {
        this.lecteurArret = lecteurArret;
    }

    @Override
    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }
}
