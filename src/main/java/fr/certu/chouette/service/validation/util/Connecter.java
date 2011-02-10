package fr.certu.chouette.service.validation.util;

import fr.certu.chouette.service.validation.commun.LoggingManager;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;
import fr.certu.chouette.service.validation.AreaCentroid;
import fr.certu.chouette.service.validation.ChouetteArea;
import fr.certu.chouette.service.validation.ChouetteAreaType;
import fr.certu.chouette.service.validation.ChouetteLineDescription;
import fr.certu.chouette.service.validation.ChouettePTNetwork;
import fr.certu.chouette.service.validation.ChouetteRoute;
import fr.certu.chouette.service.validation.Company;
import fr.certu.chouette.service.validation.ConnectionLink;
import fr.certu.chouette.service.validation.GroupOfLine;
import fr.certu.chouette.service.validation.JourneyPattern;
import fr.certu.chouette.service.validation.ITL;
import fr.certu.chouette.service.validation.Line;
import fr.certu.chouette.service.validation.PTNetwork;
import fr.certu.chouette.service.validation.PtLink;
import fr.certu.chouette.service.validation.StopArea;
import fr.certu.chouette.service.validation.StopPoint;
import fr.certu.chouette.service.validation.TimeSlot;
import fr.certu.chouette.service.validation.Timetable;
import fr.certu.chouette.service.validation.VehicleJourney;
import fr.certu.chouette.service.validation.VehicleJourneyAtStop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class Connecter {
	
    private static final Logger 			logger 				= Logger.getLogger(fr.certu.chouette.service.validation.util.Connecter.class);
    private String[]                        params              = null;
	private Map<String, StopArea> 			stopAreaById 		= new Hashtable<String, StopArea>();
	private Map<String, AreaCentroid>		areaCentroidById	= new Hashtable<String, AreaCentroid>();
	private Map<String, StopPoint> 			stopPointById 		= new Hashtable<String, StopPoint>();
	private Map<String, VehicleJourney> 	vehicleJourneyById 	= new Hashtable<String, VehicleJourney>();
	private Map<String, JourneyPattern> 	journeyPatternById 	= new Hashtable<String, JourneyPattern>();
	private Map<String, PtLink> 			ptLinkById 			= new Hashtable<String, PtLink>();
	private Map<String, ChouetteRoute> 		chouetteRouteById 	= new Hashtable<String, ChouetteRoute>();
	private Map<String, TimeSlot>	 		timeSlotById	 	= new Hashtable<String, TimeSlot>();
    private ValidationException 			validationException;
     
    Connecter(ValidationException validationException) {
    	setValidationException(validationException);
    }
    
    void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	ValidationException getValidationException() {
		return validationException;
	}
	
	void connectElements(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork == null) {
			LoggingManager.log(logger, "L'objet de type \"ChouettePTNetwork\" est null.", Level.ERROR);
			validationException.add(TypeInvalidite.NULL_CHOUETTEPTNETWORK, "L'objet de type \"ChouettePTNetwork\" est null.");
			throw validationException;
		}
		constructDictionaries(chouettePTNetwork);
		connectLineIdsInPTNetworkToLine(chouettePTNetwork);
		connectLineIdsInGroupOfLineToLine(chouettePTNetwork);
    	connectStopPointsLineIdShortCutToLine(chouettePTNetwork);
    	connectStopPointsPTNetworkIdShortCutToPTNetwork(chouettePTNetwork);
		connectConnectionLinksToStops(chouettePTNetwork);
		connectTimetablesToVehicleJourneys(chouettePTNetwork);
		connectStopAreasToSubStops(chouettePTNetwork);
		connectAreaCentroidsToStopAreas(chouettePTNetwork);
		connectChouetteRoutesToJourneyPatternsAndPtLinks(chouettePTNetwork);
		connectLinesToChouetteRoutes(chouettePTNetwork);
		connectPtLinksToStopPoints(chouettePTNetwork);
		connectJourneyPatterns(chouettePTNetwork);
		connectVehicleJourneys(chouettePTNetwork);
		connectITLs(chouettePTNetwork);
		verifierCoherenceMissionItineraire(chouettePTNetwork);
		verifierCoherenceCourseMission(chouettePTNetwork);
	}
	
	private void constructDictionaries(ChouettePTNetwork chouettePTNetwork) {
		constructStopAreasDictionary(chouettePTNetwork);
		constructAreaCentroidsDictionary(chouettePTNetwork);
		constructStopPointsDictionary(chouettePTNetwork);
		constructVehicleJourneysDictionary(chouettePTNetwork);
		constructChouetteRoutesDictionary(chouettePTNetwork);
		constructJourneyPatternsDictionary(chouettePTNetwork);
		constructPtLinksDictionary(chouettePTNetwork);
		constructTimeSlotsDictionary(chouettePTNetwork);
	}
	
	private void constructStopAreasDictionary(ChouettePTNetwork chouettePTNetwork) {
		ChouetteArea chouetteArea = chouettePTNetwork.getChouetteArea();
		if (chouetteArea == null)
			return;
		List<StopArea> stopAreas = chouetteArea.getStopAreas();
		if (stopAreas == null)
			return;
		for (int i = 0; i < stopAreas.size(); i++) {
			StopArea stopArea = stopAreas.get(i);
			if (stopArea == null)
				continue;
			String objectId = stopArea.getObjectId();
			if (objectId == null)
				continue;
			objectId = objectId.trim();
			if (objectId.length() == 0)
				continue;
			if (stopAreaById.get(objectId) != null) {
				params = new String[]{objectId};
				LoggingManager.log(logger, "Il ne peut y avoir deux \"StopArea\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.KNOWNOBJECTID_STOPAREA, "Il ne peut y avoir deux \"StopArea\" avec le m�me \"ObjectId\" ().", params);
				continue;
			}
			stopAreaById.put(objectId, stopArea);
        }
	}
	
    private void constructAreaCentroidsDictionary(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteArea chouetteArea = chouettePTNetwork.getChouetteArea();
        if (chouetteArea == null)
        	return;
        List<AreaCentroid> areaCentroids = chouetteArea.getAreaCentroids();
        if (areaCentroids == null)
        	return;
        for (int i = 0; i < areaCentroids.size(); i++) {
        	AreaCentroid areaCentroid = areaCentroids.get(i);
        	if (areaCentroid == null)
        		continue;
        	String objectId = areaCentroid.getObjectId();
        	if (objectId == null)
        		continue;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (areaCentroidById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"AreaCentroid\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_AREACENTROID, "Il ne peut y avoir deux \"AreaCentroid\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	areaCentroidById.put(objectId, areaCentroid);
        }
    }
    
    private void constructStopPointsDictionary(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	List<StopPoint> stopPoints = chouetteLineDescription.getStopPoints();
    	if (stopPoints == null)
    		return;
    	for (int i = 0; i < stopPoints.size(); i++) {
    		StopPoint stopPoint = stopPoints.get(i);
    		if (stopPoint == null)
    			continue;
    		String objectId = stopPoint.getObjectId();
    		if (objectId == null)
    			continue;
    		objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (stopPointById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"StopPoint\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_STOPPOINT, "Il ne peut y avoir deux \"StopPoint\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	stopPointById.put(objectId, stopPoint);
        }
    }
    
    private void constructVehicleJourneysDictionary(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	List<VehicleJourney> vehicleJourneys = chouetteLineDescription.getVehicleJourneys();
    	if (vehicleJourneys == null)
    		return;
        for (int i = 0; i < vehicleJourneys.size(); i++) {
        	VehicleJourney vehicleJourney = vehicleJourneys.get(i);
        	if (vehicleJourney == null)
        		continue;
        	String objectId = vehicleJourney.getObjectId();
        	if (objectId == null)
        		continue;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (vehicleJourneyById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"VehicleJourney\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_VEHICLEJOURNEY, "Il ne peut y avoir deux \"VehicleJourney\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	vehicleJourneyById.put(objectId, vehicleJourney);
        }
    }
    
    private void constructChouetteRoutesDictionary(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	List<ChouetteRoute> chouetteRoutes = chouetteLineDescription.getChouetteRoutes();
    	if (chouetteRoutes == null)
    		return;
    	for (int i = 0; i < chouetteRoutes.size(); i++) {
    		ChouetteRoute chouetteRoute = chouetteRoutes.get(i);
        	if (chouetteRoute == null)
        		continue;
        	String objectId = chouetteRoute.getObjectId();
        	if (objectId == null)
    			continue;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (chouetteRouteById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"ChouetteRoute\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_CHOUETTEROUTE, "Il ne peut y avoir deux \"ChouetteRoute\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	chouetteRouteById.put(objectId, chouetteRoute);
        }
    }
    
    private void constructJourneyPatternsDictionary(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
        if (chouetteLineDescription == null)
        	return;
        List<JourneyPattern> journeyPatterns = chouetteLineDescription.getJourneyPatterns();
        if (journeyPatterns == null)
        	return;
        for (int i = 0; i < journeyPatterns.size(); i++) {
        	JourneyPattern journeyPattern = journeyPatterns.get(i);
        	if (journeyPattern == null)
        		continue;
        	String objectId = journeyPattern.getObjectId();
        	if (objectId == null)
        		continue;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (journeyPatternById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"JourneyPattern\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_JOURNEYPATTERN, "Il ne peut y avoir deux \"JourneyPattern\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	journeyPatternById.put(objectId, journeyPattern);
        }
    }
    
    private void constructPtLinksDictionary(ChouettePTNetwork chouettePTNetwork) {
        ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
        if (chouetteLineDescription == null)
        	return;
        List<PtLink> ptLinks = chouetteLineDescription.getPtLinks();
        if (ptLinks == null)
        	return;
        for (int i = 0; i < ptLinks.size(); i++) {
        	PtLink ptLink = ptLinks.get(i);
        	if (ptLink == null)
        		continue;
        	String objectId = ptLink.getObjectId();
        	if (objectId == null)
        		continue;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		continue;
        	if (ptLinkById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"PtLink\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_PTLINK, "Il ne peut y avoir deux \"PtLink\" avec le m�me \"ObjectId\" ().", params);
        		continue;
        	}
        	ptLinkById.put(objectId, ptLink);
        }
    }
    
    private void constructTimeSlotsDictionary(ChouettePTNetwork chouettePTNetwork) {
    	List<TimeSlot> timeSlots = chouettePTNetwork.getTimeSlots();
    	if (timeSlots == null)
    		return;
    	for (int i = 0; i < timeSlots.size(); i++) {
    		TimeSlot timeSlot = timeSlots.get(i);
    		if (timeSlot == null)
    			continue;
    		String objectId = timeSlot.getObjectId();
    		if (objectId == null)
    			continue;
    		objectId = objectId.trim();
    		if (objectId.length() == 0)
    			continue;
    		if (timeSlotById.get(objectId) != null) {
        		params = new String[]{objectId};
        		LoggingManager.log(logger, "Il ne peut y avoir deux \"TimeSlot\" avec le m�me \"ObjectId\" ().", params, Level.ERROR);
        		validationException.add(TypeInvalidite.KNOWNOBJECTID_TIMESLOT, "Il ne peut y avoir deux \"TimeSlot\" avec le m�me \"ObjectId\" ().", params);
        		continue;
    		}
    		timeSlotById.put(objectId, timeSlot);
    	}
    }
    
    private void connectLineIdsInPTNetworkToLine(ChouettePTNetwork chouettePTNetwork) {
    	PTNetwork pTNetwork = chouettePTNetwork.getPTNetwork();
    	if (pTNetwork == null)
    		return;
    	String[] lineIds = pTNetwork.getLineIds();
    	if (lineIds == null)
    		return;
    	if (lineIds.length == 0)
    		return;
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	Line line = chouetteLineDescription.getLine();
    	if (line == null)
    		return;
    	String objectId = line.getObjectId();
    	if (objectId == null)
    		return;
    	objectId = objectId.trim();
    	if (objectId.length() == 0)
    		return;
    	for (int i = 0; i < lineIds.length; i++)
    		if (lineIds[i] != null)
    			if (lineIds[i].trim().equals(objectId))
    				return;
    	params = new String[]{objectId};
    	LoggingManager.log(logger, "L'\"objectId\" () de la \"Line\" n'est pas dans la liste \"lineId\" du \"PTNetwrk\".", params, Level.ERROR);
    	validationException.add(TypeInvalidite.NOLINEIDREF_PTNETWORK, "L'\"objectId\" () de la \"Line\" n'est pas dans la liste \"lineId\" du \"PTNetwrk\".", params);
    }
    
    private void connectLineIdsInGroupOfLineToLine(ChouettePTNetwork chouettePTNetwork) {
        List<GroupOfLine> groupOfLines = chouettePTNetwork.getGroupOfLines();
        if (groupOfLines == null)
        	return;
        int groupOfLinesCount = chouettePTNetwork.getGroupOfLineCount();
        for (int i = 0; i < groupOfLinesCount; i++) {
        	String[] lineIds = chouettePTNetwork.getGroupOfLine(i).getLineIds();
        	if (lineIds == null)
        		return;
        	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
        	if (chouetteLineDescription == null)
        		return;
        	Line line = chouetteLineDescription.getLine();
        	if (line == null)
        		return;
        	String objectId = line.getObjectId();
        	if (objectId == null)
        		return;
        	objectId = objectId.trim();
        	if (objectId.length() == 0)
        		return;
        	for (int j = 0; j < lineIds.length; j++)
        		if (lineIds[j] != null)
        			if (lineIds[j].trim().equals(objectId))
        				return;
        	params = new String[]{objectId};
        	LoggingManager.log(logger, "L'\"ObjectId\" () de la \"Line\" n'est pas dans la liste \"LineId\" du \"GroupOfLine\".", params, Level.ERROR);
        	validationException.add(TypeInvalidite.NOLINEIDREF_GROUPOFLINE, "L'\"ObjectId\" () de la \"Line\" n'est pas dans la liste \"LineId\" du \"GroupOfLine\".", params);
        }
    }
    
    private void connectStopPointsLineIdShortCutToLine(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	Line line = chouetteLineDescription.getLine();
    	if (line == null)
    		return;
    	String objectId = line.getObjectId();
    	if (objectId == null)
    		return;
    	objectId = objectId.trim();
    	if (objectId.length() == 0)
    		return;
    	List<StopPoint> stopPoints = chouetteLineDescription.getStopPoints();
    	if (stopPoints == null)
    		return;
    	for (int i = 0; i < chouetteLineDescription.getStopPointCount(); i++) {
    		StopPoint stopPoint = chouetteLineDescription.getStopPoint(i);
    		if (stopPoint == null)
    			continue;
    		String lineIdShortcut = stopPoint.getLineIdShortcut();
    		if (lineIdShortcut == null)
    			continue;
    		lineIdShortcut = lineIdShortcut.trim();
    		if (lineIdShortcut.length() == 0)
    			continue;
    		params = new String[] {lineIdShortcut};
    		if (lineIdShortcut.equals(objectId)) {
    			if (stopPoint.getLine() == null) {
    				stopPoint.setLine(line);
    				continue;
    			}
    			LoggingManager.log(logger, "Le \"lineIdShortcut\" () doit pointe  vers la \"Line\".", params, Level.ERROR);
    			validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUTREF_STOPPOINT, "Le \"lineIdShortcut\" () doit pointe  vers la \"Line\".", params);
    			continue;
    		}
    		LoggingManager.log(logger, "Le \"lineIdShortcut\" () doit pointe  vers la \"Line\".", params, Level.ERROR);
    		validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUTREF_STOPPOINT, "Le \"lineIdShortcut\" () doit pointe  vers la \"Line\".", params);
    	}
    }
    
    private void connectStopPointsPTNetworkIdShortCutToPTNetwork(ChouettePTNetwork chouettePTNetwork) {
    	ChouetteLineDescription chouetteLineDescription = chouettePTNetwork.getChouetteLineDescription();
    	if (chouetteLineDescription == null)
    		return;
    	PTNetwork pTNetwork = chouettePTNetwork.getPTNetwork();
    	if (pTNetwork == null)
    		return;
    	String objectId = pTNetwork.getObjectId();
    	if (objectId == null)
    		return;
    	objectId = objectId.trim();
    	if (objectId.length() == 0)
    		return;
    	List<StopPoint> stopPoints = chouetteLineDescription.getStopPoints();
    	if (stopPoints == null)
    		return;
    	for (int i = 0; i < chouetteLineDescription.getStopPointCount(); i++) {
    		StopPoint stopPoint = chouetteLineDescription.getStopPoint(i);
    		if (stopPoint == null)
    			continue;
    		String ptNetworkIdShortcut = stopPoint.getPtNetworkIdShortcut();
    		if (ptNetworkIdShortcut == null)
    			continue;
    		ptNetworkIdShortcut = ptNetworkIdShortcut.trim();
    		if (ptNetworkIdShortcut.length() == 0)
    			continue;
    		params = new String[] {ptNetworkIdShortcut};
    		if (ptNetworkIdShortcut.equals(objectId)) {
    			if (stopPoint.getPtNetwork() == null) {
    				stopPoint.setPtNetwork(pTNetwork);
    				continue;
    			}
    			LoggingManager.log(logger, "Le \"LineIdShortcut\" () doit pointe  vers la \"Line\".", params, Level.ERROR);
    			validationException.add(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUTREF_STOPPOINT, "Le \"LineIdShortcut\" () doit pointe  vers la \"Line\".", params);
    			continue;
    		}
    		LoggingManager.log(logger, "Le \"LineIdShortcut\" () doit pointe  vers la \"Line\".", params, Level.ERROR);
    		validationException.add(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUTREF_STOPPOINT, "Le \"LineIdShortcut\" () doit pointe  vers la \"Line\".", params);
    	}
    }
    
    private void connectConnectionLinksToStops(ChouettePTNetwork chouettePTNetwork) {
    	for (int i = 0; i < chouettePTNetwork.getConnectionLinkCount(); i++) {
    		ConnectionLink connectionLink = chouettePTNetwork.getConnectionLink(i);
    		if (connectionLink == null)
    			continue;
    		String stratOfLinkId = connectionLink.getStartOfLinkId();
    		String endOfLinkId = connectionLink.getEndOfLinkId();
    		if (stratOfLinkId == null)
    			continue;
    		if (endOfLinkId == null)
    			continue;
    		StopArea firstStopArea = stopAreaById.get(stratOfLinkId);
    		StopArea secondStopArea = stopAreaById.get(endOfLinkId);
    		params = new String[]{stratOfLinkId, endOfLinkId, connectionLink.getObjectId()};
    		if ((firstStopArea == null) && (secondStopArea == null)) {
    			LoggingManager.log(logger, "La \"StartOfLink\" () et la \"EndOfLink\" () de la \"ConnectionLink\" () ne sont pas dans la ligne \"Line\".", params, Level.ERROR);
    			validationException.add(TypeInvalidite.INVALIDLINKIDS_CONNECTIONLINK, "La \"StartOfLink\" () et la \"EndOfLink\" () de la \"ConnectionLink\" () ne sont pas dans la ligne \"Line\".", params);
    			continue;
    		}
    		if ((firstStopArea == secondStopArea) && ((ChouetteAreaType.BOARDINGPOSITION.equals(firstStopArea.getStopAreaExtension().getType())) || (ChouetteAreaType.QUAY.equals(firstStopArea.getStopAreaExtension().getType())))) {
    			LoggingManager.log(logger, "La \"StartOfLink\" () et la \"EndOfLink\" () de la \"ConnectionLink\" () pointent vers le meme \"Stop\".", params, Level.WARN);
    			//validationException.add(TypeInvalidite.INVALIDLINKIDS1_CONNECTIONLINK, "La \"StartOfLink\" () et la \"EndOfLink\" () de la \"ConnectionLink\" () pointent vers le meme \"Stop\".", params);
    			continue;
    		}
    		if (firstStopArea == null) {
    			params = new String[]{stratOfLinkId, connectionLink.getObjectId()};
    			LoggingManager.log(logger, "La \"StartOfLink\" () de la \"ConnectionLink\" () n'est pas dans la ligne \"Line\".", params, Level.INFO);
    		}
    		else {
    			connectionLink.setStartOfLink(firstStopArea);
    			firstStopArea.addConnectionLinkStart(connectionLink);
    		}
    		if (secondStopArea == null) {
    			params = new String[]{endOfLinkId, connectionLink.getObjectId()};
    			LoggingManager.log(logger, "La \"EndOfLink\" () de la \"ConnectionLink\" () n'est pas dans la ligne \"Line\".", params, Level.INFO);
    		}
    		else {
    			connectionLink.setEndOfLink(secondStopArea);
    			secondStopArea.addConnectionLinkEnd(connectionLink);                                             
    		}
    	}
    }
    
    private void connectTimetablesToVehicleJourneys(ChouettePTNetwork chouettePTNetwork) {
    	for (int i = 0; i < chouettePTNetwork.getTimetableCount(); i++) {
    		Timetable timetable = chouettePTNetwork.getTimetables().get(i);
    		if (timetable == null)
    			continue;
    		String[] vehicleJourneyIds = timetable.getVehicleJourneyIds();
    		if (vehicleJourneyIds == null)
    			return;
    		boolean noVehicleJourney = true;
    		for (int j = 0; j < vehicleJourneyIds.length; j++) {
    			VehicleJourney vehicleJourney = vehicleJourneyById.get(vehicleJourneyIds[j]);
    			if (vehicleJourney == null) {
    				//params = new String[]{timetable.getObjectId(), vehicleJourneyIds[j]};
    				//LoggingManager.log(logger, "\"Timetable\" () : Il n'y a pas de \"VehicleJourney\" avec comme nom ().", params, Level.ERROR);
    				//validationException.add(TypeInvalidite.NULLVEHICLEJOURNEYID_TIMETABLE, "\"Timetable\" () : Il n'y a pas de \"VehicleJourney\" avec comme nom ().", params);
    				continue;
    			}
    			timetable.addVehicleJourney(vehicleJourney);
    			vehicleJourney.addTimetable(timetable);
    			noVehicleJourney = false;
    		}
    		if(noVehicleJourney)
    			LoggingManager.log(logger, "\"Timetable\" () : Il n'y a pas de \"VehicleJourney\" dans ce TimeTable.", new String[]{timetable.getObjectId()}, Level.ERROR);
    	}
    	if (chouettePTNetwork.getChouetteLineDescription() != null)
    		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getVehicleJourneyCount(); i++)
    			if (chouettePTNetwork.getChouetteLineDescription().getVehicleJourney(i) != null)
    				if (chouettePTNetwork.getChouetteLineDescription().getVehicleJourney(i).getTimetableCount() <= 0) {
        				params = new String[]{chouettePTNetwork.getChouetteLineDescription().getVehicleJourney(i).getObjectId()};
        				LoggingManager.log(logger, "Un \"VehicleJourney\" () n'a pas de \"Timetable\".", params, Level.ERROR);
        				//validationException.add(TypeInvalidite.NULLTIMETABLE_VEHICLEJOURNEY, "Un \"VehicleJourney\" () n'a pas de \"Timetable\".", params);
        				continue;
    				}
    }
    
    private void connectStopAreasToSubStops(ChouettePTNetwork chouettePTNetwork) {
    	if (chouettePTNetwork.getChouetteArea() == null)
    		return;
    	for (int i = 0; i < chouettePTNetwork.getChouetteArea().getStopAreaCount(); i++) {
    		StopArea stopArea = chouettePTNetwork.getChouetteArea().getStopArea(i);
    		if (stopArea == null)
    			continue;
    		String[] containedStopIds = stopArea.getContainedStopIds();
    		if (containedStopIds == null)
    			continue;
    		boolean stopAreaIsUtil = false;
    		boolean containsStopPoints = false;
    		boolean containsStopAreas = false;
    		List<String> probableStopPoints = new ArrayList<String>();
    		for (int j = 0; j < containedStopIds.length; j++) {
    			StopPoint stopPoint = stopPointById.get(containedStopIds[j]);
    			StopArea _stopArea = stopAreaById.get(containedStopIds[j]);
    			boolean doTypeTest = true;
    			if (stopArea.getStopAreaExtension() == null)
    				doTypeTest = false;
    			if (stopArea.getStopAreaExtension().getType() == null)
    				doTypeTest = false;
    			if (doTypeTest)
    				if ((stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.ITL)) && (_stopArea == null)) {
    					params = new String[]{stopArea.getObjectId(), containedStopIds[j]};
    					LoggingManager.log(logger, "L'\"ITL\" () contient une \"StopArea\" () d'une autre ligne.", params, Level.WARN);
    				}
    			if ((stopPoint != null) && (_stopArea != null)) {
    				params = new String[]{containedStopIds[j]};
    				LoggingManager.log(logger, "Il existe une \"StopArea\" et un \"StopPoint\" avec le m�me \"objectId\" ().", params, Level.ERROR);
    				validationException.add(TypeInvalidite.INVALIDOBJECTID_STOPPOINTSTOPAREA, "Il existe une \"StopArea\" et un \"StopPoint\" avec le m�me \"objectId\" ().", params);
    			}
    			if (stopPoint == null)
    				if (containsStopPoints) {
    					params = new String[]{stopArea.getObjectId(), containedStopIds[j]};
    					LoggingManager.log(logger, "Ce \"StopArea\" () contient des \"StopPoint\" dont un () non d�fini.", params, Level.ERROR);
    					validationException.add(TypeInvalidite.UNKNOWNSUBSTOPIDS_STOPAREA, "Ce \"StopArea\" () contient des \"StopPoint\" dont un () non d�fini.", params);
    				}
    				else {
    					LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" ne peut contenir que des \"objectId\" de \"StopPoint\" ou de \"StopArea\" existant.", Level.INFO);
    					if (!containsStopAreas)
    						probableStopPoints.add(containedStopIds[j]);
    				}
    			else {
    				if (containsStopAreas) {
    					params = new String[]{stopArea.getObjectId()};
    					LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir que des \"objectId\" de \"StopPoint\" ou des \"objectId\" de \"StopArea\" et non les deux.", Level.ERROR);
    					validationException.add(TypeInvalidite.INVALIDCONTAINS_STOPAREA, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir que des \"objectId\" de \"StopPoint\" ou des \"objectId\" de \"StopArea\".", params);
    				}
    				containsStopPoints = true;
        			if (doTypeTest)
        				if (!stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.BOARDINGPOSITION) && !stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.QUAY)) {
        					params = new String[]{stopArea.getObjectId(), containedStopIds[j]};
        					LoggingManager.log(logger, "La \"StopArea\" () contient un \"StopPoint\" () n'est pas de type \"BOARDINGPOSITION\" ou \"QUAY\".", params, Level.ERROR);
        					validationException.add(TypeInvalidite.INVALIDCONTAINEDIDTYPE1_STOPAREA, "La \"StopArea\" () contient un \"StopPoint\" () n'est pas de type \"BOARDINGPOSITION\" ou \"QUAY\".", params);
        				}
    				stopArea.addContainedStopPoint(stopPoint);
    				if (stopPoint.getContainedInStopArea() != null) {
    					if (stopPoint.getContainedInStopArea() == stopArea) {
    						params = new String[] {stopPoint.getObjectId(), stopArea.getObjectId()};
    						LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir un \"objectId\" () de \"StopPoint\" qu'une seule fois.", params, Level.ERROR);
    						validationException.add(TypeInvalidite.INVALIDCONTAINEDID_STOPAREA, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir un \"objectId\" () de \"StopPoint\" qu'une seule fois.", params);
    					}
    					else {
    						params = new String[] {stopPoint.getObjectId(), stopArea.getObjectId(), stopPoint.getContainedInStopArea().getObjectId()};
    						LoggingManager.log(logger, "Un \"StopPoint\" () n'est contenu qu'au maximum dans un seul \"StopArea\" () et ().", params, Level.ERROR);
    						validationException.add(TypeInvalidite.INVALIDCONTAINEDINID1_STOPPOINT, "Un \"StopPoint\" () n'est contenu qu'au maximum dans un seul \"StopArea\" () et ().", params);
    					}
    				}
    				else {
    					stopPoint.setContainedInStopAreaId(stopArea.getObjectId());
    					stopPoint.setContainedInStopArea(stopArea);
    					stopAreaIsUtil = true;
    				}						
    			}
    			if (_stopArea == null)
    				logger.info("La liste des \"ContainedStopIds\" dans une \"StopArea\" ne peut contenir que des \"ObjectId\" de \"StopPoint\" ou de \"StopArea\" existant.");
    			else { // Dans ce cas "stopArea.getStopAreaExtension().getType() == COMMERCIALSTOPPOINT ou STOPPLACE ou ITL
    				boolean _doTypeTest = true;
    				if (_stopArea.getStopAreaExtension() == null)
    					_doTypeTest = false;
    				if (_stopArea.getStopAreaExtension().getType() == null)
    					_doTypeTest = false;
    				if (containsStopPoints) {
    					params = new String[]{stopArea.getObjectId()};
    					LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir que des \"objectId\" de \"StopPoint\" ou des \"objectId\" de \"StopArea\" et non les deux.", Level.ERROR);
    					validationException.add(TypeInvalidite.INVALIDCONTAINS_STOPAREA, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir que des \"objectId\" de \"StopPoint\" ou des \"objectId\" de \"StopArea\".", params);
    				}
    				containsStopAreas = true;
    				probableStopPoints = new ArrayList<String>();
    				if (doTypeTest)
    					if (stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.BOARDINGPOSITION) || stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.QUAY)) {
    						params = new String[]{stopArea.getObjectId(), containedStopIds[j], stopArea.getStopAreaExtension().getType().toString()};
    						LoggingManager.log(logger, "La \"StopArea\" () contient un autre \"StopArea\" () et est de type ().", params, Level.ERROR);
        					validationException.add(TypeInvalidite.INVALIDCONTAINEDIDTYPE2_STOPAREA, "La \"StopArea\" () contient un autre \"StopArea\" () et est de type ().", params);
        				}
        			if (doTypeTest && _doTypeTest)
        				if (stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.COMMERCIALSTOPPOINT))
        					if (!_stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.BOARDINGPOSITION) && !_stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.QUAY)) {
        						params = new String[]{stopArea.getObjectId(), _stopArea.getObjectId(), _stopArea.getStopAreaExtension().getType().toString()};
        						LoggingManager.log(logger, "La \"StopArea\" () est de type \"COMMERCIALSTOPPOINT\" alors qu'elle contient une autre \"StopArea\" () de type ().", params, Level.ERROR);
        						validationException.add(TypeInvalidite.INVALIDCONTAINEDIDTYPE3_STOPAREA, "La \"StopArea\" () est de type \"COMMERCIALSTOPPOINT\" alors qu'elle contient une autre \"StopArea\" () de type ().", params);//ERROR
        					}
    				stopArea.addContainedStopArea(_stopArea);
    				if (doTypeTest && stopArea.getStopAreaExtension().getType().equals(ChouetteAreaType.ITL)) {
    					_stopArea.addContainedInITL(stopArea);
    					stopAreaIsUtil = true;
    				}
    				else
    					if (_stopArea.getContainedInStopArea() != null) {
    						if (_stopArea.getContainedInStopArea() == stopArea) {
    							params = new String[]{stopArea.getObjectId(), _stopArea.getObjectId()};
    							LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir un \"ObjectId\" () de \"StopArea\" qu'une seule fois.", params, Level.ERROR);
    							validationException.add(TypeInvalidite.INVALIDCONTAINEDID1_STOPAREA, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne peut contenir un \"ObjectId\" () de \"StopArea\" qu'une seule fois.", params);
    						}
    						else {
    							params = new String[]{_stopArea.getObjectId(), stopArea.getObjectId(), _stopArea.getContainedInStopArea().getObjectId()};
    							LoggingManager.log(logger, "Un \"StopArea\" () n'est contenu qu'au maximum dans un seul \"StopArea\" () and ().", params, Level.ERROR);
    							validationException.add(TypeInvalidite.INVALIDCONTAINEDINID1_STOPAREA, "Un \"StopArea\" () n'est contenu qu'au maximum dans un seul \"StopArea\" () and ().", params);
    						}
    					}
    					else {
    						_stopArea.setContainedInStopArea(stopArea);
    						stopAreaIsUtil = true;
    					}
    			}
    		}
    		if (stopAreaIsUtil && (probableStopPoints.size() > 0)) {
    			params = new String[]{stopArea.getObjectId(), probableStopPoints.get(0)};
    			LoggingManager.log(logger, "Ce \"StopArea\" () contient des \"StopPoint\" dont un () non d�fini.", params, Level.ERROR);
    			validationException.add(TypeInvalidite.UNKNOWNSUBSTOPIDS_STOPAREA, "Ce \"StopArea\" () contient des \"StopPoint\" dont un () non d�fini.", params);
    		}
    		if (!stopAreaIsUtil) {
    			params = new String[]{stopArea.getObjectId()};
    			LoggingManager.log(logger, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne contient que des \"objectId\" non definis.", params, Level.ERROR);
    			validationException.add(TypeInvalidite.INVALIDCONTAINEDID2_STOPAREA, "La liste des \"ContainedStopIds\" dans une \"StopArea\" () ne contient que des \"objectId\" non definis.", params);
    		}
    		String centroidOfArea = stopArea.getCentroidOfArea();
    		if (centroidOfArea != null) {
    			AreaCentroid areaCentroid = areaCentroidById.get(centroidOfArea);
    			if (areaCentroid == null) {
    				params = new String[]{centroidOfArea, stopArea.getObjectId()};
    				LoggingManager.log(logger, "La \"CentroidOfArea\" () du \"StopArea\" () ne pointe aucun \"AreaCentroId\".", params, Level.ERROR);
    				validationException.add(TypeInvalidite.NULLCENTROIDOFAREA_STOPAREA, "La \"CentroidOfArea\" () du \"StopArea\" () ne pointe aucun \"AreaCentroId\".", params);
    			}
    			else {
    				stopArea.setAreaCentroid(areaCentroid);
    				areaCentroid.setContainerStopArea(stopArea);
    			}
    		}
    	}
    }
	
	private void connectAreaCentroidsToStopAreas(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteArea() == null)
			return;
		for (int i = 0; i < chouettePTNetwork.getChouetteArea().getAreaCentroidCount(); i++) {
			AreaCentroid areaCentroid = chouettePTNetwork.getChouetteArea().getAreaCentroid(i);
			if (areaCentroid == null)
				continue;
			StopArea containerStopArea = areaCentroid.getContainerStopArea();
			params = null;
			if (areaCentroid.getName() != null)
				params = new String[]{areaCentroid.getName()};
			else if (areaCentroid.getObjectId() != null)
				params = new String[]{areaCentroid.getObjectId()};
			if (containerStopArea == null)
				LoggingManager.log(logger, "Ce \"AreaCentroid\" () n'a pas ete declare comme \"CentroidOfArea\" d'un \"StopArea\".", params, Level.INFO);
			String containedIn = areaCentroid.getContainedIn();
			if ((containedIn == null) && (containerStopArea == null)) {
				LoggingManager.log(logger, "Un \"AreaCentroid\" () ne peut etre associe a un \"StopArea\" null.", params, Level.ERROR);
				validationException.add(TypeInvalidite.NULLCONTAINEDINSTOPAREA_AREACENTROID, "Un \"AreaCentroid\" () ne peut etre associe a un \"StopArea\" null.", params);
			}
			else {
				StopArea stopArea =  stopAreaById.get(containedIn);
				if ((stopArea == null) && (containerStopArea == null)) {
					LoggingManager.log(logger, "Un \"AreaCentroid\" () ne peut etre associe a un \"StopArea\" null.", params, Level.ERROR);
					validationException.add(TypeInvalidite.NULLCONTAINEDINSTOPAREA_AREACENTROID, "Un \"AreaCentroid\" () ne peut etre associe a un \"StopArea\" null.", params);
				}
				else if ((stopArea != null) && (containerStopArea != null) && (stopArea != containerStopArea)) {
					LoggingManager.log(logger, "Un \"AreaCentroid\" () ne peut etre associe a deux \"StopArea\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDCONTAINEDINSTOPAREA_AREACENTROID, "Un \"AreaCentroid\" () ne peut etre associe a deux \"StopArea\".", params);
				}
				else {
					stopArea.setAreaCentroid(areaCentroid);
					areaCentroid.setContainerStopArea(stopArea);						
				}
			}
		}
	}
	
	private void connectChouetteRoutesToJourneyPatternsAndPtLinks(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		if (chouettePTNetwork.getChouetteLineDescription().getChouetteRoutes() == null)
			return;
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getChouetteRouteCount(); i++) {
			ChouetteRoute chouetteRoute = chouettePTNetwork.getChouetteLineDescription().getChouetteRoute(i);
			if (chouetteRoute == null)
				continue;
			String[] journeyPatternIds = chouetteRoute.getJourneyPatternIds();
			if (journeyPatternIds != null)
				for (int j = 0; j < journeyPatternIds.length; j++) {
					JourneyPattern journeyPattern = journeyPatternById.get(journeyPatternIds[j]);
					if (journeyPattern == null) {
						params = new String[]{chouetteRoute.getObjectId(), journeyPatternIds[j]};
						LoggingManager.log(logger, "Dans une \"ChouetteRoute\" ()  un \"JourneyPatternId\" () doit absolument pointe vers un \"JourneyPattern\".", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN_CHOUETTEROUTE, "Dans une \"ChouetteRoute\" ()  un \"JourneyPatternId\" () doit absolument pointe vers un \"JourneyPattern\".", params);
					}
					else {
						if (journeyPattern.getRoute() == null) {
							journeyPattern.setRoute(chouetteRoute);
							chouetteRoute.addJourneyPattern(journeyPattern);
						}
						else
							if (journeyPattern.getRoute() == chouetteRoute) {
								params = new String[]{journeyPatternIds[j], chouetteRoute.getObjectId()};
								LoggingManager.log(logger, "Une \"JourneyPattern\" () doit figurer une seule fois dans une \"ChouetteRoute\" ().", params, Level.ERROR);
								validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN1_CHOUETTEROUTE, "Une \"JourneyPattern\" () doit figurer une seule fois dans une \"ChouetteRoute\" ().", params);
							}
							else {
								params = new String[]{journeyPatternIds[j], chouetteRoute.getObjectId(), journeyPattern.getRoute().getObjectId()};
								LoggingManager.log(logger, "Une \"JourneyPattern\" () doit figurer dans une unique \"ChouetteRoute\" () et ().", params, Level.ERROR);
								validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN2_CHOUETTEROUTE, "Une \"JourneyPattern\" () doit figurer dans une unique \"ChouetteRoute\" () et ().", params);
							}
					}
				}
			String      endOfLinkId = null;
			Set<String> endOfLinks  = new HashSet<String>();
			String[]    ptLinkIds   = chouetteRoute.getPtLinkIds();
			if (ptLinkIds != null)
				for (int j = 0; j < ptLinkIds.length; j++) {
					PtLink ptLink = ptLinkById.get(ptLinkIds[j]);
					if (ptLink == null) {
						params = new String[]{chouetteRoute.getObjectId(), ptLinkIds[j]};
						LoggingManager.log(logger, "Dans une \"ChouetteRoute\" ()  un \"PtLinkId\" () doit absolument pointe vers un \"PtLink\".", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDPTLINK_CHOUETTEROUTE, "Dans une \"ChouetteRoute\" ()  un \"PtLinkId\" () doit absolument pointe vers un \"PtLink\".", params);
					}
					else {
						if (j == 0)
							endOfLinks.add(ptLink.getStartOfLinkId());
						chouetteRoute.addPtLink(ptLink);
						ptLink.addChouetteRoute(chouetteRoute);
						if (j > 0)
							if (endOfLinkId != null)
								if (!endOfLinkId.equals(ptLink.getStartOfLinkId())) {
									params = new String[]{chouetteRoute.getObjectId()};
									LoggingManager.log(logger, "Les \"PtLink\" de cette \"ChouetteRoute\" () ne sont pas contigues.", params, Level.ERROR);
									validationException.add(TypeInvalidite.NONCONTIGUEPTLINKID_CHOUETTEROUTE, "Les \"PtLink\" de cette \"ChouetteRoute\" () ne sont pas contigues.", params);
								}
						endOfLinkId = ptLink.getEndOfLinkId();
						if (!endOfLinks.add(endOfLinkId)) {
							params = new String[]{chouetteRoute.getObjectId()};
							LoggingManager.log(logger, "Cette \"ChouetteRoute\" () contient un circuit.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDPTLINKS_CHOUETTEROUTE, "Cette \"ChouetteRoute\" () contient un circuit.", params);
						}
						if (ptLink.getChouetteRouteCount() > 1) {
							params = new String[]{ptLink.getObjectId()};
							LoggingManager.log(logger, "Le \"PTLink\" () est present dans plus qu'une \"ChouetteRoute\".", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDNUMBEROFROUTES_PTLINK, "Le \"PTLink\" () est present dans plus qu'une \"ChouetteRoute\".", params);
						}
					}
				}
			if (chouetteRoute.getWayBackRouteId() != null) {
				ChouetteRoute _chouetteRoute = chouetteRouteById.get(chouetteRoute.getWayBackRouteId());
				if ((_chouetteRoute == null) || (chouetteRoute == _chouetteRoute)) {
					params = new String[]{chouetteRoute.getWayBackRouteId(), chouetteRoute.getObjectId()};
					LoggingManager.log(logger, "La \"WayBackRoute\" () de cette \"ChouetteRoute\" () est inconnue.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDWAYBACKROUTE_CHOUETTEROUTE, "La \"WayBackRoute\" () de cette \"ChouetteRoute\" () est inconnue.", params);
				}
				else {
					chouetteRoute.setWayBackRoute(_chouetteRoute);
					_chouetteRoute.setIsWayBackRouteOf(chouetteRoute);
				}
			}
		}
	}
	
	private void connectLinesToChouetteRoutes(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		Line line = chouettePTNetwork.getChouetteLineDescription().getLine();
		if (line == null)
			return;
		String[] routeIds = line.getRouteIds();
		if (routeIds != null)
			for (int i = 0; i < routeIds.length; i++) {
				ChouetteRoute chouetteRoute = chouetteRouteById.get(routeIds[i]);
				if (chouetteRoute == null) {
					params = new String[]{routeIds[i], ""};
					if (line.getName() != null)
						params = new String[]{routeIds[i], line.getName()};
					else if (line.getObjectId() != null)
						params = new String[]{routeIds[i], line.getObjectId()};
					LoggingManager.log(logger, "Une \"routeId\" () dans une \"Line\" () doit pointer sur une \"ChouetteRoute\" non null.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTE_LINE, "Une \"routeId\" () dans une \"Line\" () doit pointer sur une \"ChouetteRoute\" non null.", params);
				}
				else
					line.addRoute(chouetteRoute);
			}
		String[] lineEnds = line.getLineEnds();
		if (lineEnds != null)
			for (int i = 0; i < lineEnds.length; i++) {
				StopPoint stopPoint = stopPointById.get(lineEnds[i]);
				if (stopPoint == null) {
					params = new String[]{lineEnds[i], ""};
					if (line.getName() != null)
						params = new String[]{lineEnds[i], line.getName()};
					else if (line.getObjectId() != null)
						params = new String[]{lineEnds[i], line.getObjectId()};
					LoggingManager.log(logger, "Une \"LineEnd\" () dans une \"Line\" () doit pointer sur un \"StopPoint\" non null.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEEND_LINE, "Une \"LineEnd\" () dans une \"Line\" () doit pointer sur un \"StopPoint\" non null.", params);
				}
				else {
					boolean isLineEnd = false;
					for (int j = 0; j < chouettePTNetwork.getChouetteLineDescription().getJourneyPatternCount(); j++) {
						if (chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j) != null)
							if (chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j).getStopPointList() != null)
								if (chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j).getStopPointList().length > 0)
									if (chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j).getStopPointList()[0].equals(stopPoint.getObjectId()) ||
											chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j).getStopPointList()[chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(j).getStopPointList().length-1].equals(stopPoint.getObjectId())) {
										isLineEnd = true;
										break;
									}
					}
					if (isLineEnd)
						line.addStopPointLineEnd(stopPoint);
					else {
						params = new String[]{stopPoint.getObjectId()};
						LoggingManager.log(logger, "Une \"LineEnd\" () doit etre un terminus de la \"Line\".", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDLINEEND1_LINE, "Une \"LineEnd\" () doit etre un terminus de la \"Line\".", params);
					}
				}
			}
		String ptNetworkIdShortcut = line.getPtNetworkIdShortcut();
		if (ptNetworkIdShortcut != null) {
	    	PTNetwork pTNetwork = chouettePTNetwork.getPTNetwork();
	    	if (pTNetwork == null)
	    		return;
	    	if (pTNetwork.getObjectId().equals(ptNetworkIdShortcut))
	    		line.setPTNetwork(pTNetwork);
	    	else {
				params = new String[]{ptNetworkIdShortcut, ""};
				if (line.getName() != null)
					params = new String[]{ptNetworkIdShortcut, line.getName()};
				else if (line.getObjectId() != null)
					params = new String[]{ptNetworkIdShortcut, line.getObjectId()};
				LoggingManager.log(logger, "Le \"PTNetworkIdShortcut\" () de cette \"Line\" () est inconnu.", params, Level.ERROR);
	    		validationException.add(TypeInvalidite.INVALIDPTNETWORKIDSHORTCUT_LINE, "Le \"PTNetworkIdShortcut\" () de cette \"Line\" () est inconnu.", params);
	    	}
		}			
	}
	
	private void connectPtLinksToStopPoints(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		if (chouettePTNetwork.getChouetteLineDescription().getPtLinks() == null)
			return;
		Set<StopPoint> stopPoints = new HashSet<StopPoint>();
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getPtLinkCount(); i++) {
			PtLink ptLink =  chouettePTNetwork.getChouetteLineDescription().getPtLinks().get(i);
			if (ptLink == null)
				continue;
			if (ptLink.getChouetteRouteCount() < 1) {
				params = new String[]{ptLink.getObjectId()};
				LoggingManager.log(logger, "Le \"PTLink\" () n'est present dans aucune \"ChouetteRoute\".", params, Level.ERROR);
				validationException.add(TypeInvalidite.INVALIDNUMBEROFROUTES1_PTLINK, "Le \"PTLink\" () n'est present dans aucune \"ChouetteRoute\".", params);
			}
			String startOfLinkId = ptLink.getStartOfLinkId();
			if (startOfLinkId != null) {
				StopPoint startOfLink = stopPointById.get(startOfLinkId);
				if (startOfLink == null) {
					params = new String[]{startOfLinkId};
					if (ptLink.getName() != null)
						params = new String[]{startOfLinkId, ptLink.getName()};
					else if (ptLink.getObjectId() != null)
						params = new String[]{startOfLinkId, ptLink.getObjectId()};
					LoggingManager.log(logger, "Le \"StartOfLinkId\" () d'un \"PtLink\" () doit pointer vers un \"StopPoint\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDSTARTOFLINK_PTLINK, "Le \"StartOfLinkId\" () d'un \"PtLink\" () doit pointer vers un \"StopPoint\".", params);
				}
				stopPoints.add(startOfLink);
				ptLink.setStartOfLink(startOfLink);
			}
			String endOfLinkId = ptLink.getEndOfLinkId();
			if (endOfLinkId != null)
				if (endOfLinkId.equals(startOfLinkId)) {
					params = new String[]{endOfLinkId, ptLink.getObjectId()};
					LoggingManager.log(logger, "Le \"StartOfLinkId\" et le \"EndOfLinkId\" () d'un \"PtLink\" () doivent etre distincts.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINKID_PTLINK, "Le \"StartOfLinkId\" et le \"EndOfLinkId\" () d'un \"PtLink\" () doivent etre distincts.", params);
				}
				else {
					StopPoint endOfLink = stopPointById.get(endOfLinkId);
					if (endOfLink == null) {
						params = new String[]{endOfLinkId};
						if (ptLink.getName() != null)
							params = new String[]{endOfLinkId, ptLink.getName()};
						else if (ptLink.getObjectId() != null)
							params = new String[]{endOfLinkId, ptLink.getObjectId()};
						LoggingManager.log(logger, "Le \"EndOfLinkId\" () d'un \"PtLink\" () doit pointer vers un \"StopPoint\".", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDENDOFLINK_PTLINK, "Le \"EndOfLinkId\" () d'un \"PtLink\" () doit pointer vers un \"StopPoint\".", params);
					}
					stopPoints.add(endOfLink);
					ptLink.setEndOfLink(endOfLink);			
				}
		}
		if (stopPoints.size() < stopPointById.size()) {
			LoggingManager.log(logger, "Il existe des \"StopPoint\" qui n'ont pas de \"ChouetteRoute\".", Level.ERROR);
			validationException.add(TypeInvalidite.INVALIDSTOPPOINTLIST2_CHOUETTEROUTE, "Il existe des \"StopPoint\" qui n'ont pas de \"ChouetteRoute\".");
		}
	}
	
	private void connectJourneyPatterns(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		Line line = chouettePTNetwork.getChouetteLineDescription().getLine();
		if (line == null)
			return;
		Set<StopPoint> stopPoints = new HashSet<StopPoint>();
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getJourneyPatternCount(); i++) {
			JourneyPattern journeyPattern = chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(i);
			if (journeyPattern == null)
				continue;
			String routeId = journeyPattern.getRouteId();
			if (routeId != null) {
				ChouetteRoute chouetteRoute = chouetteRouteById.get(routeId);
				if (chouetteRoute == null) {
					params = new String[]{routeId};
					if (journeyPattern.getName() != null)
						params = new String[]{routeId, journeyPattern.getName()};
					else if (journeyPattern.getObjectId() != null)
						params = new String[]{routeId, journeyPattern.getObjectId()};
					LoggingManager.log(logger, "Le \"RouteId\" () d'un \"JourneyPattern\" () doit pointer vers une \"ChouetteRoute\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTE_JOURNEYPATTERN, "Le \"RouteId\" () d'un \"JourneyPattern\" () doit pointer vers une \"ChouetteRoute\".", params);
				}
				else {
					if (journeyPattern.getRoute() == null) {
						if ((journeyPattern.getName() != null) && (chouetteRoute.getName() != null))
							params = new String[]{journeyPattern.getName(), chouetteRoute.getName()};
						else if ((journeyPattern.getName() != null) && (chouetteRoute.getObjectId() != null))
							params = new String[]{journeyPattern.getName(), chouetteRoute.getObjectId()};
						else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getName() != null))
							params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getName()};
						else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getObjectId() != null))
							params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getObjectId()};
						LoggingManager.log(logger, "La \"JourneyPattern\" () ne figure pas dans la liste des \"JourneyPatternId\" de la \"ChouetteRoute\" ().", params, Level.WARN);
						journeyPattern.setRoute(chouetteRoute);
						chouetteRoute.addJourneyPattern(journeyPattern);
					}
					else
						if (journeyPattern.getRoute() != chouetteRoute) {
							if ((journeyPattern.getName() != null) && (chouetteRoute.getName() != null) && (journeyPattern.getRoute().getName() != null))
								params = new String[]{journeyPattern.getName(), chouetteRoute.getName(), journeyPattern.getRoute().getName()};
							else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getName() != null) && (journeyPattern.getRoute().getName() != null))
								params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getName(), journeyPattern.getRoute().getName()};
							else if ((journeyPattern.getName() != null) && (chouetteRoute.getObjectId() != null) && (journeyPattern.getRoute().getName() != null))
								params = new String[]{journeyPattern.getName(), chouetteRoute.getObjectId(), journeyPattern.getRoute().getName()};
							else if ((journeyPattern.getName() != null) && (chouetteRoute.getName() != null) && (journeyPattern.getRoute().getObjectId() != null))
								params = new String[]{journeyPattern.getName(), chouetteRoute.getName(), journeyPattern.getRoute().getObjectId()};
							else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getObjectId() != null) && (journeyPattern.getRoute().getName() != null))
								params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getObjectId(), journeyPattern.getRoute().getName()};
							else if ((journeyPattern.getName() != null) && (chouetteRoute.getObjectId() != null) && (journeyPattern.getRoute().getObjectId() != null))
								params = new String[]{journeyPattern.getName(), chouetteRoute.getObjectId(), journeyPattern.getRoute().getObjectId()};
							else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getName() != null) && (journeyPattern.getRoute().getObjectId() != null))
								params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getName(), journeyPattern.getRoute().getObjectId()};
							else if ((journeyPattern.getObjectId() != null) && (chouetteRoute.getObjectId() != null) && (journeyPattern.getRoute().getObjectId() != null))
								params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getObjectId(), journeyPattern.getRoute().getObjectId()};
							LoggingManager.log(logger, "Une \"JourneyPattern\" () doit figurer dans une unique \"ChouetteRoute\" () et ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDROUTE1_JOURNEYPATTERN, "Une \"JourneyPattern\" () doit figurer dans une unique \"ChouetteRoute\" () et ().", params);
						}
				}
			}
			
			String[] stopPointList = journeyPattern.getStopPointList();
			if (stopPointList != null)
				for (int j = 0; j < stopPointList.length; j++) {
					StopPoint stopPoint = stopPointById.get(stopPointList[j]);
					if (stopPoint == null) {
						params = new String[] {stopPointList[j]};
						if (journeyPattern.getName() != null)
							params = new String[] {stopPointList[j], journeyPattern.getName()};
						else if (journeyPattern.getObjectId() != null)
							params = new String[] {stopPointList[j], journeyPattern.getObjectId()};
						LoggingManager.log(logger, "La liste des \"StopPointList\" () dans une \"JourneyPattern\" () doit pointer vers des \"StopPoint\" existants.", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDSTOPPOINTLIST1_JOURNEYPATTERN, "La liste des \"StopPointList\" () dans une \"JourneyPattern\" () doit pointer vers des \"StopPoint\" existants.", params);
					}
					else {
						stopPoints.add(stopPoint);
						journeyPattern.addStopPoint(stopPoint);
					}
				}
			
			String lineIdShortcut = journeyPattern.getLineIdShortcut();
			if (lineIdShortcut != null)
				if (!lineIdShortcut.equals(line.getObjectId())) {
					params = new String[]{lineIdShortcut, line.getObjectId()};
					LoggingManager.log(logger, "Le \"lineIdShortcut\" () dans une \"JourneyPattern\" doit referencer le \"Line\" ().", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT1_JOURNEYPATTERN, "Le \"lineIdShortcut\" () dans une \"JourneyPattern\" doit referencer le \"Line\" ().", params);
				}
		}
		if (stopPoints.size() < stopPointById.size()) {
			LoggingManager.log(logger, "Il existe des \"StopPoint\" qui n'ont pas de \"JourneyPattern\".", Level.ERROR);
			validationException.add(TypeInvalidite.INVALIDSTOPPOINTLIST2_JOURNEYPATTERN, "Il existe des \"StopPoint\" qui n'ont pas de \"JourneyPattern\".");
		}
	}
	
	private void connectVehicleJourneys(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		Line line = chouettePTNetwork.getChouetteLineDescription().getLine();
		if (line == null)
			return;
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getVehicleJourneyCount(); i++) {
			VehicleJourney vehicleJourney = chouettePTNetwork.getChouetteLineDescription().getVehicleJourney(i);
			if (vehicleJourney == null)
				continue;
			String routeId = vehicleJourney.getRouteId();
			if (routeId != null) {
				ChouetteRoute chouetteRoute = chouetteRouteById.get(routeId);
				if (chouetteRoute == null) {
					params = new String[]{routeId};
					LoggingManager.log(logger, "L'objet \"routeId\" () du \"VehicleJourney\" doit pointer vers une \"ChouetteRoute\" existante.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDROUTE_VEHICLEJOURNEY, "L'objet \"routeId\" () du \"VehicleJourney\" doit pointer vers une \"ChouetteRoute\" existante.", params);
				}
				else
					if (vehicleJourney.getChouetteRoute() == null)
						vehicleJourney.setChouetteRoute(chouetteRoute);
					else
						if (vehicleJourney.getChouetteRoute() == chouetteRoute) {
							params = new String[]{vehicleJourney.getObjectId(), chouetteRoute.getObjectId()};
							LoggingManager.log(logger, "Il semble que cette \"VehicleJourney\" () apparait deux fois dans la liste des \"VehicleJourney\" de cette \"ChouetteRoute\" ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDROUTE1_VEHICLEJOURNEY, "Il semble que cette \"VehicleJourney\" () apparait deux fois dans la liste des \"VehicleJourney\" de cette \"ChouetteRoute\" ().", params);
						}
						else {
							params = new String[]{vehicleJourney.getObjectId(), chouetteRoute.getObjectId(), vehicleJourney.getChouetteRoute().getObjectId()};
							LoggingManager.log(logger, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"ChouetteRoute\" () et ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDROUTE2_VEHICLEJOURNEY, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"ChouetteRoute\" () et ().", params);
						}
			}
			String journeyPatternId = vehicleJourney.getJourneyPatternId();
			if (journeyPatternId != null) {
				JourneyPattern journeyPattern = journeyPatternById.get(journeyPatternId);
				if (journeyPattern == null) {
					params = new String[]{journeyPatternId};
					LoggingManager.log(logger, "L'objet \"JourneyPatternId\" () du \"VehicleJourney\" doit pointer vers une \"JourneyPattern\" existante.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN_VEHICLEJOURNEY, "L'objet \"JourneyPatternId\" () du \"VehicleJourney\" doit pointer vers une \"JourneyPattern\" existante.", params);
				}
				else
					if (vehicleJourney.getJourneyPattern() == null)
						vehicleJourney.setJourneyPattern(journeyPattern);
					else
						if (vehicleJourney.getJourneyPattern() == journeyPattern) {
							params = new String[]{journeyPattern.getObjectId()};
							LoggingManager.log(logger, "Il semble que cette \"JourneyPattern\" () apparait deux fois dans la liste des \"JourneyPattern\" de cette \"ChouetteRoute\" ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN1_VEHICLEJOURNEY, "Il semble que cette \"JourneyPattern\" () apparait deux fois dans la liste des \"JourneyPattern\" de cette \"ChouetteRoute\" ().", params);
						}
						else  {
							params = new String[]{vehicleJourney.getObjectId(), journeyPattern.getObjectId(), vehicleJourney.getJourneyPattern().getObjectId()};
							LoggingManager.log(logger, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"JourneyPattern\" () et ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDJOURNEYPATTERN2_VEHICLEJOURNEY, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"JourneyPattern\" () et ().", params);
						}
			}
			String timeSlotId = vehicleJourney.getTimeSlotId();
			if (timeSlotId != null) {
				TimeSlot timeSlot = timeSlotById.get(timeSlotId);
				if (timeSlot == null) {
					params = new String[]{timeSlotId};
					LoggingManager.log(logger, "L'objet \"TimeSlotId\" () du \"VehicleJourney\" doit pointer vers une \"TimeSlot\" existante.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDTIMESLOT_VEHICLEJOURNEY, "L'objet \"TimeSlotId\" () du \"VehicleJourney\" doit pointer vers une \"TimeSlot\" existante.", params);
				}
				else
					if (vehicleJourney.getTimeSlot() == null)
						vehicleJourney.setTimeSlot(timeSlot);
					else
						if (vehicleJourney.getTimeSlot() == timeSlot) {
							params = new String[]{timeSlot.getObjectId()};
							LoggingManager.log(logger, "Il semble que cette \"TimeSlot\" () apparait deux fois dans la liste des \"TimeSlot\" de cette \"ChouetteRoute\".", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDTIMESLOT1_VEHICLEJOURNEY, "Il semble que cette \"TimeSlot\" () apparait deux fois dans la liste des \"TimeSlot\" de cette \"ChouetteRoute\".", params);
						}
						else {
							params = new String[]{vehicleJourney.getObjectId(), timeSlot.getObjectId(), vehicleJourney.getTimeSlot().getObjectId()};
							LoggingManager.log(logger, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"TimeSlot\" () et ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDTIMESLOT2_VEHICLEJOURNEY, "Une \"VehicleJourney\" () ne peut avoir qu'une seule \"TimeSlot\" () et ().", params);
						}
			}
			for (int j = 0; j < vehicleJourney.getVehicleJourneyAtStopCount(); j++) {
				VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStop(j);
				if (vehicleJourneyAtStop != null) {
					String stopPointId = vehicleJourneyAtStop.getStopPointId();
					if (stopPointId != null) {
						StopPoint stopPoint = stopPointById.get(stopPointId);
						if (stopPoint == null) {
							params = new String[]{stopPointId};
							LoggingManager.log(logger, "Un \"StopPointId\" () du \"VehicleJourneyAtStop\" doit referencer un \"StopPoint\" valide.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDSTOPPOINT_VEHICLEJOURNEYATSTOP, "Un \"StopPointId\" () du \"VehicleJourneyAtStop\" doit referencer un \"StopPoint\" valide.", params);
						}
						else
							vehicleJourneyAtStop.setStopPoint(stopPoint);
					}
					String vehicleJourneyId = vehicleJourneyAtStop.getVehicleJourneyId();
					if (vehicleJourneyId != null)
						if (!vehicleJourneyId.equals(vehicleJourneyAtStop.getVehicleJourney().getObjectId())) {
							params = new String[]{vehicleJourneyId, vehicleJourneyAtStop.getVehicleJourney().getObjectId()};
							LoggingManager.log(logger, "Le \"VehicleJourneyId\" () du \"VehicleJourneyAtStop\" doit etre egale a l'\"objectId\" du \"VehicleJourney\" () qui le contient.", params, Level.ERROR);
							validationException.add(TypeInvalidite.INVALIDVEHICLEJOURNEY_VEHICLEJOURNEYATSTOP, "Le \"VehicleJourneyId\" () du \"VehicleJourneyAtStop\" doit etre egale a l'\"objectId\" du \"VehicleJourney\" () qui le contient.", params);
						}
				}
			}
			
			String lineIdShortcut = vehicleJourney.getLineIdShortcut();
			if (lineIdShortcut != null)
				if (!lineIdShortcut.equals(line.getObjectId())) {
					params = new String[]{lineIdShortcut, vehicleJourney.getObjectId()};
					LoggingManager.log(logger, "Le \"LineIdShortcut\" () dans un \"VehicleJourney\" () doit referencer le \"Line\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUTLINE_VEHICLEJOURNEY, "Le \"LineIdShortcut\" () dans un \"VehicleJourney\" () doit referencer le \"Line\".");
				}
			String operatorId = vehicleJourney.getOperatorId();
			if (operatorId != null) {
				List<Company> companies = chouettePTNetwork.getCompanies();
				if (companies != null) {
					boolean wasNotFound = true;
					for (int j = 0; j < chouettePTNetwork.getCompanyCount(); j++) {
						Company company = chouettePTNetwork.getCompany(j);
						if (operatorId.equals(company.getObjectId())) {
							wasNotFound = false;
							break;
						}
					}
					if (wasNotFound) {
						params = new String[]{operatorId};
						LoggingManager.log(logger, "L'\"OperatorId\" () ne reference aucune \"Company\".", params, Level.ERROR);
						validationException.add(TypeInvalidite.INVALIDOPERATOR_VEHICLEJOURNEY, "L'\"OperatorId\" () ne reference aucune \"Company\".", params);
					}
				}
			}
		}
	}
	
	private void connectITLs(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		Line line = chouettePTNetwork.getChouetteLineDescription().getLine();
		if (line == null)
			return;
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getITLCount(); i++) {
			ITL iTL = chouettePTNetwork.getChouetteLineDescription().getITL(i);
			if (iTL == null)
				continue;
			String lineIdShortcut = iTL.getLineIdShortcut();
			if (lineIdShortcut != null)
				if (!lineIdShortcut.equals(line.getObjectId())) {
					params = new String[]{lineIdShortcut};
					LoggingManager.log(logger, "Le \"LineIdShortcut\" () dans une \"ITL\" doit referencer le \"Line\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDLINEIDSHORTCUT_ITL, "Le \"LineIdShortcut\" () dans une \"ITL\" doit referencer le \"Line\".", params);
				}
			String areaId = iTL.getAreaId();
			if (areaId != null) {
				StopArea stopArea = stopAreaById.get(areaId);
				if (stopArea == null) {
					params = new String[]{areaId};
					LoggingManager.log(logger, "Le \"AreaId\" () dans une \"ITL\" doit referencer un \"StopArea\" valide.", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDAREA_ITL, "Le \"AreaId\" () dans une \"ITL\" doit referencer un \"StopArea\" valide.", params);
					continue;
				}
				if (iTL.getArea() == null)
					iTL.setArea(stopArea);
				if (iTL.getArea() != stopArea) {
					params = new String[]{areaId};
					LoggingManager.log(logger, "Le \"AreaId\" () dans une \"ITL\" doit referencer un unique \"StopArea\".", params, Level.ERROR);
					validationException.add(TypeInvalidite.INVALIDAREA1_ITL, "Le \"AreaId\" () dans une \"ITL\" doit referencer un unique \"StopArea\".", params);
				}
			}
		}
	}
	
	private void verifierCoherenceMissionItineraire(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		for (int i = 0; i < chouettePTNetwork.getChouetteLineDescription().getJourneyPatternCount(); i++) {
			JourneyPattern journeyPattern = chouettePTNetwork.getChouetteLineDescription().getJourneyPattern(i);
			if (journeyPattern == null)
				continue;
			int numberOfStopPoints = journeyPattern.getStopPointCount();
			if (numberOfStopPoints <= 0)
				continue;
			ChouetteRoute chouetteRoute = journeyPattern.getRoute();
			if (chouetteRoute == null)
				continue;
			int index = 0;
			StopPoint stopPoint2 = journeyPattern.getStopPoint(index);
			int numberOfPtLinks = chouetteRoute.getPtLinkCount();
			for (int j = 0; j < numberOfPtLinks; j++) {
				PtLink ptLink = chouetteRoute.getPtLink(j);
				if (ptLink == null)
					continue;
				StopPoint stopPoint1 = ptLink.getStartOfLink();
				if (stopPoint1 == stopPoint2) {
					if (index == (numberOfStopPoints-1))
						break;
					index++;
					stopPoint2 = journeyPattern.getStopPoint(index);
					if (j == (numberOfPtLinks-1)) {
						stopPoint1 = ptLink.getEndOfLink();
						if (stopPoint1 != stopPoint2) {
							params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getObjectId()};
							LoggingManager.log(logger, "La \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" ().", params, Level.ERROR);
							validationException.add(TypeInvalidite.INCOHERENCE_JOURNEYPATTERN_CHOUETTEROUTE, "La \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" ().", params);
						}
					}
				}
			}
			if (index != (numberOfStopPoints-1)) {
				params = new String[]{journeyPattern.getObjectId(), chouetteRoute.getObjectId()};
				LoggingManager.log(logger, "La \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.INCOHERENCE_JOURNEYPATTERN_CHOUETTEROUTE, "La \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" ().", params);
			}
		}
	}
	
	private void verifierCoherenceCourseMission(ChouettePTNetwork chouettePTNetwork) {
		if (chouettePTNetwork.getChouetteLineDescription() == null)
			return;
		int numberOfVehicleJourney = chouettePTNetwork.getChouetteLineDescription().getVehicleJourneyCount();
		for (int i = 0; i < numberOfVehicleJourney; i++) {
			VehicleJourney vehicleJourney = chouettePTNetwork.getChouetteLineDescription().getVehicleJourney(i);
			if (vehicleJourney == null)
				continue;
			JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();
			if (journeyPattern == null) {
				verifierCoherenceCourseItineraire(vehicleJourney);
				continue;
			}
			ChouetteRoute chouetteRoute1 = vehicleJourney.getChouetteRoute();
			ChouetteRoute chouetteRoute2 = journeyPattern.getRoute();
			if ((chouetteRoute1 != null) && (chouetteRoute2 != null) && (chouetteRoute1 != chouetteRoute2)) {
				params = new String[]{chouetteRoute2.getObjectId(), journeyPattern.getObjectId(), chouetteRoute1.getObjectId(), vehicleJourney.getObjectId()};
				LoggingManager.log(logger, "La \"ChouetteRoute\" () de la \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" () de la \"VehicleJourney\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.INCOHERENCE_JOURNEYPATTERNROUTE_VEHICLEJOURNEYROUTE, "La \"ChouetteRoute\" () de la \"JourneyPattern\" () est incoherente avec la \"ChouetteRoute\" () de la \"VehicleJourney\" ().", params);
			}
			int numberOfStopPoints = journeyPattern.getStopPointCount();
			int numberVehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStopCount();
			if (numberOfStopPoints != numberVehicleJourneyAtStop) {
				params = new String[]{journeyPattern.getObjectId(), vehicleJourney.getObjectId()};
				LoggingManager.log(logger, "La \"JourneyPattern\" () est incoherente avec la \"VehicleJourney\" ().", params, Level.ERROR);
				validationException.add(TypeInvalidite.INCOHERENCE_JOURNEYPATTERN_VEHICLEJOURNEY, "La \"JourneyPattern\" () est incoherente avec la \"VehicleJourney\" ().", params);
				continue;
			}
			for (int j = 0; j < numberOfStopPoints; j++)
				if ((vehicleJourney.getVehicleJourneyAtStop(j) != null) && (journeyPattern.getStopPoint(j) != vehicleJourney.getVehicleJourneyAtStop(j).getStopPoint())) {
					params = new String[]{journeyPattern.getObjectId(), vehicleJourney.getObjectId()};
					LoggingManager.log(logger, "La \"JourneyPattern\" () est incoherente avec la \"VehicleJourney\" ().", params, Level.ERROR);
					validationException.add(TypeInvalidite.INCOHERENCE_JOURNEYPATTERN_VEHICLEJOURNEY, "La \"JourneyPattern\" () est incoherente avec la \"VehicleJourney\" ().", params);
					break;
				}
		}
	}
	
	private void verifierCoherenceCourseItineraire(VehicleJourney vehicleJourney) {
		if (vehicleJourney == null)
			return;
		ChouetteRoute chouetteRoute = vehicleJourney.getChouetteRoute();
		if (chouetteRoute == null)
			return;
		int numberOfVehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStopCount();
		if (numberOfVehicleJourneyAtStop <= 0)
			return;
		int index = 0;
		if (vehicleJourney.getVehicleJourneyAtStop(index) == null)
			return;
		StopPoint stopPoint2 = vehicleJourney.getVehicleJourneyAtStop(index).getStopPoint();
		int numberOfPtLinks = chouetteRoute.getPtLinkCount();
		for (int j = 0; j < numberOfPtLinks; j++) {
			PtLink ptLink = chouetteRoute.getPtLink(j);
			if (ptLink == null)
				continue;
			StopPoint stopPoint1 = ptLink.getStartOfLink();
			if (stopPoint1 == stopPoint2) {
				index++;
				if (index == numberOfVehicleJourneyAtStop)
					break;
				if (vehicleJourney.getVehicleJourneyAtStop(index) == null)
					continue;
				stopPoint2 = vehicleJourney.getVehicleJourneyAtStop(index).getStopPoint();
				if (j == (numberOfPtLinks-1)) {
					stopPoint1 = ptLink.getEndOfLink();
					if (stopPoint1 != stopPoint2) {
						params = new String[]{vehicleJourney.getObjectId(), chouetteRoute.getObjectId()};
						LoggingManager.log(logger, "La \"vehicleJourney\" () est incoherente avec la \"ChouetteRoute\" ().", params, Level.ERROR);
						validationException.add(TypeInvalidite.INCOHERENCE_VEHICLEJOURNEY_CHOUETTEROUTE, "La \"vehicleJourney\" () est incoherente avec la \"ChouetteRoute\" ().", params);
					}
					else
						index++;
				}
			}
		}
		if (index != numberOfVehicleJourneyAtStop) {
			params = new String[]{vehicleJourney.getObjectId(), chouetteRoute.getObjectId()};
			LoggingManager.log(logger, "La \"vehicleJourney\" () est incoherente avec la \"ChouetteRoute\" ().", params, Level.ERROR);
			validationException.add(TypeInvalidite.INCOHERENCE_VEHICLEJOURNEY_CHOUETTEROUTE, "La \"vehicleJourney\" () est incoherente avec la \"ChouetteRoute\" ().", params);
		}
	}
}


