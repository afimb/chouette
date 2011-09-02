package fr.certu.chouette.service.importateur.multilignes.genericcsv.excel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import chouette.schema.types.DayTypeType;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.identification.IIdentificationManager;
import fr.certu.chouette.service.importateur.multilignes.genericcsv.ILecteurCalendrier;
import java.text.ParseException;
import java.util.ResourceBundle;

public class LecteurCalendrier implements ILecteurCalendrier {

    private static final Logger logger = Logger.getLogger(LecteurCalendrier.class);
    private Map<String, TableauMarche> caldendriersParRef;
    private SimpleDateFormat sdf = null;
    private int colonneDesTitres;     // 7
    private IIdentificationManager identificationManager;// 
    private String cleAlias;             // "Alias"
    private String cleJour;              // "Jour d'application"
    private String cleDebut;             // "Date de début d'application"
    private String cleFin;               // "Date de fin d'application"
    private String cleLundi;             // "Lundi (O/N)"
    private String cleMardi;             // "Mardi (O/N)"
    private String cleMercredi;          // "Mercredi (O/N)"
    private String cleJeudi;             // "Jeudi (O/N)"
    private String cleVendredi;          // "Vendredi (O/N)"
    private String cleSamedi;            // "Samedi (O/N)"
    private String cleDimanche;          // "Dimanche (O/N)"
    private String cleCommentaire;       // "Libellé du tableau de marche"
    private String cleDateFormat;        // "dd/MM/yyyy"
    private TableauMarche calendrierEnCours;
    private Set<String> cellulesNonRenseignees;
    private Set<String> titres;
    private ResourceBundle bundle;
    private String lineNumber;

    @Override
    public void reinit(ResourceBundle bundle) {
        calendrierEnCours = null;
        caldendriersParRef = new HashMap<String, TableauMarche>();
        titres = new HashSet<String>();
        titres.add(cleCommentaire);
        titres.add(cleAlias);
        titres.add(cleJour);
        titres.add(cleDebut);
        titres.add(cleFin);
        titres.add(cleLundi);
        titres.add(cleMardi);
        titres.add(cleMercredi);
        titres.add(cleJeudi);
        titres.add(cleVendredi);
        titres.add(cleSamedi);
        titres.add(cleDimanche);
        cellulesNonRenseignees = new HashSet<String>(titres);
        this.bundle = bundle;
        try {
            sdf = new SimpleDateFormat(cleDateFormat);
            return;
        } catch (NullPointerException e) {
            logger.error("The key 'cleDateFormat' must be non null. It will be set to 'dd/MM/yyyy'.");
            try {
                setCleDateFormat("dd/MM/yyyy");
                sdf = new SimpleDateFormat(cleDateFormat);
            } catch (IllegalArgumentException ex) {//- if the given pattern is invalid)
                //This can never occur
            }
        }
    }

    @Override
    public boolean isTitreReconnu(String[] ligneCSV) {
        if ((ligneCSV == null) || (ligneCSV.length < (colonneDesTitres + 1))) {
            return false;
        }
        String titre = ligneCSV[colonneDesTitres];
        if (titre == null) {
            return false;
        }
        return titres.contains(titre.trim());
    }

    @Override
    public void lire(String[] ligneCSV, String _lineNumber) {
        lineNumber = _lineNumber;
        String titre = ligneCSV[colonneDesTitres];
        String valeur = null;
        if (ligneCSV.length > (colonneDesTitres + 1)) {
            valeur = ligneCSV[colonneDesTitres + 1];
        }
        if (isTitreNouvelleDonnee(titre)) {
            validerCompletudeDonneeEnCours();
            cellulesNonRenseignees = new HashSet<String>(titres);
            calendrierEnCours = new TableauMarche();
            calendrierEnCours.setObjectVersion(1);
            calendrierEnCours.setCreationTime(new Date());
            logger.debug("Nouveau calendrier");
        }
        if (!cellulesNonRenseignees.remove(titre)) {
            throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.TIMETABLE_DUPLICATEDATA, lineNumber, titre);
        }
        if (cleCommentaire.equals(titre)) {
            if (valeur == null) {
                throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.TIMETABLE_MISSINGVALUEDATA, lineNumber, cleCommentaire);
            } else {
                calendrierEnCours.setComment(valeur);
            }
        } else if (cleJour.equals(titre)) {
            boolean finDeLigne = false;
            for (int i = colonneDesTitres + 1; i < ligneCSV.length; i++) {
                valeur = ligneCSV[i];
                if ((valeur == null) || (valeur.trim().length() == 0)) {
                    finDeLigne = true;
                } else {
                    if (finDeLigne) {
                        throw new ServiceException(bundle, CodeIncident.WARNING00002, CodeDetailIncident.NULL_COLUMN, lineNumber, cleJour);
                    }
                    try {
                        Date date = sdf.parse(valeur);
                        calendrierEnCours.ajoutDate(date);
                    } catch (ParseException e) {
                        throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.DATE_TYPE_FORMAT_ERROR, lineNumber, valeur);
                    }
                }
            }
        } else if (cleDebut.equals(titre)) {
            boolean finDeLigne = false;
            for (int i = colonneDesTitres + 1; i < ligneCSV.length; i++) {
                valeur = ligneCSV[i];
                if ((valeur == null) || (valeur.trim().length() == 0)) {
                    finDeLigne = true;
                } else {
                    if (finDeLigne) {
                        throw new ServiceException(bundle, CodeIncident.WARNING00002, CodeDetailIncident.NULL_COLUMN, lineNumber, cleDebut);
                    }
                    Periode periode = new Periode();
                    calendrierEnCours.ajoutPeriode(periode);
                    try {
                        Date debut = sdf.parse(valeur);
                        periode.setDebut(debut);
                    } catch (ParseException e) {
                        throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.DATE_TYPE_FORMAT_ERROR, lineNumber, valeur);
                    }
                }
            }
        } else if (cleFin.equals(titre)) {
            if (cellulesNonRenseignees.contains(cleDebut)) // cleDebut must be before cleFin
            {
                throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.PERIODS_DEF_ERROR, lineNumber, cleDebut, cleFin);
            }
            boolean finDeLigne = false;
            List<Periode> periodes = calendrierEnCours.getPeriodes();
            for (int i = colonneDesTitres + 1; i < ligneCSV.length; i++) {
                valeur = ligneCSV[i];
                if ((valeur == null) || (valeur.trim().length() == 0)) {
                    finDeLigne = true;
                    if ((periodes != null) && (periodes.size() > i - (colonneDesTitres + 1))) {
                        throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.PERIOD_STARTDATE_ERROR, lineNumber);
                    }
                } else {
                    if (finDeLigne) {
                        throw new ServiceException(bundle, CodeIncident.WARNING00002, CodeDetailIncident.NULL_COLUMN, lineNumber, cleFin);
                    }
                    if ((periodes == null) || (periodes.size() <= i - (colonneDesTitres + 1))) {
                        throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.PERIOD_ENDDATE_ERROR, lineNumber);
                    }
                    Periode periode = periodes.get(i - (colonneDesTitres + 1));
                    try {
                        Date fin = sdf.parse(valeur);
                        periode.setFin(fin);
                    } catch (ParseException e) {
                        throw new ServiceException(bundle, CodeIncident.FATAL00002, CodeDetailIncident.DATE_TYPE_FORMAT_ERROR, lineNumber, valeur);
                    }
                }
            }
        } else if (cleLundi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.MONDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleMardi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.TUESDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleMercredi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.WEDNESDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleJeudi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.THURSDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleVendredi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.FRIDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleSamedi.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.SATURDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleDimanche.equals(titre)) {
            if (isO(valeur)) {
                Set<DayTypeType> jours = calendrierEnCours.getDayTypes();
                jours.add(DayTypeType.SUNDAY);
                calendrierEnCours.setDayTypes(jours);
            }
        } else if (cleAlias.equals(titre)) {
            logger.debug("\talias = " + valeur);
            if ((valeur == null) || (valeur.trim().length() == 0)) {
                throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.NULL_ALIAS_ERROR, lineNumber);
            }
            valeur = valeur.trim();
            if (caldendriersParRef.get(valeur.trim()) != null) {
                throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.DUPLICATE_ALIAS_ERROR, lineNumber, valeur);
            }
            calendrierEnCours.setObjectId(identificationManager.getIdFonctionnel("Timetable", valeur));
            calendrierEnCours.setVersion(valeur);
            caldendriersParRef.put(valeur, calendrierEnCours);
        } else // This can never occur
        {
            throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.UNKNOWN_KEY_ERROR, lineNumber, titre);
        }
        //calendrierEnCours.setCreatorId(creatorId);
        //calendrierEnCours.setId(id);
    }

    private void validerCompletudeDonneeEnCours() {
        if (calendrierEnCours != null) {
            validerCompletude();
        }
    }

    @Override
    public void validerCompletude() {
        if (cellulesNonRenseignees.size() > 0) {
            String[] cellsTab = cellulesNonRenseignees.toArray(new String[0]);
            String cells = cellsTab[0];
            for (int i = 1; i < cellsTab.length; i++) {
                cells += ", " + cellsTab[i];
            }
            throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.TIMETABLE_MISSINGDATA, lineNumber, cells);
        }
        List<Date> dates = calendrierEnCours.getDates();
        if (dates == null || dates.isEmpty()) {
            List<Periode> periods = calendrierEnCours.getPeriodes();
            Set<DayTypeType> dayTypes = calendrierEnCours.getDayTypes();
            if (periods == null || periods.isEmpty() || dayTypes == null || dayTypes.isEmpty()) {
                throw new ServiceException(bundle, CodeIncident.WARNING00002, CodeDetailIncident.TIMETABLE_EMPTY, lineNumber, calendrierEnCours.getVersion(), calendrierEnCours.getComment());
            }
        }
    }

    private boolean isTitreNouvelleDonnee(String titre) {
        if (titre == null) {
            return false;
        }
        return cleCommentaire.equals(titre.trim());
    }

    private boolean isO(String valeur) {
        if ((valeur == null) || (valeur.trim().length() == 0)) {
            throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.NULL_DAY_VALUE, lineNumber);
        }
        if (!"O".equalsIgnoreCase(valeur) && !"N".equalsIgnoreCase(valeur)) {
            throw new ServiceException(bundle, CodeIncident.ERROR00002, CodeDetailIncident.INVALID_DAY_VALUE, lineNumber, valeur);
        }
        if ("O".equalsIgnoreCase(valeur)) {
            return true;
        }
        return false;
    }

    @Override
    public Map<String, TableauMarche> getTableauxMarchesParRef() {
        return caldendriersParRef;
    }

    public void setTableauxMarchesParRef(Map<String, TableauMarche> caldendriersParRef) {
        this.caldendriersParRef = caldendriersParRef;
    }

    public IIdentificationManager getIdentificationManager() {
        return identificationManager;
    }

    public void setIdentificationManager(IIdentificationManager identificationManager) {
        this.identificationManager = identificationManager;
    }

    public String getCleAlias() {
        return cleAlias;
    }

    public void setCleAlias(String cleAlias) {
        this.cleAlias = cleAlias;
    }

    public String getCleJour() {
        return cleJour;
    }

    public void setCleJour(String cleJour) {
        this.cleJour = cleJour;
    }

    public String getCleDebut() {
        return cleDebut;
    }

    public void setCleDebut(String cleDebut) {
        this.cleDebut = cleDebut;
    }

    public String getCleFin() {
        return cleFin;
    }

    public void setCleFin(String cleFin) {
        this.cleFin = cleFin;
    }

    public String getCleLundi() {
        return cleLundi;
    }

    public void setCleLundi(String cleLundi) {
        this.cleLundi = cleLundi;
    }

    public String getCleMardi() {
        return cleMardi;
    }

    public void setCleMardi(String cleMardi) {
        this.cleMardi = cleMardi;
    }

    public String getCleMercredi() {
        return cleMercredi;
    }

    public void setCleMercredi(String cleMercredi) {
        this.cleMercredi = cleMercredi;
    }

    public String getCleJeudi() {
        return cleJeudi;
    }

    public void setCleJeudi(String cleJeudi) {
        this.cleJeudi = cleJeudi;
    }

    public String getCleVendredi() {
        return cleVendredi;
    }

    public void setCleVendredi(String cleVendredi) {
        this.cleVendredi = cleVendredi;
    }

    public String getCleSamedi() {
        return cleSamedi;
    }

    public void setCleSamedi(String cleSamedi) {
        this.cleSamedi = cleSamedi;
    }

    public String getCleDimanche() {
        return cleDimanche;
    }

    public void setCleDimanche(String cleDimanche) {
        this.cleDimanche = cleDimanche;
    }

    public String getCleCommentaire() {
        return cleCommentaire;
    }

    public void setCleCommentaire(String cleCommentaire) {
        this.cleCommentaire = cleCommentaire;
    }

    public int getColonneDesTitres() {
        return colonneDesTitres;
    }

    public void setColonneDesTitres(int colonneDesTitres) {
        this.colonneDesTitres = colonneDesTitres;
    }

    public String getCleDateFormat() {
        return cleDateFormat;
    }

    public void setCleDateFormat(String cleDateFormat) {
        this.cleDateFormat = cleDateFormat;
    }
}
