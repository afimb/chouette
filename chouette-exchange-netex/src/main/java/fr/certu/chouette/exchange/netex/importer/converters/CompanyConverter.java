package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import java.text.ParseException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

public class CompanyConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(CompanyConverter.class);
    private Company company = new Company();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public CompanyConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;

        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
    }
    
    public Company convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:ResourceFrame/netex:organisations/netex:Operator");
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            // Mandatory
            company.setRegistrationNumber( (String)parseOptionnalElement(nav, "CompanyNumber") );
            company.setName( (String)parseOptionnalElement(nav, "Name"));
            company.setShortName( (String)parseOptionnalElement(nav, "ShortName"));
            company.setCode( (String)parseOptionnalElement(nav, "PublicCode"));
            
            
            convertOrganisationPart();
            convertContactDetails();
            convertDepartment();
            
            company.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            company.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
        } 
              
        pilot.resetXPath();
        returnToRootElement(nav);
        return company;
    }

    private void convertOrganisationPart() throws XPathParseException, NavException, ParseException, XPathEvalException {
        nav.push();
        
        AutoPilot pilot2 = new AutoPilot(nav);
        pilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
        pilot2.selectXPath("netex:parts/netex:OrganisationPart/netex:Name");
        
        company.setOrganisationalUnit( (String)pilot2.evalXPathToString());
        pilot2.resetXPath();
        
        nav.pop();
    }

    private void convertDepartment() throws XPathParseException, NavException, ParseException, XPathEvalException {
        nav.push();
        
        AutoPilot pilot2 = new AutoPilot(nav);
        pilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
        pilot2.selectXPath("netex:departments/netex:Department/netex:Name");
        
        company.setOperatingDepartmentName( (String)pilot2.evalXPathToString());
        
        nav.pop();
    }
    
    


    private void convertContactDetails() throws XPathParseException, NavException, ParseException {
        nav.push();
        
        AutoPilot pilot2 = new AutoPilot(nav);
        pilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
        pilot2.selectXPath("netex:ContactDetails/netex:EmailAddress");
        
        company.setEmail( (String)pilot2.evalXPathToString());
        
        AutoPilot pilot3 = new AutoPilot(nav);
        pilot3.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
        pilot3.selectXPath("netex:ContactDetails/netex:ContactFaxNumber");
        
        company.setFax( (String)pilot3.evalXPathToString());
        
        AutoPilot pilot4 = new AutoPilot(nav);
        pilot4.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");            
        pilot4.selectXPath("netex:ContactDetails/netex:ContactTelephoneNumber");
        
        company.setPhone( (String)pilot4.evalXPathToString());
        pilot2.resetXPath();
        pilot3.resetXPath();
        pilot4.resetXPath();
        
        nav.pop();
    }
    
}
