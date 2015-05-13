package mobi.chouette.exchange.kml.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
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
public class ExportableData 
{
private Network network;
private Line line;
private Set<Company> companies = new HashSet<>();
private Set<StopArea> stopPlaces = new HashSet<>();
private Set<StopArea> commercialStopPoints = new HashSet<>();
private Set<StopArea> quays = new HashSet<>();
private Set<StopArea> boardingPositions = new HashSet<>();
private Set<StopArea> stopAreas = new HashSet<>();
private Set<StopArea> restrictionConstraints = new HashSet<>();
private Set<AccessLink> accessLinks = new HashSet<>();
private Set<AccessPoint> accessPoints = new HashSet<>();
private Set<ConnectionLink> connectionLinks = new HashSet<>();
private Set<Timetable> timetables = new HashSet<>();
private List<JourneyPattern> journeyPatterns = new ArrayList<>();
private List<Route> routes = new ArrayList<>();
//may be useless
private List<VehicleJourney> vehicleJourneys = new ArrayList<>();
private List<StopPoint> stopPoints = new ArrayList<>();
public Timetable findTimetable(String objectId) {
	for (Timetable tm : timetables) {
		if (tm.getObjectId().equals(objectId))
			return tm;
	}
	return null;
}
}
