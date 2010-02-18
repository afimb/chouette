package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.BoardingAlightingPossibility;
import fr.certu.chouette.service.validation.VehicleJourneyAtStop;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Duration;
import org.exolab.castor.types.Time;

class VehicleJourneyAtStopProducer {
	
	private static final Logger               logger               = Logger.getLogger(fr.certu.chouette.service.validation.util.VehicleJourneyAtStopProducer.class);
	private              ValidationException  validationException;
	private              VehicleJourneyAtStop vehicleJourneyAtStop = null;
	
	VehicleJourneyAtStopProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setVehicleJourneyAtStop(VehicleJourneyAtStop vehicleJourneyAtStop) {
		this.vehicleJourneyAtStop = vehicleJourneyAtStop;
	}
	
	VehicleJourneyAtStop getVehicleJourneyAtStop() {
		return vehicleJourneyAtStop;
	}
	
	VehicleJourneyAtStop getASG(chouette.schema.VehicleJourneyAtStop castorVehicleJourneyAtStop) {
		vehicleJourneyAtStop = new VehicleJourneyAtStop();
		String[] params = null;
		
		// StopPointId obligatoire
		String castorStopPointId = castorVehicleJourneyAtStop.getStopPointId();
		if (castorStopPointId == null) {
			LoggingManager.log(logger, "Le \"StopPointId\" du \"VehicleJourneyAtStop\" ne peut pas etre null.", Level.ERROR);
			validationException.add(TypeInvalidite.NULLSTOPPOINTID_VEHICLEJOURNEYATSTOP, "Le \"StopPointId\" du \"VehicleJourneyAtStop\" ne peut pas etre null.");
		}
		else {
			if (!MainSchemaProducer.isTridentLike(castorStopPointId)) {
				params = LoggingManager.getParams(castorStopPointId);
				LoggingManager.log(logger, "Le \"stopPointId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDSTOPPOINTID_VEHICLEJOURNEYATSTOP, "Le \"stopPointId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params);
			}
			vehicleJourneyAtStop.setStopPointId(castorStopPointId);
		}
		
		// VehicleJourneyId optionnel
		String castorVehicleJourneyId = castorVehicleJourneyAtStop.getVehicleJourneyId();
		params = new String[]{vehicleJourneyAtStop.getStopPointId()};
		if (castorVehicleJourneyId == null)
			LoggingManager.log(logger, "Le \"vehicleJourneyId\" du \"VehicleJourneyAtStop\" () ne est null.", params, Level.INFO);
		else {
			if (!MainSchemaProducer.isTridentLike(castorVehicleJourneyId)) {
				params = LoggingManager.getParams(castorVehicleJourneyId);
				LoggingManager.log(logger, "Le \"vehicleJourneyId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEYID_VEHICLEJOURNEYATSTOP, "Le \"vehicleJourneyId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params);
			}
			vehicleJourneyAtStop.setVehicleJourneyId(castorVehicleJourneyId);
		}
		
		// ConnectingServiceId optionnel
		String castorConnectingServiceId = castorVehicleJourneyAtStop.getConnectingServiceId();
		params = new String[]{vehicleJourneyAtStop.getStopPointId()};
		if (castorConnectingServiceId == null)
			LoggingManager.log(logger, "Le \"connectingServiceId\" du \"VehicleJourneyAtStop\" () ne est null.", params, Level.INFO);
		else {
			if (!MainSchemaProducer.isTridentLike(castorConnectingServiceId)) {
				params = LoggingManager.getParams(castorConnectingServiceId);
				LoggingManager.log(logger, "Le \"connectingServiceId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCONNECTINGSERVICEID_VEHICLEJOURNEYATSTOP, "Le \"connectingServiceId\" () pour ce \"VehicleJourneyAtStop\" est invalide.", params);
			}
			vehicleJourneyAtStop.setConnectingServiceId(castorConnectingServiceId);
		}
		
		// ([arrivalTime AND] departureTime [AND waitingTime]) XOR elapseDuration
		params = new String[]{vehicleJourneyAtStop.getStopPointId()};
		if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice() == null) {
			LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params, Level.ERROR);
			validationException.add(TypeInvalidite.NULLDEPARTURETIMEANDNULLELAPSEDURATIONE_VEHICLEJOURNEYATSTOP, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params);
		}
		else {
			boolean wasNotFound = true;
			if ((castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence() == null) &&
					(castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2() == null)) {
				LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.NULLDEPARTURETIMEANDNULLELAPSEDURATIONE_VEHICLEJOURNEYATSTOP, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params);
			}
			else {
				if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence() == null)
					LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"departureTime\".", params, Level.INFO);
				else {
					// ArrivalTime optionnel
					if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime() == null )
						LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"arrivalTime\".", params, Level.INFO);
					else {
						Time castorArrivalTime = castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getArrivalTime();
						if (castorArrivalTime == null)
							LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () a un \"arrivalTime\" null.", params, Level.WARN);
						else {
							vehicleJourneyAtStop.setArrivalTime(castorArrivalTime);
							wasNotFound = false; // EN ATTENDANT ...
						}
					}
					
					// departureTime obligatoire
					if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime() == null )
						LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"departureTime\".", params, Level.INFO);
					else {
						Time castorDepartureTime = castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getDepartureTime();
						if (castorDepartureTime == null)
							LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () a un \"departureTime\" null.", params, Level.WARN);
						else {
							vehicleJourneyAtStop.setDepartureTime(castorDepartureTime);
							wasNotFound = false;
						}
					}
					
					// WaitingTime optionnel
					if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getWaitingTime() == null )
						LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"waitingTime\".", params, Level.INFO);
					else {
						Time castorWaitingTime = castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence().getWaitingTime();
						if (castorWaitingTime == null)
							LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () a un \"waitingTime\" null.", params, Level.WARN);
						else {
							vehicleJourneyAtStop.setWaitingTime(castorWaitingTime);
							//wasNotFound = false;
						}
					}
				}
				if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2() == null)
					LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"elapseTime\".", params, Level.INFO);
				else {
					// ElapseDuration obligatoire
					if (castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration() == null )
						LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a pas de \"elapseTime\".", params, Level.INFO);
					else {
						Duration castorElapseDuration = castorVehicleJourneyAtStop.getVehicleJourneyAtStopTypeChoice().getVehicleJourneyAtStopTypeChoiceSequence2().getElapseDuration();
						if (castorElapseDuration == null)
							LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () a un \"elapseTime\" null.", params, Level.WARN);
						else {
							vehicleJourneyAtStop.setElapseDuration(castorElapseDuration);
							wasNotFound = false;
						}
					}
				}
			}
			if (wasNotFound) {
				LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.NULLDEPARTURETIMEANDNULLELAPSEDURATIONE_VEHICLEJOURNEYATSTOP, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params);
			}
			else if ((vehicleJourneyAtStop.getElapseDuration() != null) && ((vehicleJourneyAtStop.getWaitingTime() != null) || (vehicleJourneyAtStop.getDepartureTime() != null) || (vehicleJourneyAtStop.getArrivalTime() != null))) {
				LoggingManager.log(logger, "Le \"VehicleJourneyAtStop\" () a une \"elapseDuration\" et au moins une \"arrivalTime\", une \"departureTime\" ou une \"waitingTime\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDARRIVALDEPARTUREWAINTINGTIMEANDELAPSEDURATIONE_VEHICLEJOURNEYATSTOP, "Le \"VehicleJourneyAtStop\" () n'a ni \"departureTime\" ni \"elapseDuration\".", params);
			}
		}
		
		// HeadwayFrequency optionnel
		Duration castorHeadwayFrequency = castorVehicleJourneyAtStop.getHeadwayFrequency();
		params = new String[]{vehicleJourneyAtStop.getStopPointId()};
		if (castorHeadwayFrequency == null)
			LoggingManager.log(logger, "Ce \"VehicleJourneyAtStop\" () n'a pas de \"headwayFrequency\".", params, Level.INFO);
		else
			vehicleJourneyAtStop.setHeadwayFrequency(castorHeadwayFrequency);
		
		// BoardingAlightingPossibility optionnel
		if (castorVehicleJourneyAtStop.getBoardingAlightingPossibility() == null)
			LoggingManager.log(logger, "Ce \"VehicleJourneyAtStop\" () n'a pas de \"boardingAlightingPossibility\".", params, Level.INFO);
		else
			switch (castorVehicleJourneyAtStop.getBoardingAlightingPossibility()) 
			{
				case ALIGHTONLY:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.ALIGHTONLY);
					break;
				case ALIGHTONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.ALIGHTONREQUEST);
					break;
				case BOARDANDALIGHT:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.BOARDANDALIGHT);
					break;
				case BOARDANDALIGHTONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.BOARDANDALIGHTONREQUEST);
					break;
				case BOARDONLY:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.BOARDONLY);
					break;
				case BOARDONREQUEST:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.BOARDONREQUEST);
					break;
				case NEITHERBOARDORALIGHT:
					vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibility.NEITHERBOARDORALIGHT);
					break;
				default:
					LoggingManager.log(logger, "Le \"boardingAlightingPossibility\" de ce \"VehicleJourneyAtStop\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDBOARDINGALIGHTING_VEHICLEJOURNEYATSTOP, "Le \"boardingAlightingPossibility\" de ce \"VehicleJourneyAtStop\" () est invalide.", params);
			}
		
		// Order optionnel
		if (castorVehicleJourneyAtStop.hasOrder()) {
			int castorOrder = (int)castorVehicleJourneyAtStop.getOrder();
			vehicleJourneyAtStop.setOrder(castorOrder);
		}
		else
			LoggingManager.log(logger, "Ce \"VehicleJourneyAtStop\" () n'a pas de \"order\".", params, Level.INFO);
		
		return vehicleJourneyAtStop;
	}
}
