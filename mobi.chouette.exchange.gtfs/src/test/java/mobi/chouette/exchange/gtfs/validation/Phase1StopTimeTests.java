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
import org.testng.annotations.Test;

@Log4j
public class Phase1StopTimeTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing file" ,priority=101 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "StopTime_1 : missing file" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_1", GTFS_1_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertNull(detail.getSource().getFile().getLineNumber(), "detail must refer no line");
			Assert.assertNull(detail.getSource().getFile().getColumnNumber(), "detail must refer no column");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing column trip_id" ,priority=102 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "StopTime_2_1 : missing column trip_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2_1", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing column stop_id" ,priority=103)
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "StopTime_2_2 : missing column stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2_2", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing column stop_sequence" ,priority=104)
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "StopTime_2_3 : missing column stop_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2_3", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing column arrival_time" ,priority=105)
	public void verifyTest_2_2_4() throws Exception {
		log.info(Color.GREEN + "StopTime_2_4 : missing column arrival_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2_4", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing column departure_time" ,priority=106)
	public void verifyTest_2_2_5() throws Exception {
		log.info(Color.GREEN + "StopTime_2_5 : missing column departure_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_2_5", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "empty column trip_id" ,priority=107 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "StopTime_3_1 : empty column trip_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_3_1", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "empty column stop_id" ,priority=108 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "StopTime_3_2 : empty column stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_3_2", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "empty column stop_sequence" ,priority=109 )
	public void verifyTest_2_3_3() throws Exception {
		log.info(Color.GREEN + "StopTime_3_3 : empty column stop_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_3_3", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing arrival_time with departure_time" ,priority=110 )
	public void verifyTest_2_4_1() throws Exception {
		log.info(Color.GREEN + "StopTime_4_1 : missing arrival_time with departure_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_4_1", GTFS_1_GTFS_Common_15,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "missing departure_time with arrival_time" ,priority=111 )
	public void verifyTest_2_4_2() throws Exception {
		log.info(Color.GREEN + "StopTime_4_2 : missing departure_time with arrival_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_4_2", GTFS_1_GTFS_Common_15,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "duplicate trip_id,stop_sequence" ,priority=112 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "StopTime_5 : duplicate trip_id, stop_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_5", GTFS_2_GTFS_Common_3,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column stop_sequence" ,priority=113 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "StopTime_6 : invalid column stop_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_6", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column arrival_time" ,priority=114 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "StopTime_7 : invalid column arrival_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_7", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column departure_time" ,priority=115 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "StopTime_8 : invalid column departure_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_8", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column pickup_type" ,priority=116 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "StopTime_9 : invalid column pickup_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_9", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column drop_off_type" ,priority=117 )
	public void verifyTest_2_10() throws Exception {
		log.info(Color.GREEN + "StopTime_10 : invalid column drop_off_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_10", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column shape_dist_travel" ,priority=118 )
	public void verifyTest_2_11() throws Exception {
		log.info(Color.GREEN + "StopTime_11 : invalid column shape_dist_travel" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_11", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 StopTime" }, description = "invalid column timepoint" ,priority=119 )
	public void verifyTest_2_12() throws Exception {
		log.info(Color.GREEN + "StopTime_12 : invalid column timepoint" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_12", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(5), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 StopTime" }, description = "extra columns" ,priority=120 )
	public void verifyTest_2_13() throws Exception {
		log.info(Color.GREEN + "StopTime_13 : extra column detected" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_13", GTFS_1_GTFS_Common_11,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 2, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 StopTime" }, description = "empty file" ,priority=121 )
	public void verifyTest_2_14() throws Exception {
		log.info(Color.GREEN + "StopTime_14 : empty file" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stoptime_14", GTFS_1_GTFS_Common_5,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stop_times.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

}
