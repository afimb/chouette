package mobi.chouette.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
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

    public static org.joda.time.LocalDateTime toJodaLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return new LocalDateTime(offsetDateTime.toInstant().toEpochMilli());
    }

    public static OffsetDateTime toOffsetDateTime(org.joda.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(date.toDate().toInstant(), ZoneOffset.systemDefault());
    }

    /**
     * Convert OffsetDateTime to joda LocalDate, ignoring offset.
     *
     * This is a bit shady, but necessary as long as incoming data is represented with offset based on import time(?) and not actual offset for local timezone at
     * the given time.
     */
    public static org.joda.time.LocalDate toJodaLocalDateIgnoreOffset(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }

        return new org.joda.time.LocalDate(offsetDateTime.getYear(),offsetDateTime.getMonthValue(),offsetDateTime.getDayOfMonth());
    }

    public static LocalDate toLocalTimeFromJoda(org.joda.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.toDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
