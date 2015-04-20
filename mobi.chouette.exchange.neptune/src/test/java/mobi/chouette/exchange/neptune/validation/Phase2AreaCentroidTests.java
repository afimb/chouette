package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.Test;

public class Phase2AreaCentroidTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 area centroid" }, description = "AreaCentroid refers unknown area")
	public void verifyTest_2_AreaCentroid_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AreaCentroid-1.xml", "2-NEPTUNE-AreaCentroid-1", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 area centroid" }, description = "AreaCentroid uses invalid LongLatType")
	public void verifyTest_2_AreaCentroid_2_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AreaCentroid-2-1.xml", "2-NEPTUNE-AreaCentroid-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	@Test(groups = { "Phase 2 area centroid" }, description = "AreaCentroid uses invalid LongLatType")
	public void verifyTest_2_AreaCentroid_2_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-AreaCentroid-2-2.xml", "2-NEPTUNE-AreaCentroid-2", CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}


}
