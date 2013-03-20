package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Line;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class LineConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private Line line = new Line();    
    private AutoPilot pilot;
    private AutoPilot pilot2;
    private VTDNav nav;
    private List<String> routeObjectIds = new ArrayList<String>();
    
    public LineConverter(VTDNav vTDNav, AutoPilot autoPilot) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        pilot = autoPilot;
        pilot.selectXPath("//netex:Line");
        
        pilot2 = new AutoPilot(nav);
        pilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public Line convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            line.setName(parseMandatoryElement(nav, "Name"));
            
        } 
        
        returnToRootElement(nav);        
        return line;
    }   
    
    public List<String> routeObjectIds() throws XPathParseException, XPathEvalException, NavException
    {
        int result = -1;
        pilot2.selectXPath("//netex:Line//netex:RouteRef");
        
        while( (result = pilot2.evalXPath()) != -1 )
        {               
            String routeObjectId = parseMandatoryAttribute(nav, "ref");
            routeObjectIds.add(routeObjectId);
        }
        
        return routeObjectIds;
    }
    
}
