package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase1StopTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}

	@Test(groups = { "Phase 1 Stop" }, description = "missing file" ,priority=41 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Stop_1 : missing file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_1", GTFS_1_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertNull(detail.getSource().getFile().getLineNumber(), "detail must refer no line");
			Assert.assertNull(detail.getSource().getFile().getColumnNumber(), "detail must refer no column");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "missing column stop_id" ,priority=42 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Stop_2_1 : missing column stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_2_1", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "missing column stop_name" ,priority=43)
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Stop_2_2 : missing column stop_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_2_2", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "missing column stop_lat" ,priority=44 )
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "Stop_2_3 : missing column stop_lat" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_2_3", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "missing column stop_long" ,priority=45)
	public void verifyTest_2_2_4() throws Exception {
		log.info(Color.GREEN + "Stop_2_4 : missing column stop_long" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_2_4", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "empty column stop_id" ,priority=46 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "Stop_2 : empty column stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_3_1", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(5), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "empty column stop_name" ,priority=47 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "Stop_3_2 : empty column stop_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_3_2", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "empty column stop_lat" ,priority=48)
	public void verifyTest_2_3_3() throws Exception {
		log.info(Color.GREEN + "Stop_3_3 : empty column stop_lat" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_3_3", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "empty column stop_lon" ,priority=49 )
	public void verifyTest_2_3_4() throws Exception {
		log.info(Color.GREEN + "Stop_3_4 : empty column stop_lon" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_3_4", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "duplicate stop_id" ,priority=50 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Stop_4 : duplicate stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_4", GTFS_1_GTFS_Common_8,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column stop_lat" ,priority=51 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Stop_5 : invalid column stop_lat" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_5", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column stop_lon" ,priority=52 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Stop_6 : invalid column stop_lon" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_6", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column stop_url" ,priority=53 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Stop_7 : invalid column stop_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_7", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column location_type" ,priority=54 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Stop_8 : invalid column location_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_8", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column stop_timezone" ,priority=55 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "Stop_9 : invalid column stop_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_9", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "invalid column wheelchair_boarding" ,priority=56 )
	public void verifyTest_2_10() throws Exception {
		log.info(Color.GREEN + "Stop_10 : invalid column wheelchair_boarding" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_10", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "extra columns" ,priority=57 )
	public void verifyTest_2_11() throws Exception {
		log.info(Color.GREEN + "Stop_11 : extra column detected" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_11", GTFS_1_GTFS_Common_11,CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Stop" }, description = "empty file" ,priority=58 )
	public void verifyTest_2_12() throws Exception {
		log.info(Color.GREEN + "Stop_12 : empty file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_12", GTFS_1_GTFS_Common_5,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
}
