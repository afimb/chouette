package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.apache.log4j.Logger;

public class VehicleJourneyConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(VehicleJourneyConverter.class);
    private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();         
    private AutoPilot autoPilot;
    private VTDNav nav;    
    
    @Getter
    private Map<String, List<VehicleJourney>> vehicleJourneysByJPObjectId = new HashMap<String, List<VehicleJourney>>();  
    
    public VehicleJourneyConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
    }
    
    public List<VehicleJourney> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        autoPilot.selectXPath("//netex:vehicleJourneys//netex:ServiceJourney");
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            VehicleJourney vehicleJourney = new VehicleJourney();
            // Mandatory                        
            vehicleJourney.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal            
            vehicleJourney.setName( (String)parseOptionnalElement(nav, "Name") );            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            vehicleJourney.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            // Route
            vehicleJourney.setRouteId( (String)parseMandatoryAttribute(nav, "RouteRef", "ref") );
            // JourneyPattern
            vehicleJourney.setJourneyPatternId( (String)parseMandatoryAttribute(nav, "ServicePatternRef", "ref") );
            
            // TimeTables
            //vehicleJourney.setnull);            
            
            // Link with journeyPattern            
            String journeyPatternObjectId = vehicleJourney.getJourneyPatternId();
            if(vehicleJourneysByJPObjectId.containsKey(journeyPatternObjectId))
            {
                List<VehicleJourney> vjs = vehicleJourneysByJPObjectId.get(journeyPatternObjectId);
                vjs.add(vehicleJourney);
                vehicleJourneysByJPObjectId.put(journeyPatternObjectId, vjs);
            }
            else
            {            
                List<VehicleJourney> vjs = new ArrayList<VehicleJourney>();
                vjs.add(vehicleJourney);
                vehicleJourneysByJPObjectId.put(journeyPatternObjectId, vjs);
            }
                        
            vehicleJourneys.add(vehicleJourney);
        } 
        
        return vehicleJourneys;
    }
    
}
