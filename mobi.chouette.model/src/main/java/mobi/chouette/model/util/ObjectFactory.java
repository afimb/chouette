package mobi.chouette.model.util;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Block;
import mobi.chouette.model.Branding;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.DatedServiceJourney;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.DeadRunAtStop;
import mobi.chouette.model.DestinationDisplay;
import mobi.chouette.model.FootNoteAlternativeText;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Interchange;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RoutePoint;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

public class ObjectFactory {

	public static AccessLink getAccessLink(Referential referential,
			String objectId) {
		AccessLink result = referential.getSharedAccessLinks().get(objectId);
		if (result == null) {
			result = new AccessLink();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedAccessLinks().put(objectId, result);
		}
		if (!referential.getAccessLinks().containsKey(objectId)) {
			referential.getAccessLinks().put(objectId, result);
		}

		return result;
	}
	

	public static AccessPoint getAccessPoint(Referential referential,
			String objectId) {
		AccessPoint result = referential.getSharedAccessPoints().get(objectId);
		if (result == null) {
			result = new AccessPoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedAccessPoints().put(objectId, result);
		}
		if (!referential.getAccessPoints().containsKey(objectId)) {
			referential.getAccessPoints().put(objectId, result);
		}

		return result;
	}

	public static Timetable getTimetable(Referential referential,
			String objectId) {
		Timetable result = referential.getSharedTimetables().get(objectId);
		if (result == null) {
			result = new Timetable();
			result.setDetached(true);
			result.setObjectId(objectId);
			referential.getSharedTimetables().put(objectId, result);
		}
		if (!referential.getTimetables().containsKey(objectId)) {
			referential.getTimetables().put(objectId, result);
		}

		return result;
	}

	public static VehicleJourneyAtStop getVehicleJourneyAtStop(Referential referential,
			String objectId) {
		VehicleJourneyAtStop result = referential.getVehicleJourneyAtStops().get(objectId);
		if (result == null) {
			result = new VehicleJourneyAtStop();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getVehicleJourneyAtStops().put(objectId, result);
		}
		
		return result;
	}

	public static DeadRunAtStop getDeadRunAtStop(Referential referential,
												 String objectId) {
		DeadRunAtStop result = referential.getDeadRunAtStops().get(objectId);
		if (result == null) {
			result = new DeadRunAtStop();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getDeadRunAtStops().put(objectId, result);
		}

		return result;
	}

	public static Network getPTNetwork(Referential referential,
			String objectId) {
		Network result = referential.getSharedPTNetworks().get(objectId);
		if (result == null) {
			result = new Network();
			result.setDetached(true);
			result.setObjectId(objectId);
			referential.getSharedPTNetworks().put(objectId, result);
		}
		if (!referential.getPtNetworks().containsKey(objectId)) {
			referential.getPtNetworks().put(objectId, result);
		}

		return result;
	}

	public static Company getCompany(Referential referential, String objectId) {
		Company result = referential.getSharedCompanies().get(objectId);
		if (result == null) {
			result = new Company();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedCompanies().put(objectId, result);
		}
		if (!referential.getCompanies().containsKey(objectId)) {
			referential.getCompanies().put(objectId, result);
		}

		return result;
	}

	public static Route getRoute(Referential referential, String objectId) {
		Route result = referential.getRoutes().get(objectId);
		if (result == null) {
			result = new Route();
			result.setDetached(true);
			result.setObjectId(objectId);
			referential.getRoutes().put(objectId, result);
		}
		return result;
	}

	public static Line getLine(Referential referential, String objectId) {
		Line result = referential.getLines().get(objectId);
		if (result == null) {
			result = new Line();
			result.setDetached(true);
			result.setObjectId(objectId);
			referential.getLines().put(objectId, result);
		}
		return result;
	}

	public static JourneyPattern getJourneyPattern(Referential referential,
			String objectId) {
		JourneyPattern result = referential.getJourneyPatterns().get(objectId);
		if (result == null) {
			result = new JourneyPattern();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getJourneyPatterns().put(objectId, result);
		}
		return result;
	}

	public static ConnectionLink getConnectionLink(Referential referential,
			String objectId) {
		ConnectionLink result = referential.getSharedConnectionLinks().get(
				objectId);
		if (result == null) {
			result = new ConnectionLink();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedConnectionLinks().put(objectId, result);
		}
		if (!referential.getConnectionLinks().containsKey(objectId)) {
			referential.getConnectionLinks().put(objectId, result);
		}

		return result;
	}

	public static StopArea getStopArea(Referential referential, String objectId) {
		StopArea result = referential.getSharedStopAreas().get(objectId);
		if (result == null) {
			result = referential.getSharedStopAreas().get(referential.getStopAreaMapping().get(objectId));
		}
		if (result == null) {
			result = new StopArea();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedStopAreas().put(objectId, result);
		} 
		if (!referential.getStopAreas().containsKey(objectId)) {
			referential.getStopAreas().put(objectId, result);
		}
		
		return result;
	}

	public static GroupOfLine getGroupOfLine(Referential referential,
			String objectId) {
		GroupOfLine result = referential.getSharedGroupOfLines().get(objectId);
		if (result == null) {
			result = new GroupOfLine();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedGroupOfLines().put(objectId, result);
		}
		if (!referential.getGroupOfLines().containsKey(objectId)) {
			referential.getGroupOfLines().put(objectId, result);
		}

		return result;
	}

	public static Footnote getFootnote(Referential referential,
			String objectId) {
		Footnote result = referential.getSharedFootnotes().get(objectId);
		if (result == null) {
			result = new Footnote();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedFootnotes().put(objectId, result);
		}
		if (!referential.getFootnotes().containsKey(objectId)) {
			referential.getFootnotes().put(objectId, result);
		}

		return result;
	}

	public static FootNoteAlternativeText getFootnoteAlternativeText(Referential referential, String objectId) {

		FootNoteAlternativeText result = referential.getFootnoteAlternativeTexts().get(objectId);
		if (result == null) {
			result = new FootNoteAlternativeText();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getFootnoteAlternativeTexts().put(objectId, result);
		}
		return result;

	}

	public static DatedServiceJourney getDatedServiceJourney(Referential referential, String objectId) {

		DatedServiceJourney result = referential.getDatedServiceJourneys().get(objectId);
		if (result == null) {
			result = new DatedServiceJourney();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getDatedServiceJourneys().put(objectId, result);
		}
		return result;

	}

	public static Branding getBranding(Referential referential,
									   String objectId) {
		Branding result = referential.getSharedBrandings().get(objectId);
		if (result == null) {
			result = new Branding();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedBrandings().put(objectId, result);
		}
		if (!referential.getSharedBrandings().containsKey(objectId)) {
			referential.getBrandings().put(objectId, result);
		}

		return result;
	}

	public static StopPoint getStopPoint(Referential referential,
			String objectId) {
		StopPoint result = referential.getStopPoints().get(objectId);
		if (result == null) {
			result = new StopPoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getStopPoints().put(objectId, result);
		}
		return result;
	}

	public static RoutePoint getRoutePoint(Referential referential,
										   String objectId) {
		RoutePoint result = referential.getSharedRoutePoints().get(objectId);
		if (result == null) {
			result = new RoutePoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedRoutePoints().put(objectId, result);
		}
		if (!referential.getRoutePoints().containsKey(objectId)) {
			referential.getRoutePoints().put(objectId, result);
		}
		return result;
	}

	public static ScheduledStopPoint getScheduledStopPoint(Referential referential,
														   String objectId) {
		ScheduledStopPoint result = referential.getSharedScheduledStopPoints().get(objectId);
		if (result == null) {
			result = new ScheduledStopPoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedScheduledStopPoints().put(objectId, result);
		}
		if (!referential.getScheduledStopPoints().containsKey(objectId)) {
			referential.getScheduledStopPoints().put(objectId, result);
		}
		return result;
	}

	public static VehicleJourney getVehicleJourney(Referential referential,
			String objectId) {
		VehicleJourney result = referential.getVehicleJourneys().get(objectId);
		if (result == null) {
			result = new VehicleJourney();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getVehicleJourneys().put(objectId, result);
		}
		return result;
	}

	public static DeadRun getDeadRun(Referential referential,
											String objectId) {
		DeadRun result = referential.getDeadRuns().get(objectId);
		if (result == null) {
			result = new DeadRun();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getDeadRuns().put(objectId, result);
		}
		return result;
	}
	
	public static Timeband getTimeband(Referential referential, String objectId) {
		Timeband timeband = referential.getTimebands().get(objectId);
		if (timeband == null) {
			timeband = new Timeband();
			timeband.setObjectId(objectId);
			timeband.setDetached(true);
			referential.getTimebands().put(objectId, timeband);
		}
		return timeband;
	}


	public static RouteSection getRouteSection(Referential referential, String objectId) {
		RouteSection section = referential.getSharedRouteSections().get(objectId);
		if (section == null) {
			section = new RouteSection();
			section.setObjectId(objectId);
			section.setDetached(true);
			referential.getSharedRouteSections().put(objectId, section);
		}
		if (!referential.getRouteSections().containsKey(objectId)) {
			referential.getRouteSections().put(objectId, section);
		}
		return section;
	}


	public static DestinationDisplay getDestinationDisplay(Referential referential, String objectId) {
		DestinationDisplay result = referential.getSharedDestinationDisplays().get(objectId);
		if (result == null) {
			result = new DestinationDisplay();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedDestinationDisplays().put(objectId, result);
		} 
		if (!referential.getDestinationDisplays().containsKey(objectId)) {
			referential.getDestinationDisplays().put(objectId, result);
		}
		
		return result;
	}
	
	public static Interchange getInterchange(Referential referential, String objectId) {
		Interchange interchange = referential.getSharedInterchanges().get(objectId);
		if (interchange == null) {
			interchange = new Interchange();
			interchange.setObjectId(objectId);
			interchange.setDetached(true);
			referential.getSharedInterchanges().put(objectId, interchange);
		}
		
		if (!referential.getInterchanges().containsKey(objectId)) {
			referential.getInterchanges().put(objectId, interchange);
		}

		return interchange;
	}

	public static Block getBlock(Referential referential,
										   String objectId) {
		Block result = referential.getSharedBlocks().get(objectId);
		if (result == null) {
			result = new Block();
			result.setObjectId(objectId);
			result.setDetached(true);
			referential.getSharedBlocks().put(objectId, result);
		}
		if (!referential.getBlocks().containsKey(objectId)) {
			referential.getBlocks().put(objectId, result);
		}
		return result;
	}

}
