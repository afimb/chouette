/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.gtfs.importer;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.LineProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.VehicleJourneyAtStopProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.gtfs.refactor.importer.GtfsImporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTrip;
import fr.certu.chouette.model.neptune.CalendarDay;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.exchange.report.LimitedExchangeReportItem;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.plugin.report.Report;

/**
 * convert GTFS raw data structure to Chouette internal one
 * 
 */

public class NeptuneConverter
{
   private static Logger logger = Logger.getLogger(NeptuneConverter.class);

   private GtfsImporter importer;

   public NeptuneConverter(GtfsImporter importer)
   {
      this.importer = importer;
   }

   /**
    * convert Gfts model to Chouette model
    * 
    * @param prefix
    *           objectId prefix
    * @param data
    *           Gtfs data
    * @param maxDistanceForCommercialStop
    *           maximum distance in meter to merge same named BoardingPosition
    *           in one CommercialStop
    * @param ignoreLastWord
    *           ignore last word for BoardingPosition name comparison
    * @param ignoreEndCharacters
    *           ignore last characters for BoardingPosition name comparison
    * @param maxDistanceForConnectionLink
    *           maximum distance in meter to connect CommercialStops with a
    *           ConnectionLink
    * @return a Chouette internal model nearly connected
    * @throws Exception
    */
   public ModelAssembler convert(boolean optimizeMemory, String prefix,
         String incrementalPrefix, double maxDistanceForCommercialStop,
         boolean ignoreLastWord, int ignoreEndCharacters,
         double maxDistanceForConnectionLink, Report report) throws Exception
   {
      LineProducer lineProducer = new LineProducer();
      RouteProducer routeProducer = new RouteProducer();
      DbVehicleJourneyFactory vjFactory = new DbVehicleJourneyFactory(prefix,
            optimizeMemory);
      VehicleJourneyProducer vehicleJourneyProducer = new VehicleJourneyProducer();
      VehicleJourneyAtStopProducer vehicleJourneyAtStopProducer = new VehicleJourneyAtStopProducer();
      vehicleJourneyProducer.setFactory(vjFactory);
      vehicleJourneyAtStopProducer.setFactory(vjFactory);
      ModelAssembler assembler = new ModelAssembler();
      AbstractModelProducer.setPrefix(prefix);
      AbstractModelProducer.setIncrementalPrefix(incrementalPrefix);

      convertNetworks(prefix, assembler, report);

      convertCompanies(assembler, report);

      // lines, routes
      List<Line> lines = new ArrayList<Line>();
      List<Route> routes = new ArrayList<Route>();
      Map<String, Route> mapRouteByRouteId = new HashMap<String, Route>();

      logger.info("process routes :" + importer.getRouteById().getLength());
      Map<String, Integer> mapRouteExtensionByRouteId = new HashMap<String, Integer>();

      for (GtfsRoute gtfsRoute : importer.getRouteById())
      {
         // @TODO : check errors

         // must produce 2 routes : one for each direction;
         // at end of processing, empty route will be destroyed
         Line line = lineProducer.produce(gtfsRoute, report);
         lines.add(line);

         Route route0 = routeProducer.produce(gtfsRoute, report);
         Route route1 = new Route();
         route1.setName(route0.getName());
         route1.setPublishedName(route0.getPublishedName());
         route1.setComment(route0.getComment());
         route1.setObjectId(route0.getObjectId() + "_1");
         route0.setObjectId(route0.getObjectId() + "_0");
         route0.setLine(line);
         route1.setWayBackRouteId(route0.getObjectId());
         route0.setWayBackRouteId(route1.getObjectId());
         mapRouteByRouteId.put(gtfsRoute.getRouteId() + "_0", route0);
         line.addRoute(route0);
         routes.add(route0);
         mapRouteExtensionByRouteId.put(route0.getObjectId(),
               Integer.valueOf(1));
         route1.setLine(line);
         mapRouteByRouteId.put(gtfsRoute.getRouteId() + "_1", route1);
         line.addRoute(route1);
         routes.add(route1);
         mapRouteExtensionByRouteId.put(route1.getObjectId(),
               Integer.valueOf(1));
      }
      assembler.setLines(lines);
      assembler.setRoutes(routes);

      // stopareas
      List<StopArea> commercials = new ArrayList<StopArea>();
      List<StopArea> areas = new ArrayList<StopArea>();
      Map<String, StopArea> mapStopAreasByStopId = new HashMap<String, StopArea>();
      convertStopAreas(report, areas, commercials, mapStopAreasByStopId,
            maxDistanceForCommercialStop, ignoreLastWord, ignoreEndCharacters);
      assembler.setStopAreas(areas);

      // timetables
      Map<String, Timetable> mapTimetableByServiceId = convertTimetables(
            assembler, report);
      Map<String, Timetable> mapTimetableAfterMidnightByServiceId = new HashMap<String, Timetable>();
      for (Entry<String, Timetable> entry : mapTimetableByServiceId.entrySet())
      {
         mapTimetableAfterMidnightByServiceId.put(entry.getKey(),
               cloneTimetableAfterMidnight(entry.getValue()));
      }
      assembler.getTimetables().addAll(
            mapTimetableAfterMidnightByServiceId.values());

      // vehicleJourneys , vehicleJourneyAtStops, JourneyPatterns and
      // StopPoints
      // build in to steps :
      // first dispatch stopTimes to trips
      // next match stoptimes sequence of each trip to a journey pattern;
      // and identify stops to be affected to routes

      List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();
      List<StopPoint> stopPoints = new ArrayList<StopPoint>();
      List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
      Map<String, VehicleJourney> mapVehicleJourneyByTripId = new HashMap<String, VehicleJourney>();
      Map<String, JourneyPattern> mapJourneyPatternByStopSequence = new HashMap<String, JourneyPattern>();
      Map<String, StopPoint> mapStopPointbyJourneyPatternRank = new HashMap<String, StopPoint>();

      logger.info("process vehicleJourneys :"
            + importer.getTripById().getLength());
      int count = 0;
      LimitedExchangeReportItem vjReport = new LimitedExchangeReportItem(
            LimitedExchangeReportItem.KEY.VEHICLE_JOURNEY_ANALYSE,
            Report.STATE.OK);
      VjasComparator vjasComparator = new VjasComparator();
      for (GtfsTrip gtfsTrip : importer.getTripById())
      {
         count++;
         if (count % 1000 == 0)
         {
            logger.debug("process " + count + " vehicleJourneys ...");
         }

         VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(
               gtfsTrip, report);

         List<VehicleJourneyAtStop> lvjas = new ArrayList<>();
         boolean afterMidnight = true;
         
         // TODO [DSU iterator ?]
         for (Iterator<GtfsStopTime> stopTimesOfATrip = importer
               .getStopTimeByTrip().values(gtfsTrip.getTripId()).iterator(); stopTimesOfATrip
               .hasNext();)
         {
            GtfsStopTime gtfsStopTime = stopTimesOfATrip.next();
            // @TODO : check syntax
            lvjas.add(vehicleJourneyAtStopProducer.produce(
                  stopTimesOfATrip.next(), vjReport));
            if (afterMidnight)
            {
               if (!gtfsStopTime.getArrivalTime().moreOneDay())
                  afterMidnight = false;
               if (!gtfsStopTime.getDepartureTime().moreOneDay())
                  afterMidnight = false;
            }
         }
         Collections.sort(lvjas, vjasComparator);
         Timetable timetable = mapTimetableByServiceId.get(gtfsTrip
               .getServiceId());
         if (afterMidnight)
         {
            // vehicleJourney starts after midnight
            logger.info("trip " + gtfsTrip.getTripId()
                  + " starts after midnight");
            timetable = mapTimetableAfterMidnightByServiceId.get(gtfsTrip
                  .getServiceId());
         }
         if (timetable == null)
         {
            ExchangeReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                  Report.STATE.WARNING, "trips.txt", gtfsTrip.getId(),
                  "service_id", gtfsTrip.getServiceId());
            vjReport.addItem(item);
            logger.warn("service " + gtfsTrip.getServiceId()
                  + " not found for trip " + gtfsTrip.getTripId());
            continue;
         }
         String routeId = gtfsTrip.getRouteId() + "_"
               + gtfsTrip.getDirectionId();
         Route route = mapRouteByRouteId.get(routeId);
         if (route == null)
         {
            ExchangeReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                  Report.STATE.WARNING, "trips.txt", gtfsTrip.getId(),
                  "route_id", gtfsTrip.getRouteId());
            vjReport.addItem(item);
            logger.warn("route " + gtfsTrip.getRouteId()
                  + " not found for trip " + gtfsTrip.getTripId());
            continue;

         }

         vehicleJourney.addTimetable(timetable);
         timetable.addVehicleJourney(vehicleJourney);
         vehicleJourneys.add(vehicleJourney);
         mapVehicleJourneyByTripId.put(gtfsTrip.getTripId(), vehicleJourney);
         // stopSequence
         String journeyKey = routeId;
         for (VehicleJourneyAtStop vjas : lvjas)
         {
            journeyKey += "," + vjas.getStopPointId();
         }
         JourneyPattern journeyPattern = mapJourneyPatternByStopSequence
               .get(journeyKey);
         if (journeyPattern == null)
         {
            // logger.debug("creating new journeyPattern");
            journeyPattern = new JourneyPattern();
            if (route.getJourneyPatterns() != null
                  && !route.getJourneyPatterns().isEmpty())
            {
               // wayback relations will be lost
               route = cloneRoute(route, mapRouteExtensionByRouteId);
               routes.add(route);
               // logger.debug("cloning route " + route.getObjectId());
            }
            journeyPattern.setRoute(route);
            route.addJourneyPattern(journeyPattern);
            journeyPattern.setObjectId(route.getObjectId().replace(
                  Route.ROUTE_KEY, JourneyPattern.JOURNEYPATTERN_KEY)
                  + "a" + route.getJourneyPatterns().size());
            // compare stops
            // logger.debug("affect journeypattern " +
            // journeyPattern.getObjectId() + " to route " +
            // route.getObjectId());
            List<StopPoint> jpStopPoints = buildStopPoint(route.getObjectId(),
                  lvjas, mapStopAreasByStopId, report);
            route.setStopPoints(jpStopPoints);
            stopPoints.addAll(jpStopPoints);
            for (int i = 0; i < jpStopPoints.size(); i++)
            {
               mapStopPointbyJourneyPatternRank.put(journeyKey + "a" + (i + 1),
                     jpStopPoints.get(i));
            }
            journeyPattern.setStopPoints(jpStopPoints);
            // map journey pattern
            mapJourneyPatternByStopSequence.put(journeyKey, journeyPattern);
            journeyPatterns.add(journeyPattern);

         }
         route = journeyPattern.getRoute();
         vehicleJourney.setRoute(route);
         vehicleJourney.setRouteId(route.getObjectId());
         route.setWayBack(gtfsTrip.getDirectionId() == GtfsTrip.DirectionType.Inbound ? "R"
               : "A");
         vehicleJourney.setJourneyPattern(journeyPattern);
         vehicleJourney.setJourneyPatternId(journeyPattern.getObjectId());
         journeyPattern.addVehicleJourney(vehicleJourney);
         // vehicleJourneyAtStop
         int stRank = 1;
         boolean validVehicleJourney = true;
         for (VehicleJourneyAtStop vjas : lvjas)
         {
            String stopKey = journeyKey + "a" + stRank;
            vjas.setOrder(stRank);
            StopPoint spor = mapStopPointbyJourneyPatternRank.get(stopKey);
            if (spor == null)
            {
               ExchangeReportItem item = new ExchangeReportItem(
                     ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                     Report.STATE.WARNING, "stop_times.txt", vjas.getId(),
                     "stop_id", vjas.getStopPointId());
               vjReport.addItem(item);
               logger.error("StopPoint " + stopKey + " not found");
               validVehicleJourney = false;
               break;
            }
            vjas.setStopPoint(spor);
            vjas.setVehicleJourney(vehicleJourney);
            // reset GTFS refs
            vjas.setId(null);
            vjas.setStopPointId(null);
            vehicleJourney.addVehicleJourneyAtStop(vjas);
            stRank++;
         }
         if (!validVehicleJourney)
         {
            continue;
         }
         // apply frequencies if any
         if (importer.hasFrequencyImporter())
         {

            for (GtfsFrequency frequency : importer
                  .getFrequencyByTrip().values(gtfsTrip.getTripId()))
            {
               baseVehicleJourneyToTime(vehicleJourney, frequency
                     .getStartTime().getTime().getTime());
               try
               {
                  if (!frequency.getStartTime().moreOneDay()
                        && frequency.getEndTime().moreOneDay())
                  {

                     copyVehicleJourney(
                           vjFactory,
                           vehicleJourney,
                           frequency.getEndTime().getTime().getTime() + 24 * 3600 * 1000,
                           frequency.getHeadwaySecs() * 1000);
                  } else
                  {
                     copyVehicleJourney(vjFactory, vehicleJourney, frequency
                           .getEndTime().getTime().getTime(),
                           frequency.getHeadwaySecs() * 1000);
                  }
               } catch (Exception e)
               {
                  // TODO add report
                  logger.error("cannot apply frequency ", e);
               }
            }
         }

         vjFactory.flush(vehicleJourney);
      }
      logger.debug("process " + count + " vehicleJourneys ...");
      if (!vjReport.getStatus().equals(Report.STATE.OK))
      {
         report.addItem(vjReport);
      }

      vjFactory.flush();
      // fix spor objectids and clean empty routes

      for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();)
      {
         Route route = iterator.next();
         if (route.getStopPoints() == null || route.getStopPoints().isEmpty())
         {
            // dettach route
            route.getLine().removeRoute(route);
            if (route.getWayBackRouteId() != null)
            {
               Route wayback = mapRouteByRouteId.get(route.getWayBackRouteId()
                     .split(":")[2]);
               if (wayback == null)
               {
                  // logger.error("route to remove "+route.getObjectId()+" : opposite route "+route.getWayBackRouteId()+" not found");
               } else
               {
                  wayback.setWayBackRouteId(null);
               }
            }
            iterator.remove();
         } else
         {
            int rank = 0;
            for (StopPoint spor : route.getStopPoints())
            {
               spor.setPosition(rank++);
               spor.setRoute(route);
            }
            // force rebuild ptlinks
            route.rebuildPTLinks();
         }
      }
      // clean missing waybacks
      for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();)
      {
         Route route = iterator.next();
         if (route.getWayBackRouteId() != null)
         {
            Route wayback = mapRouteByRouteId.get(route.getWayBackRouteId()
                  .split(":")[2]);
            if (wayback == null)
            {
               route.setWayBackRouteId(null);
            }
         }
      }

      assembler.setVehicleJourneys(vehicleJourneys);
      assembler.setStopPoints(stopPoints);
      assembler.setJourneyPatterns(journeyPatterns);

      // ConnectionLinks
      List<ConnectionLink> links = new ArrayList<ConnectionLink>();
      convertConnectionLink(report, links, commercials, mapStopAreasByStopId,
            maxDistanceForConnectionLink);
      assembler.setConnectionLinks(links);

      return assembler;
   }

   /**
    * @param data
    * @param report
    * @param links
    * @param commercials
    * @param mapStopAreasByStopId
    * @param maxDistanceForConnectionLink
    * @throws Exception
    */
   public void convertConnectionLink(Report report, List<ConnectionLink> links,
         List<StopArea> commercials,
         Map<String, StopArea> mapStopAreasByStopId,
         double maxDistanceForConnectionLink) throws Exception
   {
      ConnectionLinkProducer connectionLinkProducer = new ConnectionLinkProducer();
      ConnectionLinkGenerator connectionLinkGenerator = new ConnectionLinkGenerator();
      List<ConnectionLink> excludedLinks = new ArrayList<ConnectionLink>();
      LimitedExchangeReportItem connectionLinkReport = new LimitedExchangeReportItem(
            LimitedExchangeReportItem.KEY.CONNECTION_LINK_ANALYSE,
            Report.STATE.OK);
      if (importer.hasTransferImporter())
      {
         for (GtfsTransfer transfer : importer.getTransferByFromStop())
         {
            ConnectionLink link = connectionLinkProducer.produce(transfer,
                  report);
            link.setStartOfLink(mapStopAreasByStopId.get(link
                  .getStartOfLinkId()));
            link.setEndOfLink(mapStopAreasByStopId.get(link.getEndOfLinkId()));
            if (link.getStartOfLink() == null || link.getEndOfLink() == null)
            {
               if (link.getStartOfLink() == null)
               {
                  ExchangeReportItem item = new ExchangeReportItem(
                        ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                        Report.STATE.WARNING, "transfers.txt",
                        transfer.getId(), "from_stop_id",
                        transfer.getFromStopId());
                  connectionLinkReport.addItem(item);
               }
               if (link.getEndOfLink() == null)
               {
                  ExchangeReportItem item = new ExchangeReportItem(
                        ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                        Report.STATE.WARNING, "transfers.txt",
                        transfer.getId(), "to_stop_id", transfer.getToStopId());
                  connectionLinkReport.addItem(item);
               }
               logger.error("line " + transfer.getId()
                     + " invalid transfer : form or to stop unknown");
               continue;
            }
            link.setStartOfLinkId(link.getStartOfLink().getObjectId());
            link.setEndOfLinkId(link.getEndOfLink().getObjectId());

            if ("FORBIDDEN".equals(link.getName()))
            {
               excludedLinks.add(link);
            } else
            {
               link.setName("from " + link.getStartOfLink().getName() + " to "
                     + link.getEndOfLink().getName());
               links.add(link);
               link.getStartOfLink().addConnectionLink(link);
               link.getEndOfLink().addConnectionLink(link);
            }
         }
      }

      if (!connectionLinkReport.getStatus().equals(Report.STATE.OK))
      {
         report.addItem(connectionLinkReport);
      }

      if (maxDistanceForConnectionLink > 0.)
      {
         if (links.size() > 0)
         {
            logger.warn("gtfs data has already transfers");
         }
         links.addAll(connectionLinkGenerator.createConnectionLinks(
               commercials, maxDistanceForConnectionLink, links, excludedLinks));
      }
   }

   public void convertStopAreas(Report report, List<StopArea> areas,
         List<StopArea> commercials,
         Map<String, StopArea> mapStopAreasByStopId,
         double maxDistanceForCommercialStop, boolean ignoreLastWord,
         int ignoreEndCharacters) throws Exception
   {
      StopAreaProducer stopAreaProducer = new StopAreaProducer();
      CommercialStopGenerator commercialStopGenerator = new CommercialStopGenerator();
      List<StopArea> bps = new ArrayList<StopArea>();
      Set<String> stopAreaOidSet = new HashSet<String>();

      logger.info("process stopArea :" + importer.getStopById().getLength());
      for (GtfsStop gtfsStop : importer.getStopById())
      {
         // check exceptions
         StopArea area = stopAreaProducer.produce(gtfsStop, report);
         if (area != null)
         {
            if (mapStopAreasByStopId.containsKey(gtfsStop.getStopId()))
            {
               ExchangeReportItem item = new ExchangeReportItem(
                     ExchangeReportItem.KEY.DUPLICATE_ID, Report.STATE.WARNING,
                     "Stops.txt", gtfsStop.getId(), gtfsStop.getStopId());
               report.addItem(item);
               logger.error("duplicate stop id " + gtfsStop.getStopId());
            } else
            {
               mapStopAreasByStopId.put(gtfsStop.getStopId(), area);
               if (area.getAreaType().equals(
                     ChouetteAreaEnum.CommercialStopPoint))
               {
                  commercials.add(area);
               } else
               {
                  bps.add(area);
               }
               if (stopAreaOidSet.contains(area.getObjectId()))
               {
                  ExchangeReportItem item = new ExchangeReportItem(
                        ExchangeReportItem.KEY.DUPLICATE_ID,
                        Report.STATE.WARNING, "stops.txt", gtfsStop.getId(),
                        area.getObjectId());
                  report.addItem(item);
                  logger.error("duplicate stop object id " + area.getObjectId());
               } else
               {
                  stopAreaOidSet.add(area.getObjectId());
               }
            }
         }
      }
      // connect bps to parents
      LimitedExchangeReportItem stopReport = new LimitedExchangeReportItem(
            LimitedExchangeReportItem.KEY.STOP_ANALYSE, Report.STATE.OK);
      for (StopArea bp : bps)
      {

         if (bp.getParentObjectId() != null)
         {
            StopArea parent = mapStopAreasByStopId.get(bp.getParentObjectId());
            if (parent == null)
            {
               ExchangeReportItem item = new ExchangeReportItem(
                     ExchangeReportItem.KEY.BAD_REFERENCE,
                     Report.STATE.WARNING, "StopArea", bp.getName(), "parent",
                     bp.getParentObjectId());
               stopReport.addItem(item);
               logger.warn("stop " + bp.getName()
                     + " has missing parent station " + bp.getParentObjectId());
               bp.setParentObjectId(null);
            } else if (!parent.getAreaType().equals(
                  ChouetteAreaEnum.CommercialStopPoint))
            {
               ExchangeReportItem item = new ExchangeReportItem(
                     ExchangeReportItem.KEY.BAD_REFERENCE,
                     Report.STATE.WARNING, "StopArea", bp.getName(), "parent",
                     bp.getParentObjectId());
               stopReport.addItem(item);
               logger.error("stop " + bp.getName()
                     + " has wrong parent station type "
                     + bp.getParentObjectId());
               bp.setParentObjectId(null);
            } else
            {
               bp.setParent(parent);
               parent.addContainedStopArea(bp);
               // logger.info("stop "+bp.getName()+" connected to "+parent.getName());
            }
         }
      }

      if (!stopReport.getStatus().equals(Report.STATE.OK))
      {
         report.addItem(stopReport);
      }

      // add commercials
      if (maxDistanceForCommercialStop > 0)
      {
         if (commercials.size() > 0)
         {
            // TODO check if all bps has csp
            logger.warn("GTFS has already commercial stops");
         }
         List<StopArea> generatedCommercials = commercialStopGenerator
               .createCommercialStopPoints(bps, maxDistanceForCommercialStop,
                     ignoreLastWord, ignoreEndCharacters);
         commercials.addAll(generatedCommercials);
      }
      areas.addAll(bps);
      areas.addAll(commercials);
   }

   /**
    * @param data
    * @param assembler
    * @param report
    * @return
    */
   private Map<String, Timetable> convertTimetables(ModelAssembler assembler,
         Report report) throws Exception
   {
      TimetableProducer timetableProducer = new TimetableProducer();
      // Timetables
      List<Timetable> timetables = new ArrayList<Timetable>();
      Map<String, Timetable> mapTimetableByServiceId = new HashMap<String, Timetable>();

      logger.info("process timetables from calendar :"
            + importer.getCalendarByService().getLength());
      if (importer.getCalendarByService() != null)
      {
         for (GtfsCalendar gtfsCalendar : importer.getCalendarByService())
         {
            Timetable timetable = timetableProducer.produce(gtfsCalendar,
                  report);

            timetables.add(timetable);
            mapTimetableByServiceId.put(gtfsCalendar.getServiceId(), timetable);
         }
      }
      if (importer.getCalendarDateByService() != null)
      {
         GtfsCalendar calendar = new GtfsCalendar(); // dummy calendar for
         // production
         for (String serviceId : importer.getCalendarDateByService()
               .keys())
         {         
            Timetable timetable = mapTimetableByServiceId.get(serviceId);
            if (timetable == null)
            {
               calendar.setServiceId(serviceId);
               timetable = timetableProducer.produce(calendar, report);
               timetables.add(timetable);
               mapTimetableByServiceId.put(serviceId, timetable);
            }
            for (GtfsCalendarDate date : importer
                  .getCalendarDateByService().values(serviceId))
            {                      
               timetableProducer.addDate(timetable, date);
            }
            // refresh timetable name
            timetableProducer.buildComment(timetable);
         }
      }

      assembler.setTimetables(timetables);
      return mapTimetableByServiceId;
   }

   /**
    * @param data
    * @param assembler
    * @param report
    * @throws Exception
    */
   private void convertCompanies(ModelAssembler assembler, Report report)
         throws Exception
   {
      CompanyProducer companyProducer = new CompanyProducer();
      // Companies
      List<Company> companies = new ArrayList<Company>();
      for (GtfsAgency gtfsAgency : importer.getAgencyById())
      {
         Company company = companyProducer.produce(gtfsAgency, report);
         companies.add(company);
      }
      assembler.setCompanies(companies);
   }

   /**
    * @param data
    * @param assembler
    * @param report
    */
   private void convertNetworks(String prefix, ModelAssembler assembler,
         Report report)
   {
      // PTnetwork
      PTNetwork ptNetwork = new PTNetwork();

      ptNetwork.setObjectId(prefix + ":" + PTNetwork.PTNETWORK_KEY + ":"
            + prefix);

      // VersionDate mandatory
      ptNetwork.setVersionDate(Calendar.getInstance().getTime());

      // Name mandatory
      ptNetwork.setName(prefix);

      // Registration optional
      ptNetwork.setRegistrationNumber(prefix);

      // SourceName optional
      ptNetwork.setSourceName("GTFS");
      assembler.setPtNetwork(ptNetwork);
   }

   /**
    * create a copy of a route
    * 
    * @param route
    *           route to copy
    * @param mapRouteExtensionByRouteId
    *           next rank for objectId build
    * @return new route builded by copy
    */
   private Route cloneRoute(Route route,
         Map<String, Integer> mapRouteExtensionByRouteId)
   {
      Route clone = new Route();
      clone.setLine(route.getLine());
      route.getLine().addRoute(clone);
      Integer rank = mapRouteExtensionByRouteId.get(route.getObjectId());
      clone.setObjectId(route.getObjectId() + "_" + rank);
      mapRouteExtensionByRouteId.put(route.getObjectId(), rank + 1);
      clone.setName(route.getName());
      clone.setPublishedName(route.getPublishedName());
      clone.setComment(route.getComment());

      return clone;
   }

   /**
    * build stopPoints for Route
    * 
    * @param routeId
    *           route objectId
    * @param stopTimesOfATrip
    *           first trip's ordered GTFS StopTimes
    * @param mapStopAreasByStopId
    *           stopAreas to attach created StopPoints (parent relationship)
    * @return
    */
   private List<StopPoint> buildStopPoint(String routeId,
         List<VehicleJourneyAtStop> lvjas,
         Map<String, StopArea> mapStopAreasByStopId, Report report)
   {
      List<StopPoint> stopPoints = new ArrayList<StopPoint>();
      Set<String> stopPointKeys = new HashSet<String>();

      int position = 0;
      for (VehicleJourneyAtStop vjas : lvjas)
      {
         String baseKey = routeId.replace(Route.ROUTE_KEY,
               StopPoint.STOPPOINT_KEY)
               + "a"
               + vjas.getStopPointId().trim()
                     .replaceAll("[^a-zA-Z_0-9\\-]", "_");
         String stopKey = baseKey;
         int dup = 1;
         while (stopPointKeys.contains(stopKey))
            stopKey = stopKey + "_" + (dup++);
         stopPointKeys.add(stopKey);
         StopPoint spor = new StopPoint();
         spor.setObjectId(stopKey);
         StopArea area = mapStopAreasByStopId.get(vjas.getStopPointId());
         if (area == null)
         {
            ExchangeReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.BAD_REFERENCE_IN_FILE,
                  Report.STATE.WARNING, "stop_times.txt", vjas.getId(),
                  "stop_id", vjas.getStopPointId());
            report.addItem(item);
            logger.error("StopArea for stopId" + vjas.getStopPointId()
                  + " not found");
         } else
         {
            area.addContainedStopPoint(spor);
            spor.setPosition(position++);
            spor.setContainedInStopArea(area);
            spor.setName(area.getName());
            stopPoints.add(spor);
         }
      }
      return stopPoints;

   }

   /**
    * shift vehicleJourney times to start vehicleJourney at a specific time
    * 
    * @param vj
    *           vehicleJourney to shift
    * @param t
    *           start time (first StopPoint's departureTime)
    */
   private void baseVehicleJourneyToTime(VehicleJourney vj, long t)
   {
      VehicleJourneyAtStop first = vj.getVehicleJourneyAtStops().get(0);
      long depOffset = t - first.getDepartureTime().getTime();
      long arrOffset = t - first.getArrivalTime().getTime();

      for (VehicleJourneyAtStop vjas : vj.getVehicleJourneyAtStops())
      {
         vjas.setArrivalTime(shiftTime(vjas.getArrivalTime(), arrOffset));
         vjas.setDepartureTime(shiftTime(vjas.getDepartureTime(), depOffset));
      }
   }

   /**
    * shift a time on and offset
    * 
    * @param t
    *           time to shift
    * @param offset
    *           offset (seconds)
    * @return time shifted (new instance)
    */
   private Time shiftTime(Time t, long offset)
   {
      return new Time((t.getTime() + offset) % (24 * 3600 * 1000));
   }

   /**
    * shift several times a vehicleJourney to match frequency on scheduled
    * vehicleJourneys
    * <p>
    * vehicleJourneys are added to journeyPattern
    * 
    * @param vj
    *           vehicleJourney to shift
    * @param end
    *           last StartTime in milliseconds
    * @param headway
    *           gap between each new vehicleJourneys
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws InvocationTargetException
    * @throws NoSuchMethodException
    */
   private void copyVehicleJourney(DbVehicleJourneyFactory factory,
         VehicleJourney vj, long end, long headway)
         throws IllegalAccessException, InstantiationException,
         InvocationTargetException, NoSuchMethodException
   {
      VehicleJourneyAtStop first = vj.getVehicleJourneyAtStops().get(0);
      long start = first.getDepartureTime().getTime();
      long stop = end - start;
      int iter = 1;

      long offset = headway;
      while (offset <= stop)
      {
         VehicleJourney nvj = factory.getNewVehicleJourney();
         nvj.setObjectId(vj.getObjectId() + "a" + iter);
         iter++;
         for (Timetable timetable : nvj.getTimetables())
         {
            timetable.addVehicleJourney(nvj);
         }
         List<VehicleJourneyAtStop> vjass = vj.getVehicleJourneyAtStops();
         for (VehicleJourneyAtStop vjas : vjass)
         {
            VehicleJourneyAtStop nvjas = factory.getNewVehicleJourneyAtStop();
            BeanUtils.copyProperties(nvjas, vjas);
            nvjas.setVehicleJourney(nvj);
            nvjas.setArrivalTime(shiftTime(nvjas.getArrivalTime(), offset));
            nvjas.setDepartureTime(shiftTime(nvjas.getDepartureTime(), offset));
            nvj.addVehicleJourneyAtStop(nvjas);
         }
         nvj.setRoute(vj.getRoute());
         nvj.setRouteId(vj.getRouteId());
         nvj.setJourneyPattern(vj.getJourneyPattern());
         nvj.setJourneyPatternId(vj.getJourneyPatternId());
         nvj.getJourneyPattern().addVehicleJourney(nvj);
         for (Timetable tm : vj.getTimetables())
         {
            nvj.addTimetable(tm);
            tm.addVehicleJourney(nvj);
         }
         factory.flush(nvj);
         offset += headway;
      }
      return;
   }

   private static long dayOffest = 24 * 3600000; // one day in milliseconds

   private Timetable cloneTimetableAfterMidnight(Timetable source)
   {
      Timetable result = new Timetable();
      result.setObjectId(source.getObjectId() + "_after_midnight");
      result.setComment(source.getComment() + " (after midnight)");
      result.setVersion(source.getVersion());
      for (DayTypeEnum dayType : source.getDayTypes())
      {
         switch (dayType)
         {
         case Monday:
            result.addDayType(DayTypeEnum.Tuesday);
            break;
         case Tuesday:
            result.addDayType(DayTypeEnum.Wednesday);
            break;
         case Wednesday:
            result.addDayType(DayTypeEnum.Thursday);
            break;
         case Thursday:
            result.addDayType(DayTypeEnum.Friday);
            break;
         case Friday:
            result.addDayType(DayTypeEnum.Saturday);
            break;
         case Saturday:
            result.addDayType(DayTypeEnum.Sunday);
            break;
         case Sunday:
            result.addDayType(DayTypeEnum.Monday);
            break;

         default:
            result.addDayType(dayType);
            break;
         }
      }
      for (Period period : source.getPeriods())
      {
         result.addPeriod(clonePeriodAfterMidnight(period));
      }

      for (CalendarDay calendarDay : source.getCalendarDays())
      {
         result.addCalendarDay(cloneDateAfterMidnight(calendarDay));
      }
      return result;
   }

   private Period clonePeriodAfterMidnight(Period source)
   {
      Period result = new Period();

      result.setStartDate(new Date(source.getStartDate().getTime() + dayOffest));
      result.setEndDate(new Date(source.getEndDate().getTime() + dayOffest));

      return result;
   }

   private Date cloneDateAfterMidnight(Date source)
   {
      return new Date(source.getTime() + dayOffest);
   }

   private CalendarDay cloneDateAfterMidnight(CalendarDay source)
   {
      return new CalendarDay(cloneDateAfterMidnight(source.getDate()),
            source.getIncluded());
   }

   private class VjasComparator implements Comparator<VehicleJourneyAtStop>
   {

      @Override
      public int compare(VehicleJourneyAtStop o1, VehicleJourneyAtStop o2)
      {
         return (int) (o2.getOrder() - o1.getOrder());
      }

   }
}
