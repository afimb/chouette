package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.policy.PolicyException;
import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.JourneyPattern;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.velocity.tools.generic.DateTool;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class ServiceCalendarFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private List<Timetable> timetables = new ArrayList<Timetable>();
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    DateTool date = new DateTool();
    String dateFormat = "yyyy-MM-d'T'HH:mm:ss'Z'";
    protected ModelTranslator modelTranslator = new ModelTranslator();
    

    @BeforeClass
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());

        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");

        Line line = prepareModel();
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }

    private Line prepareModel() throws CreateModelException, PolicyException {
        Line line = modelFactory.createModel(Line.class);
        Route route = modelFactory.createModel(Route.class);
        JourneyPattern journeyPattern = modelFactory.createModel(JourneyPattern.class);
        VehicleJourney vehicleJourney = modelFactory.createModel(VehicleJourney.class);

        line.addRoute(route);
        route.addJourneyPattern(journeyPattern);
        journeyPattern.addVehicleJourney(vehicleJourney);

        vehicleJourney.addTimetables(createTimetables(2));

        line.complete();

        return line;
    }

    private List<Timetable> createTimetables(int counter) throws CreateModelException {
        timetables = new ArrayList<Timetable>(counter);

        for (int i = 0; i < counter; i++) {
            Timetable timeTable = modelFactory.createModel(Timetable.class);

            timeTable.addDayType(DayTypeEnum.MONDAY);
            timeTable.addDayType(DayTypeEnum.TUESDAY);

            timeTable.addCalendarDay(new Date(10000));
            timeTable.addCalendarDay(new Date(20000));

            timeTable.addPeriod(new Period(new Date(20000), new Date(30000)));
            timeTable.addPeriod(new Period(new Date(40000), new Date(50000)));

            timetables.add(timeTable);
        }

        return timetables;
    }

    @Test(groups = {"ServiceCalendarFrame"}, description = "Verify 2 TimeTableFrame exists")
    public void verifyTimeTableFrame() throws XPathExpressionException {
        Assert.assertEquals(xPath.evaluate("count(//netex:ServiceCalendarFrame)", xmlDocument), "2", "Must have 2 ServiceCalendarFrame");
    }

    @Test(groups = {"ServiceCalendarFrame"}, description = "Check if dayTypes exist")
    public void verifyDayTypes() throws XPathExpressionException {
        for (Timetable timetable : timetables) {
            Assert.assertNotNull(xPath.evaluate("//netex:ServiceCalendarFrame//netex:DayType[@id='" +modelTranslator.netexId(timetable) + "']", xmlDocument, XPathConstants.NODE), "Must find DayType");

            for (DayTypeEnum dayType : timetable.getDayTypes()) {
                Assert.assertNotNull(xPath.evaluate("//netex:ServiceCalendarFrame//netex:DayType[@id='" + modelTranslator.netexId(timetable) + "']//netex:PropertyOfDay[netex:DaysOfWeek = '" + dayType.value() + "']", xmlDocument, XPathConstants.STRING), "daysOfWeek must exists");
            }
        }
    }


}
