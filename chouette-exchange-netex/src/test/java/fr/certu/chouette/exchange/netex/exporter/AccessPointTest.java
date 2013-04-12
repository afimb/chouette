/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.ConnectionLink;
import java.text.ParseException;

import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"ConnectionLink"}, description = "Validate ConnectionLink export in NeTEx format")
public class AccessPointTest extends ChouetteModelTest {        
    
    public List<ConnectionLink> connectionLinks()
    {
        return line.getConnectionLinks();
    }
    
    @Test(groups = { "ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected id")
    public void verifyConnectionLink() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:ServiceFrame/netex:connections/netex:SiteConnection)", 
                             1);
    }        
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected name")
    public void verifyConnectionLinkName() throws XPathExpressionException, ParseException {
        for (ConnectionLink connectionLink : connectionLinks()) {   
            logger.error(connectionLink.toString("  ", 2));
            assertXPathTrue( "boolean(//netex:SiteConnection[@id = '"+
                        connectionLink.objectIdPrefix() + ":SiteConnection:" + connectionLink.objectIdSuffix() +
                        "']/netex:Name/text()='"+connectionLink.getName()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected startOfLinkId and EndOfLinkId")
    public void verifyConnectionLinkStartAndEnd() throws XPathExpressionException, ParseException {
        for (ConnectionLink connectionLink : connectionLinks()) {            
            assertXPathTrue( "boolean(//netex:SiteConnection[@id = '"+
                        connectionLink.objectIdPrefix() + ":SiteConnection:" + connectionLink.objectIdSuffix() +
                        "']/netex:From/netex:StopPlaceRef/@ref='"+connectionLink.getStartOfLinkId()+"')");
            
            assertXPathTrue( "boolean(//netex:SiteConnection[@id = '"+
                        connectionLink.objectIdPrefix() + ":SiteConnection:" + connectionLink.objectIdSuffix() +
                        "']/netex:To/netex:StopPlaceRef/@ref='"+connectionLink.getEndOfLinkId()+"')");
        }        
        
    }
    
    
}
