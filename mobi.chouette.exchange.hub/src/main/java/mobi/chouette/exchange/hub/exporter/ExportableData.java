package mobi.chouette.exchange.hub.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
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
public class ExportableData 
{
private Set<Network> networks = new HashSet<>();
private Set<Company> companies = new HashSet<>();
private Set<GroupOfLine> groupOfLines = new HashSet<>();
private Set<StopArea> commercialStopPoints = new HashSet<>();
private Set<StopArea> physicalStopPoints = new HashSet<>();
private Set<StopArea> stopAreas = new HashSet<>();
private Set<ConnectionLink> connectionLinks = new HashSet<>();
private Set<Timetable> timetables = new HashSet<>();
private int vehicleJourneyRank = 0;
// refilled line by line
private Line line;
private int pmrFootenoteId = -1;
private List<JourneyPattern> journeyPatterns = new ArrayList<>();
private List<Route> routes = new ArrayList<>();
private List<VehicleJourney> vehicleJourneys = new ArrayList<>();
private List<StopPoint> stopPoints = new ArrayList<>();
}
