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
public class Phase2StopTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}

	@Test(groups = { "Phase 2 Stop" }, description = "missing parent_station" ,priority=310 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Stop_1 : missing parent_station" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_1", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 2 Stop" }, description = "wrong parent_station type" ,priority=311 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Stop_2 : wrong parent_station type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_2", GTFS_2_GTFS_Stop_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}


	@Test(groups = { "Phase 2 Stop" }, description = "unused stop" ,priority=312 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Stop_3 : unused stop" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_3", GTFS_2_GTFS_Common_2,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 2, "detail count");
		int count = 0;
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			if (count == 0) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
				// Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
			} else if (count == 1) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
				//Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(101), "detail must refer bad line");
			}
		}
	}

	@Test(groups = { "Phase 2 Stop" }, description = "shared values for stop_name and stop_desc" ,priority=313 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Stop_4 : shared values for stop_name and stop_desc" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_4", GTFS_2_GTFS_Stop_3,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		int count = 0;
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			if (count == 0) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
			} 
		}
	}

	@Test(groups = { "Phase 2 Stop" }, description = "shared url with agency" ,priority=314 )
	public void verifyTest_2_5_1() throws Exception {
		log.info(Color.GREEN + "Stop_5_1 : shared url with agency" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_5_1", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Stop" }, description = "shared url with route" ,priority=315 )
	public void verifyTest_2_5_2() throws Exception {
		log.info(Color.GREEN + "Stop_5_2 : shared url with route" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_5_2", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 2 Stop" }, description = "useless location_type" ,priority=316 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Stop_6 : useless location_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "stop_6", GTFS_2_GTFS_Stop_2,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
}
