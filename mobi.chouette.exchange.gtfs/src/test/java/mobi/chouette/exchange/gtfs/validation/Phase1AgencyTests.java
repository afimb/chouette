package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase1AgencyTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "missing file" ,priority=21 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Agency_1 : missing file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_1", GTFS_1_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

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
	
	// THIS IS 1-GTFS-Agency-5 NOT 1-GTFS-Agency-2
	@Test(groups = { "Phase 1 Agency" }, description = "missing agency_id" ,priority=22 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Agency_2 : missing agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_2", GTFS_1_GTFS_Common_13,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "duplicate agency_id" ,priority=23 )
	public void verifyTest_2_3() throws Exception {
		log.info(Color.GREEN + "Agency_3 : duplicate agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_3", GTFS_1_GTFS_Common_8,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "missing column agency_id" ,priority=24 )
	public void verifyTest_2_4_1() throws Exception {
		log.info(Color.GREEN + "Agency_4_1 : missing column agency_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_1", GTFS_1_GTFS_Common_10,CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "missing column agency_name" ,priority=25)
	public void verifyTest_2_4_2() throws Exception {
		log.info(Color.GREEN + "Agency_4_2 : missing column agency_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_2", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "missing column agency_url" ,priority=26 )
	public void verifyTest_2_4_3() throws Exception {
		log.info(Color.GREEN + "Agency_4_3 : missing column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_3", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "missing column agency_timezone" ,priority=27)
	public void verifyTest_2_4_4() throws Exception {
		log.info(Color.GREEN + "Agency_4_4 : missing column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_4_4", GTFS_1_GTFS_Common_9,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 1 Agency" }, description = "empty column agency_name" ,priority=28 )
	public void verifyTest_2_5_1() throws Exception {
		log.info(Color.GREEN + "Agency_5_1 : empty column agency_name" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_1", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "empty column agency_url" ,priority=29)
	public void verifyTest_2_5_2() throws Exception {
		log.info(Color.GREEN + "Agency_5_2 : empty column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_2", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "empty column agency_timezone" ,priority=30 )
	public void verifyTest_2_5_3() throws Exception {
		log.info(Color.GREEN + "Agency_5_3 : empty column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_5_3", GTFS_1_GTFS_Common_12,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "invalid column agency_timezone" ,priority=31 )
	public void verifyTest_2_6() throws Exception {
		log.info(Color.GREEN + "Agency_6 : invalid column agency_timezone" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_6", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "invalid column agency_url" ,priority=32 )
	public void verifyTest_2_7() throws Exception {
		log.info(Color.GREEN + "Agency_7 : invalid column agency_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_7", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "invalid column agency_lang" ,priority=33 )
	public void verifyTest_2_8() throws Exception {
		log.info(Color.GREEN + "Agency_8 : invalid column agency_lang" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_8", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "invalid column agency_fare_url" ,priority=34 )
	public void verifyTest_2_9() throws Exception {
		log.info(Color.GREEN + "Agency_9 : invalid column agency_fare_url" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_9", GTFS_1_GTFS_Common_16,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "extra columns" ,priority=35 )
	public void verifyTest_2_10() throws Exception {
		log.info(Color.GREEN + "Agency_10 : extra column detected" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_10", GTFS_1_GTFS_Common_11,CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 2, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

	@Test(groups = { "Phase 1 Agency" }, description = "empty file" ,priority=36 )
	public void verifyTest_2_11() throws Exception {
		log.info(Color.GREEN + "Agency_11 : empty file" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_11", GTFS_1_GTFS_Common_5,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}

}
