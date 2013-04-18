package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.JourneyPattern;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import org.apache.log4j.Logger;

public class JourneyPatternConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(JourneyPatternConverter.class);
    private List<JourneyPattern> journeyPatterns = new ArrayList<JourneyPattern>();    
    private AutoPilot autoPilot;
    private VTDNav nav;   
    
    @Getter
    private Map<String, List<JourneyPattern>> journeyPatternByRouteObjectId = new HashMap<String, List<JourneyPattern>>();         
    
    public JourneyPatternConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
    }
    
    public List<JourneyPattern> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        journeyPatterns.clear();
        int result = -1;
        autoPilot.selectXPath("//netex:servicePatterns//netex:ServicePattern");
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            JourneyPattern journeyPattern = new JourneyPattern();
            // Mandatory                        
            journeyPattern.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal            
            journeyPattern.setRegistrationNumber( (String)parseOptionnalElement(nav, "PrivateCode") );
            journeyPattern.setName( (String)parseOptionnalElement(nav, "Name") );
            journeyPattern.setPublishedName( (String)parseOptionnalElement(nav, "ShortName") );
            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            journeyPattern.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            // Route
            journeyPattern.setRouteId( (String)parseMandatoryAttribute(nav, "RouteRef", "ref") );
            
            // StopPoints
            journeyPattern.setStopPointIds( toStringList(parseMandatoryAttributes(nav, "ScheduledStopPointRef", "ref")) );
            
            // Link with route           
            String routeObjectId = journeyPattern.getRouteId();
            if(journeyPatternByRouteObjectId.containsKey(routeObjectId))
            {
                List<JourneyPattern> jps = journeyPatternByRouteObjectId.get(routeObjectId);
                jps.add(journeyPattern);
                journeyPatternByRouteObjectId.put(routeObjectId, jps);
            }
            else
            {            
                List<JourneyPattern> jps = new ArrayList<JourneyPattern>();
                jps.add(journeyPattern);
                journeyPatternByRouteObjectId.put(routeObjectId, jps);
            }
            
            journeyPatterns.add(journeyPattern);
        } 
        
        return journeyPatterns;
    }
    
}
