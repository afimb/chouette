package fr.certu.chouette.service.importateur.monoligne.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.echange.LectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ReducteurItineraire 
{
    private static final Logger logger = Logger.getLogger( ReducteurItineraire.class);
	private final static String SEP = "$";
	private IIdentificationManager identificationManager;
	
	// premiers elements de lecture
	// dictionnaire des courses, table id course -> course
	Map<String, Course> courseParIdCourse;
	// dictionnaire des itinéraires, table id itinéraire -> itinéraire
	Map<String, Itineraire> itineraireParIdItineraire;
	
	Map<String, List<String>> physiquesParIdItineraire;
	Map<String, Integer> pseudoPositionParIdPhysique;
	Map<String, List<String>> idsItinerairesParSignature;
	Map<String, List<String>> supportParSignature;
	Map<String, String> idItinerairePrincipalParSignature;
	Map<String, String> signatureParIdItineraire;
	
	private LectureEchange echange;
	
	public void reduire( LectureEchange echange)
	{
		this.echange = echange;
		
		initialiser();
		
		transformer();
	}
	
	/**
	 * Instancie et charge les variables membres Map
	 * 
	 * pré-condition: la variable membre echange est définie
	 * 
	 * post-condition: les variables membres Map sont chargées
	 * 
	 * invariant: variable membre echange
	 */
	private void initialiser()
	{
		courseParIdCourse = new Hashtable<String, Course>();
		
		List<Course> courses = echange.getCourses();
		for (Course course : courses) {
			courseParIdCourse.put( course.getObjectId(), course);
		}
		
		itineraireParIdItineraire = new Hashtable<String, Itineraire>();
		idsItinerairesParSignature = new Hashtable<String, List<String>>();
		signatureParIdItineraire = new Hashtable<String, String>();
		idItinerairePrincipalParSignature = new Hashtable<String, String>();
		
		List<Itineraire> itineraires = echange.getItineraires();
		for (Itineraire itineraire : itineraires) {
			itineraireParIdItineraire.put( itineraire.getObjectId(), itineraire);
			
			String signature = getSignature(itineraire);
			signatureParIdItineraire.put( itineraire.getObjectId(), signature);
			List<String> idsItineraires = idsItinerairesParSignature.get( signature);
			if ( idsItineraires==null)
			{
				idsItineraires = new ArrayList<String>();
				idsItinerairesParSignature.put( signature, idsItineraires);
				idItinerairePrincipalParSignature.put( signature, itineraire.getObjectId());
			}
			idsItineraires.add( itineraire.getObjectId());
		}
		
		pseudoPositionParIdPhysique = new Hashtable<String, Integer>();
		
		List<PositionGeographique> physiques = echange.getArretsPhysiques();
		int totalPhysiques = physiques.size();
		for (int i = 0; i < totalPhysiques; i++) 
		{
			pseudoPositionParIdPhysique.put( physiques.get( i).getObjectId(), new Integer( i));
		}
		

		physiquesParIdItineraire = new Hashtable<String, List<String>>();
		
		List<Horaire> horaires = echange.getHoraires();
		for (Horaire horaire : horaires) {
			Course course = courseParIdCourse.get( horaire.getVehicleJourneyId());
			//Itineraire itineraire = itineraireParIdItineraire.get( course.getRouteId());
			
			List<String> idsPhysiques = physiquesParIdItineraire.get( course.getRouteId());
			if ( idsPhysiques==null)
			{
				idsPhysiques = new ArrayList<String>();
				physiquesParIdItineraire.put( course.getRouteId(), idsPhysiques);
			}
			idsPhysiques.add( horaire.getStopPointId());
		}

		supportParSignature = new Hashtable<String, List<String>>();
		Set<String> signatures = idsItinerairesParSignature.keySet();
		for (String signature : signatures) 
		{
			List<String> idsItineraires = idsItinerairesParSignature.get( signature);
			Set<String> cumulIdPhysique = new HashSet<String>();
			
			for (String idItineraire : idsItineraires) {
				cumulIdPhysique.addAll( physiquesParIdItineraire.get( idItineraire));
			}
			supportParSignature.put( signature, getSupport(cumulIdPhysique));
		}
	}
	
	/**
	 * Réalise la réduction à l'intérieur
	 * de la variable membre echange
	 * 
	 * pré-conditions: 1 la variable membre echange est définie
	 *                 2 les variables membres Map sont chargées
	 * 
	 * post-condition: la variable membre echange est transformée
	 * 
	 * invariant: les variables membres Map
	 */
	private void transformer()
	{
		// Transformation de la relation: arrêt -> itinéraire 
		Map<String, String> itineraireParArret = new Hashtable<String, String>();
		echange.setItineraireParArret( itineraireParArret);
		
		Set<String> idItinerairesPrincipaux = new HashSet<String>( idItinerairePrincipalParSignature.values());
		
		// Reduction des itinéraires
		List<Itineraire> itineraires = echange.getItineraires();
		List<Itineraire> itineraireDeTrop = new ArrayList<Itineraire>(); 
		for (Itineraire itineraire : itineraires) 
		{
			if ( !idItinerairesPrincipaux.contains( itineraire.getObjectId()))
				itineraireDeTrop.add( itineraire);
		}
		itineraires.removeAll( itineraireDeTrop);
		
		// Màj de la relation course -> itinéraire
		List<Course> courses = echange.getCourses();
		for (Course course : courses) 
		{
			String signature = signatureParIdItineraire.get( course.getRouteId());
			String idItinerairePrincipal = idItinerairePrincipalParSignature.get( signature);
			course.setRouteId( idItinerairePrincipal);
		}
		
		// Màj de la relation horaire -> arrêt
		// Màj des arrêts
		List<ArretItineraire> arrets = new ArrayList<ArretItineraire>();
		List<Horaire> horaires = echange.getHoraires();
		for (Horaire horaire : horaires) {
			Course course = courseParIdCourse.get( horaire.getVehicleJourneyId());
			String signature = signatureParIdItineraire.get( course.getRouteId());
			String idItinerairePrincipal = idItinerairePrincipalParSignature.get( signature);
			Itineraire itineraire = itineraireParIdItineraire.get( idItinerairePrincipal);
			List<String> support = supportParSignature.get( signature);
			
			if ( support==null|| support.size()==0)
				throw new RuntimeException( "signature="+signature+
						", signatures="+supportParSignature.keySet());
			int position = support.indexOf( horaire.getStopPointId());
			if ( position==-1)
				throw new RuntimeException( "support="+support+", id="+horaire.getStopPointId());
			boolean sensAller = itineraire.getWayBack().equals("ALLER");
			
			horaire.setDepart( (position==0 && sensAller) 
							|| (position==(support.size()-1) && !sensAller));
			
			ArretItineraire arret = creerArret( itineraire, horaire.getStopPointId(), support.size(), position);
			horaire.setStopPointId( arret.getObjectId());
			
			if ( !itineraireParArret.containsKey( arret.getObjectId()))
			{
				arrets.add( arret);
				itineraireParArret.put( arret.getObjectId(), itineraire.getObjectId());
			}
		}
		echange.setArrets( arrets);
		
		// Màj de la relation mission -> itinéraire
		List<Mission> missions = echange.getMissions();
		for (Mission mission : missions) 
		{
			String signature = signatureParIdItineraire.get( mission.getRouteId());
			String idItinerairePrincipal = idItinerairePrincipalParSignature.get( signature);
			mission.setRouteId( idItinerairePrincipal);
		}
	}
	
	private ArretItineraire creerArret( Itineraire itineraire, String physiqueId, int total, int position)
	{
		ArretItineraire arret = new ArretItineraire();
		
		arret.setContainedIn( physiqueId);
		
		boolean sens = itineraire.getWayBack().equals("ALLER");
		int positionOrientee = sens?position:total-(position+1);
		arret.setPosition( positionOrientee);
		
		String nom = sens?"StopPoint":"RStopPoint";
		arret.setObjectId( identificationManager.getIdFonctionnel(nom, itineraire.getObjectId()+SEP+String.valueOf(arret.getPosition())));
		arret.setCreationTime( new Date());
		arret.setObjectVersion(1);
		
		return arret;
	}	
	private List<String> getSupport( Set<String> cumulIdPhysique)
	{
		List<String> support = new ArrayList<String>();
		
		List<PositionGeographique> physiques = echange.getArretsPhysiques();
		int totalPhysiques = physiques.size();
		for (int i = 0; i < totalPhysiques; i++) 
		{
			String idPhysique = physiques.get( i).getObjectId();
			if ( cumulIdPhysique.contains( idPhysique))
			{
				support.add( idPhysique);
			}
		}
		return support;
	}
	

	private String getSignature( Itineraire itineraire)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append( itineraire.getName());
		buffer.append( SEP);
		buffer.append( itineraire.getNumber());
		buffer.append( SEP);
		buffer.append( itineraire.getPublishedName());
		buffer.append( SEP);
		buffer.append( itineraire.getComment());
		buffer.append( SEP);
		
		return buffer.toString();
	}

	public void setIdentificationManager(
			IIdentificationManager identificationManager) {
		this.identificationManager = identificationManager;
	}
}
