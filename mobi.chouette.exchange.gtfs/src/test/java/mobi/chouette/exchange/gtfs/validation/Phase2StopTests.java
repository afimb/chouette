package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

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

	//@Test(groups = { "Phase 2 Stop" }, description = "missing parent_station" ,priority=310 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Stop_1 : missing parent_station" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_1", "2-GTFS-Stop-1",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Stop" }, description = "wrong parent_station type" ,priority=311 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Stop_2 : wrong parent_station type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_2", "2-GTFS-Stop-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}


	//@Test(groups = { "Phase 2 Stop" }, description = "unused stop" ,priority=312 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Stop_3 : unused stop" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_3", "2-GTFS-Stop-3",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Stop" }, description = "shared values for stop_name and stop_desc" ,priority=313 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "Stop_4 : shared values for stop_name and stop_desc" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_4", "2-GTFS-Stop-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Stop" }, description = "shared url with agency" ,priority=314 )
	public void verifyTest_2_5_1() throws Exception {
		log.info(Color.GREEN + "Stop_5_1 : shared url with agency" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_5_1", "2-GTFS-Stop-5",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	//@Test(groups = { "Phase 2 Stop" }, description = "shared url with route" ,priority=315 )
	public void verifyTest_2_5_2() throws Exception {
		log.info(Color.GREEN + "Stop_5_2 : shared url with route" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_5_2", "2-GTFS-Stop-5",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "stops.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	//@Test(groups = { "Phase 2 Stop" }, description = "useless location_type" ,priority=316 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Stop_6 : useless location_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "stop_6", "2-GTFS-Stop-6",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

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
