/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.xml.neptune.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.RestrictionConstraint;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ImportedItems;

/**
 * @author michel
 *
 */
public class ModelAssembler 
{

	@Getter @Setter private Line line;
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
	@Getter @Setter private List<Timetable> timetables;
	@Getter @Setter private List<AccessLink> accessLinks;
	@Getter @Setter private List<AccessPoint> accessPoints;
	@Getter @Setter private List<GroupOfLine> groupOfLines;
	@Getter @Setter private List<Facility> facilities;
	@Getter @Setter private List<TimeSlot> timeSlots;
	@Getter @Setter private List<RestrictionConstraint> restrictionConstraints;

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
	private Map<String, Timetable> timetablesDictionary = new HashMap<String, Timetable>();
	private Map<String, AccessLink> accessLinksDictionary = new HashMap<String, AccessLink>();
	private Map<String, AccessPoint> accessPointsDictionary = new HashMap<String, AccessPoint>();
	private Map<String, GroupOfLine> groupOfLinesDictionary = new HashMap<String, GroupOfLine>();
	private Map<String, Facility> facilitiesDictionary = new HashMap<String, Facility>();
	private Map<String, TimeSlot> timeSlotDictionary = new HashMap<String, TimeSlot>();

	public void connect()
	{
		populateDictionaries();
		connectFacilities();
		connectLine();
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
		connectRestrictionConstraints();
		connectTimetables();
		connectAccessLinks();
		connectGroupOfLines();
	}


	private void populateDictionaries()
	{

		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
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
		populateDictionnary(timetables, timetablesDictionary);
		populateDictionnary(accessLinks, accessLinksDictionary);
		populateDictionnary(accessPoints, accessPointsDictionary);
		populateDictionnary(groupOfLines, groupOfLinesDictionary);
		populateDictionnary(facilities, facilitiesDictionary);
		populateDictionnary(timeSlots,timeSlotDictionary);
	}

	private <T extends NeptuneIdentifiedObject> void populateDictionnary(List<T> list, Map<String,T> dictionnary)
	{

		for(T item : list)
		{
			if(item != null && item.getObjectId() != null)
			{
				dictionnary.put(item.getObjectId(), item);
			}
		}
		if(list.size() > 0)
		{
			populatedDictionaries.put(list.get(0).getClass(), dictionnary);
		}
	}

	private void connectLine()
	{
		if(companies != null && companies.size() == 1)
		{
			line.setCompany(companies.get(0));
		}
                line.setCompanies(companies);

		line.setPtNetwork(ptNetwork);
		//line.setRoutes(getObjectsFromIds(line.getRouteIds(), Route.class));
		line.setRoutes(routes);

		ImportedItems item = new ImportedItems();
		item.setAccessLinks(accessLinks);
		item.setAccessPoints(accessPoints);
		item.setAreaCentroids(areaCentroids);
		item.setCompanies(companies);
		item.setConnectionLinks(connectionLinks);
		item.setFacilities(facilities);
		item.setGroupOfLines(groupOfLines);
		item.setJourneyPatterns(journeyPatterns);
		item.setPtLinks(ptLinks);
		item.setPtNetwork(ptNetwork);
		item.setRoutes(routes);
		item.setStopAreas(stopAreas);
		item.setStopPoints(stopPoints);
		item.setTimetables(timetables);
		item.setVehicleJourneys(vehicleJourneys);
		item.setTimeSlots(timeSlots);
		item.setRestrictionConstraints(restrictionConstraints);

		line.setImportedItems(item);
		if(!groupOfLines.isEmpty())
			line.setGroupOfLine(groupOfLines.get(0));
                line.setGroupOfLines(groupOfLines);

		for (Facility facility : facilities)
		{
			if(facility.getLine() != null && facility.getLine().equals(line))
				line.addFacility(facility);
		}
		
	}

	private void connectRestrictionConstraints() 
	{
		for (RestrictionConstraint restriction : restrictionConstraints) 
		{
			if (restriction.getLineIdShortCut() != null && restriction.getLineIdShortCut().equals(line.getObjectId()))
			{
				restriction.setLine(line);
				line.addRestrictionConstraint(restriction);
			}
			for (String areaId : restriction.getAreaIds()) 
			{
				StopArea area = stopAreasDictionary.get(areaId);
				if (area != null)
				{
					restriction.addArea(area);
				}
			}
		}
		
	}


	private void connectRoutes()
	{
		for(Route route : routes)
		{
			route.setJourneyPatterns(getObjectsFromIds(route.getJourneyPatternIds(), JourneyPattern.class));
			// route.setPtLinks(getObjectsFromIds(route.getPtLinkIds(), PTLink.class));

			
			route.setPtLinks(getObjectsFromIds(route.getPtLinkIds(), PTLink.class));
			List<StopPoint> stopPoints = new ArrayList<StopPoint>();
			int position = 0;
			if (route.getPtLinks() != null)
			{
				List<PTLink> sortedLinks = sortPtLinks(route.getPtLinks());
				for (PTLink ptLink : route.getPtLinks()) 
				{
					ptLink.setRoute(route);
					ptLink.setRouteId(route.getObjectId());
				}
				
				for (PTLink ptLink : sortedLinks) 
				{
					StopPoint startPoint = getObjectFromId(ptLink.getStartOfLinkId(), StopPoint.class);
					startPoint.setPosition(position++);
					startPoint.setRoute(route);
					if(!stopPoints.contains(startPoint))
					{
						stopPoints.add(startPoint);
					}
					StopPoint endPoint = getObjectFromId(ptLink.getEndOfLinkId(), StopPoint.class);
					if(!stopPoints.contains(endPoint))
					{
						endPoint.setPosition(position);
						endPoint.setRoute(route);
						stopPoints.add(endPoint);
					}
				}
				route.setStopPoints(stopPoints);
			}
			
			route.setLine(line);
		}
	}


	private List<PTLink> sortPtLinks(List<PTLink> ptLinks) 
	{
		if (ptLinks == null || ptLinks.isEmpty()) return ptLinks;
		Map<String,PTLink> linkByStart = new HashMap<String, PTLink>();
		Map<String,PTLink> linkByEnd = new HashMap<String, PTLink>();

		for (PTLink ptLink : ptLinks) 
		{
			linkByStart.put(ptLink.getStartOfLinkId(), ptLink);
			linkByEnd.put(ptLink.getEndOfLinkId(), ptLink);
		}

		Set<String> starts = new HashSet<String>();
		starts.addAll(linkByStart.keySet());
		starts.removeAll(linkByEnd.keySet());

		String start = starts.toArray(new String[0])[0];
		PTLink link = linkByStart.get(start);
		List<PTLink> sortedLinks = new ArrayList<PTLink>();
		while (link != null)
		{
			sortedLinks.add(link);
			start = link.getEndOfLinkId();
			link = linkByStart.get(start);
		}

		return sortedLinks;
	}

	private void connectCompanies()
	{
		/*
		for(Company company : companies){
			//nothing to do...
		}
		 */
	}

	private void connectPTNetwork() 
	{
		ptNetwork.setLines(getObjectsFromIds(ptNetwork.getLineIds(), Line.class));
	}

	private void connectJourneyPatterns() 
	{
		for(JourneyPattern journeyPattern : journeyPatterns)
		{
			journeyPattern.setRoute(getObjectFromId(journeyPattern.getRouteId(), Route.class));
			journeyPattern.setStopPoints(getObjectsFromIds(journeyPattern.getStopPointIds(), StopPoint.class));
		}
	}

	private void connectPTLinks()
	{
		for(PTLink ptLink : ptLinks)
		{
			ptLink.setStartOfLink(getObjectFromId(ptLink.getStartOfLinkId(), StopPoint.class));
			ptLink.setEndOfLink(getObjectFromId(ptLink.getEndOfLinkId(), StopPoint.class));
			ptLink.setRoute(getObjectFromId(ptLink.getRouteId(), Route.class));
		}
	}

	private void connectVehicleJourneys()
	{
		for(VehicleJourney vehicleJourney : vehicleJourneys)
		{
			vehicleJourney.setCompany(getObjectFromId(vehicleJourney.getCompanyId(), Company.class));
			JourneyPattern journeyPattern = getObjectFromId(vehicleJourney.getJourneyPatternId(), JourneyPattern.class);
			vehicleJourney.setJourneyPattern(journeyPattern);
			if (journeyPattern != null)
			   journeyPattern.addVehicleJourney(vehicleJourney);
			vehicleJourney.setRoute(getObjectFromId(vehicleJourney.getRouteId(), Route.class));
			for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
				vehicleJourneyAtStop.setStopPoint(getObjectFromId(vehicleJourneyAtStop.getStopPointId(), StopPoint.class));
				vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			}
			vehicleJourney.setTimeSlot(getObjectFromId(vehicleJourney.getTimeSlotId(), TimeSlot.class));
			vehicleJourney.setLine(line);
		}
	}

	private void connectStopPoints() 
	{
		for(StopPoint stopPoint : stopPoints)
		{
			stopPoint.setContainedInStopArea(getObjectFromId(stopPoint.getContainedInStopAreaId(), StopArea.class));
			//stopPoint.setLine(getObjectFromId(stopPoint.getLineIdShortcut(), Line.class));
			stopPoint.setLine(line);
			if(ptNetwork != null && ptNetwork.getObjectId().equals(stopPoint.getPtNetworkIdShortcut()))
			{

				stopPoint.setPtNetwork(ptNetwork);
			}
			else
			{

				//TODO : throw exception ???
			}
			//			
			//			for (PTLink ptLink : ptLinks) {
			//				if(ptLink.getStartOfLink().equals(stopPoint) || ptLink.getEndOfLink().equals(stopPoint))
			//					stopPoint.setRoute(ptLink.getRoute());
			//			}
			for (Facility facility : facilities)
			{
				if(facility.getStopPoint() != null && facility.getStopPoint().equals(stopPoint))
					stopPoint.addFacility(facility);
			}
		}
	}


	private void connectStopAreas() 
	{
		for(StopArea stopArea : stopAreas)
		{
			stopArea.setAreaCentroid(getObjectFromId(stopArea.getAreaCentroidId(), AreaCentroid.class));
			stopArea.setContainedStopAreas(getObjectsFromIds(stopArea.getContainedStopIds(), StopArea.class));
			if(stopArea.getContainedStopAreas() != null)
			{
				for(StopArea childStopArea : stopArea.getContainedStopAreas())
				{
					childStopArea.setParentStopArea(stopArea);
				}
			}
			stopArea.setContainedStopPoints(getObjectsFromIds(stopArea.getContainedStopIds(), StopPoint.class));
			//no need to set containedInStopArea in StopPoint : it is already done in connectStopPoints method...
			/*
			if(stopArea.getRestrictionConstraints() != null)
			{
				for (RestrictionConstraint constraint : stopArea.getRestrictionConstraints()) 
				{
					Line tmpLine = getObjectFromId(constraint.getLineIdShortCut(), Line.class);
					constraint.setLine(tmpLine);
					constraint.setStopAreas(stopAreas);
					if (tmpLine == null) {
						logger.debug("ITL " + constraint.getName() + " (" + constraint.getAreaId() + "," + constraint.getLineIdShortCut() + ") HAS NO LINE.");
					} else {
						logger.debug("ITL " + constraint.getName() + " (" + constraint.getAreaId() + "," + constraint.getLineIdShortCut() + ") HAS A LINE.");
					}
				}
				}
				*/

				for (Facility facility : facilities)
				{
					if(facility.getStopArea() != null && facility.getStopArea().equals(stopArea))
						stopArea.addFacility(facility);
				}
			
		}
		/*
		if (StopArea.getUnvalidRestrictionConstraints() != null) {
			for (RestrictionConstraint constraint : StopArea.getUnvalidRestrictionConstraints()) {
				Line tmpLine = getObjectFromId(constraint.getLineIdShortCut(), Line.class);
				constraint.setLine(tmpLine);
				logger.debug("ITL " + constraint.getName() + " (" + constraint.getAreaId() + "," + constraint.getLineIdShortCut() + ") HAS NO STOP AREA.");
				if (tmpLine == null) {
					logger.debug("ITL " + constraint.getName() + " (" + constraint.getAreaId() + "," + constraint.getLineIdShortCut() + ") HAS NO LINE.");
				} else {
					logger.debug("ITL " + constraint.getName() + " (" + constraint.getAreaId() + "," + constraint.getLineIdShortCut() + ") HAS A LINE.");
				}
			}
		}
		*/
	}

	private void connectAreaCentroids() 
	{
		for(AreaCentroid areaCentroid : areaCentroids)
		{
			areaCentroid.setContainedInStopArea(getObjectFromId(areaCentroid.getContainedInStopAreaId(), StopArea.class));
		}
	}

	private void connectConnectionLinks() 
	{
		for(ConnectionLink connectionLink : connectionLinks)
		{
			StopArea startOfLink = getObjectFromId(connectionLink.getStartOfLinkId(), StopArea.class);
			if(startOfLink != null){
				connectionLink.setStartOfLink(startOfLink);
				startOfLink.addConnectionLink(connectionLink);
			}
			StopArea endOfLink = getObjectFromId(connectionLink.getEndOfLinkId(), StopArea.class);
			if(endOfLink != null)
			{
				connectionLink.setEndOfLink(endOfLink);
				endOfLink.addConnectionLink(connectionLink);
			}

			for (Facility facility : facilities)
			{
				if(facility.getConnectionLink() != null && facility.getConnectionLink().equals(connectionLink))
					connectionLink.addFacility(facility);
			}
		}
	}


	private void connectTimetables() 
	{
		for(Timetable timetable : timetables)
		{
			timetable.setVehicleJourneys(getObjectsFromIds(timetable.getVehicleJourneyIds(), VehicleJourney.class));
			if(timetable.getVehicleJourneys() != null)
			{
				for(VehicleJourney vehicleJourney : timetable.getVehicleJourneys())
				{
					vehicleJourney.addTimetable(timetable);
				}
			}
		}
	}

	private void connectAccessLinks() 
	{
		for(AccessLink accessLink : accessLinks)
		{
			StopArea stopArea = (getObjectFromId(accessLink.getStartOfLinkId(), StopArea.class) != null) ? getObjectFromId(accessLink.getStartOfLinkId(), StopArea.class) :
				getObjectFromId(accessLink.getEndOfLinkId(), StopArea.class);
			if(stopArea != null){
				accessLink.setStopArea(stopArea);
				stopArea.addAccessLink(accessLink);
			}
			AccessPoint accessPoint = (getObjectFromId(accessLink.getStartOfLinkId(), AccessPoint.class) != null) ? getObjectFromId(accessLink.getStartOfLinkId(), AccessPoint.class) :
				getObjectFromId(accessLink.getEndOfLinkId(), AccessPoint.class);
			if(accessPoint != null)
			{
				accessLink.setAccessPoint(accessPoint);
				accessPoint.addAccessLink(accessLink);
			}
		}
	}

	private void connectGroupOfLines()
	{
		for (GroupOfLine groupOfLine : groupOfLines) 
		{
			groupOfLine.setLines(getObjectsFromIds(groupOfLine.getLineIds(), Line.class));
		}
	}

	private void connectFacilities() 
	{
		for (Facility facility : facilities) 
		{
			if(facility.getStopAreaId() != null)
				facility.setStopArea(getObjectFromId(facility.getStopAreaId(), StopArea.class));
			if(facility.getStopPointId() != null)
				facility.setStopPoint(getObjectFromId(facility.getStopPointId(), StopPoint.class));
			if(facility.getConnectionLinkId() != null)
				facility.setConnectionLink(getObjectFromId(facility.getConnectionLinkId(), ConnectionLink.class));
			if(facility.getLineId() != null)
				facility.setLine(getObjectFromId(facility.getLineId(), Line.class));
		}
	}


	@SuppressWarnings("unchecked")
	private <T extends NeptuneIdentifiedObject> List<T> getObjectsFromIds(List<String> ids, Class<T> dictionaryClass)
	{
		Map<String, ? extends NeptuneIdentifiedObject> dictionary =  populatedDictionaries.get(dictionaryClass);
		List<T> objects = new ArrayList<T>();

		if(dictionary != null && ids != null)
		{
			for(String id : ids)
			{
				T object = (T)dictionary.get(id);
				if(object != null)
				{
					objects.add(object);
				}
			}
		}

		if(objects.size() == 0)
		{
			objects = null;
		}

		return objects;
	}

	@SuppressWarnings("unchecked")
	private <T extends NeptuneIdentifiedObject> T getObjectFromId(String id, Class<T> dictionaryClass)
	{

		Map<String, ? extends NeptuneIdentifiedObject> dictionary =  populatedDictionaries.get(dictionaryClass);
		T object = null;

		if (dictionary != null)
			object = (T)dictionary.get(id);

		return object;
	}
}
