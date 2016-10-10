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
public class Phase1ShapeTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "missing column shape_id" ,priority=230)
	public void verifyTest_2_1_1() throws Exception {
		log.info(Color.GREEN + "Shape_1_1 : missing column shape_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_1_1", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "missing column shape_pt_lat" ,priority=231)
	public void verifyTest_2_1_2() throws Exception {
		log.info(Color.GREEN + "Shape_1_2 : missing column shape_pt_lat" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_1_2", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "missing column shape_pt_lon" ,priority=232)
	public void verifyTest_2_1_3() throws Exception {
		log.info(Color.GREEN + "Shape_1_3 : missing column shape_pt_lon" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context,"shape_1_3", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "missing column shape_pt_sequence" ,priority=233)
	public void verifyTest_2_1_4() throws Exception {
		log.info(Color.GREEN + "Shape_1_4 : missing column shape_pt_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_1_4", GTFS_1_GTFS_Common_9,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "empty column shape_id" ,priority=234 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Shape_2_1 : empty column shape_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_2_1", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "empty column shape_pt_lat" ,priority=235 )
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Shape_2_2 : empty column shape_pt_lat" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_2_2", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "empty column shape_pt_lon" ,priority=236 )
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "Shape_2_3 : empty column shape_pt_lon" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_2_3", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
		
	@Test(groups = { "Phase 1 Shape" }, description = "empty column shape_pt_sequence" ,priority=237 )
	public void verifyTest_2_2_4() throws Exception {
		log.info(Color.GREEN + "Shape_2_4 : empty column shape_pt_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_2_4", GTFS_1_GTFS_Common_12,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "duplicate shape_id,shape_pt_sequence" ,priority=238 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Shape_3 : duplicate shape_id,shape_pt_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_3", GTFS_2_GTFS_Common_3,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "invalid column shape_pt_lat" ,priority=239 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Shape_4 : invalid column shape_pt_lat" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_4", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "invalid column shape_pt_lon" ,priority=240 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Shape_5 : invalid column shape_pt_lon" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_5", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "invalid column shape_pt_sequence" ,priority=241 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Shape_6 : invalid column shape_pt_sequence" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_6", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	
	@Test(groups = { "Phase 1 Shape" }, description = "invalid column shape_dist_traveled" ,priority=242 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Shape_7 : invalid column shape_dist_traveled" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_7", GTFS_1_GTFS_Common_16,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Shape" }, description = "extra columns" ,priority=243 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Shape_8 : extra column detected" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "shape_8", GTFS_1_GTFS_Common_11,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 2, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "shapes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}


}
