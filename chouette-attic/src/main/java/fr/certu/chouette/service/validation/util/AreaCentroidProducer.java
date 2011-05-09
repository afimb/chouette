package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.Address;
import fr.certu.chouette.service.validation.AreaCentroid;
import fr.certu.chouette.service.validation.LongLatType;
import fr.certu.chouette.service.validation.ProjectedPoint;
import java.math.BigDecimal;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class AreaCentroidProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.AreaCentroidProducer.class);
	private              ValidationException validationException;
	private              AreaCentroid        areaCentroid        = null;
	
	AreaCentroidProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setAreaCentroid(AreaCentroid areaCentroid) {
		this.areaCentroid = areaCentroid;
	}
	
	AreaCentroid getAreaCentroid() {
		return areaCentroid;
	}
	
	AreaCentroid getASG(chouette.schema.AreaCentroid castorAreaCentroid) {
		AreaCentroid areaCentroid = new AreaCentroid();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorAreaCentroid.getObjectId();
		if (castorObjectId == null) {
			params = LoggingManager.getParams(castorAreaCentroid.getName());
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_AREACENTROID, "Pas de \"objectId\" pour ce \"AreaCentroid\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				params = LoggingManager.getParams(castorAreaCentroid.getName());
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_AREACENTROID, "Pas de \"objectId\" pour ce \"AreaCentroid\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"AreaCentroid\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_AREACENTROID, "L'\"objectId\" () pour ce \"AreaCentroid\" est invalide.", params);
				}
				areaCentroid.setObjectId(castorObjectId);
			}
		}
		
		// ObjectVersion optionnel
		if (castorAreaCentroid.hasObjectVersion()) {
			int castorObjectVersion = (int)castorAreaCentroid.getObjectVersion();
			if (castorObjectVersion < 1) {
                            if (castorAreaCentroid.getName() != null)
                                params = LoggingManager.getParams(""+castorObjectVersion, castorAreaCentroid.getName());
                            else
                                params = LoggingManager.getParams(""+castorObjectVersion, castorAreaCentroid.getObjectId());
                            LoggingManager.log(logger, "La version () \"objectVersion\" du \"AreaCentroid\" () est invalide.", params, Level.ERROR);
                            validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_AREACENTROID, "La version () \"objectVersion\" du \"AreaCentroid\" () est invalide.", params);
			}
			else
                            areaCentroid.setObjectVersion(castorObjectVersion);
		}
		else {
                    if (castorAreaCentroid.getName() != null)
                        params = LoggingManager.getParams(castorAreaCentroid.getName());
                    else
				params = LoggingManager.getParams(castorAreaCentroid.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorAreaCentroid.getCreationTime();
		if (castorCreationTime == null) {
			if (castorAreaCentroid.getName() != null)
				params = LoggingManager.getParams(castorAreaCentroid.getName());
			else
				params = LoggingManager.getParams(castorAreaCentroid.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				if (castorAreaCentroid.getName() != null)
					params = LoggingManager.getParams(castorCreationTime.toString(), castorAreaCentroid.getName());
				else
					params = LoggingManager.getParams(castorCreationTime.toString(), castorAreaCentroid.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"AreaCentroid\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_AREACENTROID, "La \"creationTime\" () de ce \"AreaCentroid\" () est invalide.", params);
			}
			else
				areaCentroid.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorAreaCentroid.getCreatorId();
		params = null;
		if (castorAreaCentroid.getName() != null)
			params = new String[]{castorAreaCentroid.getName()};
		else if (castorAreaCentroid.getObjectId() != null)
			params = new String[]{castorAreaCentroid.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"AreaCentroid\" () est vide.", params, Level.WARN);
			else
				areaCentroid.setCreatorId(castorCreatorId);
		}
		
		// Longitude obligatoire
		BigDecimal castorLongitude = castorAreaCentroid.getLongitude();
		if (castorLongitude == null) {
			LoggingManager.log(logger, "Pas de \"longitude\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NULLLONGITUDE_AREACENTROID, "Pas de \"longitude\" pour ce \"AreaCentroid\" ().", params);
		}
		else
			areaCentroid.setLongitude(castorLongitude);
		
		// Latitude obligatoire
		BigDecimal castorLatitude = castorAreaCentroid.getLatitude();
		if (castorLatitude == null) {
			LoggingManager.log(logger, "Pas de \"latitude\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NULLLATITUDE_AREACENTROID, "Pas de \"latitude\" pour ce \"AreaCentroid\" ().", params);
		}
		else
			areaCentroid.setLatitude(castorLatitude);
		
		// LongLatType obligatoire
		chouette.schema.types.LongLatTypeType castorLongLatType = castorAreaCentroid.getLongLatType();
		if (castorLongLatType == null) {
			LoggingManager.log(logger, "Pas de \"longlattype\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			//validationException.add(TypeInvalidite.NULLLONGLATTYPE_AREACENTROID, "Pas de \"longlattype\" pour ce \"AreaCentroid\" ().", params);
		}
		else
		{	
			switch (castorAreaCentroid.getLongLatType()) 
			{
				case STANDARD:
					areaCentroid.setLongLatType(LongLatType.STANDARD);
					break;
				case WGS84:
					areaCentroid.setLongLatType(LongLatType.WGS84);
					break;
				case WGS92:
					areaCentroid.setLongLatType(LongLatType.WGS92);
					break;
				default:
					LoggingManager.log(logger, "Le \"longlattype\" de ce \"AreaCentroid\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLONGLATTYPE_AREACENTROID, "Le \"longlattype\" de ce \"AreaCentroid\" () est invalide.", params);
				}
		}
		// Address optionnel
		chouette.schema.Address castorAddress = castorAreaCentroid.getAddress();
		if (castorAddress == null)
			LoggingManager.log(logger, "Pas de \"address\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		else {
			Address address = (new AddressProducer(validationException)).getASG(castorAddress);
			if (address == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"address\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			else {
				address.setAreaCentroid(areaCentroid);
				areaCentroid.setAddress(address);
			}
		}
		
		// ProjectedPoint optionnel
		chouette.schema.ProjectedPoint castorProjectedPoint = castorAreaCentroid.getProjectedPoint();
		if (castorProjectedPoint == null)
			LoggingManager.log(logger, "Pas de \"projectedPoint\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		else {
			ProjectedPoint projectedPoint = (new ProjectedPointProducer(validationException)).getASG(castorProjectedPoint);
			if (projectedPoint == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"projectedPoint\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			else {
				areaCentroid.setProjectedPoint(projectedPoint);
				projectedPoint.setAreaCentroid(areaCentroid);
			}
		}
		
		// ContainedIn obligatoire
		String castorContainedIn = castorAreaCentroid.getContainedIn();
		if (castorContainedIn == null) {
			LoggingManager.log(logger, "Pas de \"ContainedIn\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOCONTAINEDIN_AREACENTROID, "Pas de \"ContainedIn\" pour ce \"AreaCentroid\" ().", params);
		}
		else {
			castorContainedIn = castorContainedIn.trim();
			if (castorContainedIn.length() == 0) {
				LoggingManager.log(logger, "Pas de \"ContainedIn\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOCONTAINEDIN_AREACENTROID, "Pas de \"ContainedIn\" pour ce \"AreaCentroid\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorContainedIn)) {
					params = LoggingManager.getParams(castorContainedIn);
					LoggingManager.log(logger, "Le \"containedIn\" () pour ce \"AreaCentroid\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDCONTAINEDIN_AREACENTROID, "Le \"containedIn\" () pour ce \"AreaCentroid\" est invalide.", params);
				}
				areaCentroid.setContainedIn(castorContainedIn);
			}
		}
		
		// Name obligatoire
		String castorName = castorAreaCentroid.getName();
		if (castorName == null) {
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NONAME_AREACENTROID, "Pas de \"name\" pour ce \"AreaCentroid\" ().", params);
		}
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0) {
				LoggingManager.log(logger, "Pas de \"name\" pour ce \"AreaCentroid\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NONAME_AREACENTROID, "Pas de \"name\" pour ce \"AreaCentroid\" ().", params);
			}
			else
				areaCentroid.setName(castorName);
		}
		
		// Comment optionnel
		String castorComment = castorAreaCentroid.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"AreaCentroid\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" pour ce \"AreaCentroid\" () est vide.", params, Level.WARN);
			else
				areaCentroid.setComment(castorComment);
		}
		
		return areaCentroid;
	}
}
