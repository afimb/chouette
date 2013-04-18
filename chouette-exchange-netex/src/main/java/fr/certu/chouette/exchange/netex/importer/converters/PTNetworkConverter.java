package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import java.text.ParseException;
import java.util.Date;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

public class PTNetworkConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(PTNetworkConverter.class);
    private PTNetwork network = new PTNetwork();    
    private AutoPilot pilot;
    private VTDNav nav;
    
    public PTNetworkConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;

        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    
    public PTNetwork convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        int result = -1;
        pilot.selectXPath("//netex:ServiceFrame/netex:Network");
        
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            // Mandatory
            network.setName( (String)parseMandatoryElement(nav, "Name") );
            network.setRegistrationNumber( (String)parseMandatoryElement(nav, "PrivateCode") );
            network.setObjectId( (String)parseMandatoryAttribute(nav, "id") );
                        
            // Optionnal
            network.setDescription( (String)parseOptionnalElement(nav, "Description") );  
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            network.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            network.setVersionDate( (Date)parseOptionnalAttribute(nav, "changed", "Date") );                        
        }
        pilot.resetXPath();
        
        convertResourceFrame();
        
        returnToRootElement(nav);
        return network;
    }
    public void convertResourceFrame() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        nav.push();
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        autoPilot2.selectXPath("//netex:ResourceFrame/netex:dataSources/"+
                "netex:DataSource");
        int result = -1;
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            String sourceIdentifier = (String)parseOptionnalAttribute(nav, "id");
            if ( sourceIdentifier!=null) {
                network.setSourceIdentifier(sourceIdentifier);
            }
            String sourceName = (String)parseOptionnalElement(nav, "Name");
            if ( sourceName!=null) {
                network.setSourceName(sourceName);
            }
            PTNetworkSourceTypeEnum sourceType = null;
            try { 
                sourceType = (PTNetworkSourceTypeEnum)parseOptionnalElement(nav, 
                        "Description", "PTNetworkSourceTypeEnum");
            } catch (Exception e){};
            network.setSourceType(sourceType);
        }
        autoPilot2.resetXPath();
        nav.pop();
    }

    
}
