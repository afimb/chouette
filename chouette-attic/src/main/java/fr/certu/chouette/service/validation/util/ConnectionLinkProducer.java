package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ConnectionLink;
import fr.certu.chouette.service.validation.ConnectionLinkType;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.math.BigDecimal;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exolab.castor.types.Duration;

class ConnectionLinkProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.ConnectionLinkProducer.class);
	private              ValidationException validationException;
	private              ConnectionLink      connectionLink      = null;
	
	ConnectionLinkProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setConnectionLink(ConnectionLink connectionLink) {
		this.connectionLink = connectionLink;
	}
	
	ConnectionLink getConnectionLink() {
		return connectionLink;
	}
	
	ConnectionLink getASG(chouette.schema.ConnectionLink castorConnectionLink) {
		connectionLink = new ConnectionLink();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorConnectionLink.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorConnectionLink.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_CONNECTIONLINK, "Pas de \"objectId\" pour ce \"ConnectionLink\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				params = LoggingManager.getParams(castorConnectionLink.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_CONNECTIONLINK, "Pas de \"objectId\" pour ce \"ConnectionLink\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"ConnectionLink\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_CONNECTIONLINK, "L'\"objectId\" () pour ce \"ConnectionLink\" est invalide.", params);
				}
				connectionLink.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorConnectionLink.hasObjectVersion()) {
			int castorObjectVersion = (int)castorConnectionLink.getObjectVersion();
			if (castorObjectVersion < 1) {
				if (castorConnectionLink.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorConnectionLink.getName());
				else
					params = LoggingManager.getParams(""+castorObjectVersion, castorConnectionLink.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"ConnectionLink\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_CONNECTIONLINK, "La version () \"objectVersion\" du \"ConnectionLink\" () est invalide.", params);
			}
			else
				connectionLink.setObjectVersion(castorObjectVersion);
		}
		else {
			if (castorConnectionLink.getName() != null)
				params = LoggingManager.getParams(castorConnectionLink.getName());
			else
				params = LoggingManager.getParams(castorConnectionLink.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorConnectionLink.getCreationTime();
		if (castorCreationTime == null) {
			if (castorConnectionLink.getName() != null)
				params = LoggingManager.getParams(castorConnectionLink.getName());
			else
				params = LoggingManager.getParams(castorConnectionLink.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorConnectionLink.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorConnectionLink.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorConnectionLink.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"ConnectionLink\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_CONNECTIONLINK, "La \"creationTime\" () de ce \"ConnectionLink\" () est invalide.", params);
			}
			else
				connectionLink.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorConnectionLink.getCreatorId();
		params = null;
		if (castorConnectionLink.getName() != null)
			params = new String[]{castorConnectionLink.getName()};
		else if (castorConnectionLink.getObjectId() != null)
			params = new String[]{castorConnectionLink.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"ConnectionLink\" () est vide.", params, Level.WARN);
			else
				connectionLink.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorConnectionLink.getName();
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"name\" dans ce \"ConnectionLink\" () est vide.", params, Level.WARN);
			else
				connectionLink.setName(castorName);
		}
		
		// StartOfLink obligatoire
		String castorStartOfLink = castorConnectionLink.getStartOfLink();
		if (castorStartOfLink == null) {
			LoggingManager.log(logger, "Pas de \"startOfLink\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOSTARTOFLINK_CONNECTIONLINK, "Pas de \"startOfLink\" pour ce \"ConnectionLink\" ().", params);
		}
		else {
			castorStartOfLink = castorStartOfLink.trim();
			if (castorStartOfLink.length() == 0) {
				LoggingManager.log(logger, "Pas de \"startOfLink\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOSTARTOFLINK_CONNECTIONLINK, "Pas de \"startOfLink\" pour ce \"ConnectionLink\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorStartOfLink)) {
					params = LoggingManager.getParams(castorStartOfLink);
					LoggingManager.log(logger, "Le \"startOfLink\" () pour ce \"ConnectionLink\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDSTARTOFLINK_CONNECTIONLINK, "Le \"startOfLink\" () pour ce \"ConnectionLink\" est invalide.", params);
				}
				connectionLink.setStartOfLinkId(castorStartOfLink);		
			}
		}
		
		// EndOfLink obligatoire
		String castorEndOfLink = castorConnectionLink.getEndOfLink();
		params = null;
		if (castorConnectionLink.getName() != null)
			params = new String[]{castorConnectionLink.getName()};
		else if (castorConnectionLink.getObjectId() != null)
			params = new String[]{castorConnectionLink.getObjectId()};			
		if (castorEndOfLink == null) {
			LoggingManager.log(logger, "Pas de \"endOfLink\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOENDOFLINK_CONNECTIONLINK, "Pas de \"endOfLink\" pour ce \"ConnectionLink\" ().", params);
		}
		else {
			castorEndOfLink = castorEndOfLink.trim();
			if (castorEndOfLink.length() == 0) {
				LoggingManager.log(logger, "Pas de \"endOfLink\" pour ce \"ConnectionLink\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOENDOFLINK_CONNECTIONLINK, "Pas de \"endOfLink\" pour ce \"ConnectionLink\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorEndOfLink)) {
					params = LoggingManager.getParams(castorEndOfLink);
					LoggingManager.log(logger, "Le \"endOfLink\" () pour ce \"ConnectionLink\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDENDOFLINK_CONNECTIONLINK, "Le \"endOfLink\" () pour ce \"ConnectionLink\" est invalide.", params);
				}
				connectionLink.setEndOfLinkId(castorEndOfLink);
			}
		}
		
		// LinkDistane optionnel
		params = null;
		if (castorConnectionLink.getName() != null)
			params = new String[]{castorConnectionLink.getName()};
		else if (castorConnectionLink.getObjectId() != null)
			params = new String[]{castorConnectionLink.getObjectId()};			
		BigDecimal castorLinkDistance = castorConnectionLink.getLinkDistance();
		if (castorLinkDistance == null)
			LoggingManager.log(logger, "Pas de \"linkDistance\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			connectionLink.setLinkDistance(castorLinkDistance);

		// LinkType Optionnel
		
		if (castorConnectionLink.getLinkType() == null)
			LoggingManager.log(logger, "Pas de \"linkType\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			switch (castorConnectionLink.getLinkType()) 
			{
				case MIXED:
					connectionLink.setLinkType(ConnectionLinkType.MIXED);
					break;
				case UNDERGROUND:
					connectionLink.setLinkType(ConnectionLinkType.UNDERGROUND);
					break;
				case OVERGROUND:
					connectionLink.setLinkType(ConnectionLinkType.OVERGROUND);
					break;
				default:
					validationException.add(TypeInvalidite.INVALIDLINKTYPE_CONNECTIONLINK, "Le \"linkType\" de ce \"ConnecionLink\" () est invalide.", params);
			}
		
		// DefaultDuration optionnel
		Duration castorDefaultDuration = castorConnectionLink.getDefaultDuration();
		if (castorDefaultDuration == null)
			LoggingManager.log(logger, "Pas de \"defaultDuration\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			connectionLink.setDefaultDuration(castorDefaultDuration);
		
		// FrequentTravellerDuration optionnel
		Duration castorFrequentTravellerDuration = castorConnectionLink.getFrequentTravellerDuration();
		if (castorFrequentTravellerDuration == null)
			LoggingManager.log(logger, "Pas de \"frequentTravellerDuration\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			connectionLink.setFrequentTravellerDuration(castorFrequentTravellerDuration);
		
		// OccasionalTravellerDuration optionnel
		Duration castorOccasionalTravellerDuration = castorConnectionLink.getOccasionalTravellerDuration();
		if (castorOccasionalTravellerDuration == null)
			LoggingManager.log(logger, "Pas de \"occasionalTravellerDuration\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			connectionLink.setOccasionalTravellerDuration(castorOccasionalTravellerDuration);
		
		// MobilityRestrictedTravellerDuration optionnel
		Duration castorMobilityRestrictedTravellerDuration = castorConnectionLink.getMobilityRestrictedTravellerDuration();
		if (castorMobilityRestrictedTravellerDuration == null)
			LoggingManager.log(logger, "Pas de \"mobilityRestrictedTravellerDuration\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else
			connectionLink.setMobilityRestrictedTravellerDuration(castorMobilityRestrictedTravellerDuration);
		
		// MobilityRestrictedSuitability optionnel
		boolean castorMobilityRestrictedSuitability = castorConnectionLink.getMobilityRestrictedSuitability();
		connectionLink.setMobilityRestrictedSuitability(castorMobilityRestrictedSuitability);

		// StairsAvailability optionnel
		boolean castorStairsAvailability = castorConnectionLink.getStairsAvailability();
		connectionLink.setStairsAvailability(castorStairsAvailability);

		// LiftAvailability optionnel
		boolean castorLiftAvailability = castorConnectionLink.getLiftAvailability();
		connectionLink.setLiftAvailability(castorLiftAvailability);
		
		// Comment optionnel
		String castorComment = castorConnectionLink.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"ConnectionLink\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Lobjet de type \"Comment\" dans ce \"ConnectionLink\" () est vide.", params, Level.WARN);
			else
				connectionLink.setComment(castorComment);
		}
		
		return connectionLink;
	}
}
