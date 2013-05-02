package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import org.apache.log4j.Logger;

public class VehicleJourneyConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(VehicleJourneyConverter.class);
    private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();         
    private AutoPilot autoPilot;
    private VTDNav nav;
    @Getter
    private Map<String, List<String>> timetablesByVehicleJourneyObjectId = new HashMap<String, List<String>>(); 
    
    @Getter
    private Map<String, List<VehicleJourney>> vehicleJourneysByJPObjectId = new HashMap<String, List<VehicleJourney>>();  
    private ModelTranslator modelTranslator = new ModelTranslator();
    
    public VehicleJourneyConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
    }
    private String subXpathSelection( String xPath) throws XPathParseException {
            AutoPilot autoPilot = createAutoPilot(nav);
            autoPilot.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
            autoPilot.selectXPath( xPath);

            String result = autoPilot.evalXPathToString();
            if ( result==null || result.isEmpty())
                    result = null;

            autoPilot.resetXPath();
            return result;
    }
    
    public List<VehicleJourney> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        vehicleJourneys.clear();
        timetablesByVehicleJourneyObjectId.clear();
        vehicleJourneysByJPObjectId.clear();
        
        int result = -1;
        autoPilot.selectXPath("/netex:PublicationDelivery/netex:dataObjects/"+
        "netex:CompositeFrame/netex:frames/" +
        "/netex:TimetableFrame/netex:vehicleJourneys/netex:ServiceJourney");

        nav.push();
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            VehicleJourney vehicleJourney = new VehicleJourney();
            // Mandatory                        
            vehicleJourney.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            vehicleJourney.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            vehicleJourney.setPublishedJourneyName( (String)parseOptionnalElement(nav, "Name") );          
            vehicleJourney.setPublishedJourneyIdentifier( (String)parseOptionnalElement(nav, "ShortName") );            
            vehicleJourney.setServiceStatusValue( modelTranslator.readServiceAlteration( (String)parseOptionnalElement(nav, "ServiceAlteration"))); 
            
            Long number = modelTranslator.readTrainNumberId( subXpathSelection( "netex:trainNumbers/netex:TrainNumberRef/@ref"));
            if ( number!=null)
                vehicleJourney.setNumber( number);
            
            // Route
            vehicleJourney.setRouteId( (String)parseMandatoryAttribute(nav, "RouteRef", "ref") );
            // JourneyPattern
            vehicleJourney.setJourneyPatternId( (String)parseMandatoryAttribute(nav, "ServicePatternRef", "ref") );
            
            // TimeTables   
            List<String> timetablesObjectId =  toStringList( parseMandatoryAttributes(nav, "DayTypeRef", "ref") );
            String vehicleJourneyObjectId = vehicleJourney.getObjectId();
            timetablesByVehicleJourneyObjectId.put(vehicleJourneyObjectId, timetablesObjectId);
                     
            
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
        nav.pop();
              
        autoPilot.resetXPath();
        returnToRootElement(nav);
        return vehicleJourneys;
    }
    
}
