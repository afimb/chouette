package fr.certu.chouette.exchange.netex;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NetexFileWriterTests extends AbstractTestNGSpringContextTests
{

   private NetexFileWriter netexFileWriter;  
   private ModelFactory modelFactory;
   private List<Line> lines = new ArrayList<Line>();
   private XPath xpath = XPathFactory.newInstance().newXPath();
   private DocumentBuilder builder;

   @Test(groups={"NetexFileWriter"}, description="Get a bean from context")
   public void getBean() throws ParserConfigurationException
   {    
      netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter") ;
      modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");  
      
      Line line = null;
      try {
          line = modelFactory.createModel(Line.class);
      } catch (CreateModelException ex) {
          logger.error(ex);
      }       
      lines.add(line);
      
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); 
      builder = domFactory.newDocumentBuilder();      
   }
   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have a network")
   public void verifyNetexNetwork()
   {       
       netexFileWriter.write(lines, "/tmp/test.xml");         
        try {           
            Document xmlDocument = builder.parse("/tmp/test2.xml");                       
            XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
            XPath xPath = factory.newXPath();
            XPathExpression xPathExpression = xPath.compile("//Direction/*/text()");
            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
                       
            logger.error(nodes.getLength());
            
            for (int i = 0; i < nodes.getLength(); i++) {
                logger.error(nodes.item(i).getNodeValue()); 
            }
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        } catch (SAXException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }

   }

}
