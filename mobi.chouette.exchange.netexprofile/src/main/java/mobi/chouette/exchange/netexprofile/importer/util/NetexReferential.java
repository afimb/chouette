package mobi.chouette.exchange.netexprofile.importer.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.rutebanken.netex.model.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ToString()
public class NetexReferential implements Serializable {

    private static final long serialVersionUID = 7552247971380782258L;

    @Getter
    @Setter
    private Map<String, ResourceFrame> resourceFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, SiteFrame> siteFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ServiceFrame> serviceFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ServiceCalendarFrame> serviceCalendarFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, TimetableFrame> timetableFrames = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Authority> authorities = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Operator> operators = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> stopPlaces = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Organisation> organisations = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DayType> dayTypes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Network> networks = new HashMap<>();

    @Getter
    @Setter
    private Map<String, RoutePoint> routePoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Route> routes = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Line> lines = new HashMap<>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> passengerStopAssignments = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> scheduledStopPoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, JourneyPattern> journeyPatterns = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPointInJourneyPattern> stopPointsInJourneyPattern = new HashMap<>();

    @Getter
    @Setter
    /**
     * TODO add generics support for more generic types here, i.e. VechicleJourney or Journey
     */
    private Map<String, ServiceJourney> serviceJourneys = new HashMap<>();

    public void clear() {
        resourceFrames.clear();
        siteFrames.clear();
        serviceFrames.clear();
        serviceCalendarFrames.clear();
        timetableFrames.clear();
        authorities.clear();
        operators.clear();
        stopPlaces.clear();
        organisations.clear();
        dayTypes.clear();
        networks.clear();
        routePoints.clear();
        routes.clear();
        lines.clear();
        passengerStopAssignments.clear();
        scheduledStopPoints.clear();
        journeyPatterns.clear();
        stopPointsInJourneyPattern.clear();
        serviceJourneys.clear();
    }

    public void dispose() {
        clear();
    }

}
