package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase2VehicleJourneyAtStopTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 vehicle journey at stop" }, description = "VehicleJourneyAtStop refers unknown StopPoint")
	public void verifyTest_2_VehicleJourneyAtStop_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourneyAtStop-1.xml", "2-NEPTUNE-VehicleJourneyAtStop-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey at stop" }, description = "VehicleJourneyAtStop refers wrong vehicleJourney")
	public void verifyTest_2_VehicleJourneyAtStop_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourneyAtStop-2.xml", "2-NEPTUNE-VehicleJourneyAtStop-2", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey at stop" }, description = "VehicleJourneyAtStop orders stoppoint against route")
	public void verifyTest_2_VehicleJourneyAtStop_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourneyAtStop-3.xml", "2-NEPTUNE-VehicleJourneyAtStop-3", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey at stop" }, description = "VehicleJourneyAtStop has missing StopPoint against journeypattern")
	public void verifyTest_2_VehicleJourneyAtStop_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourneyAtStop-4.xml", "2-NEPTUNE-VehicleJourneyAtStop-4", SEVERITY.ERROR, RESULT.NOK);

	}


}
