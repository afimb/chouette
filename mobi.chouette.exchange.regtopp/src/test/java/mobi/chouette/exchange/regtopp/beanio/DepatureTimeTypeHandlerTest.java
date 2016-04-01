package mobi.chouette.exchange.regtopp.beanio;

import org.beanio.types.TypeConversionException;
import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DepatureTimeTypeHandlerTest {

	@Test
	public void testBeforeMidnight() throws TypeConversionException {
		DepartureTimeTypeHandler h = new DepartureTimeTypeHandler();
		Duration d = (Duration) h.parse("0001");
		
		Assert.assertEquals(60*1000, d.getMillis());
		
	}

	@Test
	public void testAfterMidnight() throws TypeConversionException {
		DepartureTimeTypeHandler h = new DepartureTimeTypeHandler();
		Duration d = (Duration) h.parse("2401");
		
		Assert.assertEquals(24*60*60*1000+60*1000, d.getMillis());
		
	}
}
