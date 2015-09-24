package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2TimetableTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 timetable" }, description = "Timetable refers none of present VehicleJourneys")
	public void verifyTest_2_Timetable_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Timetable-1.xml", "2-NEPTUNE-Timetable-1", CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 timetable" }, description = "VehicleJourney has no Timetable")
	public void verifyTest_2_Timetable_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Timetable-2.xml", "2-NEPTUNE-Timetable-2", CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 timetable" }, description = "invalid period")
	public void verifyTest_2_Timetable_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Timetable-3.xml", "2-NEPTUNE-Timetable-3", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
