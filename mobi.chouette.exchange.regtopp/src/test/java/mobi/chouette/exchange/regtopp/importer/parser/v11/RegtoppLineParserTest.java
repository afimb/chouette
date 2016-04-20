package mobi.chouette.exchange.regtopp.importer.parser.v11;

import java.sql.Time;

import org.joda.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppLineParser;

public class RegtoppLineParserTest {

	@Test public void testCalculateDepartureTimeBeforeMidnight() {
		
		Duration tripDepartureTime = new Duration(60*1000); // at time 0001
		Duration timeSinceTripDepatureTime = new Duration(36*60*1000);
		
		Time visitTime = RegtoppLineParser.calculateTripVisitTime(tripDepartureTime, timeSinceTripDepatureTime);
		
		Assert.assertEquals(0,visitTime.getHours());
		Assert.assertEquals(37,visitTime.getMinutes());
	}

	@Test public void testCalculateDepartureTimeAfterMidnight() {
		
		Duration tripDepartureTime = new Duration(24*60*60*1000+60*1000); // at time 2401 
		Duration timeSinceTripDepatureTime = new Duration(36*60*1000);
		
		Time visitTime = RegtoppLineParser.calculateTripVisitTime(tripDepartureTime, timeSinceTripDepatureTime);
		
		Assert.assertEquals(0,visitTime.getHours());
		Assert.assertEquals(37,visitTime.getMinutes());
	}
}
