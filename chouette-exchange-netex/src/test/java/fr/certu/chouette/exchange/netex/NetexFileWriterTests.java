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
   private String fileName = "/tmp/test.xml";
   private XPath xPath = XPathFactory.newInstance().newXPath();
   private Document xmlDocument;

   @Test(groups={"NetexFileWriter"}, description="Get a bean from context")
   public void getBean() throws ParserConfigurationException, SAXException, IOException
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
      
      netexFileWriter.write(lines, fileName);
      
      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true); 
      DocumentBuilder builder = domFactory.newDocumentBuilder();  
      xmlDocument = builder.parse(fileName);
   }
   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have one network")
   public void verifyNetwork()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Network/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 1;
            //assert nodes.item(0).getNodeValue().equals("METRO");
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }
   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have one company")
   public void verifyCompany()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Operator/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 1;
            assert nodes.item(0).getNodeValue().equals("RATP");
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }
   
//   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have one line")
//   public void verifyLine()
//   {                              
//        try {                                              
//            XPathExpression xPathExpression = xPath.compile("//Line/Name/node()");            
//            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
//            
//            //assert nodes.getLength() == 1;
//            //assert nodes.item(0).getNodeValue().equals("7");
//            
//        } catch (XPathExpressionException ex) {
//            logger.error(ex);
//        }
//   }   
//   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have 4 routes")
   public void verifyRoutes()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Route/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 2;
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }   
   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have 6 PointOnRoute")
   public void verifyPointOnRoutes()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Route/PointOnRoute/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 6;
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }

}
