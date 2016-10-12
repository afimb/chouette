package mobi.chouette.exchange.regtopp.model.importer.parser;

import mobi.chouette.exchange.regtopp.importer.parser.TripVisitTimeCalculator.TripVisitTime;
import org.joda.time.Duration;
import org.testng.annotations.Test;

import java.sql.Time;

import static mobi.chouette.exchange.regtopp.importer.parser.TripVisitTimeCalculator.calculateTripVisitTime;
import static org.testng.Assert.assertEquals;

public class TripVisitTimeCalculatorTest {

    @Test
    public void testZeroDifference() {
        Duration start = Duration.ZERO;
        Duration timeFromStart = Duration.ZERO;
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 0);
        assertEquals(tripVisitTime.getTime(), new Time(0,0,0));
    }

    @Test
    public void testWithoutOffset() {
        Duration start = Duration.ZERO;
        Duration timeFromStart = Duration.standardMinutes(12 * 60 + 30);
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 0, "offset:");
        assertEquals(tripVisitTime.getTime(), new Time(12,30,0), "time:");
    }

    @Test
    public void testWithOneOffsetBorder() {
        Duration start = Duration.ZERO;
        Duration timeFromStart = Duration.standardMinutes(24 * 60);
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 1, "offset:");
        assertEquals(tripVisitTime.getTime(), new Time(0,0,0), "time:");
    }

    @Test
    public void testWithZeroOffsetBorder() {
        Duration start = Duration.ZERO;
        Duration timeFromStart = Duration.standardSeconds(23 * 60 * 60 + 59 * 60 + 59);
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 0, "offset:");
        assertEquals(tripVisitTime.getTime(), new Time(23,59,59), "time:");
    }

    @Test
    public void testWithOneOffset() {
        Duration start = Duration.ZERO;
        Duration timeFromStart = Duration.standardMinutes(24 * 60 + 30);
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 1, "offset:");
        assertEquals(tripVisitTime.getTime(), new Time(0,30,0), "time:");
    }

    @Test
    public void testTwoDaysOffset() {
        Duration start = Duration.standardMinutes(24 * 60 + 15);
        Duration timeFromStart = Duration.standardMinutes(24 * 60 + 30);
        TripVisitTime tripVisitTime = calculateTripVisitTime(start, timeFromStart);
        assertEquals(tripVisitTime.getDayOffset(), 2, "offset:");
        assertEquals(tripVisitTime.getTime(), new Time(0,45,0), "time:");
    }

}
