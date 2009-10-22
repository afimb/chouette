package fr.certu.chouette.service.validation.amivif.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import fr.certu.chouette.service.validation.amivif.TransportNetwork;
import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class TransportNetworkProducer extends TridentObjectProducer {
    
    private RegistrationProducer	registrationProducer	= new RegistrationProducer(getValidationException());
    
    public TransportNetworkProducer(ValidationException validationException) {
    	super(validationException);
	}
	
	public TransportNetwork getASG(amivif.schema.TransportNetwork castorTransportNetwork) {
		if (castorTransportNetwork == null)
			return null;
		
		// TridentObject obligatoire
		TridentObject tridentObject = super.getASG(castorTransportNetwork);
		TransportNetwork transportNetwork = new TransportNetwork();
		transportNetwork.setTridentObject(tridentObject);
		
		// versionDate obligatoire
		if (castorTransportNetwork.getVersionDate() == null)
			getValidationException().add(TypeInvalidite.NullVersionDate_TransportNetwork, "La \"versionDate\" du \"TransportNetwork\" est null.");
		else {
			transportNetwork.setVersionDate(castorTransportNetwork.getVersionDate().toDate());
			if (transportNetwork.getVersionDate().after(new Date(System.currentTimeMillis())))
				getValidationException().add(TypeInvalidite.InvalidVersionDate_TransportNetwork, "La \"versionDate\" du \"TransportNetwork\" est posterieure a la date actuelle.");
		}
		
		// description	 optionnel
		transportNetwork.setDescription(castorTransportNetwork.getDescription());
		
		// name obligatoire
		if (castorTransportNetwork.getName() == null)
			getValidationException().add(TypeInvalidite.NullName_TransportNetwork, "La \"Name\" du \"TransportNetwork\" est null.");
		else
			transportNetwork.setName(castorTransportNetwork.getName());
		
		// registration optionnel
		transportNetwork.setRegistration(registrationProducer.getASG(castorTransportNetwork.getRegistration()));
		if (transportNetwork.getRegistration() != null)
			if (transportNetwork.getRegistration().getTransportNetworkIdsCount() >= 1) {
				boolean notFound = true;
				for (int i = 0; i < transportNetwork.getRegistration().getTransportNetworkIdsCount(); i++)
					if (transportNetwork.getObjectId().toString().equals(transportNetwork.getRegistration().getTransportNetworkId(i))) {
						if (notFound)
							notFound = false;
						transportNetwork.getRegistration().removeTransportNetworkId(i);
						transportNetwork.getRegistration().addTransportNetwork(transportNetwork);
					}
				if (notFound)
					getValidationException().add(TypeInvalidite.InvalidRegistartion_TransportNetwork, "La liste des \"transportNetworkId\" de la \"registration\" du \"TransportNetwork\" ne contient pas son identifiant \"objectId\" ("+transportNetwork.getObjectId().toString()+").");
			}
		
		// sourceName optionnel
		transportNetwork.setSourceName(castorTransportNetwork.getSourceName());
		
		// sourceIdentifier optionnel
		transportNetwork.setSourceIdentifier(castorTransportNetwork.getSourceIdentifier());
		
		// sourceType optionnel
		if (castorTransportNetwork.getSourceType() != null)
			switch (castorTransportNetwork.getSourceType().getType()) {
			case amivif.schema.types.SourceTypeType.AUTOMOBILECLUBPATROL_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.AutomobileClubPatrol);
				break;
			case amivif.schema.types.SourceTypeType.BREAKDOWNSERVICE_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.BreakdownService);
				break;
			case amivif.schema.types.SourceTypeType.CAMERAOBSERVATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.CameraObservation);
				break;
			case amivif.schema.types.SourceTypeType.EMERGENCYSERVICEPATROL_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.EmergencyServicePatrol);
				break;
			case amivif.schema.types.SourceTypeType.FREIGHTVEHICLEOPERATOR_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.FreightVehicleOperator);
				break;
			case amivif.schema.types.SourceTypeType.INDIVIDUALSUBJECTOFTRAVELITINERARY_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.IndividualSubjectOfTravelItinerary);
				break;
			case amivif.schema.types.SourceTypeType.INDUCTIONLOOPMONITORINGSTATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.InductionLoopMonitoringStation);
				break;
			case amivif.schema.types.SourceTypeType.INFRAREDMONITORINGSTATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.InfraredMonitoringStation);
				break;
			case amivif.schema.types.SourceTypeType.MICROWAVEMONITORINGSTATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.MicrowaveMonitoringStation);
				break;
			case amivif.schema.types.SourceTypeType.MOBILETELEPHONECALLER_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.MobileTelephoneCaller);
				break;
			case amivif.schema.types.SourceTypeType.OTHERINFORMATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.OtherInformation);
				break;
			case amivif.schema.types.SourceTypeType.OTHEROFFICIALVEHICLE_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.OtherOfficialVehicle);
				break;
			case amivif.schema.types.SourceTypeType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.PassengerTransportCoordinatingAuthority);
				break;
			case amivif.schema.types.SourceTypeType.POLICEPATROL_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.PolicePatrol);
				break;
			case amivif.schema.types.SourceTypeType.PUBLICANDPRIVATEUTILITIES_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.PublicAndPrivateUtilities);
				break;
			case amivif.schema.types.SourceTypeType.PUBLICTRANSPORT_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.PublicTransport);
				break;
			case amivif.schema.types.SourceTypeType.REGISTEREDMOTORISTOBSERVER_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.RegisteredMotoristObserver);
				break;
			case amivif.schema.types.SourceTypeType.ROADAUTHORITIES_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.RoadAuthorities);
				break;
			case amivif.schema.types.SourceTypeType.ROADSIDETELEPHONECALLER_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.RoadsideTelephoneCaller);
				break;
			case amivif.schema.types.SourceTypeType.SPOTTERAIRCRAFT_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.SpotterAircraft);
				break;
			case amivif.schema.types.SourceTypeType.TRAFFICMONITORINGSTATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.TrafficMonitoringStation);
				break;
			case amivif.schema.types.SourceTypeType.TRANSITOPERATOR_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.TransitOperator);
				break;
			case amivif.schema.types.SourceTypeType.TRAVELAGENCY_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.TravelAgency);
				break;
			case amivif.schema.types.SourceTypeType.TRAVELINFORMATIONSERVICEPROVIDER_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.TravelInformationServiceProvider);
				break;
			case amivif.schema.types.SourceTypeType.VEHICLEPROBEMEASUREMENT_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.VehicleProbeMeasurement);
				break;
			case amivif.schema.types.SourceTypeType.VIDEOPROCESSINGMONITORINGSTATION_TYPE:
				transportNetwork.setSourceType(TransportNetwork.SourceType.VideoProcessingMonitoringStation);
				break;
			default:
				getValidationException().add(TypeInvalidite.InvalidSourceType_TransportNetwork, "La \"SourceType\" du \"TransportNetwork\" est inconnue.");
			}
		
		// lineEnd 0..w
		Set<String> aSet = new HashSet<String>();
		for (int i = 0; i < castorTransportNetwork.getLineIdCount(); i++) {
			try {
				(new TridentObject()).new TridentId(castorTransportNetwork.getLineId(i));
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorTransportNetwork.getLineId(i)+" est invalid.");
			}
			if (aSet.add(castorTransportNetwork.getLineId(i)))
				transportNetwork.addLineId(castorTransportNetwork.getLineId(i));
			else
				getValidationException().add(TypeInvalidite.MultipleTridentObject, "La liste \"lineId\" du \"TransportNetwork\" contient plusieur fois un identifiant ("+castorTransportNetwork.getLineId(i)+").");
		}
		
		// comment optionnel
		transportNetwork.setComment(castorTransportNetwork.getComment());

		return transportNetwork;
	}
}
