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
import java.util.ArrayList;
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
    
    public List<StopPoint> stopPointList( int size) {
        List<StopPoint> stopPoints = new ArrayList<StopPoint>();
        try {
            for ( int i=0; i<size; i++) {
                StopArea stopArea = modelFactory.createModel(StopArea.class);
                StopPoint stopPoint = new StopPoint();
                stopPoint.setContainedInStopArea( stopArea);
                stopPoint = modelFactory.createModel( stopPoint);

                stopPoints.add( stopPoint);
            }
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stopPoints;
    }
    public List<JourneyPattern> journeyPatternList( List<StopPoint> stopPoints, int size) {
        List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>(size);
        try {
            for ( int i=0; i<size; i++) {
                JourneyPattern journeyPattern = new JourneyPattern();
                journeyPattern.setName( i+" modulo "+size);
                journeyPattern = modelFactory.createModel( JourneyPattern.class);
                journeyPatterns.add( journeyPattern);
            }
            
            
            for ( int i=0; i<stopPoints.size(); i++) {
                JourneyPattern journeyPattern = null;
                journeyPattern = journeyPatterns.get( i%size);
                journeyPattern.addStopPoint( stopPoints.get(i));
            }
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return journeyPatterns;
    }
    
    public Route nominalRoute( int stopCount, int journeyPatternCount) {
        Route route = new Route();
        try {
        
            List<StopPoint> stopPoints = stopPointList( 20);
            List<JourneyPattern> journeyPatterns = journeyPatternList( stopPoints, 3);
            
            route = modelFactory.createModel( route);
            route.setJourneyPatterns( journeyPatterns);
            route.setStopPoints( stopPoints);
        System.out.println( "RRRRRRRRRRRRRRRRRR");
        System.out.println( "lstopPoints "+route.getStopPoints().size());
        System.out.println( "RRRRRRRRRRRRRRRRRR");
            
            
        } catch (CreateModelException ex) {
            Logger.getLogger(ComplexModelFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return route;
    }
    
}
