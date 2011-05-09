package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.Address;
import fr.certu.chouette.service.validation.LongLatType;
import fr.certu.chouette.service.validation.ProjectedPoint;
import fr.certu.chouette.service.validation.StopPoint;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.math.BigDecimal;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class StopPointProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.StopPointProducer.class);
	private              ValidationException validationException;
	private              StopPoint           stopPoint           = null;
	
	StopPointProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setStopPoint(StopPoint stopPoint) {
		this.stopPoint = stopPoint;
	}
	
	StopPoint getStopPoint() {
		return stopPoint;
	}
	
	StopPoint getASG(chouette.schema.StopPoint castorStopPoint) {
		stopPoint = new StopPoint();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorStopPoint.getObjectId();
		params = new String[]{castorStopPoint.getName()};
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_STOPPOINT, "Pas de \"objectId\" pour ce \"StopPoint\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"StopPoint\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_STOPPOINT, "Pas de \"objectId\" pour ce \"StopPoint\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"StopPoint\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_STOPPOINT, "L'\"objectId\" () pour ce \"StopPoint\" est invalide.", params);
				}
				stopPoint.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorStopPoint.hasObjectVersion()) {
			int castorObjectVersion = (int)castorStopPoint.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = null;
				if (stopPoint.getObjectId() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, stopPoint.getObjectId());
				else if (castorStopPoint.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorStopPoint.getName());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"StopPoint\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_STOPPOINT, "La version () \"objectVersion\" du \"StopPoint\" () est invalide.", params);
			}
			else
				stopPoint.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorStopPoint.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"StopPoint\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorStopPoint.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorStopPoint.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"StopPoint\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorStopPoint.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"StopPoint\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_STOPPOINT, "La \"creationTime\" () de ce \"StopPoint\" () est invalide.", params);
			}
			else
				stopPoint.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorStopPoint.getCreatorId();
		params = new String[]{castorStopPoint.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"StopPoint\" () est vide.", params, Level.WARN);
			else
				stopPoint.setCreatorId(castorCreatorId);
		}
		
		// Name obligatoire
		String castorName = castorStopPoint.getName();
		if (castorName == null) {
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_STOPPOINT, "Pas de \"name\" pour ce \"StopPoint\" ().", params);
		}
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0) {
				LoggingManager.log(logger, "Pas de \"name\" pour ce \"StopPoint\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NONAME_STOPPOINT, "Pas de \"name\" pour ce \"StopPoint\" ().", params);
			}
			else
				stopPoint.setName(castorName);
		}
		
		// LineIdShortcut optionnel
		String castorLineIdShortcut = castorStopPoint.getLineIdShortcut();
		params = null;
		if (stopPoint.getName() != null)
			params = new String[]{stopPoint.getName()};
		else if (stopPoint.getObjectId() != null)
			params = new String[]{stopPoint.getObjectId()};
		if (castorLineIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"lineIdShortcut\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			castorLineIdShortcut = castorLineIdShortcut.trim();
			if (castorLineIdShortcut.length() == 0)
				LoggingManager.log(logger, "Le \"lineIdShortcut\" pour ce \"StopPoint\" est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorLineIdShortcut)) {
					params = new String[]{castorLineIdShortcut};
					LoggingManager.log(logger, "Le \"lineIdShortcut\" () pour ce \"StopPoint\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT_STOPPOINT, "Le \"lineIdShortcut\" () pour ce \"StopPoint\" est invalide.", params);
				}
				stopPoint.setLineIdShortcut(castorLineIdShortcut);
			}
		}
		
		// PtNetworkIdShortcut optionnel
		String castorPtNetworkIdShortcut = castorStopPoint.getPtNetworkIdShortcut();
		params = null;
		if (stopPoint.getName() != null)
			params = new String[]{stopPoint.getName()};
		else if (stopPoint.getObjectId() != null)
			params = new String[]{stopPoint.getObjectId()};
		if (castorPtNetworkIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"ptNetworkIdShortcut\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			castorPtNetworkIdShortcut = castorPtNetworkIdShortcut.trim();
			if (castorPtNetworkIdShortcut.length() == 0)
				LoggingManager.log(logger, "Le \"PtNetworkIdShortcut\" pour ce \"StopPoint\" est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorPtNetworkIdShortcut)) {
					params = new String[]{castorPtNetworkIdShortcut};
					LoggingManager.log(logger, "Le \"ptNetworkIdShortcut\" () pour ce \"StopPoint\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUT_STOPPOINT, "Le \"ptNetworkIdShortcut\" () pour ce \"StopPoint\" est invalide.", params);
				}
				stopPoint.setPtNetworkIdShortcut(castorPtNetworkIdShortcut);
			}
		}
		
		// Comment optionnel
		String castorComment = castorStopPoint.getComment();
		params = null;
		if (stopPoint.getName() != null)
			params = new String[]{stopPoint.getName()};
		else if (stopPoint.getObjectId() != null)
			params = new String[]{stopPoint.getObjectId()};
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"Comment\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"Comment\" de ce \"StopPoint\" () est vide.", params, Level.WARN);
			else
				stopPoint.setComment(castorComment);
		}
		
		// Longitude obligatoire
		BigDecimal castorLongitude = castorStopPoint.getLongitude();
		if (castorLongitude == null) {
			LoggingManager.log(logger, "Pas de \"Longitude\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NOLONGITUDE_STOPPOINT, "Pas de \"Longitude\" pour ce \"StopPoint\" ().", params);
		}
		else
			stopPoint.setLongitude(castorLongitude);

		// Latitude obligatoire
		BigDecimal castorLatitude = castorStopPoint.getLatitude();
		if (castorLatitude == null) {
			LoggingManager.log(logger, "Pas de \"Latitude\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NOLATITUDE_STOPPOINT, "Pas de \"Latitude\" pour ce \"StopPoint\" ().", params);
		}
		else
			stopPoint.setLatitude(castorLatitude);
		
		// LongLatType obligatoire
		if (castorStopPoint.getLongLatType() == null) 
		{
			LoggingManager.log(logger, "Pas de \"LongLatType\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NOLONGLATTYPE_STOPPOINT, "Pas de \"LongLatType\" pour ce \"StopPoint\" ().", params);
		}
		else
		{
			switch (castorStopPoint.getLongLatType()) 
			{
				case STANDARD:
					stopPoint.setLongLatType(LongLatType.STANDARD);
					break;
				case WGS84:
					stopPoint.setLongLatType(LongLatType.WGS84);
					break;
				case WGS92:
					stopPoint.setLongLatType(LongLatType.WGS92);
					break;
				default:
					LoggingManager.log(logger, "Le \"LongLatType\" pour ce \"StopPoint\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLONGLATTYPE_STOPPOINT, "Le \"LongLatType\" pour ce \"StopPoint\" () est invalide.", params);				
			}
		}
		// Address optionnel
		chouette.schema.Address castorAddress = castorStopPoint.getAddress();
		if (castorAddress == null)
			LoggingManager.log(logger, "Pas d'\"address\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			Address address = (new AddressProducer(validationException)).getASG(castorAddress);
			if (address == null)
				LoggingManager.log(logger, "Erreur lors de la construction de l'\"address\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			else {
				stopPoint.setAddress(address);
				address.setStopPoint(stopPoint);
			}
		}
		
		// ProjectedPoint optionnel
		chouette.schema.ProjectedPoint castorProjectedPoint = castorStopPoint.getProjectedPoint();
		if (castorProjectedPoint == null)
			LoggingManager.log(logger, "Pas de \"projectedPoint\" pour ce \"StopPoint\" ().", params, Level.INFO);
		else {
			ProjectedPoint projectedPoint = (new ProjectedPointProducer(validationException)).getASG(castorProjectedPoint);
			if (projectedPoint == null)
				LoggingManager.log(logger, "Erreur lors de la construction du \"projectedPoint\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			else {
				stopPoint.setProjectedPoint(projectedPoint);
				projectedPoint.setStopPoint(stopPoint);
			}
		}
		
		// ContainedIn obligatoire
		String castorContainedIn = castorStopPoint.getContainedIn();
		if (castorContainedIn == null) {
			LoggingManager.log(logger, "Pas de \"containedIn\" pour ce \"StopPoint\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOCONTAINEDIN_STOPPOINT, "Pas de \"containedIn\" pour ce \"StopPoint\" ().", params);
		}
		else {
			castorContainedIn = castorContainedIn.trim();
			if (castorContainedIn.length() == 0) {
				LoggingManager.log(logger, "Pas de \"containedIn\" pour ce \"StopPoint\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOCONTAINEDIN_STOPPOINT, "Pas de \"containedIn\" pour ce \"StopPoint\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorContainedIn)) {
					params = new String[]{castorContainedIn};
					LoggingManager.log(logger, "Le \"containedIn\" () pour ce \"StopPoint\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDCONTAINEDINID_STOPPOINT, "Le \"containedIn\" () pour ce \"StopPoint\" est invalide.", params);
				}
				stopPoint.setContainedInStopAreaId(castorContainedIn);
			}
		}
		
		return stopPoint;
	}
}
