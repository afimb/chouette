package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.Setter;
import org.rutebanken.netex.model.*;

import java.util.*;

public class ExportableNetexData {

    @Getter
    @Setter
    private AvailabilityCondition availabilityCondition;

    @Getter
    @Setter
    private Set<Codespace> codespaces = new HashSet<>();

    @Getter
    @Setter
    private Network sharedNetwork = null;

    @Getter
    @Setter
    private Line line;

    @Getter
    @Setter
    private Map<String, Operator> sharedOperators = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();

    @Getter
    @Setter
    private Set<ScheduledStopPoint> stopPoints = new HashSet<>();

    @Getter
    @Setter
    private Set<PassengerStopAssignment> stopAssignments = new HashSet<>();

    @Getter
    @Setter
    private Set<RoutePoint> routePoints = new HashSet<>();

    @Getter
    @Setter
    private List<Route> routes = new ArrayList<>();

    @Getter
    @Setter
    private List<JourneyPattern> journeyPatterns = new ArrayList<>();

    @Getter
    @Setter
    private List<ServiceJourney> serviceJourneys = new ArrayList<>();

    @Getter
    @Setter
    private Set<DayType> dayTypes = new HashSet<>();

    @Getter
    @Setter
    private Set<DayTypeAssignment> dayTypeAssignments = new HashSet<>();

    @Getter
    @Setter
    private Set<OperatingPeriod> operatingPeriods = new HashSet<>();

    public void clear() {
        availabilityCondition = null;
        codespaces.clear();
        line = null;
        stopPoints.clear();
        stopAssignments.clear();
        routePoints.clear();
        routes.clear();
        journeyPatterns.clear();
        serviceJourneys. clear();
        dayTypes.clear();
        dayTypeAssignments.clear();
        operatingPeriods.clear();
    }

    public void dispose() {
        clear();
        sharedNetwork = null;
        sharedOperators.clear();
        sharedStopPlaces.clear();
    }

}
