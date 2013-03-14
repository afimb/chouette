/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import java.text.ParseException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 *
 * @author marc
 */
@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
@Test(groups = {"Route"}, description = "Validate Route export in NeTEx format")
public class RouteTest extends AbstractTestNGSpringContextTests {
    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private ComplexModelFactory complexModelFactory;
    private Line line;
    private Route route1;
    private Route route2;
    private Route route3;
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    @BeforeMethod
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());

        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        

        line = complexModelFactory.nominalLine( "1");

        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }    
    
    @Test(groups = { "ServiceFrame", "routes"}, description = "Validate presence of Route element with expected id")
    public void verifyRouteId() throws XPathExpressionException, ParseException {
        Assert.assertEquals( Integer.parseInt( xPath.evaluate("count(//netex:ServiceFrame/netex:routes/netex:Route)", xmlDocument)), 
                             line.getRoutes().size());
        for( Route route : line.getRoutes()) {
            assertXPathTrue( "boolean(//netex:ServiceFrame/netex:routes/netex:Route[@id = '"+route.getObjectId()+"'])");
        }
    }
    
    @Test(groups = {"ServiceFrame", "routes"}, description = "Validate presence of Route element with expected name")
    public void verifyRouteName() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            assertXPathTrue( "boolean(//netex:Route[@id = '"+route.getObjectId()+"']/netex:Name/text()='"+route.getName()+"')");
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
                                                        "[@ref = '"+
                                                        stopPoint.objectIdPrefix()+
                                                        ":StopPoint:"+
                                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "scheduledStopPoints"}, description = "Validate presence of ScheduledStopPoint with expected id")
    public void verifyScheduledStopPointId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:scheduledStopPoints/netex:ScheduledStopPoint"+
                                                        "[@id = '"+
                                                        stopPoint.objectIdPrefix()+
                                                        ":StopPoint:"+
                                                        stopPoint.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyRouteRefRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( StopPoint stopPoint : route.getStopPoints()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/netex:RouteRef"+
                                                        "[@ref = '"+
                                                        route.objectIdPrefix()+
                                                        ":Route:"+
                                                        route.objectIdSuffix()+
                                                    "'])";
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
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/netex:pointsInSequence/netex:StopPointInJourneyPattern/netex:ScheduledStopPointRef"+
                                                        "[@ref = '"+
                                                        stopPoint.objectIdPrefix()+
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
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyServicePatternRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            int vehicleCount = 0;
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                vehicleCount += journeyPattern.getVehicleJourneys().size();
            }
            
            String xPathExpr = "count(//netex:TimetableFrame/netex:vehicleJourneys/netex:ServiceJourney/netex:RouteRef"+
                                                    "[@ref = '"+
                                                    route.objectIdPrefix()+
                                                    ":Route:"+
                                                    route.objectIdSuffix()+
                                                "'])";

            Assert.assertEquals( Integer.parseInt( 
                    xPath.evaluate( xPathExpr, 
                                    xmlDocument)), vehicleCount);
                
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
                                        "[@ref = '"+
                                            stopPoint.objectIdPrefix()+
                                            ":StopPoint:"+
                                            stopPoint.objectIdSuffix()+
                                                        "'])";
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
                                    "[@ref = '"+
                                        stopPoint.objectIdPrefix()+
                                        ":StopPoint:"+
                                        stopPoint.objectIdSuffix()+
                                                    "'])";
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
                                    "[@ref = '"+
                                        stopPoint.getContainedInStopArea().objectIdPrefix()+
                                        ":Quay:"+
                                        stopPoint.getContainedInStopArea().objectIdSuffix()+
                                                    "'])";
                //if (true) throw new RuntimeException( xPathExpr);
                assertXPathTrue( xPathExpr);

            }
        }
    }

    private void assertXPathTrue(String xPathExpr) throws XPathExpressionException {
        Assert.assertTrue( Boolean.parseBoolean( 
                xPath.evaluate( xPathExpr, 
                                xmlDocument)));
    }
    
}
