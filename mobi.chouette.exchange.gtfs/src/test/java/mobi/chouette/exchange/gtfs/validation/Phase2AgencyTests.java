package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2AgencyTests extends ValidationTests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 Agency" }, description = "missing file" ,priority=1 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Agency_1 : missing file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_1", "1-GTFS-Agency-1",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertNull(detail.getSource().getFile().getLineNumber(), "detail must refer no line");
			Assert.assertNull(detail.getSource().getFile().getColumnNumber(), "detail must refer no column");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "missing agency_id" ,priority=2 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Agency_2 : missing agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_2", "1-GTFS-Agency-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "duplicate agency_id" ,priority=3 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Agency_3 : duplicate agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_3", "1-GTFS-Agency-3",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Agency" }, description = "missing column agency_id" ,priority=4 )
	public void verifyTest_2_4_1() throws Exception {
		log.info(Color.GREEN + "Agency_4_1 : missing column agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_1", "1-GTFS-Agency-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "missing column agency_name" ,priority=5)
	public void verifyTest_2_4_2() throws Exception {
		log.info(Color.GREEN + "Agency_4_2 : missing column agency_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_2", "1-GTFS-Agency-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "missing column agency_url" ,priority=6 )
	public void verifyTest_2_4_3() throws Exception {
		log.info(Color.GREEN + "Agency_4_3 : missing column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_3", "1-GTFS-Agency-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "missing column agency_timezone" ,priority=7)
	public void verifyTest_2_4_4() throws Exception {
		log.info(Color.GREEN + "Agency_4_4 : missing column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_4", "1-GTFS-Agency-4",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "empty column agency_name" ,priority=8 )
	public void verifyTest_2_5_1() throws Exception {
		log.info(Color.GREEN + "Agency_5_1 : empty column agency_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_1", "1-GTFS-Agency-5",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "empty column agency_url" ,priority=9)
	public void verifyTest_2_5_2() throws Exception {
		log.info(Color.GREEN + "Agency_5_2 : empty column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_2", "1-GTFS-Agency-5",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "empty column agency_timezone" ,priority=10 )
	public void verifyTest_2_5_3() throws Exception {
		log.info(Color.GREEN + "Agency_5_3 : empty column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_3", "1-GTFS-Agency-5",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "invalid column agency_timezone" ,priority=11 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Agency_6 : invalid column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_6", "1-GTFS-Agency-6",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "invalid column agency_url" ,priority=12 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Agency_7 : invalid column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_7", "1-GTFS-Agency-7",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	@Test(groups = { "Phase 2 Agency" }, description = "invalid column agency_lang" ,priority=13 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Agency_8 : invalid column agency_lang" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_8", "1-GTFS-Agency-8",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

}
