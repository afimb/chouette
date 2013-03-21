package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class RouteConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(RouteConverter.class);
    private List<Route> routes = new ArrayList<Route>();    
    private AutoPilot autoPilot;
    private VTDNav nav;
    
    public RouteConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
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
            route.setPublishedName(parseOptionnalAttribute(nav, "DirectionRef", "ref"));
            route.setObjectVersion(Integer.parseInt(parseOptionnalAttribute(nav, "version")));
            
            routes.add(route);
        } 
        
        return routes;
    }
    
}
