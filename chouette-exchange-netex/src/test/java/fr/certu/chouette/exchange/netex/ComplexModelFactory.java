/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author marc
 */
public class ComplexModelFactory {
    @Getter @Setter private ModelFactory modelFactory;
    
    public List<StopPoint> stopPointList( int size, String routeId) {
        List<StopPoint> stopPoints = new ArrayList<StopPoint>();
        try {
            for ( int i=0; i<size; i++) {
                StopArea stopArea = modelFactory.createModel(StopArea.class);
                StopPoint stopPoint = new StopPoint();
                stopPoint.setContainedInStopArea( stopArea);
                stopPoint.setObjectId( "T:STOP_POINT:"+routeId+"-"+i);
                stopPoint = modelFactory.createModel( stopPoint);

                stopPoints.add( stopPoint);
            }
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stopPoints;
    }
    public List<JourneyPattern> journeyPatternList( List<StopPoint> stopPoints, int size, int vehicleCount, String routeId) {
        List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>(size);
        try {
            for ( int i=0; i<size; i++) {
                JourneyPattern journeyPattern = new JourneyPattern();
                journeyPattern.setName( i+" modulo "+size);
                journeyPattern.setObjectId( "T:JOURNEY_PATTERN:"+routeId+"-"+i);
                journeyPattern.setStopPoints( new ArrayList<StopPoint>());
                journeyPattern = modelFactory.createModel( journeyPattern);
                journeyPatterns.add( journeyPattern);
            }
            
            
            for ( int i=0; i<stopPoints.size(); i++) {
                JourneyPattern journeyPattern = null;
                journeyPattern = journeyPatterns.get( i%size);
                journeyPattern.addStopPoint( stopPoints.get(i));
                
                if (journeyPattern.getStopPoints()==null)
                    throw new RuntimeException("echec");
            }
            
            
            for ( int i=0; i<size; i++) {
                JourneyPattern journeyPattern = journeyPatterns.get(i);
                journeyPattern.setVehicleJourneys( vehicleJourneyList(vehicleCount, journeyPattern, routeId+"-"+i));
            }
        } catch (Exception ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return journeyPatterns;
    }
    
    public Route nominalRoute( int stopCount, int journeyPatternCount, int vehicleCount, String routeId) {
        Route route = new Route();
        try {
            route.setObjectId( "T:ROUTE:"+routeId);
        
            List<StopPoint> stopPoints = stopPointList( stopCount, routeId);
            List<JourneyPattern> journeyPatterns = journeyPatternList( stopPoints, journeyPatternCount, vehicleCount, routeId);
            
            route.setJourneyPatterns( journeyPatterns);
            route.setStopPoints( stopPoints);
            route = modelFactory.createModel( route);
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return route;
    }
    public List<VehicleJourney> vehicleJourneyList( int count, JourneyPattern journeyPattern, String journeyId) {
        List<VehicleJourney> vehicles = new ArrayList<VehicleJourney>( count);
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.HOUR_OF_DAY, 13);
        calendar.set( Calendar.MINUTE, 5);
        calendar.set( Calendar.SECOND, 0);
        
        for ( int i=0; i<count; i++) {
            VehicleJourney vehicle = vehicleJourney( calendar, journeyPattern);
            vehicle.setObjectId( "T:VEHICLE_JOURNEY:"+journeyId+"-"+i);
            calendar.add( Calendar.MINUTE, 12);

            vehicles.add(vehicle);
        }
        
        return vehicles;
    }
    public VehicleJourney vehicleJourney( Calendar calendar, JourneyPattern journeyPattern) {
        VehicleJourney vehicle = new VehicleJourney();
        vehicle.setJourneyPattern(journeyPattern);
        try {
            
            for ( int i=0; i<journeyPattern.getStopPoints().size(); i++) {
                VehicleJourneyAtStop vjas = new VehicleJourneyAtStop();
                vjas.setStopPoint( journeyPattern.getStopPoints().get(i));
                vjas.setVehicleJourney(vehicle);
                
                vjas.setArrivalTime( new Time( calendar.getTime().getTime()));
                vjas.setDepartureTime( new Time( calendar.getTime().getTime()));
                calendar.add( Calendar.MINUTE, 3);
                vjas = modelFactory.createModel( vjas);
                
                vehicle.addVehicleJourneyAtStop(vjas);
            }
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vehicle;
    }
    
}
