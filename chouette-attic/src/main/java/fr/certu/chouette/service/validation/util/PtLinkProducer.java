package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.PtLink;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.math.BigDecimal;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class PtLinkProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.JourneyPatternProducer.class);
	private              ValidationException validationException;
	private              PtLink              ptLink              = null;
	
	PtLinkProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setPtLink(PtLink ptLink) {
		this.ptLink = ptLink;
	}
	
	PtLink getPtLink() {
		return ptLink;
	}
	
	PtLink getASG(chouette.schema.PtLink castorPtLink) {
		PtLink ptLink = new PtLink();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorPtLink.getObjectId();
		params = new String[]{castorPtLink.getName()};
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"PtLink\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_PTLINK, "Pas de \"objectId\" pour ce \"PtLink\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"PtLink\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_PTLINK, "Pas de \"objectId\" pour ce \"PtLink\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"PtLink\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_PTLINK, "L'\"objectId\" () pour ce \"PtLink\" est invalide.", params);
				}
				ptLink.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorPtLink.hasObjectVersion()) {
			int castorObjectVersion = (int)castorPtLink.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = null;
				if (ptLink.getObjectId() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, ptLink.getObjectId());
				else if (castorPtLink.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorPtLink.getName());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"PtLink\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_PTLINK, "La version () \"objectVersion\" du \"PtLink\" () est invalide.", params);
			}
			else
				ptLink.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorPtLink.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"PtLink\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorPtLink.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorPtLink.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"PtLink\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorPtLink.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"PtLink\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_PTLINK, "La \"creationTime\" () de ce \"PtLink\" () est invalide.", params);
			}
			else
				ptLink.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorPtLink.getCreatorId();
		params = new String[]{castorPtLink.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"PtLink\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"PtLink\" () est vide.", params, Level.WARN);
			else
				ptLink.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorPtLink.getName();
		params = LoggingManager.getParams(castorPtLink.getObjectId());
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"PtLink\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "Le \"name\" de ce \"PtLink\" () est vide.", params, Level.WARN);
			else
				ptLink.setName(castorName);
		}
		
		// StartOfLink obligatoire
		String castorStartOfLinkId = castorPtLink.getStartOfLink();
		if (castorStartOfLinkId == null) {
			LoggingManager.log(logger, "Le \"startOfLinkId\" de ce \"PtLink\" () est null.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NULLSTARTOFLINKID_PTLINK, "Le \"startOfLinkId\" de ce \"PtLink\" () est null.", params);
		}
		else {
			if (!MainSchemaProducer.isTridentLike(castorStartOfLinkId)) {
				params = LoggingManager.getParams(castorStartOfLinkId);
				LoggingManager.log(logger, "Le \"startOfLink\" () pour ce \"PtLink\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDSTARTOFLINKID_PTLINK, "Le \"startOfLink\" () pour ce \"PtLink\" est invalide.", params);
			}
			ptLink.setStartOfLinkId(castorStartOfLinkId);
		}
		
		// EndOfLink obligatoire
		String castorEndOfLinkId = castorPtLink.getEndOfLink();
		params = null;
		if (ptLink.getName() != null)
			params = new String[] {ptLink.getName()};
		else if (ptLink.getObjectId() != null)
			params = new String[] {ptLink.getObjectId()};
		if (castorEndOfLinkId == null) {
			LoggingManager.log(logger, "Le \"endOfLinkId\" de ce \"PtLink\" () est null.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NULLENDOFLINKID_PTLINK, "Le \"endOfLinkId\" de ce \"PtLink\" () est null.", params);
		}
		else {
			if (!MainSchemaProducer.isTridentLike(castorEndOfLinkId)) {
				params = LoggingManager.getParams(castorEndOfLinkId);
				LoggingManager.log(logger, "Le \"endOfLink\" () pour ce \"PtLink\" est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDENDOFLINKID_PTLINK, "Le \"endOfLink\" () pour ce \"PtLink\" est invalide.", params);
			}
			ptLink.setEndOfLinkId(castorEndOfLinkId);
		}
		
		// LinkDistance optionnel
		BigDecimal castorLinkDistance = castorPtLink.getLinkDistance();
		params = null;
		if (ptLink.getName() != null)
			params = new String[] {ptLink.getName()};
		else if (ptLink.getObjectId() != null)
			params = new String[] {ptLink.getObjectId()};
		if (castorLinkDistance == null)
			LoggingManager.log(logger, "Le \"LinkDistance\" de ce \"PtLink\" () est null.", params, Level.INFO);
		else
			ptLink.setLinkDistance(castorLinkDistance);
		
		// Comment optionnel
		String castorComment = castorPtLink.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Le \"Comment\" de ce \"PtLink\" () est null.", params, Level.INFO);
		else
			if (castorComment.trim().length() == 0)
				LoggingManager.log(logger, "Le \"Comment\" de ce \"PtLink\" () est vide.", params, Level.WARN);
			else
				ptLink.setComment(castorComment);
		
		return ptLink;
	}
}
