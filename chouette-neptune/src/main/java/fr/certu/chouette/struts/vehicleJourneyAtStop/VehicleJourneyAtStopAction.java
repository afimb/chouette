package fr.certu.chouette.struts.vehicleJourneyAtStop;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.outil.pagination.Pagination;

public class VehicleJourneyAtStopAction extends GeneriqueAction implements ModelDriven<VehicleJourneyAtStopModel>, Preparable
{

	private static final Logger log = Logger.getLogger(VehicleJourneyAtStopAction.class);

	@Getter @Setter private INeptuneManager<VehicleJourney> vehicleJourneyManager;
	@Setter @Getter private INeptuneManager<StopArea> stopAreaManager;
	@Getter @Setter private INeptuneManager<Route> routeManager;
	@Getter @Setter private INeptuneManager<JourneyPattern> journeyPatternManager;
	@Setter @Getter private INeptuneManager<StopPoint> stopPointManager;
	private int maxNbCoursesParPage;
	private int maxNbCalendriersParCourse;
	private List<VehicleJourney> courses = new ArrayList<VehicleJourney>();
	private Pagination pagination;
	private VehicleJourneyAtStopModel model = new VehicleJourneyAtStopModel();
	private Long idItineraire;
	private Long idLigne;
	private Long idTableauMarche;
	private Date seuilDateDepartCourse = null;
	private DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
	public static String actionMsg = null;
	public static String actionErr = null;


	public void setIdItineraire(Long idItineraire)
	{
		this.idItineraire = idItineraire;
	}

	public Long getIdItineraire()
	{
		return idItineraire;
	}

	public void setIdLigne(Long idLigne)
	{
		this.idLigne = idLigne;
	}

	public Long getIdLigne()
	{
		return idLigne;
	}

	public Long getIdTableauMarche()
	{
		return idTableauMarche;
	}

	public void setIdTableauMarche(Long idTableauMarche)
	{
		this.idTableauMarche = idTableauMarche;
	}

	public Date getSeuilDateDepartCourse()
	{
		return seuilDateDepartCourse;
	}

	public void setSeuilDateDepartCourse(Date seuilDateDepartCourse)
	{
		this.seuilDateDepartCourse = seuilDateDepartCourse;
	}

	/********************************************************
	 *                  MODEL + PREPARE                     *
	 ********************************************************/
	public VehicleJourneyAtStopModel getModel()
	{
		return model;
	}

	public void prepare() throws Exception
	{
		if (idItineraire != null)
		{
			log.debug("Filter for vehicle journey with itinerary : " + idItineraire + ", and timetable : " + getIdTableauMarche() + ", and begin hour : " + getSeuilDateDepartCourse());
			// RECUPERATION DES COURSES

			Filter filter = Filter.getNewAndFilter(
					Filter.getNewEqualsFilter("route.id", idItineraire));

			//List<VehicleJourney> vJourneys =vehicleJourneyManager.getAll(null, filter , level);
			courses =vehicleJourneyManager.getAll(null, filter , level);
			/**
			 * TODO
			 */
			//			for (VehicleJourney vehicleJourney : vJourneys) 
			//			{
			//				boolean isValideTimeTable = false;
			//				boolean isValideVJAtStop = false;
			//				for (Timetable timetable : vehicleJourney.getTimetables())
			//				{
			//					if(timetable != null && timetable.getId() != null)
			//					{
			//						if(timetable.getId().equals(getIdTableauMarche()))
			//						{
			//							isValideTimeTable = true;
			//							break;
			//						}	
			//					}
			//				}
			//
			//				for (VehicleJourneyAtStop atStop : vehicleJourney.getVehicleJourneyAtStops())
			//				{
			//					if(atStop != null && atStop.getId() != null && atStop.getDepartureTime() != null) 
			//					{
			//						if(atStop.getId().equals(getIdTableauMarche()) && atStop.getDepartureTime().after(getSeuilDateDepartCourse()))
			//						{
			//							isValideVJAtStop = true;
			//							break;
			//						}	
			//					}
			//				}
			//				if(isValideTimeTable || isValideVJAtStop)
			//					courses.add(vehicleJourney);
			//			}
			log.debug("Courses size: " + courses.size());

			// GESTION DE LA PAGINATION
			if (pagination.getNumeroPage() == null || pagination.getNumeroPage() < 1)
			{
				pagination.setNumeroPage(1);
			}
			log.debug("Page number : " + pagination.getNumeroPage());
			pagination.setNbTotalColonnes(courses.size());
			List<VehicleJourney> coursesPage = (List<VehicleJourney>) pagination.getCollectionPageCourante(courses);
			log.debug("coursesPage.size()                       : " + coursesPage.size());
			model.setCoursesPage(coursesPage);
			// GESTION DES ARRETS DE L'ITINERAIRE
			List<StopPoint> arretsItineraire = stopPointManager.getAll(null, Filter.getNewEqualsFilter("route.id", idItineraire), level);

			log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
			model.setArretsItineraire(arretsItineraire);
			Map<Long, StopArea> arretPhysiqueParIdArret = getArretPhysiqueParIdArret(arretsItineraire);

			model.setArretPhysiqueParIdArret(arretPhysiqueParIdArret);

			// PREPARATION DE LA LISTE DES TM POUR LE FILTRE
			Map<Long, String> commentParTMid = getCommentParTMId(idItineraire);
			Iterator timeTableIterator = commentParTMid.entrySet().iterator();
			int index = 1;
			while (timeTableIterator.hasNext())
			{
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

	private void prepareMapPositionArretParIdArret(List<StopPoint> arretsItineraire)
	{
		Map<Long, Integer> positionArretParIdArret = new Hashtable<Long, Integer>();
		for (StopPoint arret : arretsItineraire)
		{
			positionArretParIdArret.put(arret.getId(), arret.getPosition());
		}
		model.setPositionArretParIdArret(positionArretParIdArret);
	}

	private void prepareHoraires(List<StopPoint> arretsItineraire, List<VehicleJourney> coursesPage)
	{
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
			List<Date> heuresCourses = new ArrayList<Date>(idsCourses.size() * arretsItineraire.size());
			for (Long idCourse : idsCourses)
			{
				List<VehicleJourneyAtStop> horairesCourseOrdonnees = obtenirHorairesCourseOrdonnees(horairesCourseParIdCourse.get(idCourse), arretsItineraire);
				heuresCourses.addAll(obtenirDatesDepartFromHoraires(horairesCourseOrdonnees));
			}
			model.setHeuresCourses(heuresCourses);
		}
	}

	private void prepareMapMissionParIdCourse(List<VehicleJourney> coursesPage) throws ChouetteException
	{
		List<Long> idsMissionAffichee = new ArrayList<Long>();
		for (VehicleJourney course : coursesPage)
		{
			idsMissionAffichee.add(course.getJourneyPattern().getId());
		}
		List<JourneyPattern> missions = journeyPatternManager.getAll(null, Filter.getNewInFilter("id", idsMissionAffichee), level);
		Map<Long, JourneyPattern> missionParIdCourse = new Hashtable<Long, JourneyPattern>();
		for (JourneyPattern mission : missions)
		{
			missionParIdCourse.put(mission.getId(), mission);
		}
		model.setMissionParIdCourse(missionParIdCourse);
		log.debug("fin prepareMapMissionParIdCourse");
	}

	/*
	 * Get Timetables order for Vehicle Journey from filter
	 */
	private void prepareMapsTableauxMarche() throws ChouetteException
	{
		log.debug("appel prepareMapsTableauxMarche");
		// TimeTables ids for each vehcicle journeyid
		Map<Long, SortedSet<Integer>> tableauxMarcheParIdCourse = new HashMap<Long, SortedSet<Integer>>();
		// TimeTables ids from filter
		List<Long> timeTableId = new ArrayList(model.getTableauxMarche().keySet());

		List<VehicleJourney> vjs = vehicleJourneyManager.getAll(null, Filter.getNewEqualsFilter("route.id", idItineraire), level);
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
	public String search()
	{
		return SEARCH;
	}

	public String cancel()
	{
		setIdTableauMarche(null);
		setSeuilDateDepartCourse(null);
		actionMsg = getText("horairesDePassage.cancel.ok");
		return REDIRECTLIST;
	}

	@Override
	public String input() throws Exception
	{
		return LIST;
	}

	public String ajoutCourseAvecDecalageTemps() throws ChouetteException
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(model.getTempsDecalage());
		long tempsDecalageMillis = cal.get(Calendar.HOUR_OF_DAY) * 3600000 + cal.get(Calendar.MINUTE) * 60000;
		List<StopPoint> arretsItineraire = model.getArretsItineraire();
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
		List<VehicleJourneyAtStop> horairesADecaler = model.getHorairesParIdCourse().get(idCourseADecaler);
		//log.debug("horairesADecaler : " + horairesADecaler);
		List<VehicleJourneyAtStop> horairesADecalerResultat = new ArrayList<VehicleJourneyAtStop>();

		//création de la liste des ids des tableaux de marche de la course de référence
		List<Long> tableauxMarcheIds = new ArrayList<Long>();
		VehicleJourney vehicleJourney = vehicleJourneyManager.getById(idCourseADecaler);
		List<Timetable> timetables = vehicleJourney.getTimetables();

		//TODO gestion du décalage (EtatMajHoraire ???)
		int nbreCourseDecalageInt = nbreCourseDecalage.intValue();
		//	        int count = 0;
		//	        decalage:
		//	        for (int i = 0; i < nbreCourseDecalage; i++) {
		//	            // Création d'une course
		//	            Course course = new Course();
		//	            // Ajout du temps de décalage à toutes les dates
		//	            int compteurHoraire = 0;
		//	            Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
		//	            boolean isfirstHoraire = true;
		//	            for (Horaire horaire : horairesADecaler) {
		//	                if (horaire != null) {
		//	                    Date heureDepartOrigine = horaire.getDepartureTime();
		//	                    Date heureDepartResultat = new Date(heureDepartOrigine.getTime() + tempsDecalageMillis);
		//	                    if (isfirstHoraire && heureDepartResultat.before(heureDepartOrigine)) {
		//	                        actionErr = "course.decalage.partial";
		//	                        nbreCourseDecalageInt = count;
		//	                        break decalage;
		//	                    }
		//	                    isfirstHoraire =false;
		//	                    //	Mise à jour de la liste d'horaire résultat
		//	                    Horaire horaireResultat = new Horaire();
		//	                    horaireResultat.setIdArret(horaire.getIdArret());
		//	                    horaireResultat.setIdCourse(horaire.getIdCourse());
		//	                    horaireResultat.setDepartureTime(heureDepartResultat);
		//	                    horairesADecalerResultat.add(horaireResultat);
		//	                    Long idArretItineraire = getIdArretParIndice(compteurHoraire, arretsItineraire);
		//	                    majHoraires.add(EtatMajHoraire.getCreation(idArretItineraire, course.getId(), heureDepartResultat));
		//	                } else {
		//	                    horairesADecalerResultat.add(null);
		//	                }
		//	                compteurHoraire++;
		//	            }
		//	            count++;
		//	            course.setIdItineraire(getIdItineraire());
		//	            courseManager.creer(course);
		//	            // Copie des tableaux de marche de la course de référence dans la nouvelle
		//	            courseManager.associerCourseTableauxMarche(course.getId(), tableauxMarcheIds);
		//	            horaireManager.modifier(majHoraires);
		//	            horairesADecaler = horairesADecalerResultat;
		//	            horairesADecalerResultat = new ArrayList<Horaire>();
		//	        }

		String[] args = {""+idCourseADecaler.longValue(), ""+nbreCourseDecalageInt, ""+(tempsDecalageMillis / 1000)};
		actionMsg = getText("course.decalage.ok", args);

		return REDIRECTLIST;
	}

	public String editerHorairesCourses()
	{
		List<Date> heuresCourses = model.getHeuresCourses();
		log.debug("heuresCourses.size()                     : " + heuresCourses.size());
		List<StopPoint> arretsItineraire = model.getArretsItineraire();
		log.debug("arretsItineraire.size()                  : " + arretsItineraire.size());
		List<Integer> idsHorairesInvalides = filtreHorairesInvalides(heuresCourses, arretsItineraire.size());
		model.setIdsHorairesInvalides(idsHorairesInvalides);
		if (idsHorairesInvalides != null && !idsHorairesInvalides.isEmpty())
		{
			addActionError(getText("error.horairesInvalides"));
			return INPUT;
		}
		int indexPremiereDonneeDansCollectionPaginee = pagination.getIndexPremiereDonneePageCouranteDansCollectionPaginee(arretsItineraire.size());
		log.debug("indexPremiereDonneeDansCollectionPaginee : " + indexPremiereDonneeDansCollectionPaginee);
		log.debug("horairesCourses.size()                   : " + model.getHorairesCourses().size());

		/**
		 * TODO gestion EtatMajHoraire ???
		 */
		//		Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();
		//		for (int i = 0; i < model.getHorairesCourses().size(); i++)
		//		{
		//			Date heureCourse = model.getHeuresCourses().get(i);
		//			VehicleJourneyAtStop horaireCourse = model.getHorairesCourses().get(i);
		//			if (horaireCourse == null)
		//			{
		//				log.debug("horaireCourse.getIdCourse()              : NULL");
		//			}
		//			
		//			if (heureCourse != null && horaireCourse == null)
		//			{
		//				EtatMajHoraire etatMajHoraire = EtatMajHoraire.getCreation(
		//						getIdArretParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire),
		//						getIdCourseParIndice(indexPremiereDonneeDansCollectionPaginee, arretsItineraire),
		//						heureCourse);
		//				majHoraires.add(etatMajHoraire);
		//			} else if (heureCourse == null && horaireCourse != null)
		//			{
		//				majHoraires.add(EtatMajHoraire.getSuppression(horaireCourse));
		//			} else if (areBothDefinedAndDifferent(heureCourse, horaireCourse))
		//			{
		//				horaireCourse.setDepartureTime(heureCourse);
		//				majHoraires.add(EtatMajHoraire.getModification(horaireCourse));
		//			}
		//			indexPremiereDonneeDansCollectionPaginee++;
		//		}
		//		horaireManager.modifier(majHoraires);

		return REDIRECTLIST;
	}

	public String editerHorairesCoursesConfirmation()
	{
		List<StopPoint> arretsItineraire = model.getArretsItineraire();
		int indexPremiereDonneePagination = pagination.getIndexPremiereDonneePageCouranteDansCollectionPaginee(arretsItineraire.size());
		Collection<EtatMajHoraire> majHoraires = new ArrayList<EtatMajHoraire>();

		/**
		 * TODO gestion EtatMajHoraire ???
		 */
		//		for (int i = 0; i < model.getHorairesCourses().size(); i++)
		//		{
		//			Date heureDepart = model.getHeuresCourses().get(i);
		//			VehicleJourneyAtStop horaire = model.getHorairesCourses().get(i);
		//			

		//			if (heureDepart != null && horaire == null)
		//			{
		//				majHoraires.add(EtatMajHoraire.getCreation(
		//						getIdArretParIndice(indexPremiereDonneePagination, arretsItineraire),
		//						getIdCourseParIndice(indexPremiereDonneePagination, arretsItineraire),
		//						heureDepart));
		//			} else if (heureDepart == null && horaire != null)
		//			{
		//				majHoraires.add(EtatMajHoraire.getSuppression(horaire));
		//			} else if (areBothDefinedAndDifferent(heureDepart, horaire))
		//			{
		//				horaire.setDepartureTime(heureDepart);
		//				majHoraires.add(EtatMajHoraire.getModification(horaire));
		//			}
		//			indexPremiereDonneePagination++;
		//		}
		//		horaireManager.modifier(majHoraires);
		return REDIRECTLIST;
	}

	/********************************************************
	 *                           OTHERS                       *
	 ********************************************************/
	private List<Date> obtenirDatesDepartFromHoraires(List<VehicleJourneyAtStop> horaires)
	{
		List<Date> dates = new ArrayList<Date>(horaires.size());
		for (VehicleJourneyAtStop horaireCourse : horaires)
		{
			dates.add(horaireCourse == null ? null : new Date(horaireCourse.getDepartureTime().getTime()));
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
		List<VehicleJourneyAtStop> horaires = new ArrayList<VehicleJourneyAtStop>();

		for (VehicleJourney vehicleJourney : courses) 
		{
			horaires.addAll(vehicleJourney.getVehicleJourneyAtStops());
		}

		Map<Long, List<VehicleJourneyAtStop>> horairesCourseParIdCourse = new Hashtable<Long, List<VehicleJourneyAtStop>>();
		for (VehicleJourneyAtStop horaire : horaires)
		{
			VehicleJourney vj = horaire.getVehicleJourney();	
			if(horaire != null && vj != null)
			{
				Long idCourseCourante = vj.getId();
				List<VehicleJourneyAtStop> horairesCourse = horairesCourseParIdCourse.get(idCourseCourante);
				if (horairesCourse == null)
				{
					horairesCourse = new ArrayList<VehicleJourneyAtStop>();
					horairesCourseParIdCourse.put(idCourseCourante, horairesCourse);
				}
				horairesCourse.add(horaire);
			}						
		}
		return horairesCourseParIdCourse;
	}


	/********************************************************
	 *                        OTHERS                        *
	 ********************************************************/
	private boolean areBothDefinedAndDifferent(Date heureSaisie, Horaire horaireBase)
	{
		return heureSaisie != null && horaireBase != null && horaireBase.getDepartureTime() != null && (horaireBase.getDepartureTime().compareTo(heureSaisie) != 0);
	}

	private Long getIdArretParIndice(int indice, List<ArretItineraire> arretsItineraire)
	{
		int indiceArret = indice % arretsItineraire.size();
		return arretsItineraire.get(indiceArret).getId();
	}

	private Long getIdCourseParIndice(int indice, List<ArretItineraire> arretsItineraire)
	{
		int indiceCourse = indice / arretsItineraire.size();
		return courses.get(indiceCourse).getId();
	}

	public String getRouteName() throws ChouetteException
	{
		if (idItineraire != null)
		{
			return routeManager.getById(idItineraire).getName();
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

	public Pagination getPagination()
	{
		return pagination;
	}

	public void setPagination(Pagination pagination)
	{
		this.pagination = pagination;
	}

	public int getMaxNbCoursesParPage()
	{
		return maxNbCoursesParPage;
	}

	public void setMaxNbCoursesParPage(int maxNbCoursesParPage)
	{
		log.debug("Number Vehicle Journey maximum for 1 page : " + maxNbCoursesParPage);
		this.maxNbCoursesParPage = maxNbCoursesParPage;
	}

	public int getMaxNbCalendriersParCourse()
	{
		return maxNbCalendriersParCourse;
	}

	public void setMaxNbCalendriersParCourse(int maxNbCalendriersParCourse)
	{
		this.maxNbCalendriersParCourse = maxNbCalendriersParCourse;
	}

	private Map<Long, StopArea> getArretPhysiqueParIdArret( List<StopPoint> arrets) throws ChouetteException
	{
		Map<Long, StopArea> arretPhysiqueParIdArret = new Hashtable<Long, StopArea>();

		if ( arrets==null || arrets.isEmpty())
		{
			return arretPhysiqueParIdArret;
		}

		// CrÃ©ation de la liste des arrets physique Ã  partir de la liste des
		// identfiants des arrets physique
		List<StopArea> positionsGeographiques = new ArrayList<StopArea>(); 
		for (StopPoint arret : arrets)
		{
			if(arret != null)
				positionsGeographiques.add(arret.getContainedInStopArea());
		}

		//selectionSpecifique.getGeoPositions(idsArretsPhysiques, new Ordre("name",true));

		// CrÃ©ation d'une map liant id Ligne -> Objet Ligne
		Map<Long, StopArea> arretPhysiqueParId = new Hashtable<Long, StopArea>();
		for (StopArea geoPosition : positionsGeographiques)
		{
			arretPhysiqueParId.put(geoPosition.getId(), geoPosition);
		}
		// CrÃ©ation d'une hashtable liant id Itineraire -> Objet Ligne
		for (StopPoint arret : arrets)
		{
			if (arret.getId() != null)
			{
				StopArea arretPhysique = arretPhysiqueParId.get(arret.getId());
				if(arretPhysique != null)
					arretPhysiqueParIdArret.put(arret.getId(), arretPhysique);
			}
		}		
		return arretPhysiqueParIdArret;
	}

	private Map<Long,String> getCommentParTMId(final Long idItineraire) throws ChouetteException
	{
		Map<Long, String> result = new HashMap<Long, String>();
		List<VehicleJourney> vehicleJourneys = vehicleJourneyManager.getAll(null, Filter.getNewEqualsFilter("route.id",idItineraire ), level);
		for (VehicleJourney vehicleJourney : vehicleJourneys) 
		{
			for (Timetable timetable : vehicleJourney.getTimetables())
			{
				result.put(timetable.getId(), timetable.getComment());	
			}
		}

		return result;
	}

	private void associerCourseTableauxMarche(Long idCourse, List<Timetable> tMs) throws ChouetteException
	{
		List<Timetable> liensTMCourse = null;
		List<Timetable> liensAsupprimer = new ArrayList<Timetable>();
		List<Timetable> tMExistants = new ArrayList<Timetable>();
		VehicleJourney vehicleJourney = vehicleJourneyManager.getById(idCourse);
		liensTMCourse = vehicleJourney.getTimetables();
		for (Timetable lien : liensTMCourse)
		{
			Timetable ancienTM = lien;
			tMExistants.add(ancienTM);
			if (!tMs.contains(ancienTM))
			{
				liensAsupprimer.add(lien);
			}
		}
		if (liensAsupprimer.size() > 0)
		{
			liensTMCourse.removeAll(liensAsupprimer);
		}
		tMs.removeAll(tMExistants);

		vehicleJourney.setTimetables(tMs);
		vehicleJourneyManager.update(null, vehicleJourney);
	}

	private List<Integer> filtreHorairesInvalides( List<Date> horairesModifie, int totalArrets) {
		Date precedent = null;
		List<Integer> indexsHorairesInvalides = new ArrayList<Integer>();
		int total = horairesModifie.size();
		for (int i = 0; i < total; i++) {
			Date courant = horairesModifie.get(i);
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
}
