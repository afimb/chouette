package fr.certu.chouette.service.importateur.multilignes.pegase;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import java.io.File;
import org.apache.log4j.Logger;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import au.com.bytecode.opencsv.CSVReader;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.modele.Transporteur;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LecteurPrincipal implements ILecteurPrincipal {

    private String repertoire;        // "."
    private static final Logger logger = Logger.getLogger(LecteurPrincipal.class);
    private static final int len = 19;
    private char separateur;
    private static final String JeuCaracteres = "UTF-8";
    private Map<String, Ligne> lignes;
    private Map<String, TableauMarche> tms;
    private Map<String, Arret> arrets;
    private Map<String, Transporteur> transporteurs;
    private int ligneNumber;
    private String logFileName;
    private IIdentificationManager identificationManager;
    public static int counter;
    private ChouetteDriverManagerDataSource managerDataSource;
    private String ptname;
    private String sysname;
    private String ptreg;
    private String ptdesc;
    private String comporgunit;

    public void setPtname(String ptname) {
        this.ptname = ptname;
    }

    public String getPtname() {
        return ptname;
    }

    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    public String getSysname() {
        return sysname;
    }

    public void setPtreg(String ptreg) {
        this.ptreg = ptreg;
    }

    public String getPtreg() {
        return ptreg;
    }

    public void setPtdesc(String ptdesc) {
        this.ptdesc = ptdesc;
    }

    public String getPtdesc() {
        return ptdesc;
    }

    public void setComporgunit(String comporgunit) {
        this.comporgunit = comporgunit;
    }

    public String getComporgunit() {
        return comporgunit;
    }

    @Override
    public List<ILectureEchange> getLecturesEchange() {
        Connection connexion = null;
        try {
            Properties props = new Properties();
            props.setProperty("user", managerDataSource.getUsername());
            props.setProperty("password", managerDataSource.getPassword());
            props.setProperty("allowEncodingChanges", "true");
            connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ILectureEchange> lectureEchanges = new ArrayList<ILectureEchange>();
        Reseau reseau = new Reseau();
        counter = 1;
        //transporteurs = new HashMap<String, Transporteur>();
        //((IdentificationManager)identificationManager).setSystemId("PEGASE");
        reseau.setName(ptname);
        reseau.setRegistrationNumber(ptreg);
        reseau.setDescription(ptdesc);
        reseau.setCreationTime(new Date());
        reseau.setVersionDate(new Date());
        reseau.setObjectId(identificationManager.getIdFonctionnel("PtNetwork", sysname));
        reseau.setObjectVersion(1);
        Transporteur transporteur = null;
        for (Ligne lig : lignes.values()) {
            LectureEchange lectureEchange = new LectureEchange();
            //Transporteur transporteur = transporteurs.get(lig.getReg());
            if (transporteur == null) {
                transporteur = new Transporteur();
                transporteur.setName(lig.getReg().substring(0, lig.getReg().length() - 3));
                transporteur.setRegistrationNumber(lig.getShortName());
                transporteur.setShortName(lig.getShortName());
                transporteur.setOrganisationalUnit(comporgunit);
                //transporteur.setCode("cleCodePostal");
                //transporteur.setPhone("cleTelephone");
                //transporteur.setFax("cleFax");
                //transporteur.setEmail("cleEmail");
                transporteur.setObjectId(identificationManager.getIdFonctionnel("Company", sysname));
                transporteur.setObjectVersion(1);
                transporteur.setCreationTime(new Date());
                //transporteurs.put(lig.getReg(), transporteur);
            }
            lectureEchange.setTransporteur(transporteur);
            lectureEchange.setReseau(reseau);
            lectureEchange.setLigne(lig.getLigne());
            lectureEchange.setTableauxMarche(lig.getTableauxMarche());
            lectureEchange.setZonesCommerciales(lig.getZonesCommerciales(connexion));
            lectureEchange.setArretsPhysiques(lig.getArretsPhysiques());
            lectureEchange.setZoneParenteParObjectId(lig.getZoneParenteParObjectId());
            lectureEchange.setObjectIdZonesGeneriques(lig.getObjectIdZonesGeneriques());
            lectureEchange.setItineraires(lig.getChouetteItineraires());
            lectureEchange.setMissions(lig.getMissions());
            lectureEchange.setCourses(lig.getCourses());
            lectureEchange.setArrets(lig.getArretsItineraires());
            lectureEchange.setItineraireParArret(lig.getItineraireParArret());
            lectureEchange.setHoraires(lig.getHoraires());

            //lectureEchange.setZonesPlaces(zonesPlaces);
            //lectureEchange.setCorrespondances(correspondances);
            //lectureEchange.setInterdictionTraficLocal(interdictionsTraficLocal);
            //lectureEchange.setPhysiquesParITLId(physiquesParITLId);

            lectureEchanges.add(lectureEchange);
        }
        return lectureEchanges;
    }

    @Override
    public void lire(String nom) {
        lireCheminFichier(getCheminFichier(nom));
    }

    private String getCheminFichier(String nom) {
        return repertoire + File.separator + nom;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void lireCheminFichier(String chemin) {
        lignes = new HashMap<String, Ligne>();
        tms = new HashMap<String, TableauMarche>();
        arrets = new HashMap<String, Arret>();
        logger.debug("LECTURE DE DONNEES PEGASE : " + chemin);
        try {
            InputStreamReader inputSR = new InputStreamReader(new FileInputStream(chemin), JeuCaracteres);
            CSVReader lecteur = new CSVReader(inputSR, separateur);
            List<String[]> contenu = lecteur.readAll();
            for (String[] text : contenu) {
                ligneNumber++;
                if (text.length != len) {
                    logger.error("MAUVAIS NOMBRE DE COLONNES : " + text.length + " AU LIEU DE " + len + ".");
                } else {
                    for (int i = 0; i < len; i++) {
                        if ((text[i] == null) || (text[i].trim().length() == 0)) {
                            logger.error(ligneNumber + " : CHAMPS N°" + i + " EST VIDE.");
                        }
                    }
                }
                String regLigne = text[ 0].trim(); // Code ligne
                String dateValiditeLigne = text[ 1].trim(); // Date de validite de la ligne
                String shortNameLigne = text[ 2].trim(); // Libellé ligne court
                String nameLigne = text[ 3].trim(); // Libellé ligne long
                String codeItineraire = text[ 4].trim(); // Code itineraire
                String dateValiditeCourse = text[ 5].trim(); // Date de validite itineraire
                String shortNameItineraire = text[ 6].trim(); // Libellé itineraire court
                String nameItineraire = text[ 7].trim(); // Libellé itineraire long
                String serviceCode = text[ 8].trim(); // Code service
                String ordreArretDansCourse = text[ 9].trim(); // Ordre dans le trajet
                String serviceType = text[10].trim(); // Libellé type de service
                String codeArret = text[11].trim(); // Code du point de montee
                String heureDePassage = text[12].trim(); // Heure de passage
                String shortNameArret = text[13].trim(); // Libellé point arret court
                String nameArret = text[14].trim(); // Libellé point arret long
                String joursValidite = text[15].trim(); // Jours de fonctionnement
                String communeDeArret = text[16].trim(); // Libellé commune arret
                @SuppressWarnings("unused")
                String tarif = text[17].trim(); // tarification
                String sensCourse = text[18].trim(); // sens
                if (heureDePassage.length() != 5) {
                    logger.error("LIGNE : "+ligneNumber+" : ERREUR POUR HEURE_DE_PASSAGE : \"" + heureDePassage+"\"");//ligneNumber+"  : joursValidite.length() != 7");
                    if (joursValidite.length() != 7) {
                        logger.error("LIGNE : "+ligneNumber+" : ERREUR POUR JOURS_VALIDITE : \"" + joursValidite+"\"");//ligneNumber+"  : joursValidite.length() != 7");
                        continue;
                        //throw new TableauMarcheException("ERROR POUR JOURS_VALIDITE : " + joursValidite);//ligneNumber+"  : joursValidite.length() != 7");
                    }
                    continue;
                }
                if (joursValidite.length() != 7) {
                    logger.error("LIGNE : "+ligneNumber+" : ERREUR POUR JOURS_VALIDITE : \"" + joursValidite+"\"");//ligneNumber+"  : joursValidite.length() != 7");
                    continue;
                    //throw new TableauMarcheException("ERROR POUR JOURS_VALIDITE : " + joursValidite);//ligneNumber+"  : joursValidite.length() != 7");
                }
                //String        key;
                //if (codeItineraire.length() >= 3+regLigne.length())
                //key = codeItineraire.substring(0, 3+regLigne.length());
                //else
                //key = codeItineraire;
                Ligne lig = getLigne(regLigne, shortNameLigne, nameLigne);
                TableauMarche tm = getTM(dateValiditeLigne, joursValidite);
                if (!dateValiditeLigne.equals(dateValiditeCourse)) {
                    tm = getTM(dateValiditeCourse, joursValidite);
                }
                Itineraire it = lig.getItineraire(codeItineraire, shortNameItineraire, nameItineraire);
                Course co = it.getCourse(serviceCode, serviceType, sensCourse);
                Arret arret = getArret(codeArret, shortNameArret, nameArret, communeDeArret);
                Horaire ho = co.getHoraire(arret, ordreArretDansCourse, heureDePassage);
                co.addTM(tm);
                /*
                 * logger.error(lig.getReg()+";"+tm.getDateDebut()+";"+lig.getShortName()+";"+lig.getName()+";"+
                 * it.getCode()+";"+dateValiditeCourse+";"+it.getShortName()+";"+it.getName()+";"+
                 * co.getServiceCode()+";"+ho.getOrdre()+";"+co.getServiceType()+";"+arret.getCode()+";"+ho.getHeure()+";"+
                 * arret.getShortName()+";"+arret.getName()+";"+tm.getJoursApplication()+";"+arret.getCommune()+";"+
                 * tarif+";"+co.getSens());
                 */
            }
            // FUSION DES COURSES EQUIVALENTES :
            for (Ligne lig : lignes.values()) {
                Collection<Itineraire> its = lig.getItineraires().values();
                for (Itineraire it : its) {
                    Map<Course, List<Course>> coursesAFusionner = new HashMap<Course, List<Course>>();
                    int size = it.getCourses().values().size();
                    Course[] courses = it.getCourses().values().toArray(new Course[size]);
                    for (int i = 0; i < size; i++) {
                        for (int j = i + 1; j < size; j++) {
                            Course co1 = courses[i];
                            Course co2 = courses[j];
                            Set<Course> dejaVu = new HashSet<Course>();
                            Collection<Horaire> horaires1 = co1.getHoraires().values();
                            Collection<Horaire> horaires2 = co2.getHoraires().values();
                            if (horaires1.size() != horaires2.size()) {
                                //logger.error("ERREUR DEUX COURSES DU MEME ITINERAIRE AVEC DES NOMBRES D'HORAIRES DIFFERENTS : "+
                                //lig.getReg()+":"+it.getCode()+":"+co1.getServiceCode()+" ET "+
                                //lig.getReg()+":"+it.getCode()+":"+co2.getServiceCode());
                                continue;
                            }
                            boolean areEquals = true;
                            for (Horaire ho1 : horaires1) {
                                if (!areEquals) {
                                    break;
                                }
                                for (Horaire ho2 : horaires2) {
                                    if (ho1.getOrdre() == ho2.getOrdre()) {
                                        if (ho1.getArret() != ho2.getArret()) {
                                            //logger.error("ERREUR DEUX COURSES DU MEME ITINERAIRE AVEC DES ARRETS DIFFERENTS : "+
                                            //lig.getReg()+":"+it.getCode()+":"+co1.getServiceCode()+" ET "+
                                            //lig.getReg()+":"+it.getCode()+":"+co2.getServiceCode());
                                            areEquals = false;
                                            continue;
                                        } else if (!ho1.getHeure().equals(ho2.getHeure())) {
                                            areEquals = false;
                                        }
                                    }
                                }
                            }
                            if (areEquals && dejaVu.add(co1)) {
                                dejaVu.add(co2);
                                if (coursesAFusionner.get(co1) == null) {
                                    coursesAFusionner.put(co1, new ArrayList<Course>());
                                }
                                coursesAFusionner.get(co1).add(co2);
                            }
                        }
                    }
                    //FUSIONNER LES COURSES...
                    for (Course co1 : coursesAFusionner.keySet()) {
                        List<Course> coursesADetruire = coursesAFusionner.get(co1);
                        co1.setServiceType(co1.getServiceType() + "_" + co1.getServiceCode());
                        for (Course co2 : coursesADetruire) {
                            co1.addTMs(co2.getTMs());
                            co1.setServiceType(co1.getServiceType() + "_" + co2.getServiceCode());
                        }
                        it.removeCourses(coursesADetruire);
                    }
                }
            }
            /*
             * for (Ligne lig : lignes.values()) {
             * logger.error("LIGNE : "+lig.getReg()+". NOM : "+lig.getName()+". NOM PUBLIC : "+lig.getShortName()+".");
             * for (Itineraire it : lig.getItineraires().values()) {
             * logger.error("\tITINERAIRE : "+it.getCode()+". NOM : "+it.getName()+". NOM PUBLIC : "+it.getShortName()+".");
             * for (Course co : it.getCourses().values()) {
             * logger.error("\t\tCOURSE : "+co.getServiceCode()+". SENS : "+co.getSens());
             * for (TableauMarche tm : co.getTMs())
             * logger.error("\t\t\tCALENDRIER : "+tm.getDateDebut()+" TO "+tm.getDateFin()+". JOURS D4APPLICATIONS : "+tm.getJoursApplication());
             * for (Horaire ho : co.getHoraires().values())
             * logger.error("\t\t\tHORAIRE : "+ho.increment+". HEURE DE PASSAGE : "+ho.getHeure()+". AT : "+ho.getArret().getCode()+". NAME : "+ho.getArret().getName());
             * }
             * }
             * }
             */
        } catch (Throwable e) {
            String msg = "LIGNE NUMERO " + ligneNumber + " : " + e.getMessage();
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private Ligne getLigne(String reg, String shortName, String name) throws LigneException {
        Ligne ligne = lignes.get(reg);
        if (ligne != null) {
            if (!ligne.getShortName().equals(shortName)) {
                throw new LigneException("SHORTNAME MUST BE \"" + ligne.getShortName() + "\" AND NOT \"" + shortName + "\".");
            }
            if (!ligne.getName().equals(name)) {
                throw new LigneException("NAME MUST BE \"" + ligne.getName() + "\" AND NOT \"" + name + "\".");
            }
            return ligne;
        }
        ligne = new Ligne(identificationManager, reg, shortName, name);
        lignes.put(reg, ligne);
        return ligne;
    }

    private TableauMarche getTM(String dateV, String joursAppli) {
        TableauMarche tm = tms.get(dateV + joursAppli);
        if (tm != null) {
            return tm;
        }
        try {
            tm = new TableauMarche(identificationManager, dateV, joursAppli);
        } catch (Exception e) {
        }
        tms.put(dateV + joursAppli, tm);
        return tm;
    }

    private Arret getArret(String code, String shortName, String name, String commune) throws ArretException {
        Arret arret = arrets.get(code);
        if (arret != null) {
            if (!arret.getShortName().equals(shortName)) {
                throw new ArretException("ERREUR POUR ARRET SHORTNAME : " + shortName + " : " + arret.getShortName());
            }
            if (!arret.getName().equals(name)) {
                throw new ArretException("ERREUR POUR ARRET NAME : " + name + " : " + arret.getName());
            }
            if (!arret.getCommune().equals(commune)) {
                throw new ArretException("ERREUR POUR ARRET COMMUNE : " + commune + " : " + arret.getCommune());
            }
            return arret;
        }
        arret = new Arret(identificationManager, code, shortName, name, commune);
        arrets.put(code, arret);
        return arret;
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

    public int getLigneNumber() {
        return ligneNumber;
    }

    public void setLigneNumber(int ligneNumber) {
        this.ligneNumber = ligneNumber;
    }

    @Override
    public String getLogFileName() {
        return logFileName;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public IIdentificationManager getIdentificationManager() {
        return identificationManager;
    }

    public void setIdentificationManager(IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }

    public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
        this.managerDataSource = managerDataSource;
    }
}
