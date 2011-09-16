package fr.certu.chouette.struts.vehicleJourneyAtStop;

import chouette.schema.types.DayTypeType;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.outil.pagination.Pagination;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.database.ICourseManager;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.validation.SkipValidation;

public class VehicleJourneyAtStopAction extends GeneriqueAction implements ModelDriven<VehicleJourneyAtStopModel>, Preparable {

    private static final Log log = LogFactory.getLog(VehicleJourneyAtStopAction.class);
    private ICourseManager courseManager;
    private IPositionGeographiqueManager positionGeographiqueManager;
    private IHoraireManager horaireManager;
    private IItineraireManager itineraireManager;
    private IMissionManager missionManager;
    private int maxNbCoursesParPage;
    private int maxNbCalendriersParCourse;
    private List<Course> courses;
    private Pagination pagination;
    private VehicleJourneyAtStopModel model = new VehicleJourneyAtStopModel();
    private Long idItineraire;
    private Long idLigne;
    private Long idTableauMarche;
    private Date seuilDateDepartCourse = null;
    public static String actionMsg = null;
    public static String actionErr = null;
    
    public void setIdItineraire(Long idItineraire) {
        this.idItineraire = idItineraire;
    }

    public Long getIdItineraire() {
        return idItineraire;
    }

    public void setIdLigne(Long idLigne) {
        this.idLigne = idLigne;
    }

    public Long getIdLigne() {
        return idLigne;
    }

    public Long getIdTableauMarche() {
        //String timetableId = idTableauMarche == null ? "null" : idTableauMarche.toString();
        return idTableauMarche;
    }

    public void setIdTableauMarche(Long idTableauMarche) {
        this.idTableauMarche = idTableauMarche;
    }

    public Date getSeuilDateDepartCourse() {
        return seuilDateDepartCourse;
    }

    public void setSeuilDateDepartCourse(Date seuilDateDepartCourse) {
        this.seuilDateDepartCourse = seuilDateDepartCourse;
    }

    /********************************************************
     *                  MODEL + PREPARE                     *
     ********************************************************/
    @Override
    public VehicleJourneyAtStopModel getModel() {
        return model;
    }

    @Override
    public void prepare() throws Exception {
        if (idItineraire != null) {
            log.debug("Filter for vehicle journey with itinerary : " + idItineraire + ", and timetable : " + getIdTableauMarche() + ", and begin hour : " + getSeuilDateDepartCourse());
            // RECUPERATION DES COURSES
            courses = courseManager.getCoursesFiltrees(idItineraire, getIdTableauMarche(), getSeuilDateDepartCourse());
            log.debug("Courses size: " + courses.size());

            // GESTION DE LA PAGINATION
            if (pagination.getNumeroPage() == null || pagination.getNumeroPage() < 1) {
                pagination.setNumeroPage(1);
            }
            log.debug("Page number : " + pagination.getNumeroPage());
            pagination.setNbTotalColonnes(courses.size());
            List<Course> coursesPage = (List<Course>) pagination.getCollectionPageCourante(courses);
            log.debug("coursesPage.size()                       : " + coursesPage.size());
            model.setCoursesPage(coursesPage);
            // GESTION DES ARRETS DE L'ITINERAIRE
            List<ArretItineraire> arretsItineraire = itineraireManager.getArretsItineraire(idItineraire);
            log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
            model.setArretsItineraire(arretsItineraire);
            Map<Long, PositionGeographique> arretPhysiqueParIdArret = positionGeographiqueManager.getArretPhysiqueParIdArret(arretsItineraire);

            model.setArretPhysiqueParIdArret(arretPhysiqueParIdArret);

            // PREPARATION DE LA LISTE DES TM POUR LE FILTRE
            Map<Long, String> commentParTMid = itineraireManager.getCommentParTMId(idItineraire);
            Iterator timeTableIterator = commentParTMid.entrySet().iterator();
            int index = 1;
            while (timeTableIterator.hasNext()) {
                Map.Entry pairs = (Map.Entry) timeTableIterator.next();
                pairs.setValue("(" + index + ") " + pairs.getValue());
                index++;
            }
            model.setTableauxMarche(commentParTMid);

            // PREPARATION DES ELEMENTS NECESSAIRES A L'AFFICHAGE DE L'ENTETE DU TABLEAU
            prepareMapPositionArretParIdArret(arretsItineraire);
            prepareHoraires(arretsItineraire, coursesPage);
            prepareMapMissionParIdCourse(coursesPage);
            prepareMapsTableauxMarche();

        }
    }

    private void prepareMapPositionArretParIdArret(List<ArretItineraire> arretsItineraire) {
        Map<Long, Integer> positionArretParIdArret = new HashMap<Long, Integer>();
        for (ArretItineraire arret : arretsItineraire) {
            positionArretParIdArret.put(arret.getId(), arret.getPosition());
        }
        model.setPositionArretParIdArret(positionArretParIdArret);
    }

    private void prepareHoraires(List<ArretItineraire> arretsItineraire, List<Course> coursesPage) {
        if (coursesPage != null && arretsItineraire.size() > 0) {
            List<Long> idsCourses = new ArrayList<Long>();
            for (Course course : coursesPage) {
                idsCourses.add(course.getId());
            }
            Map<Long, List<Horaire>> horairesCourseParIdCourse = getMapHorairesCourseParIdCourse(idsCourses);
            Map<Long, List<Horaire>> horairesCourseOrdonneesParIdCourse = new HashMap<Long, List<Horaire>>();
            // RECUPERER LES HORAIRES DE COURSE POUR
            // CHAQUE COURSE DE LA PAGE
            List<Horaire> tmpHorairesCourseOrdonnees = new ArrayList<Horaire>();
            for (Long idCourse : idsCourses) {
                List<Horaire> horairesCourseOrdonnees = obtenirHorairesCourseOrdonnees(horairesCourseParIdCourse.get(idCourse), arretsItineraire);
                horairesCourseOrdonneesParIdCourse.put(idCourse, horairesCourseOrdonnees);
                tmpHorairesCourseOrdonnees.addAll(horairesCourseOrdonnees);
            }
            log.debug("horairesCourses.size()               : " + tmpHorairesCourseOrdonnees.size());
            model.setHorairesCourses(tmpHorairesCourseOrdonnees);
            model.setHorairesParIdCourse(horairesCourseOrdonneesParIdCourse);
            // RECUPERER LES HEURES DE COURSE POUR
            // CHAQUE COURSE DE LA PAGE
            idsCourses = new ArrayList<Long>();
            for (Course course : coursesPage) {
                idsCourses.add(course.getId());
            }
            List<Date> heuresCourses = new ArrayList<Date>(idsCourses.size() * arretsItineraire.size());
            for (Long idCourse : idsCourses) {
                List<Horaire> horairesCourseOrdonnees = obtenirHorairesCourseOrdonnees(horairesCourseParIdCourse.get(idCourse), arretsItineraire);
                heuresCourses.addAll(obtenirDatesDepartFromHoraires(horairesCourseOrdonnees));
            }
            model.setHeuresCourses(heuresCourses);
        }
    }

    private void prepareMapMissionParIdCourse(List<Course> coursesPage) {
        List<Long> idsMissionAffichee = new ArrayList<Long>();
        for (Course course : coursesPage) {
            idsMissionAffichee.add(course.getIdMission());
        }
        List<Mission> missions = missionManager.getMissions(idsMissionAffichee);
        Map<Long, Mission> missionParIdCourse = new HashMap<Long, Mission>();
        for (Mission mission : missions) {
            missionParIdCourse.put(mission.getId(), mission);
        }
        model.setMissionParIdCourse(missionParIdCourse);
        log.debug("fin prepareMapMissionParIdCourse");
    }

    /*
     * Get Timetables order for Vehicle Journey from filter
     */
    private void prepareMapsTableauxMarche() {
        log.debug("appel prepareMapsTableauxMarche");
        // TimeTables ids for each vehcicle journeyid
        Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse = new HashMap<Long, SortedSet<Integer>>();
        // TimeTables ids from filter
        List<Long> timeTableId = new ArrayList(model.getTableauxMarche().keySet());

        Map<Long, List<Long>> tmsParCourseId = courseManager.getTimeTablesIdByRouteId(idItineraire);
        for (Long courseId : tmsParCourseId.keySet()) {
            SortedSet<Integer> timeTablesOrder = new TreeSet<Integer>();
            List<Long> tms = tmsParCourseId.get(courseId);

            for (Long tm : tms) {
                if (timeTableId.contains(tm)) {
                    timeTablesOrder.add(timeTableId.indexOf(tm) + 1);
                }
            }
            tableauxMarcheParIdCourse.put(courseId, timeTablesOrder);
        }

        model.setTableauxMarcheParIdCourse(tableauxMarcheParIdCourse);
        log.debug("fin prepareMapsTableauxMarche");
    }

    /********************************************************
     *                           CRUD                       *
     ********************************************************/
    @SkipValidation
    public String list() {
        if (actionMsg != null) {
            addActionMessage(actionMsg);
            actionMsg = null;
        }
        if (actionErr != null) {
            addActionError(actionErr);
            actionErr = null;
        }
        return LIST;
    }

    @SkipValidation
    public String search() {
        return SEARCH;
    }

    public String cancel() {
        setIdTableauMarche(null);
        setSeuilDateDepartCourse(null);
        actionMsg = getText("horairesDePassage.cancel.ok");
        return REDIRECTLIST;
    }

    @Override
    public String input() throws Exception {
        return LIST;
    }

    public String ajoutCourseAvecDecalageTemps() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(model.getTempsDecalage());
        long tempsDecalageMillis = cal.get(Calendar.HOUR_OF_DAY) * 3600000 + cal.get(Calendar.MINUTE) * 60000;
        List<ArretItineraire> arretsItineraire = model.getArretsItineraire();
        Long idCourseADecaler = model.getIdCourseADecaler();
        Integer nbreCourseDecalage = model.getNbreCourseDecalage();
        if (idCourseADecaler == null) {
            actionErr = getText("course.decalage.noid");
            return REDIRECTLIST;
        }
        if (nbreCourseDecalage == null || nbreCourseDecalage.intValue() <= 0) {
            actionErr = getText("course.decalage.nonewcourse");
            return REDIRECTLIST;
        }
        if (tempsDecalageMillis / 1000 <= 0) {
            actionErr = getText("course.decalage.nogap");
            return REDIRECTLIST;
        }
        // Récupération des horaires de la course qu'il faut décaler d'un certain temps
        List<Horaire> horairesADecaler = model.getHorairesParIdCourse().get(idCourseADecaler);
        //log.debug("horairesADecaler : " + horairesADecaler);
        List<Horaire> horairesADecalerResultat = new ArrayList<Horaire>();
        //création de la liste des ids des tableaux de marche de la course de référence
        List<Long> tableauxMarcheIds = courseManager.getTimeTablesIdByVehicleJourneyId(idCourseADecaler);
        int nbreCourseDecalageInt = nbreCourseDecalage.intValue();
        int count = 0;
        decalage:
        for (int i = 0; i < nbreCourseDecalage; i++) {
            // Création d'une course
            Course course = new Course();
            course.setIdItineraire(getIdItineraire());
            courseManager.creer(course);
            // Ajout du temps de décalage à toutes les dates
            int compteurHoraire = 0;
            Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
            boolean isfirstHoraire = true;
            for (Horaire horaire : horairesADecaler) {
                if (horaire != null) {
                    Date heureDepartOrigine = horaire.getDepartureTime();
                    Date heureDepartResultat = new Date(heureDepartOrigine.getTime() + tempsDecalageMillis);
                    if (isfirstHoraire && heureDepartResultat.before(heureDepartOrigine)) {
                        actionErr = "course.decalage.partial";
                        nbreCourseDecalageInt = count;
                        break decalage;
                    }
                    isfirstHoraire =false;
                    //	Mise à jour de la liste d'horaire résultat
                    Horaire horaireResultat = new Horaire();
                    horaireResultat.setIdArret(horaire.getIdArret());
                    horaireResultat.setIdCourse(horaire.getIdCourse());
                    horaireResultat.setDepartureTime(heureDepartResultat);
                    horairesADecalerResultat.add(horaireResultat);
                    Long idArretItineraire = getIdArretParIndice(compteurHoraire, arretsItineraire);
                    majHoraires.add(EtatMajHoraire.getCreation(idArretItineraire, course.getId(), heureDepartResultat));
                } else {
                    horairesADecalerResultat.add(null);
                }
                compteurHoraire++;
            }
            count++;
            courseManager.modifier(course);
            // Copie des tableaux de marche de la course de référence dans la nouvelle
            courseManager.associerCourseTableauxMarche(course.getId(), tableauxMarcheIds);
            horaireManager.modifier(majHoraires);
            horairesADecaler = horairesADecalerResultat;
            horairesADecalerResultat = new ArrayList<Horaire>();
        }
        String[] args = {""+idCourseADecaler.longValue(), ""+nbreCourseDecalageInt, ""+(tempsDecalageMillis / 1000)};
        actionMsg = getText("course.decalage.ok", args);
        return REDIRECTLIST;
    }

    public String editerHorairesCourses() {
        List<Date> heuresCourses = model.getHeuresCourses();
        log.debug("heuresCourses.size()                     : " + heuresCourses.size());
        List<ArretItineraire> arretsItineraire = model.getArretsItineraire();
        log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
        List<Integer> idsHorairesInvalides = horaireManager.filtreHorairesInvalides(heuresCourses, arretsItineraire.size());
        model.setIdsHorairesInvalides(idsHorairesInvalides);
        if (idsHorairesInvalides != null && !idsHorairesInvalides.isEmpty()) {
            addActionError(getText("error.horairesInvalides"));
            return INPUT;
        }
        int indexPremiereDonneeDansCollectionPaginee = pagination.getIndexPremiereDonneePageCouranteDansCollectionPaginee(arretsItineraire.size());
        log.debug("indexPremiereDonneeDansCollectionPaginee : " + indexPremiereDonneeDansCollectionPaginee);
        log.debug("horairesCourses.size()                   : " + model.getHorairesCourses().size());
        Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
        for (int i = 0; i < model.getHorairesCourses().size(); i++) {
            Date heureCourse = model.getHeuresCourses().get(i);
            Horaire horaireCourse = model.getHorairesCourses().get(i);
            if (horaireCourse == null) {
                log.debug("horaireCourse.getIdCourse()              : NULL");
            } else {
                log.debug("idCourse : stopPointId : departureTime   : " + horaireCourse.getIdCourse() + " : " + horaireCourse.getStopPointId() + " : " + horaireCourse.getDepartureTime().toString());
            }
            if (heureCourse != null && horaireCourse == null) {
                EtatMajHoraire etatMajHoraire = EtatMajHoraire.getCreation(
                        getIdArretParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire),
                        getIdCourseParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire),
                        heureCourse);
                majHoraires.add(etatMajHoraire);
            } else if (heureCourse == null && horaireCourse != null) {
                majHoraires.add(EtatMajHoraire.getSuppression(horaireCourse));
            } else if (areBothDefinedAndDifferent(heureCourse, horaireCourse)) {
                horaireCourse.setDepartureTime(heureCourse);
                majHoraires.add(EtatMajHoraire.getModification(horaireCourse));
            }
            indexPremiereDonneeDansCollectionPaginee++;
        }
        horaireManager.modifier(majHoraires);
        return REDIRECTLIST;
    }

    public String editerHorairesCoursesConfirmation() {
        List<ArretItineraire> arretsItineraire = model.getArretsItineraire();
        int indexPremiereDonneePagination = pagination.getIndexPremiereDonneePageCouranteDansCollectionPaginee(arretsItineraire.size());
        Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
        for (int i = 0; i < model.getHorairesCourses().size(); i++) {
            Date heureDepart = model.getHeuresCourses().get(i);
            Horaire horaire = model.getHorairesCourses().get(i);
            if (heureDepart != null && horaire == null) {
                majHoraires.add(EtatMajHoraire.getCreation(
                        getIdArretParIndice(indexPremiereDonneePagination, arretsItineraire),
                        getIdCourseParIndice(indexPremiereDonneePagination, arretsItineraire),
                        heureDepart));
            } else if (heureDepart == null && horaire != null) {
                majHoraires.add(EtatMajHoraire.getSuppression(horaire));
            } else if (areBothDefinedAndDifferent(heureDepart, horaire)) {
                horaire.setDepartureTime(heureDepart);
                majHoraires.add(EtatMajHoraire.getModification(horaire));
            }
            indexPremiereDonneePagination++;
        }
        horaireManager.modifier(majHoraires);
        return REDIRECTLIST;
    }

    /********************************************************
     *                           OTHERS                       *
     ********************************************************/
    private List<Date> obtenirDatesDepartFromHoraires(List<Horaire> horaires) {
        List<Date> dates = new ArrayList<Date>(horaires.size());
        for (Horaire horaireCourse : horaires) {
            dates.add(horaireCourse == null ? null : horaireCourse.getDepartureTime());
        }
        return dates;
    }

    private List<Horaire> obtenirHorairesCourseOrdonnees(List<Horaire> horairesCourse, List<ArretItineraire> arretsItineraire) {
        Horaire[] horairesCourseOrdonnees = new Horaire[arretsItineraire.size()];
        if (horairesCourse != null) {
            for (Horaire horaireCourse : horairesCourse) {
                Integer positionHoraire = model.getPositionArretParIdArret().get(horaireCourse.getIdArret());
                if (positionHoraire != null) {
                    horairesCourseOrdonnees[positionHoraire] = horaireCourse;
                } else {
                    log.error("L'horaire " + horaireCourse.getId() + " à l'arret " + horaireCourse.getIdArret() + " n'a pas de position connue sur l'itinéraire!");
                }
            }
        }
        return Arrays.asList(horairesCourseOrdonnees);
    }

    private Map<Long, List<Horaire>> getMapHorairesCourseParIdCourse(Collection<Long> idCourses) {
        List<Horaire> horaires = courseManager.getHorairesCourses(idCourses);
        Map<Long, List<Horaire>> horairesCourseParIdCourse = new HashMap<Long, List<Horaire>>();
        for (Horaire horaire : horaires) {
            Long idCourseCourante = horaire.getIdCourse();
            List<Horaire> horairesCourse = horairesCourseParIdCourse.get(idCourseCourante);
            if (horairesCourse == null) {
                horairesCourse = new ArrayList<Horaire>();
                horairesCourseParIdCourse.put(idCourseCourante, horairesCourse);
            }
            horairesCourse.add(horaire);
        }
        return horairesCourseParIdCourse;
    }

    /********************************************************
     *                        MANAGER                       *
     ********************************************************/
    public void setCourseManager(ICourseManager courseManager) {
        this.courseManager = courseManager;
    }

    public void setHoraireManager(IHoraireManager horaireManager) {
        this.horaireManager = horaireManager;
    }

    public void setItineraireManager(IItineraireManager itineraireManager) {
        this.itineraireManager = itineraireManager;
    }

    public void setMissionManager(IMissionManager missionManager) {
        this.missionManager = missionManager;
    }

    public void setPositionGeographiqueManager(IPositionGeographiqueManager positionGeographiqueManager) {
        this.positionGeographiqueManager = positionGeographiqueManager;
    }

    /********************************************************
     *                        OTHERS                        *
     ********************************************************/
    private boolean areBothDefinedAndDifferent(Date heureSaisie, Horaire horaireBase) {
        return heureSaisie != null && horaireBase != null && horaireBase.getDepartureTime() != null && (horaireBase.getDepartureTime().compareTo(heureSaisie) != 0);
    }

    private Long getIdArretParIndice(int indice, List<ArretItineraire> arretsItineraire) {
        int indiceArret = indice % arretsItineraire.size();
        return arretsItineraire.get(indiceArret).getId();
    }

    private Long getIdCourseParIndice(int indice, List<ArretItineraire> arretsItineraire) {
        int indiceCourse = indice / arretsItineraire.size();
        return courses.get(indiceCourse).getId();
    }

    public String getRouteName() {
        if (idItineraire != null) {
            return itineraireManager.lire(idItineraire).getName();
        } else {
            return "";
        }
    }

    /********************************************************
     *                           ERROR                      *
     ********************************************************/
    public boolean isDayTypeDansDayTypes(DayTypeType dayType, Set<DayTypeType> dayTypes) {
        if (dayTypes.contains(dayType)) {
            return true;
        }
        return false;
    }

    public Boolean isErreurAjoutCourseAvecDecalageTemps() {
        if (getFieldErrors().containsKey("tempsDecalage") || getFieldErrors().containsKey("nbreCourseDecalage")) {
            return true;
        }
        return false;
    }

    public Boolean isErreurHorairesInvalides() {
        Collection<String> actionErrors = getActionErrors();
        if (actionErrors.contains(getText("error.horairesInvalides"))) {
            return true;
        }
        return false;
    }

    /********************************************************
     *                           PAGE                       *
     ********************************************************/
    public int getPage() {
        return pagination.getNumeroPage();
    }

    public void setPage(int page) {
        pagination.setNumeroPage(page);
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public int getMaxNbCoursesParPage() {
        return maxNbCoursesParPage;
    }

    public void setMaxNbCoursesParPage(int maxNbCoursesParPage) {
        log.debug("Number Vehicle Journey maximum for 1 page : " + maxNbCoursesParPage);
        this.maxNbCoursesParPage = maxNbCoursesParPage;
    }

    public int getMaxNbCalendriersParCourse() {
        return maxNbCalendriersParCourse;
    }

    public void setMaxNbCalendriersParCourse(int maxNbCalendriersParCourse) {
        this.maxNbCalendriersParCourse = maxNbCalendriersParCourse;
    }
}
