package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.PTNetwork;
import java.text.ParseException;
import org.apache.log4j.Logger;

public class PTNetworkConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(PTNetworkConverter.class);
    private PTNetwork network = new PTNetwork();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public PTNetworkConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;

        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public PTNetwork convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:Network");
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            
            network.setName(parseMandatoryElement(nav, "Name"));
            network.setRegistrationNumber(parseMandatoryElement(nav, "PrivateCode"));
            network.setObjectId(parseMandatoryAttribute(nav, "id"));
                        
            network.setDescription(parseOptionnalElement(nav, "Description"));    
            network.setObjectVersion( Integer.parseInt(parseOptionnalAttribute(nav, "version")) );
            network.setVersionDate( dateFormat.parse( parseOptionnalAttribute(nav, "changed")) );                        
        } 
        
        returnToRootElement(nav);
        return network;
    }
    
}
