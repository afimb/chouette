package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase1CalendarDateTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "missing column service_id" ,priority=170 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "CalendarDate_2_1 : missing column service_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_2_1", "1-GTFS-CalendarDate-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "missing column date" ,priority=171)
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "CalendarDate_2_2 : missing column date" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_2_2", "1-GTFS-CalendarDate-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "missing column exception_type" ,priority=172)
	public void verifyTest_2_2_3() throws Exception {
		log.info(Color.GREEN + "CalendarDate_2_3 : missing column exception_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_2_3", "1-GTFS-CalendarDate-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "empty column service_id" ,priority=173 )
	public void verifyTest_2_3_1() throws Exception {
		log.info(Color.GREEN + "CalendarDate_3_1 : empty column service_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_3_1", "1-GTFS-CalendarDate-3",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "empty column date" ,priority=174 )
	public void verifyTest_2_3_2() throws Exception {
		log.info(Color.GREEN + "CalendarDate_3_2 : empty column date" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_3_2", "1-GTFS-CalendarDate-3",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "empty column exception_type" ,priority=175 )
	public void verifyTest_2_3_3() throws Exception {
		log.info(Color.GREEN + "CalendarDate_3_3 : empty column exception_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_3_3", "1-GTFS-CalendarDate-3",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "duplicate service_id, date" ,priority=176 )
	public void verifyTest_2_4() throws Exception {
		log.info(Color.GREEN + "CalendarDate_4 : duplicate service_id,date" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_4", "1-GTFS-CalendarDate-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "invalid column date" ,priority=177 )
	public void verifyTest_2_5() throws Exception {
		log.info(Color.GREEN + "CalendarDate_5 : invalid column date" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_5", "1-GTFS-CalendarDate-5",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 CalendarDate" }, description = "invalid column exception_type" ,priority=178 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "CalendarDate_6 : invalid column exception_type" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_6", "1-GTFS-CalendarDate-6",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(4), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 2 CalendarDate" }, description = "extra columns" ,priority=179 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "CalendarDate_7 : extra column detected" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "calendar_date_7", "1-GTFS-CalendarDate-7",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "calendar_dates.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}


}
