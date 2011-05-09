package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ChouetteRoute;
import fr.certu.chouette.service.validation.PTDirectionType;
import fr.certu.chouette.service.validation.RouteExtension;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class ChouetteRouteProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.ChouetteRouteProducer.class);
	private              ValidationException validationException;
	private              ChouetteRoute       chouetteRoute       = null;
	
	ChouetteRouteProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setChouetteRoute(ChouetteRoute chouetteRoute) {
		this.chouetteRoute = chouetteRoute;
	}
	
	ChouetteRoute getChouetteRoute() {
		return chouetteRoute;
	}
	
	ChouetteRoute getASG(chouette.schema.ChouetteRoute castorChouetteRoute) {
		chouetteRoute = new ChouetteRoute();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorChouetteRoute.getObjectId();
		params = new String[]{castorChouetteRoute.getName()};
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"ChouetteRoute\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_CHOUETTEROUTE, "Pas de \"objectId\" pour ce \"ChouetteRoute\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"ChouetteRoute\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_CHOUETTEROUTE, "Pas de \"objectId\" pour ce \"ChouetteRoute\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"ChouetteRoute\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_CHOUETTEROUTE, "L'\"objectId\" () pour ce \"ChouetteRoute\" est invalide.", params);
				}
				chouetteRoute.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorChouetteRoute.hasObjectVersion()) {
			int castorObjectVersion = (int)castorChouetteRoute.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = null;
				if (chouetteRoute.getObjectId() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, chouetteRoute.getObjectId());
				else if (castorChouetteRoute.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorChouetteRoute.getName());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_CHOUETTEROUTE, "La version () \"objectVersion\" du \"ChouetteRoute\" () est invalide.", params);
			}
			else
				chouetteRoute.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorChouetteRoute.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorChouetteRoute.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorChouetteRoute.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorChouetteRoute.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_CHOUETTEROUTE, "La \"creationTime\" () de ce \"ChouetteRoute\" () est invalide.", params);
			}
			else
				chouetteRoute.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorChouetteRoute.getCreatorId();
		params = new String[]{castorChouetteRoute.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"ChouetteRoute\" () est vide.", params, Level.WARN);
			else
				chouetteRoute.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorChouetteRoute.getName();
		params = LoggingManager.getParams(castorChouetteRoute.getObjectId());
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "Le \"name\" de ce \"ChouetteRoute\" () est vide.", params, Level.WARN);
			else
				chouetteRoute.setName(castorName);
		}
		
		// Number optionnel
		String castorNumber = castorChouetteRoute.getNumber();
		params = null;
		if (chouetteRoute.getName() != null)
			params = new String[]{chouetteRoute.getName()};
		else if (chouetteRoute.getObjectId() != null)
			params = new String[]{chouetteRoute.getObjectId()};
		if (castorNumber == null)
			LoggingManager.log(logger, "Pas de \"number\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorNumber = castorNumber.trim();
			if (castorNumber.length() == 0)
				LoggingManager.log(logger, "Le \"number\" pour ce \"ChouetteRoute\" est vide ().", params, Level.WARN);
			else
				chouetteRoute.setNumber(castorNumber);
		}
		
		// PublishedName optionnel
		String castorPublishedName = castorChouetteRoute.getPublishedName();
		if (castorPublishedName == null)
			LoggingManager.log(logger, "Pas de \"publishedName\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorPublishedName = castorPublishedName.trim();
			if (castorPublishedName.length() == 0)
				LoggingManager.log(logger, "Le \"publishedName\" pour ce \"ChouetteRoute\" est vide ().", params, Level.WARN);
			else
				chouetteRoute.setPublishedName(castorPublishedName);
		}
		
		// Direction optionnel
		if (castorChouetteRoute.getDirection() == null)
		{
			LoggingManager.log(logger, "Pas de \"Direction\" pour ce \"ChouetteRoute\".", params, Level.INFO);
		}
		else
		{
			switch (castorChouetteRoute.getDirection()) 
			{
				case A:
					chouetteRoute.setPTDirectionType(PTDirectionType.A);
					break;
				case CLOCKWISE:
					chouetteRoute.setPTDirectionType(PTDirectionType.CLOCKWISE);
					break;
				case COUNTERCLOCKWISE:
					chouetteRoute.setPTDirectionType(PTDirectionType.COUNTERCLOCKWISE);
					break;
				case EAST:
					chouetteRoute.setPTDirectionType(PTDirectionType.EAST);
					break;
				case NORTH:
					chouetteRoute.setPTDirectionType(PTDirectionType.NORTH);
					break;
				case NORTHEAST:
					chouetteRoute.setPTDirectionType(PTDirectionType.NORTHEAST);
					break;
				case NORTHWEST:
					chouetteRoute.setPTDirectionType(PTDirectionType.NORTHWEST);
					break;
				case R:
					chouetteRoute.setPTDirectionType(PTDirectionType.R);
					break;
				case SOUTH:
					chouetteRoute.setPTDirectionType(PTDirectionType.SOUTH);
					break;
				case SOUTHEAST:
					chouetteRoute.setPTDirectionType(PTDirectionType.SOUTHEAST);
					break;
				case SOUTHWEST:
					chouetteRoute.setPTDirectionType(PTDirectionType.SOUTHWEST);
					break;
				case WEST:
					chouetteRoute.setPTDirectionType(PTDirectionType.WEST);
					break;
				default:
					LoggingManager.log(logger, "La \"direction\" de ce \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDDIRECTIONTYPE_CHOUETTEROUTE, "La \"direction\" de ce \"ChouetteRoute\" () est invalide.", params);
			}
		}
		
		// PtLink [1..w]
		String[] castorPtLinkIds = castorChouetteRoute.getPtLinkId();
		if (castorPtLinkIds == null) {
			LoggingManager.log(logger, "La liste des \"ptLink\" de ce \"ChouetteRoute\" () est vide.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NULLPTLINKID_CHOUETTEROUTE, "La liste des \"ptLink\" de ce \"ChouetteRoute\" () est vide.", params);
		}
		else
			if (castorPtLinkIds.length < 1) {
				LoggingManager.log(logger, "La liste des \"ptLink\" de ce \"ChouetteRoute\" () est vide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.NULLPTLINKID_CHOUETTEROUTE, "La liste des \"PtLink\" de ce \"ChouetteRoute\" () est vide.", params);
			}
			else {
				Set<String> ptLinks = new HashSet<String>();
				for (int i = 0; i < castorPtLinkIds.length; i++)
					if ((castorPtLinkIds[i] != null) && (castorPtLinkIds[i].trim().length() > 0))
						if (!ptLinks.add(castorPtLinkIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"ptLink\" pour ce \"ChouetteRoute\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (ptLinks.size() == 0)
					LoggingManager.log(logger, "La liste des \"ptLink\" pour ce \"ChouetteRoute\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String ptLink : ptLinks)
						if (!MainSchemaProducer.isTridentLike(ptLink)) {
							params = new String[]{ptLink, ""};
							if (chouetteRoute.getName() != null)
								params = new String[]{ptLink, chouetteRoute.getName()};
							else if (chouetteRoute.getObjectId() != null)
								params = new String[]{ptLink, chouetteRoute.getObjectId()};
							LoggingManager.log(logger, "Le \"ptLink\" () pour ce \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDPTLINKID_CHOUETTEROUTE, "Le \"ptLink\" () pour ce \"ChouetteRoute\" () est invalide.", params);
						}
					chouetteRoute.setPtLinkIds(castorPtLinkIds);
					//chouetteRoute.setPtLinkIds((String[])ptLinks.toArray(new String[0]));
				}
			}
		
		// JourneyPatternId [1.. w]
		String[] castorJourneyPatternIds = castorChouetteRoute.getJourneyPatternId();
		if (castorJourneyPatternIds == null) {
			LoggingManager.log(logger, "La liste des \"journeyPattern\" de ce \"ChouetteRoute\" () est vide.", params, Level.ERROR);
			validationException.add(TypeInvalidite.NULLJOURNEYPATTERNID_CHOUETTEROUTE, "La liste des \"journeyPattern\" de ce \"ChouetteRoute\" () est vide.", params);
		}
		else
			if (castorJourneyPatternIds.length < 1) {
				LoggingManager.log(logger, "La liste des \"journeyPattern\" de ce \"ChouetteRoute\" () est vide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.NULLJOURNEYPATTERNID_CHOUETTEROUTE, "La liste des \"journeyPattern\" de ce \"ChouetteRoute\" () est vide.", params);
			}
			else {
				Set<String> journeyPatternIds = new HashSet<String>();
				for (int i = 0; i < castorJourneyPatternIds.length; i++)
					if ((castorJourneyPatternIds[i] != null) && (castorJourneyPatternIds[i].trim().length() > 0))
						if (!journeyPatternIds.add(castorJourneyPatternIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"journeyPattern\" pour ce \"ChouetteRoute\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (journeyPatternIds.size() == 0)
					LoggingManager.log(logger, "La liste des \"journeyPattern\" pour ce \"ChouetteRoute\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String journeyPattern : journeyPatternIds)
						if (!MainSchemaProducer.isTridentLike(journeyPattern)) {
							params = new String[]{journeyPattern, ""};
							if (chouetteRoute.getName() != null)
								params = new String[]{journeyPattern, chouetteRoute.getName()};
							else if (chouetteRoute.getObjectId() != null)
								params = new String[]{journeyPattern, chouetteRoute.getObjectId()};
							LoggingManager.log(logger, "Le \"journeyPattern\" () pour ce \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERNID_CHOUETTEROUTE, "Le \"journeyPattern\" () pour ce \"ChouetteRoute\" () est invalide.", params);
						}
					chouetteRoute.setJourneyPatternIds((String[])journeyPatternIds.toArray(new String[0]));
				}
			}
		
		// WayBackRouteId optionnel
		String castorWayBackRouteId = castorChouetteRoute.getWayBackRouteId();
		params = null;
		if (chouetteRoute.getName() != null)
			params = new String[]{chouetteRoute.getName()};
		else if (chouetteRoute.getObjectId() != null)
			params = new String[]{chouetteRoute.getObjectId()};
		if (castorWayBackRouteId == null)
			LoggingManager.log(logger, "Pas de \"WayBackRouteId\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorWayBackRouteId = castorWayBackRouteId.trim();
			if (castorWayBackRouteId.length() == 0)
				LoggingManager.log(logger, "Le \"WayBackRouteId\" pour ce \"ChouetteRoute\" () est vide.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorWayBackRouteId)) {
					params = new String[]{castorWayBackRouteId, ""};
					if (chouetteRoute.getName() != null)
						params = new String[]{castorWayBackRouteId, chouetteRoute.getName()};
					else if (chouetteRoute.getObjectId() != null)
						params = new String[]{castorWayBackRouteId, chouetteRoute.getObjectId()};
					LoggingManager.log(logger, "Le \"wayBackRouteId\" () pour ce \"ChouetteRoute\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDWAYBACKROUTEID_CHOUETTEROUTE, "Le \"wayBackRouteId\" () pour ce \"ChouetteRoute\" () est invalide.", params);
				}
				chouetteRoute.setWayBackRouteId(castorWayBackRouteId);
			}
		}
		
		// Comment  optionnel
		String castorComment = castorChouetteRoute.getComment();
		params = null;
		if (chouetteRoute.getName() != null)
			params = new String[]{chouetteRoute.getName()};
		else if (chouetteRoute.getObjectId() != null)
			params = new String[]{chouetteRoute.getObjectId()};
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"Comment\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"Comment\" pour ce \"ChouetteRoute\" () est vide.", params, Level.WARN);
			else
				chouetteRoute.setComment(castorComment);
		}
		
		// RouteExtension optionnel
		chouette.schema.RouteExtension castorRouteExtension = castorChouetteRoute.getRouteExtension();
		if (castorRouteExtension == null)
			LoggingManager.log(logger, "Pas de \"RouteExtension\" pour ce \"ChouetteRoute\" ().", params, Level.INFO);
		else {
			RouteExtension routeExtension = (new RouteExtensionProducer(validationException)).getASG(castorRouteExtension);
			if (routeExtension == null)
				LoggingManager.log(logger, "Erreur lors de la production du \"RouteExtension\" de ce \"ChouetteRoute\" ().", params, Level.ERROR);
			else {
				chouetteRoute.setRouteExtension(routeExtension);
				routeExtension.setChouetteRoute(chouetteRoute);
			}
		}
		
		return chouetteRoute;
	}
}
