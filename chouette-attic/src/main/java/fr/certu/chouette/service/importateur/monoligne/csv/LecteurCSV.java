package fr.certu.chouette.service.importateur.monoligne.csv;

import au.com.bytecode.opencsv.CSVReader;
import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.importateur.monoligne.ILecteurCSV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

public class LecteurCSV implements ILecteurCSV {

    private static final Logger logger = Logger.getLogger(LecteurCSV.class);
    private String repertoire;                       // "."
    private char separateur;                       // ';'
    private int debutPartieFixe;                  // 0
    private int finPartieFixe;                    // 18
    private int debutPartieDynamique;             // 20
    private int finPartieDynamique;               // 38
    private int debutPartieArret;                 // 39
    private int colonneTitrePartieFixe;           // 5 --> 7
    private LecteurTransporteurCSV lecteurTransporteurCSV;
    private LecteurCourseCSV lecteurCourseCSV;
    private LecteurItineraireCSV lecteurItineraireCSV;
    private LecteurMissionCSV lecteurMissionCSV;
    private LecteurArretPhysiqueCSV lecteurArretPhysiqueCSV;
    private LecteurHoraireCSV lecteurHoraireCSV;
    private LecteurLigneCSV lecteurLigneCSV;
    private LecteurReseauCSV lecteurReseauCSV;
    private LecteurTableauMarcheCSV lecteurTableauMarcheCSV;
    private ReducteurItineraire reducteurItineraire;
    private ReducteurMission reducteurMission;
    private ReducteurTableauMarche reducteurTableauMarche;
    private Map<String, String> valeurParTitre;
    private static final String JeuCaracteres = "ISO-8859-1";
    private CSVReader lecteur;
    public LectureEchange lectureEchange;

    public LecteurCSV() {
        super();
        valeurParTitre = new HashMap<String, String>();
    }

    @Override
    public void lire(String nom) {
        lireCheminFichier(getCheminfichier(nom));
    }

    @Override
    public void lireCheminFichier(String chemin) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(chemin), JeuCaracteres);
            lecteur = new CSVReader(inputStreamReader, separateur);
            List<String[]> contenu = lecteur.readAll();
            // chargement de la partie statique
            valeurParTitre.clear();
            //  controle du nombre suffisant de lignes
            if (contenu == null) {
                contenu = new ArrayList<String[]>();
            }
            if (contenu.size() <= finPartieFixe) {
                throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_LIGNE, CodeDetailIncident.LINE_COUNT, finPartieFixe, contenu.size());
            }
            for (int i = debutPartieFixe; i < finPartieFixe; i++) {
                String[] contenuLigne = contenu.get(i);
                if (contenuLigne.length < (colonneTitrePartieFixe + 2)) {
                    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_INVALIDE, CodeDetailIncident.COLUMN_COUNT, contenuLigne.length, (colonneTitrePartieFixe + 2));
                }
                if ((contenuLigne[colonneTitrePartieFixe] != null) && (contenuLigne[colonneTitrePartieFixe].trim().length() > 0)) {
                    valeurParTitre.put(contenuLigne[colonneTitrePartieFixe], contenuLigne[colonneTitrePartieFixe + 1]);
                }
            }
            // chargement de la partie dynamique sans arret
            // parcours ces lignes et parcourir le nb de course
            // pour chaque course former: la TM, la mission, l'itineraire, la course (sans horaire)
            int totalCourses = calculerTotalCourses(Arrays.asList(contenu.get(debutPartieDynamique)));
            lecteurArretPhysiqueCSV.initialiser();
            lecteurHoraireCSV.initialiser(totalCourses);
            lecteurCourseCSV.initialiser(totalCourses);
            lecteurItineraireCSV.initialiser(totalCourses);
            lecteurMissionCSV.initialiser(totalCourses);
            lecteurTableauMarcheCSV.initialiser(totalCourses);
            for (int i = debutPartieDynamique; i < contenu.size(); i++) {
                List<String> contenuLigne = Arrays.asList(contenu.get(i));
                if (totalCourses != calculerTotalCourses(contenuLigne)) {
                    throw new ServiceException(CodeIncident.ERR_CSV_FORMAT_COLONNE, CodeDetailIncident.COLUMN_COUNT, totalCourses, calculerTotalCourses(contenuLigne));
                }
                String titre = contenuLigne.get(colonneTitrePartieFixe);
                List<String> proprietes = getProprieteParCourse(contenuLigne);
                if (i < finPartieDynamique) {
                    lecteurCourseCSV.ajouter(titre, proprietes);
                    lecteurItineraireCSV.ajouter(titre, proprietes);
                    lecteurMissionCSV.ajouter(titre, proprietes);
                    lecteurTableauMarcheCSV.ajouter(titre, proprietes);
                } else if (debutPartieArret <= i) {
                    // tester s'il y a au moins 1 horaire,
                    // sinon ignorer la ligne
                    List<String> contenuHoraires = contenuLigne.subList(colonneTitrePartieFixe + 1, contenuLigne.size());
                    if (isColonnesPairesNonVides(contenuHoraires)) {
                        lecteurArretPhysiqueCSV.ajouter(titre, contenuLigne.subList(0, colonneTitrePartieFixe + 1));
                        lecteurHoraireCSV.ajouter(titre, contenuHoraires);
                    }
                }
            }
            // verification de la lecture
            Set<String> titresIntrouvables = lecteurItineraireCSV.getTitresIntrouvables();
            titresIntrouvables.addAll(lecteurMissionCSV.getTitresIntrouvables());
            titresIntrouvables.addAll(lecteurTableauMarcheCSV.getTitresIntrouvables());
            Set<String> clesReseauIntrouvables = lecteurReseauCSV.getCles();
            clesReseauIntrouvables.removeAll(valeurParTitre.keySet());
            Set<String> clesTransporteurIntrouvables = lecteurTransporteurCSV.getCles();
            clesTransporteurIntrouvables.removeAll(valeurParTitre.keySet());
            Set<String> clesLigneIntrouvables = lecteurLigneCSV.getCles();
            clesLigneIntrouvables.removeAll(valeurParTitre.keySet());
            titresIntrouvables.addAll(clesReseauIntrouvables);
            titresIntrouvables.addAll(clesTransporteurIntrouvables);
            titresIntrouvables.addAll(clesLigneIntrouvables);
            // TODO : verifier que toutes les cellules de titre sont là
            if (!titresIntrouvables.isEmpty()) {
                throw new ServiceException(CodeIncident.ERR_CSV_CELLULE_INTROUVABLE, CodeDetailIncident.DEFAULT, titresIntrouvables);
            }
            // chargement des arrets et des courses
            // former la liste des arrets et la liste des horaires des courses
            //List<List<String>> arretsCoursesColonnes = lireEnColonnes( contenu.subList( debutPartieArret, contenu.size()));
            lectureEchange = new LectureEchange();
            lectureEchange.setTransporteur(lecteurTransporteurCSV.lire(valeurParTitre));
            lectureEchange.setLigne(lecteurLigneCSV.lire(valeurParTitre));
            lectureEchange.setReseau(lecteurReseauCSV.lire(valeurParTitre));
            lectureEchange.setArretsPhysiques(lecteurArretPhysiqueCSV.lire());
            List<PositionGeographique> physiques = lectureEchange.getArretsPhysiques();
            List<String> objectIdZonesGeneriques = new ArrayList<String>();
            for (PositionGeographique physique : physiques) {
                objectIdZonesGeneriques.add(physique.getObjectId());
            }
            lectureEchange.setObjectIdZonesGeneriques(objectIdZonesGeneriques);
            lectureEchange.setCourses(lecteurCourseCSV.lire());
            lectureEchange.setItineraires(lecteurItineraireCSV.lire());
            lectureEchange.setMissions(lecteurMissionCSV.lire());
            lectureEchange.setTableauxMarche(lecteurTableauMarcheCSV.lire());
            List<Horaire> tousLesHoraires = new ArrayList<Horaire>();
            List<List<Horaire>> horairesParCourses = lecteurHoraireCSV.lire();
            for (List<Horaire> horaires : horairesParCourses) {
                tousLesHoraires.addAll(horaires);
            }
            lectureEchange.setHoraires(tousLesHoraires);
            reducteurItineraire.reduire(lectureEchange);
            reducteurMission.reduire(lectureEchange);
            reducteurTableauMarche.reduire(lectureEchange);
        } catch (FileNotFoundException e) {
            throw new ServiceException(CodeIncident.ERR_CSV_NON_TROUVE, e);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e, "Echec initialisation");
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

    private int calculerTotalCourses(List<String> contenu) {
        return (contenu.size() - (colonneTitrePartieFixe + 1)) / 2;
    }

    private List<String> getProprieteParCourse(List<String> contenu) {
        return contenu.subList(colonneTitrePartieFixe + 1, contenu.size());
    }

    public void ecrire(ChouettePTNetworkTypeType chouettePTNetworkType, File file) {
        if (chouettePTNetworkType == null) {
            logger.error("EXPORT CSV : chouettePTNetworkType == null");
            return;
        }
        if (chouettePTNetworkType.getChouetteLineDescription() == null) {
            logger.error("EXPORT CSV : chouettePTNetworkType.getChouetteLineDescription() == null");
            return;
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), JeuCaracteres);
        } catch (IOException e) {
            logger.error("EXPORT CSV : " + e.getMessage());
            return;
        }
        List<String[]> contenu = new ArrayList<String[]>();
        // contenu ...
        int length = 2 * chouettePTNetworkType.getChouetteLineDescription().getVehicleJourneyCount() + colonneTitrePartieFixe + 1;
        if (length == colonneTitrePartieFixe + 1)
            length += 1;
        for (int i = 0; i < debutPartieFixe; i++) {
            String[] line = new String[length];
            contenu.add(line);
        }

        // TABLEAUX DE MARCHE
        List<String[]> donneesTableauxMarche = lecteurTableauMarcheCSV.ecrire(chouettePTNetworkType, length, colonneTitrePartieFixe, lecteurCourseCSV);
        contenu.addAll(donneesTableauxMarche);

        // RESEAU
        List<String[]> donneesReseau = lecteurReseauCSV.ecrire(chouettePTNetworkType.getPTNetwork(), length, colonneTitrePartieFixe);
        contenu.addAll(donneesReseau);
        addSeparateur(contenu, length);

        // TRANSPORTEUR
        List<String[]> donneesTransporteur = lecteurTransporteurCSV.ecrire(chouettePTNetworkType.getCompany(), length, colonneTitrePartieFixe);
        contenu.addAll(donneesTransporteur);
        addSeparateur(contenu, length);

        // LIGNE
        List<String[]> donneesLigne = lecteurLigneCSV.ecrire(chouettePTNetworkType.getChouetteLineDescription().getLine(), length, colonneTitrePartieFixe);
        contenu.addAll(donneesLigne);
        for (int i = finPartieFixe + 1; i < debutPartieDynamique; i++) {
            addSeparateur(contenu, length);
        }

        // DEBUT PARTIE DYNAMIQUE
        String[] donnees = new String[length];
        donnees[colonneTitrePartieFixe] = "Horaires des cours";
        for (int i = 0; i < chouettePTNetworkType.getChouetteLineDescription().getVehicleJourneyCount(); i++) {
            donnees[colonneTitrePartieFixe + 1 + 2 * i] = "Horaires";
        }
        contenu.add(donnees);

        // ITINERAIRES
        List<String[]> donneesItineraires = lecteurItineraireCSV.ecrire(chouettePTNetworkType.getChouetteLineDescription(), length, colonneTitrePartieFixe);
        contenu.addAll(donneesItineraires);

        // MISSIONS
        List<String[]> donneesMissions = lecteurMissionCSV.ecrire(chouettePTNetworkType.getChouetteLineDescription(), length, colonneTitrePartieFixe);
        contenu.addAll(donneesMissions);

        // HORAIRES
        String[] donneeArrets = lecteurArretPhysiqueCSV.ecrire(length, colonneTitrePartieFixe);
        contenu.add(donneeArrets);
        Collection<List<String[]>> donneesArrets = lecteurArretPhysiqueCSV.ecrire(chouettePTNetworkType.getChouetteLineDescription(), length, colonneTitrePartieFixe);
        for (List<String[]> aDonneesArrets : donneesArrets) {
            contenu.addAll(aDonneesArrets);
        }

        try {
            for (int i = 0; i < contenu.size(); i++) {
                String[] ligne = contenu.get(i);
                for (int j = 0; j < ligne.length; j++) {
                    if (ligne[j] != null) {
                        if (isNumeric(ligne[j])) {
                            writer.write(ligne[j]);
                        } else {
                            writer.write("\"" + ligne[j] + "\"");
                        }
                    }
                    if (j == (ligne.length - 1)) {
                        writer.write("\n");
                    } else {
                        writer.write(";");
                    }
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("EXPORT CSV : " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    logger.error("");
                }
            }
        }
    }

    private boolean isNumeric(String st) {
        if (st != null) {
            for (int i = 0; i < st.length(); i++) {
                switch (st.charAt(i)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case '/':
                    case ':':
                        break;
                    default:
                        return false;
                }
            }
        }
        return true;
    }

    private void addSeparateur(List<String[]> contenu, int length) {
        String[] line = new String[length];
        contenu.add(line);
    }

    private boolean isColonnesPairesNonVides(List<String> contenu) {
        int total = contenu.size() / 2;
        for (int i = 0; i < total; i++) {
            if (!contenu.get(2 * i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String getCheminfichier(String nom) {
        return repertoire + File.separator + nom;
    }

    public static final Logger getLogger() {
        return logger;
    }

    public void setLectureEchange(LectureEchange lectureEchange) {
        this.lectureEchange = lectureEchange;
    }

    public ILectureEchange getLectureEchange() {
        return lectureEchange;
    }

    public void setRepertoire(String repertoire) {
        this.repertoire = repertoire;
    }

    public String getRepertoire() {
        return repertoire;
    }

    public void setSeparateur(char separateur) {
        this.separateur = separateur;
    }

    public char getSeparateur() {
        return separateur;
    }

    public void setDebutPartieFixe(int debutPartieFixe) {
        this.debutPartieFixe = debutPartieFixe;
    }

    public int getDebutPartieFixe() {
        return debutPartieFixe;
    }

    public void setFinPartieFixe(int finPartieFixe) {
        this.finPartieFixe = finPartieFixe;
    }

    public int getFinPartieFixe() {
        return finPartieFixe;
    }

    public void setDebutPartieDynamique(int debutPartieDynamique) {
        this.debutPartieDynamique = debutPartieDynamique;
    }

    public int getDebutPartieDynamique() {
        return debutPartieDynamique;
    }

    public void setFinPartieDynamique(int finPartieDynamique) {
        this.finPartieDynamique = finPartieDynamique;
    }

    public int getFinPartieDynamique() {
        return finPartieDynamique;
    }

    public void setDebutPartieArret(int debutPartieArret) {
        this.debutPartieArret = debutPartieArret;
    }

    public int getDebutPartieArret() {
        return debutPartieArret;
    }

    public void setColonneTitrePartieFixe(int colonneTitrePartieFixe) {
        this.colonneTitrePartieFixe = colonneTitrePartieFixe;
    }

    public int getColonneTitrePartieFixe() {
        return colonneTitrePartieFixe;
    }

    public void setLecteurTransporteurCSV(LecteurTransporteurCSV lecteurTransporteurCSV) {
        this.lecteurTransporteurCSV = lecteurTransporteurCSV;
    }

    public LecteurTransporteurCSV getLecteurTransporteurCSV() {
        return lecteurTransporteurCSV;
    }

    public void setLecteurCourseCSV(LecteurCourseCSV lecteurCourseCSV) {
        this.lecteurCourseCSV = lecteurCourseCSV;
    }

    public LecteurCourseCSV getLecteurCourseCSV() {
        return lecteurCourseCSV;
    }

    public void setLecteurItineraireCSV(LecteurItineraireCSV lecteurItineraireCSV) {
        this.lecteurItineraireCSV = lecteurItineraireCSV;
    }

    public LecteurItineraireCSV getLecteurItineraireCSV() {
        return lecteurItineraireCSV;
    }

    public void setLecteurMissionCSV(LecteurMissionCSV lecteurMissionCSV) {
        this.lecteurMissionCSV = lecteurMissionCSV;
    }

    public LecteurMissionCSV getLecteurMissionCSV() {
        return lecteurMissionCSV;
    }

    public void setLecteurArretPhysiqueCSV(LecteurArretPhysiqueCSV lecteurArretPhysiqueCSV) {
        this.lecteurArretPhysiqueCSV = lecteurArretPhysiqueCSV;
    }

    public LecteurArretPhysiqueCSV getLecteurArretPhysiqueCSV() {
        return lecteurArretPhysiqueCSV;
    }

    public void setLecteurHoraireCSV(LecteurHoraireCSV lecteurHoraireCSV) {
        this.lecteurHoraireCSV = lecteurHoraireCSV;
    }

    public LecteurHoraireCSV getLecteurHoraireCSV() {
        return lecteurHoraireCSV;
    }

    public void setLecteurLigneCSV(LecteurLigneCSV lecteurLigneCSV) {
        this.lecteurLigneCSV = lecteurLigneCSV;
    }

    public LecteurLigneCSV getLecteurLigneCSV() {
        return lecteurLigneCSV;
    }

    public void setLecteurReseauCSV(LecteurReseauCSV lecteurReseauCSV) {
        this.lecteurReseauCSV = lecteurReseauCSV;
    }

    public LecteurReseauCSV getLecteurReseauCSV() {
        return lecteurReseauCSV;
    }

    public void setLecteurTableauMarcheCSV(LecteurTableauMarcheCSV lecteurTableauMarcheCSV) {
        this.lecteurTableauMarcheCSV = lecteurTableauMarcheCSV;
    }

    public LecteurTableauMarcheCSV getLecteurTableauMarcheCSV() {
        return lecteurTableauMarcheCSV;
    }

    public void setReducteurItineraire(ReducteurItineraire reducteurItineraire) {
        this.reducteurItineraire = reducteurItineraire;
    }

    public ReducteurItineraire getReducteurItineraire() {
        return reducteurItineraire;
    }

    public void setReducteurMission(ReducteurMission reducteurMission) {
        this.reducteurMission = reducteurMission;
    }

    public ReducteurMission getReducteurMission() {
        return reducteurMission;
    }

    public void setReducteurTableauMarche(ReducteurTableauMarche reducteurTableauMarche) {
        this.reducteurTableauMarche = reducteurTableauMarche;
    }

    public ReducteurTableauMarche getReducteurTableauMarche() {
        return reducteurTableauMarche;
    }
}
