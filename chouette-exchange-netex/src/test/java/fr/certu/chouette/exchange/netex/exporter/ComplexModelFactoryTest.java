/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.util.List;
import org.springframework.test.context.ContextConfiguration;

/**
 *
 * @author marc
 */
@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class ComplexModelFactoryTest  extends AbstractTestNGSpringContextTests {
    private ComplexModelFactory complexModelFactory;
    private ModelFactory modelFactory;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        complexModelFactory = new ComplexModelFactory();
    }   

    @Test(groups = {"ComplexModelFactory.nominalRoute"}, description = "Should create as many journeys as requested, the same for stops")
    public void verifyRoute() {
        complexModelFactory.setJourneyPatternCount( 2);
        complexModelFactory.setVehicleCount( 2);
        complexModelFactory.setRouteStopCount( 5);
        complexModelFactory.setQuayCount( 5);
        complexModelFactory.init();
        
        Route route = complexModelFactory.nominalLine( "1").getRoutes().get(0);
        Assert.assertEquals( route.getJourneyPatterns().size(), complexModelFactory.getJourneyPatternCount());
        Assert.assertEquals( route.getStopPoints().size(), complexModelFactory.getRouteStopCount());
    }

    @Test(groups = {"ComplexModelFactory.nominalRoute"}, description = "Should complitable")
    public void verifyRouteCompletable() {
        complexModelFactory.setJourneyPatternCount( 2);
        complexModelFactory.setVehicleCount( 2);
        complexModelFactory.setRouteStopCount( 5);
        complexModelFactory.setQuayCount( 5);
        complexModelFactory.init();
        
        Route route = complexModelFactory.nominalLine( "1").getRoutes().get(0);
        System.out.println( "route.getStopPoints().size()="+route.getStopPoints().size()); 
        route.complete();
        Assert.assertTrue(true);
    }

    @Test(groups = {"ComplexModelFactory.nominalRoute"}, description = "Should make a stop point partition and affect each stops part to each journey")
    public void verifyJourneyPatternStopPointCount() {
        int stopCount = 27;
        int journeyPatternCount = 5;
        int vehicleCount = 2;
        
        complexModelFactory.setJourneyPatternCount( journeyPatternCount);
        complexModelFactory.setVehicleCount( vehicleCount);
        complexModelFactory.setRouteStopCount( stopCount);
        complexModelFactory.setQuayCount( stopCount);
        complexModelFactory.init();
        
        Route route = complexModelFactory.nominalLine( "1").getRoutes().get(0);
        for ( int i=0; i<journeyPatternCount; i++) {
            JourneyPattern jp = route.getJourneyPatterns().get(i);
            int reste = stopCount % journeyPatternCount;
            int quotien = stopCount / journeyPatternCount;
            int jpCount = quotien + (i<reste ? 1 : 0);
            System.out.println( "jp.getStopPoints().size()="+jp.getStopPoints().size());
            System.out.println( "jpCount="+jpCount);
            Assert.assertEquals( jp.getStopPoints().size(), jpCount);
        }
    }

    @Test(groups = {"ComplexModelFactory.nominalRoute"}, description = "Should make vehicleJourney referencing the journey pattern")
    public void verifyVehicleJourneysOnJourneyPattern() {
        int stopCount = 27;
        int journeyPatternCount = 5;
        int vehicleCount = 2;
        
        complexModelFactory.setJourneyPatternCount( journeyPatternCount);
        complexModelFactory.setVehicleCount( vehicleCount);
        complexModelFactory.setRouteStopCount( stopCount);
        complexModelFactory.setQuayCount( stopCount);
        complexModelFactory.init();
        
        Route route = complexModelFactory.nominalLine( "1").getRoutes().get(0);
                
        for ( int i=0; i<journeyPatternCount; i++) {
            JourneyPattern jp = route.getJourneyPatterns().get(i);
            for ( VehicleJourney vj : jp.getVehicleJourneys()) {                
                Assert.assertEquals( vj.getJourneyPattern().getObjectId(), jp.getObjectId());
            }
        }
    }

    @Test(groups = {"ComplexModelFactory.nominalRoute"}, description = "Should define same blueprint attribute for wayback")
    public void verifySimpleAttribute() {
        complexModelFactory.init();
        
        Route route = complexModelFactory.nominalLine( "1").getRoutes().get(0);
        Route defaultRoute = null;
        try {
            defaultRoute = modelFactory.createModel( Route.class);
        } catch (CreateModelException ex) {
            Assert.assertTrue( false);
        }
        Assert.assertEquals( route.getWayBack(), defaultRoute.getWayBack());
    }
    
    
}
