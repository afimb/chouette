package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.StopAreaExtensionProducer;
import fr.certu.chouette.service.validation.StopArea;
import fr.certu.chouette.service.validation.StopAreaExtension;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class StopAreaProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.AreaCentroidProducer.class);
	private              ValidationException validationException;
	private              StopArea            stopArea            = null;
	
	StopAreaProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setStopArea(StopArea stopArea) {
		this.stopArea = stopArea;
	}
	
	StopArea getStopArea() {
		return stopArea;
	}
	
	StopArea getASG(chouette.schema.StopArea castorStopArea) {
		stopArea = new StopArea();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorStopArea.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorStopArea.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"StopArea\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_STOPAREA, "Pas de \"objectId\" pour ce \"StopArea\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				params = LoggingManager.getParams(castorStopArea.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"StopArea\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_STOPAREA, "Pas de \"objectId\" pour ce \"StopArea\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"StopArea\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_STOPAREA, "L'\"objectId\" () pour ce \"StopArea\" est invalide.", params);
				}
				stopArea.setObjectId(castorObjectId);
			}
		}
		
		// ObjectVersion optionnel
		if (castorStopArea.hasObjectVersion()) {
			int castorObjectVersion = (int)castorStopArea.getObjectVersion();
			if (castorObjectVersion < 1) {
				if (castorStopArea.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorStopArea.getName());
				else
					params = LoggingManager.getParams(""+castorObjectVersion, castorStopArea.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"StopArea\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_STOPAREA, "La version () \"objectVersion\" du \"StopArea\" () est invalide.", params);
			}
			else
				stopArea.setObjectVersion(castorObjectVersion);
		}
		else {
			if (castorStopArea.getName() != null)
				params = LoggingManager.getParams(castorStopArea.getName());
			else
				params = LoggingManager.getParams(castorStopArea.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"StopArea\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorStopArea.getCreationTime();
		if (castorCreationTime == null) {
			if (castorStopArea.getName() != null)
				params = LoggingManager.getParams(castorStopArea.getName());
			else
				params = LoggingManager.getParams(castorStopArea.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"StopArea\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorStopArea.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorStopArea.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorStopArea.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"StopArea\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_STOPAREA, "La \"creationTime\" () de ce \"StopArea\" () est invalide.", params);
			}
			else
				stopArea.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorStopArea.getCreatorId();
		params = null;
		if (castorStopArea.getName() != null)
			params = new String[]{castorStopArea.getName()};
		else if (castorStopArea.getObjectId() != null)
			params = new String[]{castorStopArea.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"StopArea\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"StopArea\" () est vide.", params, Level.WARN);
			else
				stopArea.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorStopArea.getName();
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"StopArea\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"name\" dans ce \"StopArea\" () est vide.", params, Level.WARN);
			else
				stopArea.setName(castorName);
		}
		
		// contains [1..w]
		String[] castorContainedStopIds = castorStopArea.getContains();
		if (castorContainedStopIds == null) {
			LoggingManager.log(logger, "Pas de \"contains\" pour ce \"StopArea\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOCONTAINS_STOPAREA, "Pas de \"contains\" pour ce \"StopArea\" ().", params);
		}
		else
			if (castorContainedStopIds.length == 0) {
				LoggingManager.log(logger, "La liste des \"contains\" pour ce \"StopArea\" () est vide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOCONTAINS_STOPAREA, "Pas de \"contains\" pour ce \"StopArea\" ().", params);
			}
			else {
				Set<String> ontainedStopIds = new HashSet<String>();
				for (int i = 0; i < castorContainedStopIds.length; i++)
					if ((castorContainedStopIds[i] != null) && (castorContainedStopIds[i].trim().length() > 0))
						if (!ontainedStopIds.add(castorContainedStopIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"contains\" pour ce \"StopArea\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (ontainedStopIds.size() == 0) {
					LoggingManager.log(logger, "La liste des \"contains\" pour ce \"StopArea\" () ne contient que des \"objectsId\" vide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.NOCONTAINS_STOPAREA, "Pas de \"contains\" pour ce \"StopArea\" ().", params);
				}
				else {
					for (String lineId : ontainedStopIds)
						if (!MainSchemaProducer.isTridentLike(lineId)) {
							params = new String[]{lineId, ""};
							if (castorStopArea.getName() != null)
								params = new String[]{lineId, castorStopArea.getName()};
							else if (castorStopArea.getObjectId() != null)
								params = new String[]{lineId, castorStopArea.getObjectId()};
							LoggingManager.log(logger, "Le \"contains\" () pour ce \"StopArea\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDCONTAINEDSTOP_STOPAREA, "Le \"contains\" () pour ce \"StopArea\" () est invalide.", params);
						}
					stopArea.setContainedStopIds((String[])ontainedStopIds.toArray(new String[0]));
				}
			}
		
		// BoundaryPoint [0..w]
		if (castorStopArea.getBoundaryPoint() == null)
			LoggingManager.log(logger, "Pas de \"boundaryPoint\" pour ce \"StopArea\" ().", params, Level.INFO);
		else
			if (castorStopArea.getBoundaryPointCount() == 0)
				LoggingManager.log(logger, "La liste des \"boundaryPoint\" pour ce \"StopArea\" () est vide.", params, Level.WARN);
			else {
				Set<String> boundaryPoints = new HashSet<String>();
				for (int i = 0; i < castorStopArea.getBoundaryPointCount(); i++)
					if ((castorStopArea.getBoundaryPoint(i) != null) && (castorStopArea.getBoundaryPoint(i).trim().length() > 0))
						if (!boundaryPoints.add(castorStopArea.getBoundaryPoint(i).trim()))
							LoggingManager.log(logger, "La liste des \"boundaryPoint\" pour ce \"StopArea\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (boundaryPoints.size() == 0)
					LoggingManager.log(logger, "La liste des \"boundaryPoint\" pour ce \"StopArea\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String boundaryPoint : boundaryPoints)
						if (!MainSchemaProducer.isTridentLike(boundaryPoint)) {
							params = new String[]{boundaryPoint, ""};
							if (castorStopArea.getName() != null)
								params = new String[]{boundaryPoint, castorStopArea.getName()};
							else if (castorStopArea.getObjectId() != null)
								params = new String[]{boundaryPoint, castorStopArea.getObjectId()};
							LoggingManager.log(logger, "Le \"boundaryPoint\" () pour ce \"StopArea\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDBOUNDARYPOINT_STOPAREA, "Le \"boundaryPoint\" () pour ce \"StopArea\" () est invalide.", params);
						}
					stopArea.setBoundaryPoints((String[])boundaryPoints.toArray(new String[0]));
				}
			}
		
		// CentroidOfArea optionnel
		String castorCentroidOfArea = castorStopArea.getCentroidOfArea();
		if (castorCentroidOfArea == null)
			LoggingManager.log(logger, "Pas de \"centroidOfArea\" pour ce \"StopArea\" ().", params, Level.INFO);
		else {
			castorCentroidOfArea = castorCentroidOfArea.trim();
			if (castorCentroidOfArea.length() == 0)
				LoggingManager.log(logger, "Le \"centroidOfArea\" pour ce \"StopArea\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorCentroidOfArea)) {
					params = new String[]{castorCentroidOfArea, ""};
					if (castorStopArea.getName() != null)
						params = new String[]{castorCentroidOfArea, castorStopArea.getName()};
					else if (castorStopArea.getObjectId() != null)
						params = new String[]{castorCentroidOfArea, castorStopArea.getObjectId()};
					LoggingManager.log(logger, "Le \"centroidOfArea\" () pour ce \"StopArea\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDCENTROIDOFAREA_STOPAREA, "Le \"centroidOfArea\" () pour ce \"StopArea\" () est invalide.", params);
				}
				stopArea.setCentroidOfArea(castorCentroidOfArea);
			}
		}
		
		// Comment optionnel
		String castorComment = castorStopArea.getComment();
		params = null;
		if (castorStopArea.getName() != null)
			params = new String[]{castorStopArea.getName()};
		else if (castorStopArea.getObjectId() != null)
			params = new String[]{castorStopArea.getObjectId()};			
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"StopArea\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"StopArea\" () est vide.", params, Level.WARN);
			else
				stopArea.setComment(castorComment);
		}
		
		// StopAreaExtension optionnel
		chouette.schema.StopAreaExtension castorStopAreaExtension = castorStopArea.getStopAreaExtension();
		if (castorStopAreaExtension == null)
			LoggingManager.log(logger, "Pas de \"StopAreaExtension\" pour ce \"PTNetwork\" ().", params, Level.INFO);
		else {
			StopAreaExtension stopAreaExtension = (new StopAreaExtensionProducer(validationException)).getASG(castorStopAreaExtension);
			if (stopAreaExtension == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"StopAreaExtension\" pour ce \"PTNetwork\" ().", params, Level.ERROR);
			else {
				stopArea.setStopAreaExtension(stopAreaExtension);
				stopAreaExtension.setStopArea(stopArea);
			}
		}
		
		return stopArea;
	}
}
