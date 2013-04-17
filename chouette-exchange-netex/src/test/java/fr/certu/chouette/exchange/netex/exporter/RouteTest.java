/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.Route;
import java.text.ParseException;

import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

/**
 *
 * @author marc
 */
@Test(groups = {"Route"}, description = "Validate Route export in NeTEx format")
public class RouteTest extends ChouetteModelTest {
    
    @Test(groups = { "ServiceFrame", "routes"}, description = "Validate presence of Route element with expected id")
    public void verifyRouteId() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:ServiceFrame/netex:routes/netex:Route)", 
                             line.getRoutes().size());
        for( Route route : line.getRoutes()) {
            assertXPathTrue( "boolean(//netex:ServiceFrame/netex:routes/netex:Route[@id = '"+route.getObjectId()+"'])");
        }
    }
    
    @Test(groups = {"ServiceFrame", "routes"}, description = "Validate presence of Route element with expected name")
    public void verifyRouteName() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            assertXPathTrue( "boolean(//netex:Route[@id = '"+modelTranslator.netexId(route) +"']/netex:Name/text()='"+route.getName()+"')");
        }
    }
    
    @Test(groups = {"ServiceFrame", "routes"}, description = "Validate presence of PointOnRoute with expected id")
    public void verifyPointOnRouteId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:routes/netex:Route/netex:pointsInSequence/netex:PointOnRoute"+
                                                        "[@id = '"+
                                                        route.objectIdPrefix()+
                                                        ":PointOnRoute:"+
                                                        stopPoint.objectIdSuffix()+
                                                        "-"+
                                                        stopPoint.getPosition()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "routes"}, description = "Validate presence of RoutePointRef with expected ref")
    public void verifyRoutePointRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:routes/netex:Route/netex:pointsInSequence/netex:PointOnRoute/netex:RoutePointRef"+
                                                        "[@ref = '"+
                                                        route.objectIdPrefix()+
                                                        ":RoutePoint:"+
                                                        route.objectIdSuffix()+
                                                        "A"+
                                                        stopPoint.getPosition()+
                                                        "A"+
                                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "routePoints"}, description = "Validate presence of RoutePoint with expected id")
    public void verifyRoutePointId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:routePoints/netex:RoutePoint"+
                                                        "[@id = '"+
                                                        route.objectIdPrefix()+
                                                        ":RoutePoint:"+
                                                        route.objectIdSuffix()+
                                                        "A"+
                                                        stopPoint.getPosition()+
                                                        "A"+
                                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "routePoints"}, description = "Validate presence of ProjectedPointRef with expected ref")
    public void verifyProjectedPointRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:routePoints/netex:RoutePoint/netex:projections/netex:PointProjection/netex:ProjectedPointRef"+
                                    "[@ref = '"+modelTranslator.netexId(stopPoint)+"'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "scheduledStopPoints"}, description = "Validate presence of ScheduledStopPoint with expected id")
    public void verifyScheduledStopPointId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:scheduledStopPoints/netex:ScheduledStopPoint"+
                                        "[@id = '"+modelTranslator.netexId(stopPoint)+"'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyRouteRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/netex:RouteRef"+
                                         "[@ref = '"+ modelTranslator.netexId(route) + "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of StopPointInJourneyPattern with expected id")
    public void verifyStopPointInJourneyPatternId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/netex:pointsInSequence/netex:StopPointInJourneyPattern"+
                                                        "[@id = '"+
                                                        stopPoint.objectIdPrefix()+
                                                        ":StopPointInJourneyPattern:"+
                                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of ScheduledStopPointRef with expected ref")
    public void verifyScheduledStopPointRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/"+
                                            "netex:pointsInSequence/netex:StopPointInJourneyPattern/"+
                                            "netex:ScheduledStopPointRef"+
                                            "[@ref = '"+modelTranslator.netexId(stopPoint)+"'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyServicePatternRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            int vehicleCount = 0;
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                vehicleCount += journeyPattern.getVehicleJourneys().size();
            }
            
            String xPathExpr = "count(//netex:TimetableFrame/netex:vehicleJourneys/netex:ServiceJourney/netex:RouteRef"+
                                    "[@ref = '"+modelTranslator.netexId(route)+"'])";

            assertXPathCount(  xPathExpr, vehicleCount);
                
        }
    }

    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ScheduledStopPointRef with expected ref")
    public void verifyVehicleJourneysScheduledStopPointRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                for ( StopPoint stopPoint : journeyPattern.getStopPoints()) {
                    String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                        "netex:ServiceJourney/netex:calls/"+
                                        "netex:Call/netex:ScheduledStopPointRef"+
                                        "[@ref = '"+modelTranslator.netexId(stopPoint)+"'])";
                    assertXPathTrue( xPathExpr);

                }
            }
            
                
        }
    }
    
    @Test(groups = {"ServiceFrame", "stopAssignments"}, description = "Validate presence of PassengerStopAssignment with expected ref")
    public void verifyPassengerStopAssignmentId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:stopAssignments/"+
                                    "netex:PassengerStopAssignment"+
                                    "[@id = '"+
                                        stopPoint.objectIdPrefix()+
                                        ":PassengerStopAssignment:"+
                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);

            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "stopAssignments"}, description = "Validate presence of ScheduledStopPointRef with expected ref")
    public void verifyPassengerStopAssignmentScheduledStopPointRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:stopAssignments/"+
                                    "netex:PassengerStopAssignment/netex:ScheduledStopPointRef"+
                                    "[@ref = '"+ modelTranslator.netexId(stopPoint)+ "'])";
                assertXPathTrue( xPathExpr);

            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "stopAssignments"}, description = "Validate presence of QuayRef with expected ref")
    public void verifyPassengerStopAssignmentQuayRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:stopAssignments/"+
                                    "netex:PassengerStopAssignment/netex:QuayRef"+
                                    "[@ref = '"+ modelTranslator.netexId(stopPoint.getContainedInStopArea())+"'])";
                //if (true) throw new RuntimeException( xPathExpr);
                assertXPathTrue( xPathExpr);

            }
        }
    }
    
}
