package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

public class ModelAssembler {
	@Getter @Setter private List<Line> lines;
	@Getter @Setter private List<Route> routes;
	@Getter @Setter private List<Company> companies;
	@Getter @Setter private PTNetwork ptNetwork;
	@Getter @Setter private List<JourneyPattern> journeyPatterns;
	@Getter @Setter private List<PTLink> ptLinks;
	@Getter @Setter private List<VehicleJourney> vehicleJourneys;
	@Getter @Setter private List<StopPoint> stopPoints;
	@Getter @Setter private List<StopArea> stopAreas;
	@Getter @Setter private List<AreaCentroid> areaCentroids;
	@Getter @Setter private List<ConnectionLink> connectionLinks;
	
	private Map<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>> populatedDictionaries = new HashMap<Class<? extends NeptuneIdentifiedObject>, Map<String,? extends NeptuneIdentifiedObject>>();
	
	private Map<String, Line> linesDictionary = new HashMap<String, Line>();
	private Map<String, Route> routesDictionary = new HashMap<String, Route>();
	private Map<String, Company> companiesDictionary = new HashMap<String, Company>();
	private Map<String, JourneyPattern> journeyPatternsDictionary = new HashMap<String, JourneyPattern>();
	private Map<String, PTLink> ptLinksDictionary = new HashMap<String, PTLink>();
	private Map<String, VehicleJourney> vehicleJourneysDictionary = new HashMap<String, VehicleJourney>();
	private Map<String, StopPoint> stopPointsDictionary = new HashMap<String, StopPoint>();
	private Map<String, StopArea> stopAreasDictionary = new HashMap<String, StopArea>();
	private Map<String, AreaCentroid> areaCentroidsDictionary = new HashMap<String, AreaCentroid>();
	private Map<String, ConnectionLink> connectionLinksDictionary = new HashMap<String, ConnectionLink>();

	
	public void connect(){
		populateDictionaries();
		connectLines();
		connectRoutes();
		connectCompanies();
		connectPTNetwork();
		connectJourneyPatterns();
		connectPTLinks();
		connectVehicleJourneys();
		connectStopPoints();
		connectStopAreas();
		connectAreaCentroids();
		connectConnectionLinks();
	}

	private void populateDictionaries(){
		populateDictionnary(lines, linesDictionary);
		populateDictionnary(routes, routesDictionary);
		populateDictionnary(companies, companiesDictionary);
		populateDictionnary(journeyPatterns, journeyPatternsDictionary);
		populateDictionnary(ptLinks, ptLinksDictionary);
		populateDictionnary(vehicleJourneys, vehicleJourneysDictionary);
		populateDictionnary(stopPoints, stopPointsDictionary);
		populateDictionnary(stopAreas, stopAreasDictionary);
		populateDictionnary(areaCentroids, areaCentroidsDictionary);
		populateDictionnary(connectionLinks, connectionLinksDictionary);
	}
	
	private <T extends NeptuneIdentifiedObject> void populateDictionnary(List<T> list, Map<String,T> dictionnary){
		for(T item : list){
			if(item != null && item.getObjectId() != null){
				dictionnary.put(item.getObjectId(), item);
			}
		}
		if(list.size() > 0){
			populatedDictionaries.put(list.get(0).getClass(), dictionnary);
		}
	}
	
	private void connectLines(){
		for(Line line : lines){
			//FIXME : field CompanyId not filled by import...
			//line.setCompany(getObjectFromId(Long.toString(line.getCompanyId()), Company.class));
			
			line.setPtNetwork(ptNetwork);
			line.setRoutes(getObjectsFromIds(line.getRouteIds(), Route.class));
		}
	}
	
	private void connectRoutes(){
		for(Route route : routes){
			route.setJourneyPatterns(getObjectsFromIds(route.getJourneyPatternIds(), JourneyPattern.class));
			route.setPtLinks(getObjectsFromIds(route.getPtLinkIds(), PTLink.class));
		}
	}
	
	private void connectCompanies(){
		for(Company company : companies){
			//nothing to do...
		}
	}

	private void connectPTNetwork() {
		// nothing to do...
	}
	
	private void connectJourneyPatterns() {
		for(JourneyPattern journeyPattern : journeyPatterns){
			journeyPattern.setRoute(getObjectFromId(journeyPattern.getRouteId(), Route.class));
			journeyPattern.setStopPoints(getObjectsFromIds(journeyPattern.getStopPointIds(), StopPoint.class));
		}
	}
	
	private void connectPTLinks(){
		for(PTLink ptLink : ptLinks){
			ptLink.setStartOfLink(getObjectFromId(ptLink.getStartOfLinkId(), StopPoint.class));
			ptLink.setEndOfLink(getObjectFromId(ptLink.getStartOfLinkId(), StopPoint.class));
		}
	}
	
	private void connectVehicleJourneys(){
		for(VehicleJourney vehicleJourney : vehicleJourneys){
			JourneyPattern journeyPattern = getObjectFromId(vehicleJourney.getJourneyPatternId(), JourneyPattern.class);
			vehicleJourney.setJourneyPattern(journeyPattern);
			journeyPattern.addVehicleJourney(vehicleJourney);
			vehicleJourney.setRoute(getObjectFromId(vehicleJourney.getRouteId(), Route.class));
			for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
				vehicleJourneyAtStop.setStopPoint(getObjectFromId(vehicleJourneyAtStop.getStopPointId(), StopPoint.class));
			}
			//vehicleJourney.setTimeSlot(getObjectFromId(vehicleJourney.getTimeSlotId(), TimeSlot.class));
		}
	}

	private void connectStopPoints() {
		for(StopPoint stopPoint : stopPoints){
			stopPoint.setContainedInStopArea(getObjectFromId(stopPoint.getContainedInStopAreaId(), StopArea.class));
			stopPoint.setLine(getObjectFromId(stopPoint.getLineIdShortcut(), Line.class));
			if(ptNetwork.getObjectId().equals(stopPoint.getPtNetworkIdShortcut())){
				stopPoint.setPtNetwork(ptNetwork);
			}
			else{
				//TODO : throw exception ???
			}
		}
	}

	private void connectStopAreas() {
		for(StopArea stopArea : stopAreas){
			stopArea.setAreaCentroid(getObjectFromId(stopArea.getAreaCentroidId(), AreaCentroid.class));
			stopArea.setContainedStopAreas(getObjectsFromIds(stopArea.getContainedStopIds(), StopArea.class));
			if(stopArea.getContainedStopAreas() != null){
				for(StopArea childStopArea : stopArea.getContainedStopAreas()){
					childStopArea.setParentStopArea(stopArea);
				}
			}
			stopArea.setContainedStopPoints(getObjectsFromIds(stopArea.getContainedStopIds(), StopPoint.class));
			//no need to set containedInStopArea in StopPoint : it is already done in connectStopPoints method...
		}
	}
	
	private void connectAreaCentroids() {
		for(AreaCentroid areaCentroid : areaCentroids){
			areaCentroid.setContainedInStopArea(getObjectFromId(areaCentroid.getContainedInStopAreaId(), StopArea.class));
		}
	}
	
	private void connectConnectionLinks() {
		for(ConnectionLink connectionLink : connectionLinks){
			StopArea startOfLink = getObjectFromId(connectionLink.getStartOfLinkId(), StopArea.class);
			if(startOfLink != null){
				connectionLink.setStartOfLink(startOfLink);
				startOfLink.addConnectionLink(connectionLink);
			}
			StopArea endOfLink = getObjectFromId(connectionLink.getEndOfLinkId(), StopArea.class);
			if(endOfLink != null){
				connectionLink.setEndOfLink(endOfLink);
				endOfLink.addConnectionLink(connectionLink);
			}
		}
	}
	
	private <T extends NeptuneIdentifiedObject> List<T> getObjectsFromIds(List<String> ids, Class<T> dictionaryClass){
		Map<String, ? extends NeptuneIdentifiedObject> dictionary =  populatedDictionaries.get(dictionaryClass);
		List<T> objects = new ArrayList<T>();
		
		if(dictionary != null){
			for(String id : ids){
				T object = (T)dictionary.get(id);
				if(object != null){
					objects.add(object);
				}
			}
		}
		
		if(objects.size() == 0){
			objects = null;
		}
		
		return objects;
	}
	
	private <T extends NeptuneIdentifiedObject> T getObjectFromId(String id, Class<T> dictionaryClass){
		Map<String, ? extends NeptuneIdentifiedObject> dictionary =  populatedDictionaries.get(dictionaryClass);
		T object = null;
		
		object = (T)dictionary.get(id);
		
		return object;
	}
}
