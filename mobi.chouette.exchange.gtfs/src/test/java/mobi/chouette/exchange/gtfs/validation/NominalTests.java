package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class NominalTests extends AbstractPhase1Tests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Nominal" }, description = "valid files",priority=1)
	public void verifyTest_1() throws Exception {
		log.info(Color.GREEN + "Nominal : valid files" + Color.NORMAL);
		verifyValidation( log, "nominal", "NONE",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

	}
	
	@Test(groups = { "Minimal" }, description = "minimum valid files",priority=2)
	public void verifyTest_2() throws Exception {
		log.info(Color.GREEN + "Minimal : minimum valid files" + Color.NORMAL);
		verifyValidation( log, "minimal", "NONE",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,true);

	}

	@Test(groups = { "Nominal stop" }, description = "valid stops files",priority=3)
	public void verifyTest_3() throws Exception {
		log.info(Color.GREEN + "Nominal : valid stops files" + Color.NORMAL);
		verifyValidation( log, "stops", "NONE",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,false);

	}
	
	@Test(groups = { "Minimal stop" }, description = "minimum valid stops files",priority=4)
	public void verifyTest_4() throws Exception {
		log.info(Color.GREEN + "Minimal : minimum valid stops files" + Color.NORMAL);
		verifyValidation( log, "stops_mini", "NONE",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK,false);

	}


}
