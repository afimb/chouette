package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import java.text.ParseException;
import org.apache.log4j.Logger;

public class CompanyConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(CompanyConverter.class);
    private Company company = new Company();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public CompanyConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;

        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
    }
    
    public Company convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:Operator");
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            // Mandatory
            company.setRegistrationNumber( (String)parseMandatoryElement(nav, "CompanyNumber") );
            company.setName( (String)parseMandatoryElement(nav, "Name"));
            company.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            company.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );                                        
        } 
              
        pilot.resetXPath();
        returnToRootElement(nav);
        return company;
    }
    
}
