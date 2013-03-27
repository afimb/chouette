package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
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
    private VTDNav nav;
    
    public RouteConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        stopPointByObjectId = new HashMap<String, StopPoint>();
    }
    
    public List<Route> convert() throws XPathEvalException, NavException, XPathParseException
    {
        autoPilot.selectXPath("//netex:routes//netex:Route");
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            Route route = new Route();
            // Mandatory            
            route.setName(parseMandatoryElement(nav, "Name"));
            route.setObjectId(parseMandatoryAttribute(nav, "id"));
            
            // Optionnal
            String a = parseOptionnalAttribute(nav, "DirectionRef", "ref");
            route.setPublishedName(a);
            route.setObjectVersion(Integer.parseInt(parseOptionnalAttribute(nav, "version")));
            

            List<String> pointOnRouteIds = parseMandatoryAttributes(nav, "PointOnRoute", "id");
            
            for( String pointOnRouteId : pointOnRouteIds) {
                StopPoint stopPoint = new StopPoint();
                stopPoint.setPosition( pointOnRouteIds.indexOf( pointOnRouteId));
                stopPoint.setObjectId( stopPointObjectId( route, pointOnRouteId));
                
                stopPointByObjectId.put( stopPoint.getObjectId(), stopPoint);
                route.addStopPoint( stopPoint);
            }
            
            routes.add(route);
        } 
        
        // lecture des PassengerStopAssignment
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");   
        autoPilot2.selectXPath("//netex:ServiceFrame/netex:stopAssignments/"+
                "netex:PassengerStopAssignment");
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            String stopPointId = parseOptionnalAttribute(nav, "ScheduledStopPointRef", "ref");
            String quayId = parseOptionnalAttribute(nav, "QuayRef", "ref");
            
            StopPoint stopPoint = stopPointByObjectId.get( stopPointId);
            stopPoint.setContainedInStopAreaId( quayId);
        }
        
        
        return routes;
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
