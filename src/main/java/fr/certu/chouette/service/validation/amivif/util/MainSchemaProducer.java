package fr.certu.chouette.service.validation.amivif.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.service.validation.amivif.AccessPoint;
import fr.certu.chouette.service.validation.amivif.Company;
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
import fr.certu.chouette.service.validation.amivif.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.amivif.commun.ValidationException;

public class MainSchemaProducer {
	
	private static final Logger				logger							= Logger.getLogger(MainSchemaProducer.class);
    private ValidationException				validationException				= new ValidationException();
    private TransportNetworkProducer		transportNetworkProducer		= new TransportNetworkProducer(validationException);
    private CompanyProducer					companyProducer					= new CompanyProducer(validationException);
    private GroupOfLineProducer				groupOfLineProducer				= new GroupOfLineProducer(validationException);
    private LineProducer					lineProducer					= new LineProducer(validationException);
    private StopAreaProducer				stopAreaProducer				= new StopAreaProducer(validationException);
    private StopPointProducer				stopPointProducer				= new StopPointProducer(validationException);
    private PTLinkProducer					pTLinkProducer					= new PTLinkProducer(validationException);
    private RouteProducer					routeProducer					= new RouteProducer(validationException);
    private SubLineProducer					subLineProducer					= new SubLineProducer(validationException);
    private AccessPointProducer				accessPointProducer				= new AccessPointProducer(validationException);
    private PTAccessLinkProducer			pTAccessLinkProducer			= new PTAccessLinkProducer(validationException);
    private StopPointInConnectionProducer	stopPointInConnectionProducer	= new StopPointInConnectionProducer(validationException);
    private ConnectionLinkProducer			connectionLinkProducer			= new ConnectionLinkProducer(validationException);
    private ICTProducer						iCTProducer						= new ICTProducer(validationException);
    private TimetableProducer				timetableProducer				= new TimetableProducer(validationException);
    private JourneyPatternProducer			journeyPatternProducer			= new JourneyPatternProducer(validationException);
    private VehicleJourneyProducer			vehicleJourneyProducer			= new VehicleJourneyProducer(validationException);
    
    public void setValidationException(ValidationException validationException) {
		this.validationException = validationException;
	}
	
	public ValidationException getValidationException() {
		return validationException;
	}
	
	/**
	 * Construit le Graphe Syntaxique Abstrait (ASG : Abstract Syntaxic Graph) de AMIVIF.
	 * @param castorRespPTLineStructTimetable
	 * @return
	 */
	public RespPTLineStructTimetableType getASG(amivif.schema.RespPTLineStructTimetable castorRespPTLineStructTimetable)  {
		logger.info("DEBUT DE LA GENERATION DE L'ASG.");
		if (castorRespPTLineStructTimetable == null)
			return null;
		RespPTLineStructTimetableType respPTLineStructTimetableType = new RespPTLineStructTimetableType();
		
		// TransportNetwork optionnel
		amivif.schema.TransportNetwork castorTransportNetwork = castorRespPTLineStructTimetable.getTransportNetwork();
		if (castorTransportNetwork != null) {
			TransportNetwork transportNetwork = transportNetworkProducer.getASG(castorTransportNetwork);
			respPTLineStructTimetableType.setTransportNetwork(transportNetwork);
			transportNetwork.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
		}
		
		// Company 1..w
		Set<String> aSet = new HashSet<String>();
		amivif.schema.Company[] castorCompanies = castorRespPTLineStructTimetable.getCompany();
		if ((castorCompanies == null) || (castorCompanies.length < 1))
			validationException.add(TypeInvalidite.NoCompany_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"Company\".");
		else
			for (int i = 0; i < castorCompanies.length; i++) {
				Company company = companyProducer.getASG(castorCompanies[i]);
				if (!aSet.add(company.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"Company\" ont le meme \"objectId\" ("+company.getObjectId().toString()+").");
				respPTLineStructTimetableType.addCompany(company);
				company.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// GroupOfLine 0..w
		aSet = new HashSet<String>();
		amivif.schema.GroupOfLine[] castorGroupOfLines = castorRespPTLineStructTimetable.getGroupOfLine();
		if (castorGroupOfLines != null)
			for (int i = 0; i < castorGroupOfLines.length; i++) {
				GroupOfLine groupOfLine = groupOfLineProducer.getASG(castorGroupOfLines[i]);
				if (!aSet.add(groupOfLine.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"GroupOfLine\" ont le meme \"objectId\" ("+groupOfLine.getObjectId().toString()+").");
				respPTLineStructTimetableType.addGroupOfLine(groupOfLine);
				groupOfLine.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// Line obligatoire
		amivif.schema.Line castorLine = castorRespPTLineStructTimetable.getLine();
		if (castorLine == null)
			validationException.add(TypeInvalidite.NoLine_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"Line\".");
		else {
			Line line = lineProducer.getASG(castorLine);
			respPTLineStructTimetableType.setLine(line);
			line.setRespPTLineStructTimetableType(respPTLineStructTimetableType);
		}
		
		// StopArea 0..w
		aSet = new HashSet<String>();
		amivif.schema.StopArea[] castorStopAreas = castorRespPTLineStructTimetable.getStopArea();
		if (castorStopAreas != null)
			for (int i = 0; i < castorStopAreas.length; i++) {
				StopArea stopArea = stopAreaProducer.getASG(castorStopAreas[i]);
				if (!aSet.add(stopArea.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"StopArea\" ont le meme \"objectId\" ("+stopArea.getObjectId().toString()+").");
				respPTLineStructTimetableType.addStopArea(stopArea);
				stopArea.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// StopPoint 2..w
		aSet = new HashSet<String>();
		amivif.schema.StopPoint[] castorStopPoints = castorRespPTLineStructTimetable.getStopPoint();
		if ((castorStopPoints == null) || (castorStopPoints.length < 2))
			validationException.add(TypeInvalidite.UnvalidNumberOfStopPoints_RespPTLineStructTimetableType, "Ce reseau n'a pas suffisament de \"StopPoint\".");
		else
			for (int i = 0; i < castorStopPoints.length; i++) {
				StopPoint stopPoint = stopPointProducer.getASG(castorStopPoints[i]);
				if (!aSet.add(stopPoint.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"StopPoint\" ont le meme \"objectId\" ("+stopPoint.getObjectId().toString()+").");
				respPTLineStructTimetableType.addStopPoint(stopPoint);
				stopPoint.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// PTLink 1..w
		aSet = new HashSet<String>();
		amivif.schema.PTLink[] castorPTLinks = castorRespPTLineStructTimetable.getPTLink();
		if ((castorPTLinks == null) || (castorPTLinks.length < 1))
			validationException.add(TypeInvalidite.NoPTLink_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"PTLink\".");
		else
			for (int i = 0; i < castorPTLinks.length; i++) {
				PTLink pTLink = pTLinkProducer.getASG(castorPTLinks[i]);
				if (!aSet.add(pTLink.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"PTLink\" ont le meme \"objectId\" ("+pTLink.getObjectId().toString()+").");
				respPTLineStructTimetableType.addPTLink(pTLink);
				pTLink.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// Route 1..w
		aSet = new HashSet<String>();
		amivif.schema.Route[] castorRoutes = castorRespPTLineStructTimetable.getRoute();
		if ((castorRoutes == null) || (castorRoutes.length < 1))
			validationException.add(TypeInvalidite.NoRoute_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"Route\".");
		else
			for (int i = 0; i < castorRoutes.length; i++) {
				Route route = routeProducer.getASG(castorRoutes[i]);
				if (!aSet.add(route.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"Route\" ont le meme \"objectId\" ("+route.getObjectId().toString()+").");
				respPTLineStructTimetableType.addRoute(route);
				route.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// SubLine 0..w
		aSet = new HashSet<String>();
		amivif.schema.SubLine[] castorSubLines = castorRespPTLineStructTimetable.getSubLine();
		if (castorSubLines != null)
			for (int i = 0; i < castorSubLines.length; i++) {
				SubLine subLine = subLineProducer.getASG(castorSubLines[i]);
				if (!aSet.add(subLine.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"SubLine\" ont le meme \"objectId\" ("+subLine.getObjectId().toString()+").");
				respPTLineStructTimetableType.addSubLine(subLine);
				subLine.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// AccessPoint 0..w
		aSet = new HashSet<String>();
		amivif.schema.AccessPoint[] castorAccessPoints = castorRespPTLineStructTimetable.getAccessPoint();
		if (castorAccessPoints != null)
			for (int i = 0; i < castorAccessPoints.length; i++) {
				AccessPoint accessPoint = accessPointProducer.getASG(castorAccessPoints[i]);
				if (!aSet.add(accessPoint.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"AccessPoint\" ont le meme \"objectId\" ("+accessPoint.getObjectId().toString()+").");
				respPTLineStructTimetableType.addAccessPoint(accessPoint);
				accessPoint.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// PTAccessLink 0..w
		aSet = new HashSet<String>();
		amivif.schema.PTAccessLink[] castorPTAccessLinks = castorRespPTLineStructTimetable.getPTAccessLink();
		if (castorPTAccessLinks != null)
			for (int i = 0; i < castorPTAccessLinks.length; i++) {
				PTAccessLink pTAccessLink = pTAccessLinkProducer.getASG(castorPTAccessLinks[i]);
				if (!aSet.add(pTAccessLink.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"PTAccessLink\" ont le meme \"objectId\" ("+pTAccessLink.getObjectId().toString()+").");
				respPTLineStructTimetableType.addPTAccessLink(pTAccessLink);
				pTAccessLink.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// StopPointInConnection 0..w
		aSet = new HashSet<String>();
		amivif.schema.StopPointInConnection[] castorStopPointInConnections = castorRespPTLineStructTimetable.getStopPointInConnection();
		if (castorStopPointInConnections != null)
			for (int i = 0; i < castorStopPointInConnections.length; i++) {
				StopPointInConnection stopPointInConnection = stopPointInConnectionProducer.getASG(castorStopPointInConnections[i]);
				if (!aSet.add(stopPointInConnection.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"StopPointInConnection\" ont le meme \"objectId\" ("+stopPointInConnection.getObjectId().toString()+").");
				respPTLineStructTimetableType.addStopPointInConnection(stopPointInConnection);
				stopPointInConnection.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// ConnectionLink 0..w
		aSet = new HashSet<String>();
		amivif.schema.ConnectionLink[] castorConnectionLinks = castorRespPTLineStructTimetable.getConnectionLink();
		if (castorConnectionLinks != null)
			for (int i = 0; i < castorConnectionLinks.length; i++) {
				ConnectionLink connectionLink = connectionLinkProducer.getASG(castorConnectionLinks[i]);
				if (!aSet.add(connectionLink.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"ConnectionLink\" ont le meme \"objectId\" ("+connectionLink.getObjectId().toString()+").");
				respPTLineStructTimetableType.addConnectionLink(connectionLink);
				connectionLink.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// ICT 0..w
		aSet = new HashSet<String>();
		amivif.schema.ICT[] castorICTs = castorRespPTLineStructTimetable.getICT();
		if (castorICTs != null)
			for (int i = 0; i < castorICTs.length; i++) {
				ICT iCT = iCTProducer.getASG(castorICTs[i]);
				if (!aSet.add(iCT.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"ICT\" ont le meme \"objectId\" ("+iCT.getObjectId().toString()+").");
				respPTLineStructTimetableType.addICT(iCT);
				iCT.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// Timetable 1..w
		aSet = new HashSet<String>();
		amivif.schema.Timetable[] castorTimetables = castorRespPTLineStructTimetable.getTimetable();
		if ((castorTimetables == null) || (castorTimetables.length < 1))
			validationException.add(TypeInvalidite.NoTimetable_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"Timetable\".");
		else
			for (int i = 0; i < castorTimetables.length; i++) {
				Timetable timetable = timetableProducer.getASG(castorTimetables[i]);
				if (!aSet.add(timetable.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"Timetable\" ont le meme \"objectId\" ("+timetable.getObjectId().toString()+").");
				respPTLineStructTimetableType.addTimetable(timetable);
				timetable.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// JourneyPattern 0..w
		aSet = new HashSet<String>();
		amivif.schema.JourneyPattern[] castorJourneyPatterns = castorRespPTLineStructTimetable.getJourneyPattern();
		if (castorJourneyPatterns != null)
			for (int i = 0; i < castorJourneyPatterns.length; i++) {
				JourneyPattern journeyPattern = journeyPatternProducer.getASG(castorJourneyPatterns[i]);
				if (!aSet.add(journeyPattern.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"JourneyPattern\" ont le meme \"objectId\" ("+journeyPattern.getObjectId().toString()+").");
				respPTLineStructTimetableType.addJourneyPattern(journeyPattern);
				journeyPattern.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		// VehicleJourney 1..w
		aSet = new HashSet<String>();
		amivif.schema.VehicleJourney[] castorVehicleJourneys = castorRespPTLineStructTimetable.getVehicleJourney();
		if ((castorVehicleJourneys == null) || (castorVehicleJourneys.length < 1))
			validationException.add(TypeInvalidite.NoVehicleJourney_RespPTLineStructTimetableType, "Ce reseau est depourvu de \"VehicleJourney\".");
		else
			for (int i = 0; i < castorVehicleJourneys.length; i++) {
				VehicleJourney vehicleJourney = vehicleJourneyProducer.getASG(castorVehicleJourneys[i]);
				if (!aSet.add(vehicleJourney.getObjectId().toString()))
					validationException.add(TypeInvalidite.MultipleTridentObject, "Plusieurs \"VehicleJourney\" ont le meme \"objectId\" ("+vehicleJourney.getObjectId().toString()+").");
				respPTLineStructTimetableType.addVehicleJourney(vehicleJourney);
				vehicleJourney.addRespPTLineStructTimetableType(respPTLineStructTimetableType);
			}
		
		(new Connecter(validationException, respPTLineStructTimetableType)).valider();
		
		logger.info("FIN DE LA GENERATION DE L'ASG.");
		
		if (validationException.getCategories() != null)
			if (validationException.getCategories().size() > 0)
				throw validationException;
		
		return respPTLineStructTimetableType;
	}
}
