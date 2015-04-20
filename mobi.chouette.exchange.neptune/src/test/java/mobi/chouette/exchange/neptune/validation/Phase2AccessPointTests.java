package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2AccessPointTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint refers unknown area")
	public void verifyTest_2_AccessPoint_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-1.xml", "2-NEPTUNE-AccessPoint-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint refers ITL Area")
	public void verifyTest_2_AccessPoint_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-2.xml", "2-NEPTUNE-AccessPoint-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint has no AccessLink")
	public void verifyTest_2_AccessPoint_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-3.xml", "2-NEPTUNE-AccessPoint-3", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint In with out links")
	public void verifyTest_2_AccessPoint_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-4.xml", "2-NEPTUNE-AccessPoint-4", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint Out with in links")
	public void verifyTest_2_AccessPoint_5() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-5.xml", "2-NEPTUNE-AccessPoint-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint InOut without in links")
	public void verifyTest_2_AccessPoint_6_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-6-1.xml", "2-NEPTUNE-AccessPoint-6", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint InOut without out links")
	public void verifyTest_2_AccessPoint_6_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-6-2.xml", "2-NEPTUNE-AccessPoint-6", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 access point" }, description = "AccessPoint With wrong referential type")
	public void verifyTest_2_AccessPoint_7() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AccessPoint-7.xml", "2-NEPTUNE-AccessPoint-7", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
