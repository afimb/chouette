package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2TransferTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Phase 2 Transfer" }, description = "unknown from_stop_id" ,priority=390)
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Transfer_1 : unknown from_stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "transfer_1", GTFS_2_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
	
	@Test(groups = { "Phase 2 Transfer" }, description = "unknown to_stop_id" ,priority=391)
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Transfer_2 : unknown to_stop_id" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "transfer_2", GTFS_2_GTFS_Common_1,CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "transfers.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(2), "detail must refer bad line");
		}
	}
}
