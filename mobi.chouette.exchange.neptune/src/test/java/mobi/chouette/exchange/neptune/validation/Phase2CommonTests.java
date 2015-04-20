package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2CommonTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 common" }, description = "good line")
	public void verifyTest_0_OK() throws Exception {
		
		verifyValidation( "Ligne_OK.xml", "NONE",null , CheckPoint.RESULT.OK);

	}

	@Test(groups = { "Phase 2 common" }, description = "shareable data are identical : network")
	public void verifyTest_2_Common_1_1() throws Exception {
		
		verifyCrossValidation( "2-NEPTUNE-Common-1-1.zip", "2-NEPTUNE-Common-1", CheckPoint.RESULT.NOK);

	}

	@Test(groups = { "Phase 2 common" }, description = "shareable data are identical : company")
	public void verifyTest_2_Common_1_2() throws Exception {
		
		verifyCrossValidation( "2-NEPTUNE-Common-1-2.zip", "2-NEPTUNE-Common-1", CheckPoint.RESULT.NOK);

	}

	@Test(groups = { "Phase 2 common" }, description = "shareable data are identical : group of lines")
	public void verifyTest_2_Common_1_3() throws Exception {
		
		verifyCrossValidation( "2-NEPTUNE-Common-1-3.zip", "2-NEPTUNE-Common-1", CheckPoint.RESULT.NOK);

	}

	@Test(groups = { "Phase 2 common" }, description = "shareable data are identical : company")
	public void verifyTest_2_Common_2() throws Exception {
		
		verifyCrossValidation( "2-NEPTUNE-Common-2.zip", "2-NEPTUNE-Common-2", CheckPoint.RESULT.NOK);

	}



}
