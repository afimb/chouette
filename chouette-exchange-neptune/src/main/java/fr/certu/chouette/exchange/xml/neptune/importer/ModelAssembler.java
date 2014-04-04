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

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.xml.neptune.model.AreaCentroid;
import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.Facility;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourney;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

/**
 * Assemble every extracted object to object referring it with its objectId
 * 
 */
public class ModelAssembler
{
	private static final Logger                                                                           logger                    = Logger
			.getLogger(ModelAssembler.class);

	/**
	 * extracted line
	 */
	@Getter
	@Setter
	private Line                                                                                          line;
	/**
	 * extracted routes
	 */
	@Getter
	@Setter
	private List<Route>                                                                                   routes;
	/**
	 * extracted companies
	 */
	@Getter
	@Setter
	private List<Company>                                                                                 companies;
	/**
	 * extracted network
	 */
	@Getter
	@Setter
	private PTNetwork                                                                                     ptNetwork;
	/**
	 * extracted journey patterns
	 */
	@Getter
	@Setter
	private List<JourneyPattern>                                                                          journeyPatterns;
	/**
	 * extracted ptLinks
	 */
	@Getter
	@Setter
	private List<PTLink>                                                                                  ptLinks;
	/**
	 * extracted vehicle journeys
	 */
	@Getter
	@Setter
	private List<VehicleJourney>                                                                          vehicleJourneys;
	/**
	 * extracted stop points
	 */
	@Getter
	@Setter
	private List<StopPoint>                                                                               stopPoints;
	/**
	 * extracted stop areas
	 */
	@Getter
	@Setter
	private List<StopArea>                                                                                stopAreas;
	/**
	 * extracted area centroids
	 */
	@Getter
	@Setter
	private List<AreaCentroid>                                                                            areaCentroids;
	/**
	 * extracted connection links
	 */
	@Getter
	@Setter
	private List<ConnectionLink>                                                                          connectionLinks;
	/**
	 * extracted time tables
	 */
	@Getter
	@Setter
	private List<Timetable>                                                                               timetables;
	/**
	 * extracted access links
	 */
	@Getter
	@Setter
	private List<AccessLink>                                                                              accessLinks;
	/**
	 * extracted access points
	 */
	@Getter
	@Setter
	private List<AccessPoint>                                                                             accessPoints;
	/**
	 * extracted group of lines
	 */
	@Getter
	@Setter
	private List<GroupOfLine>                                                                             groupOfLines;
	/**
	 * extracted facilities
	 */
	@Getter
	@Setter
	private List<Facility>                                                                                facilities;
	/**
	 * extracted time slots
	 */
	@Getter
	@Setter
	private List<TimeSlot>                                                                                timeSlots;
	/**
	 * extracted temporary routing constraint relations
	 */
	@Getter
	@Setter
	private List<NeptuneRoutingConstraint>                                                                routingConstraints;

	/**
	 * map of object's dictionaries
	 */
	private Map<Class<? extends NeptuneIdentifiedObject>, Map<String, ? extends NeptuneIdentifiedObject>> populatedDictionaries     = new HashMap<Class<? extends NeptuneIdentifiedObject>, Map<String, ? extends NeptuneIdentifiedObject>>();
	/**
	 * dictionary (map) of line object ids
	 */
	private Map<String, Line>                                                                             linesDictionary           = new HashMap<String, Line>();
	/**
	 * dictionary (map) of route object ids
	 */
	private Map<String, Route>                                                                            routesDictionary          = new HashMap<String, Route>();
	/**
	 * dictionary (map) of company object ids
	 */
	private Map<String, Company>                                                                          companiesDictionary       = new HashMap<String, Company>();
	/**
	 * dictionary (map) of journeyPattern object ids
	 */
	private Map<String, JourneyPattern>                                                                   journeyPatternsDictionary = new HashMap<String, JourneyPattern>();
	/**
	 * dictionary (map) of ptLink object ids
	 */
	private Map<String, PTLink>                                                                           ptLinksDictionary         = new HashMap<String, PTLink>();
	/**
	 * dictionary (map) of vehicleJourney object ids
	 */
	private Map<String, VehicleJourney>                                                                   vehicleJourneysDictionary = new HashMap<String, VehicleJourney>();
	/**
	 * dictionary (map) of StopPoint object ids
	 */
	private Map<String, StopPoint>                                                                        stopPointsDictionary      = new HashMap<String, StopPoint>();
	/**
	 * dictionary (map) of stopArea object ids
	 */
	private Map<String, StopArea>                                                                         stopAreasDictionary       = new HashMap<String, StopArea>();
	/**
	 * dictionary (map) of areaCentroid object ids
	 */
	private Map<String, AreaCentroid>                                                                     areaCentroidsDictionary   = new HashMap<String, AreaCentroid>();
	/**
	 * dictionary (map) of connectionLink object ids
	 */
	private Map<String, ConnectionLink>                                                                   connectionLinksDictionary = new HashMap<String, ConnectionLink>();
	/**
	 * dictionary (map) of timetable object ids
	 */
	private Map<String, Timetable>                                                                        timetablesDictionary      = new HashMap<String, Timetable>();
	/**
	 * dictionary (map) of accessLink object ids
	 */
	private Map<String, AccessLink>                                                                       accessLinksDictionary     = new HashMap<String, AccessLink>();
	/**
	 * dictionary (map) of accessPoint object ids
	 */
	private Map<String, AccessPoint>                                                                      accessPointsDictionary    = new HashMap<String, AccessPoint>();
	/**
	 * dictionary (map) of grouopOfLine object ids
	 */
	private Map<String, GroupOfLine>                                                                      groupOfLinesDictionary    = new HashMap<String, GroupOfLine>();
	/**
	 * dictionary (map) of facility object ids
	 */
	private Map<String, Facility>                                                                         facilitiesDictionary      = new HashMap<String, Facility>();
	/**
	 * dictionary (map) of timeSlot object ids
	 */
	private Map<String, TimeSlot>                                                                         timeSlotDictionary        = new HashMap<String, TimeSlot>();

	private ReportItem importReport;

	private PhaseReportItem validationReport;

	private String sourceFile;

	private SharedImportedData sharedData;

	private UnsharedImportedData unsharedData;

	public ModelAssembler(String sourceFile, SharedImportedData sharedData, UnsharedImportedData unsharedData, ReportItem report, PhaseReportItem validationItem)
	{
		this.importReport = report;
		this.validationReport = validationItem;
		this.sourceFile = sourceFile;
		this.sharedData = sharedData;
		this.unsharedData = unsharedData;
	}

	/**
	 * connect all objects
	 */
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
		connectRoutingConstraints();
		connectTimetables();
		connectAccessLinks();
		connectAccessPoints();
		connectGroupOfLines();

		validate();

	}

	private void validate()
	{

	}

	/**
	 * fill each objectId dictionary
	 */
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
		populateDictionnary(timeSlots, timeSlotDictionary);
	}

	/**
	 * populate specific object dictionary
	 * 
	 * @param <T>
	 *           object type
	 * @param list
	 *           objects
	 * @param dictionnary
	 *           dictionary to fill
	 */
	private <T extends NeptuneIdentifiedObject> void populateDictionnary(List<T> list, Map<String, T> dictionnary)
	{

		for (T item : list)
		{
			if (item != null && item.getObjectId() != null)
			{
				dictionnary.put(item.getObjectId(), item);
			}
		}
		if (list.size() > 0)
		{
			populatedDictionaries.put(list.get(0).getClass(), dictionnary);
			// logger.debug(list.get(0).getClass().getName()+" count = "+dictionnary.size());
		}
	}

	/**
	 * connect direct relation between line and other objects
	 */
	private void connectLine()
	{
		if (companies != null && companies.size() == 1)
		{
			// connect first company for internal model (in file, companies are
			// connected to vehiclejourneys or just present in file)
			line.setCompany(companies.get(0));
		}
		line.setCompanies(companies);

		// bypass objectId check : connect ptnetwork in file to line
		line.setPtNetwork(ptNetwork);		

		// bypass objectId check : connect every route in file to line
		line.setRoutes(routes);

		if (!groupOfLines.isEmpty())
		{
			for (GroupOfLine groupOfLine : groupOfLines)
			{
				line.addGroupOfLine(groupOfLine);
			}
		}

		for (Facility facility : facilities)
		{
			if (facility.getLine() != null && facility.getLine().equals(line))
				line.addFacility(facility);
		}

	}

	/**
	 * connect routing constraint relation between line and ITL stop area
	 */
	private void connectRoutingConstraints()
	{
		for (NeptuneRoutingConstraint restriction : routingConstraints)
		{
			if (restriction.getLineId() == null || !restriction.getLineId().equals(line.getObjectId()))
			{
				logger.warn("ITL with lineId = " + restriction.getLineId() + ": rejected");
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"ITL",restriction.getObjectId(),"lineId",restriction.getLineId());
				importReport.addItem(item);
			}
			for (String areaId : restriction.getRoutingConstraintIds())
			{
				StopArea area = stopAreasDictionary.get(areaId);
				if (area != null && area.getAreaType().equals(ChouetteAreaEnum.ITL))
				{
					line.addRoutingConstraint(area);
					area.addRoutingConstraintLine(line);
					area.addRoutingConstraintLineId(restriction.getLineId());
				}
				else
				{
					logger.warn("ITL with stopAreaId = " + areaId + ": rejected");
					ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"ITL",restriction.getObjectId(),"stopAreaId",areaId);
					importReport.addItem(item);
				}
			}
		}

	}

	/**
	 * connect direct relation between routes and other objects
	 */
	private void connectRoutes()
	{
		for (Route route : routes)
		{
			route.setJourneyPatterns(getObjectsFromIds(route.getJourneyPatternIds(), JourneyPattern.class));

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
					if (startPoint != null)
					{
						startPoint.setPosition(position++);
						startPoint.setRoute(route);
						if (!stopPoints.contains(startPoint))
						{
							stopPoints.add(startPoint);
						}
					}
					StopPoint endPoint = getObjectFromId(ptLink.getEndOfLinkId(), StopPoint.class);
					if (endPoint != null)
					{
						if (!stopPoints.contains(endPoint))
						{
							endPoint.setPosition(position);
							endPoint.setRoute(route);
							stopPoints.add(endPoint);
						}
					}
				}
				route.setStopPoints(stopPoints);
			}

			route.setLine(line);
		}
	}

	/**
	 * sort ptLinks
	 */
	private List<PTLink> sortPtLinks(List<PTLink> ptLinks)
	{
		if (ptLinks == null || ptLinks.isEmpty())
			return ptLinks;
		Map<String, PTLink> linkByStart = new HashMap<String, PTLink>();
		Map<String, PTLink> linkByEnd = new HashMap<String, PTLink>();

		for (PTLink ptLink : ptLinks)
		{
			linkByStart.put(ptLink.getStartOfLinkId(), ptLink);
			linkByEnd.put(ptLink.getEndOfLinkId(), ptLink);
		}

		Set<String> starts = new HashSet<String>();
		starts.addAll(linkByStart.keySet());
		starts.removeAll(linkByEnd.keySet());

		List<PTLink> sortedLinks = new ArrayList<PTLink>();

		if (!starts.isEmpty())
		{
			String start = starts.toArray(new String[0])[0];
			PTLink link = linkByStart.get(start);
			while (link != null)
			{
				sortedLinks.add(link);
				start = link.getEndOfLinkId();
				link = linkByStart.remove(start);
			}
		}

		return sortedLinks;
	}

	/**
	 * connect direct relation between companies and other objects
	 */
	private void connectCompanies()
	{
		// nothing to connect
	}

	/**
	 * connect direct relation between PTnetwork and other objects
	 */
	private void connectPTNetwork()
	{
		ptNetwork.setLines(getObjectsFromIds(ptNetwork.getLineIds(), Line.class));


	}

	/**
	 * connect direct relation between JourneyPatterns and other objects
	 */
	private void connectJourneyPatterns()
	{
		for (JourneyPattern journeyPattern : journeyPatterns)
		{
			journeyPattern.setStopPoints(getObjectsFromIds(journeyPattern.getStopPointIds(), StopPoint.class));
			if (journeyPattern.getStopPoints() == null)
			{
				logger.error("journeyPattern has no stoppoints , rejected "+journeyPattern.getObjectId());
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.EMPTY_JOURNEY_PATTERN,Report.STATE.WARNING,journeyPattern.getObjectId());
				importReport.addItem(item);
				continue;
			}
			for (StopPoint point : journeyPattern.getStopPoints())
			{
				if (journeyPattern.getArrivalStopPoint() == null || journeyPattern.getArrivalStopPoint().before(point))
				{
					journeyPattern.setArrivalStopPoint(point);
				}
				if (journeyPattern.getDepartureStopPoint() == null || journeyPattern.getDepartureStopPoint().after(point))
				{
					journeyPattern.setDepartureStopPoint(point);
				}
			}

			Route route = getObjectFromId(journeyPattern.getRouteId(), Route.class);
			journeyPattern.setRoute(route);
			// Neptune norm said Route must have JourneyPatternId but XSD accepts if missing
			// in this case, let add journeyPattern here
			if (route != null) 
				route.addJourneyPattern(journeyPattern);
			else
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"JourneyPattern",journeyPattern.getObjectId(),"routeId",journeyPattern.getRouteId());
				importReport.addItem(item);
			}

		}
	}

	/**
	 * connect direct relation between PTLinks and other objects
	 */
	private void connectPTLinks()
	{
		for (PTLink ptLink : ptLinks)
		{
			ptLink.setStartOfLink(getObjectFromId(ptLink.getStartOfLinkId(), StopPoint.class));
			ptLink.setEndOfLink(getObjectFromId(ptLink.getEndOfLinkId(), StopPoint.class));
			ptLink.setRoute(getObjectFromId(ptLink.getRouteId(), Route.class));
		}
	}

	/**
	 * connect direct relation between VehicleJourneys and other objects
	 */
	private void connectVehicleJourneys()
	{
		for (VehicleJourney vehicleJourney : vehicleJourneys)
		{
			vehicleJourney.setCompany(getObjectFromId(vehicleJourney.getCompanyId(), Company.class));
			JourneyPattern journeyPattern = getObjectFromId(vehicleJourney.getJourneyPatternId(), JourneyPattern.class);
			vehicleJourney.setJourneyPattern(journeyPattern);
			if (journeyPattern != null)
			{
				journeyPattern.addVehicleJourney(vehicleJourney);
			}
			else
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"VehicleJourney",vehicleJourney.getObjectId(),"journeyPatternId",vehicleJourney.getJourneyPatternId());
				importReport.addItem(item);
			}
			vehicleJourney.setRoute(getObjectFromId(vehicleJourney.getRouteId(), Route.class));
			if (vehicleJourney.getRoute() == null)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"VehicleJourney",vehicleJourney.getObjectId(),"routeId",vehicleJourney.getRouteId());
				importReport.addItem(item);
			}
			for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops())
			{
				vehicleJourneyAtStop.setStopPoint(getObjectFromId(vehicleJourneyAtStop.getStopPointId(), StopPoint.class));
				if (vehicleJourneyAtStop.getStopPoint() == null)
				{
					ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"VehicleJourneyAtStop",vehicleJourney.getObjectId(),"stopPointId",vehicleJourneyAtStop.getStopPointId());
					importReport.addItem(item);
				}
				vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
			}
			vehicleJourney.setTimeSlot(getObjectFromId(vehicleJourney.getTimeSlotId(), TimeSlot.class));
			vehicleJourney.setLine(line);
		}
	}

	/**
	 * connect direct relation between StopPoints and other objects
	 */
	private void connectStopPoints()
	{
		for (StopPoint stopPoint : stopPoints)
		{
			stopPoint.setContainedInStopArea(getObjectFromId(stopPoint.getContainedInStopAreaId(), StopArea.class));
			if (stopPoint.getContainedInStopArea() == null)
			{
				ReportItem item = new ExchangeReportItem(ExchangeReportItem.KEY.BAD_REFERENCE,Report.STATE.WARNING,"StopPoint",stopPoint.getObjectId(),"containedInStopArea",stopPoint.getContainedInStopAreaId());
				importReport.addItem(item);
			}

			//			stopPoint.setLine(line);
			//			if (ptNetwork != null && ptNetwork.getObjectId().equals(stopPoint.getPtNetworkIdShortcut()))
			//			{
			//
			//				stopPoint.setPtNetwork(ptNetwork);
			//			}

			for (Facility facility : facilities)
			{
				if (facility.getStopPoint() != null && facility.getStopPoint().equals(stopPoint))
					stopPoint.addFacility(facility);
			}
		}
	}

	/**
	 * connect direct relation between StopAreas and other objects
	 */
	private void connectStopAreas()
	{
		for (StopArea stopArea : stopAreas)
		{
			AreaCentroid centroid = getObjectFromId(stopArea.getAreaCentroidId(), AreaCentroid.class);
			if (centroid != null)
			{
				centroid.populateStopArea(stopArea);
			}
			if (!stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				if (stopArea.getAreaType().equals(ChouetteAreaEnum.Quay) || stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition))
				{
					stopArea.setContainedStopPoints(getObjectsFromIds(stopArea.getContainedStopIds(), StopPoint.class));
					stopArea.setContainedStopAreas(null);
				}
				else
				{
					stopArea.setContainedStopAreas(getObjectsFromIds(stopArea.getContainedStopIds(), StopArea.class));
					stopArea.setContainedStopPoints(null);
				}
				stopArea.setRoutingConstraintAreas(null);

				if (stopArea.getContainedStopAreas() != null)
				{
					for (StopArea childStopArea : stopArea.getContainedStopAreas())
					{
						childStopArea.setParent(stopArea);
					}
				}
			}
			else if (stopArea.getAreaType().equals(ChouetteAreaEnum.ITL))
			{
				stopArea.setRoutingConstraintAreas(getObjectsFromIds(stopArea.getContainedStopIds(), StopArea.class));
				stopArea.setContainedStopAreas(null);
				stopArea.setContainedStopPoints(null);
			}


			for (Facility facility : facilities)
			{
				if (facility.getStopArea() != null && facility.getStopArea().equals(stopArea))
					stopArea.addFacility(facility);
			}

		}
	}

	/**
	 * connect direct relation between AreaCentroids and other objects
	 */
	private void connectAreaCentroids()
	{
		for (AreaCentroid areaCentroid : areaCentroids)
		{
			areaCentroid.setContainedInStopArea(getObjectFromId(areaCentroid.getContainedInStopAreaId(), StopArea.class));
		}
	}

	/**
	 * connect direct relation between ConnectionLinks and other objects
	 */
	private void connectConnectionLinks()
	{
		for (ConnectionLink connectionLink : connectionLinks)
		{
			StopArea startOfLink = getObjectFromId(connectionLink.getStartOfLinkId(), StopArea.class);
			if (startOfLink != null)
			{
				connectionLink.setStartOfLink(startOfLink);
				startOfLink.addConnectionLink(connectionLink);
			}
			StopArea endOfLink = getObjectFromId(connectionLink.getEndOfLinkId(), StopArea.class);
			if (endOfLink != null)
			{
				connectionLink.setEndOfLink(endOfLink);
				endOfLink.addConnectionLink(connectionLink);
			}

			for (Facility facility : facilities)
			{
				if (facility.getConnectionLink() != null && facility.getConnectionLink().equals(connectionLink))
					connectionLink.addFacility(facility);
			}
		}
	}

	/**
	 * connect direct relation between Timetables and other objects
	 */
	private void connectTimetables()
	{
		for (Timetable timetable : timetables)
		{
			timetable.setVehicleJourneys(getObjectsFromIds(timetable.getVehicleJourneyIds(), VehicleJourney.class));
			if (timetable.getVehicleJourneys() == null || timetable.getVehicleJourneys().isEmpty() )
			{
				List<DbVehicleJourney> journeys = getObjectsFromIds(timetable.getVehicleJourneyIds(), DbVehicleJourney.class);
				if (journeys != null)
				{
					for (VehicleJourney dbVehicleJourney : journeys)
					{
						timetable.addVehicleJourney(dbVehicleJourney);
					}
				}
			}

			if (timetable.getVehicleJourneys() != null && !timetable.getVehicleJourneys().isEmpty() )
			{
				for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys())
				{
					vehicleJourney.addTimetable(timetable);
				}
			}
			else 
			{
				logger.warn("timetable "+timetable.getComment()+" ("+timetable.getObjectId()+" ) has no VehicleJourney");
			}
		}
	}

	/**
	 * connect direct relation between AccessLinks and other objects
	 */
	private void connectAccessLinks()
	{
		for (AccessLink accessLink : accessLinks)
		{
			StopArea stopArea = (getObjectFromId(accessLink.getStartOfLinkId(), StopArea.class) != null) ? getObjectFromId(
					accessLink.getStartOfLinkId(), StopArea.class) : getObjectFromId(accessLink.getEndOfLinkId(),
							StopArea.class);
					if (stopArea != null)
					{
						accessLink.setStopArea(stopArea);
						stopArea.addAccessLink(accessLink);
					}
					AccessPoint accessPoint = (getObjectFromId(accessLink.getStartOfLinkId(), AccessPoint.class) != null) ? getObjectFromId(
							accessLink.getStartOfLinkId(), AccessPoint.class) : getObjectFromId(accessLink.getEndOfLinkId(),
									AccessPoint.class);
							if (accessPoint != null)
							{
								accessLink.setAccessPoint(accessPoint);
								accessPoint.addAccessLink(accessLink);
							}
		}
	}

	/**
	 * connect direct relation between AccessPoints and other objects
	 */
	private void connectAccessPoints()
	{
		boolean missingContainer = false;
		for (AccessPoint accessPoint : accessPoints)
		{
			StopArea stopArea = getObjectFromId(accessPoint.getContainedInStopArea(), StopArea.class);
			if (stopArea != null)
			{
				accessPoint.setContainedIn(stopArea);
			}
			else
			{
				accessPoint.setContainedInStopArea(null);
				missingContainer = true;
			}
		}
		if (missingContainer)
		{
			for (AccessLink link : accessLinks) 
			{
				if (link.getAccessPoint().getContainedIn() == null)
				{
					StopArea area = link.getStopArea();
					if (area != null)
					{
						if (area.getAreaType().equals(ChouetteAreaEnum.BoardingPosition) || area.getAreaType().equals(ChouetteAreaEnum.Quay))
						{
							if (area.getParent() != null) area = area.getParent();
							link.getAccessPoint().setContainedIn(area);
						}
					}
				}
			} 
		}

	}

	/**
	 * connect direct relation between GroupOfLines and other objects
	 */
	private void connectGroupOfLines()
	{
		if (groupOfLines != null)
		{
			for (GroupOfLine groupOfLine : groupOfLines)
			{
				groupOfLine.setLines(getObjectsFromIds(groupOfLine.getLineIds(), Line.class));
			}
		}


	}

	/**
	 * connect direct relation between Facilities and other objects
	 */
	private void connectFacilities()
	{
		for (Facility facility : facilities)
		{
			if (facility.getContainedIn() != null)
			{
				StopArea bean = getObjectFromId(facility.getContainedIn(), StopArea.class);
				if (bean != null)
				{
					facility.setContainedInStopArea(bean);
				}
			}
			if (facility.getStopAreaId() != null)
			{
				StopArea bean = getObjectFromId(facility.getStopAreaId(), StopArea.class);
				if (bean != null)
				{
					facility.setStopArea(bean);
					bean.addFacility(facility);
				}
			}
			if (facility.getStopPointId() != null)
			{
				StopPoint bean = getObjectFromId(facility.getStopPointId(), StopPoint.class);
				if (bean != null)
				{
					facility.setStopPoint(bean);
					bean.addFacility(facility);
				}
			}
			if (facility.getConnectionLinkId() != null)
			{
				ConnectionLink bean = getObjectFromId(facility.getConnectionLinkId(), ConnectionLink.class);
				if (bean != null)
				{
					facility.setConnectionLink(bean);
					bean.addFacility(facility);
				}
			}
			if (facility.getLineId() != null)
			{
				Line bean = getObjectFromId(facility.getLineId(), Line.class);
				if (bean != null)
				{
					facility.setLine(bean);
					bean.addFacility(facility);
				}
			}
		}
	}

	/**
	 * extract objects form dictionary from a list of object ids
	 * 
	 * @param <T>
	 *           object type
	 * @param ids
	 *           object ids required
	 * @param dictionaryClass
	 *           class of object
	 * @return extracted objects
	 */
	@SuppressWarnings("unchecked")
	private <T extends NeptuneIdentifiedObject> List<T> getObjectsFromIds(List<String> ids, Class<T> dictionaryClass)
	{
		Map<String, ? extends NeptuneIdentifiedObject> dictionary = populatedDictionaries.get(dictionaryClass);
		List<T> objects = new ArrayList<T>();

		if (dictionary != null && ids != null)
		{
			// logger.debug(dictionaryClass.getName()+" count = "+dictionary.size());
			for (String id : ids)
			{
				T object = (T) dictionary.get(id);
				if (object != null)
				{
					objects.add(object);
				}
				else
				{
					// logger.warn("object "+id+" not found in file");
				}
			}
		}

		if (objects.size() == 0)
		{
			objects = null;
		}

		return objects;
	}

	/**
	 * extract object form dictionary from its object id
	 * 
	 * @param <T>
	 *           object type
	 * @param id
	 *           requered object id
	 * @param dictionaryClass
	 *           object class
	 * @return object (null if not found)
	 */
	@SuppressWarnings("unchecked")
	private <T extends NeptuneIdentifiedObject> T getObjectFromId(String id, Class<T> dictionaryClass)
	{

		Map<String, ? extends NeptuneIdentifiedObject> dictionary = populatedDictionaries.get(dictionaryClass);
		T object = null;

		if (dictionary != null)
			object = (T) dictionary.get(id);

		if (object == null && id != null)
		{
			logger.warn("object not found "+id+" in "+dictionaryClass.getName());
		}

		return object;
	}

	//	private void addValidationError(String checkPointKey,DetailReportItem item)
	//	{
	//		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
	//		checkPoint.addItem(item);
	//
	//	}
	//
	//	private void prepareCheckPoint(String checkPointKey)
	//	{
	//		CheckPointReportItem checkPoint = validationReport.getItem(checkPointKey);
	//		if (!checkPoint.hasItems()) checkPoint.setStatus(Report.STATE.OK);
	//	}

}
