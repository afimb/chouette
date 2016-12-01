package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Phase2RouteTests extends ValidationTests {

	
	@Test(groups = { "Phase 2 route" }, description = "Route refers unknown journey pattern")
	public void verifyTest_2_Route_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-1.xml", "2-NEPTUNE-Route-1", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "Route refers unknown ptlink")
	public void verifyTest_2_Route_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-2.xml", "2-NEPTUNE-Route-2", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "Route refers unknown wayback")
	public void verifyTest_2_Route_3() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-3.xml", "2-NEPTUNE-Route-3", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "Route share ptlink")
	public void verifyTest_2_Route_4() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-4.xml", "2-NEPTUNE-Route-4", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "PtLink share start of link")
	public void verifyTest_2_Route_5_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-5-1.xml", "2-NEPTUNE-Route-5", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "PtLink share end of link")
	public void verifyTest_2_Route_5_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-5-2.xml", "2-NEPTUNE-Route-5", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "circle route")
	public void verifyTest_2_Route_6_1() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-6-1.xml", "2-NEPTUNE-Route-6", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "broken route")
	public void verifyTest_2_Route_6_2() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-6-2.xml", "2-NEPTUNE-Route-6", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "Unreferenced journeypattern")
	public void verifyTest_2_Route_7() throws Exception {
		
		verifyValidation( "2-NEPTUNE-Route-7.xml", "2-NEPTUNE-Route-7", SEVERITY.ERROR, RESULT.NOK);

	}
	@Test(groups = { "Phase 2 route" }, description = "journeypattern dont use route's stoppoints")
	public void verifyTest_2_Route_8() throws Exception {
		
		CheckPointErrorReport detail = verifyValidation( "2-NEPTUNE-Route-8.xml", "2-NEPTUNE-Route-8", SEVERITY.ERROR, RESULT.NOK);
        Assert.assertEquals(detail.getTargets().size(), 1, "targets size");

	}
	@Test(groups = { "Phase 2 route" }, description = "unused stoppoint")
	public void verifyTest_2_Route_9() throws Exception {
		
		CheckPointErrorReport detail = verifyValidation( "2-NEPTUNE-Route-9.xml", "2-NEPTUNE-Route-9", SEVERITY.WARNING, RESULT.NOK);
        Assert.assertEquals(detail.getTargets().size(), 1, "targets size");

	}
	@Test(groups = { "Phase 2 route" }, description = "unused stoppoint")
	public void verifyTest_2_Route_10() throws Exception {
		
		CheckPointErrorReport detail = verifyValidation( "2-NEPTUNE-Route-10.xml", "2-NEPTUNE-Route-10", SEVERITY.ERROR, RESULT.NOK);
        Assert.assertEquals(detail.getTargets().size(), 1, "targets size");

	}
	@Test(groups = { "Phase 2 route" }, description = "unused stoppoint")
	public void verifyTest_2_Route_11() throws Exception {
		
		CheckPointErrorReport detail = verifyValidation( "2-NEPTUNE-Route-11.xml", "2-NEPTUNE-Route-11", SEVERITY.WARNING, RESULT.NOK);
        Assert.assertEquals(detail.getTargets().size(), 1, "targets size");

	}
	@Test(groups = { "Phase 2 route" }, description = "unused stoppoint")
	public void verifyTest_2_Route_12() throws Exception {
		
		CheckPointErrorReport detail = verifyValidation( "2-NEPTUNE-Route-12.xml", "2-NEPTUNE-Route-12", SEVERITY.WARNING, RESULT.NOK);
        Assert.assertEquals(detail.getTargets().size(), 1, "targets size");
		
	}


}
