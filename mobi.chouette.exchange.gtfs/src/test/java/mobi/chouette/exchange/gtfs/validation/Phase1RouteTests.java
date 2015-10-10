package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase1RouteTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 Route" }, description = "missing file" ,priority=61 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Route_1 : missing file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_1", GTFS_1_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertNull(detail.getSource().getFile().getLineNumber(), "detail must refer no line");
			Assert.assertNull(detail.getSource().getFile().getColumnNumber(), "detail must refer no column");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "missing column route_id" ,priority=62 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Route_2_1 : missing column route_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_2_1", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "missing column route_type" ,priority=63)
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Route_2_2 : missing column route_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_2_2", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "empty column route_id" ,priority=64 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "Route_3_1 : empty column route_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_3_1", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Route" }, description = "empty column route_type" ,priority=65 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "Route_3_2 : empty column route_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_3_2", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "missing route_short_name and route_long_name" ,priority=66 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Route_4 : missing route_short_name and route_long_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_4", GTFS_1_GTFS_Route_2,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(5), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "duplicate route_id" ,priority=67 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Route_5 : duplicate route_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_5", GTFS_1_GTFS_Common_8,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "invalid column route_type" ,priority=68 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Route_6 : invalid column route_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_6", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "invalid column route_url" ,priority=69 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Route_7 : invalid column route_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_7", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "invalid column route_color" ,priority=70 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Route_8 : invalid column route_color" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_8", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 1 Route" }, description = "invalid column route_text_color" ,priority=71 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "Route_9 : invalid column route_text_color" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_9", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Route" }, description = "extra columns" ,priority=72 )
	public void verifyTest_2_10() throws Exception {
		log.info(Color.GREEN + "Route_10 : extra column detected" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_10", GTFS_1_GTFS_Common_11,CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Route" }, description = "empty file" ,priority=73 )
	public void verifyTest_2_11() throws Exception {
		log.info(Color.GREEN + "Route_11 : empty file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "route_11", GTFS_1_GTFS_Common_5,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
}
