package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class NeptuneConverter {
    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    private PTNetworkConverter networkConverter;
    private CompanyConverter companyConverter;
    private LineConverter lineConverter;
    private RouteConverter routeConverter;
    private JourneyPatternConverter journeyPatternConverter;
    private VehicleJourneyConverter vehicleJourneyConverter;
    private VehicleJourneyAtStopConverter vehicleJourneyAtStopConverter;
    
    public NeptuneConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        networkConverter = new PTNetworkConverter(vTDNav);
        companyConverter = new CompanyConverter(vTDNav);
        lineConverter = new LineConverter(vTDNav);
        routeConverter = new RouteConverter(vTDNav);
        journeyPatternConverter = new JourneyPatternConverter(vTDNav);
        vehicleJourneyConverter = new VehicleJourneyConverter(vTDNav);
        vehicleJourneyAtStopConverter = new VehicleJourneyAtStopConverter(vTDNav);
    }
    
    public Line convert() throws XPathParseException, XPathEvalException, NavException, ParseException
    {
        PTNetwork network = networkConverter.convert();        
        Company company = companyConverter.convert();                
        Line line = lineConverter.convert();       
        List<Route> routes = routeConverter.convert();
        List<JourneyPattern> journeyPatterns = journeyPatternConverter.convert();
        List<VehicleJourney> vehicleJourneys = vehicleJourneyConverter.convert();
        List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourneyAtStopConverter.convert();
        
        // Ids
        Map<String,StopPoint> stopPointByObjectId = routeConverter.getStopPointByObjectId();
        
        // Link line with network and company
        line.setPtNetwork(network);                
        line.setCompany(company);                
        
        // Link route with journey patterns        
        for (Route route : routes) {
            // Hack perhaps to do before
            line.addRoute(route);            
            
            for (JourneyPattern journeyPattern : journeyPatterns) {
                if(journeyPattern.getRouteId().equals(route.getObjectId()))
                    route.addJourneyPattern(journeyPattern);
            }
        }
        
        Map<String, List<VehicleJourney>> vehicleJourneysByJPObjectId = vehicleJourneyConverter.getVehicleJourneysByJPObjectId();
        logger.error(vehicleJourneysByJPObjectId.toString());
        // Link journey pattern with stop points and vehicle journeys
        for (JourneyPattern journeyPattern : journeyPatterns) {
            for (String stopPointId : journeyPattern.getStopPointIds()) {
                if (stopPointByObjectId.get(stopPointId) != null)
                    journeyPattern.addStopPoint(stopPointByObjectId.get(stopPointId));
            }
            
            journeyPattern.setVehicleJourneys( vehicleJourneysByJPObjectId.get(journeyPattern.getObjectId()) );
        }
                                   
        Map<String, List<VehicleJourneyAtStop>> vehicleJourneyAtStopsByVJObjectId = vehicleJourneyAtStopConverter.getVehicleJourneyAtStopsByVJObjectId();
        // Link vehicle journeys with vehicle journey at stop
        for (VehicleJourney vehicleJourney : vehicleJourneys) {
            vehicleJourney.setVehicleJourneyAtStops( vehicleJourneyAtStopsByVJObjectId.get(vehicleJourney.getObjectId()) );
        }
                                
        //complete
        line.complete();
        
        return line;
    }
    
}
