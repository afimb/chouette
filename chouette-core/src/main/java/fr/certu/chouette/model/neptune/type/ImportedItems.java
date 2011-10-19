package fr.certu.chouette.model.neptune.type;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
/**
 * 
 * @author mamadou keira
 *
 */
public class ImportedItems implements Serializable{
	
	private static final long serialVersionUID = 891961434525645809L;
	
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private List<Company> companies;
	@Getter @Setter private List<Route> routes;
	@Getter @Setter private List<JourneyPattern> journeyPatterns;
	@Getter @Setter private List<AccessLink> accessLinks;
	@Getter @Setter private List<AccessPoint> accessPoints;
	@Getter @Setter private List<AreaCentroid> areaCentroids;
	@Getter @Setter private List<ConnectionLink> connectionLinks;
	@Getter @Setter private List<Facility> facilities;
	@Getter @Setter private List<GroupOfLine> groupOfLines;
	@Getter @Setter private List<PTLink> ptLinks;
	@Getter @Setter private List<StopArea> stopAreas;
	@Getter @Setter private List<StopPoint> stopPoints;
	@Getter @Setter private List<Timetable> timetables;
	@Getter @Setter private List<VehicleJourney> vehicleJourneys;
	@Getter @Setter private List<TimeSlot> timeSlots;
}
