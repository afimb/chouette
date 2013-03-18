package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.JourneyPattern;
import org.apache.log4j.Logger;

public class JourneyPatternConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(JourneyPatternConverter.class);
    private JourneyPattern journeyPattern = new JourneyPattern();    
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    
    public JourneyPatternConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        autoPilot.selectXPath("//netex:Network");
    }
    
    public JourneyPattern convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            journeyPattern.setName(parseMandatoryElement(vTDNav, "Name"));                                              
        } 
        
        return journeyPattern;
    }
    
}
