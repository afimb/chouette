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
public class Phase1CommonTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1" }, description = "invalid csv syntax" ,priority=11 )
	public void verifyTest_1_1() throws Exception {
		log.info(Color.GREEN + "CSV_1 : invalid csv syntax" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "csv_1_1", GTFS_1_GTFS_CSV_2,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1" }, description = "newline in field", priority=12)
	public void verifyTest_1_2() throws Exception {
		log.info(Color.GREEN + "CSV_1 : newline in field" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"csv_1_2", GTFS_1_GTFS_CSV_5,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1" }, description = "double quote in field", priority=13)
	public void verifyTest_1_3() throws Exception {
		log.info(Color.GREEN + "CSV_1 : double quote in field" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"csv_1_3", GTFS_1_GTFS_CSV_5,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1" }, description = "space in field ends", priority=14)
	public void verifyTest_1_4() throws Exception {
		log.info(Color.GREEN + "CSV_1 : space in field ends" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"csv_1_4", GTFS_1_GTFS_CSV_7,SEVERITY.WARNING, RESULT.NOK,true);
		
		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1" }, description = "missing LF at end of file", priority=15)
	public void verifyTest_1_5() throws Exception {
		log.info(Color.GREEN + "CSV_1 : missing LF at end of file" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"csv_1_5", GTFS_1_GTFS_CSV_5,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	// WHAT IS THE PROBLEM HERE ????
	//@Test(groups = { "Phase 1" }, description = "invalid enconding", priority=16)
	public void verifyTest_1_6() throws Exception {
		log.info(Color.GREEN + "CSV_1 : invalid enconding" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "csv_1_6", GTFS_1_GTFS_CSV_5,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1" }, description = "empty header name", priority=17)
	public void verifyTest_1_7() throws Exception {
		log.info(Color.GREEN + "CSV_1 : empty header name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "csv_1_7", GTFS_1_GTFS_CSV_3,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1" }, description = "duplicate header name", priority=18)
	public void verifyTest_1_8() throws Exception {
		log.info(Color.GREEN + "CSV_1 : duplicate header name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "csv_1_8", GTFS_1_GTFS_CSV_4,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1" }, description = "HTML Tag in header", priority=19)
	public void verifyTest_1_9() throws Exception {
		log.info(Color.GREEN + "CSV_1 : HTML Tag in header" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"csv_1_9", GTFS_1_GTFS_CSV_6,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

}
