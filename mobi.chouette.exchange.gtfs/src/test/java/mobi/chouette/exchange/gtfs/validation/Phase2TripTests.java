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
public class Phase2TripTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 Trip" }, description = "unknown route_id" ,priority=340 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Trip_1 : unknown route_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_1", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Trip" }, description = "unknown service_id" ,priority=341 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Trip_2 : unknown service_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_2", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Trip" }, description = "unknown shape_id with shapes.txt missing" ,priority=342 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "Trip_3 : unknown shape_id with shapes.txt missing" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_3_1", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
// TODO faire le jeu de données

	@Test(groups = { "Phase 2 Trip" }, description = "unknown shape_id with shapes.txt present" ,priority=343 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "Trip_3 : unknown shape_id with shapes.txt present" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_3_2", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Trip" }, description = "isolate block_id" ,priority=344 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Trip_4 : isolate block_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_4", "2-GTFS-Trip-4",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 Trip" }, description = "unused trip_id" ,priority=345 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Trip_5 : unused trip_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_5", "2-GTFS-Trip-5",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 Trip" }, description = "too short trip" ,priority=346 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Trip_6 : too short trip" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_6", "2-GTFS-Trip-6",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 Trip" }, description = "missing direction_id" ,priority=347 )
	public void verifyTest_2_7_1() throws Exception {
		log.info(Color.GREEN + "Trip_7 : direction_id should be present" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_7_1", "2-GTFS-Trip-7",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Trip" }, description = "missing direction_id values" ,priority=348 )
	public void verifyTest_2_7_2() throws Exception {
		log.info(Color.GREEN + "Trip_7 : direction_id should be filed" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "trip_7_2", "2-GTFS-Trip-7",SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
}
