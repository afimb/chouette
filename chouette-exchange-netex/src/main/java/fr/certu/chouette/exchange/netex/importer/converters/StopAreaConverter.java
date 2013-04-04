/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import com.ximpleware.NavException;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDNav;
import fr.certu.chouette.model.neptune.AreaCentroid;
import java.text.ParseException;
import java.util.List;
import org.apache.log4j.Logger;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author marc
 */
public class StopAreaConverter extends GenericConverter 
{
    private static final Logger logger = Logger.getLogger(StopAreaConverter.class);
    private List<StopArea> stopareas = new ArrayList<StopArea> ();    
    private Map<String,StopArea> stopAreaByObjectId;
    private AutoPilot autoPilot;
    private VTDNav nav;
    
    public StopAreaConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException
    {
        nav = vTDNav;
        
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        stopAreaByObjectId = new HashMap<String, StopArea>();
    }
    
    public List<StopArea> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        autoPilot.selectXPath("//netex:SiteFrame/netex:topographicPlaces/"+
                "netex:TopographicPlace");
        int result = -1;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {  
            StopArea stopArea = new StopArea();
            
            // Mandatory            
            stopArea.setName((String)parseMandatoryElement(nav, "Name"));
            stopArea.setObjectId((String)parseMandatoryAttribute(nav, "id"));
            stopArea.setAreaType(ChouetteAreaEnum.STOPPLACE);
            
            stopareas.add(stopArea);
            stopAreaByObjectId.put( stopArea.getObjectId(), stopArea);
        } 
        
        convertStopPlaces();
                
        return stopareas;
    }
    public void convertStopPlaces() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        AutoPilot autoPilot2 = new AutoPilot(nav);
        autoPilot2.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        autoPilot2.selectXPath("//netex:SiteFrame/netex:stopPlaces/"+
                "netex:StopPlace");
        
        int result = -1;
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            StopArea stopArea = new StopArea();
            
            // Mandatory            
            stopArea.setObjectId((String)parseMandatoryAttribute(nav, "id"));
            stopArea.setAreaType(ChouetteAreaEnum.COMMERCIALSTOPPOINT);
            stopArea.setName((String)parseMandatoryElement(nav, "Name"));
            
            // Optionnal
            stopArea.setRegistrationNumber((String)parseOptionnalAttribute(nav, "PrivateCode"));
            stopArea.setNearestTopicName((String)parseOptionnalAttribute(nav, "LandMark"));
            stopArea.setComment((String)parseOptionnalAttribute(nav, "Description"));
            
            String topographicRef = (String)parseOptionnalAttribute(nav, "ContainedInPlaceRef", "ref");
            if ( topographicRef!= null) {
                stopArea.setParent( stopAreaByObjectId.get( topographicRef));
            }
            
            stopareas.add(stopArea);
            stopAreaByObjectId.put( stopArea.getObjectId(), stopArea);
            
            convertQuays( stopArea);
        } 
        
    }
    public void convertQuays( StopArea stopPlace) throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        AutoPilot autoPilot2 = createAutoPilot(nav);
        autoPilot2.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
        autoPilot2.selectXPath("//netex:SiteFrame/netex:stopPlaces/"+
                "netex:StopPlace"+
                "[@id='"+stopPlace.getObjectId()+"']/netex:quays/netex:Quay");
        
        int result = -1;
        
        while( (result = autoPilot2.evalXPath()) != -1 )
        {  
            StopArea stopArea = new StopArea();
            
            // Mandatory            
            stopArea.setObjectId((String)parseMandatoryAttribute(nav, "id"));
            stopArea.setAreaType( ChouetteAreaEnum.QUAY);
            stopArea.setParent( stopPlace);
            
            // Optionnal
            stopArea.setName((String)parseOptionnalAttribute(nav, "Name"));
            stopArea.setRegistrationNumber((String)parseOptionnalAttribute(nav, "PrivateCode"));
            stopArea.setNearestTopicName((String)parseOptionnalAttribute(nav, "LandMark"));
            stopArea.setComment((String)parseOptionnalAttribute(nav, "Description"));
            
            AreaCentroid centroid = new AreaCentroid();
            Object longitude = parseOptionnalAttribute(nav, "Longitude", "Double");
            if ( longitude!=null) {
                centroid.setLongitude(BigDecimal.valueOf( (Double)longitude));
            }
            Object latitude = parseOptionnalAttribute(nav, "Latitude", "Double");
            if ( latitude!=null) {
                centroid.setLatitude(BigDecimal.valueOf( (Double)latitude));
            }
            centroid.setLongLatType(LongLatTypeEnum.WGS84);

            stopArea.setAreaCentroid( centroid);
            
            ProjectedPoint projectedPoint = new ProjectedPoint();
            projectedPoint.setProjectionType( (String)parseOptionnalCAttribute(nav, "pos", "srsName"));
            centroid.setProjectedPoint(projectedPoint);
            
            stopareas.add(stopArea);
            stopAreaByObjectId.put( stopArea.getObjectId(), stopArea);
        } 
        
    }

}
