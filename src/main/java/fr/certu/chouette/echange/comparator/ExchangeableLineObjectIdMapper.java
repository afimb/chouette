/**
 * 
 */
package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;

/**
 * @author michel
 *
 */
public class ExchangeableLineObjectIdMapper
{
	private Map<String, ArretItineraire> stopPointByIdMap;
	private Map<String, PositionGeographique> stopAreaByIdMap;
	private Map<String, Itineraire> routeByIdMap;
	private Map<String, Itineraire> routeByStopPointIdMap;
	private Map<String, Mission> journeyPatternByIdMap;
	private Map<String, Course> vehicleJourneyByIdMap;

	// maps des courses d'une mission
	private Map<String,List<Course>> vjListByjpIdMap;
	// map des tableaux de marche d'une missiion
	private Map<String, List<String>> timetableIdListByjpIdMap;
	// maps des horaires d'une course
	private Map<String,List<Horaire>> vjasListByvjIdMap;

	// map des arrets d'un itinéraire
	private Map<String,List<ArretItineraire>> stopPointsOfRouteIdMap;
	private Map<String, List<String>> journeyPatternIdListByRouteIdMap;

	public ExchangeableLineObjectIdMapper(ILectureEchange line)
	{
		// map of StopPoint
		List<ArretItineraire> arrets = line.getArrets();
		stopPointByIdMap = new HashMap<String, ArretItineraire>();
		stopPointsOfRouteIdMap = new HashMap<String, List<ArretItineraire>>();
		for (ArretItineraire arretItineraire : arrets)
		{
			stopPointByIdMap.put(arretItineraire.getObjectId(), arretItineraire);

			String key = arretItineraire.getObjectId();
			String value = line.getItineraireArret(key);
			int pos = arretItineraire.getPosition();
			List<ArretItineraire> children = stopPointsOfRouteIdMap.get(value);
			if (children == null) 
			{
				children = new ArrayList<ArretItineraire>();
				stopPointsOfRouteIdMap.put(value, children);
			}
			// size the list to at least the position of the stoppoint
			while (children.size() <= pos)
			{
				children.add(null);
			}
			children.set(pos,arretItineraire);

		}

		// map of StopArea
		stopAreaByIdMap = new HashMap<String,PositionGeographique>();
		List<PositionGeographique> areas = line.getArretsPhysiques();
		for (PositionGeographique area : areas)
		{
			stopAreaByIdMap.put(area.getObjectId(), area);
		}
		areas = line.getZonesPlaces();
		for (PositionGeographique area : areas)
		{
			stopAreaByIdMap.put(area.getObjectId(), area);
		}
		areas = line.getZonesCommerciales();
		for (PositionGeographique area : areas)
		{
			stopAreaByIdMap.put(area.getObjectId(), area);
		}

		// map of Routes
		List<Itineraire> routes = line.getItineraires();
		routeByIdMap = new HashMap<String, Itineraire>();
		for (Itineraire itineraire : routes)
		{
			routeByIdMap.put(itineraire.getObjectId(), itineraire);
		}

		// map of JourneyPatern
		List<Mission> journeyPatterns = line.getMissions();
		journeyPatternByIdMap = new HashMap<String,Mission>();
		for (Mission mission : journeyPatterns)
		{
			journeyPatternByIdMap.put(mission.getObjectId(), mission);
		}        

		// map of VehicleJourney and list by JourneyPatternId
		List<Course> vehicleJourneys = line.getCourses();
		vehicleJourneyByIdMap = new HashMap<String, Course>();
		vjListByjpIdMap = new HashMap<String, List<Course>>();
		for (Course course : vehicleJourneys)
		{
			vehicleJourneyByIdMap.put(course.getObjectId(), course);
			List<Course> vehicleJourneyList = vjListByjpIdMap.get(course.getJourneyPatternId());
			if (vehicleJourneyList == null)
			{
				vehicleJourneyList = new ArrayList<Course>();
				vjListByjpIdMap.put(course.getJourneyPatternId(), vehicleJourneyList);
			}
			vehicleJourneyList.add(course);
		}

		// map of VehicleJourneyAtStop and list by vehicleJourneyId
		List<Horaire> vehicleJourneyAtStops = line.getHoraires();
		// vehicleJourneyAtStopByIdMap = new HashMap<String, Horaire>();
		vjasListByvjIdMap = new HashMap<String, List<Horaire>>();
		for (Horaire horaire : vehicleJourneyAtStops)
		{
			// vehicleJourneyAtStopByIdMap.put(horaire.get, horaire);
			List<Horaire> vehicleJourneyAtStopList = vjasListByvjIdMap.get(horaire.getVehicleJourneyId());
			if (vehicleJourneyAtStopList == null)
			{
				vehicleJourneyAtStopList = new ArrayList<Horaire>();
				vjasListByvjIdMap.put(horaire.getVehicleJourneyId(), vehicleJourneyAtStopList);
			}
			vehicleJourneyAtStopList.add(horaire);
		}

		// map stoppoint route
		routeByStopPointIdMap = new HashMap<String, Itineraire>();
		journeyPatternIdListByRouteIdMap = new HashMap<String,List<String>>();
		for (Mission mission : journeyPatterns)
		{
			Itineraire route = getRoute(mission.getRouteId());
			List<Horaire> stopTimes = getVehicleJourneyAtStopListOfJourneyPattern(mission.getObjectId());
			if (stopTimes != null)
			{
				for (Horaire stopTime : stopTimes) 
				{
					routeByStopPointIdMap.put(stopTime.getStopPointId(),route);
				}
			}
			List<String> journeyPatternList = journeyPatternIdListByRouteIdMap.get(mission.getRouteId());
			if (journeyPatternList == null)
			{
				journeyPatternList = new ArrayList<String>();
				journeyPatternIdListByRouteIdMap.put(mission.getRouteId(), journeyPatternList);
			}
			journeyPatternList.add(mission.getObjectId());
		}        

		this.timetableIdListByjpIdMap = new HashMap<String, List<String>>();
		List<TableauMarche> timetables = line.getTableauxMarche();
		for (TableauMarche timetable : timetables)
		{
			for (int i = 0; i < timetable.getVehicleJourneyIdCount(); ++i)
			{
				String vehicleJourneyId = timetable.getVehicleJourneyId(i);
				Course vehicleJourney = (Course)this.vehicleJourneyByIdMap.get(vehicleJourneyId);
				if (vehicleJourney != null)
				{
					String journeyPatternId = vehicleJourney.getJourneyPatternId();
					List<String> timetablesOfJP = this.timetableIdListByjpIdMap.get(journeyPatternId);
					if (timetablesOfJP == null)
					{
						timetablesOfJP = new ArrayList<String>();
						this.timetableIdListByjpIdMap.put(journeyPatternId, timetablesOfJP);
					}
					if (timetablesOfJP.contains(timetable.getObjectId()))
						continue;
					timetablesOfJP.add(timetable.getObjectId());
				}
			}
		}

	}

	public ArretItineraire getStopPoint(String stopPointId)
	{
		return stopPointByIdMap.get(stopPointId);
	}

	public List<ArretItineraire> getStopPointsOfRoute(String routeId)
	{
		return stopPointsOfRouteIdMap.get(routeId);
	}

	public PositionGeographique getStopArea(String stopAreaId)
	{
		return stopAreaByIdMap.get(stopAreaId);
	}

	public Itineraire getRoute(String routeId)
	{
		return routeByIdMap.get(routeId);
	}

	public Itineraire getRouteOfStopPoint(String stopPointId)
	{
		return routeByStopPointIdMap.get(stopPointId);
	}

	public Mission getJourneyPattern(String journeyPatternId)
	{
		return journeyPatternByIdMap.get(journeyPatternId);
	}

	public Course getVehicleJourney(String vehicleJourneyId)
	{
		return vehicleJourneyByIdMap.get(vehicleJourneyId);
	}

	public List<Course> getVehicleJourneyList(String journeyPatternId)
	{
		return vjListByjpIdMap.get(journeyPatternId);
	}

	public List<Horaire> getVehicleJourneyAtStopList(String vehicleJourneyId)
	{
		return vjasListByvjIdMap.get(vehicleJourneyId);
	}

	public List<Horaire> getVehicleJourneyAtStopListOfJourneyPattern(String journeyPatternId)
	{
		List<Course> courses = getVehicleJourneyList(journeyPatternId);
		if (courses == null) return null;
		if (courses.size() == 0) return null;
		return (getVehicleJourneyAtStopList(courses.get(0).getObjectId()));
	}

	public List<String> getTimetableIdList(String journeyPatternId)
	{
		return timetableIdListByjpIdMap.get(journeyPatternId);
	}

	public List<String> getJourneyPatternList(String routeId)
	{
		List<String> ret = journeyPatternIdListByRouteIdMap.get(routeId);
		if (ret == null) ret = new ArrayList<String>();
		return ret;
	}

}

