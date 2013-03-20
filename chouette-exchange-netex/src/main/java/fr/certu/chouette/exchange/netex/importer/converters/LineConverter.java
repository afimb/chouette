package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Line;
import org.apache.log4j.Logger;

public class LineConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private Line line = new Line();    
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    
    public LineConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        autoPilot.selectXPath("//netex:Line");
    }
    
    public Line convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {                        
            line.setName(parseMandatoryElement(vTDNav, "Name"));
            
        } 
        
        return line;
    }
    
}
