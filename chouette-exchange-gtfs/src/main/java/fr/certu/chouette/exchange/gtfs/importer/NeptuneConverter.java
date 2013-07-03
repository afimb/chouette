/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.exchange.gtfs.importer;

import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Setter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.importer.producer.AbstractModelProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.CompanyProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.ConnectionLinkProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.LineProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.PTNetworkProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.RouteProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.StopAreaProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.TimetableProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.VehicleJourneyAtStopProducer;
import fr.certu.chouette.exchange.gtfs.importer.producer.VehicleJourneyProducer;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;
import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;
import fr.certu.chouette.exchange.gtfs.model.GtfsFrequency;
import fr.certu.chouette.exchange.gtfs.model.GtfsRoute;
import fr.certu.chouette.exchange.gtfs.model.GtfsStop;
import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;
import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;
import fr.certu.chouette.plugin.exchange.tools.DbVehicleJourneyFactory;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.plugin.report.ReportItem;

/**
 * convert GTFS raw data structure to Chouette internal one
 * 
 */
public class NeptuneConverter
{
	private static Logger                logger = Logger.getLogger(NeptuneConverter.class);

	/**
	 * Line producer from GtfsRoute
	 */
	@Setter
	private LineProducer                 lineProducer;
	/**
	 * Route producer from GtfsRoute
	 */
	@Setter
	private RouteProducer                routeProducer;
	/**
	 * PTNetwork producer from base name
	 */
	@Setter
	private PTNetworkProducer            networkProducer;
	/**
	 * Company producer from GtfsAgency
	 */
	@Setter
	private CompanyProducer              companyProducer;
	/**
	 * VehicleJourney producer from GtfsTrip
	 */
	@Setter
	private VehicleJourneyProducer       vehicleJourneyProducer;
	/**
	 * VehicleJourneyAtStop producer from GtfsStopTime
	 */
	@Setter
	private VehicleJourneyAtStopProducer vehicleJourneyAtStopProducer;
	/**
	 * StopArea (BoardingPosition) producer from GtfsStop
	 */
	@Setter
	private StopAreaProducer             stopAreaProducer;
	/**
	 * Timetable producer from GtfsCalendar and CtfsCalendarDate
	 */
	@Setter
	private TimetableProducer            timetableProducer;

	/**
	 * Connection producer from GtfsTransfer
	 */
	@Setter private ConnectionLinkProducer connectionLinkProducer ;

	/**
	 * CommercialStopPoint generator
	 */
	@Setter
	private CommercialStopGenerator      commercialStopGenerator;
	/**
	 * ConnectionLink generator
	 */
	@Setter
	private ConnectionLinkGenerator      connectionLinkGenerator;

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
	 */
	public ModelAssembler convert(boolean optimizeMemory, String prefix, String incrementalPrefix, GtfsData data, double maxDistanceForCommercialStop,
			boolean ignoreLastWord, int ignoreEndCharacters, double maxDistanceForConnectionLink, boolean mergeRouteByShortName)
	{
		DbVehicleJourneyFactory vjFactory = new DbVehicleJourneyFactory(prefix,optimizeMemory);
		vehicleJourneyProducer.setFactory(vjFactory);
		ModelAssembler assembler = new ModelAssembler();
		ReportItem report = null;
		AbstractModelProducer.setPrefix(prefix);
		AbstractModelProducer.setIncrementalPrefix(incrementalPrefix);

		// PTnetwork
		PTNetwork network = networkProducer.produce(data.getNetwork(), report);
		assembler.setPtNetwork(network);

		// Companies
		List<Company> companies = new ArrayList<Company>();
		for (GtfsAgency gtfsAgency : data.getAgencies().getAll())
		{
			Company company = companyProducer.produce(gtfsAgency, report);
			companies.add(company);
		}
		data.getAgencies().clear();
		assembler.setCompanies(companies);

		// lines, routes
		List<Line> lines = new ArrayList<Line>();
		List<Route> routes = new ArrayList<Route>();
		Map<String, Line> mapLineByRouteName = new HashMap<String, Line>();
		Map<String, Route> mapRouteByRouteId = new HashMap<String, Route>();
		logger.info("process routes :" + data.getRoutes().size());
		Map<String, Integer> mapRouteExtensionByRouteId = new HashMap<String, Integer>();

		for (GtfsRoute gtfsRoute : data.getRoutes().getAll())
		{
			// must produce 2 routes : one for each direction;
			// at end of processing, empty route will be destroyed
			String routeName = mergeRouteByShortName?gtfsRoute.getRouteShortName():gtfsRoute.getRouteId();
			Line line = mapLineByRouteName.get(routeName);
			if (line == null)
			{
				line = lineProducer.produce(gtfsRoute, report);
				mapLineByRouteName.put(routeName, line);
				lines.add(line);
			}
			Route route0 = routeProducer.produce(gtfsRoute, report);
			Route route1 = new Route();
			route1.setName(route0.getName());
			route1.setPublishedName(route0.getPublishedName());
			route1.setComment(route0.getComment());
			route1.setObjectId(route0.getObjectId()+"_1");
			route0.setObjectId(route0.getObjectId()+"_0");
			route0.setLine(line);
			route1.setWayBackRouteId(route0.getObjectId());
			route0.setWayBackRouteId(route1.getObjectId());
			mapRouteByRouteId.put(gtfsRoute.getRouteId()+"_0", route0);
			line.addRoute(route0);
			routes.add(route0);
			mapRouteExtensionByRouteId.put(route0.getObjectId(), Integer.valueOf(1));
			route1.setLine(line);
			mapRouteByRouteId.put(gtfsRoute.getRouteId()+"_1", route1);
			line.addRoute(route1);
			routes.add(route1);
			mapRouteExtensionByRouteId.put(route1.getObjectId(), Integer.valueOf(1));
		}
		// System.gc();
		logger.debug("free memory = "+Runtime.getRuntime().freeMemory());
		logger.debug("max memory = "+Runtime.getRuntime().maxMemory());
		logger.debug("total memory = "+Runtime.getRuntime().totalMemory());
		assembler.setLines(lines);
		assembler.setRoutes(routes);

		// stopareas
		List<StopArea> commercials = new ArrayList<StopArea>();
		List<StopArea> bps = new ArrayList<StopArea>();
		Map<String, StopArea> mapStopAreasByStopId = new HashMap<String, StopArea>();
		Set<String> stopAreaOidSet = new HashSet<String>();
		logger.info("process stopArea :" + data.getStops().size());
		for (GtfsStop gtfsStop : data.getStops().getAll())
		{
			StopArea area = stopAreaProducer.produce(gtfsStop, report);
			if (mapStopAreasByStopId.containsKey(gtfsStop.getStopId()))
			{
				logger.error("duplicate stop id "+gtfsStop.getStopId());
			}
			else
			{
				mapStopAreasByStopId.put(gtfsStop.getStopId(), area) ;
				if (area.getAreaType().equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
				{
					commercials.add(area);
				}
				else
				{
					bps.add(area);
				}
				if (stopAreaOidSet.contains(area.getObjectId()))
				{
					logger.error("duplicate stop object id "+area.getObjectId());
				}
				else
				{
					stopAreaOidSet.add(area.getObjectId());
				}
			}
		}
		data.getStops().clear();
		// connect bps to parents
		for (StopArea bp : bps) 
		{
			if (bp.getParentObjectId() != null)
			{
				StopArea parent = mapStopAreasByStopId.get(bp.getParentObjectId());
				if (parent == null)
				{
					logger.warn("stop "+bp.getName()+" has missing parent station "+bp.getParentObjectId());
					bp.setParentObjectId(null);
				}
				else if (!parent.getAreaType().equals(ChouetteAreaEnum.COMMERCIALSTOPPOINT))
				{
					logger.error("stop "+bp.getName()+" has wrong parent station type "+bp.getParentObjectId());
					bp.setParentObjectId(null);
				}
				else
				{
					bp.setParent(parent);
					parent.addContainedStopArea(bp);
					// logger.info("stop "+bp.getName()+" connected to "+parent.getName());					
				}
			}
		}

		// add commercials
		List<StopArea> areas = new ArrayList<StopArea>();
		if (maxDistanceForCommercialStop > 0)
		{
			if (commercials.size() > 0)
			{
				// TODO check if all bps has csp
				logger.warn("GTFS has already commercial stops");
			}
			List<StopArea> generatedCommercials = commercialStopGenerator.createCommercialStopPoints(bps,
					maxDistanceForCommercialStop, ignoreLastWord, ignoreEndCharacters);
			commercials.addAll(generatedCommercials);
		}
		areas.addAll(bps);
		areas.addAll(commercials);
		assembler.setStopAreas(areas);

		// Timetables
		List<Timetable> timetables = new ArrayList<Timetable>();
		Map<String, Timetable> mapTimetableByServiceId = new HashMap<String, Timetable>();

		logger.info("process timetables :" + data.getCalendars().size());
		for (GtfsCalendar gtfsCalendar : data.getCalendars().getAll())
		{
			List<GtfsCalendarDate> dates = data.getCalendarDates().getAllFromParent(gtfsCalendar.getServiceId());
			for (GtfsCalendarDate date : dates)
			{
				gtfsCalendar.addCalendarDate(date);
			}

			Timetable timetable = timetableProducer.produce(gtfsCalendar, report);

			timetables.add(timetable);
			mapTimetableByServiceId.put(gtfsCalendar.getServiceId(), timetable);
		}

		assembler.setTimetables(timetables);

		// vehicleJourneys , vehicleJourneyAtStops, JourneyPatterns and StopPoints
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

		logger.info("process vehicleJourneys :" + data.getTrips().size());
		int count = 0;
		for (GtfsTrip gtfsTrip : data.getTrips().getAll()) 
		{
			count++;
			if (count % 1000 == 0)
			{
				logger.debug("process "+count+" vehicleJourneys ...");
				logger.debug("free memory = "+Runtime.getRuntime().freeMemory());
				logger.debug("max memory = "+Runtime.getRuntime().maxMemory());
				logger.debug("total memory = "+Runtime.getRuntime().totalMemory());
			}

			VehicleJourney vehicleJourney = vehicleJourneyProducer.produce(gtfsTrip, report);
			Timetable timetable = mapTimetableByServiceId.get(gtfsTrip.getServiceId());
			if (timetable == null) 
			{
				logger.warn("service "+gtfsTrip.getServiceId()+" not found for trip "+gtfsTrip.getTripId());
			}
			else
			{
				vehicleJourney.addTimetable(timetable);
				timetable.addVehicleJourney(vehicleJourney);
			}
			vehicleJourneys.add(vehicleJourney);
			String routeId = gtfsTrip.getRouteId()+"_"+gtfsTrip.getDirectionId();
			Route route = mapRouteByRouteId.get(routeId);
			mapVehicleJourneyByTripId.put(gtfsTrip.getTripId(), vehicleJourney);
			// stopSequence
			List<GtfsStopTime> stopTimesOfATrip = data.getStopTimes().getAllFromParent(gtfsTrip.getTripId());
			Collections.sort(stopTimesOfATrip);
			String journeyKey = routeId;
			for (GtfsStopTime gtfsStopTime : stopTimesOfATrip)
			{
				journeyKey += "," + gtfsStopTime.getStopId();
			}
			JourneyPattern journeyPattern = mapJourneyPatternByStopSequence.get(journeyKey);
			if (journeyPattern == null)
			{
				// logger.debug("creating new journeyPattern");
				journeyPattern = new JourneyPattern();
				if (route.getJourneyPatterns() != null && !route.getJourneyPatterns().isEmpty())
				{
					// wayback relations will be lost
					route = cloneRoute(route, mapRouteExtensionByRouteId);
					routes.add(route);
					// logger.debug("cloning route " + route.getObjectId());
				}
				journeyPattern.setRoute(route);
				route.addJourneyPattern(journeyPattern);
				journeyPattern.setObjectId(route.getObjectId().replace(Route.ROUTE_KEY, JourneyPattern.JOURNEYPATTERN_KEY)
						+ "a" + route.getJourneyPatterns().size());
				// compare stops
				// logger.debug("affect journeypattern " + journeyPattern.getObjectId() + " to route " + route.getObjectId());
				List<StopPoint> jpStopPoints = buildStopPoint(route.getObjectId(), stopTimesOfATrip, mapStopAreasByStopId);
				route.setStopPoints(jpStopPoints);
				stopPoints.addAll(jpStopPoints);
				for (int i = 0; i < jpStopPoints.size(); i++)
				{
					mapStopPointbyJourneyPatternRank.put(journeyKey + "a" + (i + 1), jpStopPoints.get(i));
				}
				journeyPattern.setStopPoints(jpStopPoints);
				// map journey pattern
				mapJourneyPatternByStopSequence.put(journeyKey, journeyPattern);
				journeyPatterns.add(journeyPattern);

			}
			route = journeyPattern.getRoute();
			vehicleJourney.setRoute(route);
			vehicleJourney.setRouteId(route.getObjectId());
			route.setWayBack(gtfsTrip.getDirectionId() == 0 ? "A" : "R");
			vehicleJourney.setJourneyPattern(journeyPattern);
			vehicleJourney.setJourneyPatternId(journeyPattern.getObjectId());
			journeyPattern.addVehicleJourney(vehicleJourney);
			// vehicleJourneyAtStop
			int stRank = 1;
			for (GtfsStopTime gtfsStopTime : stopTimesOfATrip)
			{
				VehicleJourneyAtStop vjas = vehicleJourneyAtStopProducer.produce(gtfsStopTime, report);
				vjas.setVehicleJourney(vehicleJourney);
				vehicleJourney.addVehicleJourneyAtStop(vjas);
				String stopKey = journeyKey + "a" + stRank;
				vjas.setOrder(stRank);
				StopPoint spor = mapStopPointbyJourneyPatternRank.get(stopKey);
				if (spor == null)
				{
					logger.error("StopPoint " + stopKey + " not found");
				}
				vjas.setStopPoint(spor);
				stRank++;
			}
			stopTimesOfATrip.clear();
			// apply frequencies
			for (GtfsFrequency frequency : data.getFrequencies().getAllFromParent(gtfsTrip.getTripId()))
			{
				baseVehicleJourneyToTime(vehicleJourney, frequency.getStartTime().getTime().getTime());
				try
				{
					if (!frequency.getStartTime().isTomorrow() && frequency.getEndTime().isTomorrow())
					{

						copyVehicleJourney(vjFactory,vehicleJourney, frequency.getEndTime().getTime().getTime() + 24 * 3600 * 1000,
								frequency.getHeadwaySecs() * 1000);
					}
					else
					{
						copyVehicleJourney(vjFactory,vehicleJourney, frequency.getEndTime().getTime().getTime(), frequency.getHeadwaySecs() * 1000);
					}
				}
				catch (Exception e)
				{
					logger.error("cannot apply frequency ", e);
				}
			}

			vjFactory.flush(vehicleJourney);
			// System.gc();
		}
		logger.debug("process "+count+" vehicleJourneys ...");

		// free some unused maps 
		// mapStopTimesByTrip.clear();
		data.getTrips().clear();
		data.getStopTimes().clear();
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
					Route wayback = mapRouteByRouteId.get(route.getWayBackRouteId().split(":")[2]);
					if (wayback == null)
					{
						// logger.error("route to remove "+route.getObjectId()+" : opposite route "+route.getWayBackRouteId()+" not found");  
					}
					else
					{
						wayback.setWayBackRouteId(null);
					}
				}
				iterator.remove();
			}
			else
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
				Route wayback = mapRouteByRouteId.get(route.getWayBackRouteId().split(":")[2]);
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
		List<ConnectionLink> excludedLinks = new ArrayList<ConnectionLink>();

		for (GtfsTransfer transfer : data.getTransfers().getAll())
		{
			ConnectionLink link = connectionLinkProducer.produce(transfer, report);
			link.setStartOfLink(mapStopAreasByStopId.get(link.getStartOfLinkId()));
			link.setEndOfLink(mapStopAreasByStopId.get(link.getEndOfLinkId()));
			if (link.getStartOfLink() == null || link.getEndOfLink() == null)
			{
				logger.error("line "+transfer.getFileLineNumber()+" invalid transfer : form or to stop unknown");
				continue;
			}
			link.setStartOfLinkId(link.getStartOfLink().getObjectId());
			link.setEndOfLinkId(link.getEndOfLink().getObjectId());

			if ("FORBIDDEN".equals(link.getName()))
			{
				excludedLinks.add(link);
			}
			else
			{
				link.setName("from "+link.getStartOfLink().getName()+" to "+link.getEndOfLink().getName());
				links.add(link);
				link.getStartOfLink().addConnectionLink(link);
				link.getEndOfLink().addConnectionLink(link);
			}
		}

		if (maxDistanceForConnectionLink > 0.)
		{
			if (links.size() > 0)
			{
				logger.warn("gtfs data has already transfers");
			}
			links.addAll(connectionLinkGenerator.createConnectionLinks(commercials,
					maxDistanceForConnectionLink,links,excludedLinks));
			assembler.setConnectionLinks(links);
		}

		return assembler;
	}

	/**
	 * compare VehicleJourneys on vehicleJourneyAtStops departure and arrival
	 * times
	 * 
	 * @param vj1
	 * @param vj2
	 * @return
	 */
	//   private boolean compareTimes2(VehicleJourney vj1, VehicleJourney vj2)
	//   {
	//      List<VehicleJourneyAtStop> vjass1 = vj1.getVehicleJourneyAtStops();
	//      List<VehicleJourneyAtStop> vjass2 = vj2.getVehicleJourneyAtStops();
	//
	//      for (int i = 0; i < vjass1.size(); i++)
	//      {
	//         if (vjass1.get(i).getArrivalTime() != vjass2.get(i).getArrivalTime())
	//            return false;
	//         if (vjass1.get(i).getDepartureTime() != vjass2.get(i).getDepartureTime())
	//            return false;
	//      }
	//      return true;
	//   }

	/**
	 * create a copy of a route
	 * 
	 * @param route
	 *           route to copy
	 * @param mapRouteExtensionByRouteId
	 *           next rank for objectId build
	 * @return new route builded by copy
	 */
	private Route cloneRoute(Route route, Map<String, Integer> mapRouteExtensionByRouteId)
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
	private List<StopPoint> buildStopPoint(String routeId, List<GtfsStopTime> stopTimesOfATrip,
			Map<String, StopArea> mapStopAreasByStopId)
			{
		List<StopPoint> stopPoints = new ArrayList<StopPoint>();
		Set<String> stopPointKeys = new HashSet<String>();

		for (GtfsStopTime gtfsStopTime : stopTimesOfATrip)
		{
			String stopKey = routeId.replace(Route.ROUTE_KEY, StopPoint.STOPPOINT_KEY) + "a" + gtfsStopTime.getStopId().trim().replaceAll("[^a-zA-Z_0-9\\-]", "_");
			if (stopPointKeys.contains(stopKey))
				stopKey = stopKey + "_1";
			stopPointKeys.add(stopKey);
			StopPoint spor = new StopPoint();
			spor.setObjectId(stopKey);
			StopArea area = mapStopAreasByStopId.get(gtfsStopTime.getStopId());
			area.addContainedStopPoint(spor);
			spor.setContainedInStopArea(area);
			spor.setName(area.getName());
			spor.setLatitude(area.getLatitude());
			spor.setLongitude(area.getLongitude());
			spor.setLongLatType(area.getLongLatType());
			stopPoints.add(spor);

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
	private void copyVehicleJourney(DbVehicleJourneyFactory factory, VehicleJourney vj, long end, long headway) throws IllegalAccessException,
	InstantiationException, InvocationTargetException, NoSuchMethodException
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
				VehicleJourneyAtStop nvjas = (VehicleJourneyAtStop) BeanUtils.cloneBean(vjas);
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

}
