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
public class Phase1TransferTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "missing column from_stop_id" ,priority=210)
	public void verifyTest_2_1_1() throws Exception {
		log.info(Color.GREEN + "Transfer_1_1 : missing column from_stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_1_1", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "missing column to_stop_id" ,priority=211)
	public void verifyTest_2_1_2() throws Exception {
		log.info(Color.GREEN + "Transfer_1_2 : missing column to_stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_1_2", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "missing column transfer_type" ,priority=212)
	public void verifyTest_2_1_3() throws Exception {
		log.info(Color.GREEN + "Transfer_1_3 : missing column transfer_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_1_3", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	
	@Test(groups = { "Phase 1 Transfer" }, description = "empty column from_stop_id" ,priority=213 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Transfer_2_1 : empty column from_stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_2_1", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "empty column to_stop_id" ,priority=214 )
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Transfer_2_2 : empty column to_stop_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_2_2", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "empty column transfer_type" ,priority=215 )
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "Transfer_2_3 : empty column transfer_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_2_3", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
		
	@Test(groups = { "Phase 1 Transfer" }, description = "invalid column transfer_type" ,priority=216 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Transfer_3 : invalid column transfer_type" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_3", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "missing column min_transfer_time" ,priority=217 )
	public void verifyTest_2_4_1() throws Exception {
		log.info(Color.GREEN + "Transfer_4_1 : missing column min_transfer_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_4_1", GTFS_1_GTFS_Common_15,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 5, "detail count");
		int i = 2;
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(i++), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "empty column min_transfer_time" ,priority=218 )
	public void verifyTest_2_4_2() throws Exception {
		log.info(Color.GREEN + "Transfer_4_2 : missing column min_transfer_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_4_2", GTFS_1_GTFS_Common_15,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	
	
	@Test(groups = { "Phase 1 Transfer" }, description = "invalid column min_transfer_time" ,priority=219 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Transfer_5 : invalid column min_transfer_time" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_5", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Transfer" }, description = "extra columns" ,priority=220 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Transfer_6 : extra column detected" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "transfer_6", GTFS_1_GTFS_Common_11,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 2, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}


}
