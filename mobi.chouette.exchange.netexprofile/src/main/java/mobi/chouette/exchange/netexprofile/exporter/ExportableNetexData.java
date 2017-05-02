package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.Setter;
import org.rutebanken.netex.model.*;

import java.util.*;

public class ExportableNetexData {

    @Getter
    @Setter
    private AvailabilityCondition commonCondition;

    @Getter
    @Setter
    private AvailabilityCondition lineCondition;

    @Getter
    @Setter
    private Map<String, Codespace> sharedCodespaces = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Network> sharedNetworks = new HashMap<>();

    @Getter
    @Setter
    private Map<String, GroupOfLines> sharedGroupsOfLines = new HashMap<>();

    @Getter
    @Setter
    private Line line;

    @Getter
    @Setter
    private Map<String, Authority> sharedAuthorities = new HashMap<>();

    @Getter
    @Setter
    private Map<String, Operator> sharedOperators = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> sharedStopPoints = new HashMap<>();

    @Getter
    @Setter
    private Set<ScheduledStopPoint> stopPoints = new HashSet<>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> sharedStopAssignments = new HashMap<>();

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
        lineCondition = null;
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
        commonCondition = null;
        sharedCodespaces.clear();
        sharedNetworks.clear();
        sharedGroupsOfLines.clear();
        sharedAuthorities.clear();
        sharedOperators.clear();
        sharedStopPlaces.clear();
        sharedStopAssignments.clear();
        sharedStopPoints.clear();
    }

}
