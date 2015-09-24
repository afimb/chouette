package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2StopTimeTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "unknown trip_id" ,priority=350 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "StopTime_1 : unknown trip_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_1", "2-GTFS-StopTime-1",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "unknown stop_id" ,priority=351 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "StopTime_2 : unknown stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_2", "2-GTFS-StopTime-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "time order" ,priority=352 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "StopTime_3 : time order" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_3", "2-GTFS-StopTime-3",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "missing times on first stop" ,priority=353 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "StopTime_4 : missing times on first stop" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_4", "2-GTFS-StopTime-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "missing times on last stop" ,priority=354 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "StopTime_5 : missing times on last stop" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_5", "2-GTFS-StopTime-5",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "time order in trip" ,priority=355 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "StopTime_6 : time order in trip" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_6", "2-GTFS-StopTime-6",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "pickup_type and drop_off_type conflict" ,priority=356 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "StopTime_7 : pickup_type and drop_off_type conflict" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_7", "2-GTFS-StopTime-7",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "shape_dist_travel progression" ,priority=357 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "StopTime_8 : shape_dist_travel progression" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_8", "2-GTFS-StopTime-8",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(39), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 StopTime" }, description = "invalid stop type" ,priority=358 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "StopTime_9 : invalid stop type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stoptime_9", "2-GTFS-StopTime-9",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
}
