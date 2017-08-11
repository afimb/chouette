package mobi.chouette.common;

import java.time.LocalDate;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TimeUtilTest {

    @Test
    public void testSubtractSameDay() {
        Duration duration = TimeUtil.subtract(new LocalTime(12, 30, 0), new LocalTime(10, 5, 30));
        Assert.assertEquals(duration, Duration.standardSeconds(2 * 60 * 60 + 24 * 60 + 30));
    }

    @Test
    public void testSubtractThisDepartureOnNextDay() {
        Duration duration = TimeUtil.subtract(new LocalTime(00, 10, 0), new LocalTime(23, 50, 00));
        Assert.assertEquals(duration, Duration.standardSeconds(20 * 60));
    }

    @Test
    public void javaLocalTimeToJodaLocalTimeTest() {
        org.joda.time.LocalTime converted = TimeUtil.toJodaLocalTime(java.time.LocalTime.of(1, 5, 10));
        Assert.assertEquals(converted.getHourOfDay(), 1);
        Assert.assertEquals(converted.getMinuteOfHour(), 5);
        Assert.assertEquals(converted.getSecondOfMinute(), 10);
    }

    @Test
    public void jodaLocalTimeToJavaLocalTimeTest() {
        java.time.LocalTime converted = TimeUtil.toLocalTimeFromJoda(new org.joda.time.LocalTime(1, 5, 10));
        Assert.assertEquals(converted.getHour(), 1);
        Assert.assertEquals(converted.getMinute(), 5);
        Assert.assertEquals(converted.getSecond(), 10);
    }

    @Test
    public void jodaLocalDateToJavaLocalDateTest() {
        LocalDate converted = TimeUtil.toLocalTimeFromJoda(new org.joda.time.LocalDate(2017, 5, 10));
        Assert.assertEquals(converted.getYear(), 2017);
        Assert.assertEquals(converted.getMonthValue(), 5);
        Assert.assertEquals(converted.getDayOfMonth(), 10);
    }

    @Test
    public void javaLocalDateToJodaLocalDateTest() {
        org.joda.time.LocalDate converted = TimeUtil.toJodaLocalDate(LocalDate.of(2017, 5, 10));
        Assert.assertEquals(converted.getYear(), 2017);
        Assert.assertEquals(converted.getMonthOfYear(), 5);
        Assert.assertEquals(converted.getDayOfMonth(), 10);
    }

    @Test
    public void javaDurationToJodaDurationTest(){
        Assert.assertEquals(TimeUtil.toJodaDuration(java.time.Duration.ofMinutes(60)), org.joda.time.Duration.standardMinutes(60));
    }
}
