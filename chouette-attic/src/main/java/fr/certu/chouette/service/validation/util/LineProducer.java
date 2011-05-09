package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.Line;
import fr.certu.chouette.service.validation.Registration;
import fr.certu.chouette.service.validation.TransportMode;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class LineProducer {
	
	private static final Logger              logger              = Logger.getLogger(fr.certu.chouette.service.validation.util.LineProducer.class);
	private              ValidationException validationException;
	private              Line                line                = null;
	
	LineProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setLine(Line line) {
		this.line = line;
	}
	
	Line getLine() {
		return line;
	}
	
	Line getASG(chouette.schema.Line castorLine) {
		line = new Line();
		String[] params = null;
		
		// ObjectId obligatoire
		String castorObjectId = castorLine.getObjectId();
		params = new String[]{castorLine.getName()};
		if (castorObjectId == null) {
			LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Line\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOOBJECTID_LINE, "Pas de \"objectId\" pour ce \"Line\" ().", params);
		}
		else {
			castorObjectId = castorObjectId.trim();
			if (castorObjectId.length() == 0) {
				LoggingManager.log(logger, "Pas de \"objectId\" pour ce \"Line\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOOBJECTID_LINE, "Pas de \"objectId\" pour ce \"Line\" ().", params);
			}
			else {
				if (!MainSchemaProducer.isTridentLike(castorObjectId)) {
					params = LoggingManager.getParams(castorObjectId);
					LoggingManager.log(logger, "L'\"objectId\" () pour ce \"Line\" est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDOBJECTID_LINE, "L'\"objectId\" () pour ce \"Line\" est invalide.", params);
				}
				line.setObjectId(castorObjectId);		
			}
		}
		
		// ObjectVersion optionnel
		if (castorLine.hasObjectVersion()) {
			int castorObjectVersion = (int)castorLine.getObjectVersion();
			if (castorObjectVersion < 1) {
				params = null;
				if (line.getObjectId() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, line.getObjectId());
				else if (castorLine.getName() != null)
					params = LoggingManager.getParams(""+castorObjectVersion, castorLine.getName());
				LoggingManager.log(logger, "La version () \"objectVersion\" du \"Line\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDOBJECTVERSION_LINE, "La version () \"objectVersion\" du \"Line\" () est invalide.", params);
			}
			else
				line.setObjectVersion(castorObjectVersion);
		}
		else {
			params = LoggingManager.getParams(castorLine.getObjectId());
			LoggingManager.log(logger, "Pas d'\"objectVersion\" pour ce \"Line\" ().", params, Level.INFO);
		}
		
		// CreationTime optionnel
		java.util.Date castorCreationTime = castorLine.getCreationTime();
		if (castorCreationTime == null) {
			params = LoggingManager.getParams(castorLine.getObjectId());
			LoggingManager.log(logger, "Pas de \"creationTime\" pour ce \"Line\" ().", params, Level.INFO);
		}
		else
			if (castorCreationTime.after(new java.util.Date(System.currentTimeMillis()))) {
				params = LoggingManager.getParams(castorCreationTime.toString(), castorLine.getObjectId());
				LoggingManager.log(logger, "La \"creationTime\" () de ce \"Line\" () est invalide.", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDCREATIONTIME_LINE, "La \"creationTime\" () de ce \"Line\" () est invalide.", params);
			}
			else
				line.setCreationTime(castorCreationTime);
		
		// CreatorId optionnel
		String castorCreatorId = castorLine.getCreatorId();
		params = new String[]{castorLine.getObjectId()};			
		if (castorCreatorId == null)
			LoggingManager.log(logger, "Pas de \"creatorId\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorCreatorId = castorCreatorId.trim();
			if (castorCreatorId.length() == 0)
				LoggingManager.log(logger, "L'objet de type \"creatorId\" dans ce \"Line\" () est vide.", params, Level.WARN);
			else
				line.setCreatorId(castorCreatorId);
		}
		
		// Name optionnel
		String castorName = castorLine.getName();
		params = LoggingManager.getParams(castorLine.getObjectId());
		if (castorName == null)
			LoggingManager.log(logger, "Pas de \"name\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorName = castorName.trim();
			if (castorName.length() == 0)
				LoggingManager.log(logger, "Le \"name\" de ce \"Line\" () est vide.", params, Level.WARN);
			else
				line.setName(castorName);
		}
		
		// Number optionnel
		String castorNumber = castorLine.getNumber();
		params = null;
		if (line.getName() != null)
			params = new String[]{line.getName()};
		else if (line.getObjectId() != null)
			params = new String[]{line.getObjectId()};
		if (castorNumber == null)
			LoggingManager.log(logger, "Pas de \"number\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorNumber = castorNumber.trim();
			if (castorNumber.length() == 0)
				LoggingManager.log(logger, "Le \"number\" pour ce \"Line\" est vide ().", params, Level.WARN);
			else
				line.setNumber(castorNumber);
		}
		
		// PublishedName optionnel
		String castorPublishedName = castorLine.getPublishedName();
		if (castorPublishedName == null)
			LoggingManager.log(logger, "Pas de \"publishedName\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorPublishedName = castorPublishedName.trim();
			if (castorPublishedName.length() == 0)
				LoggingManager.log(logger, "Le \"publishedName\" pour ce \"Line\" est vide ().", params, Level.WARN);
			else
				line.setPublishedName(castorPublishedName);
		}
		
		// TransportModeName optionnel
		if (castorLine.getTransportModeName() == null)
		{
			LoggingManager.log(logger, "Pas de \"transportModeName\" pour ce \"Line\" ().", params, Level.INFO);
		}
		else
		{
			switch (castorLine.getTransportModeName()) 
			{
				case AIR:
					line.setTransportMode(TransportMode.AIR);
				break;
				case BICYCLE:
					line.setTransportMode(TransportMode.BICYCLE);
					break;
				case BUS:
					line.setTransportMode(TransportMode.BUS);
					break;
				case COACH:
					line.setTransportMode(TransportMode.COACH);
					break;
				case FERRY:
					line.setTransportMode(TransportMode.FERRY);
					break;
				case LOCALTRAIN:
					line.setTransportMode(TransportMode.LOCALTRAIN);
					break;
				case LONGDISTANCETRAIN:
					line.setTransportMode(TransportMode.LONGDISTANCETRAIN);
					break;
				case METRO:
					line.setTransportMode(TransportMode.METRO);
					break;
				case OTHER:
					line.setTransportMode(TransportMode.OTHER);
					break;
				case PRIVATEVEHICLE:
					line.setTransportMode(TransportMode.PRIVATEVEHICLE);
					break;
				case RAPIDTRANSIT:
					line.setTransportMode(TransportMode.RAPIDTRANSIT);
					break;
				case SHUTTLE:
					line.setTransportMode(TransportMode.SHUTTLE);
					break;
				case TAXI:
					line.setTransportMode(TransportMode.TAXI);
					break;
				case TRAIN:
					line.setTransportMode(TransportMode.TRAIN);
					break;
				case TRAMWAY:
					line.setTransportMode(TransportMode.TRAMWAY);
					break;
				case TROLLEYBUS:
					line.setTransportMode(TransportMode.TROLLEYBUS);
					break;
				case VAL:
					line.setTransportMode(TransportMode.VAL);
					break;
				case WALK:
					line.setTransportMode(TransportMode.WALK);
					break;
				case WATERBORNE:
					line.setTransportMode(TransportMode.WATERBORNE);
					break;
				default:
					LoggingManager.log(logger, "Le \"transportModeName\" pour ce \"Line\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDTRANSPORTMODENAMETYPE_LINE, "Le \"transportModeName\" pour ce \"Line\" () est invalide.", params);
			}
		}
		// LineEnd [0..w]
		String[] castorLineEnds = castorLine.getLineEnd();
		if (castorLineEnds == null)
			LoggingManager.log(logger, "Pas de \"lineEnd\" pour cette \"Line\" ().", params, Level.INFO);
		else
			if (castorLineEnds.length == 0)
				LoggingManager.log(logger, "La liste des \"lineEnd\" pour ce \"Line\" () est vide.", params, Level.WARN);
			else {
				Set<String> lineEnds = new HashSet<String>();
				for (int i = 0; i < castorLineEnds.length; i++)
					if ((castorLineEnds[i] != null) && (castorLineEnds[i].trim().length() > 0))
						if (!lineEnds.add(castorLineEnds[i].trim()))
							LoggingManager.log(logger, "La liste des \"lineEnd\" pour ce \"Line\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (lineEnds.size() == 0)
					LoggingManager.log(logger, "La liste des \"lineEnd\" pour ce \"Line\" () ne contient que des \"objectsId\" vide.", params, Level.WARN);
				else {
					for (String lineEnd : lineEnds)
						if (!MainSchemaProducer.isTridentLike(lineEnd)) {
							params = new String[]{lineEnd, ""};
							if (line.getName() != null)
								params = new String[]{lineEnd, line.getName()};
							else if (line.getObjectId() != null)
								params = new String[]{lineEnd, line.getObjectId()};
							LoggingManager.log(logger, "Le \"lineEnd\" () pour ce \"Line\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDLINEENDID_LINE, "Le \"lineEnd\" () pour ce \"Line\" () est invalide.", params);
						}
					line.setLineEnds((String[])lineEnds.toArray(new String[0]));
				}
			}
		
		// RouteId [1..w]
		String[] castorRouteIds = castorLine.getRouteId();
		params = null;
		if (line.getName() != null)
			params = new String[]{line.getName()};
		else if (line.getObjectId() != null)
			params = new String[]{line.getObjectId()};
		if (castorRouteIds == null) {
			LoggingManager.log(logger, "Pas de \"routeId\" pour cette \"Line\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.NOROUTEID_LINE, "Pas de \"routeId\" pour cette \"Line\" ().", params);
		}
		else
			if (castorRouteIds.length < 1) {
				LoggingManager.log(logger, "Pas de \"routeId\" pour cette \"Line\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.NOROUTEID_LINE, "Pas de \"routeId\" pour cette \"Line\" ().", params);
			}
			else {
				Set<String> routeIds = new HashSet<String>();
				for (int i = 0; i < castorRouteIds.length; i++)
					if ((castorRouteIds[i] != null) && (castorRouteIds[i].trim().length() > 0))
						if (!routeIds.add(castorRouteIds[i].trim()))
							LoggingManager.log(logger, "La liste des \"routeId\" pour ce \"Line\" () contient des \"objectsId\" en double.", params, Level.WARN);
				if (routeIds.size() == 0) {
					LoggingManager.log(logger, "La liste des \"routeId\" pour ce \"Line\" () ne contient que des \"objectsId\" vide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.NOROUTEID_LINE, "Pas de \"routeId\" pour ce \"Line\" ().", params);
				}
				else {
					for (String routeId : routeIds)
						if (!MainSchemaProducer.isTridentLike(routeId)) {
							params = new String[]{routeId, ""};
							if (castorLine.getName() != null)
								params = new String[]{routeId, castorLine.getName()};
							else if (castorLine.getObjectId() != null)
								params = new String[]{routeId, castorLine.getObjectId()};
							LoggingManager.log(logger, "Le \"routeId\" () pour ce \"Line\" () est invalide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDROUTEID_LINE, "Le \"routeId\" () pour ce \"Line\" () est invalide.", params);
						}
					line.setRouteIds((String[])routeIds.toArray(new String[0]));
				}
			}
		
		// Registration optionnel
		chouette.schema.Registration castorRegistration = castorLine.getRegistration();
		params = null;
		if (line.getName() != null)
			params = new String[]{line.getName()};
		else if (line.getObjectId() != null)
			params = new String[]{line.getObjectId()};
		if (castorRegistration == null)
			LoggingManager.log(logger, "Pas de \"registration\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			Registration registration = (new RegistrationProducer(validationException)).getASG(castorRegistration);
			if (registration == null)
				LoggingManager.log(logger, "Error lors de la construction de la \"registration\" pour ce \"Line\" ().", params, Level.ERROR);
			else {
				registration.setLine(line);
				line.setRegistration(registration);
			}
		}
		
		// PtNetworkShortcut optionnel
		String castorPtNetworkIdShortcut = castorLine.getPtNetworkIdShortcut();
		if (castorPtNetworkIdShortcut == null)
			LoggingManager.log(logger, "Pas de \"ptNetworkShortCut\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorPtNetworkIdShortcut = castorPtNetworkIdShortcut.trim();
			if (castorPtNetworkIdShortcut.length() == 0)
				LoggingManager.log(logger, "Le \"ptNetworkShortCut\" pour ce \"Line\" () est null.", params, Level.WARN);
			else {
				if (!MainSchemaProducer.isTridentLike(castorPtNetworkIdShortcut)) {
					params = new String[]{castorPtNetworkIdShortcut, ""};
					if (castorLine.getName() != null)
						params = new String[]{castorPtNetworkIdShortcut, castorLine.getName()};
					else if (castorLine.getObjectId() != null)
						params = new String[]{castorPtNetworkIdShortcut, castorLine.getObjectId()};
					LoggingManager.log(logger, "Le \"ptNetworkIdShortcut\" () pour ce \"Line\" () est invalide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUTID_LINE, "Le \"ptNetworkIdShortcut\" () pour ce \"Line\" () est invalide.", params);
				}
				else
					line.setPtNetworkIdShortcut(castorPtNetworkIdShortcut);
			}
		}
		
		// Comment optionnel
		String castorComment = castorLine.getComment();
		if (castorComment == null)
			LoggingManager.log(logger, "Pas de \"comment\" pour ce \"Line\" ().", params, Level.INFO);
		else {
			castorComment = castorComment.trim();
			if (castorComment.length() == 0)
				LoggingManager.log(logger, "Le \"comment\" de ce \"Line\" () est null.", params, Level.WARN);
			else
				line.setComment(castorComment);
		}
		
		return line;
	}
}
