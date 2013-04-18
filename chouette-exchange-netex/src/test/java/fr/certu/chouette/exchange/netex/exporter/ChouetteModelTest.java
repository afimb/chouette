/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.exchange.netex.NetexNamespaceContext;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.exchange.netex.ModelTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.StopArea;
import java.text.SimpleDateFormat;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.w3c.dom.Document;

/**
 *
 * @author marc
 */
@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class ChouetteModelTest extends AbstractTestNGSpringContextTests {
    protected ModelTranslator modelTranslator = new ModelTranslator();
    protected NetexFileWriter netexFileWriter;
    protected ModelFactory modelFactory;
    protected ComplexModelFactory complexModelFactory;
    protected Line line;
    protected String fileName = "/tmp/test.xml";
    protected XPath xPath = XPathFactory.newInstance().newXPath();
    protected Document xmlDocument;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");
    protected DatatypeFactory durationFactory;

    @BeforeMethod
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
        durationFactory = DatatypeFactory.newInstance();

        netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter");
        
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        
        
        line = complexModelFactory.nominalLine( "1");
        line.complete();
        
        netexFileWriter.writeXmlFile(line, fileName);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        xmlDocument = builder.parse(fileName);
    }    
    
    protected void assertXPathTrue(String xPathExpr) throws XPathExpressionException {       
        Assert.assertTrue( Boolean.parseBoolean( 
                xPath.evaluate( xPathExpr, 
                                xmlDocument)));
    }
    
    protected void assertXPathEquals(String xPathExpr, boolean expected) throws XPathExpressionException {        
        Assert.assertEquals( Boolean.parseBoolean(                 
                xPath.evaluate( xPathExpr, 
                                xmlDocument)), expected);
    }

    protected void assertXPathCount(String xPathExpr, int vehicleCount) throws NumberFormatException, XPathExpressionException {
        Assert.assertEquals( Integer.parseInt( 
                xPath.evaluate( xPathExpr, 
                                xmlDocument)), vehicleCount);
    }
}
