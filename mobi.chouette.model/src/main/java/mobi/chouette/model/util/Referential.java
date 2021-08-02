package mobi.chouette.model.util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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

@NoArgsConstructor
@ToString()
public class Referential implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Map<String, AccessLink> sharedAccessLinks = new HashMap<String, AccessLink>();

	@Getter
	@Setter
	private Map<String, DestinationDisplay> sharedDestinationDisplays = new HashMap<String, DestinationDisplay>();

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
	private Map<String, ConnectionLink> sharedConnectionLinks = new HashMap<String, ConnectionLink>();

	@Getter
	@Setter
	private Map<String, StopArea> sharedStopAreas = new HashMap<String, StopArea>();

	@Getter
	@Setter
	private Map<String, String> stopAreaMapping = new HashMap<>();


	@Getter
	@Setter
	private Map<String, GroupOfLine> sharedGroupOfLines = new HashMap<String, GroupOfLine>();


	@Getter
	@Setter
	private Map<String, Line> sharedLines = new HashMap<String, Line>();

	@Getter
	@Setter
	private Map<String, Timetable> sharedTimetables = new HashMap<String, Timetable>();

	@Getter
	@Setter
	private Map<String, Timeband> sharedTimebands = new HashMap<String, Timeband>();

	@Getter
	@Setter
	private Map<String, Interchange> sharedInterchanges = new HashMap<String, Interchange>();

	@Getter
	@Setter
	private Map<String, ScheduledStopPoint> sharedScheduledStopPoints = new HashMap<>();

	@Getter
	@Setter
	private Map<String, Footnote> sharedFootnotes = new HashMap<String, Footnote>();

	@Getter
	@Setter
	private Map<String, Branding> sharedBrandings = new HashMap<>();

	@Getter
	@Setter
	private Map<String, RoutePoint> sharedRoutePoints = new HashMap<>();

	@Getter
	@Setter
	private Map<String, RouteSection> sharedRouteSections = new HashMap<String, RouteSection>();

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
	private Map<String, StopPoint> stopPoints = new HashMap<String, StopPoint>();

	@Getter
	@Setter
	private Map<String, RoutePoint> routePoints = new HashMap<>();

	@Getter
	@Setter
	private Map<String, ScheduledStopPoint> scheduledStopPoints = new HashMap<>();

	@Getter
	@Setter
	private Map<String, VehicleJourney> vehicleJourneys = new HashMap<String, VehicleJourney>();

	@Getter
	@Setter
	private Map<String, DeadRun> deadRuns = new HashMap<String, DeadRun>();

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

	@Getter
	@Setter
	private Map<String, DestinationDisplay> destinationDisplays = new HashMap<String, DestinationDisplay>();



	@Getter
	@Setter
	private Map<String, Interchange> interchanges = new HashMap<String, Interchange>();

	@Getter
	@Setter
	private Map<String, Footnote> footnotes = new HashMap<String, Footnote>();

	@Getter
	@Setter
	private Map<String, FootNoteAlternativeText> footnoteAlternativeTexts = new HashMap<>();

	@Getter
	@Setter
	private Map<String, DatedServiceJourney> datedServiceJourneys = new HashMap<>();

	@Getter
	@Setter
	private Map<String, Block> sharedBlocks = new HashMap<>();

	@Getter
	@Setter
	private Map<String, Block> blocks = new HashMap<>();

	@Getter
	@Setter
	private Map<String, Branding> brandings = new HashMap<>();


	@Getter
	@Setter
	private Map<String, VehicleJourneyAtStop> vehicleJourneyAtStops = new HashMap<String, VehicleJourneyAtStop>();

	@Getter
	@Setter
	private Map<String, DeadRunAtStop> deadRunAtStops = new HashMap<>();

	public void clear(boolean cascade) {
		if (cascade) {
			for (Line line : lines.values()) {
				line.getRoutes().clear();
				line.getFootnotes().clear();
				line.getRoutingConstraints().clear();
				line.getGroupOfLines().clear();
			}
			for (Route route : routes.values()) {
				route.getRoutePoints().clear();
				route.getJourneyPatterns().clear();
				route.getRoutePoints().clear();
			}
			for (JourneyPattern jp : journeyPatterns.values()) {
				for(StopPoint sp : jp.getStopPoints()) {
					sp.getFootnotes().clear();
				}
				jp.getStopPoints().clear();
				jp.getVehicleJourneys().clear();
				jp.getRouteSections().clear();
				jp.getFootnotes().clear();
			}
			for (VehicleJourney vj : vehicleJourneys.values()) {
				for(VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops()) {
					vjas.getFootnotes().clear();
				}
				vj.getVehicleJourneyAtStops().clear();
				vj.getTimetables().clear();
				vj.getJourneyFrequencies().clear();
				vj.getFootnotes().clear();
				vj.getDatedServiceJourneys().clear();
			}
			for (DeadRun dr : deadRuns.values()) {
				dr.getDeadRunAtStops().clear();
				dr.getTimetables().clear();
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
				area.getContainedScheduledStopPoints().clear();
			}
			for (DestinationDisplay display : sharedDestinationDisplays.values()) {
				display.getVias().clear();
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
		routePoints.clear();
		timebands.clear();
		timetables.clear();
		vehicleJourneys.clear();
		datedServiceJourneys.clear();
		routeSections.clear();
		destinationDisplays.clear();
		interchanges.clear();
		footnotes.clear();
		vehicleJourneyAtStops.clear();
		brandings.clear();
		blocks.clear();
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
		stopAreaMapping.clear();
		sharedTimebands.clear();
		sharedTimetables.clear();
		sharedDestinationDisplays.clear();
		sharedInterchanges.clear();
		sharedFootnotes.clear();
		sharedBrandings.clear();
		sharedRoutePoints.clear();
		sharedRouteSections.clear();
		sharedBlocks.clear();
	}

}
