package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import org.apache.log4j.Logger;

public class CompanyConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(CompanyConverter.class);
    private Company company = new Company();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public CompanyConverter(VTDNav vTDNav, AutoPilot autoPilot) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        pilot = autoPilot;
        pilot.selectXPath("//netex:Network");
    }
    
    public Company convert() throws XPathEvalException, NavException
    {
        int result = -1;
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            company.setName(parseMandatoryElement(nav, "Name"));                                         
        } 
                
        returnToRootElement(nav);
        return company;
    }
    
}
