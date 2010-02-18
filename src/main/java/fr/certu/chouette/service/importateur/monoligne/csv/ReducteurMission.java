package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ReducteurMission 
{
    private static final Logger logger = Logger.getLogger( ReducteurMission.class);
	private final static String SEP = "$";
	private IIdentificationManager identificationManager;
	
	// premiers elements de lecture
	Map<String, Course> courseParIdCourse;
	Map<String, Mission> missionParIdMission;
	Map<String, List<String>> supportParSignature;
	Map<String, String> signatureParIdMission;
	Map<String, String> supportParIdMission;
	Map<String, String> missionPrincipaleParSupportSignature;
	
	private LectureEchange echange;
	
	public void reduire( LectureEchange echange)
	{
		this.echange = echange;
		
		initialiser();
		
		transformer();
	}
	
	private void initialiser()
	{
		courseParIdCourse = new Hashtable<String, Course>();
		
		List<Course> courses = echange.getCourses();
		for (Course course : courses) {
			courseParIdCourse.put( course.getObjectId(), course);
		}
		
		List<Horaire> horaires = echange.getHoraires();
		Map<String, List<String>> physiquesParIdMission = new Hashtable<String, List<String>>();
		for (Horaire horaire : horaires) 
		{
			Course course = courseParIdCourse.get( horaire.getVehicleJourneyId());
			List<String> idsPhysiques = physiquesParIdMission.get( course.getJourneyPatternId());
			if ( idsPhysiques==null)
			{
				idsPhysiques = new ArrayList<String>();
				physiquesParIdMission.put( course.getJourneyPatternId(), idsPhysiques);
			}
			idsPhysiques.add( horaire.getStopPointId());
		}
		
		missionPrincipaleParSupportSignature = new Hashtable<String, String>();
		missionParIdMission = new Hashtable<String, Mission>();
		signatureParIdMission = new Hashtable<String, String>();
		supportParIdMission = new Hashtable<String, String>();
		List<Mission> missions = echange.getMissions();
		for (Mission mission : missions) {
			String signature = getSignature(mission);
			
			missionParIdMission.put( mission.getObjectId(), mission);
			signatureParIdMission.put( mission.getObjectId(), signature);
			
			List<String> arretsMission = physiquesParIdMission.get( mission.getObjectId());
			String support = getSupport( arretsMission);
			supportParIdMission.put( mission.getObjectId(), support);
			
			missionPrincipaleParSupportSignature.put( getSupportsignature( support, signature), mission.getObjectId());
		}
	}
	
	private String getSupportsignature( String support, String signature)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( support);
		buffer.append( SEP);
		buffer.append( signature);
		buffer.append( SEP);
		
		return buffer.toString();
	}
	
	private void transformer()
	{
		List<Course> courses = echange.getCourses();
		for (Course course : courses) 
		{
			String signature = signatureParIdMission.get( course.getJourneyPatternId());
			String support = supportParIdMission.get( course.getJourneyPatternId());
			String idMissionPrincipale = missionPrincipaleParSupportSignature.get( getSupportsignature(support, signature));
			course.setJourneyPatternId( idMissionPrincipale);
		}
		
		List<Mission> principales = new ArrayList<Mission>();
		Collection<String> idPrincipaux = missionPrincipaleParSupportSignature.values();
		for (String idPrincipal : idPrincipaux) {
			principales.add( missionParIdMission.get( idPrincipal));
		}
		echange.setMissions( principales);
	}
	
	private String getSupport( Collection<String> idArrets)
	{
		StringBuffer buffer = new StringBuffer();
		for (String idArret : idArrets) {
			buffer.append( idArret);
			buffer.append( SEP);
		}
		
		return buffer.toString();
	}
	

	private String getSignature( Mission mission)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( mission.getRouteId());
		buffer.append( SEP);
		buffer.append( mission.getName());
		buffer.append( SEP);
		buffer.append( mission.getPublishedName());
		buffer.append( SEP);
		
		return buffer.toString();
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
