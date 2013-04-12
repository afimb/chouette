/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class MyTest  extends AbstractTestNGSpringContextTests {
    private VTDNav nav;
    private GenericConverter genericConverter;
    
    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line_test.xml");
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        nav = vg.getNav();
        genericConverter = new GenericConverter();
    }
    
    private void xpathLine() throws XPathParseException, XPathEvalException, NavException, ParseException {
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");

        int result = -1;
        
        autoPilot.selectXPath("//netex:Line");
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {
            // Mandatory
            Assert.assertNotNull( genericConverter.parseMandatoryElement(nav, "PublicCode"));
            Assert.assertNotNull(genericConverter.parseMandatoryElement(nav, "Name") );
            Assert.assertNotNull(genericConverter.parseMandatoryAttribute(nav, "id"));
            
            // Optionnal            
            Assert.assertNotNull(genericConverter.parseOptionnalElement(nav, "TransportMode", "TransportModeNameEnum") );

        }
        //autoPilot.resetXPath();
    }
    private void xpathNetwork() throws XPathParseException, XPathEvalException, NavException, ParseException {
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");

        int result = -1;
        
        autoPilot.selectXPath("//netex:ServiceFrame/netex:Network");
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {
            // Mandatory
            Assert.assertNotNull(genericConverter.parseMandatoryElement(nav, "PrivateCode"));
            Assert.assertNotNull(genericConverter.parseMandatoryElement(nav, "Name") );
            Assert.assertNotNull(genericConverter.parseMandatoryAttribute(nav, "id"));
            Assert.assertNotNull(genericConverter.parseOptionnalElement(nav, "Description") );  
            Assert.assertNotNull(genericConverter.parseOptionnalAttribute(nav, "version", "Integer"));
            

        }
        //autoPilot.resetXPath();
    }
    private void xpathRoute() throws XPathParseException, XPathEvalException, NavException, ParseException {
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");

        autoPilot.selectXPath("//netex:ServiceFrame/netex:routes/netex:Route");
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            Assert.assertNotNull(genericConverter.parseMandatoryElement(nav, "Name") );
            String routobid = (String)genericConverter.parseMandatoryAttribute(nav, "id");
            System.out.println( "routobid="+routobid);
            Assert.assertNotNull(genericConverter.parseMandatoryAttribute(nav, "id") );
            
            Assert.assertNotNull(genericConverter.parseOptionnalCAttribute(nav, "DirectionRef", "ref"));
            
            Assert.assertNotNull(genericConverter.parseOptionnalAttribute(nav, "version", "Integer"));

        }
        //autoPilot.resetXPath();


    }

    private void xpathStopAssignment() throws XPathParseException, XPathEvalException, NavException, ParseException {
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        
        autoPilot.selectXPath("//netex:ServiceFrame/netex:stopAssignments/"+
                "netex:PassengerStopAssignment");
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            String ref = (String)genericConverter.parseMandatoryAttribute(nav, "ScheduledStopPointRef", "ref");
            System.out.println( "ref="+ref);
            
            Assert.assertNotNull(ref);
            Assert.assertNotNull(genericConverter.parseMandatoryAttribute(nav, "QuayRef", "ref"));
        }
        //autoPilot.resetXPath();  
    }
    
    @Test(groups = {"VTD-XML"}, description = "Must return a company")
    public void verifyAPI() throws XPathEvalException, NavException, XPathParseException, ParseException {
        xpathStopAssignment();
        xpathLine();
        xpathNetwork();
        xpathRoute();
        xpathLine();
        xpathNetwork();
        xpathRoute();
    }
    
}
