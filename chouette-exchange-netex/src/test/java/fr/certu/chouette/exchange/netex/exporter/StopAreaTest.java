/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.StopArea;

import java.text.ParseException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

/**
 *
 * @author marc
 */
@Test(groups = {"StopArea"}, description = "Validate StopArea export in NeTEx format")
public class StopAreaTest extends ChouetteModelTest {
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected id")
    public void verifyQuayId() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quay.objectIdPrefix()+
                                ":Quay:"+
                                quay.objectIdSuffix()+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected latitude")
    public void verifyQuayLatitude() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quay.objectIdPrefix()+
                                ":Quay:"+
                                quay.objectIdSuffix()+
                                "']/netex:Centroid/netex:Location/"+
                                "netex:Latitude/"+
                                "text() = '"+
                                quay.getAreaCentroid().getLatitude()+
                                "')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected longitude")
    public void verifyQuayLongitude() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quay.objectIdPrefix()+
                                ":Quay:"+
                                quay.objectIdSuffix()+
                                "']/netex:Centroid/netex:Location/"+
                                "netex:Longitude/"+
                                "text() = '"+
                                quay.getAreaCentroid().getLongitude()+
                                "')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected longlattype")
    public void verifyQuayLongLatType() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quay.objectIdPrefix()+
                                ":Quay:"+
                                quay.objectIdSuffix()+
                                "']/netex:Centroid/netex:Location/"+
                                "gml:pos"+
                                "[@srsName = '"+
                                quay.getAreaCentroid().getLongitude()+
                                "']";
            assertXPathTrue( xPathExpr);
        }
    }
}
