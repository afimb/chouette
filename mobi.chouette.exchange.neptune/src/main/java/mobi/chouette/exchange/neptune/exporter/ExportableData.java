package mobi.chouette.exchange.neptune.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import mobi.chouette.exchange.neptune.model.Facility;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;

@Data
public class ExportableData 
{
private PTNetwork network;
private Line line;
private Set<Company> companies = new HashSet<>();
private Set<GroupOfLine> groupOfLines = new HashSet<>();
private Set<StopArea> stopAreas = new HashSet<>();
private Set<ConnectionLink> connectionLinks = new HashSet<>();
private Set<AccessLink> accessLinks = new HashSet<>();
private Set<AccessPoint> accessPoints = new HashSet<>();
private Set<Timetable> timetables = new HashSet<>();
private Set<Facility> facilities = new HashSet<>();
private List<VehicleJourney> vehicleJourneys = new ArrayList<>();
private List<JourneyPattern> journeyPatterns = new ArrayList<>();
private List<Route> routes = new ArrayList<>();
private List<StopPoint> stopPoints = new ArrayList<>();
}
