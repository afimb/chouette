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

import fr.certu.chouette.exchange.xml.neptune.model.NeptuneRoutingConstraint;
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
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.TimeSlot;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ImportedItems;

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
      connectGroupOfLines();
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

      // add every objets for validation purpose
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

      line.setImportedItems(item);
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
               startPoint.setPosition(position++);
               startPoint.setRoute(route);
               if (!stopPoints.contains(startPoint))
               {
                  stopPoints.add(startPoint);
               }
               StopPoint endPoint = getObjectFromId(ptLink.getEndOfLinkId(), StopPoint.class);
               if (!stopPoints.contains(endPoint))
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
         Route route = getObjectFromId(journeyPattern.getRouteId(), Route.class);
         journeyPattern.setRoute(route);
         // Neptune norm said Route must have JourneyPatternId but XSD accepts if missing
         // in this case, let add journeyPattern here
         if (route != null) route.addJourneyPattern(journeyPattern);
         journeyPattern.setStopPoints(getObjectsFromIds(journeyPattern.getStopPointIds(), StopPoint.class));
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
            journeyPattern.addVehicleJourney(vehicleJourney);
         vehicleJourney.setRoute(getObjectFromId(vehicleJourney.getRouteId(), Route.class));
         for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops())
         {
            vehicleJourneyAtStop.setStopPoint(getObjectFromId(vehicleJourneyAtStop.getStopPointId(), StopPoint.class));
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
         // stopPoint.setLine(getObjectFromId(stopPoint.getLineIdShortcut(),
         // Line.class));
         stopPoint.setLine(line);
         if (ptNetwork != null && ptNetwork.getObjectId().equals(stopPoint.getPtNetworkIdShortcut()))
         {

            stopPoint.setPtNetwork(ptNetwork);
         }
         else if (stopPoint.getPtNetworkIdShortcut() != null)
         {
            logger.warn("unknown PtNetworkIdShortcut " + stopPoint.getPtNetworkIdShortcut() + " for StopPoint "
                  + stopPoint.getObjectId());
            // TODO : report
         }

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
         stopArea.setAreaCentroid(getObjectFromId(stopArea.getAreaCentroidId(), AreaCentroid.class));
         stopArea.setContainedStopAreas(getObjectsFromIds(stopArea.getContainedStopIds(), StopArea.class));
         if (stopArea.getContainedStopAreas() != null)
         {
            for (StopArea childStopArea : stopArea.getContainedStopAreas())
            {
               childStopArea.addParent(stopArea);
            }
         }
         stopArea.setContainedStopPoints(getObjectsFromIds(stopArea.getContainedStopIds(), StopPoint.class));

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
         if (timetable.getVehicleJourneys() != null)
         {
            for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys())
            {
               vehicleJourney.addTimetable(timetable);
            }
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
         if (facility.getStopAreaId() != null)
            facility.setStopArea(getObjectFromId(facility.getStopAreaId(), StopArea.class));
         if (facility.getStopPointId() != null)
            facility.setStopPoint(getObjectFromId(facility.getStopPointId(), StopPoint.class));
         if (facility.getConnectionLinkId() != null)
            facility.setConnectionLink(getObjectFromId(facility.getConnectionLinkId(), ConnectionLink.class));
         if (facility.getLineId() != null)
            facility.setLine(getObjectFromId(facility.getLineId(), Line.class));
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
         for (String id : ids)
         {
            T object = (T) dictionary.get(id);
            if (object != null)
            {
               objects.add(object);
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

      return object;
   }
}
