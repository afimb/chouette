package fr.certu.chouette.service.importateur.multilignes.hastus;

import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
//import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
//import java.io.IOException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;
import org.apache.log4j.Logger;

public class LecteurPrincipal implements ILecteurPrincipal {
    
    private static final Logger             logger             = Logger.getLogger(LecteurPrincipal.class);
    private static final String             JeuCaracteres      = "UTF-8";
    private              String             repertoire;        // "."
    private              char               separateur;        // ';'
    private              ILecteurZone       lecteurZone;       // 
    private              ILecteurArret      lecteurArret;      // 
    private              ILecteurLigne      lecteurLigne;      // 
    private              ILecteurItineraire lecteurItineraire; // 
    private              ILecteurCourse     lecteurCourse;     // 
    private              ILecteurHoraire    lecteurHoraire;    // 
    private              ILecteurOrdre      lecteurOrdre;      // 
    private              String             logFileName;       // 
    private static final SimpleDateFormat   sdf                = new SimpleDateFormat("HH:mm:ss");
    
    @Override
    public List<ILectureEchange> getLecturesEchange() {
	List<ILectureEchange> lecturesEchange = new ArrayList<ILectureEchange>();
        Map<String, Ligne> ligneParRegistration = lecteurLigne.getLigneParRegistration();
        Set<String> regLignes = ligneParRegistration.keySet();
        Map<Itineraire, Ligne> ligneParItineraire = lecteurItineraire.getLigneParItineraire();
        Map<String, Mission> missionParNom = lecteurItineraire.getMissionParNom();
        Map<Course, Ligne> ligneParCourse = lecteurCourse.getLigneParCourse();
        Map<String, Map<ArretItineraire, Map<Course, Horaire>>> ordre = lecteurHoraire.getOrdre();
        Map<String, PositionGeographique> zoneParObjectId = lecteurZone.getZonesParObjectId();
        Map<String, PositionGeographique> arretPhysiqueParObjectId = lecteurArret.getArretsPhysiquesParObjectId();
        Map<String, String> objectIdParParentObjectId = lecteurArret.getObjectIdParParentObjectId();
	FileWriter        fw                = null; //To write log infos into
        try {
            fw = new FileWriter(logFileName, true);
	    fw.write("##############################################################################################################\n");
	    fw.write("#                                CREATION DES LIGNES DE TRANSPORT                                            #\n");
	    fw.write("##############################################################################################################\n");
            for (String regLigne : regLignes) {
                LectureEchange lectureEchange = new LectureEchange();
                Ligne ligne = ligneParRegistration.get(regLigne);
                lectureEchange.setLigne(ligne);
                lectureEchange.setTransporteur(lecteurLigne.getTransporteur());
                lectureEchange.setReseau(lecteurLigne.getReseau());
                List<TableauMarche> tableauxMarces = lecteurCourse.getTableauxMarches(ligne);
                if ((tableauxMarces == null) || (tableauxMarces.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas de calendrier d'application.\n");
                    continue;
                }
                lectureEchange.setTableauxMarche(tableauxMarces);
                List<Itineraire> itineraires = new ArrayList<Itineraire>();
                List<Mission> missions = new ArrayList<Mission>();
                Set<ArretItineraire> arretsItineraires = new HashSet<ArretItineraire>();
                Set<Horaire> horaires = new HashSet<Horaire>();
                Set<Course> courses = new HashSet<Course>();
                Set<PositionGeographique> arretsPhysiques = new HashSet<PositionGeographique>();
                Set<PositionGeographique> zones = new HashSet<PositionGeographique>();
                Map<String, String> zoneObjectIdParArretPhysiqueObjectId = new HashMap<String, String>();
                Map<String, String> itineraireParArret = new HashMap<String, String>();
                Set<String> objectIdZonesGeneriques = new HashSet<String>();
                for (Itineraire itineraire : ligneParItineraire.keySet())
                    if (ligneParItineraire.get(itineraire) == ligne) {
                        itineraires.add(itineraire);
                        missions.add(missionParNom.get(itineraire.getNumber()));
                        for (String key : ordre.keySet())
                            if (key.indexOf(itineraire.getNumber()+";") == 0) {
                                Set<ArretItineraire> arretsIts = ordre.get(key).keySet();
                                arretsItineraires.addAll(arretsIts);
                                for (ArretItineraire arretItineraire : arretsIts) {
                                    itineraireParArret.put(arretItineraire.getObjectId(), itineraire.getObjectId());
                                    String arretPhysiqueObjectId = arretItineraire.getContainedIn();
                                    String zoneObjectId = objectIdParParentObjectId.get(arretPhysiqueObjectId);
                                    zoneObjectIdParArretPhysiqueObjectId.put(arretPhysiqueObjectId, zoneObjectId);
                                    objectIdZonesGeneriques.add(arretPhysiqueObjectId);
                                    objectIdZonesGeneriques.add(zoneObjectId);
                                    arretsPhysiques.add(arretPhysiqueParObjectId.get(arretPhysiqueObjectId));
                                    zones.add(zoneParObjectId.get(zoneObjectId));
                                    Map<Course, Horaire> horaireParCoure = ordre.get(key).get(arretItineraire);
                                    Set<Course> tmpCourses = horaireParCoure.keySet();
                                    courses.addAll(tmpCourses);
                                    for (Course course : tmpCourses)
                                        horaires.add(horaireParCoure.get(course));
                                }
                            }
                    }
                if ((itineraires == null) || (itineraires.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas d'itineraire.\n");
                    continue;
                }
                lectureEchange.setItineraires(itineraires);
                if ((missions == null) || (missions.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas de calendrier de mission.\n");
                    continue;
                }
                lectureEchange.setMissions(missions);
                if ((courses == null) || (courses.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas de course.\n");
                    continue;
                }
                lectureEchange.setCourses(new ArrayList<Course>(courses));
                if ((arretsPhysiques == null) || (arretsPhysiques.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas d'arret physique.\n");
                    continue;
                }
                lectureEchange.setArretsPhysiques(new ArrayList<PositionGeographique>(arretsPhysiques));
                if ((horaires == null) || (horaires.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas d'horaire.\n");
                    continue;
                }
                lectureEchange.setHoraires(new ArrayList<Horaire>(horaires));
                if ((arretsItineraires == null) || (arretsItineraires.isEmpty())) {
                    fw.write("La ligne \""+ligne.getNumber()+"\" n'a pas d'arret itineraire.\n");
                    continue;
                }
                lectureEchange.setArrets(new ArrayList<ArretItineraire>(arretsItineraires));
                lectureEchange.setZonesCommerciales(new ArrayList<PositionGeographique>(zones));
                lectureEchange.setZoneParenteParObjectId(zoneObjectIdParArretPhysiqueObjectId);
                lectureEchange.setItineraireParArret(itineraireParArret);
                lectureEchange.setObjectIdZonesGeneriques(new ArrayList<String>(objectIdZonesGeneriques));

                //lectureEchange.setCorrespondances(null);
                //lectureEchange.setInterdictionTraficLocal(null);
                //lectureEchange.setPhysiquesParITLId(null);
                //lectureEchange.setZonesPlaces(null);
                lecturesEchange.add(lectureEchange);
            }
        }
        catch(IOException e) {
        }
	finally {
	    try {
		fw.flush();
		fw.close();
	    }
	    catch(Exception e) {
	    }
	}
	return lecturesEchange;
    }
    
    @Override
    public void lire(String nom) {
	lireCheminFichier(getCheminfichier(nom));
    }
    
    @Override
    public void lireCheminFichier(String chemin) throws ServiceException {
	logger.debug("LECTURE DE DONNEES HASTUS DEPUIS : "+chemin);
        File              inputFile         = null;
        InputStreamReader inputStreamReader = null;
	CSVReader         lecteur           = null;
	FileWriter        fw                = null; //To write log infos into
        Set<String>       aSet              = new HashSet<String>();
	int               ligneNumber       = 0;    //Actual line number
        int               counter           = 0;    //Actual line type
        String[]          ligneCSV          = null; //Actual line
	try {
            initialisation();
	    fw = new FileWriter(logFileName, false);
	    fw.write("##############################################################################################################\n");
	    fw.write("# LECTURE DES DONNEES HASTUS \""+chemin+"\" #\n");
	    fw.write("##############################################################################################################\n");
            inputFile = new File(chemin);
            if (!inputFile.exists()) {
                fw.write("FATAL00001 : Le fichier '"+chemin+"' n'existe pas.\n");
                throw new ServiceException(CodeIncident.FILE_NOT_FOUND, "FATAL00001 : Le fichier '"+chemin+"' n'existe pas.");
            }
            if (!inputFile.isFile()) {
                fw.write("FATAL00001 : Le fichier '"+chemin+"' n'est pas un fichier valide.\n");
                throw new ServiceException(CodeIncident.INVALIDE_FILE_TYPE, "FATAL00001 : Le fichier '"+chemin+"' n'est pas un fichier valide.");
            }
            if (!inputFile.canRead()) {
                fw.write("FATAL00001 : Le fichier '"+chemin+"' n'est pas accessible en Lecture.\n");
                throw new ServiceException(CodeIncident.INVALIDE_FILE_PARAMS, "FATAL00001 : Le fichier '"+chemin+"' n'est pas accessible en Lecture.");
            }
	    inputStreamReader = new InputStreamReader(new FileInputStream(inputFile), JeuCaracteres);
	    lecteur = new CSVReader(inputStreamReader, separateur);
	    ligneCSV = lecteur.readNext();
	    while (ligneCSV != null) {
		try {
		    ligneNumber++;
		    if (lecteurZone.isTitreReconnu(ligneCSV)) {
			if (counter != 0)
                            throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00101 : Toutes les lignes de la section '01' doivent figurer avant les autres sections.");
			lecteurZone.lire(ligneCSV);
		    }
		    else if (lecteurArret.isTitreReconnu(ligneCSV)) {
			if (counter == 0) {
                            lecteurZone.completion();
			    lecteurArret.setZones(lecteurZone.getZones());
			    counter++;
			}
			if (counter != 1)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00102 : Toutes les lignes de la section '02' doivent figurer apres la section '01' et avant les autres sections.");
			lecteurArret.lire(ligneCSV);
		    }
		    else if (lecteurLigne.isTitreReconnu(ligneCSV)) {
			if (counter == 1) {
			    lecteurArret.completion();
			    counter++;
			}
			if (counter != 2)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00103 : Toutes les lignes de la section '03' doivent figurer apres les sections '01' et '02', et avant les autres sections.");
			lecteurLigne.lire(ligneCSV);
		    }
		    else if (lecteurItineraire.isTitreReconnu(ligneCSV)) {
			if (counter == 2) {
                            lecteurLigne.completion();
			    lecteurItineraire.setLigneParRegistration(lecteurLigne.getLigneParRegistration());
			    lecteurItineraire.setZones(lecteurZone.getZones());
			    counter++;
			}
			if (counter != 3)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00104 : Toutes les lignes de la section '04' doivent figurer apres les sections '01', '02' et '03, et avant les autres sections.");
			lecteurItineraire.lire(ligneCSV);
		    }
		    else if (lecteurCourse.isTitreReconnu(ligneCSV)) {
			if (counter == 3) {
			    lecteurItineraire.completion();
			    lecteurCourse.setLigneParRegistration(lecteurLigne.getLigneParRegistration());
                            lecteurCourse.setZones(lecteurZone.getZones());
                            lecteurCourse.setItineraireParNumber(lecteurItineraire.getItineraireParNumber());
                            //lecteurCourse.setMissionParNom(lecteurItineraire.getMissionParNom());
			    counter++;
			}
			if (counter != 4)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00105 : Toutes les lignes de la section '05' doivent figurer apres les sections '01', '02', '03' et '04', et avant les autres sections.");
			lecteurCourse.lire(ligneCSV);
		    }
		    else if (lecteurHoraire.isTitreReconnu(ligneCSV)) {
			if (counter == 4) {
                            lecteurCourse.completion();
			    lecteurHoraire.setCourseParNumber(lecteurCourse.getCourseParNumber());
			    lecteurHoraire.setItineraireParNumber(lecteurItineraire.getItineraireParNumber());
			    lecteurHoraire.setArretsPhysiquesParRegistration(lecteurArret.getArretsPhysiques());
			    counter++;
			}
			if (counter != 5)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00106 : Toutes les lignes de la section '06' doivent figurer apres les sections '01', '02', '03', '04' et '05', et avant la section '07'.");
			lecteurHoraire.lire(ligneCSV);
		    }
		    else if (lecteurOrdre.isTitreReconnu(ligneCSV)) {
			if (counter == 5) {
			    lecteurHoraire.completion();
                            lecteurOrdre.setOrdre(lecteurHoraire.getOrdre());
                            lecteurOrdre.setItineraireParNumber(lecteurItineraire.getItineraireParNumber());
                            lecteurOrdre.setArretsPhysiquesParRegistration(lecteurArret.getArretsPhysiques());
			    counter++;
			}
			if (counter != 6)
			    throw new ServiceException(CodeIncident.INVALIDE_FILE_FORMAT, "FATAL00107 : Toutes les lignes de la section '07' doivent figurer apres les autres sections.");
			lecteurOrdre.lire(ligneCSV);
		    }
		    else if (!isEmptyLigne(ligneCSV))
			throw new ServiceException(CodeIncident.INVALIDE_LIGNE_FORMAT, "FATAL00108 : Il n'y a aucune autre section que les sections '01', '02', '03', '04', '05', '06', et '07'.");
		}
		catch(ServiceException e) {
                    String msg = e.getMessage();
                    String code = e.getCode().toString();
		    if (aSet.add(code+" : "+msg))
                        logger.error("LIGNE NUMERO "+ligneNumber+" : "+code+" : "+msg);
                    fw.write(msg+"\n");
                    String ligneCSVTxt = "";
                    if (ligneCSV != null)
                        for (int i = 0; i < ligneCSV.length; i++) {
                            if (i != 0)
                                ligneCSVTxt += ";";
                           ligneCSVTxt += ligneCSV[i];
                        }
                    fw.write("             Ligne "+ligneNumber+" : "+ligneCSVTxt+"\n");
                    if (msg.startsWith("FATAL")) {
                        fw.flush();
                        fw.close();
                        throw e;
                    }
		}

		ligneCSV = lecteur.readNext();
	    }
	    lecteur.close();
            try {
                lecteurOrdre.completion();
            }
            catch(ServiceException e) {
                String msg = e.getMessage();
                String code = e.getCode().toString();
                if (aSet.add(code+" : "+msg))
                    logger.error("LIGNE NUMERO "+ligneNumber+" : "+code+" : "+msg);
                fw.write(msg+"\n");
                if (msg.startsWith("FATAL")) {
                    fw.flush();
                    fw.close();
                    throw e;
                }
            }
	}
	catch (FileNotFoundException e) {
	    throw new ServiceException(CodeIncident.FILE_NOT_FOUND, "Le fichier \""+chemin+"\" est introuvable.");
	}
	catch (ServiceException e) {
	    throw new ServiceException(e.getCode(), "LIGNE NUMERO "+ligneNumber+" : "+e.getMessage());
	}
	catch(Exception e) {
            //e.printStackTrace();
	    throw new ServiceException(CodeIncident.DONNEE_INVALIDE, "Echec initialisation", e);
	}
	finally {
	    try {
		fw.flush();
		fw.close();
	    }
	    catch(Exception e) {
	    }
	    if (lecteur != null) {
		try {
		    logger.error("Numero de ligne actuel :"+ligneNumber);
		    lecteur.close();
		}
		catch (Exception e) {
		    logger.error("Echec cloture du fichier "+chemin+", "+e.getMessage(), e);
		}
	    }
	}
	logger.debug("FIN DE LECTURE DE DONNEES CSV : "+chemin);
    }
    
    private boolean isEmptyLigne(String[] ligneCSV) {
	if (ligneCSV == null)
            return true;
	if (ligneCSV.length == 0)
	    return true;
	for (int i = 0; i < ligneCSV.length; i++)
	    if (ligneCSV[i] != null)
		if (ligneCSV[i].trim().length() != 0)
		    return false;
	return true;
    }
    
    private String getCheminfichier(String nom) {
	return repertoire + File.separator + nom;
    }
    
    private void initialisation() {
	lecteurZone.reinit();
	lecteurArret.reinit();
	lecteurLigne.reinit();
	lecteurItineraire.reinit();
	lecteurCourse.reinit();
	lecteurHoraire.reinit();
	lecteurOrdre.reinit();
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
    
    public ILecteurZone getLecteurZone() {
	return lecteurZone;
    }
    
    public void setLecteurZone(ILecteurZone lecteurZone) {
	this.lecteurZone = lecteurZone;
    }
    
    public ILecteurArret getLecteurArret() {
	return lecteurArret;
    }
    
    public void setLecteurArret(ILecteurArret lecteurArret) {
	this.lecteurArret = lecteurArret;
    }
    
    public ILecteurLigne getLecteurLigne() {
	return lecteurLigne;
    }
    
    public void setLecteurLigne(ILecteurLigne lecteurLigne) {
	this.lecteurLigne = lecteurLigne;
    }
    
    public ILecteurItineraire getLecteurItineraire() {
	return lecteurItineraire;
    }
    
    public void setLecteurItineraire(ILecteurItineraire lecteurItineraire) {
	this.lecteurItineraire = lecteurItineraire;
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
    
    public ILecteurOrdre getLecteurOrdre() {
	return lecteurOrdre;
    }
    
    public void setLogFileName(String logFileName) {
	this.logFileName = logFileName;
    }

    @Override
    public String getLogFileName() {
        return logFileName;
    }
    
    public void setLecteurOrdre(ILecteurOrdre lecteurOrdre) {
	this.lecteurOrdre = lecteurOrdre;
    }
}
