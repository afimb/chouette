package mobi.chouette.exchange.netexprofile.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mobi.chouette.model.RouteSection;

import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.Block;
import org.rutebanken.netex.model.Branding;
import org.rutebanken.netex.model.Codespace;
import org.rutebanken.netex.model.DatedServiceJourney;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DeadRun;
import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.Line;
import org.rutebanken.netex.model.Line_VersionStructure;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.OperatingDay;
import org.rutebanken.netex.model.OperatingPeriod;
import org.rutebanken.netex.model.Organisation_VersionStructure;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.Route;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.StopPlace;

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
    private Line_VersionStructure line;

    @Getter
    @Setter
    private Map<String, Organisation_VersionStructure> sharedOrganisations = new HashMap<>();

    @Getter
    @Setter
    private Map<String, StopPlace> sharedStopPlaces = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ScheduledStopPoint> sharedScheduledStopPoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, DestinationDisplay> sharedDestinationDisplays = new HashMap<>();

    @Getter
    @Setter
    private Map<String, PassengerStopAssignment> sharedStopAssignments = new HashMap<>();

    @Getter
    @Setter
    private Map<String, RoutePoint> sharedRoutePoints = new HashMap<>();

    @Getter
    @Setter
    private Map<String, ServiceLink> sharedServiceLinks = new HashMap<>();

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
    private List<DeadRun> deadRuns = new ArrayList<>();

    @Getter
    @Setter
    private List<DatedServiceJourney> datedServiceJourneys = new ArrayList<>();

    @Getter
    @Setter
    private List<Block> blocks = new ArrayList<>();

    @Getter
    @Setter
    private Map<String,Notice> sharedNotices = new HashMap<>();

    @Getter
    @Setter
    private Map<String,Branding> sharedBrandings = new HashMap<>();

    @Getter
    @Setter
    private Set<NoticeAssignment> noticeAssignmentsTimetableFrame = new HashSet<>();

    @Getter
    @Setter
    private Set<NoticeAssignment> noticeAssignmentsServiceFrame = new HashSet<>();

    @Getter
    @Setter
    private Map<String,DayType> sharedDayTypes = new HashMap<>();

    @Getter
    @Setter
    private Set<DayTypeAssignment> sharedDayTypeAssignments = new HashSet<>();

    @Getter
    @Setter
    private Map<String,OperatingPeriod> sharedOperatingPeriods = new HashMap<>();

    @Getter
    @Setter
    private Map<String, OperatingDay> sharedOperatingDays = new HashMap<>();

    @Getter
    @Setter
    private List<ServiceJourneyInterchange> serviceJourneyInterchanges = new ArrayList<>();


    public void clear() {
        lineCondition = null;
        line = null;
        routes.clear();
        journeyPatterns.clear();
        serviceJourneys.clear();
        datedServiceJourneys.clear();
        noticeAssignmentsServiceFrame.clear();
        noticeAssignmentsTimetableFrame.clear();
        serviceJourneyInterchanges.clear();
    }

    public void dispose() {
        clear();
        sharedDayTypes.clear();
        sharedDayTypeAssignments.clear();
        sharedOperatingPeriods.clear();
        sharedOperatingDays.clear();
        commonCondition = null;
        sharedCodespaces.clear();
        sharedNetworks.clear();
        sharedGroupsOfLines.clear();
        sharedOrganisations.clear();
        sharedStopPlaces.clear();
        sharedNotices.clear();
        sharedStopAssignments.clear();
        sharedScheduledStopPoints.clear();
        sharedBrandings.clear();
        sharedRoutePoints.clear();
    }

}
