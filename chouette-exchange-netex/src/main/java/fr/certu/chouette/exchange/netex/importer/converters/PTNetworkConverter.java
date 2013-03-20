package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.PTNetwork;
import org.apache.log4j.Logger;

public class PTNetworkConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(PTNetworkConverter.class);
    private PTNetwork network = new PTNetwork();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public PTNetworkConverter(VTDNav vTDNav, AutoPilot autoPilot) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        pilot = autoPilot;
        autoPilot.selectXPath("//netex:Network");
    }
    
    public PTNetwork convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            network.setName(parseMandatoryElement(nav, "Name"));
            network.setDescription(parseMandatoryElement(nav, "Description"));                                              
        } 
        
        returnToRootElement(nav);
        return network;
    }
    
}
