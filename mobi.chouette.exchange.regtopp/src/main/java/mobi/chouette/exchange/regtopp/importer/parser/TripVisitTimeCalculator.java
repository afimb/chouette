package mobi.chouette.exchange.regtopp.importer.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Period;

@Log4j
public class TripVisitTimeCalculator {

    public static TripVisitTime calculateTripVisitTime(Duration tripDepartureTime, Duration timeSinceTripDepatureTime) {
        Period period = tripDepartureTime.plus(timeSinceTripDepatureTime).toPeriod();

        int days = period.getHours() / 24;
        if (days > 0){
            log.trace(period.getHours() + " hours in period gives " + days + " days offset");
            period = period.minusHours(days * 24);
            log.trace("Reducing hours to " + period.getHours());
        }

        LocalTime sqlTime = new LocalTime(period.getHours(), period.getMinutes(), period.getSeconds());

        return new TripVisitTime(days, sqlTime);
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class TripVisitTime {
        final int dayOffset;
        final LocalTime time;
    }

}


