package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2ConnectionLinkTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 connection link" }, description = "ConnectionLink refers 2 unknown areas")
	public void verifyTest_2_ConnectionLink_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ConnectionLink-1.xml", "2-NEPTUNE-ConnectionLink-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
