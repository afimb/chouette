package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase2NetworkTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 network" }, description = "line missing in network's list")
	public void verifyTest_2_Network_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Network-1.xml", "2-NEPTUNE-Network-1",SEVERITY.WARNING, RESULT.NOK);

	}

	@Test(groups = { "Phase 2 network" }, description = "invalid sourceType")
	public void verifyTest_2_Network_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Network-2.xml", "2-NEPTUNE-Network-2",SEVERITY.WARNING, RESULT.NOK);

	}


}
