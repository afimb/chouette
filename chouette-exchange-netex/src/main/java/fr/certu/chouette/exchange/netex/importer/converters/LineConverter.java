package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.apache.log4j.Logger;

public class LineConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private Line line = new Line();    
    private AutoPilot pilot;
    private AutoPilot pilot2;
    private VTDNav nav;

    @Getter
    private List<String> routeObjectIds = new ArrayList<String>();
    
    public LineConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        
        pilot2 = new AutoPilot(nav);
        pilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public Line convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:Line");
        
        while( (result = pilot.evalXPath()) != -1 )
        {
            // Mandatory
            line.setRegistrationNumber( (String)parseMandatoryElement(nav, "PublicCode") );
            line.setName( (String)parseMandatoryElement(nav, "Name") );
            line.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal            
            line.setTransportModeName( (TransportModeNameEnum)parseOptionnalElement(nav, "TransportMode", "TransportModeNameEnum") );
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            line.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            // Routes
            routeObjectIds = toStringList(parseMandatoryAttributes(nav, "RouteRef", "ref"));            
        }
        
        returnToRootElement(nav);        
        return line;
    }      
    
}
