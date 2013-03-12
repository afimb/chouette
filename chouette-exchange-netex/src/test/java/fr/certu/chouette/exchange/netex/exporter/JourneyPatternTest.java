/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;


import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.ParseException;
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
@Test(groups = {"JourneyPattern"}, description = "Validate JourneyPattern export in NeTEx format")
public class JourneyPatternTest extends AbstractTestNGSpringContextTests {
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
        complexModelFactory = (ComplexModelFactory) applicationContext.getBean("complexModelFactory");
        
        
        line = modelFactory.createModel(Line.class);
        route1 = complexModelFactory.nominalRoute(21, 7, 2, "1");
        route2 = complexModelFactory.nominalRoute(22, 7, 2, "2");
        route3 = complexModelFactory.nominalRoute(23, 7, 2, "3");
        List<Route> routes = new ArrayList<Route>(3);
        routes.add(route1);routes.add(route2);routes.add(route3);
        line.setRoutes(routes);
        
        line.complete();
        // TODO: code below should be in ComplexModelFactory 
        for( Route route : line.getRoutes()) {
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                journeyPattern.setRoute( route);
                for ( VehicleJourney vehicleJourney : journeyPattern.getVehicleJourneys()) {
                    vehicleJourney.setRoute( route);
                    vehicleJourney.setJourneyPattern( journeyPattern);
                }
            }
        }
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }    
    
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyServicePatternId() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern"+
                                                        "[@id = '"+
                                                        journeyPattern.objectIdPrefix()+
                                                        ":ServicePattern:"+
                                                        journeyPattern.objectIdSuffix()+
                                                    "'])";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"ServiceFrame", "servicePatterns"}, description = "Validate presence of RouteRef with expected ref")
    public void verifyServicePatternName() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                String xPathExpr = "boolean(//netex:ServiceFrame/netex:servicePatterns/netex:ServicePattern/netex:Name/text()='"+
                                                        journeyPattern.getName()+
                                                    "')";
                assertXPathTrue( xPathExpr);
                
            }
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServicePatternRef with expected ref")
    public void verifyServicePatternRef() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            for ( JourneyPattern journeyPattern : route.getJourneyPatterns()) {
                String xPathExpr = "count(//netex:TimetableFrame/netex:vehicleJourneys/netex:ServiceJourney/netex:ServicePatternRef"+
                                                        "[@ref = '"+
                                                        journeyPattern.objectIdPrefix()+
                                                        ":ServicePattern:"+
                                                        journeyPattern.objectIdSuffix()+
                                                    "'])";
                Assert.assertEquals( Integer.parseInt( 
                        xPath.evaluate( xPathExpr, 
                                        xmlDocument)), journeyPattern.getVehicleJourneys().size());

            }
        }
    }

    private void assertXPathTrue(String xPathExpr) throws XPathExpressionException {
        Assert.assertTrue( Boolean.parseBoolean( 
                xPath.evaluate( xPathExpr, 
                                xmlDocument)));
    }
    
}
