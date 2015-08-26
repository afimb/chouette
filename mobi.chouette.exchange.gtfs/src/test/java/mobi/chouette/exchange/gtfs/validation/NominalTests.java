package mobi.chouette.exchange.gtfs.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.exchange.validation.report.CheckPoint;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Log4j
public class NominalTests extends ValidationTests {
	
	
	@BeforeSuite 
	public void init()
	{
		super.init();
	}
	
	@Test(groups = { "Nominal" }, description = "valid files")
	public void verifyTest_1() throws Exception {
		log.info(Color.GREEN + "Nominal : valid files" + Color.NORMAL);
		verifyValidation( log, "nominal", "NONE",CheckPoint.SEVERITY.ERROR, CheckPoint.RESULT.NOK);

	}
	

}
