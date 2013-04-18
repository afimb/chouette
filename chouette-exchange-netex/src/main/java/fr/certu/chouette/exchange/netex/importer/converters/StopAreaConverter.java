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

		convertTariffs();
		convertStopPlaces();

		return stopareas;
	}
	public void convertTariffs() throws XPathEvalException, NavException, XPathParseException, ParseException
	{
		AutoPilot autoPilot = createAutoPilot(nav);
		autoPilot.selectXPath("//netex:ServiceFrame/netex:tariffZones/"+
				"netex:TariffZone");

		int result = -1;

		nav.push();
		while( (result = autoPilot.evalXPath()) != -1 )
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

		int result = -1;

		nav.push();
		while( (result = autoPilot2.evalXPath()) != -1 )
		{  
			StopArea stopArea = new StopArea();

			// Mandatory            
			stopArea.setAreaType(ChouetteAreaEnum.COMMERCIALSTOPPOINT);

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
				parentPlace.setAreaType(ChouetteAreaEnum.STOPPLACE);
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

		int result = -1;

		nav.push();
		while( (result = autoPilot2.evalXPath()) != -1 )
		{  
			StopArea stopArea = new StopArea();

			// Mandatory            
			stopArea.setAreaType( ChouetteAreaEnum.QUAY);
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
