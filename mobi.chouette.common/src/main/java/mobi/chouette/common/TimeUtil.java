package mobi.chouette.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;

public class TimeUtil {

    public static Duration subtract(LocalTime thisDeparture, LocalTime firstDeparture) {
        int seconds;
        // Assuming journeys last no more than 24 hours
        if (firstDeparture.isBefore(thisDeparture)) {
            seconds = Seconds.secondsBetween(firstDeparture, thisDeparture).getSeconds();
        } else {
            seconds = DateTimeConstants.SECONDS_PER_DAY - Seconds.secondsBetween(thisDeparture, firstDeparture).getSeconds();
        }

        return Duration.standardSeconds(seconds);
    }

    public static java.time.LocalTime toLocalTimeFromJoda(org.joda.time.LocalTime jodaTime) {
        if (jodaTime == null) {
            return null;
        }
        return java.time.LocalTime.of(jodaTime.getHourOfDay(), jodaTime.getMinuteOfHour(), jodaTime.getSecondOfMinute());
    }

    public static org.joda.time.LocalTime toJodaLocalTime(java.time.LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return new org.joda.time.LocalTime(localTime.getHour(), localTime.getMinute(), localTime.getSecond());
    }

    public static org.joda.time.Duration toJodaDuration(java.time.Duration duration) {
        if (duration == null) {
            return null;
        }
        return org.joda.time.Duration.millis(duration.toMillis());
    }

    public static java.time.Duration toDurationFromJodaDuration(Duration jodaDuration) {
        if (jodaDuration == null) {
            return null;
        }
        return java.time.Duration.ofMillis(jodaDuration.getMillis());
    }

    public static org.joda.time.LocalDate toJodaLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return new org.joda.time.LocalDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    public static LocalDate toLocalDateFromJoda(org.joda.time.LocalDate jodaDate) {
        if (jodaDate == null) {
            return null;
        }
        return LocalDate.of(jodaDate.getYear(), jodaDate.getMonthOfYear(), jodaDate.getDayOfMonth());
    }

    public static org.joda.time.LocalDateTime toJodaLocalDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return new org.joda.time.LocalDateTime(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }
    /**
     * Convert localDateTime to joda LocalDate, ignoring time.
     *
     * This is a bit shady, but necessary as long as incoming data, while semantically a LocalDate, is represented as xs:dateTime.
     */
    public static org.joda.time.LocalDate toJodaLocalDateIgnoreTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        return new org.joda.time.LocalDate(localDateTime.getYear(),localDateTime.getMonthValue(),localDateTime.getDayOfMonth());
    }

}
