package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2LineTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown network")
	public void verifyTest_2_Line_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-1.xml", "2-NEPTUNE-Line-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown lineEnd")
	public void verifyTest_2_Line_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-2.xml", "2-NEPTUNE-Line-2", CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers non lineEnd stopPoint")
	public void verifyTest_2_Line_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-3.xml", "2-NEPTUNE-Line-3", CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Line refers unknown route")
	public void verifyTest_2_Line_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-4.xml", "2-NEPTUNE-Line-4", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 line" }, description = "Route unrefered by line")
	public void verifyTest_2_Line_5() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Line-5.xml", "2-NEPTUNE-Line-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}

}
