package mobi.chouette.exchange.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Data
public class ValidationData {
	private Set<Network> networks = new HashSet<>();
	private Set<String> networkIds = new HashSet<>();
	private Set<Line> lines = new HashSet<>();
	private Set<String> lineIds = new HashSet<>();
	private Set<Company> companies = new HashSet<>();
	private Set<String> companyIds = new HashSet<>();
	private Set<GroupOfLine> groupOfLines = new HashSet<>();
	private Set<String> groupOfLineIds = new HashSet<>();
	private Set<StopArea> stopAreas = new HashSet<>();
	private Set<StopArea> dummyStopAreas = new HashSet<>(); // for connectionLink tests
	private Set<String> stopAreaIds = new HashSet<>();
	private Set<ConnectionLink> connectionLinks = new HashSet<>();
	private Set<String> connectionLinkIds = new HashSet<>();
	private Set<AccessLink> accessLinks = new HashSet<>();
	private Set<String> accessLinkIds = new HashSet<>();
	private Set<AccessPoint> accessPoints = new HashSet<>();
	private Set<String> accessPointIds = new HashSet<>();
	private Set<Timetable> timetables = new HashSet<>();
	private Set<String> timetableIds = new HashSet<>();
	private List<VehicleJourney> vehicleJourneys = new ArrayList<>();
	private List<JourneyPattern> journeyPatterns = new ArrayList<>();
	private List<Route> routes = new ArrayList<>();
	private List<StopPoint> stopPoints = new ArrayList<>();
	private Line currentLine;
	private Map<String,Location> fileLocations = new HashMap<>();
	private Map<String,Set<String>> linesOfStopAreas = new HashMap<>();

	public void clear() {
		vehicleJourneys.clear();
		journeyPatterns.clear();
		routes.clear();
		stopPoints.clear();
		currentLine = null;
	}
	
	public void dispose()
	{
		clear();
		networks.clear();
		networkIds.clear();
		lines.clear();
		lineIds.clear();
		companies.clear();
		companyIds.clear();
		groupOfLines.clear();
		groupOfLineIds.clear();
		stopAreas.clear();
		dummyStopAreas.clear();
		stopAreaIds.clear();
		connectionLinks.clear();
		connectionLinkIds.clear();
		accessLinks.clear();
		accessLinkIds.clear();
		accessPoints.clear();
		accessPointIds.clear();
		timetables.clear();
		timetableIds.clear();
		fileLocations.clear();
		linesOfStopAreas.clear();
	}
}
