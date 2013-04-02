package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import java.lang.String;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class RouteConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(RouteConverter.class);
    private List<Route> routes = new ArrayList<Route>();    
    private AutoPilot autoPilot;
    private Map<String,StopPoint> stopPointByObjectId;
    private Map<String,PTDirectionEnum> directionByRef;
    private Map<String,String> waybackByRef;
    private Map<String,String> commentByObjectId;
    private Map<String,String> numberByObjectId;
    private VTDNav nav;
    
    public RouteConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        stopPointByObjectId = new HashMap<String, StopPoint>();
        directionByRef = new HashMap<String, PTDirectionEnum>();
        waybackByRef = new HashMap<String, String>();
        commentByObjectId = new HashMap<String, String>();
        numberByObjectId = new HashMap<String, String>();
    }
    
    public void convertDirections() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        autoPilot2.selectXPath("//netex:ServiceFrame/netex:directions/"+
                "netex:Direction");
        int result = -1;
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            //List<Object> params = new ArrayList<Object>();
            //params.add("PTDirectionEnum");
            PTDirectionEnum direction = (PTDirectionEnum)parseOptionnalElement(nav, "Name", "PTDirectionEnum");
            String ref = (String)parseOptionnalAttribute(nav, "id");
            directionByRef.put(ref, direction);
            
            String wayback = (String)parseOptionnalElement(nav, "DirectionType");
            waybackByRef.put(ref, (wayback=="inbound") ? "A": "R");
        }
    }
    
    public void convertKeyValues( String routeObjectId) throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        autoPilot2.selectXPath("//netex:ServiceFrame/netex:routes/"+
                "netex:Route[@id = '"+routeObjectId+"']/netex:keyList/"+
                "netex:KeyValue");
        int result = -1;
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            List<String> keys = toStringList( parseMandatoryElements(nav, "Key"));
            List<String> values = toStringList( parseMandatoryElements(nav, "Value"));
            
            for(int i=0; i<keys.size(); i++) {
                if ( keys.get(i).equals( "Comment")) {
                    commentByObjectId.put(routeObjectId, values.get(i));
                } else if ( keys.get(i).equals( "Number")) {
                    numberByObjectId.put(routeObjectId, values.get(i));
                }
            }
        }
    }
    
    public List<Route> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        stopPointByObjectId.clear();
        directionByRef.clear();
        waybackByRef.clear();
        commentByObjectId.clear();
        numberByObjectId.clear();
        routes.clear();
        
        convertDirections();
        
        autoPilot.selectXPath("//netex:ServiceFrame/netex:routes/netex:Route");
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            Route route = new Route();
            // Mandatory            
            route.setName( (String)parseMandatoryElement(nav, "Name") );
            route.setObjectId( (String)parseMandatoryAttribute(nav, "id") );
            
            route.setPublishedName( (String)parseOptionnalElement(nav, "ShortName") );
            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            route.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );                        

            String directionRef = (String)parseOptionnalCAttribute(nav, "DirectionRef", "ref");
            if ( directionRef!=null && directionByRef.containsKey( directionRef)) {
                route.setDirection( directionByRef.get( directionRef));
            }
            if ( directionRef!=null && waybackByRef.containsKey( directionRef)) {
                route.setWayBack( waybackByRef.get( directionRef));
            }
            
            String inverseRouteRef = (String)parseOptionnalAttribute(nav, "InverseRouteRef", "ref");
            if ( inverseRouteRef!=null) {
                route.setWayBackRouteId(inverseRouteRef);
            }
            
            List<String> pointOnRouteIds = toStringList(parseMandatoryAttributes(nav, "PointOnRoute", "id"));
            
            for( String pointOnRouteId : pointOnRouteIds) {
                StopPoint stopPoint = new StopPoint();
                stopPoint.setPosition( pointOnRouteIds.indexOf( pointOnRouteId));
                stopPoint.setObjectId( stopPointObjectId( route, pointOnRouteId));
                
                stopPointByObjectId.put( stopPoint.getObjectId(), stopPoint);
                route.addStopPoint( stopPoint);
            }
            
            // Optionnal
            convertKeyValues( route.getObjectId());
            if ( commentByObjectId.containsKey( route.getObjectId())) {
                route.setComment( commentByObjectId.get( route.getObjectId()));
            }
            if ( numberByObjectId.containsKey( route.getObjectId())) {
                route.setNumber( numberByObjectId.get( route.getObjectId()));
            }
            
            routes.add(route);
        }

        convertStopPoints();
        
        return routes;
    }

    private void convertStopPoints() throws NavException, XPathParseException, XPathEvalException, ParseException {
        int result = -1;
        
        // lecture des PassengerStopAssignment
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");   
        autoPilot2.selectXPath("//netex:ServiceFrame/netex:stopAssignments/"+
                "netex:PassengerStopAssignment");
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            String stopPointId = (String)parseMandatoryAttribute(nav, "ScheduledStopPointRef", "ref");
            String quayId = (String)parseMandatoryAttribute(nav, "QuayRef", "ref");
            
            StopPoint stopPoint = stopPointByObjectId.get( stopPointId);
            stopPoint.setContainedInStopAreaId( quayId);
        }
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
