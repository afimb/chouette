package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReporter.RESULT;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;

@Log4j
public class Phase2StopTimeTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "unknown trip_id" ,priority=350 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "StopTime_1 : unknown trip_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_1", "2-GTFS-StopTime-1",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "unknown stop_id" ,priority=351 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "StopTime_2 : unknown stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2", "2-GTFS-StopTime-2",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "time order" ,priority=352 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "StopTime_3 : time order" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_3", "2-GTFS-StopTime-3",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "missing times on first stop" ,priority=353 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "StopTime_4 : missing times on first stop" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_4", "2-GTFS-StopTime-4",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "missing times on last stop" ,priority=354 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "StopTime_5 : missing times on last stop" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_5", "2-GTFS-StopTime-5",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "time order in trip" ,priority=355 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "StopTime_6 : time order in trip" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_6", "2-GTFS-StopTime-6",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "pickup_type and drop_off_type conflict" ,priority=356 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "StopTime_7 : pickup_type and drop_off_type conflict" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_7", "2-GTFS-StopTime-7",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "shape_dist_travel progression" ,priority=357 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "StopTime_8 : shape_dist_travel progression" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_8", "2-GTFS-StopTime-8",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(39), "detail must refer bad line");
		}
	}
	
	// @Test(groups = { "Phase 2 StopTime" }, description = "invalid stop type" ,priority=358 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "StopTime_9 : invalid stop type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_9", "2-GTFS-StopTime-9",SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
}
