package fr.certu.chouette.exchange.netex.importer.converters;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import javax.xml.datatype.DatatypeConfigurationException;

public class RouteConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(RouteConverter.class);
    private List<Route> routes = new ArrayList<Route>();
    private AutoPilot autoPilot;   
    private Map<String,PTDirectionEnum> directionByRef;
    private Map<String,String> waybackByRef;
    private Map<String,String> commentByObjectId;
    private Map<String,String> numberByObjectId;
    private VTDNav nav;
    private ModelTranslator modelTranslator = new ModelTranslator();
    
    @Getter
    private Map<String,StopPoint> stopPointByObjectId;
        
    public RouteConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        autoPilot = createAutoPilot(nav);
        
        stopPointByObjectId = new HashMap<String, StopPoint>();
        directionByRef = new HashMap<String, PTDirectionEnum>();
        waybackByRef = new HashMap<String, String>();
        commentByObjectId = new HashMap<String, String>();
        numberByObjectId = new HashMap<String, String>();
    }
    
    public List<Route> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        stopPointByObjectId.clear();
        directionByRef.clear();
        waybackByRef.clear();
        commentByObjectId.clear();
        numberByObjectId.clear();
        routes.clear();
        
        autoPilot.selectXPath("//netex:ServiceFrame/netex:routes/netex:Route");
        int result = -1;
        
        nav.push();
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            Route route = new Route();
            
            // Mandatory            
            route.setObjectId( (String)parseMandatoryAttribute(nav, "id") );
            
            route.setName( (String)parseMandatoryElement(nav, "Name") );
            route.setPublishedName( (String)parseOptionnalElement(nav, "ShortName") );
            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            route.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );                        

            route.setWayBackRouteId( subXpathSelection( "netex:InverseRouteRef/@ref"));
            
            // Optionnal Direction and WayBack
            String directionRef = (String)parseOptionnalCAttribute(nav, "DirectionRef", "ref");
            if ( directionRef!=null ) {
                convertDirectionProperties( route, directionRef);
            }

            List<String> pointOnRouteIds = toStringList(parseMandatoryAttributes(nav, "PointOnRoute", "id"));
            
            for( String pointOnRouteId : pointOnRouteIds) {
                StopPoint stopPoint = new StopPoint();
                stopPoint.setPosition( pointOnRouteIds.indexOf( pointOnRouteId));
                stopPoint.setObjectId( stopPointObjectId( route, pointOnRouteId));
                
                stopPointByObjectId.put( stopPoint.getObjectId(), stopPoint);
                route.addStopPoint( stopPoint);
            }
            
            // Optionnal Comment, Number
            convertKeyListProperties(route);
            
            routes.add(route);
        }
        nav.pop();

        convertStopPoints();
        
        return routes;
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

    private void convertDirectionProperties( Route route, String directionObjectId)  throws XPathParseException, NavException, ParseException, XPathEvalException {
        int result = -1;
        
        // lecture des PassengerStopAssignment
        AutoPilot autoPilot2 = createAutoPilot(nav);
        String xPath = "//netex:ServiceFrame/netex:directions/";
        xPath += "netex:Direction[@id='"+directionObjectId+"']";
        autoPilot2.selectXPath(xPath);
        
        nav.push();
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            AutoPilot autoPilot3 = createAutoPilot(nav);
            autoPilot3.selectXPath("netex:Name");
            String directionName = autoPilot3.evalXPathToString();
            autoPilot3.resetXPath();

            if ( directionName!=null) {
                route.setDirection( modelTranslator.readPTDirection( directionName));
            }
            
            AutoPilot autoPilot4 = createAutoPilot(nav);
            autoPilot4.selectXPath("netex:DirectionType");
            String inboundVal = autoPilot4.evalXPathToString();
            autoPilot4.resetXPath();
            
            route.setWayBack( (inboundVal=="outbound") ? "A": "R");
        }
        nav.pop();
        autoPilot2.resetXPath();
    }
    private void convertKeyListProperties( Route route)  throws XPathParseException, NavException, ParseException, XPathEvalException {
        int result = -1;
        
        // lecture des PassengerStopAssignment
        AutoPilot autoPilot2 = createAutoPilot(nav);
        String xPath = "../netex:Route[@id='"+route.getObjectId()+"']/netex:keyList";
        autoPilot2.selectXPath(xPath);
        
        nav.push();
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            AutoPilot autoPilot3 = createAutoPilot(nav);
            autoPilot3.selectXPath("netex:KeyValue/netex:Key[text()='Comment']/../netex:Value");
            route.setComment( autoPilot3.evalXPathToString());
            autoPilot3.resetXPath();

            AutoPilot autoPilot4 = createAutoPilot(nav);
            autoPilot4.selectXPath("netex:KeyValue/netex:Key[text()='Number']/../netex:Value");
            route.setNumber( autoPilot4.evalXPathToString());
            autoPilot4.resetXPath();
        }
        nav.pop();
        autoPilot2.resetXPath();
    }
    
    private void convertStopPoints() throws NavException, XPathParseException, XPathEvalException, ParseException {
        int result = -1;
        
        // lecture des PassengerStopAssignment
        AutoPilot autoPilot2 = createAutoPilot(nav);
        autoPilot2.selectXPath("//netex:ServiceFrame/netex:stopAssignments/"+
                "netex:PassengerStopAssignment");
        nav.push();
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            AutoPilot autoPilot3 = createAutoPilot(nav);
            autoPilot3.selectXPath("netex:ScheduledStopPointRef/@ref");
            String stopPointId = autoPilot3.evalXPathToString();
            autoPilot3.resetXPath();
            
            AutoPilot autoPilot4 = createAutoPilot(nav);
            autoPilot4.selectXPath("netex:QuayRef/@ref");
            String quayId = autoPilot4.evalXPathToString();
            autoPilot4.resetXPath();
            
            StopPoint stopPoint = stopPointByObjectId.get( stopPointId);
            stopPoint.setContainedInStopAreaId( quayId);
        }
        nav.pop();
        autoPilot2.resetXPath();
    }
    
    public String readStopPointObjectIdFromPointOnRouteId( String pointOnRouteId) {
        Matcher m = Pattern.compile( "\\S+:\\S+:(\\S+)-\\d+$").matcher(pointOnRouteId);
        if ( ! m.matches()) {
            throw new RuntimeException( "PointOnRoute.id "+pointOnRouteId);
        }
        return m.group(1);
    }
    
    public String stopPointObjectId(Route route, String pointOnRouteId) {
        return route.objectIdPrefix()+":StopPoint:"+
                readStopPointObjectIdFromPointOnRouteId(pointOnRouteId);
    }
    
}
