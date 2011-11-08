package fr.certu.chouette.struts.vehicleJourneyAtStop;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import chouette.schema.types.DayTypeType;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.outil.pagination.Pagination;

public class VehicleJourneyAtStopAction extends GeneriqueAction implements ModelDriven<VehicleJourneyAtStopModel>, Preparable
{
	private static final long serialVersionUID = 8853857975522453126L;

	private static final Logger log = Logger.getLogger(VehicleJourneyAtStopAction.class);

	@Getter @Setter private INeptuneManager<VehicleJourney> vehicleJourneyManager;
	@Getter @Setter private INeptuneManager<Route> routeManager;
	@Getter @Setter private INeptuneManager<JourneyPattern> journeyPatternManager;
	@Setter @Getter private int maxNbCoursesParPage;
	@Setter @Getter private int maxNbCalendriersParCourse;
	@Setter @Getter private Pagination pagination;
	@Getter private VehicleJourneyAtStopModel model = new VehicleJourneyAtStopModel();
	@Getter @Setter private Long idItineraire;
	@Getter @Setter private Long idLigne;
	@Getter @Setter private Long idTableauMarche;
	@Getter @Setter private Time seuilHeureDepartCourse = null;
	
	private User user = null;

	public static String actionMsg = null;
	public static String actionErr = null;

	/********************************************************
	 *                  MODEL + PREPARE                     *
	 ********************************************************/

	public void prepare() throws Exception
	{
		log.debug("appel prepare");
		if (idItineraire != null)
		{
			log.debug("Filter for vehicle journey with itinerary : " + idItineraire + ", and timetable : " + getIdTableauMarche() + ", and begin hour : " + getSeuilHeureDepartCourse());
			// RECUPERATION DES COURSES

			Route route = model.getRoute();
			if (route == null || !route.getId().equals(idItineraire))
			{
				route = routeManager.getById(idItineraire);
				model.setRoute(route);
			}

			List<VehicleJourney> courses = new ArrayList<VehicleJourney>();

			for (JourneyPattern jp : route.getJourneyPatterns())
			{
				for (VehicleJourney vehicleJourney : jp.getVehicleJourneys()) 
				{
					boolean isValideTimeTable = (idTableauMarche == null);
					boolean isValideVJAtStop = (getSeuilHeureDepartCourse() == null);
					if(!isValideTimeTable)
					{
						for (Timetable timetable : vehicleJourney.getTimetables())
						{
							if(timetable != null && timetable.getId() != null)
							{
								if(timetable.getId().equals(getIdTableauMarche()))
								{
									isValideTimeTable = true;
									break;
								}	
							}
						}
					}
					if (isValideTimeTable && !isValideVJAtStop)
					{
						// check only first stop
						if (vehicleJourney.getVehicleJourneyAtStops() != null && !vehicleJourney.getVehicleJourneyAtStops().isEmpty())
						{
							VehicleJourneyAtStop atStop = vehicleJourney.getVehicleJourneyAtStops().get(0);

							if(atStop.getDepartureTime() != null) 
							{
								if(atStop.getDepartureTime().after(getSeuilHeureDepartCourse()))
								{
									isValideVJAtStop = true;
								}	
							}
						}

					}
					if(isValideTimeTable && isValideVJAtStop)
						courses.add(vehicleJourney);
				}
			}
			// trier les courses : 
			Collections.sort(courses,new VehicleJourneyStartTimeComparator());

			log.debug("Courses size: " + courses.size());
			model.setCourses(courses);
			model.setCoursesParIdCourse(VehicleJourney.mapOnIds(courses));

			// GESTION DE LA PAGINATION
			if (pagination.getNumeroPage() == null || pagination.getNumeroPage() < 1)
			{
				pagination.setNumeroPage(1);
			}
			log.debug("Page number : " + pagination.getNumeroPage());
			pagination.setNbTotalColonnes(courses.size());
			List<VehicleJourney> coursesPage =  pagination.getCollectionPageCourante(courses);
			log.debug("coursesPage.size()                       : " + coursesPage.size());
			model.setCoursesPage(coursesPage);
			// GESTION DES ARRETS DE L'ITINERAIRE

			List<StopPoint> arretsItineraire = route.getStopPoints();

			log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
			model.setArretsItineraire(arretsItineraire);
			Map<Long, StopArea> arretPhysiqueParIdArret = getArretPhysiqueParIdArret(arretsItineraire);

			model.setArretPhysiqueParIdArret(arretPhysiqueParIdArret);

			// PREPARATION DE LA LISTE DES TM POUR LE FILTRE
			Map<Long, String> commentParTMid = getCommentParTMId(idItineraire);
			Iterator<Entry<Long,String>> timeTableIterator = commentParTMid.entrySet().iterator();
			int index = 1;
			while (timeTableIterator.hasNext())
			{
				Map.Entry<Long,String> pairs = timeTableIterator.next();
				pairs.setValue("(" + index + ") " + pairs.getValue());
				index++;
			}
			model.setTableauxMarche(commentParTMid);

			// PREPARATION DES ELEMENTS NECESSAIRES A L'AFFICHAGE DE L'ENTETE DU TABLEAU
			prepareMapPositionArretParIdArret(arretsItineraire);
			prepareHoraires(arretsItineraire, coursesPage);
			prepareMapMissionParIdMission(coursesPage);
			prepareMapsTableauxMarche(courses);

		}
		log.debug("fin prepare");
	}

	private void prepareMapPositionArretParIdArret(List<StopPoint> arretsItineraire)
	{
		log.debug("appel prepareMapPositionArretParIdArret");
		Map<Long, Integer> positionArretParIdArret = new Hashtable<Long, Integer>();
		for (StopPoint arret : arretsItineraire)
		{
			positionArretParIdArret.put(arret.getId(), arret.getPosition());
		}
		model.setPositionArretParIdArret(positionArretParIdArret);
		log.debug("fin prepareMapPositionArretParIdArret");
	}

	private void prepareHoraires(List<StopPoint> arretsItineraire, List<VehicleJourney> coursesPage)
	{
		log.debug("appel prepareHoraires");
		if (coursesPage != null && arretsItineraire.size() > 0)
		{
			List<Long> idsCourses = new ArrayList<Long>();
			for (VehicleJourney course : coursesPage)
			{
				idsCourses.add(course.getId());
			}
			Map<Long, List<VehicleJourneyAtStop>> horairesCourseParIdCourse = getMapHorairesCourseParIdCourse(coursesPage);
			Map<Long, List<VehicleJourneyAtStop>> horairesCourseOrdonneesParIdCourse = new HashMap<Long, List<VehicleJourneyAtStop>>();
			// RECUPERER LES HORAIRES DE COURSE POUR
			// CHAQUE COURSE DE LA PAGE
			List<VehicleJourneyAtStop> tmpHorairesCourseOrdonnees = new ArrayList<VehicleJourneyAtStop>();
			for (Long idCourse : idsCourses)
			{
				List<VehicleJourneyAtStop> horairesCourseOrdonnees = obtenirHorairesCourseOrdonnees(horairesCourseParIdCourse.get(idCourse), arretsItineraire);
				horairesCourseOrdonneesParIdCourse.put(idCourse, horairesCourseOrdonnees);
				tmpHorairesCourseOrdonnees.addAll(horairesCourseOrdonnees);
			}
			log.debug("horairesCourses.size()               : " + tmpHorairesCourseOrdonnees.size());
			model.setHorairesCourses(tmpHorairesCourseOrdonnees);
			model.setHorairesParIdCourse(horairesCourseOrdonneesParIdCourse);
			// RECUPERER LES HEURES DE COURSE POUR
			// CHAQUE COURSE DE LA PAGE
			idsCourses = new ArrayList<Long>();
			for (VehicleJourney course : coursesPage)
			{
				idsCourses.add(course.getId());
			}
			List<Time> heuresCourses = new ArrayList<Time>(idsCourses.size() * arretsItineraire.size());
			for (Long idCourse : idsCourses)
			{
				List<VehicleJourneyAtStop> horairesCourseOrdonnees = obtenirHorairesCourseOrdonnees(horairesCourseParIdCourse.get(idCourse), arretsItineraire);
				heuresCourses.addAll(obtenirHeuresDepartFromHoraires(horairesCourseOrdonnees));
			}
			model.setHeuresCourses(heuresCourses);
		}
		log.debug("fin prepareHoraires");
	}

	private void prepareMapMissionParIdMission(List<VehicleJourney> coursesPage) throws ChouetteException
	{
		log.debug("appel prepareMapMissionParIdCourse");
		Map<Long, JourneyPattern> missionParIdMission = new HashMap<Long, JourneyPattern>();
		for (VehicleJourney course : coursesPage)
		{
			JourneyPattern mission = course.getJourneyPattern();
			if (!missionParIdMission.containsKey(mission.getId()))
				missionParIdMission.put(mission.getId(), mission);
		}

		model.setMissionParIdMission(missionParIdMission);
		log.debug("fin prepareMapMissionParIdMission");
	}

	/*
	 * Get Timetables order for Vehicle Journey from filter
	 */
	private void prepareMapsTableauxMarche(List<VehicleJourney> vjs) throws ChouetteException
	{
		log.debug("appel prepareMapsTableauxMarche");
		// TimeTables ids for each vehicle journeyid
		Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse = new HashMap<Long, SortedSet<Integer>>();
		// TimeTables ids from filter
		List<Long> timeTableId = new ArrayList<Long>(model.getTableauxMarche().keySet());

		Map<Long, List<Long>> tmsParCourseId = new HashMap<Long, List<Long>>();
		for (VehicleJourney vehicleJourney : vjs) 
		{
			List<Long> timetableIds = new ArrayList<Long>();
			for (Timetable timetable : vehicleJourney.getTimetables()) 
			{
				timetableIds.add(timetable.getId());
			}
			tmsParCourseId.put(vehicleJourney.getId(), timetableIds);
		}


		for (Long courseId : tmsParCourseId.keySet())
		{
			SortedSet<Integer> timeTablesOrder = new TreeSet<Integer>();
			List<Long> tms = tmsParCourseId.get(courseId);

			for (Long tm : tms)
			{
				if (timeTableId.contains(tm))
				{
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
	public String list()
	{
		log.debug("appel list");
		if (actionMsg != null) {
			addActionMessage(actionMsg);
			actionMsg = null;
		}
		if (actionErr != null) {
			addActionError(actionErr);
			actionErr = null;
		}
		log.debug("fin list");
		return LIST;
	}

	@SkipValidation
	public String search()
	{
		log.debug("appel search");
		return SEARCH;
	}

	public String cancel()
	{
		log.debug("appel cancel");
		setIdTableauMarche(null);
		setSeuilHeureDepartCourse(null);
		actionMsg = getText("horairesDePassage.cancel.ok");
		log.debug("fin cancel");
		return REDIRECTLIST;
	}

	@Override
	public String input() throws Exception
	{
		log.debug("appel input");
		return LIST;
	}

	public String ajoutCourseAvecDecalageTemps() throws ChouetteException
	{
		log.debug("appel ajoutCourseAvecDecalageTemps");
      Calendar cal = Calendar.getInstance();
      cal.setTime(model.getTempsDecalage());
      long tempsDecalageMillis = cal.get(Calendar.HOUR_OF_DAY) * 3600000 + cal.get(Calendar.MINUTE) * 60000;
		Long idCourseADecaler = model.getIdCourseADecaler();
		Integer nbreCourseDecalage = model.getNbreCourseDecalage();
		if (idCourseADecaler == null) {
			actionErr = getText("course.decalage.noid");
			log.debug("fin ajoutCourseAvecDecalageTemps");
			return REDIRECTLIST;
		}
		if (nbreCourseDecalage == null || nbreCourseDecalage.intValue() <= 0) {
			actionErr = getText("course.decalage.nonewcourse");
			log.debug("fin ajoutCourseAvecDecalageTemps");
			return REDIRECTLIST;
		}
		if (tempsDecalageMillis / 1000 <= 0) {
			actionErr = getText("course.decalage.nogap");
			log.debug("fin ajoutCourseAvecDecalageTemps");
			return REDIRECTLIST;
		}


		// Récupération des horaires de la course qu'il faut décaler d'un certain temps
		VehicleJourney vehicleJourney = getCourseById(idCourseADecaler);
		List<VehicleJourneyAtStop> horairesADecaler = vehicleJourney.getVehicleJourneyAtStops();
		Route route = model.getRoute();
		JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();

		// gestion du décalage (EtatMajHoraire ???)
		int nbreCourseDecalageInt = nbreCourseDecalage.intValue();
		int count = 0;
		String objectIdPrefix = vehicleJourney.getObjectId().split(":")[0];
		decalage:
			for (int i = 0; i < nbreCourseDecalage; i++) 
			{
				// Création d'une course
				VehicleJourney course = vehicleJourneyManager.getNewInstance(user);
				// Ajout du temps de décalage à toutes les dates
				int compteurHoraire = 0;
				// Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
				boolean isfirstHoraire = true;
				for (VehicleJourneyAtStop horaire : horairesADecaler) 
				{
					if (horaire != null) {
						Time heureDepartOrigine = horaire.getDepartureTime();
						Time heureDepartResultat = new Time(heureDepartOrigine.getTime() + tempsDecalageMillis);
						if (isfirstHoraire && heureDepartResultat.before(heureDepartOrigine)) {
							actionErr = "course.decalage.partial";
							nbreCourseDecalageInt = count;
							break decalage;
						}
						isfirstHoraire =false;
						//	Mise à jour de la liste d'horaire résultat
						VehicleJourneyAtStop horaireResultat = new VehicleJourneyAtStop();
						course.addVehicleJourneyAtStop(horaireResultat);
						horaireResultat.setStopPoint(horaire.getStopPoint());
						horaireResultat.setVehicleJourney(horaire.getVehicleJourney());
						horaireResultat.setDepartureTime(heureDepartResultat);
					}
					compteurHoraire++;
				}
				course.setObjectId(objectIdPrefix);
				vehicleJourneyManager.save(user, course, false);
            count++;
				course.setRoute(route);
				course.setJourneyPattern(journeyPattern);
				journeyPattern.addVehicleJourney(course);
				course.addTimetables(vehicleJourney.getTimetables());
				horairesADecaler = course.getVehicleJourneyAtStops();
			}
		// end label decalage
		if (count > 0)
		{
			routeManager.update(user, route);
		}
		String[] args = {""+idCourseADecaler.longValue(), ""+nbreCourseDecalageInt, ""+(tempsDecalageMillis / 1000)};
		actionMsg = getText("course.decalage.ok", args);
		log.debug("fin ajoutCourseAvecDecalageTemps");
		return REDIRECTLIST;
	}

	private VehicleJourney getCourseById(Long idCourseADecaler) 
	{

		return getModel().getCoursesParIdCourse().get(idCourseADecaler);
	}

	public String editerHorairesCourses()
	{
		log.debug("appel editerHorairesCourses");
		return editerHorairesCoursesInterne(true);
	}

	private String editerHorairesCoursesInterne(boolean checkInvalidTimes)
	{
		log.debug("appel editerHorairesCoursesInterne");
		List<Time> heuresCourses = model.getHeuresCourses();
		log.debug("heuresCourses.size()                     : " + heuresCourses.size());
		List<StopPoint> arretsItineraire = model.getArretsItineraire();
		log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
		if (checkInvalidTimes)
		{
			List<Integer> idsHorairesInvalides = filtreHorairesInvalides(heuresCourses, arretsItineraire.size());
			model.setIdsHorairesInvalides(idsHorairesInvalides);
			if (idsHorairesInvalides != null && !idsHorairesInvalides.isEmpty())
			{
				addActionError(getText("error.horairesInvalides"));
				log.debug("fin editerHorairesCoursesInterne");
				return INPUT;
			}
		}
		int indexPremiereDonneeDansCollectionPaginee = pagination.getIndexPremiereDonneePageCouranteDansCollectionPaginee(arretsItineraire.size());
		log.debug("indexPremiereDonneeDansCollectionPaginee : " + indexPremiereDonneeDansCollectionPaginee);
		log.debug("horairesCourses.size()                   : " + model.getHorairesCourses().size());

		Set<VehicleJourney> coursesAsauver = new HashSet<VehicleJourney>();

		for (int i = 0; i < model.getHorairesCourses().size(); i++)
		{
			Time heureCourse = model.getHeuresCourses().get(i);
			VehicleJourneyAtStop horaireCourse = model.getHorairesCourses().get(i);
			if (horaireCourse == null)
			{
				log.debug("horaireCourse.getIdCourse()              : NULL");
			}

			if (heureCourse != null && horaireCourse == null)
			{
				log.debug("nouvel horaire sur nouvel arret");
				StopPoint arret = getArretParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire);
				VehicleJourney course = getCourseParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire);

				horaireCourse = new VehicleJourneyAtStop();
				horaireCourse.setStopPoint(arret);
				horaireCourse.setVehicleJourney(course);
				horaireCourse.setDepartureTime(heureCourse);
				horaireCourse.setArrivalTime(heureCourse);

				course.addVehicleJourneyAtStop(horaireCourse);
				coursesAsauver.add(course);

			} 
			else if (heureCourse == null && horaireCourse != null)
			{
				log.debug("horaire supprimé sur arret");
				VehicleJourney course = getCourseParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire);
				StopPoint arret = getArretParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire);
				course.removeStopPoint(arret);
				coursesAsauver.add(course);
			} 
			else if (areBothDefinedAndDifferent(heureCourse, horaireCourse))
			{
				log.debug("horaire modifié sur arret");
				VehicleJourney course = getCourseParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire);
				horaireCourse.setDepartureTime(heureCourse);
				coursesAsauver.add(course);
			}
			indexPremiereDonneeDansCollectionPaginee++;
		}
		try 
		{

			// check if new journeyPatterns are to be created
			List<JourneyPattern> journeyPatternAdded = new ArrayList<JourneyPattern>();
			for (VehicleJourney vehicleJourney : coursesAsauver) 
			{
				if (vehicleJourney.checkJourneyPattern() ) 
				{
					log.debug("mission modifiée sur course");
					if (vehicleJourney.getJourneyPattern().getId() == null)
					{
						// save journeyPattern
						log.debug("nouvelle mission");
						JourneyPattern jp = vehicleJourney.getJourneyPattern();
						journeyPatternAdded.add(jp);
					}
				}
			}
			// save change before new JourneyPatterns
			//routeManager.update(user, model.getRoute());

			if (journeyPatternAdded.size() > 0)
			{
				for (JourneyPattern jp : journeyPatternAdded) 
				{
					journeyPatternManager.save(user, jp,false );
					jp.setRoute(model.getRoute());
					model.getRoute().addJourneyPattern(jp);
				}
			}
			routeManager.update(user, model.getRoute());
		} 
		catch (Exception e) 
		{
			log.error("fail to save updated vehiclejourneys "+e.getMessage(),e);
			addActionError(e.getMessage());
		}
		log.debug("fin editerHorairesCoursesInterne");
		return REDIRECTLIST;
	}

	public String editerHorairesCoursesConfirmation()
	{
		log.debug("appel editerHorairesCoursesConfirmation");
		return editerHorairesCoursesInterne(false);
	}

	/********************************************************
	 *                           OTHERS                       *
	 ********************************************************/
	private List<Time> obtenirHeuresDepartFromHoraires(List<VehicleJourneyAtStop> horaires)
	{
		List<Time> dates = new ArrayList<Time>(horaires.size());
		for (VehicleJourneyAtStop horaireCourse : horaires)
		{
			dates.add(horaireCourse == null ? null : horaireCourse.getDepartureTime());
		}
		return dates;
	}

	private List<VehicleJourneyAtStop> obtenirHorairesCourseOrdonnees(List<VehicleJourneyAtStop> horairesCourse, List<StopPoint> arretsItineraire)
	{
		VehicleJourneyAtStop[] horairesCourseOrdonnees = new VehicleJourneyAtStop[arretsItineraire.size()];
		if (horairesCourse != null)
		{
			for (VehicleJourneyAtStop horaireCourse : horairesCourse)
			{
				Integer positionHoraire = model.getPositionArretParIdArret().get(horaireCourse.getStopPoint().getId());
				if (positionHoraire != null)
				{
					horairesCourseOrdonnees[positionHoraire] = horaireCourse;
				} else
				{
					log.error("L'horaire " + horaireCourse.getId() + " à l'arret " + horaireCourse.getStopPoint().getId() + " n'a pas de position connue sur l'itinéraire!");
				}
			}
		}
		return Arrays.asList(horairesCourseOrdonnees);
	}

	private Map<Long, List<VehicleJourneyAtStop>> getMapHorairesCourseParIdCourse(List<VehicleJourney> courses)
	{
		Map<Long, List<VehicleJourneyAtStop>> horairesCourseParIdCourse = new Hashtable<Long, List<VehicleJourneyAtStop>>();
		for (VehicleJourney vehicleJourney : courses) 
		{
			horairesCourseParIdCourse.put(vehicleJourney.getId(),vehicleJourney.getVehicleJourneyAtStops());
		}

		return horairesCourseParIdCourse;
	}


	/********************************************************
	 *                        OTHERS                        *
	 ********************************************************/
	private boolean areBothDefinedAndDifferent(Time heureSaisie, VehicleJourneyAtStop horaireBase)
	{
		return heureSaisie != null && horaireBase != null && horaireBase.getDepartureTime() != null && (horaireBase.getDepartureTime().compareTo(heureSaisie) != 0);
	}

	private StopPoint getArretParIndice(int indice, List<StopPoint> arretsItineraire)
	{
		int indiceArret = indice % arretsItineraire.size();
		return arretsItineraire.get(indiceArret);
	}

	private VehicleJourney getCourseParIndice(int indice, List<StopPoint> arretsItineraire)
	{
		int indiceCourse = indice / arretsItineraire.size();
		return model.getCourses().get(indiceCourse);
	}

	public String getRouteName() throws ChouetteException
	{
		if (idItineraire != null)
		{
			return model.getRoute().getName();
		} else
		{
			return "";
		}
	}

	/********************************************************
	 *                           ERROR                      *
	 ********************************************************/
	public boolean isDayTypeDansDayTypes(DayTypeType dayType, Set<DayTypeType> dayTypes)
	{
		if (dayTypes.contains(dayType))
		{
			return true;
		}
		return false;
	}

	public Boolean isErreurAjoutCourseAvecDecalageTemps()
	{
		if (getFieldErrors().containsKey("tempsDecalage") || getFieldErrors().containsKey("nbreCourseDecalage"))
		{
			return true;
		}
		return false;
	}

	public Boolean isErreurHorairesInvalides()
	{
		Collection<String> actionErrors = getActionErrors();
		if (actionErrors.contains(getText("error.horairesInvalides")))
		{
			return true;
		}
		return false;
	}

	/********************************************************
	 *                           PAGE                       *
	 ********************************************************/
	public int getPage()
	{
		return pagination.getNumeroPage();
	}

	public void setPage(int page)
	{
		pagination.setNumeroPage(page);
	}



	/**
	 * creation d'une map idArretItineraire vers arrêt physique
	 * @param arrets
	 * @return
	 * @throws ChouetteException
	 */
	private Map<Long, StopArea> getArretPhysiqueParIdArret( List<StopPoint> arrets) throws ChouetteException
	{
		Map<Long, StopArea> arretPhysiqueParIdArret = new Hashtable<Long, StopArea>();

		if ( arrets==null || arrets.isEmpty())
		{
			return arretPhysiqueParIdArret;
		}


		// Creation d'une hashtable liant id arret itineraire ->  arret physique
		for (StopPoint arret : arrets)
		{
			if (arret.getId() != null)
			{
				StopArea arretPhysique = arret.getContainedInStopArea();
				if(arretPhysique != null)
					arretPhysiqueParIdArret.put(arret.getId(), arretPhysique);
			}
		}		
		return arretPhysiqueParIdArret;
	}

	private Map<Long,String> getCommentParTMId(final Long idItineraire) throws ChouetteException
	{
		log.debug("appel getCommentParTMId");
		Map<Long, String> result = new HashMap<Long, String>();
		List<VehicleJourney> vehicleJourneys = model.getCourses();
		for (VehicleJourney vehicleJourney : vehicleJourneys) 
		{
			if (vehicleJourney.getRoute().getId().equals(idItineraire))
			{
				for (Timetable timetable : vehicleJourney.getTimetables())
				{
					result.put(timetable.getId(), timetable.getComment());	
				}
			}
		}
		log.debug("fin getCommentParTMId");
		return result;
	}

	private List<Integer> filtreHorairesInvalides( List<Time> horairesModifie, int totalArrets) {
		Time precedent = null;
		List<Integer> indexsHorairesInvalides = new ArrayList<Integer>();
		int total = horairesModifie.size();
		for (int i = 0; i < total; i++) {
			Time courant = horairesModifie.get(i);
			// sur changement de course, reinitialiser
			if ((i%totalArrets)==0)
				precedent = null;
			if (courant != null) {
				if (precedent==null)
					precedent = courant;
				int delta = ((int)((courant.getTime() - precedent.getTime()) / 1000L)) % (3600 * 24);
				if (delta < 0)
					delta += (3600 * 24);
				if (3600 < delta)
					indexsHorairesInvalides.add(Integer.valueOf(i));
				precedent = courant;
			}
		}
		return indexsHorairesInvalides;
	}

	class VehicleJourneyStartTimeComparator implements Comparator<VehicleJourney>
	{

		@Override
		public int compare(VehicleJourney o1, VehicleJourney o2) 
		{
			if (o1.getVehicleJourneyAtStops() == null) return -1;
			if (o2.getVehicleJourneyAtStops() == null) return -1;
			if (o1.getVehicleJourneyAtStops().isEmpty()) return -1;
			if (o2.getVehicleJourneyAtStops().isEmpty()) return -1;
			VehicleJourneyAtStop stop1 = o1.getVehicleJourneyAtStops().get(0);
			VehicleJourneyAtStop stop2 = o2.getVehicleJourneyAtStops().get(0);
			if (stop1.getDepartureTime() == null) return -1;
			if (stop2.getDepartureTime() == null) return -1;

			return (int) (stop1.getDepartureTime().getTime() - stop2.getDepartureTime().getTime());
		}

	}

}
