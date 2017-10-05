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
public class Phase2RouteTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "shared values for route_short_name and route_long_name" ,priority=320 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Route_1 : shared values for route_short_name and route_long_name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_1", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "missing value agency_id is ok" ,priority=321 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Route_2_1 : missing value agency_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_2_1", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.OK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 0, "detail count");
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "missing column agency_id is ok" ,priority=322 )
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Route_2_2 : missing column agency_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_2_2", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.OK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 0, "detail count");
	}

	@Test(groups = { "Phase 2 Route" }, description = "unknown agency_id" ,priority=323 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Route_3 : unknown agency_id" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_3", GTFS_2_GTFS_Common_1,SEVERITY.ERROR, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "unused route" ,priority=324 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Route_4 : unused route" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_4", GTFS_2_GTFS_Common_2,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 2, "detail count");
		int count = 0;
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			if (count == 0) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
				//Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(100), "detail must refer bad line");
			} else if (count == 1) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getFilename(), "trips.txt", "detail must refer bad file");
				// Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
			}
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "duplicate route names" ,priority=325 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "Route_5 : duplicate route names" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_5", GTFS_2_GTFS_Route_1,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "route_long_name includes route_short_name" ,priority=326 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Route_8 : route_long_name includes route_short_name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_8", GTFS_2_GTFS_Route_2,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "color contrast" ,priority=327 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "Route_9 : color contrast" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_9", GTFS_2_GTFS_Route_3,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 5, "detail count");
		int count = 0;
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			if (count == 0) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
			} else if (count == 1) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
			} else if (count == 2) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(6), "detail must refer bad line");
			} else if (count == 3) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(7), "detail must refer bad line");
			} else if (count == 4) {
				count++;
				Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(8), "detail must refer bad line");
			} 
		}
	}

	@Test(groups = { "Phase 2 Route" }, description = "disctinct route_desc from route_short_name" ,priority=328 )
	public void verifyTest_2_10_1() throws Exception {
		log.info(Color.GREEN + "Route_10_1 : disctinct route_desc from route_short_name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_10_1", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Route" }, description = "disctinct route_desc from route_long_name" ,priority=329 )
	public void verifyTest_2_10_2() throws Exception {
		log.info(Color.GREEN + "Route_10_2 : disctinct route_desc from route_long_name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_10_2", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 2 Route" }, description = "reverse combination short and long name" ,priority=330 )
	public void verifyTest_2_11() throws Exception {
		log.info(Color.GREEN + "Route_11 : reverse combination short and long name" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_11", GTFS_2_GTFS_Route_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 2 Route" }, description = "url shared with agency" ,priority=331 )
	public void verifyTest_2_12() throws Exception {
		log.info(Color.GREEN + "Route_12 : url shared with agency" + Color.NORMAL);
		Context context = new Context();
		CheckPointReport result = verifyValidation( log, context, "route_12", GTFS_2_GTFS_Common_4,SEVERITY.WARNING, RESULT.NOK,true);

		Assert.assertEquals(result.getCheckPointErrorCount(), 1, "detail count");
		for (CheckPointErrorReport detail : getDetails(context, result)) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "routes.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
}
