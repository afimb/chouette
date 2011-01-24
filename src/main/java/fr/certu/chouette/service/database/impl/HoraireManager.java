package fr.certu.chouette.service.database.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.IModificationSpecifique;
import fr.certu.chouette.dao.ISelectionSpecifique;
import fr.certu.chouette.dao.ITemplateDao;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IHoraireManager;
import fr.certu.chouette.service.database.IMissionManager;
import fr.certu.chouette.service.database.impl.modele.EnumMaj;
import fr.certu.chouette.service.database.impl.modele.EtatMajHoraire;

public class HoraireManager implements IHoraireManager {
	
    private static final Logger logger = Logger.getLogger( HoraireManager.class);
	private ITemplateDao<Course> courseDao;
	private ITemplateDao<Horaire> horaireDao;
	private ISelectionSpecifique selectionSpecifique;
	private IModificationSpecifique modificationSpecifique;
	private IMissionManager missionManager;

	public void modifier( Horaire horaire) {
		horaireDao.update( horaire);
	}

	public void creer( Horaire horaire) {
		horaireDao.save( horaire);
	}

	public void creer( Long idArret, Long idCourse, Date depart) {
		Horaire horaire = new Horaire();
		horaire.setIdArret(idArret);
		horaire.setIdCourse(idCourse);
		horaire.setModifie(false);
		horaire.setDepartureTime(depart);
		horaireDao.save( horaire);
	}

        public List<Integer> filtreHorairesInvalides( List<Date> horairesModifie, int totalArrets) {
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
	
	
	public void modifier(Collection<EtatMajHoraire> etatsMajHoraire) {
		
		Map<String, Long> missionParSupport = null;
		Set<Long> courses = new HashSet<Long>();
		Set<Long> missions = new HashSet<Long>();
		Long idItineraire = null;
		
		if (etatsMajHoraire == null) return;
		
		if (!etatsMajHoraire.isEmpty()) {
			
			Horaire horaire = etatsMajHoraire.iterator().next().getHoraire();
			long courseId = horaire.getIdCourse();
			Course course = courseDao.get( courseId);
			if ( course==null)
			{
				throw new ServiceException( CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.VEHICLEJOURNEYATSTOP_VEHICLEJOURNEY,courseId,horaire.getId());
			}
			idItineraire = course.getIdItineraire();
			missions.add( course.getIdMission());
			missionParSupport = missionManager.getMissionParSupport( idItineraire);
			
			for (String support : missionParSupport.keySet()) {
				logger.debug( missionParSupport.get( support)+" "+support);
			}
		}
		
		for (EtatMajHoraire etatMajHoraire : etatsMajHoraire) {
			
			courses.add( etatMajHoraire.getHoraire().getIdCourse());
			
			if ( EnumMaj.CREER.equals( etatMajHoraire.getEnumMaj()))
			{
				creer( etatMajHoraire.getHoraire());
			}
			else if ( EnumMaj.DEPLACER.equals( etatMajHoraire.getEnumMaj()))
			{
				modifier( etatMajHoraire.getHoraire());
			}
			else if ( EnumMaj.SUPPRIMER.equals( etatMajHoraire.getEnumMaj()))
			{
				supprimer( etatMajHoraire.getHoraire().getId());
			}
		}
		
		if ( !etatsMajHoraire.isEmpty())
		{
			modificationSpecifique.referencerDepartsCourses( idItineraire);
			missionManager.majMissions(courses, missions, missionParSupport);
		}
		
	}


	public Horaire lire( Long idHoraire)
	{
		return horaireDao.get( idHoraire);
	}

	public List<Horaire> lire()
	{
		return horaireDao.getAll();
	}

	public void supprimer( Long idHoraire)
	{
		horaireDao.remove( idHoraire);
	}

	public void setHoraireDao(ITemplateDao<Horaire> horaireDao) {
		this.horaireDao = horaireDao;
	}

	public void setCourseDao(ITemplateDao<Course> courseDao) {
		this.courseDao = courseDao;
	}

	public void setSelectionSpecifique(ISelectionSpecifique selectionSpecifique) {
		this.selectionSpecifique = selectionSpecifique;
	}

	public void setModificationSpecifique(
			IModificationSpecifique modificationSpecifique) {
		this.modificationSpecifique = modificationSpecifique;
	}

	public void setMissionManager(IMissionManager missionManager) {
		this.missionManager = missionManager;
	}
}
