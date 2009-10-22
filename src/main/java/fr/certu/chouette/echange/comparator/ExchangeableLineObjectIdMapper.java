/**
 * 
 */
package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chouette.schema.StopPoint;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Course;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.PositionGeographique;

/**
 * @author michel
 *
 */
public class ExchangeableLineObjectIdMapper
{
    private HashMap<String, ArretItineraire> stopPointByIdMap;
    private HashMap<String, PositionGeographique> stopAreaByIdMap;
    private HashMap<String, Itineraire> routeByIdMap;
    private HashMap<String, Mission> journeyPatternByIdMap;
    private HashMap<String, Course> vehicleJourneyByIdMap;
    
    // maps des courses d'une mission
    private HashMap<String,List<Course>> vjListByjpIdMap;
    // maps des horaires d'une course
    private HashMap<String,List<Horaire>> vjasListByvjIdMap;

    public ExchangeableLineObjectIdMapper(ILectureEchange line)
    {
        // map of StopPoint
        List<ArretItineraire> arrets = line.getArrets();
        stopPointByIdMap = new HashMap<String, ArretItineraire>();
        for (ArretItineraire arretItineraire : arrets)
        {
            stopPointByIdMap.put(arretItineraire.getObjectId(), arretItineraire);
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
    }
    
    public ArretItineraire getStopPoint(String objectId)
    {
        return stopPointByIdMap.get(objectId);
    }
        
    public PositionGeographique getStopArea(String objectId)
    {
        return stopAreaByIdMap.get(objectId);
    }
        
    public Itineraire getRoute(String objectId)
    {
        return routeByIdMap.get(objectId);
    }
    
    public Mission getJourneyPattern(String objectId)
    {
        return journeyPatternByIdMap.get(objectId);
    }
    
    public Course getVehicleJourney(String objectId)
    {
        return vehicleJourneyByIdMap.get(objectId);
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
    
}

