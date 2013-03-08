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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
        complexModelFactory = (ComplexModelFactory) applicationContext.getBean("complexModelFactory");
        
        
        line = modelFactory.createModel(Line.class);
        route1 = complexModelFactory.nominalRoute(21, 7, 2, "1");
        route2 = complexModelFactory.nominalRoute(22, 7, 2, "2");
        route3 = complexModelFactory.nominalRoute(23, 7, 2, "3");
        List<Route> routes = new ArrayList<Route>(3);
        routes.add(route1);routes.add(route2);routes.add(route3);
        line.setRoutes(routes);
        
        line.complete();
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }    
    
    @Test(groups = {"Route"}, description = "Validate presence of Route element for each route in line")
    public void verifyRouteElementsPresenceWithId() throws XPathExpressionException, ParseException {
        Assert.assertEquals( Integer.parseInt( xPath.evaluate("count(//netex:ServiceFrame/netex:routes/netex:Route)", xmlDocument)), 
                             line.getRoutes().size());
        for( Route route : line.getRoutes()) {
            Assert.assertTrue( Boolean.parseBoolean( xPath.evaluate("boolean(//netex:ServiceFrame/netex:routes/netex:Route[@id = '"+route.getObjectId()+"'])", xmlDocument)));
        }
    }
    
    @Test(groups = {"Route"}, description = "Validate Route Name")
    public void verifyRouteElementsName() throws XPathExpressionException, ParseException {
        for( Route route : line.getRoutes()) {
            Assert.assertTrue( Boolean.parseBoolean( 
                xPath.evaluate("boolean(//netex:Route[@id = '"+route.getObjectId()+"']/netex:Name/text()='"+route.getName()+"')", xmlDocument)));
        }
    }
    
}
