package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class NeptuneConverter {
    
    private static final Logger       logger = Logger.getLogger(LineConverter.class);
    private AutoPilot autoPilot;
    private VTDNav vTDNav;
    private PTNetworkConverter networkConverter;
    private CompanyConverter companyConverter;
    private LineConverter lineConverter;
    private RouteConverter routeConverter;
    
    public NeptuneConverter(VTDNav nav) throws XPathParseException, XPathEvalException, NavException
    {
        vTDNav = nav;
        networkConverter = new PTNetworkConverter(vTDNav);
        companyConverter = new CompanyConverter(vTDNav);
        lineConverter = new LineConverter(vTDNav);
        routeConverter = new RouteConverter(vTDNav);
    }
    
    public Line convert() throws XPathParseException, XPathEvalException, NavException, ParseException
    {
        PTNetwork network = networkConverter.convert();        
        Company company = companyConverter.convert();                
        Line line = lineConverter.convert();       
        List<Route> routes = routeConverter.convert();
        
        // Link between objects
        line.setPtNetwork(network);                
        line.setCompany(company);
        
        for (Route route : routes) {
            line.addRoute(route);
        }
                
        //complete
        line.complete();
        
        return line;
    }
    
}
