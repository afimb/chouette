package mobi.chouette.jadira;

import java.sql.Time;

import org.jadira.usertype.spi.shared.AbstractTimeColumnMapper;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SqlTimeDurationMapper extends AbstractTimeColumnMapper<Duration> {

    private static final long serialVersionUID = -5741261927204773374L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");

    @Override
    public Duration fromNonNullValue(Time value) {
        return Duration.millis(value.getTime());
    }

    @Override
    public Duration fromNonNullString(String s) {
        return Duration.standardSeconds(Seconds.secondsBetween(new LocalTime(0, 0), new LocalTime(s)).getSeconds());
    }

    @Override
    public Time toNonNullValue(Duration value) {
        return new Time(value.getMillis());
    }

    @Override
    public String toNonNullString(Duration value) {
        return LOCAL_TIME_FORMATTER.print(new LocalTime(value.getMillis()));
    }
}
