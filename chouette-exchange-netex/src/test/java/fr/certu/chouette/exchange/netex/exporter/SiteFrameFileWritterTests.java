package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.exporter.NetexFileWriter;
import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class SiteFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private List<Line> lines = new ArrayList<Line>();
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;

    @BeforeClass
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");

        Line line = modelFactory.createModel(Line.class);    
        Route route = modelFactory.createModel(Route.class);
        line.addRoute(route);
        route.setStopPoints(stopPointsAndAncestors(2));        
        line.complete();
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }
 
    private List<StopPoint> stopPointsAndAncestors(int counter) throws CreateModelException
    {       
        List<StopPoint> stopPoints = new ArrayList<StopPoint>();
        
        for (int i = 0; i < counter; i++) {            
            StopArea stopPlace = modelFactory.createModel(StopArea.class);
            stopPlace.setAreaType(ChouetteAreaEnum.STOPPLACE);
            stopPlace.setObjectId("RATP_PIVI:StopArea:SP" + i);
            
            StopArea commercialStopPoint = modelFactory.createModel(StopArea.class);
            commercialStopPoint.setAreaType(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
            commercialStopPoint.setParent(stopPlace);
            commercialStopPoint.setObjectId("RATP_PIVI:StopArea:CSP" + i);
            
            stopPlace.addContainedStopArea(commercialStopPoint);
            
            StopArea quay = modelFactory.createModel(StopArea.class);
            quay.setAreaType(ChouetteAreaEnum.QUAY);
            quay.setParent(commercialStopPoint);
            quay.setObjectId("RATP_PIVI:StopArea:Q" + i);
            
            commercialStopPoint.addContainedStopArea(quay);
            
            StopPoint stopPoint = modelFactory.createModel(StopPoint.class);
            stopPoint.setContainedInStopArea(quay);
            stopPoint.setObjectId("RATP_PIVI:StopPoint:" + i);
            
            quay.addContainedStopPoint(stopPoint);
            
            stopPoints.add(stopPoint);
        }
        
        
        return stopPoints;
    }

    @Test(groups = {"SiteFrame"}, description = "Site frame must have 2 stop places")
    public void verifyStopPlace() throws XPathExpressionException {
        NodeList nodes = (NodeList) xPath.evaluate("//netex:stopPlaces/netex:StopPlace", xmlDocument, XPathConstants.NODESET);        
        assert nodes.getLength() == 2;                
        assert( xPath.evaluate("//netex:stopPlaces/netex:StopPlace[@id='RATP_PIVI:StopPlace:CSP0']", xmlDocument, XPathConstants.NODE) != null );
        assert( xPath.evaluate("//netex:stopPlaces/netex:StopPlace[@id='RATP_PIVI:StopPlace:CSP1']", xmlDocument, XPathConstants.NODE) != null );
        assert( xPath.evaluate("//netex:StopPlace/netex:ContainedInPlaceRef[@ref='RATP_PIVI:TopographicPlace:SP0']", xmlDocument, XPathConstants.NODE) != null );
        assert( xPath.evaluate("//netex:StopPlace/netex:ContainedInPlaceRef[@ref='RATP_PIVI:TopographicPlace:SP1']", xmlDocument, XPathConstants.NODE) != null );
    }
    
    @Test(groups = {"SiteFrame"}, description = "Site frame must have 2 topographic stop places")
    public void verifyTopographicStopPlace() throws XPathExpressionException {
        NodeList nodes = (NodeList) xPath.evaluate("//netex:topographicPlaces/netex:TopographicPlace", xmlDocument, XPathConstants.NODESET);       
        assert nodes.getLength() == 2;
        assert( xPath.evaluate("//netex:topographicPlaces/netex:TopographicPlace[@id='RATP_PIVI:TopographicPlace:SP0']", xmlDocument, XPathConstants.NODE) != null );
        assert( xPath.evaluate("//netex:topographicPlaces/netex:TopographicPlace[@id='RATP_PIVI:TopographicPlace:SP1']", xmlDocument, XPathConstants.NODE) != null );
    }
    
    @Test(groups = {"SiteFrame"}, description = "Site frame must have 2 quays")
    public void verifyQuay() throws XPathExpressionException {
        NodeList nodes = (NodeList) xPath.evaluate("//netex:quays/netex:Quay", xmlDocument, XPathConstants.NODESET);       
        assert nodes.getLength() == 2;
        
        assert( xPath.evaluate("//netex:quays/netex:Quay[@id='RATP_PIVI:Quay:Q0']", xmlDocument, XPathConstants.NODE) != null );
        assert( xPath.evaluate("//netex:quays/netex:Quay[@id='RATP_PIVI:Quay:Q1']", xmlDocument, XPathConstants.NODE) != null );
    }
   
  
}
