package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2AccessLinkTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 access link" }, description = "AccessLink refers unknown area")
	public void verifyTest_2_AccessLink_1_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessLink-1-1.xml", "2-NEPTUNE-AccessLink-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access link" }, description = "AccessLink refers unknown access")
	public void verifyTest_2_AccessLink_1_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessLink-1-2.xml", "2-NEPTUNE-AccessLink-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access link" }, description = "AccessLink refers 2 areas")
	public void verifyTest_2_AccessLink_2_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessLink-2-1.xml", "2-NEPTUNE-AccessLink-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access link" }, description = "AccessLink refers 2 access")
	public void verifyTest_2_AccessLink_2_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessLink-2-1.xml", "2-NEPTUNE-AccessLink-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
