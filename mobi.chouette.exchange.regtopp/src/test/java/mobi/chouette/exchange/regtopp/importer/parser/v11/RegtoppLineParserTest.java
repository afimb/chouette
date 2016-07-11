package mobi.chouette.exchange.regtopp.importer.parser.v11;

import java.sql.Time;

import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

public class RegtoppLineParserTest {

	@Test public void testCalculateDepartureTimeBeforeMidnight() {
		
		Duration tripDepartureTime = new Duration(60*1000); // at time 0001
		Duration timeSinceTripDepatureTime = new Duration(36*60*1000);
		
		Time visitTime = RegtoppLineParser.calculateTripVisitTime(tripDepartureTime, timeSinceTripDepatureTime);
		
		Assert.assertEquals(0,visitTime.getHours());
		Assert.assertEquals(37,visitTime.getMinutes());
	}

	@Test public void testCalculateDepartureTimeAfterMidnight() {
		
		Duration tripDepartureTime = new Duration(24*60*60*1000+60*1000); // at time 2401 
		Duration timeSinceTripDepatureTime = new Duration(36*60*1000);
		
		Time visitTime = RegtoppLineParser.calculateTripVisitTime(tripDepartureTime, timeSinceTripDepatureTime);
		
		Assert.assertEquals(0,visitTime.getHours());
		Assert.assertEquals(37,visitTime.getMinutes());
	}
	

	@Test public void testMergeIdenticalRoutesAndPatterns() {

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();

		RegtoppLineParser parser = new RegtoppLineParser();
		
		// Populate referential
		// Add line
		ObjectFactory.getLine(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.LINE_KEY, "1"));
		
		
		createRouteType1(referential,"1",3,0);
		createRouteType1(referential,"3",3,3);
		
		
		
		parser.deduplicateIdenticalRoutes(referential, configuration);
		parser.deduplicateIdenticalJourneyPatterns(referential, configuration);
	
		Assert.assertEquals(referential.getRoutes().size(), 1);
		Assert.assertEquals(referential.getLines().values().iterator().next().getRoutes().size(), 1);
		Assert.assertEquals(referential.getRoutes().values().iterator().next().getJourneyPatterns().size(), 1);
	}
	
	
	@Test public void testDoNotMergeDifferentRoutes() {

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();

		RegtoppLineParser parser = new RegtoppLineParser();
		
		// Populate referential
		// Add line
		ObjectFactory.getLine(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.LINE_KEY, "1"));
		
		
		createRouteType1(referential,"1",3,0);
		createRouteType1(referential,"3",4,0);
		
		
		
		parser.deduplicateIdenticalRoutes(referential, configuration);
		parser.deduplicateIdenticalJourneyPatterns(referential, configuration);
	
		Assert.assertEquals(referential.getRoutes().size(), 2);
		Assert.assertEquals(referential.getLines().values().iterator().next().getRoutes().size(), 2);
	}
	
	
	private void createRouteType1(Referential referential,String id, int numStopPoints, int stopPointOffset) {
		
		Route route = ObjectFactory.getRoute(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.ROUTE_KEY, id));
		route.setLine(referential.getLines().values().iterator().next());
		
		for(int i=stopPointOffset;i<stopPointOffset+numStopPoints;i++) {
			route.getStopPoints().add(createStopPoint(referential,i,i-stopPointOffset,i-stopPointOffset,BoardingPossibilityEnum.normal,AlightingPossibilityEnum.normal));
			
		}
		
		JourneyPattern jp = ObjectFactory.getJourneyPattern(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.JOURNEYPATTERN_KEY, id));
		jp.setRoute(route);
		for(StopPoint sp : route.getStopPoints()) {
			jp.addStopPoint(sp);
		}
		
		VehicleJourney vj = ObjectFactory.getVehicleJourney(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.VEHICLEJOURNEY_KEY, id));
	
		vj.setRoute(route);
		vj.setJourneyPattern(jp);
		for(StopPoint sp : route.getStopPoints()) {
			VehicleJourneyAtStop vjS =  ObjectFactory.getVehicleJourneyAtStop();
			vjS.setStopPoint(sp);
			vjS.setVehicleJourney(vj);
		}
		
	}


	private StopPoint createStopPoint(Referential referential, int stopPointId, Integer position, int stopAreaId, BoardingPossibilityEnum forBoarding, AlightingPossibilityEnum forAlighting) {
		StopPoint sp = ObjectFactory.getStopPoint(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.STOPPOINT_KEY, ""+stopPointId));
		
		sp.setPosition(position);
		sp.setForAlighting(forAlighting);
		sp.setForBoarding(forBoarding);
		
		StopArea sa = ObjectFactory.getStopArea(referential, AbstractConverter.composeObjectId("XYZ", ObjectIdTypes.STOPAREA_KEY, ""+stopAreaId));
		sp.setContainedInStopArea(sa);
		
		return sp;
	}

}
