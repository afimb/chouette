package mobi.chouette.exchange.netex;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ChouetteId;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.util.Referential;

public class NetexChouetteIdObjectUtil{

	public static AccessLink getAccessLink(Referential referential,
			ChouetteId chouetteId) {
		AccessLink result = referential.getSharedAccessLinks().get(chouetteId);
		if (result == null) {
			result = new AccessLink();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace(chouetteId.getCodeSpace());
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedAccessLinks().put(chouetteId, result);
		}
		if (!referential.getAccessLinks().containsKey(chouetteId)) {
			referential.getAccessLinks().put(chouetteId, result);
		}

		return result;
	}
	

	public static AccessPoint getAccessPoint(Referential referential,
			ChouetteId chouetteId) {
		AccessPoint result = referential.getSharedAccessPoints().get(chouetteId);
		if (result == null) {
			result = new AccessPoint();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace(chouetteId.getCodeSpace());
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedAccessPoints().put(chouetteId, result);
		}
		if (!referential.getAccessPoints().containsKey(chouetteId)) {
			referential.getAccessPoints().put(chouetteId, result);
		}

		return result;
	}

	public static Timetable getTimetable(Referential referential,
			ChouetteId chouetteId) {
		Timetable result = referential.getSharedTimetables().get(chouetteId);
		if (result == null) {
			result = new Timetable();
			result.setDetached(true);
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			referential.getSharedTimetables().put(chouetteId, result);
		}
		if (!referential.getTimetables().containsKey(chouetteId)) {
			referential.getTimetables().put(chouetteId, result);
		}

		return result;
	}

	public static VehicleJourneyAtStop getVehicleJourneyAtStop() {
		// TODO [DSU] object pool
		VehicleJourneyAtStop result = new VehicleJourneyAtStop();
		return result;
	}

	public static Network getPTNetwork(Referential referential,
			ChouetteId chouetteId) {
		Network result = referential.getSharedPTNetworks().get(chouetteId);
		if (result == null) {
			result = new Network();
			result.setDetached(true);
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			referential.getSharedPTNetworks().put(chouetteId, result);
		}
		if (!referential.getPtNetworks().containsKey(chouetteId)) {
			referential.getPtNetworks().put(chouetteId, result);
		}

		return result;
	}

	public static Company getCompany(Referential referential, ChouetteId chouetteId) {
		Company result = referential.getSharedCompanies().get(chouetteId);
		if (result == null) {
			result = new Company();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedCompanies().put(chouetteId, result);
		}
		if (!referential.getCompanies().containsKey(chouetteId)) {
			referential.getCompanies().put(chouetteId, result);
		}

		return result;
	}

	public static Route getRoute(Referential referential, ChouetteId chouetteId) {
		Route result = referential.getRoutes().get(chouetteId);
		if (result == null) {
			result = new Route();
			result.setDetached(true);
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			referential.getRoutes().put(chouetteId, result);
		}
		return result;
	}

	public static Line getLine(Referential referential, ChouetteId chouetteId) {
		Line result = referential.getLines().get(chouetteId);
		if (result == null) {
			result = new Line();
			result.setDetached(true);
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			referential.getLines().put(chouetteId, result);
		}
		return result;
	}

	public static JourneyPattern getJourneyPattern(Referential referential,
			ChouetteId chouetteId) {
		JourneyPattern result = referential.getJourneyPatterns().get(chouetteId);
		if (result == null) {
			result = new JourneyPattern();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getJourneyPatterns().put(chouetteId, result);
		}
		return result;
	}

	public static ConnectionLink getConnectionLink(Referential referential,
			ChouetteId chouetteId) {
		ConnectionLink result = referential.getSharedConnectionLinks().get(
				chouetteId);
		if (result == null) {
			result = new ConnectionLink();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedConnectionLinks().put(chouetteId, result);
		}
		if (!referential.getConnectionLinks().containsKey(chouetteId)) {
			referential.getConnectionLinks().put(chouetteId, result);
		}

		return result;
	}

	public static StopArea getStopArea(Referential referential, ChouetteId chouetteId) {
		StopArea result = referential.getSharedStopAreas().get(chouetteId);
		if (result == null) {
			result = new StopArea();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedStopAreas().put(chouetteId, result);
		} 
		if (!referential.getStopAreas().containsKey(chouetteId)) {
			referential.getStopAreas().put(chouetteId, result);
		}
		
		return result;
	}
	
	// Arret Netex
	public static RoutingConstraint getRoutingConstraint(Referential referential, ChouetteId chouetteId) {
		RoutingConstraint result = referential.getSharedRoutingConstraints().get(chouetteId);
		if (result == null) {
			result = new RoutingConstraint();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedRoutingConstraints().put(chouetteId, result);
		} 
		if (!referential.getRoutingConstraints().containsKey(chouetteId)) {
			referential.getRoutingConstraints().put(chouetteId, result);
		}
		
		return result;
	}

	public static GroupOfLine getGroupOfLine(Referential referential,
			ChouetteId chouetteId) {
		GroupOfLine result = referential.getSharedGroupOfLines().get(chouetteId);
		if (result == null) {
			result = new GroupOfLine();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getSharedGroupOfLines().put(chouetteId, result);
		}
		if (!referential.getGroupOfLines().containsKey(chouetteId)) {
			referential.getGroupOfLines().put(chouetteId, result);
		}

		return result;
	}

	public static StopPoint getStopPoint(Referential referential,
			ChouetteId chouetteId) {
		StopPoint result = referential.getStopPoints().get(chouetteId);
		if (result == null) {
			result = new StopPoint();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getStopPoints().put(chouetteId, result);
		}
		return result;
	}

	public static VehicleJourney getVehicleJourney(Referential referential,
			ChouetteId chouetteId) {
		VehicleJourney result = referential.getVehicleJourneys().get(chouetteId);
		if (result == null) {
			result = new VehicleJourney();
			result.setChouetteId(new ChouetteId());
			result.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			result.getChouetteId().setObjectId(chouetteId.getObjectId());
			result.setDetached(true);
			referential.getVehicleJourneys().put(chouetteId, result);
		}
		return result;
	}
	
	public static Timeband getTimeband(Referential referential, ChouetteId chouetteId) {
		Timeband timeband = referential.getTimebands().get(chouetteId);
		if (timeband == null) {
			timeband = new Timeband();
			timeband.setChouetteId(new ChouetteId());
			timeband.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			timeband.getChouetteId().setObjectId(chouetteId.getObjectId());
			timeband.setDetached(true);
			referential.getTimebands().put(chouetteId, timeband);
		}
		return timeband;
	}


	public static RouteSection getRouteSection(Referential referential, ChouetteId chouetteId) {
		RouteSection section = referential.getRouteSections().get(chouetteId);
		if (section == null) {
			section = new RouteSection();
			section.setChouetteId(new ChouetteId());
			section.getChouetteId().setCodeSpace("DEFAULT_CODESPACE");
			section.getChouetteId().setObjectId(chouetteId.getObjectId());
			section.setDetached(true);
			referential.getRouteSections().put(chouetteId, section);
		}
		return section;
	}
}
