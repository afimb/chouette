package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.exchange.netex.exporter.ModelTranslator;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;

public class ServiceFrameWriter extends AbstractWriter{
	

	
	public static void write(Writer writer, ExportableData data ) throws IOException, DatatypeConfigurationException 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		DatatypeFactory durationFactory = DatatypeFactory.newInstance();
		Line line = data.getLine();
		Network network = line.getNetwork();
		ModelTranslator modelTranslator = new ModelTranslator();
		writer.write("  ");
		writer.write("<!-- ServiceFrame to map the PTNetwork  NEPTUNE Object -->\n");
		writer.write("<ServiceFrame version=\"any\"  id=\""+line.objectIdPrefix()+":ServiceFrame:"+line.objectIdSuffix()+"\">\n");
		writer.write("  <!-- NEPTUNE PTNetwork Mappling =========================================== -->\n");
		writer.write("  <Network version=\""+network.getObjectVersion()+"\" changed=\""+dateFormat.format(line.getNetwork().getVersionDate())+"\" \n");
		writer.write("           id=\""+modelTranslator.netexId(network)+"\" >\n");
		//      #if ( $network.comment )
		if (isSet(network.getComment()))
		{
		writer.write("      <keyList>\n");
		writer.write("          <KeyValue>\n");
		writer.write("              <Key>Comment</Key>\n");
		writer.write("              <Value>"+toXml(network.getComment())+"</Value>\n");
		writer.write("          </KeyValue>\n");
		writer.write("      </keyList>\n");
		}
		//      #end
		//    #if ( $network.name )
		if (isSet(network.getName()))
		writer.write("    <Name>"+toXml(network.getName())+"</Name>\n");
		//    #end
		//    #if ( $network.description )
		if (isSet(network.getDescription()))
		writer.write("    <Description>"+toXml(network.getDescription())+"</Description>\n");
		//    #end
		//    #if ( $network.registrationNumber )
		if (isSet(network.getRegistrationNumber()))
		writer.write("    <PrivateCode>"+toXml(network.getRegistrationNumber())+"</PrivateCode>\n");
		//    #end
		writer.write("  </Network>\n");
		writer.write("  <!-- NEPTUNE Route Mapping (part 1 : directions ) =========================================== -->\n");
		writer.write("  <directions>\n");
		//    #foreach( $route in $line.routes )   
		for (Route route : line.getRoutes()) {
		writer.write("    <Direction version=\"any\" id=\""+route.objectIdPrefix()+":Route:"+route.objectIdSuffix()+":Direction\">\n");
		//      #if ( $route.direction )
		if (isSet(route.getDirection()))
		writer.write("      <Name>"+toXml(route.getDirection())+"</Name>\n");
		//      #end
		if (isSet(route.getWayBack()))
		//      #if ($route.wayBack == \"A\")
			if (route.getWayBack().equals("A"))
		writer.write("      <DirectionType>outbound</DirectionType>\n");
		//      #elseif( $route.wayBack == \"R\" )
			else if (route.getWayBack().equals("R"))
		writer.write("      <DirectionType>inbound</DirectionType>\n");
		//      #end
		writer.write("    </Direction>\n");
		}
		//    #end                  
		writer.write("  </directions>\n");
		writer.write("  <!-- NEPTUNE StopPoint (RoutePoint)  mapping=========================================== -->\n");
		writer.write("  <routePoints>\n");
		//    #foreach( $route in $line.routes ) 
		for (Route route : line.getRoutes()) {
		//      #foreach( $stopPoint in $route.stopPoints )
		for (StopPoint stopPoint : route.getStopPoints()) {
			if (stopPoint == null) continue;
		//      #set($routePointId = ${route.objectIdSuffix()} + 'A' + $stopPoint.position + 'A' + ${stopPoint.objectIdSuffix()} )
			String routePointId = route.objectIdSuffix() + "A" + stopPoint.getPosition()+"A"+stopPoint.objectIdSuffix();
		writer.write("    <RoutePoint version=\""+stopPoint.getObjectVersion()+"\" id=\""+stopPoint.objectIdPrefix()+":RoutePoint:"+routePointId+"\">\n");
		writer.write("      <projections>\n");
		writer.write("        <PointProjection version=\"any\" id=\""+stopPoint.objectIdPrefix()+":PointProjection:"+routePointId+"\">\n");
		writer.write("          <ProjectedPointRef version=\""+stopPoint.getContainedInStopArea().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(stopPoint)+"\"/>\n");
		writer.write("        </PointProjection>\n");
		writer.write("      </projections>\n");
		writer.write("    </RoutePoint>\n");
		}
		//      #end
		}
        //    #end
		writer.write("  </routePoints>\n");
		writer.write("  <!-- NEPTUNE Route Mapping (part 2 : routes ) =========================================== -->\n");
		writer.write("  <routes>\n");
		//    #foreach( $route in $line.routes )   
		for (Route route : line.getRoutes()) {
		writer.write("    <Route version=\""+route.getObjectVersion()+"\" id=\""+modelTranslator.netexId(route)+"\">\n");
		//      #if ( $route.comment || $route.number )
		if (isSet(route.getComment(),route.getNumber())){
		writer.write("      <keyList>\n");
		 //         #if ( $route.comment )
		if (isSet(route.getComment())){
		writer.write("          <KeyValue>\n");
		writer.write("              <Key>Comment</Key>\n");
		writer.write("              <Value>"+toXml(route.getComment())+"</Value>\n");
		writer.write("          </KeyValue>\n");
		}
		//          #end
		//          #if ( $route.number )
		if (isSet(route.getNumber())){
		writer.write("          <KeyValue>\n");
		writer.write("              <Key>Number</Key>\n");
		writer.write("              <Value>"+toXml(route.getNumber())+"</Value>\n");
		writer.write("          </KeyValue>\n");
		}
		//          #end
		writer.write("      </keyList>\n");
		}
		//      #end
		//      #if ( $route.name )
		if (isSet(route.getName()))
		writer.write("      <Name>"+toXml(route.getName())+"</Name>\n");
		//      #end
		//      #if ( $route.publishedName )
		if (isSet(route.getPublishedName()))
		writer.write("      <ShortName>"+toXml(route.getPublishedName())+"</ShortName>\n");
		//      #end
		writer.write("      <!-- NEPTUNE [mapping:variable] : Route->DirectionRef reference of Direction object mapping to NEPTUNE Route->publishedName and Route->RouteExtension->wayBack -->\n");
		writer.write("      <DirectionRef version=\"any\" ref=\""+route.objectIdPrefix()+":Route:"+route.objectIdSuffix()+":Direction\"/>\n");
		writer.write("      <pointsInSequence>\n");                             
		//        #foreach( $stopPoint in $route.stopPoints )
		for (StopPoint stopPoint : route.getStopPoints()) {
			if (stopPoint == null) continue;
		//        #set($routePointId = ${route.objectIdSuffix()} + 'A' + $stopPoint.position + 'A' + ${stopPoint.objectIdSuffix()} )
			String routePointId = route.objectIdSuffix() + "A" + stopPoint.getPosition()+"A"+stopPoint.objectIdSuffix();
		//        #set($pointOnRouteId = ${stopPoint.objectIdSuffix()} + '-' + $stopPoint.position )
			String pointOnRouteId = stopPoint.objectIdSuffix()+"-"+stopPoint.getPosition();
		//        #set($netex_order = 1 + $stopPoint.position)
			int netex_order = stopPoint.getPosition() + 1;
		writer.write("        <PointOnRoute version=\"any\" id=\""+stopPoint.objectIdPrefix()+":PointOnRoute:"+pointOnRouteId+"\" order=\""+netex_order+"\">\n");
		writer.write("          <RoutePointRef version=\""+route.getObjectVersion()+"\" ref=\""+stopPoint.objectIdPrefix()+":RoutePoint:"+routePointId+"\"/>\n");
		writer.write("        </PointOnRoute>\n");
		}
		//        #end
		writer.write("      </pointsInSequence>\n");
		//      #if ( $route.wayBackRouteId )
		if (isSet(route.getOppositeRoute()))
		writer.write("      <InverseRouteRef version=\"any\" ref=\""+modelTranslator.netexId(route.getOppositeRoute())+"\"/>\n");
		//      #end
		writer.write("    </Route>\n");
		}
		//    #end
		writer.write("  </routes>\n");
		writer.write("  <!-- NEPTUNE Line Mapping =========================================== -->\n");
		writer.write("  <lines>\n");
		writer.write("   <Line version=\""+line.getObjectVersion()+"\" id=\""+modelTranslator.netexId(line)+"\">\n");
		//      #if ( $line.name )
		if (isSet(line.getName()))
		writer.write("      <Name>"+toXml(line.getName())+"</Name>\n");
		//      #end
		//      #if ( $line.publishedName )
		if (isSet(line.getPublishedName()))
		writer.write("      <ShortName>"+toXml(line.getPublishedName())+"</ShortName>\n");
		//      #end
		//      #if ( $line.comment )
		if (isSet(line.getComment()))
		writer.write("      <Description>"+toXml(line.getComment())+"</Description>\n");
		//      #end
		//      #if ( $line.transportModeName )
		if (isSet(line.getTransportModeName()))
		writer.write("      <TransportMode>"+modelTranslator.toTransportModeNetex(line.getTransportModeName())+"</TransportMode>\n");
		//      #end
		//      #if ( $line.number )
		if (isSet(line.getNumber()))
		writer.write("      <PublicCode>"+toXml(line.getNumber())+"</PublicCode>\n");
		 //     #end
		//      #if ( $line.registrationNumber )
		if (isSet(line.getRegistrationNumber()))
		writer.write("      <PrivateCode>"+toXml(line.getRegistrationNumber())+"</PrivateCode>\n");
		//      #end
		writer.write("      <routes>\n");
		//        #foreach( $route in $line.routes )
		for (Route route : line.getRoutes()) {
		writer.write("        <RouteRef version=\""+route.getObjectVersion()+"\" ref=\""+modelTranslator.netexId(route)+"\"/>\n");
		}
		//        #end
		writer.write("      </routes>\n");
		writer.write("    </Line>\n");
		writer.write("  </lines>\n");
		//  #set($groupCount = ${line.groupOfLines.size()} )
		//  #if ( $groupCount > 0 )
		if (nonEmpty(line.getGroupOfLines()))
		{
		writer.write("  <groupsOfLines>\n");
		//    #foreach( $group in $line.groupOfLines )
		for (GroupOfLine groupOfLine : line.getGroupOfLines()) {
		writer.write("    <GroupOfLines version=\""+groupOfLine.getObjectVersion()+"\" id=\""+modelTranslator.netexId(groupOfLine)+"\"> \n"); 
		//      #if ( $group.name )
		if (isSet(groupOfLine.getName()))
		writer.write("      <Name>"+toXml(groupOfLine.getName())+"</Name>\n");
		//      #end
		//      #if ( $group.comment )
		if (isSet(groupOfLine.getComment()))
		writer.write("      <Description>"+toXml(groupOfLine.getComment())+"</Description>\n");
		//      #end
		writer.write("    </GroupOfLines>\n");
		}
		//    #end
		writer.write("  </groupsOfLines>\n");
		}
		//  #end
		writer.write("  <!-- NEPTUNE Stop Area with \"Quay\" or \"BoardingPosition\" type  =========================================== -->\n");
		writer.write("  <scheduledStopPoints>\n");
		//    #foreach( $route in $line.routes )
		for (Route route : line.getRoutes()) {
		//      #foreach( $stopPoint in $route.stopPoints )
		for (StopPoint stopPoint : route.getStopPoints()) {
			if (stopPoint == null) continue;
		writer.write("    <ScheduledStopPoint version=\""+stopPoint.getObjectVersion()+"\" id=\""+modelTranslator.netexId(stopPoint)+"\">\n");
		//      #if ( $stopPoint.name )
		if (isSet(stopPoint.getContainedInStopArea().getName()))
		writer.write("      <Name>"+toXml(stopPoint.getContainedInStopArea().getName())+"</Name>\n");
		//      #end
		writer.write("      <TimingPointStatus>timingPoint</TimingPointStatus>\n");
		writer.write("    </ScheduledStopPoint>\n");
		}
		//      #end
		}
		//    #end
		writer.write("  </scheduledStopPoints>\n");
		writer.write("  <!--  SERVICE PATTERN : Service Links are not mapped (they are not mandatory in NeTeEx), the sequence of Scheduled Stop Point is used instead -->\n");
		writer.write("  <servicePatterns>\n");
		//    #foreach( $route in $line.routes )
		for (Route route : line.getRoutes()) {
		//      #foreach( $journeyPattern in $route.journeyPatterns )      
		for (JourneyPattern journeyPattern : route.getJourneyPatterns()) {
		writer.write("    <ServicePattern version=\""+journeyPattern.getObjectVersion()+"\" id=\""+modelTranslator.netexId(journeyPattern)+"\">\n");
		//      #if ( $journeyPattern.comment )
		if (isSet(journeyPattern.getComment())) {
		writer.write("      <keyList>\n");
		writer.write("          <KeyValue>\n");
		writer.write("              <Key>Comment</Key>\n");
		writer.write("              <Value>"+toXml(journeyPattern.getComment())+"</Value>\n");
		writer.write("          </KeyValue>\n");
		writer.write("      </keyList>\n");
		}
		//      #end
		//      #if ( $journeyPattern.name )
		if (isSet(journeyPattern.getName())) 
		writer.write("      <Name>"+toXml(journeyPattern.getName())+"</Name>\n");
		//      #end
		//      #if ( $journeyPattern.publishedName )
		if (isSet(journeyPattern.getPublishedName())) 
		writer.write("      <ShortName>"+toXml(journeyPattern.getPublishedName())+"</ShortName>\n");
		//      #end
		//      #if ( $journeyPattern.registrationNumber )
		if (isSet(journeyPattern.getRegistrationNumber())) 
		writer.write("      <PrivateCode>"+toXml(journeyPattern.getRegistrationNumber())+"</PrivateCode>\n");
		//      #end
		writer.write("      <!-- NEPTUNE [mapping:variable] : ServicePattern->RouteRef mapped to NEPTUNE JourneyPattern->routeId -->\n");
		writer.write("      <RouteRef version=\"1\" ref=\""+modelTranslator.netexId(route)+"\"/>\n");
		writer.write("      <pointsInSequence>\n");
		//        #foreach( $stopPoint in $journeyPattern.stopPoints )
		for (StopPoint stopPoint : journeyPattern.getStopPoints()) {
		writer.write("        <StopPointInJourneyPattern version=\"1\" id=\""+stopPoint.objectIdPrefix()+":StopPointInJourneyPattern:"+stopPoint.objectIdSuffix()+"\">\n");
		writer.write("          <ScheduledStopPointRef version=\""+stopPoint.getObjectVersion()+"\" ref=\""+modelTranslator.netexId(stopPoint)+"\"/>\n");
		writer.write("        </StopPointInJourneyPattern>\n");
		}
		//        #end       
		writer.write("      </pointsInSequence>\n");
		writer.write("    </ServicePattern>\n");
		}
		//      #end    
		}
		//    #end
		writer.write("  </servicePatterns>\n");
		writer.write("\n");
		//  #if ( $connectionLinks && $connectionLinks.size() > 0 )
		Set<ConnectionLink> connectionLinks = data.getConnectionLinks();
		if (nonEmpty(connectionLinks)) {
		writer.write("  <!-- Assignments of Connection Links -->\n");
		writer.write("  <connections>\n");
		//    #foreach( $connectionLink in $connectionLinks )    
		for (ConnectionLink connectionLink : connectionLinks) {
		writer.write("    <SiteConnection id=\""+modelTranslator.netexId(connectionLink)+"\" version=\""+connectionLink.getObjectVersion()+"\">\n");
		//      #if ( $connectionLink.name )
		if (isSet(connectionLink.getName())) 
		writer.write("      <Name>"+toXml(connectionLink.getName())+"</Name>\n");
		//      #end     
		//      #if ( $connectionLink.comment )
		if (isSet(connectionLink.getComment())) 
		writer.write("      <Description>"+toXml(connectionLink.getComment())+"</Description>\n");
		//      #end 
		//      #if ( $connectionLink.linkDistance )
		if (isSet(connectionLink.getLinkDistance())) 
		writer.write("      <Distance>"+connectionLink.getLinkDistance()+"</Distance>\n");
		//      #end 
		//      #if ( $connectionLink.defaultDuration || $connectionLink.frequentTravellerDuration ||
		//            $connectionLink.occasionalTravellerDuration || $connectionLink.mobilityRestrictedTravellerDuration)
		if (isSet(connectionLink.getDefaultDuration(),connectionLink.getFrequentTravellerDuration(),connectionLink.getOccasionalTravellerDuration(),connectionLink.getMobilityRestrictedTravellerDuration())) {
		writer.write("      <TransferDuration>\n");
		//        #if ( $connectionLink.defaultDuration)
		if (isSet(connectionLink.getDefaultDuration())) 
		writer.write("       <DefaultDuration>"+durationFactory.newDuration(connectionLink.getDefaultDuration().getTime())+"</DefaultDuration>\n");
		//        #end
		//        #if ( $connectionLink.frequentTravellerDuration)
		if (isSet(connectionLink.getFrequentTravellerDuration())) 
		writer.write("        <FrequentTravellerDuration>"+durationFactory.newDuration(connectionLink.getFrequentTravellerDuration().getTime())+"</FrequentTravellerDuration>\n");
		//        #end
		//        #if ( $connectionLink.occasionalTravellerDuration)
		if (isSet(connectionLink.getOccasionalTravellerDuration())) 
		writer.write("        <OccasionalTravellerDuration>"+durationFactory.newDuration(connectionLink.getOccasionalTravellerDuration().getTime())+"</OccasionalTravellerDuration>\n");
		//        #end
		//        #if ( $connectionLink.mobilityRestrictedTravellerDuration)
		if (isSet(connectionLink.getMobilityRestrictedTravellerDuration())) 
		writer.write("        <MobilityRestrictedTravellerDuration>"+durationFactory.newDuration(connectionLink.getMobilityRestrictedTravellerDuration().getTime())+"</MobilityRestrictedTravellerDuration>\n"); 
		//        #end
		writer.write("      </TransferDuration>\n");
		}
		//      #end
		//      #if ( $connectionLink.startOfLink)
		if (isSet(connectionLink.getStartOfLink())) {
		writer.write("      <From>\n");
		writer.write("         <StopPlaceRef ref=\""+modelTranslator.netexId(connectionLink.getStartOfLink())+"\" />\n");
		writer.write("      </From> \n");
		}
		//      #end
		//      #if ( $connectionLink.endOfLink)
		if (isSet(connectionLink.getEndOfLink())) {
		writer.write("      <To> \n");        
		writer.write("         <StopPlaceRef ref=\""+modelTranslator.netexId(connectionLink.getEndOfLink())+"\" />\n");
		writer.write("      </To>  \n");
		}
		//      #end
		writer.write("      <navigationPaths>\n");
		writer.write("       <NavigationPath version=\""+connectionLink.getObjectVersion()+"\" id=\""+modelTranslator.netexMockId(connectionLink,"NavigationPath")+"N1\">\n");
		//          #if ( $connectionLink.mobilityRestrictedSuitable)
		if (isSet(connectionLink.getMobilityRestrictedSuitable())) {
		writer.write("          <AccessibilityAssessment version=\""+connectionLink.getObjectVersion()+"\" id=\""+modelTranslator.netexMockId(connectionLink,"AccessibilityAssessment")+"\">\n");
		writer.write("            <MobilityImpairedAccess>"+connectionLink.getMobilityRestrictedSuitable()+"</MobilityImpairedAccess>\n");
		writer.write("          </AccessibilityAssessment>\n");
		}
		//          #end
		//          #if ( $connectionLink.linkType)
		if (isSet(connectionLink.getLinkType())) 
		writer.write("          <Covered>"+modelTranslator.toLinkType(connectionLink.getLinkType())+"</Covered>\n");
		//          #end
		writer.write("        </NavigationPath>\n");
		writer.write("      </navigationPaths>\n");   
		writer.write("    </SiteConnection>\n");
		}
		//    #end
		writer.write("  </connections>\n");
		}
		//  #end
		writer.write("  <!--   -->\n");
		writer.write("  <!-- Tarif zone  : Not really available in NEPTUNE, but there is a need to map Tariff Zone 1 (which is an attribute in NEPTUNE -->\n");
		writer.write("  <!-- This object needs to be created inside the mapping mechanism -->\n");
		List<Integer> tariffs = prepareTariffs(data);
		//  #if ( $tariffs && $tariffs.size() > 0)
		if (nonEmpty(tariffs)) {
		writer.write("  <tariffZones>\n");
		//    #foreach( $tariff in $tariffs) 
		for (Integer tariff : tariffs) {
		writer.write("    <TariffZone version=\"any\" id=\"Local:TariffZone:"+tariff+"\">\n");
		writer.write("      <Name>"+tariff+"</Name>\n");
		writer.write("    </TariffZone>\n");
		}
		//    #end 
		writer.write("  </tariffZones>\n");
		}
		//  #end
		writer.write("  <!--   -->\n");
		writer.write("  <!-- Assignments of Schedules Stop Points to Stop Places -->\n");
		writer.write("  <stopAssignments>\n");
		//  #foreach( $route in $line.routes )
		for (Route route : line.getRoutes()) {
		//    #foreach( $stopPoint in $route.stopPoints )
		for (StopPoint stopPoint : route.getStopPoints()) {
			if (stopPoint == null) continue;
		writer.write("    <PassengerStopAssignment version=\"any\" id=\""+modelTranslator.netexMockId(stopPoint,"PassengerStopAssignment")+"\">\n");
		writer.write("      <ScheduledStopPointRef version=\"1\" ref=\""+modelTranslator.netexId(stopPoint)+"\"/>\n");
		//      #if ($stopPoint.containedInStopArea && $stopPoint.containedInStopArea.parent) 
		if (isSet(stopPoint.getContainedInStopArea())) {
		if (isSet(stopPoint.getContainedInStopArea().getParent()))
		writer.write("      <StopPlaceRef version=\""+stopPoint.getContainedInStopArea().getParent().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(stopPoint.getContainedInStopArea().getParent())+"\"/> \n"); 
		//      #end 
		//      #if ($stopPoint.containedInStopArea) 
		writer.write("      <QuayRef version=\""+stopPoint.getContainedInStopArea().getObjectVersion()+"\" ref=\""+modelTranslator.netexId(stopPoint.getContainedInStopArea())+"\"/>\n");
		}
		//      #end 
		writer.write("    </PassengerStopAssignment> \n");  
		}
		//    #end
		}
		//  #end  
		writer.write("  </stopAssignments>\n");
		writer.write("  <!-- routingConstraintZone : for ITL NEPTUNE-->\n");
		List<StopArea> routingConstraints = line.getRoutingConstraints();
		//  #if ( $routingConstraints && $routingConstraints.size() > 0)
		if (nonEmpty(routingConstraints)) {
		writer.write("  <routingConstraintZones>\n");
		//    #foreach( $routingConstraint in $routingConstraints )     
		for (StopArea routingConstraint : routingConstraints) {
		writer.write("    <RoutingConstraintZone version=\"any\" id=\""+modelTranslator.netexId(routingConstraint)+"\">\n");
		//      #if ( $routingConstraint.name)
		if (isSet(routingConstraint.getName())) 
		writer.write("      <Name>"+toXml(routingConstraint.getName())+"</Name>\n");		
		//      #end
		//      #if ( $routingConstraint.comment)
		if (isSet(routingConstraint.getComment())) 
		writer.write("      <Description>"+toXml(routingConstraint.getComment())+"</Description>\n");
		//      #end
		//      #if ( $routingConstraint.registrationNumber)
		if (isSet(routingConstraint.getRegistrationNumber())) 
		writer.write("      <PrivateCode>"+toXml(routingConstraint.getRegistrationNumber())+"</PrivateCode>\n");
		//      #end
		//      #if ( $routingConstraint.routingConstraintAreas && $routingConstraint.routingConstraintAreas.size() > 0)
		if (nonEmpty(routingConstraint.getRoutingConstraintAreas())) {
		writer.write("      <members>\n");
		//          #foreach( $routingConstraintArea in $routingConstraint.routingConstraintAreas )     
		for (StopArea routingConstraintArea : routingConstraint.getRoutingConstraintAreas()) {
		writer.write("          <ScheduledStopPointRef version=\"1\" ref=\""+modelTranslator.netexId(routingConstraintArea)+"\"/>\n");		
		}
		//          #end
		writer.write("      </members>\n");
		}
		//      #end
		//      #if( ( $routingConstraint.latitude && $routingConstraint.longitude) || ( $routingConstraint.x && $routingConstraint.y && $routingConstraint.projectionType))
		if (routingConstraint.hasCoordinates() || routingConstraint.hasProjection())
		writer.write("      <Centroid>\n");
		writer.write("        <Location id=\""+modelTranslator.netexMockId(routingConstraint,"Location")+"\">\n");
		//          #if ( $routingConstraint.longitude && $routingConstraint.latitude )
		if (routingConstraint.hasCoordinates()) {
		writer.write("          <Longitude>"+routingConstraint.getLongitude()+"</Longitude>\n");
		writer.write("          <Latitude>"+routingConstraint.getLatitude()+"</Latitude>\n");
		}
		//          #end
		//          #if ( $routingConstraint.x && $routingConstraint.y && $routingConstraint.projectionType)
		if (routingConstraint.hasProjection()) {
		writer.write("          <gml:pos srsName=\""+routingConstraint.getProjectionType()+"\">"+routingConstraint.getX()+" "+routingConstraint.getY()+"</gml:pos>\n");
		//          #end
		writer.write("        </Location>\n");
		writer.write("      </Centroid>\n");
		}
		//      #end
		writer.write("      <ZoneUse>cannotBoardAndAlightInSameZone</ZoneUse>\n");
		//      #if ( $routingConstraint.routingConstraintLines && $routingConstraint.routingConstraintLines.size() > 0)
		if (nonEmpty(routingConstraint.getRoutingConstraintLines())) {
		writer.write("      <lines>\n");
		//          #foreach( $routingConstraintLine in $routingConstraint.routingConstraintLines )   
		for (Line routingConstraintLine : routingConstraint.getRoutingConstraintLines()) {
		writer.write("          <LineRef version=\"1\" ref=\""+modelTranslator.netexId(routingConstraintLine)+"\"/>\n");
		}
		//          #end
		writer.write("      </lines>\n");
		}
		//      #end
		writer.write("    </RoutingConstraintZone>\n"); 
	    }
		//    #end
		writer.write("  </routingConstraintZones>\n");
		}
		//  #end
		writer.write("</ServiceFrame>\n");


	}
	
	private static  List<Integer> prepareTariffs(ExportableData collection) {
		List<Integer> tariffs = new ArrayList<Integer>();

		for (StopArea stopArea : collection.getStopAreas()) {
			if (stopArea.getFareCode() != null && !tariffs.contains(stopArea.getFareCode()))
				tariffs.add(stopArea.getFareCode());
		}
		return tariffs;
	}


}
