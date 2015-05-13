package mobi.chouette.exchange.gtfs.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Data
public class ExportableData {
	private Network network;
	private Line line;
	private Set<Company> companies = new HashSet<>();
	private Set<StopArea> physicalStops = new HashSet<>();
	private Set<StopArea> commercialStops = new HashSet<>();
	private Set<StopArea> sharedStops = new HashSet<>();
	private Set<ConnectionLink> connectionLinks = new HashSet<>();
	private Set<Timetable> timetables = new HashSet<>();
	private Map<String, List<Timetable>> timetableMap = new HashMap<>();
	private List<VehicleJourney> vehicleJourneys = new ArrayList<>();
	// may be useless
	private List<JourneyPattern> journeyPatterns = new ArrayList<>();
	private List<Route> routes = new ArrayList<>();
	private List<StopPoint> stopPoints = new ArrayList<>();

	public Timetable findTimetable(String objectId) {
		for (Timetable tm : timetables) {
			if (tm.getObjectId().equals(objectId))
				return tm;
		}
		return null;
	}
}
