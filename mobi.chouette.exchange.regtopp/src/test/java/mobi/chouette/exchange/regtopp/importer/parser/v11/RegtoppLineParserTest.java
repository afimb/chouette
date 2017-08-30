package mobi.chouette.exchange.regtopp.importer.parser.v11;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Route;
import mobi.chouette.model.ScheduledStopPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.AlightingPossibilityEnum;
import mobi.chouette.model.type.BoardingPossibilityEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegtoppLineParserTest {

	@Test public void testMergeIdenticalRoutesAndPatterns() {

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();

		RegtoppLineParser parser = new RegtoppLineParser();
		
		// Populate referential
		// Add line
		ObjectFactory.getLine(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.LINE_KEY, "1"));
		
		
		createRoute(referential,"1",3,0);
		createRoute(referential,"3",3,3);
		
		
		
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
		ObjectFactory.getLine(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.LINE_KEY, "1"));
		
		
		createRoute(referential,"1",3,0);
		createRoute(referential,"3",4,0);
		
		
		
		parser.deduplicateIdenticalRoutes(referential, configuration);
		parser.deduplicateIdenticalJourneyPatterns(referential, configuration);
	
		Assert.assertEquals(referential.getRoutes().size(), 2);
		Assert.assertEquals(referential.getLines().values().iterator().next().getRoutes().size(), 2);
	}
	
	@Test public void testMergeSimilarRoutes() {

		RegtoppImportParameters configuration = new RegtoppImportParameters();
		Referential referential = new Referential();

		RegtoppLineParser parser = new RegtoppLineParser();
		
		// Populate referential
		// Add line
		ObjectFactory.getLine(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.LINE_KEY, "1"));
		
		
		createRoute(referential,"1",5,0);
		createRoute(referential,"3",5,0,2);
		
		
		
		parser.deduplicateSimilarRoutes(referential, configuration);
	
		Assert.assertEquals(referential.getRoutes().size(), 1);
		Assert.assertEquals(referential.getLines().values().iterator().next().getRoutes().size(), 1);
		Assert.assertEquals(referential.getRoutes().values().iterator().next().getJourneyPatterns().size(), 2);
	}
	
	

	
	private void createRoute(Referential referential,String id, int numStopPoints, int stopPointOffset, int... skipStops) {
		
		Route route = ObjectFactory.getRoute(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.ROUTE_KEY, id));
		route.setLine(referential.getLines().values().iterator().next());
		
		for(int i=stopPointOffset;i<stopPointOffset+numStopPoints;i++) {
			route.getStopPoints().add(createStopPoint(referential,i,i-stopPointOffset,i-stopPointOffset,BoardingPossibilityEnum.normal,AlightingPossibilityEnum.normal));
			
		}
		
		JourneyPattern jp = ObjectFactory.getJourneyPattern(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.JOURNEYPATTERN_KEY, id));
		jp.setRoute(route);
		for(StopPoint sp : route.getStopPoints()) {
			jp.addStopPoint(sp);
		}
		
		jp.setDepartureStopPoint(jp.getStopPoints().get(0));
		jp.setArrivalStopPoint(jp.getStopPoints().get(jp.getStopPoints().size()-1));
		
		
		VehicleJourney vj = ObjectFactory.getVehicleJourney(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.VEHICLEJOURNEY_KEY, id));
	
		vj.setRoute(route);
		vj.setJourneyPattern(jp);
		for(StopPoint sp : route.getStopPoints()) {
			VehicleJourneyAtStop vjS =  ObjectFactory.getVehicleJourneyAtStop();
			vjS.setStopPoint(sp);
			vjS.setVehicleJourney(vj);
		}
		
	}


	private StopPoint createStopPoint(Referential referential, int stopPointId, Integer position, int stopAreaId, BoardingPossibilityEnum forBoarding, AlightingPossibilityEnum forAlighting) {
		StopPoint sp = ObjectFactory.getStopPoint(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.STOPPOINT_KEY, ""+stopPointId));
		sp.setPosition(position);
		ScheduledStopPoint ssp = ObjectFactory.getScheduledStopPoint(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.SCHEDULED_STOP_POINT_KEY, ""+stopPointId));
		ssp.setForAlighting(forAlighting);
		ssp.setForBoarding(forBoarding);
		
		StopArea sa = ObjectFactory.getStopArea(referential, ObjectIdCreator.composeGenericObjectId("XYZ", ObjectIdTypes.STOPAREA_KEY, ""+stopAreaId));
		ssp.setContainedInStopArea(sa);

		sp.setScheduledStopPoint(ssp);

		return sp;
	}

}
