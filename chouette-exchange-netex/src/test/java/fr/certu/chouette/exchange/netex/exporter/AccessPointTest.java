/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.AccessPoint;
import java.text.ParseException;

import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"AccessPoint"}, description = "Validate AccessPoint export in NeTEx format")
public class AccessPointTest extends ChouetteModelTest {        
    
    public List<AccessPoint> accessPoints()
    {
        return line.getAccessPoints();
    }
    
    @Test(groups = { "ServiceFrame", "accessPoints"}, description = "Validate presence of AccessPoint element with expected id")
    public void verifyAccessPoint() throws XPathExpressionException, ParseException {
        assertXPathCount( "count(//netex:entrances/netex:StopPlaceEntrance)", 
                             1);
        
        for (AccessPoint accessPoint : accessPoints()) {  
            assertXPathCount( "count(//netex:entrances/netex:StopPlaceEntrance[@id = '"+
                    modelTranslator.netexId( accessPoint) + "'])", 1 );
        }
    }        
    
    @Test(groups = {"ServiceFrame", "accessPoints"}, description = "Validate presence of AccessPoint element with expected name")
    public void verifyAccessPointName() throws XPathExpressionException, ParseException {
        for (AccessPoint accessPoint : accessPoints()) {               
            assertXPathTrue( "boolean(//netex:entrances/netex:StopPlaceEntrance[@id = '"+
                        modelTranslator.netexId( accessPoint) +
                        "']/netex:Name/text()='"+accessPoint.getName()+"')");
        }        
    }
    
    @Test(groups = {"ServiceFrame", "accessPoints"}, description = "Validate presence of AccessPoint element with expected coordinates")
    public void verifyAccessPointCoordinates() throws XPathExpressionException, ParseException {
        for (AccessPoint accessPoint : accessPoints()) {                  
            
            assertXPathTrue( "boolean(//netex:StopPlaceEntrance[@id = '"+
                        modelTranslator.netexId( accessPoint) +
                        "']/netex:Centroid/netex:Location/netex:Longitude/text()='"+accessPoint.getLongitude().toPlainString()+"')");
            
            assertXPathTrue( "boolean(//netex:StopPlaceEntrance[@id = '"+
                        modelTranslator.netexId( accessPoint) +
                        "']/netex:Centroid/netex:Location/netex:Latitude/text()='"+accessPoint.getLatitude().toPlainString()+"')");
        }        
        
    }
    
    
}
