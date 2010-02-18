package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.ChouetteLineDescription;
import fr.certu.chouette.service.validation.ChouetteRoute;
import fr.certu.chouette.service.validation.ITL;
import fr.certu.chouette.service.validation.JourneyPattern;
import fr.certu.chouette.service.validation.Line;
import fr.certu.chouette.service.validation.PtLink;
import fr.certu.chouette.service.validation.StopPoint;
import fr.certu.chouette.service.validation.VehicleJourney;
import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class ChouetteLineDescriptionProducer {
	
	private static final Logger                  logger                  = Logger.getLogger(fr.certu.chouette.service.validation.util.ChouetteLineDescriptionProducer.class);
	private              ValidationException     validationException;
	private              ChouetteLineDescription chouetteLineDescription = null;
	
	ChouetteLineDescriptionProducer(ValidationException validationException) {
		setValidationException(validationException);
	}
	
	void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void setChouetteLineDescription(ChouetteLineDescription chouetteLineDescription) {
		this.chouetteLineDescription = chouetteLineDescription;
	}
	
	ChouetteLineDescription getChouetteLineDescription() {
		return chouetteLineDescription;
	}
	
	ChouetteLineDescription getASG(chouette.schema.ChouetteLineDescription castorChouetteLineDescription) {
		chouetteLineDescription = new ChouetteLineDescription();
		String[] params = null;
		LoggingManager.log(logger, "DEBUT DE LA GENERATION DU CHOUETTELINEDESCRIPTION.", Level.DEBUG);
		
		// Line obligatoire
		chouette.schema.Line castorLine = castorChouetteLineDescription.getLine();
		if (castorLine == null) {
			LoggingManager.log(logger, "L'objet de type \"Line\" est null.", Level.FATAL);
			validationException.add(TypeInvalidite.NULL_LINE, "L'objet de type \"Line\" est null.");
			throw validationException;
		}
		else {
			Line line = (new LineProducer(validationException)).getASG(castorLine);
			if (line == null) {
				params = null;
				if (castorLine.getObjectId() != null)
					params = new String[]{castorLine.getObjectId()};
				LoggingManager.log(logger, "Une erreure s'est produite lors de la construction d'un objet \"Line\" ().", params, Level.ERROR);
			}
			else {
				chouetteLineDescription.setLine(line);
				line.setChouetteLineDescription(chouetteLineDescription);
			}
		}
		
		// ChouetteRoute [1..w]
		if (castorChouetteLineDescription.getChouetteRoute() == null) {
			LoggingManager.log(logger, "L'objet de type \"ChouetteRoute\" est null.", Level.ERROR);
			validationException.add(TypeInvalidite.NULL_CHOUETTEROUTE, "L'objet de type \"ChouetteLine\" est null.");
		}
		else {
			int numberOfChouetteRoutes = castorChouetteLineDescription.getChouetteRouteCount();
			if (numberOfChouetteRoutes < 1) {
				LoggingManager.log(logger, "L'objet de type \"ChouetteRoute\" est null.", Level.ERROR);
				validationException.add(TypeInvalidite.NULL_CHOUETTEROUTE, "L'objet de type \"ChouetteLine\" est null.");
			}
			else
				for (int i = 0; i < numberOfChouetteRoutes; i++) {
					chouette.schema.ChouetteRoute castorChouetteRoute = castorChouetteLineDescription.getChouetteRoute(i);
					if (castorChouetteRoute == null) {
						LoggingManager.log(logger, "L'objet de type \"ChouetteRoute\" est null.", Level.ERROR);
						validationException.add(TypeInvalidite.NULL_CHOUETTEROUTE, "L'objet de type \"ChouetteLine\" est null.");
					}
					else {
						ChouetteRoute chouetteRoute = (new ChouetteRouteProducer(validationException)).getASG(castorChouetteRoute);
						if (chouetteRoute == null)
							LoggingManager.log(logger, "Error de construction de la \"ChouetteRoute\".", Level.ERROR);
						else {
							chouetteLineDescription.addChouetteRoute(chouetteRoute);
							chouetteRoute.setChouetteLineDescription(chouetteLineDescription);
						}
					}
				}
		}
		
		// StopPoint [2..w]
		if (castorChouetteLineDescription.getStopPoint() == null) {
			LoggingManager.log(logger, "L'objet de type \"StopPoint\" est null.", Level.ERROR);
			validationException.add(TypeInvalidite.NULL_STOPPOINT, "L'objet de type \"StopPoint\" est null.");
		}
		else {
			int numberOfStopPoints = castorChouetteLineDescription.getStopPointCount();
			if (numberOfStopPoints < 2) {
				LoggingManager.log(logger, "L'objet de type \"StopPoint\" est null ou singleton.", Level.ERROR);
				validationException.add(TypeInvalidite.NULL_STOPPOINT, "L'objet de type \"StopPoint\" est null ou singleton.");
			}
			else
				for (int i = 0; i < numberOfStopPoints; i++) {
					chouette.schema.StopPoint castorStopPoint = castorChouetteLineDescription.getStopPoint(i);
					if (castorStopPoint == null) {
						LoggingManager.log(logger, "L'objet de type \"StopPoint\" est null.", Level.ERROR);
						validationException.add(TypeInvalidite.NULL_STOPPOINT, "L'objet de type \"StopPoint\" est null.");
					}
					else {
						StopPoint stopPoint = (new StopPointProducer(validationException)).getASG(castorStopPoint);
						if (stopPoint == null)
							LoggingManager.log(logger, "Error de construction du \"StopPoint\".", Level.ERROR);
						else {
							chouetteLineDescription.addStopPoint(stopPoint);
							stopPoint.setChouetteLineDescription(chouetteLineDescription);
						}
					}
				}
		}
		
		// ITL [0..w]
		int numberOfITLs = castorChouetteLineDescription.getITLCount();
		if (numberOfITLs <= 0)
			LoggingManager.log(logger, "L'objet de type \"ITL\" est null.", Level.INFO);
		for (int i = 0; i < numberOfITLs; i++) {
			chouette.schema.ITL castorITL = castorChouetteLineDescription.getITL(i);
			if (castorITL == null)
				LoggingManager.log(logger, "L'objet de type \"ITL\" est null.", Level.INFO);
			else {
				ITL iTL = (new ITLProducer(validationException)).getASG(castorITL);
				if (iTL == null)
					LoggingManager.log(logger, "Error de construction de l'\"ITL\".", Level.ERROR);
				else {
					chouetteLineDescription.addITL(iTL);
					iTL.setChouetteLineDescription(chouetteLineDescription);
				}
			}
		}
		
		// PtLink [1..w]
		if (castorChouetteLineDescription.getPtLink() == null) {
			LoggingManager.log(logger, "L'objet de type \"PtLink\" est null.", Level.ERROR);
			validationException.add(TypeInvalidite.NULL_PTLINK, "L'objet de type \"PtLink\" est null.");
		}
		else {
			int numberOfPtLinks = castorChouetteLineDescription.getPtLinkCount();
			if (numberOfPtLinks < 1) {
				LoggingManager.log(logger, "L'objet de type \"PtLink\" est null.", Level.ERROR);
				validationException.add(TypeInvalidite.NULL_PTLINK, "L'objet de type \"PtLink\" est null.");
			}
			for (int i = 0; i < numberOfPtLinks; i++) {
				chouette.schema.PtLink castorPtLink = castorChouetteLineDescription.getPtLink(i);
				if (castorPtLink == null) {
					LoggingManager.log(logger, "L'objet de type \"PtLink\" est null.", Level.ERROR);
					validationException.add(TypeInvalidite.NULL_PTLINK, "L'objet de type \"PtLink\" est null.");
				}
				else {
					PtLink ptLink = (new PtLinkProducer(validationException)).getASG(castorPtLink);
					if (ptLink == null)
						LoggingManager.log(logger, "Error de construction du \"PtLink\".", Level.ERROR);
					else {
						chouetteLineDescription.addPtLink(ptLink);
						ptLink.setChouetteLineDescription(chouetteLineDescription);
					}
				}
			}
		}
		
		// JourneyPattern [0..w]
		int numberOfJourneyPatterns = castorChouetteLineDescription.getJourneyPatternCount();
		if (numberOfJourneyPatterns <= 0)
			LoggingManager.log(logger, "L'objet de type \"JourneyPattern\" est null.", Level.INFO);
		for (int i = 0; i < numberOfJourneyPatterns; i++) {
			chouette.schema.JourneyPattern castorJourneyPattern = castorChouetteLineDescription.getJourneyPattern(i);
			if (castorJourneyPattern == null)
				LoggingManager.log(logger, "L'objet de type \"JourneyPattern\" est null.", Level.INFO);
			else {
				JourneyPattern journeyPattern = (new JourneyPatternProducer(validationException)).getASG(castorJourneyPattern);
				if (journeyPattern == null)
					LoggingManager.log(logger, "Error de construction du \"JourneyPattern\".", Level.ERROR);
				else {
					chouetteLineDescription.addJourneyPattern(journeyPattern);
					journeyPattern.setChouetteLineDescription(chouetteLineDescription);
				}
			}
		}
		
		// VehicleJourney [0..w]
		int numberOfVehicleJourneys = castorChouetteLineDescription.getVehicleJourneyCount();
		if (numberOfVehicleJourneys <= 0)
			LoggingManager.log(logger, "L'objet de type \"VehicleJourney\" est null.", Level.INFO);
		for (int i = 0; i < numberOfVehicleJourneys; i++) {
			chouette.schema.VehicleJourney castorVehicleJourney = castorChouetteLineDescription.getVehicleJourney(i);
			if (castorVehicleJourney == null)
				LoggingManager.log(logger, "L'objet de type \"VehicleJourney\" est null.", Level.INFO);
			else {
				VehicleJourney vehicleJourney = (new VehicleJourneyProducer(validationException)).getASG(castorVehicleJourney);
				if (vehicleJourney == null)
					LoggingManager.log(logger, "Error de construction du \"VehicleJourney\".", Level.ERROR);
				else {
					chouetteLineDescription.addVehicleJourney(vehicleJourney);
					vehicleJourney.setChouetteLineDescription(chouetteLineDescription);
				}
			}
		}
		LoggingManager.log(logger, "FIN DE LA GENERATION DU CHOUETTELINEDESCRIPTION.", Level.DEBUG);
		
		return chouetteLineDescription;
	}
}
