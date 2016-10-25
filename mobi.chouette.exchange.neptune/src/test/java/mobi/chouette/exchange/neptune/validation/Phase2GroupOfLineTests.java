package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase2GroupOfLineTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 group of lines" }, description = "line missing in group of line's list")
	public void verifyTest_2_GroupOfLine_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-GroupOfLine-1.xml", "2-NEPTUNE-GroupOfLine-1", SEVERITY.WARNING, RESULT.NOK);

	}


}
