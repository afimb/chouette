package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import org.apache.log4j.Logger;

public class NeptuneConverter {
    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    
    public NeptuneConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public Line convert() throws XPathParseException, XPathEvalException, NavException
    {
        PTNetworkConverter networkConverter = new PTNetworkConverter(vTDNav, autoPilot);
        PTNetwork network = networkConverter.convert();
        
        CompanyConverter companyConverter = new CompanyConverter(vTDNav, autoPilot);
        Company company = companyConverter.convert();
        
        LineConverter lineConverter = new LineConverter(vTDNav, autoPilot);
        Line line = lineConverter.convert();
        lineConverter.routeObjectIds();
        
        // Link between objects
        line.setPtNetwork(network);
        line.setCompany(company);
        
        
        return line;
    }
    
}
