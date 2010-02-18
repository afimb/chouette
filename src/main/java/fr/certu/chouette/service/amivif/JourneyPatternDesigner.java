package fr.certu.chouette.service.amivif;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.ChouetteLineDescription;
import chouette.schema.ChouetteRoute;
import chouette.schema.JourneyPattern;
import chouette.schema.VehicleJourney;
import fr.certu.chouette.service.identification.ObjectIdLecteur;

public class JourneyPatternDesigner 
{
	private ChouetteLineDescription lineDescription;
	private Map<String, String> signatureParCourse = new Hashtable<String, String>();
	private Map<String, String> missionParSignature = new Hashtable<String, String>();
	private Map<String, String> itineraireParSignature = new Hashtable<String, String>();
	private Map<String, List<String>> arretsParSignature = new Hashtable<String, List<String>>();
	private String separteur = "M";

	public void transform()
	{
		int totalCourses = lineDescription.getVehicleJourneyCount();
		
		for (int i = 0; i < totalCourses; i++) 
		{
			add( lineDescription.getVehicleJourney( i));
		}
		
		Map<String, ChouetteRoute> itineraireParId = new Hashtable<String, ChouetteRoute>();
		int totalItineraires = lineDescription.getChouetteRouteCount();
		for (int i = 0; i < totalItineraires; i++) 
		{
			ChouetteRoute itineraire = lineDescription.getChouetteRoute(i);
			itineraire.setJourneyPatternId( new String[ 0]);
			itineraireParId.put( itineraire.getObjectId(), itineraire);
		}
		
		Set<String> signatures = missionParSignature.keySet();
		for (String signature : signatures) 
		{
			chouette.schema.JourneyPattern journey = new JourneyPattern();

			journey.setObjectId( missionParSignature.get( signature));
			journey.setRouteId( itineraireParSignature.get( signature));
			
			ChouetteRoute itineraire = itineraireParId.get( journey.getRouteId());
			itineraire.addJourneyPatternId( journey.getObjectId());
			
			List<String> arrets = arretsParSignature.get( signature);
			for (String arretId : arrets) 
			{
				journey.addStopPointList( arretId);
			}
			lineDescription.addJourneyPattern( journey);
		}
		
		signatureParCourse.clear();
		missionParSignature.clear();
		itineraireParSignature.clear();
		arretsParSignature.clear();
	}
	
	private void add( VehicleJourney vehicleJourney)
	{
		String signature = getSignature( vehicleJourney);
		
		signatureParCourse.put( vehicleJourney.getObjectId(), signature);
		
		if ( !missionParSignature.containsKey( signature))
		{
			String routeCodeId = ObjectIdLecteur.lirePartieCode( vehicleJourney.getRouteId());
			String system = ObjectIdLecteur.lirePartieSysteme( vehicleJourney.getRouteId());
			
			StringBuffer missionId = new StringBuffer( system);
			missionId.append( ":JourneyPattern:");
			missionId.append( routeCodeId);
			missionId.append( separteur);
			missionId.append( String.valueOf( signatureParCourse.keySet().size()));
			
			missionParSignature.put( signature, missionId.toString());
			itineraireParSignature.put( signature, vehicleJourney.getRouteId());
			
			List<String> arrets = new ArrayList<String>();
			int totalArrets = vehicleJourney.getVehicleJourneyAtStopCount();
			for (int i = 0; i < totalArrets; i++) 
			{
				arrets.add( vehicleJourney.getVehicleJourneyAtStop( i).getStopPointId());
			}
			arretsParSignature.put( signature, arrets);
		}
		vehicleJourney.setJourneyPatternId( missionParSignature.get( signature));
	}
	
	private String[] getSignatureIds( String signature)
	{
		String code = ObjectIdLecteur.lirePartieCode( signature);
		return signature.split( code);
	}
	
	private String getSignature( VehicleJourney vehicleJourney)
	{
		StringBuffer buf = new StringBuffer( vehicleJourney.getRouteId());
		
		int total = vehicleJourney.getVehicleJourneyAtStopCount();
		for (int i = 0; i < total; i++) 
		{
			buf.append( separteur);
			buf.append( ObjectIdLecteur.lirePartieCode( vehicleJourney.getVehicleJourneyAtStop( i).getStopPointId()));
		}
		return buf.toString();
	}

	public void setLineDescription(ChouetteLineDescription lineDescription) {
		this.lineDescription = lineDescription;
	}
}
