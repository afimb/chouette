package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2StopAreaTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 stop areas" }, description = "child of stoparea type is wrong")
	public void verifyTest_2_StopArea_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-1.xml", "2-NEPTUNE-StopArea-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "child of stoparea type is wrong (StopPlace)")
	public void verifyTest_2_StopArea_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-2.xml", "2-NEPTUNE-StopArea-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "child of stoparea type is wrong (CommercialStopPoint)")
	public void verifyTest_2_StopArea_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-3.xml", "2-NEPTUNE-StopArea-3", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "child of stoparea type is wrong (BoardingPosition)")
	public void verifyTest_2_StopArea_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-4.xml", "2-NEPTUNE-StopArea-4", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "Missing AreaCentroid (StopPlace)")
	public void verifyTest_2_StopArea_5_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-5-1.xml", "2-NEPTUNE-StopArea-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "Missing AreaCentroid (CommercialStopPoint)")
	public void verifyTest_2_StopArea_5_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-5-2.xml", "2-NEPTUNE-StopArea-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "Missing AreaCentroid (BoardingPosition)")
	public void verifyTest_2_StopArea_5_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-5-3.xml", "2-NEPTUNE-StopArea-5", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 stop areas" }, description = "cross references between stoparea and areacentroid")
	public void verifyTest_2_StopArea_6() throws Exception {
		
		verifyValidation( "2-NEPTUNE-StopArea-6.xml", "2-NEPTUNE-StopArea-6", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
