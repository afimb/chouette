/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.model.neptune.Timetable;
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
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected publishedJourneyName")
    public void verifyPublishedJourneyName() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:Name/"+
                                "text() = '"+vehicleJourney.getPublishedJourneyName()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected publishedJourneyIdentifier")
    public void verifyPublishedJourneyIdentifier() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:ShortName/"+
                                "text() = '"+vehicleJourney.getPublishedJourneyIdentifier()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected description")
    public void verifyDescription() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:Description/"+
                                "text() = '"+vehicleJourney.getComment()+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected serviceStatusValue")
    public void verifyServiceStatusValue() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:ServiceAlteration/"+
                                "text() = '"+modelTranslator.toServiceAlteration(vehicleJourney.getServiceStatusValue())+"')";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected routeRef")
    public void verifyRouteRef() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:RouteRef[ @ref = '"+modelTranslator.netexId(vehicleJourney.getRoute()) +"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected servicePatternRef")
    public void verifyServicePatternRef() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:ServicePatternRef[ @ref = '"+modelTranslator.netexId(vehicleJourney.getJourneyPattern()) +"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected operatorId")
    public void verifyOperatorRef() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:OperatorRef[ @ref = '"+modelTranslator.netexId(vehicleJourney.getCompany()) +"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of ServiceJourney with expected trainNumber")
    public void verifyTrainNumberRef() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                "netex:ServiceJourney"+
                                "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                "netex:trainNumbers/" +
                                "netex:TrainNumberRef[ @ref = '"+modelTranslator.trainNumberId(vehicleJourney.getNumber()) +"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "vehicleJourneys"}, description = "Validate presence of trainNumbers with expected trainNumber")
    public void verifyTrainNumbers() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
           String xPathExpr = "boolean(//netex:TimetableFrame/"+
                                "netex:trainNumbers/" +
                                "netex:TrainNumber[ @id = '"+modelTranslator.trainNumberId(vehicleJourney.getNumber()) +"'])";
            assertXPathTrue( xPathExpr);
        }
    }
    
    @Test(groups = {"TimetableFrame", "dayTypes"}, description = "Validate presence of ServiceJourney with expected DayTypeRef")
    public void verifyDayTypeRef() throws XPathExpressionException, ParseException {
        
        List<VehicleJourney> vehicleJourneys = line.getVehicleJourneys();
        
        for( VehicleJourney vehicleJourney : vehicleJourneys) {
            for ( Timetable timetable : vehicleJourney.getTimetables()) {
               String xPathExpr = "boolean(//netex:TimetableFrame/netex:vehicleJourneys/"+
                                    "netex:ServiceJourney"+
                                    "[@id = '"+modelTranslator.netexId(vehicleJourney)+"']/" +
                                    "netex:dayTypes/"+
                                    "netex:DayTypeRef[ @ref = '"+modelTranslator.netexId(timetable) +"'])";
               assertXPathTrue( xPathExpr);
            }
        }
    }
    
}
