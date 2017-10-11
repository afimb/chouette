package mobi.chouette.exchange.netexprofile.importer.util;

import java.math.BigInteger;
import java.time.LocalTime;

import org.rutebanken.netex.model.TimetabledPassingTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.VehicleJourneyAtStop;

public class NetexTimeConverisonUtilTest {

	@Test
	public void testConversion() {
		
		TimetabledPassingTime tpt = new TimetabledPassingTime();
		VehicleJourneyAtStop vj = new VehicleJourneyAtStop();
		
		// Convert to local time, should be past midnight with day offset 1
		LocalTime localTime1801 = LocalTime.of(18, 1, 0, 0);
		tpt.setDepartureTime(localTime1801);
		
		NetexTimeConversionUtil.parsePassingTime(tpt, false, vj);
		
		Assert.assertEquals(vj.getDepartureTime().getHourOfDay(), 18);
		Assert.assertEquals(vj.getDepartureDayOffset(), 0);
		
		NetexTimeConversionUtil.populatePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(tpt.getDepartureTime(), localTime1801);
	}

	@Test
	public void testConversionWithDayOffset() {
		
		TimetabledPassingTime tpt = new TimetabledPassingTime();
		VehicleJourneyAtStop vj = new VehicleJourneyAtStop();

		LocalTime localTime2301 = LocalTime.of(1, 1, 0, 0);
		tpt.setDepartureTime(localTime2301);
		tpt.setDepartureDayOffset(BigInteger.ONE);
		
		NetexTimeConversionUtil.parsePassingTime(tpt, false, vj);
		
		Assert.assertEquals(vj.getDepartureTime().getHourOfDay(), 1);
		Assert.assertEquals(vj.getDepartureDayOffset(), 1);
		
		NetexTimeConversionUtil.populatePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(tpt.getDepartureTime(), localTime2301);
	}
}
