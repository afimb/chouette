/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;
import java.text.ParseException;

import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"AccessLink"}, description = "Validate AccessLink export in NeTEx format")
public class AccessLinkTest extends ChouetteModelTest {   
    
    public List<AccessLink> accessLinks()
    {
        return line.getAccessLinks();
    }
    
    @Test(groups = { "ServiceFrame", "accessLinks"}, description = "Validate presence of AccessLink element with expected id")
    public void verifyAccessLink() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:pathLinks/netex:PathLink)", 1);
    }        
    
    @Test(groups = {"ServiceFrame", "accessLinks"}, description = "Validate presence of accessLink element with expected name")
    public void verifyaccessLinkName() throws XPathExpressionException, ParseException {
        for (AccessLink accessLink : accessLinks()) {   
            assertXPathTrue( "boolean(//netex:PathLink[@id = '"+
                        accessLink.objectIdPrefix() + ":PathLink:" + accessLink.objectIdSuffix() +
                        "']/netex:Name/text()='"+accessLink.getName()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "accessLinks"}, description = "Validate presence of StopPlaceEntranceRef")
    public void verifyaccessLinkStartAndEnd() throws XPathExpressionException, ParseException {
        for (AccessLink accessLink : accessLinks()) { 
            NeptuneIdentifiedObject startLink = null;
            NeptuneIdentifiedObject endLink = null;
            if ( accessLink.getLinkOrientation().equals( LinkOrientationEnum.ACCESSPOINT_TO_STOPAREA)) {
                startLink = accessLink.getAccessPoint();
                endLink = accessLink.getStopArea();
            } else {
                startLink = accessLink.getStopArea();
                endLink = accessLink.getAccessPoint();
            }
            String xPathExpr = "boolean(//netex:PathLink"+
                "[@id = '"+ modelTranslator.netexId(accessLink) + "']" +
                "/netex:To/netex:PlaceRef/@ref='"+modelTranslator.netexId(startLink)+"')";
            System.out.println("xPathExpr="+xPathExpr);
            assertXPathTrue( "boolean(//netex:PathLink"+
                "[@id = '"+ modelTranslator.netexId(accessLink) + "']" +
                "/netex:To/netex:PlaceRef/@ref='"+modelTranslator.netexId(endLink)+"')");           
            
            assertXPathTrue( "boolean(//netex:PathLink"+
                "[@id = '"+ modelTranslator.netexId(accessLink) + "']" +
                "/netex:From/netex:EntranceRef/@ref='"+modelTranslator.netexId(startLink) +"')");           
        }        
        
    }
    
    
}
