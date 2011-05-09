package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.JourneyPattern;
import fr.certu.chouette.service.validation.Registration;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class JourneyPatternProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.JourneyPatternProducer.class);
	private              ValidationException validationException;
	private              JourneyPattern      journeyPattern      = null;
    
	JourneyPatternProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setJourneyPattern(JourneyPattern journeyPattern) {
		this.journeyPattern = journeyPattern;
	}
	
	JourneyPattern getJourneyPattern() {
		return journeyPattern;
	}
	
	JourneyPattern getASG(chouette.schema.JourneyPattern castorJourneyPattern) {
		journeyPattern = new JourneyPattern();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorJourneyPattern.getObjectId();
		params = new String[]{castorJourneyPattern.getName()};
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"JourneyPattern\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_JOURNEYPATTERN, "Pas de \"objectId\" pour ce \"JourneyPattern\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"JourneyPattern\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_JOURNEYPATTERN, "Pas de \"objectId\" pour ce \"JourneyPattern\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"JourneyPattern\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_JOURNEYPATTERN, "L'\"objectId\" () pour ce \"JourneyPattern\" est invalide.", params);
				}
				journeyPattern.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorJourneyPattern.hasObjectVersion()) {
			int castorObjectVersion = (int)castorJourneyPattern.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = null;
				if (journeyPattern.getObjectId() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, journeyPattern.getObjectId());
				else if (castorJourneyPattern.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorJourneyPattern.getName());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"JourneyPattern\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_JOURNEYPATTERN, "La version () \"objectVersion\" du \"JourneyPattern\" () est invalide.", params);
			}
			else
				journeyPattern.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorJourneyPattern.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorJourneyPattern.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorJourneyPattern.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorJourneyPattern.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"JourneyPattern\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_JOURNEYPATTERN, "La \"creationTime\" () de ce \"JourneyPattern\" () est invalide.", params);
			}
			else
				journeyPattern.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorJourneyPattern.getCreatorId();
		params = new String[]{castorJourneyPattern.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"JourneyPattern\" () est vide.", params, Level.WARN);
			else
				journeyPattern.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorJourneyPattern.getName();
		params = LoggingManager.getParams(castorJourneyPattern.getObjectId());
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "Le \"name\" de ce \"JourneyPattern\" () est vide.", params, Level.WARN);
			else
				journeyPattern.setName(castorName);
		}
		
		// PublishedName optionnel
		String castorPublishedName = castorJourneyPattern.getPublishedName();
		params = null;
		if (journeyPattern.getName() != null)
			params = new String[]{journeyPattern.getName()};
		else if (journeyPattern.getObjectId() != null)
			params = new String[]{journeyPattern.getObjectId()};
		if (castorPublishedName == null)
			LoggingManager.log(logger, "Pas de \"publishedName\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorPublishedName = castorPublishedName.trim();
			if (castorPublishedName.length() == 0)
				LoggingManager.log(logger, "Le \"publishedName\" pour ce \"JourneyPattern\" est vide ().", params, Level.WARN);
			else
				journeyPattern.setPublishedName(castorPublishedName);
		}
		
		// RouteId obligatoire
		String castorRouteId = castorJourneyPattern.getRouteId();
		if (castorRouteId == null) {
			LoggingManager.log(logger, "Pas de \"routeId\" pour ce \"JourneyPattern\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOROUTEID_JOURNEYPATTERN, "Pas de \"routeId\" pour ce \"JourneyPattern\" ().", params);
		}
		else {
			castorRouteId = castorRouteId.trim();
			if (castorRouteId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"routeId\" pour ce \"JourneyPattern\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOROUTEID_JOURNEYPATTERN, "Pas de \"routeId\" pour ce \"JourneyPattern\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorRouteId)) {
					params = LoggingManager.getParams(castorRouteId);
					LoggingManager.log(logger, "Le \"routeId\" () pour ce \"JourneyPattern\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTEID_JOURNEYPATTERN, "Le \"routeId\" () pour ce \"JourneyPattern\" est invalide.", params);
				}
				journeyPattern.setRouteId(castorRouteId);
			}
		}
		
		// Origin optionnel
		String castorOrigin = castorJourneyPattern.getOrigin();
		if (castorOrigin == null)
			LoggingManager.log(logger, "Pas de \"origin\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorOrigin = castorOrigin.trim();
			if (castorOrigin.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"origin\" dans ce \"JourneyPattern\" est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorOrigin)) {
					params = LoggingManager.getParams(castorOrigin);
					LoggingManager.log(logger, "Le \"origin\" () pour ce \"JourneyPattern\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDORIGIN_JOURNEYPATTERN, "Le \"origin\" () pour ce \"JourneyPattern\" est invalide.", params);
				}
				journeyPattern.setOrigin(castorOrigin);
			}
		}
		
		// Destination optionnel
		String castorDestination = castorJourneyPattern.getDestination();
		params = null;
		if (journeyPattern.getName() != null)
			params = new String[]{journeyPattern.getName()};
		else if (journeyPattern.getObjectId() != null)
			params = new String[]{journeyPattern.getObjectId()};
		if (castorDestination == null)
			LoggingManager.log(logger, "Pas de \"destination\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorDestination = castorDestination.trim();
			if (castorDestination.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"destination\" dans ce \"JourneyPattern\" est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorDestination)) {
					params = LoggingManager.getParams(castorDestination);
					LoggingManager.log(logger, "Le \"destination\" () pour ce \"JourneyPattern\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDDESTINATION_JOURNEYPATTERN, "Le \"destination\" () pour ce \"JourneyPattern\" est invalide.", params);
				}
				journeyPattern.setDestination(castorDestination);
			}
		}
		
		// StopPointList [2..w]
		String[] castorStopPointList = castorJourneyPattern.getStopPointList();
		params = null;
		if (journeyPattern.getName() != null)
			params = new String[]{journeyPattern.getName()};
		else if (journeyPattern.getObjectId() != null)
			params = new String[]{journeyPattern.getObjectId()};
		if (castorStopPointList == null) {
			LoggingManager.log(logger, "La liste des \"stopPointList\" pour ce \"JourneyPattern\" () est null.", params, Level.ERROR);
			validationException.add(TypeInvalidite.INVALIDSTOPPOINTLIST_JOURNEYPATTERN, "La liste des \"stopPointList\" pour ce \"JourneyPattern\" () est null.", params);
		}
		else
			if (castorStopPointList.length < 2) {
				LoggingManager.log(logger, "La liste des \"stopPointList\" pour ce \"JourneyPattern\" () contient moins de deux elements.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDSTOPPOINTLIST_JOURNEYPATTERN, "La liste des \"stopPointList\" pour ce \"JourneyPattern\" () contient moins de deux elements.", params);
			}
			else {
				Set<String> stopPoints = new HashSet<String>();
				for (int i = 0; i < castorStopPointList.length; i++)
					if ((castorStopPointList[i] != null) && (castorStopPointList[i].trim().length() > 0))
						if (!stopPoints.add(castorStopPointList[i].trim()))
							LoggingManager.log(logger, "La liste \"stopPointList\" pour ce \"JourneyPattern\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (stopPoints.size() == 0)
					LoggingManager.log(logger, "La liste \"stopPointList\" pour ce \"JourneyPattern\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String stopPoint : stopPoints)
						if (!MainSchemaProducer.isTridentLike(stopPoint)) {
							params = new String[]{stopPoint, ""};
							if (journeyPattern.getName() != null)
								params = new String[]{stopPoint, journeyPattern.getName()};
							else if (journeyPattern.getObjectId() != null)
								params = new String[]{stopPoint, journeyPattern.getObjectId()};
							LoggingManager.log(logger, "Un element de \"stopPointList\" () de ce \"JourneyPattern\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDSTOPPOINTID_JOURNEYPATTERN, "Un element de \"stopPointList\" () de ce \"JourneyPattern\" () est invalide.", params);
						}
					journeyPattern.setStopPointList(castorStopPointList);
					//journeyPattern.setStopPointList((String[])stopPoints.toArray(new String[0]));
				}
			}
		
		// Registration optionnel
		chouette.schema.Registration castorRegistration = castorJourneyPattern.getRegistration();
		params = null;
		if (journeyPattern.getName() != null)
			params = new String[]{journeyPattern.getName()};
		else if (journeyPattern.getObjectId() != null)
			params = new String[]{journeyPattern.getObjectId()};
		if (castorRegistration == null)
			LoggingManager.log(logger, "Pas de \"Registration\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			Registration registration = (new RegistrationProducer(validationException)).getASG(castorRegistration);
			if (castorRegistration == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"Registration\" pour ce \"JourneyPattern\" ().", params, Level.ERROR);
			else {
				journeyPattern.setRegistration(registration);
				registration.setJourneyPattern(journeyPattern);
			}
		}
		
		// LineIdShortcut optionnel
		String castorLineIdShortcut = castorJourneyPattern.getLineIdShortcut();
		if (castorLineIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"lineIdShortcut\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorLineIdShortcut = castorLineIdShortcut.trim();
			if (castorLineIdShortcut.length() == 0)
				LoggingManager.log(logger, "Lobjet de type \"lineIdShortcut\" dans ce \"JourneyPattern\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorLineIdShortcut)) {
					params = new String[]{castorLineIdShortcut, ""};
					if (journeyPattern.getName() != null)
						params = new String[]{castorLineIdShortcut, journeyPattern.getName()};
					else if (journeyPattern.getObjectId() != null)
						params = new String[]{castorLineIdShortcut, journeyPattern.getObjectId()};
					LoggingManager.log(logger, "Le \"lineIdShortcut\" () de ce \"JourneyPattern\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT_JOURNEYPATTERN, "Le \"lineIdShortcut\" () de ce \"JourneyPattern\" () est invalide.", params);
				}
				journeyPattern.setLineIdShortcut(castorLineIdShortcut);
			}
		}
		
		// Comment optionnel
		String castorComment = castorJourneyPattern.getComment();
		params = null;
		if (journeyPattern.getName() != null)
			params = new String[]{journeyPattern.getName()};
		else if (journeyPattern.getObjectId() != null)
			params = new String[]{journeyPattern.getObjectId()};
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"Comment\" pour ce \"JourneyPattern\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Lobjet de type \"Comment\" dans ce \"JourneyPattern\" () est vide.", params, Level.WARN);
			else
				journeyPattern.setComment(castorComment);
		}
		
		return journeyPattern;
	}
}
