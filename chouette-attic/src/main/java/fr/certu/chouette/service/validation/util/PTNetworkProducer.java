package fr.certu.chouette.service.validation.util;

import java.util.HashSet;
import java.util.Set;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.PTNetworkProducer;
import fr.certu.chouette.service.validation.util.RegistrationProducer;
import fr.certu.chouette.service.validation.PTNetwork;
import fr.certu.chouette.service.validation.PTNetworkSourceType;
import fr.certu.chouette.service.validation.Registration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Date;

class PTNetworkProducer {
	
    private static final Logger              logger               = Logger.getLogger(fr.certu.chouette.service.validation.util.PTNetworkProducer.class);
    private              ValidationException validationException;
    private              PTNetwork           pTNetwork            = null; 
    
    PTNetworkProducer(ValidationException validationException) {
    	setValidationException(validationException);
    }
    
    void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setPTNetwork(PTNetwork pTNetwork) {
		this.pTNetwork = pTNetwork;
	}
	
	PTNetwork getPTNetwork() {
		return pTNetwork;
	}
	
	PTNetwork getASG(chouette.schema.PTNetwork castorPTNetwork) {
		pTNetwork = new PTNetwork();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorPTNetwork.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorPTNetwork.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_PTNETWORK, "Pas de \"objectId\" pour ce \"PTNetwork\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				params = LoggingManager.getParams(castorPTNetwork.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_PTNETWORK, "Pas de \"objectId\" pour ce \"PTNetwork\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"PTNetwork\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_PTNETWORK, "L'\"objectId\" () pour ce \"PTNetwork\" est invalide.", params);
				}
				pTNetwork.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorPTNetwork.hasObjectVersion()) {
			int castorObjectVersion = (int)castorPTNetwork.getObjectVersion();
			if (castorObjectVersion < 1) {
				if (castorPTNetwork.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorPTNetwork.getName());
				else
					params = LoggingManager.getParams(""+castorObjectVersion, castorPTNetwork.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"PTNetwork\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_PTNETWORK, "La version () \"objectVersion\" du \"PTNetwork\" () est invalide.", params);
			}
			else
				pTNetwork.setObjectVersion(castorObjectVersion);
		}
		else {
			if (castorPTNetwork.getName() != null)
				params = LoggingManager.getParams(castorPTNetwork.getName());
			else
				params = LoggingManager.getParams(castorPTNetwork.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorPTNetwork.getCreationTime();
		if (castorCreationTime == null) {
			if (castorPTNetwork.getName() != null)
				params = LoggingManager.getParams(castorPTNetwork.getName());
			else
				params = LoggingManager.getParams(castorPTNetwork.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorPTNetwork.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorPTNetwork.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorPTNetwork.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"PTNetwork\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_PTNETWORK, "La \"creationTime\" () de ce \"PTNetwork\" () est invalide.", params);
			}
			else
				pTNetwork.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorPTNetwork.getCreatorId();
		params = null;
		if (castorPTNetwork.getName() != null)
			params = new String[]{castorPTNetwork.getName()};
		else if (castorPTNetwork.getObjectId() != null)
			params = new String[]{castorPTNetwork.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"PTNetwork\" () est vide.", params, Level.WARN);
			else
				pTNetwork.setCreatorId(castorCreatorId);
		}
		
		// VersionDate obligatoire
		Date castorVersionDate = castorPTNetwork.getVersionDate();
		if (castorVersionDate == null) {
			LoggingManager.log(logger, "Pas de \"versionDate\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NOVERSIONDATE_PTNETWORK, "Pas de \"versionDate\" pour ce \"PTNetwork\" ().", params);
		}
		else
			if (castorVersionDate.toDate().after(new java.util.Date(System.currentTimeMillis()))) {
				params = new String[]{castorVersionDate.toString()};
				if (castorPTNetwork.getName() != null)
					params = new String[]{castorVersionDate.toString(), castorPTNetwork.getName()};
				else if (castorPTNetwork.getObjectId() != null)
					params = new String[]{castorVersionDate.toString(), castorPTNetwork.getObjectId()};
				LoggingManager.log(logger, "La \"versionDate\" () de ce \"PTNetwork\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDVERSIONDATE_PTNETWORK, "La \"versionDate\" () de ce \"PTNetwork\" () est invalide.", params);
			}
			else
				pTNetwork.setVersionDate(castorVersionDate);
		
		// Description optionnel
		String castorDescription = castorPTNetwork.getDescription();
		params = null;
		if (castorPTNetwork.getName() != null)
			params = new String[]{castorPTNetwork.getName()};
		else if (castorPTNetwork.getObjectId() != null)
			params = new String[]{castorPTNetwork.getObjectId()};			
		if (castorDescription == null)
			LoggingManager.log(logger, "Pas de \"description\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			castorDescription = castorDescription.trim();
			if (castorDescription.length() == 0)
				LoggingManager.log(logger, "Pas de \"description\" vide pour ce \"PTNetwork\" ().", params, Level.WARN);
			else			
				pTNetwork.setDescription(castorDescription);
		}
		
		// Name obligatoire
		String castorName = castorPTNetwork.getName();
		params = null;
		if (castorPTNetwork.getObjectId() != null)
			params = new String[]{castorPTNetwork.getObjectId()};			
		if (castorName == null) {
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_PTNETWORK, "Pas de \"name\" pour ce \"PTNetwork\" ().", params);
		}
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0) {
				LoggingManager.log(logger, "Pas de \"name\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NONAME_PTNETWORK, "Pas de \"name\" pour ce \"PTNetwork\" ().", params);
			}
			else
				pTNetwork.setName(castorName);
		}
		
		// Registartion optionnel
		chouette.schema.Registration castorRegistration = castorPTNetwork.getRegistration();
		params = null;
		if (castorPTNetwork.getName() != null)
			params = new String[]{castorPTNetwork.getName()};
		else if (castorPTNetwork.getObjectId() != null)
			params = new String[]{castorPTNetwork.getObjectId()};
		if (castorRegistration == null)
			LoggingManager.log(logger, "Pas de \"registration\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			Registration registration = (new RegistrationProducer(validationException)).getASG(castorRegistration);
			if (registration == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"registration\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
			else {
				pTNetwork.setRegistration(registration);
				registration.setPTNetwork(pTNetwork);
			}
		}
		
		// SourceName optionnel
		String castorSourceName = castorPTNetwork.getSourceName();
		if (castorSourceName == null)
			LoggingManager.log(logger, "Pas de \"sourceName\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			castorSourceName = castorSourceName.trim();
			if (castorSourceName.length() == 0)
				LoggingManager.log(logger, "Pas de \"sourceName\" vide pour ce \"PTNetwork\" ().", params, Level.WARN);
			else
				pTNetwork.setSourceName(castorSourceName);
		}
		
		// SourceIdentifier optionnel
		String castorSourceIdentifier = castorPTNetwork.getSourceIdentifier();
		if (castorSourceIdentifier == null)
			LoggingManager.log(logger, "Pas de \"sourceIdentifier\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			castorSourceIdentifier = castorSourceIdentifier.trim();
			if (castorSourceIdentifier.length() == 0)
				LoggingManager.log(logger, "Pas de \"sourceIdentifier\" vide pour ce \"PTNetwork\" ().", params, Level.WARN);
			else
				pTNetwork.setSourceIdentifier(castorSourceIdentifier);
		}
		
		// SourceType optionnel
		if (castorPTNetwork.getSourceType() == null)
			LoggingManager.log(logger, "Pas de \"sourceType\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else
			switch (castorPTNetwork.getSourceType()) {
			case AUTOMOBILECLUBPATROL:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.AUTOMOBILECLUBPATROL);
				break;
			case BREAKDOWNSERVICE:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.BREAKDOWNSERVICE);
				break;
			case CAMERAOBSERVATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.CAMERAOBSERVATION);
				break;
			case EMERGENCYSERVICEPATROL:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.EMERGENCYSERVICEPATROL);
				break;
			case FREIGHTVEHICLEOPERATOR:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.FREIGHTVEHICLEOPERATOR);
				break;
			case INDIVIDUALSUBJECTOFTRAVELITINERARY:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.INDIVIDUALSUBJECTOFTRAVELITINERARY);
				break;
			case INDUCTIONLOOPMONITORINGSTATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.INDUCTIONLOOPMONITORINGSTATION);
				break;
			case INFRAREDMONITORINGSTATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.INFRAREDMONITORINGSTATION);
				break;
			case MICROWAVEMONITORINGSTATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.MICROWAVEMONITORINGSTATION);
				break;
			case MOBILETELEPHONECALLER:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.MOBILETELEPHONECALLER);
				break;
			case OTHERINFORMATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.OTHERINFORMATION);
				break;
			case OTHEROFFICIALVEHICLE:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.OTHEROFFICIALVEHICLE);
				break;
			case PASSENGERTRANSPORTCOORDINATINGAUTHORITY:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.PASSENGERTRANSPORTCOORDINATINGAUTHORITY);
				break;
			case POLICEPATROL:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.POLICEPATROL);
				break;
			case PUBLICANDPRIVATEUTILITIES:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.PUBLICANDPRIVATEUTILITIES);
				break;
			case PUBLICTRANSPORT:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.PUBLICTRANSPORT);
				break;
			case REGISTEREDMOTORISTOBSERVER:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.REGISTEREDMOTORISTOBSERVER);
				break;
			case ROADAUTHORITIES:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.ROADAUTHORITIES);
				break;
			case ROADSIDETELEPHONECALLER:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.ROADSIDETELEPHONECALLER);
				break;
			case SPOTTERAIRCRAFT:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.SPOTTERAIRCRAFT);
				break;
			case TRAFFICMONITORINGSTATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.TRAFFICMONITORINGSTATION);
				break;
			case TRANSITOPERATOR:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.TRANSITOPERATOR);
				break;
			case TRAVELAGENCY:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.TRAVELAGENCY);
				break;
			case TRAVELINFORMATIONSERVICEPROVIDER:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.TRAVELINFORMATIONSERVICEPROVIDER);
				break;
			case VEHICLEPROBEMEASUREMENT:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.VEHICLEPROBEMEASUREMENT);
				break;
			case VIDEOPROCESSINGMONITORINGSTATION:
				pTNetwork.setPTNetworkSourceType(PTNetworkSourceType.VIDEOPROCESSINGMONITORINGSTATION);
				break;
			default:
				LoggingManager.log(logger, "Le \"sourceType\" pour ce \"PTNetwork\" () est invalide.", params, Level.ERROR);
			    validationException.add(TypeInvalidite.INVALIDSOURCETYPE_PTNETWORK, "Le \"sourceType\" pour ce \"PTNetwork\" () est invalide.", params);
			}
		
		// LineId[0..w]
		String[] castorLineIds = castorPTNetwork.getLineId();
		if (castorLineIds == null)
			LoggingManager.log(logger, "Pas de \"lineId\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else
			if (castorLineIds.length == 0)
				LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"PTNetwork\" () est vide.", params, Level.WARN);
			else {
				Set<String> lineIds = new HashSet<String>();
				for (int i = 0; i < castorLineIds.length; i++)
					if ((castorLineIds[i] != null) && (castorLineIds[i].trim().length() > 0))
						if (!lineIds.add(castorLineIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"PTNetwork\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (lineIds.size() == 0)
					LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"PTNetwork\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String lineId : lineIds)
						if (!MainSchemaProducer.isTridentLike(lineId)) {
							params = new String[]{lineId, ""};
							if (castorPTNetwork.getName() != null)
								params = new String[]{lineId, castorPTNetwork.getName()};
							else if (castorPTNetwork.getObjectId() != null)
								params = new String[]{lineId, castorPTNetwork.getObjectId()};
							LoggingManager.log(logger, "Le \"lineId\" () pour ce \"PTNetwork\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDLINEID_PTNETWORK, "Le \"lineId\" () pour ce \"PTNetwork\" () est invalide.", params);
						}
					pTNetwork.setLineIds((String[])lineIds.toArray(new String[0]));
				}
			}
		
		// Comment optionnel
		String castorComment = castorPTNetwork.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"PTNetwork\" () est vide.", params, Level.WARN);
			else
				pTNetwork.setComment(castorComment);
		}
		
		return pTNetwork;
	}
}
