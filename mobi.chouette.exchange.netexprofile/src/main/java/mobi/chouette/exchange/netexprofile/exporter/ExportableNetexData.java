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
    private Map<String, Organisation_VersionStructure> sharedOrganisations = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> scheduledStopPoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DestinationDisplay> sharedDestinationDisplays = new HashMap<>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> stopAssignments = new HashMap<>();

    @Getter
    @Setter
    private Map<String, RoutePoint> routePoints = new HashMap<>();

    @Getter
    @Setter
    private List<Route> routes = new ArrayList<>();

    @Getter
    @Setter
    private List<ServiceJourneyPattern> journeyPatterns = new ArrayList<>();

    @Getter
    @Setter
    private List<ServiceJourney> serviceJourneys = new ArrayList<>();

    @Getter
    @Setter
    private List<Notice> sharedNotices = new ArrayList<>();

    @Getter
    @Setter
    private Set<NoticeAssignment> noticeAssignments = new HashSet<>();

    @Getter
    @Setter
    private Map<String,DayType> sharedDayTypes = new HashMap<>();

    @Getter
    @Setter
    private Set<DayTypeAssignment> sharedDayTypeAssignments = new HashSet<>();

    @Getter
    @Setter
    private Set<OperatingPeriod> sharedOperatingPeriods = new HashSet<>();

    @Getter
    @Setter
    private List<ServiceJourneyInterchange> serviceJourneyInterchanges = new ArrayList<>();


    public void clear() {
        lineCondition = null;
        line = null;
        routes.clear();
        journeyPatterns.clear();
        serviceJourneys. clear();
        sharedNotices.clear();
        noticeAssignments.clear();
        serviceJourneyInterchanges.clear();
        
        routePoints.clear();
        stopAssignments.clear();
        scheduledStopPoints.clear();
    }

    public void dispose() {
        clear();
        sharedDayTypes.clear();
        sharedDayTypeAssignments.clear();
        sharedOperatingPeriods.clear();
        commonCondition = null;
        sharedCodespaces.clear();
        sharedNetworks.clear();
        sharedGroupsOfLines.clear();
        sharedOrganisations.clear();
        sharedStopPlaces.clear();
        scheduledStopPoints.clear();
    }

}
