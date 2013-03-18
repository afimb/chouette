package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import org.apache.log4j.Logger;

public class RouteConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(RouteConverter.class);
    private Route route = new Route();    
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    
    public RouteConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        autoPilot.selectXPath("//netex:Network");
    }
    
    public Route convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            route.setName(parseMandatoryElement(vTDNav, "Name"));                               
        } 
        
        return route;
    }
    
}
