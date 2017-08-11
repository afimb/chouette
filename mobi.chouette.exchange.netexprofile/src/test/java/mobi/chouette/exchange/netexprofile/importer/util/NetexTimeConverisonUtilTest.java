package mobi.chouette.exchange.netexprofile.importer.util;

import java.time.OffsetTime;
import java.time.ZoneOffset;

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
		OffsetTime zulu1801 = OffsetTime.of(18, 1, 0, 0, ZoneOffset.UTC);
		tpt.setDepartureTime(zulu1801);
		
		NetexTimeConversionUtil.parsePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(vj.getDepartureTime().getHourOfDay(), 20);
		Assert.assertEquals(vj.getDepartureDayOffset(), 0);
		
		NetexTimeConversionUtil.populatePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(tpt.getDepartureTime(), zulu1801);
		Assert.assertNull(tpt.getDepartureDayOffset());
		
		
	}

	@Test
	public void testConversionMidnight() {
		
		TimetabledPassingTime tpt = new TimetabledPassingTime();
		VehicleJourneyAtStop vj = new VehicleJourneyAtStop();
		
		// Convert to local time, should be past midnight with day offset 1
		OffsetTime zulu2301 = OffsetTime.of(23, 1, 0, 0, ZoneOffset.UTC);
		tpt.setDepartureTime(zulu2301);
		
		NetexTimeConversionUtil.parsePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(vj.getDepartureTime().getHourOfDay(), 1);
		Assert.assertEquals(vj.getDepartureDayOffset(), 1);
		
		NetexTimeConversionUtil.populatePassingTimeUtc(tpt, false, vj);
		
		Assert.assertEquals(tpt.getDepartureTime(), zulu2301);
		Assert.assertNull(tpt.getDepartureDayOffset());
		
		
	}
}
