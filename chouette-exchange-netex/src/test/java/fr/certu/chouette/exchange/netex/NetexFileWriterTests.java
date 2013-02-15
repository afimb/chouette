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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
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
   
    private static class NetexNamespaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if ("netex".equals(prefix)) {
                return "http://www.netex.org.uk/netex";
            } else if ("acsb".equals(prefix)) {
                return "http://www.ifopt.org.uk/acsb";
            } else if ("ifopt".equals(prefix)) {
                return "http://www.ifopt.org.uk/ifopt";
            } else if ("gml".equals(prefix)) {
                return "http://www.opengis.net/gml/3.2";
            } else if ("siri".equals(prefix)) {
                return "http://www.siri.org.uk/siri";
            }
            return null;
        }

        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    }
    @BeforeMethod
    protected void setUp() throws Exception {
        xPath.setNamespaceContext(new NetexNamespaceContext());
    }

   @Test(groups={"NetexFileWriter"}, description="Get a bean from context")
   public void getBean() 
   {    
      netexFileWriter = (NetexFileWriter) applicationContext.getBean("netexFileWriter") ;
      modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");  
      
      Line line = null;
      try {
          line = modelFactory.createModel(Line.class);          
          lines.add(line);          
          netexFileWriter.write(lines, fileName);
          
          DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
          domFactory.setNamespaceAware(true); 
          DocumentBuilder builder = domFactory.newDocumentBuilder();  
          xmlDocument = builder.parse(fileName);
      } catch (SAXException ex) {
            Logger.getLogger(NetexFileWriterTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetexFileWriterTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(NetexFileWriterTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CreateModelException ex) {
          logger.error(ex);
      }      
   }
   
   @Test (groups = {"NetexFileWriter"}, description = "Export Plugin should have one network")
   public void verifyNetwork()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//netex:Network/netex:Name/node()");   
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
            XPathExpression xPathExpression = xPath.compile("//netex:Operator/netex:Name/node()");            
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
            XPathExpression xPathExpression = xPath.compile("//netex:Route/netex:Name/node()");            
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
            XPathExpression xPathExpression = xPath.compile("//netex:Route/netex:PointOnRoute/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 6;
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }

}
