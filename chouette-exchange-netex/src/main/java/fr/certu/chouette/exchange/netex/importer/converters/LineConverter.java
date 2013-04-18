package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import org.apache.log4j.Logger;

public class LineConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private Line line = new Line();    
    private AutoPilot pilot;
    private VTDNav nav;
    private ModelTranslator modelTranslator = new ModelTranslator();

    @Getter
    private List<String> routeObjectIds = new ArrayList<String>();
    
    public LineConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public Line convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:Line");
        
        while( (result = pilot.evalXPath()) != -1 )
        {
            // Mandatory
            line.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal
            line.setName( (String)parseOptionnalElement(nav, "Name") );
            line.setPublishedName( (String)parseOptionnalElement(nav, "ShortName") );
            line.setRegistrationNumber( (String)parseOptionnalElement(nav, "PrivateCode") );
            line.setNumber( (String)parseOptionnalElement(nav, "PublicCode") );
            line.setComment( (String)parseOptionnalElement(nav, "Description") );
            
            // Optionnal            
            line.setTransportModeName( modelTranslator.readTransportMode((String)parseOptionnalElement(nav, "TransportMode")));
            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            line.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            // Routes
            routeObjectIds = toStringList(parseMandatoryAttributes(nav, "RouteRef", "ref"));            
        }
        pilot.resetXPath();
        
        returnToRootElement(nav);        
        return line;
    }      
    
}
