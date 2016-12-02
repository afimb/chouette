package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.apache.log4j.BasicConfigurator;
import org.testng.annotations.Test;

public class Phase2ITLTests extends ValidationTests {

	static {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	@Test(groups = { "Phase 2 ITL" }, description = "StopArea ITL refers StopPoint")
	public void verifyTest_2_ITL_1_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ITL-1-1.xml", "2-NEPTUNE-ITL-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ITL" }, description = "StopArea ITL refers ITL")
	public void verifyTest_2_ITL_1_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ITL-1-2.xml", "2-NEPTUNE-ITL-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ITL" }, description = "StopArea ITL unused")
	public void verifyTest_2_ITL_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ITL-2.xml", "2-NEPTUNE-ITL-2", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ITL" }, description = "ITL refers unknown area")
	public void verifyTest_2_ITL_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ITL-3.xml", "2-NEPTUNE-ITL-3", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ITL" }, description = "ITL refers non ITL area")
	public void verifyTest_2_ITL_4() throws Exception {
		
		//verifyValidation( "2-NEPTUNE-ITL-4.xml", "2-NEPTUNE-ITL-4", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 ITL" }, description = "ITL refers wrong line")
	public void verifyTest_2_ITL_5() throws Exception {
		
		verifyValidation( "2-NEPTUNE-ITL-5.xml", "2-NEPTUNE-ITL-5", SEVERITY.ERROR, RESULT.NOK);

	}

}
