package mobi.chouette.exchange.netexprofile.exporter;

import lombok.Getter;
import lombok.Setter;
import org.rutebanken.netex.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportableNetexData {

    @Getter
    @Setter
    private AvailabilityCondition availabilityCondition;

    @Getter
    @Setter
    private Set<Codespace> codespaces = new HashSet<>();

    @Getter
    @Setter
    private Network network;

    @Getter
    @Setter
    private Line line;

    @Getter
    @Setter
    private Set<Operator> operators = new HashSet<>();

    @Getter
    @Setter
    private Set<StopPlace> stopPlaces = new HashSet<>();

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
        network = null;
        line = null;
        operators.clear();
        stopPlaces.clear();
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
}
