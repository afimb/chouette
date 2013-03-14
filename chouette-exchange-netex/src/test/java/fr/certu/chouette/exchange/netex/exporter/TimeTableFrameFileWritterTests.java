package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.policy.PolicyException;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.JourneyPattern;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.sql.Time;
import java.util.Calendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class TimeTableFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private ComplexModelFactory complexModelFactory;
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    private VehicleJourney vehicleJourney;

    @BeforeClass
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        complexModelFactory = (ComplexModelFactory) applicationContext.getBean("complexModelFactory");
        Line line = prepareModel();        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }
    
    private Line prepareModel() throws CreateModelException, PolicyException
    {
        Line line = modelFactory.createModel(Line.class);        
        Route route = modelFactory.createModel(Route.class);
        JourneyPattern journeyPattern = modelFactory.createModel(JourneyPattern.class);
        vehicleJourney = modelFactory.createModel(VehicleJourney.class);
        
        line.addRoute(route);
        route.addJourneyPattern(journeyPattern);
        
        vehicleJourney.setRoute(route);
        vehicleJourney.setJourneyPattern(journeyPattern);
        
        for (int i = 0; i < 4; i++) {
            Timetable timeTable = modelFactory.createModel(Timetable.class);            
            vehicleJourney.addTimetable(timeTable);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(13, 5, 0);
        for (int j = 0; j < 4; j++) {
            VehicleJourneyAtStop vehicleJourneyAtStop = modelFactory.createModel(VehicleJourneyAtStop.class);
            vehicleJourneyAtStop.setArrivalTime(new Time(calendar.getTime().getTime()));
            vehicleJourneyAtStop.setDepartureTime(new Time(calendar.getTime().getTime()));
            calendar.add(Calendar.MINUTE, 3);
            vehicleJourney.addVehicleJourneyAtStop(vehicleJourneyAtStop);
        }                 
        
        journeyPattern.addVehicleJourney(vehicleJourney);        
        line.complete();
        
        return line;
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if 2 vehicle journeys exists")
    public void verifyVehicleJourneys() throws XPathExpressionException {
        assert xPath.evaluate("count(//netex:ServiceJourney)", xmlDocument).equals("1");               
        assert( xPath.evaluate("//netex:ServiceJourney[@id='" + vehicleJourney.objectIdPrefix() + ":VehicleJourney:" + vehicleJourney.objectIdSuffix() + "']", xmlDocument, XPathConstants.NODE) != null );
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if dayType exists")
    public void verifyDayTypes() throws XPathExpressionException {
        assert xPath.evaluate("count(//netex:ServiceJourney//netex:DayTypeRef)", xmlDocument).equals("4");       
        for (int i = 0; i < vehicleJourney.getTimetables().size(); i++) {
            Timetable timetable = vehicleJourney.getTimetables().get(i);                   
            assert( xPath.evaluate("//netex:ServiceJourney//netex:DayTypeRef[@ref='" + timetable.objectIdPrefix() + ":DayType:" + timetable.objectIdSuffix() + "']", xmlDocument, XPathConstants.NODE) != null );
        }            
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if a route exists")
    public void verifyRoute() throws XPathExpressionException {
        assert xPath.evaluate("count(//netex:ServiceJourney/netex:RouteRef)", xmlDocument).equals("1");
        assert( xPath.evaluate("//netex:ServiceJourney/netex:RouteRef[@ref='" + vehicleJourney.getRoute().objectIdPrefix() + ":Route:" + vehicleJourney.getRoute().objectIdSuffix() + "']", xmlDocument, XPathConstants.NODE) != null );
        
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "Check if a journey pattern exist")
    public void verifyJourneyPattern() throws XPathExpressionException {
        assert xPath.evaluate("count(//netex:ServiceJourney/netex:ServiceJourneyPatternRef)", xmlDocument).equals("1");
        assert( xPath.evaluate("//netex:ServiceJourney/netex:ServiceJourneyPatternRef[@ref='" + vehicleJourney.getJourneyPattern().objectIdPrefix() + ":ServiceJourneyPattern:" + vehicleJourney.getJourneyPattern().objectIdSuffix() + "']", xmlDocument, XPathConstants.NODE) != null );        
    }
        
    @Test(groups = {"TimeTableFrame"}, description = "Check if Calls exists")
    public void verifyCalls() throws XPathExpressionException {
        assert xPath.evaluate("count(//netex:ServiceJourney//netex:Call)", xmlDocument).equals("4");
        assert xPath.evaluate("count(//netex:ServiceJourney//netex:ScheduledStopPointRef)", xmlDocument).equals("4");
        for (int i = 0; i < vehicleJourney.getVehicleJourneyAtStops().size(); i++) {
            VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourney.getVehicleJourneyAtStops().get(i);                   
            assert( xPath.evaluate("//netex:ServiceJourney//netex:ScheduledStopPointRef[@ref='" + vehicleJourneyAtStop.getStopPoint().objectIdPrefix() + ":StopPoint:" + vehicleJourneyAtStop.getStopPoint().objectIdSuffix() + "']", xmlDocument, XPathConstants.NODE) != null );
        }        
    }
    
}
