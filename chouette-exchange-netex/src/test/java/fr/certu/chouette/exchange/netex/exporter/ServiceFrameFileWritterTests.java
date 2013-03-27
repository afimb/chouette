package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.exporter.NetexFileWriter;
import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.Route;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class ServiceFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private ComplexModelFactory complexModelFactory;
    private List<Line> lines = new ArrayList<Line>();
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    private int stopCount = 20;
    private int journeyPatternCount = 3;
    private int vehicleCount = 2;

    @BeforeMethod
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
        
        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        
        
        Line line = complexModelFactory.nominalLine( "1");
        line.complete();
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one network")
    public void verifyNetwork() throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile("//netex:Network/netex:Name/node()"); 
        NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);       
//        for (int i = 0; i < nodes.getLength(); i++) {
//            logger.error(nodes.item(0).toString());
//        }
        assert nodes.getLength() == 1;
        assert nodes.item(0).getNodeValue().equals("METRO");
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one company")
    public void verifyCompany() throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile("//netex:Operator/netex:Name/node()");
        NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);

        assert nodes.getLength() == 1;
        assert nodes.item(0).getNodeValue().equals("RATP nom long");

    }


    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one line")
    public void verifyLine() throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile("//netex:Line/netex:Name/node()");
        NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);

        assert nodes.getLength() == 1;
        assert nodes.item(0).getNodeValue().equals("7B");
    }




    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have TariffZone")
    public void verifyTariffZone() throws XPathExpressionException {
        XPathExpression xPathExpression = xPath.compile("//netex:tariffZones/netex:TariffZone");
        NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
        //assert nodes.getLength() == 6;
    }


}
