/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.model.factory;

import java.util.List;

import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;

/**
 * 
 * @author marc
 */

public class ComplexModelFactoryTest {
	private ComplexModelFactory complexModelFactory;
	private ModelFactory modelFactory;

	@BeforeMethod
	protected void setUp() throws Exception {
		modelFactory = new ModelFactory();
		modelFactory.setRegisterBlueprintsByPackage("mobi.chouette.model.blueprint");
		complexModelFactory = new ComplexModelFactory();
	}

	@Test(groups = { "ComplexModelFactory.nominalRoute" }, description = "Should create as many journeys as requested, the same for stops")
	public void verifyRoute() {
		complexModelFactory.setJourneyPatternCount(2);
		complexModelFactory.setVehicleCount(2);
		complexModelFactory.setRouteStopCount(5);
		complexModelFactory.setQuayCount(5);
		complexModelFactory.init();

		Route route = complexModelFactory.nominalLine("1").getRoutes().get(0);
		Assert.assertEquals(route.getJourneyPatterns().size(), complexModelFactory.getJourneyPatternCount());
		for (JourneyPattern jp : route.getJourneyPatterns()) {
			Assert.assertNotNull(jp.getRoute());
			Assert.assertEquals(jp.getRoute(), route);
		}
		Assert.assertEquals(route.getStopPoints().size(), complexModelFactory.getRouteStopCount());
		for (StopPoint sp : route.getStopPoints()) {
			Assert.assertNotNull(sp.getRoute());
			Assert.assertEquals(sp.getRoute(), route);
		}
	}

	@Test(groups = { "ComplexModelFactory.nominalRoute" }, description = "Should create as 2 timetable for each vehicle")
	public void verifyTimetable() {
		complexModelFactory.init();

		List<VehicleJourney> vehicles = complexModelFactory.nominalLine("1").getRoutes().get(0).getJourneyPatterns()
				.get(0).getVehicleJourneys();
		for (int i = 0; i < vehicles.size(); i++) {
			Assert.assertEquals(vehicles.get(i).getTimetables().size(), 2);
		}
	}

	@Test(groups = { "ComplexModelFactory.nominalLine" }, description = "Should create a network for line")
	public void verifyNetwork() {
		complexModelFactory.init();

		Line l = complexModelFactory.nominalLine("1");
		Network network = l.getNetwork();
		Assert.assertNotNull(network);
		Assert.assertTrue(network.getLines().contains(l));
	}

	@Test(groups = { "ComplexModelFactory.nominalLine" }, description = "Should put line in many groups of lines")
	public void verifyGroupOfLine() {
		complexModelFactory.init();

		Line l = complexModelFactory.nominalLine("1");
		Assert.assertEquals(l.getGroupOfLines().size(),
				complexModelFactory.getGroupOfLinesCount());
		for (GroupOfLine g : l.getGroupOfLines()) {
			Assert.assertTrue(g.getLines().contains(l));
		}
	}

	@Test(groups = { "ComplexModelFactory.nominalRoute" }, description = "Should make a stop point partition and affect each stops part to each journey")
	public void verifyJourneyPatternStopPointCount() {
		int stopCount = 27;
		int journeyPatternCount = 5;
		int vehicleCount = 2;

		complexModelFactory.setJourneyPatternCount(journeyPatternCount);
		complexModelFactory.setVehicleCount(vehicleCount);
		complexModelFactory.setRouteStopCount(stopCount);
		complexModelFactory.setQuayCount(stopCount);
		complexModelFactory.init();

		Route route = complexModelFactory.nominalLine("1").getRoutes().get(0);
		for (int i = 0; i < journeyPatternCount; i++) {
			JourneyPattern jp = route.getJourneyPatterns().get(i);
			int reste = stopCount % journeyPatternCount;
			int quotien = stopCount / journeyPatternCount;
			int jpCount = quotien + (i < reste ? 1 : 0);
			Assert.assertEquals(jp.getStopPoints().size(), jpCount);
		}
	}

	@Test(groups = { "ComplexModelFactory.nominalRoute" }, description = "Should make vehicleJourney referencing the journey pattern")
	public void verifyVehicleJourneysOnJourneyPattern() {
		int stopCount = 27;
		int journeyPatternCount = 5;
		int vehicleCount = 2;

		complexModelFactory.setJourneyPatternCount(journeyPatternCount);
		complexModelFactory.setVehicleCount(vehicleCount);
		complexModelFactory.setRouteStopCount(stopCount);
		complexModelFactory.setQuayCount(stopCount);
		complexModelFactory.init();

		Route route = complexModelFactory.nominalLine("1").getRoutes().get(0);

		for (int i = 0; i < journeyPatternCount; i++) {
			JourneyPattern jp = route.getJourneyPatterns().get(i);
			for (VehicleJourney vj : jp.getVehicleJourneys()) {
				Assert.assertEquals(vj.getJourneyPattern().getObjectId(), jp.getObjectId());
			}
		}
	}

	@Test(groups = { "ComplexModelFactory.nominalRoute" }, description = "Should define same blueprint attribute for wayback")
	public void verifySimpleAttribute() {
		complexModelFactory.init();

		Route route = complexModelFactory.nominalLine("1").getRoutes().get(0);
		Route defaultRoute = null;
		try {
			defaultRoute = modelFactory.createModel(Route.class);
		} catch (CreateModelException ex) {
			Assert.assertTrue(false);
		}
		Assert.assertEquals(route.getWayBack(), defaultRoute.getWayBack());
	}

}
