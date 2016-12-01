package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase2StopPointTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 stop point" }, description = "StopPoint refers unknown Line")
	public void verifyTest_2_StopPoint_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopPoint-1.xml", "2-NEPTUNE-StopPoint-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop point" }, description = "StopPoint refers unknown Network")
	public void verifyTest_2_StopPoint_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopPoint-2.xml", "2-NEPTUNE-StopPoint-2", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop point" }, description = "StopPoint refers unknown Parent")
	public void verifyTest_2_StopPoint_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopPoint-3.xml", "2-NEPTUNE-StopPoint-3", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop point" }, description = "StopPoint uses invalid geographical referential (Standard)")
	public void verifyTest_2_StopPoint_4_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopPoint-4-1.xml", "2-NEPTUNE-StopPoint-4", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop point" }, description = "StopPoint uses invalid geographical referential (WGS92)")
	public void verifyTest_2_StopPoint_4_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopPoint-4-2.xml", "2-NEPTUNE-StopPoint-4", SEVERITY.ERROR, RESULT.NOK);

	}


}
