package fr.certu.chouette.service.amivif.base;

import chouette.schema.VehicleJourneyAtStopTypeChoice;
import chouette.schema.VehicleJourneyAtStopTypeChoiceSequence;
import fr.certu.chouette.service.amivif.IAccesseurAreaStop;


public class VehicleAtStopConverter 
{
	private chouette.schema.VehicleJourneyAtStop atc(amivif.schema.VehicleJourneyAtStop amivif) {
		
		if (amivif == null ) return null ;
		
		chouette.schema.VehicleJourneyAtStop chouette = new chouette.schema.VehicleJourneyAtStop ();
		
		chouette.setOrder( amivif.getOrder());
		chouette.setVehicleJourneyId( amivif.getVehicleJourneyId());
		chouette.setStopPointId( amivif.getStopPointId());
		
		VehicleJourneyAtStopTypeChoice typeChoice = new VehicleJourneyAtStopTypeChoice();
		VehicleJourneyAtStopTypeChoiceSequence typeChoiceSequence = new VehicleJourneyAtStopTypeChoiceSequence();
		
		if ( amivif.getArrivalTime()!=null)
			typeChoiceSequence.setArrivalTime( amivif.getArrivalTime());
			
		if ( amivif.getDepartureTime()==null)
			typeChoiceSequence.setDepartureTime( amivif.getArrivalTime());
		else
			typeChoiceSequence.setDepartureTime( amivif.getDepartureTime());
		
		typeChoice.setVehicleJourneyAtStopTypeChoiceSequence( typeChoiceSequence);
		chouette.setVehicleJourneyAtStopTypeChoice( typeChoice);

		return chouette;
	}

	public chouette.schema.VehicleJourneyAtStop [] atc(amivif.schema.VehicleJourneyAtStop [] amivifs){
		
		if(amivifs == null ) return new chouette.schema.VehicleJourneyAtStop [0];
		
		int total = amivifs.length;
		chouette.schema.VehicleJourneyAtStop [] chouettes = new chouette.schema.VehicleJourneyAtStop [total];
		
		 for (int i = 0; i < total; i++) {
			 chouettes[i] = atc(amivifs[i]);
	     }
		 return  chouettes ;
	}
	private amivif.schema.VehicleJourneyAtStop cta(chouette.schema.VehicleJourneyAtStop chouette,
			IAccesseurAreaStop accesseur) 
	{
		if (chouette== null ) return null ;
		
		amivif.schema.VehicleJourneyAtStop amivif = new amivif.schema.VehicleJourneyAtStop ();
		amivif.setOrder( chouette.getOrder());
		amivif.setVehicleJourneyId( chouette.getVehicleJourneyId());
		amivif.setStopPointId( accesseur.getStopAreaOfStop( chouette.getStopPointId()).getObjectId());
		
		amivif.setArrivalTime( chouette.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime());
		amivif.setDepartureTime( chouette.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime());
		return amivif;
	}

	public amivif.schema.VehicleJourneyAtStop[] cta(chouette.schema.VehicleJourneyAtStop[] chouettes,
			IAccesseurAreaStop accesseur){
		
		if(chouettes == null ) return new amivif.schema.VehicleJourneyAtStop[0];
		
		int total = chouettes.length;
		amivif.schema.VehicleJourneyAtStop[] amivifs = new amivif.schema.VehicleJourneyAtStop[total];
		 for (int i = 0; i < total; i++) {
			 amivifs[i] = cta(chouettes[i], accesseur);
	     }
		 return  amivifs;
	}
}
