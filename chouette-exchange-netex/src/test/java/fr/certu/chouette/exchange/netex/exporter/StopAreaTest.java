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
    private String quayObjectId(StopArea quay) {
        return quay.objectIdPrefix()+
                ":Quay:"+
                quay.objectIdSuffix();
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of StopPlace with expected id")
    public void verifyStopPlaceId() throws XPathExpressionException, ParseException {
        
        List<StopArea> topoPoints = line.getStopPlaces();
        
        for( StopArea topoPoint : topoPoints) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace"+
                                "[@id = '"+
                                topoPoint.objectIdPrefix()+
                                ":StopPlace:"+
                                topoPoint.objectIdSuffix()+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of parent StopPlace with expected name")
    public void verifyStopPlaceParentName() throws XPathExpressionException, ParseException {
        
        List<StopArea> topoPoints = line.getStopPlaces();
        
        for( StopArea topoPoint : topoPoints) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace[not(netex:quays/netex:Quay)]/"+
                                "netex:Name/"+
                                "text() = '"+
                                topoPoint.getName()+
                                "')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of expected amount of StopPlace")
    public void verifyStopPlaceCount() throws XPathExpressionException, ParseException {
        String xPathExpr = "count(//netex:SiteFrame/netex:stopPlaces/"+
                            "netex:StopPlace)";
        assertXPathCount( xPathExpr, line.getStopPlaces().size()+line.getCommercialStopPoints().size());
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of StopPlace with expected id")
    public void verifyId() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace"+
                                "[@id = '"+
                                quay.getParent().objectIdPrefix()+
                                ":StopPlace:"+
                                quay.getParent().objectIdSuffix()+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of ParentZoneRef with expected ref")
    public void verifyParentZoneRef() throws XPathExpressionException, ParseException {
        List<StopArea> commercials = line.getCommercialStopPoints();
        
        for( StopArea commercial : commercials) {
            if (commercial.getParent()!=null) {
                String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                    "netex:StopPlace/netex:ParentZoneRef"+
                                    "[@ref = '"+
                                    commercial.getParent().objectIdPrefix()+
                                    ":StopPlace:"+
                                    commercial.getParent().objectIdSuffix()+
                                    "'])";
                assertXPathTrue( xPathExpr);
            }
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected id")
    public void verifyQuayId() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected comment")
    public void verifyQuayComment() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']/netex:Description/text() = '"+
                                quay.getComment()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected name")
    public void verifyQuayName() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']/netex:Name/text() = '"+
                                quay.getName()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected nearestTopicName")
    public void verifyQuayNearestTopicName() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']/netex:LandMark/text() = '"+
                                quay.getNearestTopicName()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected streetName")
    public void verifyQuayStreetName() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']//netex:AddressLine1/text() = '"+
                                quay.getAreaCentroid().getAddress().getStreetName()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected countryCode")
    public void verifyQuayPostCode() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']//netex:PostCode/text() = '"+
                                quay.getAreaCentroid().getAddress().getCountryCode()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Quay with expected registrationNumber")
    public void verifyQuayRegistrationNumber() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']/netex:PrivateCode/text() = '"+
                                quay.getRegistrationNumber()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"SiteFrame", "stopPlaces"}, description = "Validate presence of Location with expected id")
    public void verifyLocation() throws XPathExpressionException, ParseException {
        List<StopArea> quays = line.getQuays();
        quays.addAll( line.getBoardingPositions());
        
        for( StopArea quay : quays) {
            String xPathExpr = "boolean(//netex:SiteFrame/netex:stopPlaces/"+
                                "netex:StopPlace/netex:quays/netex:Quay"+
                                "[@id = '"+
                                quayObjectId(quay)+
                                "']/netex:Centroid/netex:Location"+
                                "[@id = '"+
                                quay.getAreaCentroid().objectIdPrefix()+
                                ":Location:"+
                                quay.getAreaCentroid().objectIdSuffix()+
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
    public void verifyQuayProjectionType() throws XPathExpressionException, ParseException {
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
                                quay.getAreaCentroid().getProjectedPoint().getProjectionType()+
                                "'])";
            assertXPathTrue( xPathExpr);
        }
    }
}
