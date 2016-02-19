package mobi.chouette.model.util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
	private Map<String, AccessLink> sharedAccessLinks = new HashMap<String, AccessLink>();

	@Getter
	@Setter
	private Map<String, AccessPoint> sharedAccessPoints = new HashMap<String, AccessPoint>();

	@Getter
	@Setter
	private Map<String, Network> sharedPTNetworks = new HashMap<String, Network>();

	@Getter
	@Setter
	private Map<String, Company> sharedCompanies = new HashMap<String, Company>();

	@Getter
	@Setter
	private Map<String, Route> routes = new HashMap<String, Route>();

	@Getter
	@Setter
	private Map<String, Line> lines = new HashMap<String, Line>();

	@Getter
	@Setter
	private Map<String, JourneyPattern> journeyPatterns = new HashMap<String, JourneyPattern>();

	@Getter
	@Setter
	private Map<String, ConnectionLink> sharedConnectionLinks = new HashMap<String, ConnectionLink>();

	@Getter
	@Setter
	private Map<String, StopArea> sharedStopAreas = new HashMap<String, StopArea>();

	@Getter
	@Setter
	private Map<String, GroupOfLine> sharedGroupOfLines = new HashMap<String, GroupOfLine>();

	@Getter
	@Setter
	private Map<String, StopPoint> stopPoints = new HashMap<String, StopPoint>();

	@Getter
	@Setter
	private Map<String, VehicleJourney> vehicleJourneys = new HashMap<String, VehicleJourney>();

	@Getter
	@Setter
	private Map<String, Line> sharedLines = new HashMap<String, Line>();

	@Getter
	@Setter
	private Map<String, Timetable> sharedTimetables = new HashMap<String, Timetable>();

	@Getter
	@Setter
	private Map<String, Timeband> sharedTimebands = new HashMap<String, Timeband>();

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
			}
			for (VehicleJourney vj : vehicleJourneys.values()) {
				vj.getVehicleJourneyAtStops().clear();
				vj.getTimetables().clear();
				vj.getJourneyFrequencies().clear();
			}
			for (Timetable timetable : timetables.values()) {
				timetable.getVehicleJourneys().clear();
			}
			for (Timetable timetable : sharedTimetables.values()) {
				timetable.getVehicleJourneys().clear();
			}
			for (GroupOfLine group : sharedGroupOfLines.values()) {
				group.getLines().clear();
			}
			for (StopArea area : sharedStopAreas.values()) {
				area.getContainedStopPoints().clear();
			}
			for (Timetable timetable : sharedTimetables.values()) {
				timetable.getVehicleJourneys().clear();
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

	@Getter
	@Setter
	private Map<String, AccessLink> accessLinks = new HashMap<String, AccessLink>();

	@Getter
	@Setter
	private Map<String, AccessPoint> accessPoints = new HashMap<String, AccessPoint>();

	@Getter
	@Setter
	private Map<String, Network> ptNetworks = new HashMap<String, Network>();

	@Getter
	@Setter
	private Map<String, Company> companies = new HashMap<String, Company>();

	@Getter
	@Setter
	private Map<String, ConnectionLink> connectionLinks = new HashMap<String, ConnectionLink>();

	@Getter
	@Setter
	private Map<String, StopArea> stopAreas = new HashMap<String, StopArea>();

	@Getter
	@Setter
	private Map<String, GroupOfLine> groupOfLines = new HashMap<String, GroupOfLine>();

	@Getter
	@Setter
	private Map<String, Timetable> timetables = new HashMap<String, Timetable>();

	@Getter
	@Setter
	private Map<String, Timeband> timebands = new HashMap<String, Timeband>();

	@Getter
	@Setter
	private Map<String, RouteSection> routeSections = new HashMap<String, RouteSection>();

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
