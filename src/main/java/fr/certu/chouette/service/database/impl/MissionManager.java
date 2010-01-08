package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.dao.hibernate.Couple;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class MissionManager implements IMissionManager 
{
    private static final Logger logger = Logger.getLogger( MissionManager.class);
	private ITemplateDao<Mission> missionDao;
	private ISelectionSpecifique selectionSpecifique;
	public IModificationSpecifique modificationSpecifique;
	private IIdentificationManager identificationManager;
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IMissionManager#lire(java.lang.Long)
	 */
	public Mission lire( Long idMission)
	{
		return missionDao.get( idMission);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IMissionManager#lire()
	 */
	public List<Mission> lire()
	{
		return missionDao.getAll();
	}
	
	public List<Mission> getMissions(Collection<Long> idMissions) {
		return selectionSpecifique.getMissions(idMissions);
	}

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IMissionManager#modifier(fr.certu.chouette.modele.Mission)
	 */
	public void modifier( Mission mission)
	{
		missionDao.update( mission);
	}
	
	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IMissionManager#creer(fr.certu.chouette.modele.Mission)
	 */
	public void creer( Mission mission)
	{
		missionDao.save( mission);
		String objectId = identificationManager.getIdFonctionnel("JourneyPattern", mission);
		mission.setObjectId(objectId);
		mission.setCreationTime( new Date());
		mission.setObjectVersion(1);
		missionDao.update( mission);
	}
	
	public void fusionnerMissions( Long idItineraire)
	{
		Map<Long, SortedSet<Long>> arretsParMission = getArretsParMission(idItineraire);

		Map<String, Long> missionParSupport = new Hashtable<String, Long>();
		for (Long idMission : arretsParMission.keySet()) {
			String support = arretsParMission.get( idMission).toString();
			Long idMissionPrincipale = missionParSupport.get( support);
			
			if ( idMissionPrincipale==null)
			{
				missionParSupport.put( support, idMission);
			}
			else
			{
				// fusionner la mission courante qui fait doublon
				modificationSpecifique.fusionnerMissions(idMission, idMissionPrincipale);
			}
		}		
	}

	public void majMissions( Set<Long> courses, Set<Long> missions, Map<String, Long> missionParSupport) 
	{
		Map<String, Set<Long>> coursesParNvSupport = new Hashtable<String, Set<Long>>();
		Map<String, Set<Long>> coursesParExSupport = new Hashtable<String, Set<Long>>();
		Map<Long, SortedSet<Long>> arretsParCourse = getArretsParCourse( courses);
		for (Long idCourse : arretsParCourse.keySet()) {
			String support = arretsParCourse.get( idCourse).toString();
			logger.debug( "support="+support);
			
			Map<String, Set<Long>> coursesParSupport = 
				( missionParSupport.containsKey( support))?
						coursesParExSupport:
						coursesParNvSupport;
			comptabiliser(coursesParSupport, idCourse, support);
		}
		
		for (Set<Long> coursesNvSupport : coursesParNvSupport.values()) 
		{
			// créer une mission pour ces courses
			Mission mission = new Mission();
			creer( mission);
			
			// affecter la nouvelle mission aux courses
			for (Long long1 : coursesNvSupport) {
				logger.debug( "course "+long1+" liée à une nouvelle mission "+mission.getId());
			}
			modificationSpecifique.affecterMission( mission.getId(), coursesNvSupport);
		}
		for (String exSupport : coursesParExSupport.keySet()) 
		{
			Set<Long> coursesExSupport = coursesParExSupport.get( exSupport);
			
			// affecter la mission existante aux courses
			for (Long long1 : coursesExSupport) {
				logger.debug( "course "+long1+" liée à une ex mission "+missionParSupport.get( exSupport));
			}

			modificationSpecifique.affecterMission( missionParSupport.get( exSupport), coursesExSupport);
		}
		modificationSpecifique.supprimerMissionSansCourse(missions);
	}

	public void comptabiliser(Map<String, Set<Long>> coursesParNvSupport,
			Long idCourse, String support) {
		Set<Long> coursesNv = coursesParNvSupport.get( support);
		if ( coursesNv==null)
		{
			coursesNv = new HashSet<Long>();
			coursesParNvSupport.put( support, coursesNv);
		}
		coursesNv.add( idCourse);
	}
	
	public Map<String, Long> getMissionParSupport( Long idItineraire)
	{
		Map<Long, SortedSet<Long>> arretsParMission = getArretsParMission(idItineraire);
		
		Map<String, Long> missionParSupport = new Hashtable<String, Long>();
		for (Long idMission : arretsParMission.keySet()) {
			Long idMissionIdentique = missionParSupport.put( arretsParMission.get( idMission).toString(), idMission);
			if ( idMissionIdentique!=null)
			{
				logger.error( "Les missions "+idMission+" et "+idMissionIdentique+" sont composées des mêmes arrêts");
			}
		}

		return missionParSupport;
	}

	public Map<Long, SortedSet<Long>> getArretsParMission(Long idItineraire) {
		Map<Long, Long> missionParCourse = new Hashtable<Long, Long>();
		List<Couple> couples = selectionSpecifique.getIdMissionIdCourseType(idItineraire);
		Collection<Long> idCourses = new ArrayList<Long>();
		for (Couple couple : couples) {
			idCourses.add( couple.deuxieme);
			missionParCourse.put( couple.deuxieme, couple.premier);
		}

		Map<Long, SortedSet<Long>> arretsParMission = new Hashtable<Long, SortedSet<Long>>();
		List<Horaire> horaires = selectionSpecifique.getHorairesCourses( idCourses);
		for (Horaire horaire : horaires) {
			Long idMission = missionParCourse.get( horaire.getIdCourse());
			SortedSet<Long> arretsMission = arretsParMission.get( idMission);
			
			if ( arretsMission==null)
			{
				arretsMission = new TreeSet<Long>();
				arretsParMission.put( idMission, arretsMission);
			}
			arretsMission.add( horaire.getIdArret());
		}
		return arretsParMission;
	}
	
	public Map<Long, SortedSet<Long>> getArretsParCourse( Collection<Long> idCourses)
	{
		Map<Long, SortedSet<Long>> arretsParCourse = new Hashtable<Long, SortedSet<Long>>();
		List<Horaire> horaires = selectionSpecifique.getHorairesCourses( idCourses);
		for (Horaire horaire : horaires) {
			SortedSet<Long> arretsMission = arretsParCourse.get( horaire.getIdCourse());
			
			if ( arretsMission==null)
			{
				arretsMission = new TreeSet<Long>();
				arretsParCourse.put( horaire.getIdCourse(), arretsMission);
			}
			arretsMission.add( horaire.getIdArret());
		}
		return arretsParCourse;
	}
	public void setMissionDao(ITemplateDao<Mission> missionDao) {
		this.missionDao = missionDao;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
