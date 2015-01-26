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
import mobi.chouette.model.PTLink;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@NoArgsConstructor
@ToString()
public class Referential implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Map<String, AccessLink> accessLink = new HashMap<String, AccessLink>();

	@Getter
	@Setter
	private Map<String, AccessPoint> accessPoint = new HashMap<String, AccessPoint>();

	@Getter
	@Setter
	private Map<String, PTLink> ptLink = new HashMap<String, PTLink>();

	/* ------------------ */

	@Getter
	@Setter
	private Map<String, PTNetwork> networks = new HashMap<String, PTNetwork>();

	@Getter
	@Setter
	private Map<String, Company> companies = new HashMap<String, Company>();

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
	private Map<String, ConnectionLink> connectionLinks = new HashMap<String, ConnectionLink>();

	@Getter
	@Setter
	private Map<String, StopArea> stopAreas = new HashMap<String, StopArea>();

	@Getter
	@Setter
	private Map<String, GroupOfLine> groupOfLines = new HashMap<String, GroupOfLine>();

	@Getter
	@Setter
	private Map<String, StopPoint> stopPoints = new HashMap<String, StopPoint>();

	@Getter
	@Setter
	private Map<String, VehicleJourney> vehicleJourneys = new HashMap<String, VehicleJourney>();

	@Getter
	@Setter
	private Map<String, Timetable> timetables = new HashMap<String, Timetable>();

	public void clear() {
		lines.clear();
		routes.clear();
		stopPoints.clear();
		ptLink.clear();
		journeyPatterns.clear();
		vehicleJourneys.clear();
	}

}
