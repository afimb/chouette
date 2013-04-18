package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import org.apache.log4j.Logger;

public class VehicleJourneyAtStopConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(VehicleJourneyAtStopConverter.class);
    private List<VehicleJourneyAtStop> vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>();    
    private AutoPilot autoPilot;
    private VTDNav nav;    
    
    @Getter
    private Map<String, List<VehicleJourneyAtStop>> vehicleJourneyAtStopsByVJObjectId = new HashMap<String, List<VehicleJourneyAtStop>>();  
    
    public VehicleJourneyAtStopConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
    }
    
    public List<VehicleJourneyAtStop> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        vehicleJourneyAtStops.clear();
        int result = -1;
        autoPilot.selectXPath("/netex:PublicationDelivery/netex:dataObjects/netex:CompositeFrame/netex:frames/netex:TimetableFrame/netex:vehicleJourneys/netex:ServiceJourney/netex:calls/netex:Call");
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {               
            VehicleJourneyAtStop vehicleJourneyAtStop = new VehicleJourneyAtStop();
            // Mandatory             
            vehicleJourneyAtStop.setDepartureTime( (Time)parseMandatorySubElement(nav, "Departure", "Time", "Time") );            
            vehicleJourneyAtStop.setArrivalTime( (Time)parseMandatorySubElement(nav, "Arrival", "Time", "Time") );
            vehicleJourneyAtStop.setStopPointId( (String)parseMandatoryAttribute(nav, "ScheduledStopPointRef", "ref") );
            vehicleJourneyAtStop.setVehicleJourneyId( (String)parseParentAttribute(nav, "id") );
            
            // Link with vehicle journey            
            String vehicleJourneyObjectId = vehicleJourneyAtStop.getVehicleJourneyId();
            if(vehicleJourneyAtStopsByVJObjectId.containsKey(vehicleJourneyObjectId))
            {
                List<VehicleJourneyAtStop> vjas = vehicleJourneyAtStopsByVJObjectId.get(vehicleJourneyObjectId);
                vjas.add(vehicleJourneyAtStop);
                vehicleJourneyAtStopsByVJObjectId.put(vehicleJourneyObjectId, vjas);
            }
            else
            {            
                List<VehicleJourneyAtStop> vjas = new ArrayList<VehicleJourneyAtStop>();
                vjas.add(vehicleJourneyAtStop);
                vehicleJourneyAtStopsByVJObjectId.put(vehicleJourneyObjectId, vjas);
            }
            
            vehicleJourneyAtStops.add(vehicleJourneyAtStop);
        } 
        
        return vehicleJourneyAtStops;
    }
    
}
