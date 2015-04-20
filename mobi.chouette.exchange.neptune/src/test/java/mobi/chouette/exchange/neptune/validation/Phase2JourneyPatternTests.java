package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2JourneyPatternTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 journey pattern" }, description = "JourneyPattern refers unknown route")
	public void verifyTest_2_JourneyPattern_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-JourneyPattern-1.xml", "2-NEPTUNE-JourneyPattern-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 journey pattern" }, description = "JourneyPattern refers unknown stopPoint")
	public void verifyTest_2_JourneyPattern_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-JourneyPattern-2.xml", "2-NEPTUNE-JourneyPattern-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 journey pattern" }, description = "JourneyPattern refers unknown Line")
	public void verifyTest_2_JourneyPattern_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-JourneyPattern-3.xml", "2-NEPTUNE-JourneyPattern-3", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
