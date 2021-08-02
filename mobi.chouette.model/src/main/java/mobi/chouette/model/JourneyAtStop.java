package mobi.chouette.model;

import org.joda.time.LocalTime;

public interface JourneyAtStop {

    void setArrivalTime(LocalTime toJodaLocalTime);

    void setArrivalDayOffset(int intValue);

    void setDepartureTime(LocalTime toJodaLocalTime);

    void setDepartureDayOffset(int intValue);

    LocalTime getArrivalTime();

    LocalTime getDepartureTime();

    int getArrivalDayOffset();

    int getDepartureDayOffset();
}
