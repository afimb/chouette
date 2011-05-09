package fr.certu.chouette.service.validation.util;

import java.util.HashSet;
import java.util.Set;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.util.GroupOfLineProducer;
import fr.certu.chouette.service.validation.GroupOfLine;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class GroupOfLineProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.GroupOfLineProducer.class);
	private              ValidationException validationException;
	private              GroupOfLine         groupOfLine         = null;
	
	GroupOfLineProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setGroupOfLine(GroupOfLine groupOfLine) {
		this.groupOfLine = groupOfLine;
	}
	
	GroupOfLine getGroupOfLine() {
		return groupOfLine;
	}
	
	GroupOfLine getASG(chouette.schema.GroupOfLine castorGroupOfLine) {
		groupOfLine = new GroupOfLine();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorGroupOfLine.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorGroupOfLine.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"GroupOfLine\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_GROUPOFLINE, "Pas de \"ObjectId\" pour ce \"GroupOfLine\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length()== 0) {
				params = LoggingManager.getParams(castorGroupOfLine.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"GroupOfLine\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_GROUPOFLINE, "Pas de \"ObjectId\" vide pour ce \"GroupOfLine\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"GroupOfLine\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_GROUPOFLINE, "L'\"objectId\" () pour ce \"GroupOfLine\" est invalide.", params);
				}
				groupOfLine.setObjectId(castorObjectId);
			}
		}
		
		// ObjectVersion optionnel
		if (castorGroupOfLine.hasObjectVersion()) {
			int castorObjectVersion = (int)castorGroupOfLine.getObjectVersion();
			if (castorObjectVersion < 1) {
				if (castorGroupOfLine.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorGroupOfLine.getName());
				else
					params = LoggingManager.getParams(""+castorObjectVersion, castorGroupOfLine.getObjectId());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"GroupOfLine\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_GROUPOFLINE, "La version () \"objectVersion\" du \"GroupOfLine\" () est invalide.", params);
			}
			else
				groupOfLine.setObjectVersion(castorObjectVersion);
		}
		else {
			if (castorGroupOfLine.getName() != null)
				params = LoggingManager.getParams(castorGroupOfLine.getName());
			else
				params = LoggingManager.getParams(castorGroupOfLine.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"GroupOfLine\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorGroupOfLine.getCreationTime();
		if (castorCreationTime == null) {
			if (castorGroupOfLine.getName() != null)
				params = LoggingManager.getParams(castorGroupOfLine.getName());
			else
				params = LoggingManager.getParams(castorGroupOfLine.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"GroupOfLine\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorGroupOfLine.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorGroupOfLine.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorGroupOfLine.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"GroupOfLine\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_GROUPOFLINE, "La \"creationTime\" () de ce \"GroupOfLine\" () est invalide.", params);
			}
			else
				groupOfLine.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorGroupOfLine.getCreatorId();
		params = null;
		if (castorGroupOfLine.getName() != null)
			params = new String[]{castorGroupOfLine.getName()};
		else if (castorGroupOfLine.getObjectId() != null)
			params = new String[]{castorGroupOfLine.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"GroupOfLine\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"GroupOfLine\" () est vide.", params, Level.WARN);
			else
				groupOfLine.setCreatorId(castorCreatorId);
		}
		
		// Name obligatoire
		String castorName = castorGroupOfLine.getName();
		params = null;
		if (castorGroupOfLine.getObjectId() != null)
			params = new String[]{castorGroupOfLine.getObjectId()};			
		if (castorName == null) {
			LoggingManager.log(logger, "Pas de \"Name\" pour ce \"GroupOfLine\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_GROUPOFLINE, "Pas de \"name\" pour ce \"GroupOfLine\" ().", params);
		}
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0) {
				LoggingManager.log(logger, "Pas de \"Name\" pour ce \"GroupOfLine\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NONAME_GROUPOFLINE, "Pas de \"name\" pour ce \"GroupOfLine\" ().", params);
			}
			else
				groupOfLine.setName(castorName);
		}
		
		// LineId [1..w]
		String[] castorLineIds = castorGroupOfLine.getLineId();
		params = null;
		if (castorGroupOfLine.getName() != null)
			params = new String[]{castorGroupOfLine.getName()};
		else if (castorGroupOfLine.getObjectId() != null)
			params = new String[]{castorGroupOfLine.getObjectId()};			
		if (castorLineIds == null) {
			LoggingManager.log(logger, "Pas de \"lineId\" pour ce \"GroupOfLine\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOLINEID_GROUPOFLINE, "Pas de \"lineId\" pour ce \"GroupOfLine\" ().", params);
		}
		else
			if (castorLineIds.length == 0) {
				LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"GroupOfLine\" () est vide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOLINEID_GROUPOFLINE, "Pas de \"lineId\" pour ce \"GroupOfLine\" ().", params);
			}
			else {
				Set<String> lineIds = new HashSet<String>();
				for (int i = 0; i < castorLineIds.length; i++)
					if ((castorLineIds[i] != null) && (castorLineIds[i].trim().length() > 0))
						if (!lineIds.add(castorLineIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"GroupOfLine\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (lineIds.size() == 0) {
					LoggingManager.log(logger, "La liste des \"lineId\" pour ce \"GroupOfLine\" () ne contient que des \"objectsId\" vide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.NOLINEID_GROUPOFLINE, "Pas de \"lineId\" pour ce \"GroupOfLine\" ().", params);
				}
				else {
					for (String lineId : lineIds)
						if (!MainSchemaProducer.isTridentLike(lineId)) {
							params = new String[]{lineId, ""};
							if (castorGroupOfLine.getName() != null)
								params = new String[]{lineId, castorGroupOfLine.getName()};
							else if (castorGroupOfLine.getObjectId() != null)
								params = new String[]{lineId, castorGroupOfLine.getObjectId()};
							LoggingManager.log(logger, "Le \"lineId\" () pour ce \"GroupOfLine\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDLINEID_GROUPOFLINE, "Le \"lineId\" () pour ce \"GroupOfLine\" () est invalide.", params);
						}
					groupOfLine.setLineIds((String[])lineIds.toArray(new String[0]));
				}
			}
		
		// Comment optionnel
		String castorComment = castorGroupOfLine.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"GroupOfLine\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"GroupOfLine\" () est vide.", params, Level.WARN);
			else
				groupOfLine.setComment(castorComment);
		}
		
		return groupOfLine;
	}
}
