package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.model.neptune.Company;
import java.text.ParseException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.Line;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class CompositeFrameFileWritterTests extends AbstractTestNGSpringContextTests
{
    private NetexFileWriter netexFileWriter;
    private ModelFactory modelFactory;
    private Line line;
    private String fileName = "/tmp/test.xml";
    private XPath xPath = XPathFactory.newInstance().newXPath();
    private Document xmlDocument;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    @BeforeClass
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());

        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        line = modelFactory.createModel(Line.class);
        line.complete();
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }    
    
    @Test(groups = {"CompositeFrame"}, description = "Validate CompositeFrame build")
    public void verifyCompositeFrame() throws XPathExpressionException, ParseException {
        
        assert xPath.evaluate("//netex:CompositeFrame/@created", xmlDocument).equals(dateFormat.format(line.getPtNetwork().getVersionDate()));
        assert xPath.evaluate("//netex:Codespace/@id", xmlDocument).equals(line.objectIdPrefix());
        assert xPath.evaluate("//netex:DefaultCodespaceRef/@ref", xmlDocument).equals(line.objectIdPrefix());        
        assert xPath.evaluate("//netex:TypeOfFrame/netex:Description/node()", xmlDocument).equals(line.objectIdPrefix() + "--Neptune-Line-xxxxxx  frames will be composite frame containing Service, Timetable (and Service Calendar) and Resource frames with all the usual NEPTUNE attributes filled in, for a given line xxxxx.");
    }
    
    @Test(groups = {"CompositeFrame"}, description = "Validate Organisation build")
    public void verifyOperator() throws XPathExpressionException, ParseException {
        Company company = line.getCompany();
        
        assert Integer.parseInt(xPath.evaluate("//netex:Operator/@version", xmlDocument)) == company.getObjectVersion();                        
        assert xPath.evaluate("//netex:Operator/@id", xmlDocument).equals(company.objectIdPrefix() + ":Company:" + company.objectIdSuffix());        
        assert xPath.evaluate("//netex:Operator/netex:Name", xmlDocument).equals(company.getName());
        assert xPath.evaluate("//netex:Operator/netex:CompanyNumber", xmlDocument).equals(company.getRegistrationNumber());
       
    }
}
