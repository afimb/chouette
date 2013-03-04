package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.exporter.NetexFileWriter;
import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class SiteFrameFileWritterTests extends AbstractTestNGSpringContextTests {

    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private List<Line> lines = new ArrayList<Line>();
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;

    @BeforeMethod
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");

        Line line = modelFactory.createModel(Line.class);
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }

//    @Test(groups = {"ServiceFrame"}, dependsOnMethods = {"getBean"}, description = "Export Plugin should have one network")
//    public void verifyNetwork() throws XPathExpressionException {
//        XPathExpression xPathExpression = xPath.compile("//Netex:Network/Name/node()");
//        NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
//
//        assert nodes.getLength() == 1;
//        assert nodes.item(0).getNodeValue().equals("METRO");
//
//    }
   
  
}
