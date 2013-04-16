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
    
    public AccessPointConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        pilot = new AutoPilot(nav);
        pilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
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
            accessPoint.setContainedInStopArea((String)parseMandatoryAttribute(nav, "StopPlaceEntranceRef", "ref", "id"));
            
            // Optionnal            
            accessPoint.setLongitude( new BigDecimal((String)parseOptionnalSubElement(nav, "Location", "Longitude")) );
            accessPoint.setLatitude( new BigDecimal((String)parseOptionnalSubElement(nav, "Location", "Latitude")) );
            // Force type to WGS84
            accessPoint.setLongLatType( LongLatTypeEnum.WGS84 );
            
            String isEntry = (String)parseMandatoryElement(nav, "isEntry", "boolean");
            String isExit = (String)parseMandatoryElement(nav, "isExit", "boolean");        
            
            if (isEntry.equals("true") && isExit.equals("true"))
                accessPoint.setType(AccessPointTypeEnum.INOUT);              
            else if(isEntry.equals("false") && isExit.equals("true") )
                accessPoint.setType(AccessPointTypeEnum.OUT);              
            else if(isEntry.equals("true") && isExit.equals("false") )
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
