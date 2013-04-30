/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.LinkOrientationEnum;

public class AccessLinkConverter extends GenericConverter {
    private static final Logger logger = Logger.getLogger(ConnectionLinkConverter.class);
    private List<AccessLink> links = new ArrayList<AccessLink>();
    private AutoPilot pilot;
    private VTDNav nav;
    
    @Getter
    private Map<String, List<AccessLink>> accessLinksByStopPlaceObjectId = new HashMap<String, List<AccessLink>>();
    
    public AccessLinkConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        pilot = createAutoPilot(nav);            
    }
    
    public List<AccessLink> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        links.clear();
        accessLinksByStopPlaceObjectId.clear();
        
        int result = -1;
        pilot.selectXPath("/netex:PublicationDelivery/netex:dataObjects/"+
                "netex:CompositeFrame/netex:frames/" +
                "/netex:SiteFrame/netex:pathLinks/netex:PathLink");
        
        nav.push();
        while( (result = pilot.evalXPath()) != -1 )
        {                        
            AccessLink link = new AccessLink();
            
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
            
            String fromEntrance = subXpathSelection( "netex:From/netex:EntranceRef/@ref");
            String toEntrance = subXpathSelection( "netex:To/netex:EntranceRef/@ref");
            if(fromEntrance != null)
            {
                link.setStartOfLinkId(fromEntrance);
                link.setEndOfLinkId( subXpathSelection( "netex:To/netex:PlaceRef/@ref"));
                link.setLinkOrientation(LinkOrientationEnum.ACCESSPOINT_TO_STOPAREA);
            }
            else
            {
                link.setStartOfLinkId(toEntrance);
                link.setEndOfLinkId( subXpathSelection( "netex:From/netex:PlaceRef/@ref"));
                link.setLinkOrientation(LinkOrientationEnum.STOPAREA_TO_ACCESSPOINT);
            }
                        
            String stopAreaObjectId = link.getEndOfLinkId();
            if(stopAreaObjectId != null && 
                    accessLinksByStopPlaceObjectId.containsKey(stopAreaObjectId))
            {
                List<AccessLink> alks = accessLinksByStopPlaceObjectId.get(stopAreaObjectId);
                alks.add(link);
                accessLinksByStopPlaceObjectId.put(stopAreaObjectId, alks);
            }
            else
            {            
                List<AccessLink> alks = new ArrayList<AccessLink>();
                alks.add(link);
                accessLinksByStopPlaceObjectId.put(stopAreaObjectId, alks);
            }                                                       
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
