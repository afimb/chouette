package fr.certu.chouette.service.validation.amivif.util;

import fr.certu.chouette.service.validation.amivif.TridentObject;
import fr.certu.chouette.service.validation.amivif.VehicleJourney;
import fr.certu.chouette.service.validation.amivif.VehicleJourney.VehicleJourneyAtStop;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class VehicleJourneyProducer extends TridentObjectProducer {
    
    public VehicleJourneyProducer(ValidationException validationException) {
		super(validationException);
	}

	public VehicleJourney getASG(amivif.schema.VehicleJourney castorVehicleJourney) {
		if (castorVehicleJourney == null)
			return null;
		TridentObject tridentObject = super.getASG(castorVehicleJourney);
		VehicleJourney vehicleJourney = new VehicleJourney();
		vehicleJourney.setTridentObject(tridentObject);
		
		// routeId obligatoire
		if (castorVehicleJourney.getRouteId() == null)
			getValidationException().add(TypeInvalidite.NoRoute_VehicleJourney, "Le \"routeId\" dans la \"VehicleJourney\" ("+castorVehicleJourney.getObjectId()+") est null.");
		else {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourney.getRouteId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourney.getRouteId()+" est invalid.");
			}
			vehicleJourney.setRouteId(castorVehicleJourney.getRouteId());
		}
		
		// journeyPatternId optionnel
		vehicleJourney.setJourneyPatternId(castorVehicleJourney.getJourneyPatternId());
		if (vehicleJourney.getJourneyPatternId() != null) {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourney.getJourneyPatternId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourney.getJourneyPatternId()+" est invalid.");
			}
		}
		
		// publishedJourneyName optionnel
		vehicleJourney.setPublishedJourneyName(castorVehicleJourney.getPublishedJourneyName());
		
		// publishedJourneyIdentifier
		vehicleJourney.setPublishedJourneyIdentifier(castorVehicleJourney.getPublishedJourneyIdentifier());
		
		// transportMode optionne
		if (castorVehicleJourney.getTransportMode() != null)
		{
			switch (castorVehicleJourney.getTransportMode()) 
			{
				case AIR:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Air);
					break;
				case BICYCLE:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Bicycle);
					break;
				case BUS:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Bus);
					break;
				case COACH:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Coach);
					break;
				case LOCALTRAIN:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.LocalTrain);
					break;
				case LONGDISTANCETRAIN:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.LongDistanceTrain);
					break;
				case METRO:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Metro);
					break;
				case OTHER:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Other);
					break;
				case PRIVATEVEHICLE:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.PrivateVehicle);
					break;
				case RAPIDTRANSIT:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.RapidTransit);
					break;
				case SHUTTLE:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Shuttle);
					break;
				case TAXI:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Taxi);
					break;
				case TRAIN:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Train);
					break;
				case TRAMWAY:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Tramway);
					break;
				case TROLLEYBUS:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Trolleybus);
					break;
				case VAL:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.VAL);
					break;
				case WALK:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Walk);
					break;
				case WATERBORNE:
					vehicleJourney.setTransportMode(VehicleJourney.TransportMode.Waterborne);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidTransportMode_VehicleJourney, "Le \"transportMode\" ("+castorVehicleJourney.getTransportMode().toString()+") du \"VehicleJourney\" ("+castorVehicleJourney.getObjectId()+") est invalid.");
			}
		}
		// vehicleTypeIdentifier optionnel
		vehicleJourney.setVehicleTypeIdentifier(castorVehicleJourney.getVehicleTypeIdentifier());
		
		// statusValue optionnel
		if (castorVehicleJourney.getStatusValue() != null)
		{
			switch (castorVehicleJourney.getStatusValue()) 
			{
				case CANCELLED:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Cancelled);
					break;
				case DELAYED:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Delayed);
					break;
				case DISRUPTED:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Disrupted);
					break;
				case EARLY:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Early);
					break;
				case INCREASEDSERVICE:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.IncreasedService);
					break;
				case NORMAL:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Normal);
					break;
				case NOTSTOPPING:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.NotStopping);
					break;
				case REDUCEDSERVICE:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.ReducedService);
					break;
				case REROUTED:
					vehicleJourney.setStatusValue(VehicleJourney.StatusValue.Rerouted);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidStatusValue_VehicleJourney, "Le \"statusValue\" ("+castorVehicleJourney.getStatusValue().toString()+") du \"VehicleJourney\" ("+castorVehicleJourney.getObjectId()+") est invalid.");
			}
		}
		
		// lineIdShortcut optionnel
		vehicleJourney.setLineIdShortcut(castorVehicleJourney.getLineIdShortcut());
		if (vehicleJourney.getLineIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourney.getLineIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourney.getLineIdShortcut()+" est invalid.");
			}
		}
		
		// routeIdShortcut optionnel
		vehicleJourney.setRouteIdShortcut(castorVehicleJourney.getRouteIdShortcut());
		if (vehicleJourney.getRouteIdShortcut() != null) {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourney.getRouteIdShortcut());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourney.getRouteIdShortcut()+" est invalid.");
			}
		}
		
		// operatorId optionnel
		vehicleJourney.setOperatorId(castorVehicleJourney.getOperatorId());
		if (vehicleJourney.getOperatorId() != null) {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourney.getOperatorId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourney.getOperatorId()+" est invalid.");
			}
		}
		
		// facility optionnel
		vehicleJourney.setFacility(castorVehicleJourney.getFacility());
		
		// number optionnel
		if (castorVehicleJourney.hasNumber())
			vehicleJourney.setNumber((int)castorVehicleJourney.getNumber());
		else
			vehicleJourney.setNumber(-1);
		
		// vehicleJourneyAtStop 2..w
		if ((castorVehicleJourney.getVehicleJourneyAtStop() == null) || (castorVehicleJourney.getVehicleJourneyAtStopCount() < 2))
			getValidationException().add(TypeInvalidite.InvalidNumberOfVehicleJourneyAtStop_VehicleJourneyAtStop, "Le nombre de \"vehicleJourneyAtStop\" dans le \"VehicleJourney\" ("+castorVehicleJourney.getObjectId()+") est invalid.");
		else
			for (int i = 0; i < castorVehicleJourney.getVehicleJourneyAtStopCount(); i++)
				vehicleJourney.addVehicleJourneyAtStop(getASG(castorVehicleJourney.getVehicleJourneyAtStop(i), vehicleJourney));
		
		// comment optionel
		vehicleJourney.setComment(castorVehicleJourney.getComment());
		
		return vehicleJourney;
	}

	private VehicleJourneyAtStop getASG(amivif.schema.VehicleJourneyAtStop castorVehicleJourneyAtStop, VehicleJourney vehicleJourney) {
		if (castorVehicleJourneyAtStop == null)
			return null;
		VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.new VehicleJourneyAtStop();
		
		// stopPointId obligatoire
		if (castorVehicleJourneyAtStop.getStopPointId() == null)
			getValidationException().add(TypeInvalidite.NoStopPoint_VehicleJourneyAtStop, "Le \"stopPointId\" du \"VehicleJourneyAtStop\" est indispensable.");
		else {
			try {
				(new TridentObject()).new TridentId(castorVehicleJourneyAtStop.getStopPointId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourneyAtStop.getStopPointId()+" est invalid.");
			}
			vehicleJourneyAtStop.setStopPointId(castorVehicleJourneyAtStop.getStopPointId());
		}
		
		// vehicleJourneyId optionnel
		vehicleJourneyAtStop.setVehicleJourneyId(castorVehicleJourneyAtStop.getVehicleJourneyId());
		if (vehicleJourneyAtStop.getVehicleJourneyId() != null) {
			try {
				(new TridentObject()).new TridentId(vehicleJourneyAtStop.getVehicleJourneyId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourneyAtStop.getVehicleJourneyId()+" est invalid.");
			}
		}
		
		// connectingServiceId optionnel
		vehicleJourneyAtStop.setConnectingServiceId(castorVehicleJourneyAtStop.getConnectingServiceId());
		if (vehicleJourneyAtStop.getConnectingServiceId() != null) {
			try {
				(new TridentObject()).new TridentId(vehicleJourneyAtStop.getConnectingServiceId());
			}
			catch(NullPointerException e) {
				getValidationException().add(TypeInvalidite.NullTridentObject, "Un \"objectId\" ne peut etre null.");
			}
			catch(IndexOutOfBoundsException e) {
				getValidationException().add(TypeInvalidite.InvalidTridentObject, "L'\"objectId\" "+castorVehicleJourneyAtStop.getConnectingServiceId()+" est invalid.");
			}
		}
		
		// arrivalTime optionnel
		if (castorVehicleJourneyAtStop.getArrivalTime() != null)
			vehicleJourneyAtStop.setArrivalTime(castorVehicleJourneyAtStop.getArrivalTime().toDate());
		
		// departureTime optionnel
		if (castorVehicleJourneyAtStop.getDepartureTime() != null) {
			vehicleJourneyAtStop.setDepartureTime(castorVehicleJourneyAtStop.getDepartureTime().toDate());
			if ((vehicleJourneyAtStop.getArrivalTime() != null) && (vehicleJourneyAtStop.getArrivalTime().after(vehicleJourneyAtStop.getDepartureTime())))
				getValidationException().add(TypeInvalidite.InvalidArrivalDepartureTime_VehicleJourneyAtStop, "Le \"arrivalTime\" ("+vehicleJourneyAtStop.getArrivalTime().toString()+") est anterieur au \"departureTime\" ("+vehicleJourneyAtStop.getDepartureTime().toString()+").");
		}
		
		// waitingTime optionnel
		if (castorVehicleJourneyAtStop.getWaitingTime() != null)
			vehicleJourneyAtStop.setWaitingTime(castorVehicleJourneyAtStop.getWaitingTime().toDate());
		
		// headwayFrequency optionnel
		vehicleJourneyAtStop.setHeadwayFrequency(castorVehicleJourneyAtStop.getHeadwayFrequency());
		
		// boardingAlightingPossiblity optionnel
		if (castorVehicleJourneyAtStop.getBoardingAlightingPossibility() != null)
			switch (castorVehicleJourneyAtStop.getBoardingAlightingPossibility()) 
			{
				case ALIGHTONLY:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.AlightOnly);
					break;
				case ALIGHTONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.AlightOnRequest);
					break;
				case BOARDANDALIGHT:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.BoardAndAlight);
					break;
				case BOARDANDALIGHTONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.BoardAndAlightOnRequest);
					break;
				case BOARDONLY:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.BoardOnly);
					break;
				case BOARDONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.BoardOnRequest);
					break;
				case NEITHERBOARDORALIGHT:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(VehicleJourney.BoardingAlightingPossibility.NeitherBoardOrAlight);
					break;
				default:
					getValidationException().add(TypeInvalidite.InvalidBoardingAlightingPossibility_VehicleJourneyAtStop, "Le \"BoardingAlightingPossibility\" ("+castorVehicleJourneyAtStop.getBoardingAlightingPossibility().toString()+") est unvalid.");
			}
			
		// order optionnel
		if (castorVehicleJourneyAtStop.hasOrder())
			vehicleJourneyAtStop.setOrder((int)castorVehicleJourneyAtStop.getOrder());
		else
			vehicleJourneyAtStop.setOrder(-1);
		
		return vehicleJourneyAtStop;
	}
}
