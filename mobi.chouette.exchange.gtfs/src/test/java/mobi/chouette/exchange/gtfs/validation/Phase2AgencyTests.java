package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2AgencyTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 Agency" }, description = "unused agency" ,priority=300 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Agency_1 : unused agency" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_1", "2-GTFS-Agency-1",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Agency" }, description = "missing agency_id value" ,priority=301 )
	public void verifyTest_2_2_1() throws Exception {
		log.info(Color.GREEN + "Agency_2_1 : missing agency_id value with one agency" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_2_1", "2-GTFS-Agency-2",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Agency" }, description = "missing agency_id column" ,priority=301 )
	public void verifyTest_2_2_2() throws Exception {
		log.info(Color.GREEN + "Agency_2_2 : missing agency_id column with one agency" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "agency_2_2", "2-GTFS-Agency-2",CheckPoint.SEVERITY.WARNING, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "agency.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	

}
