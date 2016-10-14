package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.annotations.Test;

public class Phase1Tests extends ValidationTests {

	
	@Test(groups = { "Phase 1" }, description = "incomplete xml")
	public void verifyTest_1_1_1() throws Exception {
		
		verifySaxValidation( "1-NEPTUNE-XML-1_1.xml", "1-NEPTUNE-XML-1",SEVERITY.ERROR, RESULT.NOK);

	}

	@Test(groups = { "Phase 1" }, description = "broken tag")
	public void verifyTest_1_1_2() throws Exception {
		
		verifySaxValidation( "1-NEPTUNE-XML-1_2.xml", "1-NEPTUNE-XML-1",SEVERITY.ERROR,  RESULT.NOK);

	}

	@Test(groups = { "Phase 1" }, description = "missing mandatory tag")
	public void verifyTest_1_2_1() throws Exception {
		
		verifySaxValidation( "1-NEPTUNE-XML-2_1.xml", "1-NEPTUNE-XML-2",SEVERITY.ERROR,  RESULT.NOK);

	}



}
