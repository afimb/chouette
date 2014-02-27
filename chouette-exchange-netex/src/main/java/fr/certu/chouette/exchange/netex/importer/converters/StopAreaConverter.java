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
import java.text.ParseException;
import java.util.List;
import org.apache.log4j.Logger;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;

/**
 *
 * @author marc
 */
public class StopAreaConverter extends GenericConverter 
{
	private static final Logger logger = Logger.getLogger(StopAreaConverter.class);
	private List<StopArea> stopareas = new ArrayList<StopArea> ();        
	private AutoPilot autoPilot;
	private VTDNav nav;

	@Getter
	private Map<String,StopArea> stopAreaByObjectId;
	private Map<String,String> tariffByTariffId;


	public StopAreaConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
	{
		nav = vTDNav;

		autoPilot = new AutoPilot(nav);
		autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        

		stopAreaByObjectId = new HashMap<String, StopArea>();
		tariffByTariffId = new HashMap<String, String>();
	}

	public List<StopArea> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		stopareas.clear();
		stopAreaByObjectId.clear();
                tariffByTariffId.clear();
                
		convertTariffs();
		convertStopPlaces();
                convertRoutingConstraintZones();

		return stopareas;
	}
	public void convertRoutingConstraintZones() throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		AutoPilot autoPilot = createAutoPilot(nav);
		autoPilot.selectXPath( xPathFrames()+"netex:routingConstraintZones/"+
				"netex:RoutingConstraintZone");

		nav.push();
                logger.info( "ITL reading");
		while(autoPilot.evalXPath() != -1 )
		{  
                    String zoneUse = subXpathSelection("ZoneUse");
                    
                    if ( zoneUse.equals( "cannotBoardAndAlightInSameZone")) {
                        StopArea stopArea = new StopArea();
                        stopArea.setAreaType( ChouetteAreaEnum.ITL);

                        stopArea.setObjectId( subXpathSelection("@id"));
                        stopArea.setName( subXpathSelection("netex:Name"));
                        stopArea.setComment( subXpathSelection("netex:Description"));
                        stopArea.setRegistrationNumber( subXpathSelection("netex:PrivateCode"));
                        
                        List<String> lines = subXpathListSelection("netex:lines/netex:LineRef");
                        logger.info( "ITL lines reading");
                        for( String line : lines) {
                            logger.info( "line "+line);
                        }
                    }
		}
		nav.pop();
	}
	public void convertTariffs() throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		AutoPilot autoPilot = createAutoPilot(nav);
		autoPilot.selectXPath("//netex:ServiceFrame/netex:tariffZones/"+
				"netex:TariffZone");


		nav.push();
		while( autoPilot.evalXPath() != -1 )
		{  
			String tariffId = subXpathSelection("@id");
			String name = subXpathSelection("netex:Name");
			tariffByTariffId.put(tariffId, name);
		}
		nav.pop();
	}
	public void convertStopPlaces() throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		AutoPilot autoPilot2 = createAutoPilot(nav);
		autoPilot2.selectXPath("//netex:SiteFrame/netex:stopPlaces/"+
				"netex:StopPlace");
		Map<String,String> stopPlaceParentByStopPlace = new HashMap<String, String>();;

		nav.push();
		while( autoPilot2.evalXPath() != -1 )
		{  
			StopArea stopArea = new StopArea();

			// Mandatory            
			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

			convertCommonAttributes(stopArea);

			String parentRef = subXpathSelection("ParentZoneRef/@ref");
			if ( parentRef!= null) {
				stopPlaceParentByStopPlace.put( stopArea.getObjectId(), parentRef);
			}

			stopareas.add(stopArea);
			stopAreaByObjectId.put( stopArea.getObjectId(), stopArea);

			convertQuays( stopArea);
		} 
		nav.pop();

		for( String spObjectId : stopPlaceParentByStopPlace.keySet()) {
			StopArea stopPlace = stopAreaByObjectId.get( spObjectId);
			StopArea parentPlace = stopAreaByObjectId.get( stopPlaceParentByStopPlace.get( spObjectId));

			if ( parentPlace==null) {
				// TODO RAPPORT : mettre un message d'erreur dans le rapport
				logger.warn("missing parent "+stopPlaceParentByStopPlace.get( spObjectId)+" for "+spObjectId);
			}
			else
			{
				stopPlace.setParent( parentPlace);
				parentPlace.setAreaType(ChouetteAreaEnum.StopPlace);
			}
		}

	}


	public void convertQuays( StopArea stopPlace) throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		AutoPilot autoPilot2 = createAutoPilot(nav);
		autoPilot2.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
		autoPilot2.selectXPath("//netex:SiteFrame/netex:stopPlaces/"+
				"netex:StopPlace"+
				"[@id='"+stopPlace.getObjectId()+"']/netex:quays/netex:Quay");

		nav.push();
		while( autoPilot2.evalXPath() != -1 )
		{  
			StopArea stopArea = new StopArea();

			// Mandatory            
			stopArea.setAreaType( ChouetteAreaEnum.Quay);
			stopArea.setParent( stopPlace);

			convertCommonAttributes(stopArea);

			stopareas.add(stopArea);
			stopAreaByObjectId.put( stopArea.getObjectId(), stopArea);
		} 
		nav.pop();

	}
	private BigDecimal readNumber( String numberStr) {
		if ( numberStr==null)
			return null;
		try {
			return BigDecimal.valueOf( Double.valueOf(numberStr));
		} catch (Exception e) {
			return null;
		}
	}

	private List<String> subXpathListSelection( String xPath) throws XPathParseException, NavException, XPathEvalException {
		AutoPilot autoPilot = createAutoPilot(nav);
		autoPilot.declareXPathNameSpace("gml","http://www.opengis.net/gml/3.2");        
		autoPilot.selectXPath( xPath);

                List<String> result = new ArrayList<String>();
                int number = autoPilot.evalXPath();
                logger.info("number="+number);
                while ( autoPilot.iterate())
                {
                    String element = autoPilot.evalXPathToString();
                    if ( element!=null && !element.isEmpty())
                            result.add( element);
                    
                }

		autoPilot.resetXPath();
		return result;
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

	private void convertCommonAttributes(StopArea stopArea) throws XPathParseException, NavException, NumberFormatException, ParseException {

		// Optionnal
		stopArea.setObjectId( subXpathSelection("@id"));

		Object objectVersion = subXpathSelection("@version");
		stopArea.setObjectVersion( 0);
		try { stopArea.setObjectVersion( (Integer)objectVersion); } catch(Exception e) {}

		stopArea.setName( subXpathSelection("netex:Name"));
		stopArea.setRegistrationNumber( subXpathSelection("netex:PrivateCode"));
		stopArea.setNearestTopicName( subXpathSelection("netex:Landmark"));
		stopArea.setComment( subXpathSelection("netex:Description"));
		stopArea.setCountryCode( subXpathSelection( "netex:PostalAddress/netex:PostCode"));
		stopArea.setStreetName( subXpathSelection( "netex:PostalAddress/netex:AddressLine1"));

		String tariffName = tariffByTariffId.get( subXpathSelection( "netex:tariffZones/netex:TariffZoneRef/@ref"));
		if ( tariffName!=null) {
			try {
				stopArea.setFareCode( Integer.parseInt(tariffName));            
			} catch( Exception e){}
		}

		BigDecimal longitude = readNumber( subXpathSelection("netex:Centroid/netex:Location/netex:Longitude"));
		BigDecimal latitude = readNumber( subXpathSelection("netex:Centroid/netex:Location/netex:Latitude"));

		if ( longitude!=null && latitude!=null) {
			stopArea.setLatitude(latitude);
			stopArea.setLongitude(longitude);
			stopArea.setLongLatType( LongLatTypeEnum.WGS84);
		}

		String projectedType = subXpathSelection( "netex:Centroid/netex:Location/gml:pos/@srsName");
		String xy = subXpathSelection( "netex:Centroid/netex:Location/gml:pos");

		BigDecimal x = readX(xy);
		BigDecimal y = readY(xy);

		if ( projectedType!=null && x!=null && y!=null) {
			stopArea.setProjectionType(projectedType);
			stopArea.setX(x);
			stopArea.setY(y);
		}
	}
	public BigDecimal readNumberInPattern( String xy, String pattern) {
		String numberStr = null;
		if (xy!=null) {
			Matcher m = Pattern.compile( pattern).matcher(xy.trim());
			if ( m.matches()) {
				numberStr = m.group(1);
			}
		}
		return readNumber(numberStr);
	}
	public BigDecimal readX( String xy) {
		return readNumberInPattern( xy, "([\\d\\.]+) [\\d\\.]+");
	}
	public BigDecimal readY( String xy) {
		return readNumberInPattern( xy, "[\\d\\.]+ ([\\d\\.]+)");
	}

}
