package mobi.chouette.model.util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.common.ChouetteId;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
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

@NoArgsConstructor
@ToString()
public class Referential implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Map<ChouetteId, AccessLink> sharedAccessLinks = new HashMap<ChouetteId, AccessLink>();

	@Getter
	@Setter
	private Map<ChouetteId, AccessPoint> sharedAccessPoints = new HashMap<ChouetteId, AccessPoint>();

	@Getter
	@Setter
	private Map<ChouetteId, Network> sharedPTNetworks = new HashMap<ChouetteId, Network>();

	@Getter
	@Setter
	private Map<ChouetteId, Company> sharedCompanies = new HashMap<ChouetteId, Company>();

	@Getter
	@Setter
	private Map<ChouetteId, ConnectionLink> sharedConnectionLinks = new HashMap<ChouetteId, ConnectionLink>();

	@Getter
	@Setter
	private Map<ChouetteId, StopArea> sharedStopAreas = new HashMap<ChouetteId, StopArea>();
	
	@Getter
	@Setter
	private Map<ChouetteId, RoutingConstraint> sharedRoutingConstraints = new HashMap<ChouetteId, RoutingConstraint>();

	@Getter
	@Setter
	private Map<ChouetteId, GroupOfLine> sharedGroupOfLines = new HashMap<ChouetteId, GroupOfLine>();


	@Getter
	@Setter
	private Map<ChouetteId, Line> sharedLines = new HashMap<ChouetteId, Line>();

	@Getter
	@Setter
	private Map<ChouetteId, Timetable> sharedTimetables = new HashMap<ChouetteId, Timetable>();

	@Getter
	@Setter
	private Map<ChouetteId, Timeband> sharedTimebands = new HashMap<ChouetteId, Timeband>();


	@Getter
	@Setter
	private Map<ChouetteId, Route> routes = new HashMap<ChouetteId, Route>();

	@Getter
	@Setter
	private Map<ChouetteId, Line> lines = new HashMap<ChouetteId, Line>();

	@Getter
	@Setter
	private Map<ChouetteId, JourneyPattern> journeyPatterns = new HashMap<ChouetteId, JourneyPattern>();

	@Getter
	@Setter
	private Map<ChouetteId, StopPoint> stopPoints = new HashMap<ChouetteId, StopPoint>();

	@Getter
	@Setter
	private Map<ChouetteId, VehicleJourney> vehicleJourneys = new HashMap<ChouetteId, VehicleJourney>();

	@Getter
	@Setter
	private Map<ChouetteId, AccessLink> accessLinks = new HashMap<ChouetteId, AccessLink>();

	@Getter
	@Setter
	private Map<ChouetteId, AccessPoint> accessPoints = new HashMap<ChouetteId, AccessPoint>();

	@Getter
	@Setter
	private Map<ChouetteId, Network> ptNetworks = new HashMap<ChouetteId, Network>();

	@Getter
	@Setter
	private Map<ChouetteId, Company> companies = new HashMap<ChouetteId, Company>();

	@Getter
	@Setter
	private Map<ChouetteId, ConnectionLink> connectionLinks = new HashMap<ChouetteId, ConnectionLink>();

	@Getter
	@Setter
	private Map<ChouetteId, StopArea> stopAreas = new HashMap<ChouetteId, StopArea>();
	
	@Getter
	@Setter
	private Map<ChouetteId, RoutingConstraint> routingConstraints = new HashMap<ChouetteId, RoutingConstraint>();

	@Getter
	@Setter
	private Map<ChouetteId, GroupOfLine> groupOfLines = new HashMap<ChouetteId, GroupOfLine>();

	@Getter
	@Setter
	private Map<ChouetteId, Timetable> timetables = new HashMap<ChouetteId, Timetable>();

	@Getter
	@Setter
	private Map<ChouetteId, Timeband> timebands = new HashMap<ChouetteId, Timeband>();

	@Getter
	@Setter
	private Map<ChouetteId, RouteSection> routeSections = new HashMap<ChouetteId, RouteSection>();

	public void clear(boolean cascade) {
		if (cascade) {
			for (Line line : lines.values()) {
				line.getRoutes().clear();
				line.getFootnotes().clear();
				line.getRoutingConstraints().clear();
				line.getGroupOfLines().clear();
			}
			for (Route route : routes.values()) {
				route.getStopPoints().clear();
				route.getJourneyPatterns().clear();
			}
			for (JourneyPattern jp : journeyPatterns.values()) {
				jp.getStopPoints().clear();
				jp.getVehicleJourneys().clear();
				jp.getRouteSections().clear();
			}
			for (VehicleJourney vj : vehicleJourneys.values()) {
				vj.getVehicleJourneyAtStops().clear();
				vj.getTimetables().clear();
				vj.getJourneyFrequencies().clear();
				vj.getFootnotes().clear();
			}
			for (Timetable timetable : timetables.values()) {
				timetable.getVehicleJourneys().clear();
			}
			for (Timetable timetable : sharedTimetables.values()) {
				timetable.getVehicleJourneys().clear();
			}
			for (Timeband timeband : sharedTimebands.values()) {
				timeband.getJourneyFrequencies().clear();
			}
			for (Timeband timeband : timebands.values()) {
				timeband.getJourneyFrequencies().clear();
			}
			for (GroupOfLine group : sharedGroupOfLines.values()) {
				group.getLines().clear();
			}
			for (StopArea area : sharedStopAreas.values()) {
				area.getContainedStopPoints().clear();
			}
		}
		accessLinks.clear();
		accessPoints.clear();
		companies.clear();
		connectionLinks.clear();
		groupOfLines.clear();
		journeyPatterns.clear();
		lines.clear();
		ptNetworks.clear();
		routes.clear();
		stopAreas.clear();
		stopPoints.clear();
		timebands.clear();
		timetables.clear();
		vehicleJourneys.clear();
		routeSections.clear();
	}

	public void dispose() {
		// clear(false);
		sharedAccessLinks.clear();
		sharedAccessPoints.clear();
		sharedCompanies.clear();
		sharedConnectionLinks.clear();
		sharedGroupOfLines.clear();
		sharedLines.clear();
		sharedPTNetworks.clear();
		sharedStopAreas.clear();
		sharedTimebands.clear();
		sharedTimetables.clear();
	}

}
