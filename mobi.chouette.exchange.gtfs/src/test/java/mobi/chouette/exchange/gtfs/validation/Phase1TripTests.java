package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase1TripTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "missing file" ,priority=81 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Trip_1 : missing file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_1", GTFS_1_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertNull(detail.getSource().getFile().getLineNumber(), "detail must refer no line");
			Assert.assertNull(detail.getSource().getFile().getColumnNumber(), "detail must refer no column");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "missing column trip_id" ,priority=82 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Trip_2_1 : missing column trip_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_2_1", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "missing column route_id" ,priority=83)
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Trip_2_2 : missing column route_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_2_2", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "missing column service_id" ,priority=84)
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "Trip_2_3 : missing column service_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_2_3", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "empty column trip_id" ,priority=85 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "Trip_3_1 : empty column trip_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_3_1", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "empty column route_id" ,priority=86 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "Trip_3_2 : empty column route_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_3_2", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "empty column service_id" ,priority=87 )
	public void verifyTest_2_3_3() throws Exception {
		log.info(Color.GREEN + "Trip_3_3 : empty column service_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_3_3", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "duplicate trip_id" ,priority=88 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Trip_4 : duplicate trip_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_4", GTFS_1_GTFS_Common_8,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "invalid column direction_id" ,priority=89 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Trip_5 : invalid column direction_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_5", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "invalid column wheelchair_accessible" ,priority=90 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Trip_6 : invalid column wheelchair_accessible" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_6", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Trip" }, description = "invalid column bike_allowed" ,priority=91 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Trip_7 : invalid column bike_allowed" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_7", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Trip" }, description = "extra columns" ,priority=92 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Trip_8 : extra column detected" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_8", GTFS_1_GTFS_Common_11,CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Trip" }, description = "empty file" ,priority=93 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "Trip_9 : empty file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "trip_9", GTFS_1_GTFS_Common_5,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

}
