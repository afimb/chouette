package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ServiceStatusValueType;
import fr.certu.chouette.service.validation.TransportMode;
import fr.certu.chouette.service.validation.VehicleJourney;
import fr.certu.chouette.service.validation.VehicleJourneyAtStop;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class VehicleJourneyProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.VehicleJourneyProducer.class);
	private              ValidationException validationException;
	private              VehicleJourney      vehicleJourney      = null;
	
	VehicleJourneyProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setVehicleJourney(VehicleJourney vehicleJourney) {
		this.vehicleJourney = vehicleJourney;
	}
	
	VehicleJourney getVehicleJourney() {
		return vehicleJourney;
	}
	
	VehicleJourney getASG(chouette.schema.VehicleJourney castorVehicleJourney) {
		vehicleJourney = new VehicleJourney();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorVehicleJourney.getObjectId();
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"VehicleJourney\".", Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_VEHICLEJOURNEY, "Pas de \"objectId\" pour ce \"VehicleJourney\".");
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"VehicleJourney\".", Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_VEHICLEJOURNEY, "Pas de \"objectId\" pour ce \"VehicleJourney\".");
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_VEHICLEJOURNEY, "L'\"objectId\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorVehicleJourney.hasObjectVersion()) {
			int castorObjectVersion = (int)castorVehicleJourney.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = LoggingManager.getParams(""+castorObjectVersion, vehicleJourney.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"VehicleJourney\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_VEHICLEJOURNEY, "La version () \"objectVersion\" du \"VehicleJourney\" () est invalide.", params);
			}
			else
				vehicleJourney.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorVehicleJourney.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorVehicleJourney.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorVehicleJourney.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorVehicleJourney.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"VehicleJourney\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_VEHICLEJOURNEY, "La \"creationTime\" () de ce \"VehicleJourney\" () est invalide.", params);
			}
			else
				vehicleJourney.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorVehicleJourney.getCreatorId();
		params = new String[]{castorVehicleJourney.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setCreatorId(castorCreatorId);
		}
		
		// RouteId obligatoire
		String castorRouteId = castorVehicleJourney.getRouteId();
		if (castorRouteId == null) {
			LoggingManager.log(logger, "Pas de \"routeId\" pour ce \"VehicleJourney\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOROUTEID_VEHICLEJOURNEY, "Pas de \"routeId\" pour ce \"VehicleJourney\" ().", params);
		}
		else {
			castorRouteId = castorRouteId.trim();
			if (castorRouteId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"routeId\" pour ce \"VehicleJourney\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOROUTEID_VEHICLEJOURNEY, "Pas de \"routeId\" pour ce \"VehicleJourney\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorRouteId)) {
					params = LoggingManager.getParams(castorRouteId);
					LoggingManager.log(logger, "Le \"routeId\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTEID_VEHICLEJOURNEY, "Le \"routeId\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setRouteId(castorRouteId);
			}
		}
		
		// JourneyPatternId optionnel
		String castorJourneyPatternId = castorVehicleJourney.getJourneyPatternId();
		params = new String[]{castorVehicleJourney.getObjectId()};
		if (castorJourneyPatternId == null)
			LoggingManager.log(logger, "Pas de \"journeyPatternId\" pour ce \"VehicleJourney\".", params, Level.INFO);
		else {
			castorJourneyPatternId = castorJourneyPatternId.trim();
			if (castorJourneyPatternId.length() == 0)
				LoggingManager.log(logger, "Le \"journeyPatternId\" pour ce \"VehicleJourney\" est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorJourneyPatternId)) {
					params = LoggingManager.getParams(castorJourneyPatternId);
					LoggingManager.log(logger, "Le \"journeyPatternId\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERNID_VEHICLEJOURNEY, "Le \"journeyPatternId\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setJourneyPatternId(castorJourneyPatternId);
			}
		}
		
		// PublishedJourneyName optionnel
		String castorPublishedJourneyName = castorVehicleJourney.getPublishedJourneyName();
		params = new String[]{castorVehicleJourney.getObjectId()};
		if (castorPublishedJourneyName == null)
			LoggingManager.log(logger, "Pas de \"publishedJourneyName\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorPublishedJourneyName = castorPublishedJourneyName.trim();
			if (castorPublishedJourneyName.length() == 0)
				LoggingManager.log(logger, "Le \"publishedJourneyName\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setPublishedJourneyName(castorPublishedJourneyName);
		}
		
		// PublishedJourneyIdentifier optionnel
		String castorPublishedJourneyIdentifier = castorVehicleJourney.getPublishedJourneyIdentifier();
		if (castorPublishedJourneyIdentifier == null)
			LoggingManager.log(logger, "Pas de \"publishedJourneyIdentifier\" pour ce \"VehicleJourney\".", params, Level.INFO);
		else {
			castorPublishedJourneyIdentifier = castorPublishedJourneyIdentifier.trim();
			if (castorPublishedJourneyIdentifier.length() == 0)
				LoggingManager.log(logger, "Le \"publishedJourneyIdentifier\" pour ce \"VehicleJourney\" est vide.", params, Level.WARN);
			else
				vehicleJourney.setPublishedJourneyIdentifier(castorPublishedJourneyIdentifier);
		}
		
		// TransportMode optionnel
		if (castorVehicleJourney.getTransportMode() == null)
		{
			LoggingManager.log(logger, "Pas de \"transportMode\" pour ce \"VehicleJourney\".", params, Level.INFO);
		}
		else
		{
			switch (castorVehicleJourney.getTransportMode()) 
			{
				case AIR:
					vehicleJourney.setTransportMode(TransportMode.AIR);
					break;
				case BICYCLE:
					vehicleJourney.setTransportMode(TransportMode.BICYCLE);
					break;
				case BUS:
					vehicleJourney.setTransportMode(TransportMode.BUS);
					break;
				case COACH:
					vehicleJourney.setTransportMode(TransportMode.COACH);
					break;
				case FERRY:
					vehicleJourney.setTransportMode(TransportMode.FERRY);
					break;
				case LOCALTRAIN:
					vehicleJourney.setTransportMode(TransportMode.LOCALTRAIN);
					break;
				case LONGDISTANCETRAIN:
					vehicleJourney.setTransportMode(TransportMode.LONGDISTANCETRAIN);
					break;
				case METRO:
					vehicleJourney.setTransportMode(TransportMode.METRO);
					break;
				case OTHER:
					vehicleJourney.setTransportMode(TransportMode.OTHER);
					break;
				case PRIVATEVEHICLE:
					vehicleJourney.setTransportMode(TransportMode.PRIVATEVEHICLE);
					break;
				case RAPIDTRANSIT:
					vehicleJourney.setTransportMode(TransportMode.RAPIDTRANSIT);
					break;
				case SHUTTLE:
					vehicleJourney.setTransportMode(TransportMode.SHUTTLE);
					break;
				case TAXI:
					vehicleJourney.setTransportMode(TransportMode.TAXI);
					break;
				case TRAIN:
					vehicleJourney.setTransportMode(TransportMode.TRAIN);
					break;
				case TRAMWAY:
					vehicleJourney.setTransportMode(TransportMode.TRAMWAY);
					break;
				case TROLLEYBUS:
					vehicleJourney.setTransportMode(TransportMode.TROLLEYBUS);
					break;
				case VAL:
					vehicleJourney.setTransportMode(TransportMode.VAL);
					break;
				case WALK:
					vehicleJourney.setTransportMode(TransportMode.WALK);
					break;
				case WATERBORNE:
					vehicleJourney.setTransportMode(TransportMode.WATERBORNE);
					break;
				default:
					LoggingManager.log(logger, "Le \"transportMode\" de ce \"VehicleJourney\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDTRANSPORTMODENAMETYPE_VEHICLEJOURNEY, "Le \"transportMode\" de ce \"VehicleJourney\" () est invalide.", params);
			}
		}
		// VehicleTypeIdentifier optionnel
		String castorVehicleTypeIdentifier = castorVehicleJourney.getVehicleTypeIdentifier();
		if (castorVehicleTypeIdentifier == null)
			LoggingManager.log(logger, "Pas de \"vehicleTypeIdentifier\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorVehicleTypeIdentifier = castorVehicleTypeIdentifier.trim();
			if (castorVehicleTypeIdentifier.length() == 0)
				LoggingManager.log(logger, "Le \"VehicleTypeIdentifier\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setVehicleTypeIdentifier(castorVehicleTypeIdentifier);
		}
		
		// StatusValue optionnel
		if (castorVehicleJourney.getStatusValue() == null)
		{
			LoggingManager.log(logger, "Pas de \"statusValue\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		}
		else
		{
			switch (castorVehicleJourney.getStatusValue()) 
			{
				case CANCELLED:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.CANCELLED);
					break;
				case DELAYED:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.DELAYED);
					break;
				case DISRUPTED:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.DISRUPTED);
					break;
				case EARLY:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.EARLY);
					break;
				case INCREASEDSERVICE:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.INCREASEDSERVICE);
					break;
				case NORMAL:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.NORMAL);
					break;
				case NOTSTOPPING:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.NOTSTOPPING);
					break;
				case REDUCEDSERVICE:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.REDUCEDSERVICE);
					break;
				case REROUTED:
					vehicleJourney.setServiceStatusValueType(ServiceStatusValueType.REROUTED);
					break;
				default:
					LoggingManager.log(logger, "Le \"statusValue\" de ce \"VehicleJourney\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDSERVICESTATUSVALUETYPE_VEHICLEJOURNEY, "Le \"statusValue\" de ce \"VehicleJourney\" () est invalide.", params);
			}
		}
		// LineIdShortcut optionnel
		String castorLineIdShortcut = castorVehicleJourney.getLineIdShortcut();
		if (castorLineIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"lineIdShortcut\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorLineIdShortcut = castorLineIdShortcut.trim();
			if (castorLineIdShortcut.length() == 0)
				LoggingManager.log(logger, "Le \"LineIdShortcut\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorLineIdShortcut)) {
					params = LoggingManager.getParams(castorLineIdShortcut);
					LoggingManager.log(logger, "Le \"lineIdShortcut\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT_VEHICLEJOURNEY, "Le \"lineIdShortcut\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setLineIdShortcut(castorLineIdShortcut);
			}
		}
		
		// RouteIdShortcut optionnel
		String castorRouteIdShortcut = castorVehicleJourney.getRouteIdShortcut();
		params = new String[]{vehicleJourney.getObjectId()};
		if (castorRouteIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"routeIdShortcut\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorRouteIdShortcut = castorRouteIdShortcut.trim();
			if (castorRouteIdShortcut.length() == 0)
				LoggingManager.log(logger, "Le \"routeIdShortcut\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorRouteIdShortcut)) {
					params = LoggingManager.getParams(castorRouteIdShortcut);
					LoggingManager.log(logger, "Le \"routeIdShortcut\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTEIDSHORTCUT_VEHICLEJOURNEY, "Le \"routeIdShortcut\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setRouteIdShortcut(castorRouteIdShortcut);
			}
		}
		
		// OperatorId optionnel
		String castorOperatorId = castorVehicleJourney.getOperatorId();
		if (castorOperatorId== null)
			LoggingManager.log(logger, "Pas de \"operatorId\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorOperatorId = castorOperatorId.trim();
			if (castorOperatorId.length() == 0)
				LoggingManager.log(logger, "Le \"operatorId\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorOperatorId)) {
					params = LoggingManager.getParams(castorOperatorId);
					LoggingManager.log(logger, "Le \"operatorId\" () pour ce \"VehicleJourney\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOPERATORID_VEHICLEJOURNEY, "Le \"operatorId\" () pour ce \"VehicleJourney\" est invalide.", params);
				}
				vehicleJourney.setOperatorId(castorOperatorId);
			}
		}
		
		// Facility optionnel
		String castorFacility = castorVehicleJourney.getFacility();
		if (castorFacility== null)
			LoggingManager.log(logger, "Pas de \"facility\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorFacility = castorFacility.trim();
			if (castorFacility.length() == 0)
				LoggingManager.log(logger, "Le \"facility\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setFacility(castorFacility);
		}
		
		// Number optionnel
		if (castorVehicleJourney.hasNumber()) {
			int castorNumber = (int)castorVehicleJourney.getNumber();
			if (castorNumber < 0) {
				LoggingManager.log(logger, "Le numero \"number\" du \"VehicleJourney\" () ne peut < 0.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDNUMBER_VEHICLEJOURNEY, "Le numero \"number\" du \"VehicleJourney\" () ne peut < 0.", params);
			}
			else
				vehicleJourney.setNumber(castorNumber);
		}
		else
			LoggingManager.log(logger, "Pas de \"number\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		
		// VehicleJourneyAtStop [2..w]
		chouette.schema.VehicleJourneyAtStop[] castorVehicleJourneyAtStops = castorVehicleJourney.getVehicleJourneyAtStop();
		if (castorVehicleJourneyAtStops == null) {
			LoggingManager.log(logger, "La liste des \"vehicleJourneyAtStop\" dans un \"VehicleJourney\" () ne peut etre null.", params, Level.ERROR);
			validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEYATSTOPLIST_VEHICLEJOURNEY, "La liste des \"vehicleJourneyAtStop\" dans un \"VehicleJourney\" () ne peut etre null.", params);
		}
		else
			if (castorVehicleJourneyAtStops.length < 2) {
				LoggingManager.log(logger, "La liste des \"vehicleJourneyAtStop\" dans un \"VehicleJourney\" () ne peut avoir moins de deux elements.",params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEYATSTOPLIST_VEHICLEJOURNEY, "La liste des \"vehicleJourneyAtStop\" dans un \"VehicleJourney\" () ne peut avoir moins de deux elements.", params);
			}
			else
				for (int i = 0; i < castorVehicleJourney.getVehicleJourneyAtStopCount(); i++) {
					chouette.schema.VehicleJourneyAtStop castorVehicleJourneyAtStop = castorVehicleJourney.getVehicleJourneyAtStop(i);
					if (castorVehicleJourneyAtStop == null) {
						LoggingManager.log(logger, "Un \"vehicleJourneyAtStop\" dans une liste de \"vehicleJourneyAtStop\" d'un \"VehicleJourney\" () ne peut etre null.",params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEYATSTOPLIST_VEHICLEJOURNEY, "Un \"vehicleJourneyAtStop\" dans une liste de \"vehicleJourneyAtStop\" d'un \"VehicleJourney\" () ne peut etre null.", params);
					}
					else {
						VehicleJourneyAtStop vehicleJourneyAtStop = (new VehicleJourneyAtStopProducer(validationException)).getASG(castorVehicleJourneyAtStop);
						if (vehicleJourneyAtStop == null)
							LoggingManager.log(logger, "Erreur de conversion : Un \"vehicleJourneyAtStop\" dans une liste de \"vehicleJourneyAtStop\" d'un \"VehicleJourney\" () ne peut etre null.",params, Level.ERROR);
						else {
							vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
							vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
						}
					}
				}
		
		// Comment  optionnel
		String castorComment = castorVehicleJourney.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setComment(castorComment);
		}
		
		// TimeSlotId optionnel
		String castorTimeSlotId = castorVehicleJourney.getTimeSlotId();
		if (castorTimeSlotId == null)
			LoggingManager.log(logger, "Pas de \"timeSlotId\" pour ce \"VehicleJourney\" ().", params, Level.INFO);
		else {
			castorTimeSlotId = castorTimeSlotId.trim();
			if (castorTimeSlotId.length() == 0)
				LoggingManager.log(logger, "Le \"timeSlotId\" pour ce \"VehicleJourney\" () est vide.", params, Level.WARN);
			else
				vehicleJourney.setTimeSlotId(castorTimeSlotId);
		}
		
		return vehicleJourney;
	}
}
