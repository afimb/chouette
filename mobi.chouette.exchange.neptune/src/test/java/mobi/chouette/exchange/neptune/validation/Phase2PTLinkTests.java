package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2PTLinkTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 ptlink" }, description = "PtLink refers unknown start stoppoint")
	public void verifyTest_2_PTLink_1_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-PtLink-1-1.xml", "2-NEPTUNE-PtLink-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ptlink" }, description = "PtLink refers unknown end stoppoint")
	public void verifyTest_2_PTLink_1_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-PtLink-1-2.xml", "2-NEPTUNE-PtLink-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
