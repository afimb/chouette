package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2VehicleJourneyTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers unknown Route")
	public void verifyTest_2_VehicleJourney_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-1.xml", "2-NEPTUNE-VehicleJourney-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers unknown JourneyPattern")
	public void verifyTest_2_VehicleJourney_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-2.xml", "2-NEPTUNE-VehicleJourney-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers unknown Line")
	public void verifyTest_2_VehicleJourney_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-3.xml", "2-NEPTUNE-VehicleJourney-3", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers unknown Operator")
	public void verifyTest_2_VehicleJourney_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-4.xml", "2-NEPTUNE-VehicleJourney-4", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers unknown TimeSlot")
	public void verifyTest_2_VehicleJourney_5() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-5.xml", "2-NEPTUNE-VehicleJourney-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers Wrong Route/JourneyPattern")
	public void verifyTest_2_VehicleJourney_6() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-6.xml", "2-NEPTUNE-VehicleJourney-6", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 vehicle journey" }, description = "No VehicleJourney refers JourneyPattern")
	public void verifyTest_2_VehicleJourney_7() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-7.xml", "2-NEPTUNE-VehicleJourney-7", CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK);

	}

	@Test(groups = { "Phase 2 vehicle journey" }, description = "VehicleJourney refers no journeyPattern")
	public void verifyTest_2_VehicleJourney_8() throws Exception {
		
		verifyValidation( "2-NEPTUNE-VehicleJourney-8.xml", "2-NEPTUNE-VehicleJourney-8", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}

}
