/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

public class ConnectionLinkConverter extends GenericConverter
{
    private static final Logger logger = Logger.getLogger(ConnectionLinkConverter.class);
    private List<ConnectionLink> links = new ArrayList<ConnectionLink>();
    private AutoPilot pilot;
    private VTDNav nav;
    
    public ConnectionLinkConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;

        pilot = createAutoPilot(nav);            
    }
    
    public List<ConnectionLink> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        links.clear();
        
        int result = -1;
        pilot.selectXPath("/netex:PublicationDelivery/netex:dataObjects/"+
                "netex:CompositeFrame/netex:frames/" +
                "/netex:ServiceFrame/netex:connections/netex:SiteConnection");
        
        nav.push();
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            ConnectionLink link = new ConnectionLink();
            
            // Mandatory            
            link.setObjectId( (String)parseMandatoryAttribute(nav, "id") );
            
            // Optionnal
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            link.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            link.setName( (String)parseOptionnalElement(nav, "Name") );
            link.setComment( (String)parseOptionnalElement(nav, "Description") );
            link.setLinkDistance( (BigDecimal)parseOptionnalElement(nav, "Distance", "BigDecimal") );
            link.setLinkType( (ConnectionLinkTypeEnum)parseOptionnalElement(nav, "Covered", "ConnectionLinkTypeEnum") );
            link.setDefaultDuration( (Time)parseOptionnalElement(nav, "DefaultDuration", "Duration") );
            link.setFrequentTravellerDuration( (Time)parseOptionnalElement(nav, "FrequentTravellerDuration", "Duration") );
            link.setOccasionalTravellerDuration( (Time)parseOptionnalElement(nav, "OccasionalTravellerDuration", "Duration") );
            link.setMobilityRestrictedTravellerDuration( (Time)parseOptionnalElement(nav, "MobilityRestrictedTravellerDuration", "Duration") );
            
            link.setEndOfLinkId( subXpathSelection( "netex:From/netex:StopPlaceRef/@ref"));
            link.setStartOfLinkId( subXpathSelection( "netex:To/netex:StopPlaceRef/@ref"));
            links.add(link);
        } 
        nav.pop();
              
        pilot.resetXPath();
        returnToRootElement(nav);
        return links;
    }
    
    private String subXpathSelection( String xPath) throws XPathParseException {
        AutoPilot localAutoPilot = createAutoPilot(nav);
        localAutoPilot.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
        localAutoPilot.selectXPath( xPath);
        
        String result = localAutoPilot.evalXPathToString();
        if ( result==null || result.isEmpty())
            result = null;
        
        localAutoPilot.resetXPath();
        return result;
    }
}
    