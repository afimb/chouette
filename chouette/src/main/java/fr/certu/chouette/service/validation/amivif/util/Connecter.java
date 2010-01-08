package fr.certu.chouette.service.validation.amivif.util;

import java.util.Hashtable;
import java.util.Map;

import fr.certu.chouette.service.validation.amivif.AccessPoint;
import fr.certu.chouette.service.validation.amivif.ConnectionLink;
import fr.certu.chouette.service.validation.amivif.GroupOfLine;
import fr.certu.chouette.service.validation.amivif.ICT;
import fr.certu.chouette.service.validation.amivif.JourneyPattern;
import fr.certu.chouette.service.validation.amivif.Line;
import fr.certu.chouette.service.validation.amivif.PTAccessLink;
import fr.certu.chouette.service.validation.amivif.PTLink;
import fr.certu.chouette.service.validation.amivif.RespPTLineStructTimetableType;
import fr.certu.chouette.service.validation.amivif.Route;
import fr.certu.chouette.service.validation.amivif.StopArea;
import fr.certu.chouette.service.validation.amivif.StopPoint;
import fr.certu.chouette.service.validation.amivif.StopPointInConnection;
import fr.certu.chouette.service.validation.amivif.SubLine;
import fr.certu.chouette.service.validation.amivif.Timetable;
import fr.certu.chouette.service.validation.amivif.TransportNetwork;
import fr.certu.chouette.service.validation.amivif.VehicleJourney;
import fr.certu.chouette.service.validation.amivif.VehicleJourney.VehicleJourneyAtStop;
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class Connecter {
	
	private ValidationException					validationException;
	private RespPTLineStructTimetableType		respPTLineStructTimetableType;
	private Map<String, StopPoint>				stopPointsByObjectId				= new Hashtable<String, StopPoint>();
	private Map<String, AccessPoint>			accessPointsByObjectId				= new Hashtable<String, AccessPoint>();
	private Map<String, StopPointInConnection>	stopPointInConnectionsByObjectId	= new Hashtable<String, StopPointInConnection>();
	private Map<String, StopArea>				stopAreasByObjectId					= new Hashtable<String, StopArea>();
	private Map<String, Route>					routesByObjectId					= new Hashtable<String, Route>();
	private Map<String, PTLink>					pTLinksByObjectId					= new Hashtable<String, PTLink>();
	private Map<String, JourneyPattern>			journeyPatternsByObjectId			= new Hashtable<String, JourneyPattern>();
	private Map<String, VehicleJourney>			vehicleJourneysByObjectId			= new Hashtable<String, VehicleJourney>();
	
	public Connecter(ValidationException validationException, RespPTLineStructTimetableType respPTLineStructTimetableType) {
		setValidationException(validationException);
		setRespPTLineStructTimetableType(respPTLineStructTimetableType);
	}
	
    public void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	public ValidationException getValidationException() {
		return validationException;
	}
	
	public void setRespPTLineStructTimetableType(RespPTLineStructTimetableType respPTLineStructTimetableType) {
		this.respPTLineStructTimetableType = respPTLineStructTimetableType;
	}
	
	public RespPTLineStructTimetableType getRespPTLineStructTimetableType() {
		return respPTLineStructTimetableType;
	}
	
	public void valider() {
		if (respPTLineStructTimetableType == null)
			return;
		construitDictionaires();
		valider(respPTLineStructTimetableType.getTransportNetwork());
		for (int i = 0; i < respPTLineStructTimetableType.getGroupOfLinesCount(); i++)
			valider(respPTLineStructTimetableType.getGroupOfLine(i));
		valider(respPTLineStructTimetableType.getLine());
		for (int i = 0; i < respPTLineStructTimetableType.getStopAreasCount(); i++)
			valider(respPTLineStructTimetableType.getStopArea(i));
		for (int i = 0; i < respPTLineStructTimetableType.getStopPointsCount(); i++)
			valider(respPTLineStructTimetableType.getStopPoint(i));
		for (int i = 0; i < respPTLineStructTimetableType.getPTLinksCount(); i++)
			valider(respPTLineStructTimetableType.getPTLink(i));
		for (int i = 0; i < respPTLineStructTimetableType.getRoutesCount(); i++)
			valider(respPTLineStructTimetableType.getRoute(i));
		for (int i = 0; i < respPTLineStructTimetableType.getSubLinesCount(); i++)
			valider(respPTLineStructTimetableType.getSubLine(i));
		for (int i = 0; i < respPTLineStructTimetableType.getAccessPointsCount(); i++)
			valider(respPTLineStructTimetableType.getAccessPoint(i));
		for (int i = 0; i < respPTLineStructTimetableType.getPTAccessLinksCount(); i++)
			valider(respPTLineStructTimetableType.getPTAccessLink(i));
		for (int i = 0; i < respPTLineStructTimetableType.getStopPointInConnectionsCount(); i++)
			valider(respPTLineStructTimetableType.getStopPointInConnection(i));
		for (int i = 0; i < respPTLineStructTimetableType.getConnectionLinksCount(); i++)
			valider(respPTLineStructTimetableType.getConnectionLink(i));
		for (int i = 0; i < respPTLineStructTimetableType.getICTsCount(); i++)
			valider(respPTLineStructTimetableType.getICT(i));
		for (int i = 0; i < respPTLineStructTimetableType.getTimetablesCount(); i++)
			valider(respPTLineStructTimetableType.getTimetable(i));
		for (int i = 0; i < respPTLineStructTimetableType.getJourneyPatternsCount(); i++)
			valider(respPTLineStructTimetableType.getJourneyPattern(i));
		for (int i = 0; i < respPTLineStructTimetableType.getVehicleJourneysCount(); i++)
			valider(respPTLineStructTimetableType.getVehicleJourney(i));
	}
	
	public void construitDictionaires() {
		for (int i = 0;i < respPTLineStructTimetableType.getStopPointsCount(); i++)
			stopPointsByObjectId.put(respPTLineStructTimetableType.getStopPoint(i).getObjectId().toString(), respPTLineStructTimetableType.getStopPoint(i));
		for (int i = 0;i < respPTLineStructTimetableType.getAccessPointsCount(); i++)
			accessPointsByObjectId.put(respPTLineStructTimetableType.getAccessPoint(i).getObjectId().toString(), respPTLineStructTimetableType.getAccessPoint(i));
		for (int i = 0;i < respPTLineStructTimetableType.getStopPointInConnectionsCount(); i++)
			stopPointInConnectionsByObjectId.put(respPTLineStructTimetableType.getStopPointInConnection(i).getObjectId().toString(), respPTLineStructTimetableType.getStopPointInConnection(i));
		for (int i = 0;i < respPTLineStructTimetableType.getStopAreasCount(); i++)
			stopAreasByObjectId.put(respPTLineStructTimetableType.getStopArea(i).getObjectId().toString(), respPTLineStructTimetableType.getStopArea(i));
		for (int i = 0;i < respPTLineStructTimetableType.getRoutesCount(); i++)
			routesByObjectId.put(respPTLineStructTimetableType.getRoute(i).getObjectId().toString(), respPTLineStructTimetableType.getRoute(i));
		for (int i = 0; i < respPTLineStructTimetableType.getPTLinksCount(); i++)
			pTLinksByObjectId.put(respPTLineStructTimetableType.getPTLink(i).getObjectId().toString(), respPTLineStructTimetableType.getPTLink(i));
		for (int i = 0;i < respPTLineStructTimetableType.getJourneyPatternsCount(); i++)
			journeyPatternsByObjectId.put(respPTLineStructTimetableType.getJourneyPattern(i).getObjectId().toString(), respPTLineStructTimetableType.getJourneyPattern(i));
		for (int i = 0;i < respPTLineStructTimetableType.getVehicleJourneysCount(); i++)
			vehicleJourneysByObjectId.put(respPTLineStructTimetableType.getVehicleJourney(i).getObjectId().toString(), respPTLineStructTimetableType.getVehicleJourney(i));
	}
	
	private void valider(TransportNetwork transportNetwork) {
		if (transportNetwork == null)
			return;
		Line line = respPTLineStructTimetableType.getLine();
		if (line == null)
			return;
		for (int i = 0; i < transportNetwork.getLineIdsCount(); i++) {
			if (transportNetwork.getLineId(i).equals(line.getObjectId().toString())) {
				transportNetwork.removeLineId(i);
				transportNetwork.addLine(line);
				break;
			}
			if (i == transportNetwork.getLineIdsCount()-1)
				getValidationException().add(TypeInvalidite.InvalidLineId_TransportNetwork, "La liste des \"lineId\" ne contient pas l'identifiant de \"Line\" ("+line.getObjectId().toString()+").");
		}
	}
	
	private void valider(GroupOfLine groupOfLine) {
		if (groupOfLine == null)
			return;
		Line line = respPTLineStructTimetableType.getLine();
		if (line == null)
			return;
		for (int i = 0; i < groupOfLine.getLineIdsCount(); i++) {
			if (groupOfLine.getLineId(i).equals(line.getObjectId().toString())) {
				groupOfLine.removeLineId(i);
				groupOfLine.addLine(line);
				break;
			}
			if (i == groupOfLine.getLineIdsCount()-1)
				getValidationException().add(TypeInvalidite.InvalidLineId_GroupOfLine, "La liste des \"lineId\" ne contient pas l'identifiant de \"Line\" ("+line.getObjectId().toString()+").");
		}		
	}
	
	private void valider(Line line) {
		if (line == null)
			return;
		for (int i = 0; i < line.getLineEndIdsCount(); i++) {
			StopPoint stopPoint = stopPointsByObjectId.get(line.getLineEndId(i));
			if (stopPoint == null)
				getValidationException().add(TypeInvalidite.InvalidLineEnd_Line, "Le \"lineEnd\" ("+line.getLineEndId(i)+") de la \"Line\" ("+line.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
			else {
				line.addLineEnd(stopPoint);
				line.removeLineEnd(i);
			}
		}
		for (int i = 0; i < line.getRoutesCount(); i++) {
			Route route = routesByObjectId.get(line.getRouteId(i));
			if (route == null)
				getValidationException().add(TypeInvalidite.InvalidRouteId_Line, "Le \"routeId\" ("+line.getRouteId(i)+") de la \"Line\" ("+line.getObjectId().toString()+") ne correspond a auncune \"Route\".");
			else {
				line.addRoute(route);
				line.removeRouteId(i);
			}
		}
		if (line.getPTNetworkIdShortcut() != null)
			if (line.getPTNetworkIdShortcut().equals(respPTLineStructTimetableType.getTransportNetwork().getObjectId().toString())) {
				line.setTransportNetwork(respPTLineStructTimetableType.getTransportNetwork());
			}
	}

	private void valider(StopArea stopArea) {
		if (stopArea == null)
			return;
		for (int i = 0; i < stopArea.getContainsCount(); i++) {
			StopPoint stopPoint = stopPointsByObjectId.get(stopArea.getContain(i));
			AccessPoint accessPoint = accessPointsByObjectId.get(stopArea.getContain(i));
			StopPointInConnection stopPointInConnection = stopPointInConnectionsByObjectId.get(stopArea.getContain(i));
			if ((stopPoint == null) && (accessPoint == null) && (stopPointInConnection == null))
				getValidationException().add(TypeInvalidite.InvalidContain_StopArea, "Le \"contains\" ("+stopArea.getContain(i)+") de la \"StopArea\" ("+stopArea.getObjectId().toString()+") ne correspond a auncun \"StopPoint\" ou \"AccessPoint\" ou \"StopPointInConnection\".");
			else if (((stopPoint != null) && (accessPoint != null)) || ((stopPointInConnection != null) && (accessPoint != null))|| ((stopPointInConnection != null) && (stopPoint != null)))
				getValidationException().add(TypeInvalidite.InvalidContain_StopArea, "Le \"contains\" ("+stopArea.getContain(i)+") de la \"StopArea\" ("+stopArea.getObjectId().toString()+") correspond a deux element de types \"StopPoint\", \"AccessPoint\" et \"StopPointInConnection\".");
			else if (stopPoint != null) {
				stopArea.addStopPoint(stopPoint);
				stopArea.removeContain(i);
			}
			else if (accessPoint != null) {
				stopArea.addAccessPoint(accessPoint);
				stopArea.removeContain(i);				
			}
			else {
				stopArea.addStopPointInConnection(stopPointInConnection);
				stopArea.removeContain(i);				
			}
		}
	}

	private void valider(StopPoint stopPoint) {
		if (stopPoint == null)
			return;
		Line line = respPTLineStructTimetableType.getLine();
		TransportNetwork transportNetwork = respPTLineStructTimetableType.getTransportNetwork();
		if (line != null)
			if (stopPoint.getLineIdShortcut() != null)
				if (stopPoint.getLineIdShortcut().equals(line.getObjectId().toString()))
					stopPoint.setLine(line);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_StopPoint, "Le \"lineIdShortcut\" ("+stopPoint.getLineIdShortcut()+") du \"StopPoint\" ("+stopPoint.getObjectId().toString()+") ne correspond pas a l'identifiant de la \"Line\" ("+line.getObjectId().toString()+").");
		if (transportNetwork != null)
			if (stopPoint.getPTNetworkIdShortcut() != null)
				if (stopPoint.getPTNetworkIdShortcut().equals(transportNetwork.getObjectId().toString()))
					stopPoint.setTransportNetwork(transportNetwork);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_StopPoint, "Le \"ptNetworkIdShortcut\" ("+stopPoint.getPTNetworkIdShortcut()+") du \"StopPoint\" ("+stopPoint.getObjectId().toString()+") ne correspond pas a l'identifiant au \"TransportNetwork\" ("+transportNetwork.getObjectId().toString()+").");
		for (int i = 0; i < stopPoint.getContainedInsCount(); i++) {
			StopArea stopArea = stopAreasByObjectId.get(stopPoint.getContainedIn(i));
			if (stopArea == null)
				getValidationException().add(TypeInvalidite.InvalidContain_StopPoint, "Le \"containedIn\" ("+stopPoint.getContainedIn(i)+") du \"StopPoint\" ("+stopPoint.getObjectId().toString()+") ne correspond a auncun \"StopArea\".");
			else {
				stopPoint.addStopArea(stopArea);
				stopPoint.removeContainedIn(i);
			}
		}
	}

	private void valider(PTLink pTLink) {
		if (pTLink == null)
			return;
		StopPoint startStopPoint = stopPointsByObjectId.get(pTLink.getStartOfLinkId());
		StopPoint endStopPoint = stopPointsByObjectId.get(pTLink.getEndOfLinkId());
		if (startStopPoint == null)
			getValidationException().add(TypeInvalidite.InvalidStartOfLink_PTLink, "Le \"startOfLinkId\" ("+pTLink.getStartOfLinkId()+") du \"PTLink\" ("+pTLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
		else
			pTLink.setStartOfLink(startStopPoint);
		if (endStopPoint == null)
			getValidationException().add(TypeInvalidite.InvalidEndOfLink_PTLink, "Le \"endOfLinkId\" ("+pTLink.getEndOfLinkId()+") du \"PTLink\" ("+pTLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
		else
			pTLink.setEndOfLink(endStopPoint);
	}

	private void valider(Route route) {
		if (route == null)
			return;
		for (int i = 0; i < route.getPTLinkIdsCount(); i++) {
			PTLink pTLink = pTLinksByObjectId.get(route.getPTLinkId(i));
			if (pTLink == null)
				getValidationException().add(TypeInvalidite.InvalidPTLinkId_Route, "Le \"ptLinkId\" ("+route.getPTLinkId(i)+") de la \"Route\" ("+route.getObjectId().toString()+") ne correspond a auncun \"PTLink\".");
			else {
				route.addPTLink(pTLink);
				route.removePTLinkId(i);
			}
		}
		for (int i = 0; i < route.getJourneyPatternIdsCount(); i++) {
			JourneyPattern journeyPattern = journeyPatternsByObjectId.get(route.getJourneyPatternId(i));
			if (journeyPattern == null)
				getValidationException().add(TypeInvalidite.InvalidJourneyPatternId_Route, "Le \"journeyPatternId\" ("+route.getJourneyPatternId(i)+") de la \"Route\" ("+route.getObjectId().toString()+") ne correspond a auncun \"JourneyPattern\".");
			else {
				route.addJourneyPattern(journeyPattern);
				route.removeJourneyPatternId(i);
			}
		}
		if (route.getWayBackRouteId() != null) {
			Route wayBackRoute = routesByObjectId.get(route.getWayBackRouteId());
			if (wayBackRoute == null)
				getValidationException().add(TypeInvalidite.InvalidWayBackRouteId_Route, "La \"wayBackRouteId\" ("+route.getWayBackRouteId()+") de la \"Route\" ("+route.getObjectId().toString()+") ne correspond a auncune \"Route\".");
			else
				route.setWayBackRoute(wayBackRoute);
		}
	}

	private void valider(SubLine subLine) {
		if (subLine == null)
			return;
		Line line = respPTLineStructTimetableType.getLine();
		if (line != null)
			if (((line.getName()!= null) && (!line.getName().equals(subLine.getLineName()))) || (!line.getObjectId().toString().equals(subLine.getLineId())))
				getValidationException().add(TypeInvalidite.InvalidLineNameId_SubLine, "La \"lineName\" ("+subLine.getLineName()+") ou le \"lineId\" ("+subLine.getLineId()+") de la \"SubLine\" ("+subLine.getObjectId().toString()+") ne correspond pas a la \"Line\" ("+line.getObjectId().toString()+").");
			else
				subLine.setLine(line);
		for (int i = 0; i < subLine.getRouteIdsCount(); i++) {
			Route route = routesByObjectId.get(subLine.getRouteId(i));
			if (route == null)
				getValidationException().add(TypeInvalidite.InvalidRouteId_SubLine, "Le \"routeId\" ("+subLine.getRouteId(i)+") de la \"SubLine\" ("+subLine.getObjectId().toString()+") ne correspond pas a aucune \"Route\".");
			else {
				subLine.addRoute(route);
				subLine.removeRouteId(i);
			}
		}
	}
	
	private void valider(AccessPoint accessPoint) {
		if (accessPoint == null)
			return;
		for (int i = 0; i < accessPoint.getContainedInsCount(); i++) {
			StopArea stopArea = stopAreasByObjectId.get(accessPoint.getContainedIn(i));
			if (stopArea == null)
				getValidationException().add(TypeInvalidite.InvalidContain_AccessPoint, "Le \"containedIn\" ("+accessPoint.getContainedIn(i)+") du \"AccessPoint\" ("+accessPoint.getObjectId().toString()+") ne correspond a auncun \"StopArea\".");
			else {
				accessPoint.addStopArea(stopArea);
				accessPoint.removeContainedIn(i);
			}
		}
	}
	
	private void valider(PTAccessLink pTAccessLink) {
		if (pTAccessLink == null)
			return;
		StopPoint startStopPoint = stopPointsByObjectId.get(pTAccessLink.getStartOfLinkId());
		StopPoint endStopPoint = stopPointsByObjectId.get(pTAccessLink.getEndOfLinkId());
		if (startStopPoint == null)
			getValidationException().add(TypeInvalidite.InvalidStartOfLink_PTAccessLink, "Le \"startOfLinkId\" ("+pTAccessLink.getStartOfLinkId()+") du \"PTAccessLink\" ("+pTAccessLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
		else
			pTAccessLink.setStartOfLink(startStopPoint);
		if (endStopPoint == null)
			getValidationException().add(TypeInvalidite.InvalidEndOfLink_PTAccessLink, "Le \"endOfLinkId\" ("+pTAccessLink.getEndOfLinkId()+") du \"PTAccessLink\" ("+pTAccessLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
		else
			pTAccessLink.setEndOfLink(endStopPoint);
	}
	
	private void valider(StopPointInConnection stopPointInConnection) {
		if (stopPointInConnection == null)
			return;
		Line line = respPTLineStructTimetableType.getLine();
		TransportNetwork transportNetwork = respPTLineStructTimetableType.getTransportNetwork();
		if (line != null)
			if (stopPointInConnection.getLineIdShortcut() != null)
				if (stopPointInConnection.getLineIdShortcut().equals(line.getObjectId().toString()))
					stopPointInConnection.setLine(line);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_StopPointInConnection, "Le \"lineIdShortcut\" ("+stopPointInConnection.getLineIdShortcut()+") du \"StopPointInConnection\" ("+stopPointInConnection.getObjectId().toString()+") ne correspond pas a l'identifiant de la \"Line\" ("+line.getObjectId().toString()+").");
		if (transportNetwork != null)
			if (stopPointInConnection.getPTNetworkIdShortcut() != null)
				if (stopPointInConnection.getPTNetworkIdShortcut().equals(transportNetwork.getObjectId().toString()))
					stopPointInConnection.setTransportNetwork(transportNetwork);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_StopPointInConnection, "Le \"ptNetworkIdShortcut\" ("+stopPointInConnection.getPTNetworkIdShortcut()+") du \"StopPointInConnection\" ("+stopPointInConnection.getObjectId().toString()+") ne correspond pas a l'identifiant au \"TransportNetwork\" ("+transportNetwork.getObjectId().toString()+").");
		for (int i = 0; i < stopPointInConnection.getContainedInsCount(); i++) {
			StopArea stopArea = stopAreasByObjectId.get(stopPointInConnection.getContainedIn(i));
			if (stopArea == null)
				getValidationException().add(TypeInvalidite.InvalidContain_StopPointInConnection, "Le \"containedIn\" ("+stopPointInConnection.getContainedIn(i)+") du \"StopPointInConnection\" ("+stopPointInConnection.getObjectId().toString()+") ne correspond a auncun \"StopArea\".");
			else {
				stopPointInConnection.addStopArea(stopArea);
				stopPointInConnection.removeContainedIn(i);
			}
		}
	}
	
	private void valider(ConnectionLink connectionLink) {
		if (connectionLink == null)
			return;
		StopPoint startStopPoint = stopPointsByObjectId.get(connectionLink.getStartOfLinkId());
		StopPoint endStopPoint = stopPointsByObjectId.get(connectionLink.getEndOfLinkId());
		StopPointInConnection startStopPointInConnection = stopPointInConnectionsByObjectId.get(connectionLink.getStartOfLinkId());
		StopPointInConnection endStopPointInConnection = stopPointInConnectionsByObjectId.get(connectionLink.getEndOfLinkId());
		if ((startStopPoint == null) && (startStopPointInConnection == null))
			getValidationException().add(TypeInvalidite.InvalidStartOfLink_ConnectionLink, "Le \"startOfLinkId\" ("+connectionLink.getStartOfLinkId()+") du \"ConnectionLink\" ("+connectionLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\" ou \"StopPointInConnection\".");
		else if (startStopPoint == null)
			connectionLink.setStartOfLinkInConnection(startStopPointInConnection);
		else
			connectionLink.setStartOfLink(startStopPoint);
		if ((endStopPoint == null) && (endStopPointInConnection == null))
			getValidationException().add(TypeInvalidite.InvalidEndOfLink_ConnectionLink, "Le \"endOfLinkId\" ("+connectionLink.getEndOfLinkId()+") du \"ConnectionLink\" ("+connectionLink.getObjectId().toString()+") ne correspond a auncun \"StopPoint\" ou \"StopPointInConnection\".");
		else if (endStopPoint == null)
			connectionLink.setEndOfLinkInConnection(endStopPointInConnection);
		else
			connectionLink.setEndOfLink(endStopPoint);
	}
	
	private void valider(ICT iCT) {
		if (iCT == null)
			return;
		Route route = routesByObjectId.get(iCT.getRouteId());
		if (route == null)
			getValidationException().add(TypeInvalidite.InvalidRouteId_ICT, "Le \"routeId\" ("+iCT.getRouteId()+") de l'\"ICT\" ("+iCT.getObjectId().toString()+") ne correspond pas a aucune \"Route\".");
		else
			iCT.setRoute(route);
		StopPoint stopPoint0 = stopPointsByObjectId.get(iCT.getStopPointId()[0]);
		StopPoint stopPoint1 = stopPointsByObjectId.get(iCT.getStopPointId()[1]);
		if ((stopPoint0 == null) || (stopPoint0 == null))
			getValidationException().add(TypeInvalidite.InvalidStopPointIds_ICT, "Un des deux \"stopPointId\" ("+iCT.getStopPointId()[0]+","+iCT.getStopPointId()[1]+") de l'\"ICT\" ("+iCT.getObjectId().toString()+") ne correspond pas a aucun \"StopPoint\".");
		else {
			StopPoint[] stopPoints = {stopPoint0, stopPoint1};
			iCT.setStopPoint(stopPoints);
		}
		if (iCT.getVehicleJourneyId() == null)
			return;
		VehicleJourney vehicleJourney = vehicleJourneysByObjectId.get(iCT.getVehicleJourneyId());
		if (vehicleJourney == null)
			getValidationException().add(TypeInvalidite.InvalidVehicleJourneyIds_ICT, "Le \"vehicleJourneyId\" ("+iCT.getVehicleJourneyId()+") de l'\"ICT\" ("+iCT.getObjectId().toString()+") ne correspond pas a aucun \"VehicleJourney\".");
		else
			iCT.setVehicleJourney(vehicleJourney);
	}
	
	private void valider(Timetable timetable) {
		if (timetable == null)
			return;
		for (int i = 0; i < timetable.getStopPointIdsCount(); i++) {
			StopPoint stopPoint = stopPointsByObjectId.get(timetable.getStopPointId(i));
			if (stopPoint == null)
				getValidationException().add(TypeInvalidite.InvalidStopPointIds_Timetable, "Le \"stopPointId\" ("+timetable.getStopPointId(i)+") de la \"Timetable\" ("+timetable.getObjectId().toString()+") ne correspond a auncun \"StopPoint\".");
			else {
				timetable.addStopPoint(stopPoint);
				timetable.removeStopPointId(i);
			}
		}
		for (int i = 0; i < timetable.getVehicleJourneyIdsCount(); i++) {
			VehicleJourney vehicleJourney = vehicleJourneysByObjectId.get(timetable.getVehicleJourneyId(i));
			if (vehicleJourney == null)
				getValidationException().add(TypeInvalidite.InvalidVehicleJourneyIds_Timetable, "Le \"vehicleJourneyId\" ("+timetable.getVehicleJourneyId(i)+") de la \"Timetable\" ("+timetable.getObjectId().toString()+") ne correspond pas a aucun \"VehicleJourney\".");
			else {
				timetable.addVehicleJourney(vehicleJourney);
				timetable.removeVehicleJourneyId(i);
			}
		}
	}
	
	private void valider(JourneyPattern journeyPattern) {
		if (journeyPattern == null)
			return;
		Route route = routesByObjectId.get(journeyPattern.getRouteId());
		if (route == null)
			getValidationException().add(TypeInvalidite.InvalidRouteId_JourneyPattern, "Le \"routeId\" ("+journeyPattern.getRouteId()+") de la \"JourneyPattern\" ("+journeyPattern.getObjectId().toString()+") ne correspond pas a aucune \"Route\".");
		else
			journeyPattern.setRoute(route);
		if (journeyPattern.getOrigin() != null) {
			StopPoint originStopPoint = stopPointsByObjectId.get(journeyPattern.getOrigin());
			if (originStopPoint == null)
				getValidationException().add(TypeInvalidite.InvalidOrigin_JourneyPattern, "L'\"origin\" ("+journeyPattern.getOrigin()+") de la \"JourneyPattern\" ("+journeyPattern.getObjectId().toString()+") ne correspond pas a aucune \"StopPoint\".");
			else
				journeyPattern.setOriginStopPoint(originStopPoint);
		}
		if (journeyPattern.getDestination() != null) {
			StopPoint destinationStopPoint = stopPointsByObjectId.get(journeyPattern.getDestination());
			if (destinationStopPoint == null)
				getValidationException().add(TypeInvalidite.InvalidOrigin_JourneyPattern, "La \"destination\" ("+journeyPattern.getDestination()+") de la \"JourneyPattern\" ("+journeyPattern.getObjectId().toString()+") ne correspond pas a aucune \"StopPoint\".");
			else
				journeyPattern.setOriginStopPoint(destinationStopPoint);
		}
		for (int i = 0; i < journeyPattern.getStopPointIdsCount(); i++) {
			StopPoint stopPoint = stopPointsByObjectId.get(journeyPattern.getStopPointId(i));
			if (stopPoint == null)
				getValidationException().add(TypeInvalidite.InvalidStopPointId_JourneyPattern, "Le \"stopPointId\" ("+journeyPattern.getStopPointId(i)+") de la \"JourneyPattern\" ("+journeyPattern.getObjectId().toString()+") ne correspond pas a aucune \"StopPoint\".");
			else {
				journeyPattern.addStopPoint(stopPoint);
				journeyPattern.removeStopPointId(i);
			}
		}
		Line line = respPTLineStructTimetableType.getLine();
		if (line != null)
			if (journeyPattern.getLineIdShortcut() != null)
				if (journeyPattern.getLineIdShortcut().equals(line.getObjectId().toString()))
					journeyPattern.setLine(line);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_JourneyPattern, "Le \"lineIdShortcut\" ("+journeyPattern.getLineIdShortcut()+") du \"JourneyPattern\" ("+journeyPattern.getObjectId().toString()+") ne correspond pas a l'identifiant de la \"Line\" ("+line.getObjectId().toString()+").");
	}
	
	private void valider(VehicleJourney vehicleJourney) {
		if (vehicleJourney == null)
			return;
		Route route = routesByObjectId.get(vehicleJourney.getRouteId());
		if (route == null)
			getValidationException().add(TypeInvalidite.InvalidRouteId_VehicleJourney, "Le \"routeId\" ("+vehicleJourney.getRouteId()+") de la \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a aucune \"Route\".");
		else
			vehicleJourney.setRoute(route);
		if (vehicleJourney.getJourneyPatternId() != null) {
			JourneyPattern journeyPattern = journeyPatternsByObjectId.get(vehicleJourney.getJourneyPatternId());
			if (journeyPattern == null)
				getValidationException().add(TypeInvalidite.InvalidJourneyPatternId_VehicleJourney, "Le \"journeyPatternId\" ("+vehicleJourney.getJourneyPatternId()+") de la \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a aucune \"JourneyPattern\".");
			else
				vehicleJourney.setJourneyPattern(journeyPattern);
		}
		Line line = respPTLineStructTimetableType.getLine();
		if (line != null)
			if (vehicleJourney.getLineIdShortcut() != null)
				if (vehicleJourney.getLineIdShortcut().equals(line.getObjectId().toString()))
					vehicleJourney.setLine(line);
				else
					getValidationException().add(TypeInvalidite.InvalidLineIdShortcut_VehicleJourney, "Le \"lineIdShortcut\" ("+vehicleJourney.getLineIdShortcut()+") du \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a l'identifiant de la \"Line\" ("+line.getObjectId().toString()+").");
		if (vehicleJourney.getRouteIdShortcut() != null)
			if (route != routesByObjectId.get(vehicleJourney.getRouteIdShortcut()))
				getValidationException().add(TypeInvalidite.InvalidRouteIdShortcut_VehicleJourney, "Le \"routeIdShortcut\" ("+vehicleJourney.getRouteIdShortcut()+") de la \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a la \"Route\" ("+route.getObjectId().toString()+").");
		for (int i = 0; i < vehicleJourney.getVehicleJourneyAtStopsCount(); i++) {
			VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStop(i);
			if (vehicleJourneyAtStop != null) {
				StopPoint stopPoint = stopPointsByObjectId.get(vehicleJourneyAtStop.getStopPointId());
				if (stopPoint == null)
					getValidationException().add(TypeInvalidite.InvalidStopPointId_VehicleJourneyAtStop, "Le \"stopPointId\" ("+vehicleJourneyAtStop.getStopPointId()+") d'une \"VehicleJourneyAtStop\" de la \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a aucun \"StopPoint\".");
				else
					vehicleJourneyAtStop.setStopPoint(stopPoint);
				if (vehicleJourneyAtStop.getVehicleJourneyId() != null)
					if (vehicleJourneyAtStop.getVehicleJourneyId().equals(vehicleJourney.getObjectId().toString()))
						vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
					else
						getValidationException().add(TypeInvalidite.InvalidVehicleJourneyId_VehicleJourneyAtStop, "Le \"vehicleJourneyId\" ("+vehicleJourneyAtStop.getVehicleJourneyId()+") d'une \"VehicleJourneyAtStop\" de la \"VehicleJourney\" ("+vehicleJourney.getObjectId().toString()+") ne correspond pas a la \"VehicleJourney\".");
			}
		}
	}
}
