package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class Phase2FrequencyTests extends AbstractPhase2Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
//	@Test(groups = { "Phase 2 Frequency" }, description = "time inversion" ,priority=380 )
	public void verifyTest_2_1() throws Exception {
		log.info(Color.GREEN + "Frequency_1 : time inversion" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "frequency_1", "2-GTFS-Frequency-1",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "frequencies.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(1), "detail must refer bad line");
		}
	}
		
//	@Test(groups = { "Phase 2 Frequency" }, description = "period coverage" ,priority=381 )
	public void verifyTest_2_2() throws Exception {
		log.info(Color.GREEN + "Frequency_2 : period coverage" + Color.NORMAL);
		CheckPoint result = verifyValidation( log, "frequency_2", "2-GTFS-Frequency-2",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

		Assert.assertEquals(result.getDetailCount(), 1, "detail count");
		for (Detail detail : result.getDetails()) 
		{
			Assert.assertNotNull(detail.getSource(), "detail must refer a source");
			Assert.assertNotNull(detail.getSource().getFile(), "detail must refer a file source");
			Assert.assertEquals(detail.getSource().getFile().getFilename(), "frequencies.txt", "detail must refer bad file");
			Assert.assertEquals(detail.getSource().getFile().getLineNumber(), Integer.valueOf(3), "detail must refer bad line");
		}
	}
	

}
