package fr.certu.chouette.exchange.netex;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Line;
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
import org.xml.sax.SAXException;

@ContextConfiguration(locations={"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class ServiceFrameFileWritterTests extends AbstractTestNGSpringContextTests
{
   private NetexFileWriter netexFileWriter;  
   private ModelFactory modelFactory;
   private List<Line> lines = new ArrayList<Line>();
   private String fileName = "/tmp/test.xml";
   private XPath xPath = XPathFactory.newInstance().newXPath();
   private Document xmlDocument;

   @BeforeMethod
   protected void setUp() throws Exception {
       xPath.setNamespaceContext(new NetexNamespaceContext());
   }   
   
   @Test(groups={"NetexFileWriter"}, invocationCount = 1, description="Get a bean from context")
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
          logger.error(ex);
      } catch (IOException ex) {
          logger.error(ex);
      } catch (ParserConfigurationException ex) {
          logger.error(ex);
      } catch (CreateModelException ex) {
          logger.error(ex);
      }       
   }
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have one network" )
   public void verifyNetwork()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:Network/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            for (int i = 0; i < nodes.getLength(); i++) {
                logger.error(nodes.item(0).getNodeValue());
            }
            assert nodes.getLength() == 1;
            assert nodes.item(0).getNodeValue().equals("METRO");
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have one company")
   public void verifyCompany()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:Operator/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 1;
            assert nodes.item(0).getNodeValue().equals("RATP");
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   } 
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have route points")
   public void verifyRoutePoint()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:routePoints/Netex:RoutePoint");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            assert nodes.getLength() == 6; 
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   } 
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have 4 routes")
   public void verifyRoutes()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:Route/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 2;
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }   
 
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have one line")
   public void verifyLine()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:Line/Name/node()");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            
            assert nodes.getLength() == 1;       
            assert nodes.item(0).getNodeValue().equals("7B");
            
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }      
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have Scheduled StopPoint")
   public void verifyScheduledStopPoint()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:scheduledStopPoints/Netex:ScheduledStopPoint");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);       
            for (int i = 0; i < nodes.getLength(); i++) {                
                logger.error(nodes.item(i).getNodeName());                
            }            
            assert nodes.getLength() == 6;
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }  

      
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have service patterns")
   public void verifyServicePattern()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:servicePatterns/Netex:ServicePattern");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            assert nodes.getLength() == 4;
            
            xPathExpression = xPath.compile("//Netex:pointsInSequence/Netex:PointOnRoute/@id");            
            nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);                      
            assert nodes.getLength() == 6;              
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }  
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have TariffZone")
   public void verifyTariffZone()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:tariffZones/Netex:TariffZone");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);     
            //assert nodes.getLength() == 6;
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }     
   
   @Test (groups = {"ServiceFrame"}, dependsOnMethods={"getBean"}, description = "Export Plugin should have PassengerStopAssignment")
   public void verifyPassengerStopAssignment()
   {                              
        try {                                              
            XPathExpression xPathExpression = xPath.compile("//Netex:stopAssignments/Netex:PassengerStopAssignment");            
            NodeList nodes =  (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);     
            assert nodes.getLength() == 6;
        } catch (XPathExpressionException ex) {
            logger.error(ex);
        }
   }  
   
}
