package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase2LineTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown network")
	public void verifyTest_2_Line_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-1.xml", "2-NEPTUNE-Line-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown lineEnd")
	public void verifyTest_2_Line_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-2.xml", "2-NEPTUNE-Line-2", SEVERITY.WARNING, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers non lineEnd stopPoint")
	public void verifyTest_2_Line_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-3.xml", "2-NEPTUNE-Line-3", SEVERITY.WARNING, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown route")
	public void verifyTest_2_Line_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-4.xml", "2-NEPTUNE-Line-4", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Route unrefered by line")
	public void verifyTest_2_Line_5() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-5.xml", "2-NEPTUNE-Line-5", SEVERITY.ERROR, RESULT.NOK);

	}

}
