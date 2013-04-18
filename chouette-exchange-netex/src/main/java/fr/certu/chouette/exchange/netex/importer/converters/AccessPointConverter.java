package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.type.AccessPointTypeEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
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

public class AccessPointConverter extends GenericConverter 
{    
    private static final Logger       logger = Logger.getLogger(AccessPointConverter.class);
    private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();    
    private AutoPilot pilot;
    private VTDNav nav;

    @Getter
    private Map<String, AccessPoint> accessPointsByObjectId = new HashMap<String, AccessPoint>();
    
    @Getter
    private Map<String, List<AccessPoint>> accessPointsByStopPlaceObjectId = new HashMap<String, List<AccessPoint>>();
    
    public AccessPointConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        
        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
    }
    private String subXpathSelection( String xPath) throws XPathParseException {
        AutoPilot autoPilot = createAutoPilot(nav);
        autoPilot.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
        autoPilot.selectXPath( xPath);

        String result = autoPilot.evalXPathToString();
        if ( result==null || result.isEmpty())
                result = null;

        autoPilot.resetXPath();
        return result;
}

    public List<AccessPoint> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        accessPoints.clear();
        accessPointsByStopPlaceObjectId.clear();
        accessPointsByObjectId.clear();
        
        int result = -1;
        pilot.selectXPath("//netex:SiteFrame//netex:stopPlaces/netex:StopPlace/netex:entrances/netex:StopPlaceEntrance");
        
        while( (result = pilot.evalXPath()) != -1 )
        {
            AccessPoint accessPoint = new AccessPoint();
            
            // Mandatory
            accessPoint.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            accessPoint.setName( (String)parseMandatoryElement(nav, "Name") );
            
            accessPoint.setContainedInStopArea( subXpathSelection( "../../@id"));
            
            // Optionnal            
            accessPoint.setLongitude( new BigDecimal((String)parseOptionnalSubElement(nav, "Location", "Longitude")) );
            accessPoint.setLatitude( new BigDecimal((String)parseOptionnalSubElement(nav, "Location", "Latitude")) );
            // Force type to WGS84
            accessPoint.setLongLatType( LongLatTypeEnum.WGS84 );
            
            String entryVal = subXpathSelection( "netex:IsEntry/text()");
            String exitVal = subXpathSelection( "netex:IsExit/text()");
            boolean isEntry = entryVal!=null && entryVal.equals("true");
            boolean isExit = exitVal!=null && exitVal.equals("true");        
            
            if (isEntry && isExit)
                accessPoint.setType(AccessPointTypeEnum.INOUT);              
            else if( !isEntry && isExit)
                accessPoint.setType(AccessPointTypeEnum.OUT);              
            else if(isEntry && !isExit)
                accessPoint.setType(AccessPointTypeEnum.IN);
            else
                accessPoint.setType(AccessPointTypeEnum.INOUT);
            
            accessPoint.setOpeningTime((Time)parseOptionnalSubElement(nav, "TimeBand", "StartTime", "Time"));
            accessPoint.setClosingTime((Time)parseOptionnalSubElement(nav, "TimeBand", "EndTime", "Time"));                        
            accessPoint.setComment( (String)parseOptionnalElement(nav, "Description") );           
  
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            accessPoint.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );  
            
            String stopAreaObjectId = accessPoint.getContainedInStopArea();
            if(stopAreaObjectId!= null && 
                    accessPointsByStopPlaceObjectId.containsKey(stopAreaObjectId))
            {
                List<AccessPoint> aps = accessPointsByStopPlaceObjectId.get(stopAreaObjectId);
                aps.add(accessPoint);
                accessPointsByStopPlaceObjectId.put(stopAreaObjectId, aps);
            }
            else
            {            
                List<AccessPoint> aps = new ArrayList<AccessPoint>();
                aps.add(accessPoint);
                accessPointsByStopPlaceObjectId.put(stopAreaObjectId, aps);
            }                                            
            accessPointsByObjectId.put(accessPoint.getObjectId(), accessPoint);
            accessPoints.add(accessPoint);
        }
        pilot.resetXPath();
        
        returnToRootElement(nav);        
        return accessPoints;
    }      
    
}
