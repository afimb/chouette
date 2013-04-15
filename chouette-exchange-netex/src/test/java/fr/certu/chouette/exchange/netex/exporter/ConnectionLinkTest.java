/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.EnumTranslator;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import java.text.ParseException;

import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"ConnectionLink"}, description = "Validate ConnectionLink export in NeTEx format")
public class ConnectionLinkTest extends ChouetteModelTest {    
    private String xPathRoot = "/netex:PublicationDelivery/netex:dataObjects/"+
                "netex:CompositeFrame/netex:frames/" +
                "/netex:ServiceFrame/netex:connections";
    private EnumTranslator enumTranslator = new EnumTranslator();
    
    private String getId( ConnectionLink connectionLink) {
        return connectionLink.objectIdPrefix() + ":SiteConnection:" + 
                connectionLink.objectIdSuffix();
    }
    
    @Test(groups = { "ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected id")
    public void verifyConnectionLink() throws XPathExpressionException, ParseException {
        assertXPathCount( "count("+xPathRoot+"/netex:SiteConnection)", 1);
    }        
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected id")
    public void verifyId() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                                    "[@id = '"+ getId(connectionLink) + "'])");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected name")
    public void verifyName() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:Name/text()='"+connectionLink.getName()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected comment")
    public void verifyComment() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:Description/text()='"+connectionLink.getComment()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected linkDistance")
    public void verifyLinkDistance() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:Distance/text()='"+connectionLink.getLinkDistance()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected linkType")
    public void verifyLinkType() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:navigationPaths/netex:NavigationPath"+
                    "/netex:Covered/text()='"+ enumTranslator.toLinkType(connectionLink.getLinkType()) +"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected DefaultDuration")
    public void verifyDefaultDuration() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:navigationPaths/netex:NavigationPath"+
                    "/netex:TransfertDuration" +
                    "/netex:DefaultDuration/text()='"+ connectionLink.getDefaultDuration() +"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected FrequentTravellerDuration")
    public void verifyFrequentTravellerDuration() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {   
            assertXPathTrue( "boolean("+xPathRoot+"/netex:SiteConnection"+
                    "[@id = '"+ getId(connectionLink) + "']"+
                    "/netex:navigationPaths/netex:NavigationPath"+
                    "/netex:TransfertDuration" +
                    "/netex:FrequentTravellerDuration/text()='"+ connectionLink.getFrequentTravellerDuration() +"')");
        }        
    }
    
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected startOfLinkId and EndOfLinkId")
    public void verifyConnectionLinkStartAndEnd() throws XPathExpressionException, ParseException {
        List<ConnectionLink> connectionLinks = line.getConnectionLinks();
        
        for (ConnectionLink connectionLink : connectionLinks) {            
            assertXPathTrue( "boolean(//netex:SiteConnection[@id = '"+
                        connectionLink.objectIdPrefix() + ":SiteConnection:" + connectionLink.objectIdSuffix() +
                        "']/netex:From/netex:StopPlaceRef/@ref='"+connectionLink.getStartOfLinkId()+"')");
            
            assertXPathTrue( "boolean(//netex:SiteConnection[@id = '"+
                        connectionLink.objectIdPrefix() + ":SiteConnection:" + connectionLink.objectIdSuffix() +
                        "']/netex:To/netex:StopPlaceRef/@ref='"+connectionLink.getEndOfLinkId()+"')");
        }        
        
    }
    
    
}
