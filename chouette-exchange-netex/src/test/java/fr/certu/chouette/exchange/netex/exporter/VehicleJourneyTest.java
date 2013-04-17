/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.VehicleJourney;

import java.text.ParseException;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.testng.annotations.Test;

@Test(groups = {"VehicleJourney"}, description = "Validate VehicleJourney export in NeTEx format")
public class VehicleJourneyTest extends ChouetteModelTest {
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected id")
    public void verifyId() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
            String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
}
