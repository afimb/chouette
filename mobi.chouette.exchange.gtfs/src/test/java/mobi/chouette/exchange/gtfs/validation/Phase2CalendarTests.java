package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2CalendarTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
//	@Test(groups = { "Phase 2 Calendar" }, description = "date progression" ,priority=370 )
//	public void verifyTest_2_1() throws Exception {
//		log.info(Color.GREEN + "Calendar_1 : date progression" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_1", "2-GTFS-Calendar-1",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);
//
//		 // 2 files are needed: "calendar.txt" and "calendar_dates.txt"
//		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
//		String filename = "calendar.txt";
//		for (Detail detail : result.getDetails()) {
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), filename, "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
//			filename = "calendar_dates.txt";
//		}
//	}
//	
//	@Test(groups = { "Phase 2 Calendar" }, description = "no selected days" ,priority=371 )
//	public void verifyTest_2_2() throws Exception {
//		log.info(Color.GREEN + "Calendar_2 : no selected days" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_2", "2-GTFS-Calendar-2",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
//		}
//	}
//
//	@Test(groups = { "Phase 2 Calendar" }, description = "no valid day in service : calendar only" ,priority=372 )
//	public void verifyTest_2_3_1() throws Exception {
//		log.info(Color.GREEN + "Calendar_3_1 : no valid day in service : calendar only" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_3_1", "2-GTFS-Calendar-3",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
//		}
//	}
//	
//	@Test(groups = { "Phase 2 Calendar" }, description = "no valid day in service : calendar and calendar_dates" ,priority=374 )
//	public void verifyTest_2_3_3() throws Exception {
//		log.info(Color.GREEN + "Calendar_3_2 : no valid day in service : calendar and calendar_dates" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_3_2", "2-GTFS-Calendar-3",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
//		}
//	}
//	
//	@Test(groups = { "Phase 2 Calendar" }, description = "exclusion without calendar" ,priority=375 )
//	public void verifyTest_2_4() throws Exception {
//		log.info(Color.GREEN + "Calendar_4 : exclusion without calendar" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_4", "2-GTFS-Calendar-4",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
//		}
//	}
	
	// NE PAS DECOMMENTER LES 2 DERNIERS TESTS !
	
//	@Test(groups = { "Phase 2 Calendar" }, description = "old services" ,priority=376 )
//	public void verifyTest_2_5() throws Exception {
//		log.info(Color.GREEN + "Calendar_5 : old services" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_5", "2-GTFS-Calendar-5",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
//		}
//	}
	
//	@Test(groups = { "Phase 2 Calendar" }, description = "nearly old services" ,priority=377 )
//	public void verifyTest_2_6() throws Exception {
//		log.info(Color.GREEN + "Calendar_6 : nearly old services" + Color.NORMAL);
//		CheckPoint result = verifyValidation( log, "calendar_6", "2-GTFS-Calendar-6",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);
//
//		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
//		for (Detail detail : result.getDetails()) 
//		{
//			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
//			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
//			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar.txt", "detail must refer bad file");
//			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
//		}
//	}
	
}
