package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.exchange.netex.exporter.ModelTranslator;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LinkOrientationEnum;

public class SiteFrameWriter extends AbstractWriter{
	

	
	public static void write(Writer writer, ExportableData data ) throws IOException, DatatypeConfigurationException 
	{
		Line line = data.getLine();
		Set<StopArea> stopPlaces = new HashSet<>();
		stopPlaces.addAll(data.getStopPlaces());
		stopPlaces.addAll(data.getCommercialStops());
		Set<AccessLink> accessLinks = data.getAccessLinks();
		ModelTranslator modelTranslator = new ModelTranslator();
		DatatypeFactory durationFactory = DatatypeFactory.newInstance();

		writer.write("\n");

		writer.write("<!-- =========================================== -->\n");
		writer.write("<!-- NEPTUNE StopPlaces (StopArea of type StopPlace) are mapped to NeTEx StopPlace -->\n");
		writer.write("<SiteFrame version=\"any\" id=\""+line.objectIdPrefix()+":SiteFrame:SF01\"> \n");
		writer.write("  <stopPlaces>\n");
		//    #foreach( $stopPlace in $stopPlaces )
		for (StopArea stopPlace : stopPlaces) {
		writer.write("    <StopPlace version=\""+stopPlace.getObjectVersion()+"\" id=\""+modelTranslator.netexId(stopPlace)+"\">\n");
		//     #if( $stopPlace.name )
		if (isSet(stopPlace.getName()))
		writer.write("     <Name>"+toXml(stopPlace.getName())+"</Name>\n");
		//     #end
		//     #if( $stopPlace.comment )
		if (isSet(stopPlace.getComment()))
		writer.write("     <Description>"+toXml(stopPlace.getComment())+"</Description>\n");
		//     #end
		//     #if( $stopPlace.registrationNumber )
		if (isSet(stopPlace.getRegistrationNumber()))
		writer.write("     <PrivateCode>"+toXml(stopPlace.getRegistrationNumber())+"</PrivateCode>\n");
		//     #end
		//      #if( ( $stopPlace.latitude && $stopPlace.longitude) || ( $stopPlace.x && $stopPlace.y && $stopPlace.projectionType))
		if (stopPlace.hasCoordinates() || stopPlace.hasProjection()) {
		writer.write("      <Centroid>\n");
		writer.write("        <Location id=\""+modelTranslator.netexMockId(stopPlace,"Location")+"\">\n");
		//          #if ( $stopPlace.longitude && $stopPlace.latitude )
		if (stopPlace.hasCoordinates()) {
		writer.write("          <Longitude>"+stopPlace.getLongitude()+"</Longitude>\n");
		writer.write("          <Latitude>"+stopPlace.getLatitude()+"</Latitude>\n");
		}
		//          #end
		//          #if ( $stopPlace.x && $stopPlace.y && $stopPlace.projectionType)
		if (stopPlace.hasProjection()) 
		writer.write("          <gml:pos srsName=\""+stopPlace.getProjectionType()+"\">"+stopPlace.getX()+" "+stopPlace.getY()+"</gml:pos>\n");
		//          #end
		writer.write("        </Location>\n");
		writer.write("      </Centroid>\n");
		}
		//      #end
		//      #if( $stopPlace.parent )
		if (isSet(stopPlace.getParent()))
		writer.write("      <ParentZoneRef version=\""+stopPlace.getParent().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(stopPlace.getParent())+"\" />\n");
		//      #end
		//      #if( $stopPlace.nearestTopicName )
		if (isSet(stopPlace.getNearestTopicName()))
		writer.write("      <Landmark>"+toXml(stopPlace.getNearestTopicName())+"</Landmark>\n");
		//      #end
		//      #if($stopPlace.streetName || $stopPlace.countryCode)
		if (stopPlace.hasAddress()) {
		writer.write("      <PostalAddress version=\"any\" id=\""+modelTranslator.netexMockId(stopPlace,"PostalAddress")+"\">\n");
		writer.write("        <CountryRef ref=\"fr\"/>\n");
		//        #if ( $stopPlace.streetName )
		if (isSet(stopPlace.getStreetName()))
		writer.write("        <AddressLine1>"+toXml(stopPlace.getStreetName())+"</AddressLine1>\n");
		//        #end
		//        #if ( $stopPlace.countryCode )
		if (isSet(stopPlace.getCountryCode()))
		writer.write("        <PostCode>"+toXml(stopPlace.getCountryCode())+"</PostCode>\n");
		//        #end
		writer.write("      </PostalAddress>\n");
		}
		//      #end
		//      #if ( $stopPlace.accessLinks && $stopPlace.accessLinks.size() > 0 )
		if (nonEmpty(stopPlace.getAccessPoints())) {
		writer.write("      <entrances>\n");
		//        #foreach( $accessLink in $stopPlace.accessLinks )
		for (AccessPoint accessPoint : stopPlace.getAccessPoints()) {
		//        #set( $accessPoint = $accessLink.accessPoint )
		//        #if ( $accessPoint )
		writer.write("        <StopPlaceEntrance version=\""+accessPoint.getObjectVersion()+"\" id=\""+modelTranslator.netexId(accessPoint)+"\">\n");
		//          #if ( $accessPoint.name )
		if (isSet(accessPoint.getName()))
		writer.write("          <Name>"+toXml(accessPoint.getName())+"</Name>    \n");      
		//          #end
		//          #if ( $accessPoint.comment )
		if (isSet(accessPoint.getComment()))
		writer.write("          <Description>"+toXml(accessPoint.getComment())+"</Description>  \n");
		//          #end
		//          #if ( $accessPoint.longitude && $accessPoint.latitude)
		if (accessPoint.hasCoordinates()) {
		writer.write("          <Centroid>\n");
		writer.write("            <Location srsName=\""+accessPoint.getLongLatType()+"\" >\n");
		writer.write("              <Longitude>"+accessPoint.getLongitude()+"</Longitude>\n");
		writer.write("              <Latitude>"+accessPoint.getLatitude()+"</Latitude>\n");
		writer.write("            </Location>\n");
		writer.write("          </Centroid>\n");
		}
		//          #end
		if (isSet(accessPoint.getOpeningTime()) && isSet(accessPoint.getClosingTime()))
		//          #if ( $accessPoint.openingTime && $accessPoint.closingTime)
		writer.write("          <validityConditions>\n");
		writer.write("            <AvailabilityCondition version=\""+accessPoint.getObjectVersion()+"\" id=\""+modelTranslator.netexMockId(accessPoint,"AvailabilityCondition")+"\">\n");
		writer.write("                <timebands>\n");
		writer.write("                    <Timeband version=\""+accessPoint.getObjectVersion()+"\" id=\""+modelTranslator.netexMockId(accessPoint,"Timeband")+"\">\n");
		writer.write("                        <StartTime>"+accessPoint.getOpeningTime()+"</StartTime>\n");
		writer.write("                        <EndTime>"+accessPoint.getClosingTime()+"</EndTime>\n");
		writer.write("                    </Timeband>\n");
		writer.write("                </timebands>\n");
		writer.write("            </AvailabilityCondition>\n");
		writer.write("          </validityConditions>\n");
		//          #end
		writer.write("          <IsEntry>#if( $accessPoint.type == \"In\" || $accessPoint.type == \"InOut\")true#{else}false#end</IsEntry>\n");
		writer.write("          <IsExit>#if( $accessPoint.type == \"Out\" || $accessPoint.type == \"InOut\")true#{else}false#end</IsExit>    \n");     
		writer.write("       </StopPlaceEntrance>\n");
		//        #end  
		}
		//        #end
		writer.write("      </entrances> \n");
		}
		//      #end

		//      #if($stopPlace.fareCode)
		if (isSet(stopPlace.getFareCode())) {
		writer.write("      <tariffZones>\n");
		writer.write("        <TariffZoneRef version=\"any\" ref=\"Local:TariffZone:"+stopPlace.getFareCode()+"\"/>\n");
		writer.write("      </tariffZones>\n");
		}
		//      #end
		//      #if ( ( $stopPlace.areaType.toString() == \"CommercialStopPoint\") &&
		//            $stopPlace.containedStopAreas &&
		//            $stopPlace.containedStopAreas.size() > 0    )
		if (stopPlace.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint) && nonEmpty(stopPlace.getContainedStopAreas())) {
		writer.write("      <quays>\n");
		//        #foreach( $quay in $stopPlace.containedStopAreas )
		for (StopArea quay : stopPlace.getContainedStopAreas()) {
		writer.write("        <Quay version=\""+quay.getObjectVersion()+"\" id=\""+modelTranslator.netexId(quay)+"\">\n");
		//         #if( $quay.name )
		if (isSet(quay.getName())) 
		writer.write("         <Name>"+toXml(quay.getName())+"</Name>\n");
		//         #end
		 //        #if( $quay.comment )
		if (isSet(quay.getComment())) 
		writer.write("         <Description>"+toXml(quay.getComment())+"</Description>\n");
		//         #end
		//         #if( $quay.registrationNumber )
		if (isSet(quay.getRegistrationNumber())) 
		writer.write("         <PrivateCode>"+toXml(quay.getRegistrationNumber())+"</PrivateCode>\n");
		//         #end
		//         #if( ( $quay.latitude && $quay.longitude) || ($quay.x && $quay.y && $quay.projectionType))
		if (quay.hasCoordinates() || quay.hasProjection()) {
		writer.write("         <Centroid>\n");
		writer.write("            <Location id=\""+modelTranslator.netexMockId(quay,"Location")+"\">\n");
		//          #if ( $stopPlace.longitude && $stopPlace.latitude )
		if (quay.hasCoordinates()) {
		writer.write("             <Longitude>"+quay.getLongitude()+"</Longitude>\n");
		writer.write("             <Latitude>"+quay.getLatitude()+"</Latitude>\n");
		}
		//          #end
		//          #if ( $stopPlace.x && $stopPlace.y && $stopPlace.projectionType)
		if (quay.hasProjection()) 
		writer.write("             <gml:pos srsName=\""+quay.getProjectionType()+"\">"+quay.getX()+" "+quay.getY()+"</gml:pos>\n");
		//          #end
		writer.write("            </Location>\n");
		writer.write("          </Centroid>\n");
		}
		//          #end

		//         #if( $quay.nearestTopicName )
		if (isSet(quay.getRegistrationNumber())) 
		writer.write("         <Landmark>"+toXml(quay.getComment())+"</Landmark>\n");
		//         #end

		//          #if($quay.streetName || $quay.countryCode)
		if (quay.hasAddress()) {
		writer.write("         <PostalAddress version=\"any\" id=\""+modelTranslator.netexMockId(quay,"PostalAddress")+"\">\n");
		writer.write("            <CountryRef ref=\"fr\"/>\n");
		//            #if ( $quay.streetName )
		if (isSet(quay.getStreetName())) 
		writer.write("            <AddressLine1>"+toXml(quay.getStreetName())+"</AddressLine1>\n");
		//            #end
		//            #if ( $quay.countryCode )
		if (isSet(quay.getCountryCode())) 
		writer.write("            <PostCode>"+toXml(quay.getCountryCode())+"</PostCode>\n");
		//            #end
		writer.write("          </PostalAddress>\n");
		}
		//          #end
		//          #if($quay.fareCode)
		if (isSet(quay.getFareCode())) {
		writer.write("          <tariffZones>\n");
		writer.write("            <TariffZoneRef version=\"any\" ref=\"Local:TariffZone:"+quay.getFareCode()+"\"/>\n");
		writer.write("          </tariffZones>\n");
		}
		//          #end
		writer.write("        </Quay>	\n");
		}
		//        #end	
		writer.write("      </quays>\n");
		}
		//      #end	
		writer.write("    </StopPlace>\n");
		}
		//    #end
		writer.write("  </stopPlaces>\n");
		//  #if ( $accessLinks && $accessLinks.size() > 0 )
		if (nonEmpty(accessLinks)) {
		writer.write("  <!-- Assignments of Path Links -->\n");
		writer.write("  <pathLinks>\n");
		 //   #foreach( $accessLink in $accessLinks )       
		for (AccessLink accessLink : accessLinks) {
		writer.write("    <PathLink id=\""+modelTranslator.netexId(accessLink)+"\" version=\""+accessLink.getObjectVersion()+"\">  \n");
		//      #if ( $accessLink.name )
		if (isSet(accessLink.getName()))
		writer.write("      <Name>"+toXml(accessLink.getName())+"</Name>\n");
		//      #end
		//      #if ( $accessLink.linkDistance)
		if (isSet(accessLink.getLinkDistance()))
		writer.write("      <Distance>"+accessLink.getLinkDistance()+"</Distance>\n");
		//      #end
		//      #if($accessLink.accessPoint && $accessLink.stopArea && $accessLink.linkOrientation) 
		if (isSet(accessLink.getAccessPoint()) && isSet(accessLink.getStopArea()) && isSet(accessLink.getLinkOrientation()) ) {
		//          #if($accessLink.linkOrientation == \"AccessPointToStopArea\")
		if (accessLink.getLinkOrientation().equals(LinkOrientationEnum.AccessPointToStopArea)) {
		writer.write("          <From>\n");
		writer.write("            <PlaceRef ref=\""+modelTranslator.netexId(accessLink.getAccessPoint().getContainedIn())+"\" />\n");
		writer.write("            <EntranceRef ref=\""+modelTranslator.netexId(accessLink.getAccessPoint())+"\" />   \n");     
		writer.write("          </From> \n");
		writer.write("          <To> \n");
		writer.write("           <PlaceRef ref=\""+modelTranslator.netexId(accessLink.getStopArea())+"\" /> \n");
		writer.write("          </To>\n");
		}
		//          #else
		else {
		writer.write("          <From>\n");
		writer.write("            <PlaceRef ref=\""+modelTranslator.netexId(accessLink.getStopArea())+"\" />  \n");
		writer.write("          </From> \n");
		writer.write("          <To> \n");
		writer.write("            <PlaceRef ref=\""+modelTranslator.netexId(accessLink.getAccessPoint().getContainedIn())+"\" /> \n");
		writer.write("            <EntranceRef ref=\""+modelTranslator.netexId(accessLink.getAccessPoint())+"\" />     \n");  
		writer.write("          </To>\n");
		}
		//          #end
		}
		//      #end
		//      #if ( $accessLink.mobilityRestrictedSuitable)
		if (isSet(accessLink.getMobilityRestrictedSuitable())) {
		writer.write("      <AccessibilityAssessment version=\""+accessLink.getObjectVersion()+"\" id=\""+modelTranslator.netexMockId(accessLink,"AccessibilityAssessment")+"\">\n");
		writer.write("        <MobilityImpairedAccess>"+accessLink.getMobilityRestrictedSuitable()+"</MobilityImpairedAccess>\n");
		writer.write("      </AccessibilityAssessment>\n");
		}
		//      #end
		//      #if ( $accessLink.linkType)
		if (isSet(accessLink.getLinkType()))
		writer.write("      <Covered>"+modelTranslator.toLinkType(accessLink.getLinkType())+"</Covered>\n");
		//      #end
		//      #if ( $accessLink.defaultDuration || $accessLink.frequentTravellerDuration ||
		//            $accessLink.occasionalTravellerDuration || $accessLink.mobilityRestrictedTravellerDuration)
		if (isSet(accessLink.getDefaultDuration(),accessLink.getFrequentTravellerDuration(),accessLink.getOccasionalTravellerDuration(),accessLink.getMobilityRestrictedTravellerDuration())) {
		writer.write("      <TransferDuration>\n");
		//        #if ( $accessLink.defaultDuration)
		if (isSet(accessLink.getDefaultDuration())) 
		writer.write("       <DefaultDuration>"+durationFactory.newDuration(accessLink.getDefaultDuration().getTime())+"</DefaultDuration>\n");
		//        #end
		//        #if ( $connectionLink.frequentTravellerDuration)
		if (isSet(accessLink.getFrequentTravellerDuration())) 
		writer.write("        <FrequentTravellerDuration>"+durationFactory.newDuration(accessLink.getFrequentTravellerDuration().getTime())+"</FrequentTravellerDuration>\n");
		//        #end
		//        #if ( $connectionLink.occasionalTravellerDuration)
		if (isSet(accessLink.getOccasionalTravellerDuration())) 
		writer.write("        <OccasionalTravellerDuration>"+durationFactory.newDuration(accessLink.getOccasionalTravellerDuration().getTime())+"</OccasionalTravellerDuration>\n");
		//        #end
		//        #if ( $connectionLink.mobilityRestrictedTravellerDuration)
		if (isSet(accessLink.getMobilityRestrictedTravellerDuration())) 
		writer.write("        <MobilityRestrictedTravellerDuration>"+durationFactory.newDuration(accessLink.getMobilityRestrictedTravellerDuration().getTime())+"</MobilityRestrictedTravellerDuration>\n"); 
		 //       #end
		writer.write("      </TransferDuration>\n");
		}
		//      #end
		//      ## TODO : Add enum  
		writer.write("    </PathLink>\n");
		}
		//    #end
		writer.write("  </pathLinks> \n"); 
		}
		//  #end

		writer.write("</SiteFrame>\n");

	}

}
