package mobi.chouette.exchange.netexprofile.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.*;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.*;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static mobi.chouette.exchange.netexprofile.Constant.EXPORTABLE_NETEX_DATA;
import static mobi.chouette.exchange.netexprofile.exporter.producer.CalendarProducer.*;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

@Log4j
public class NetexLineDataProducer extends NetexProducer implements Constant {

    private static final String NSR_OBJECT_ID = "NSR:Authority:NSR";
    private static final String NSR_COMPANY_NUMBER = "917422575";
    private static final String NSR_NAME = "Nasjonal Stoppestedsregister";
    private static final String NSR_LEGAL_NAME = "NASJONAL STOPPESTEDSREGISTER";
    private static final String NSR_PHONE = "0047 236 20 000";

    private static final Map<String, Codespace> CODESPACE_MAP = new HashMap<>();

    private static OperatorProducer operatorProducer = new OperatorProducer();
    private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();
    private static NetworkProducer networkProducer = new NetworkProducer();
    private static LineProducer lineProducer = new LineProducer();
    private static RouteProducer routeProducer = new RouteProducer();
    private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
    private static CalendarProducer calendarProducer = new CalendarProducer();
    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    public void produce(Context context) throws Exception {
        NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Metadata metadata = (Metadata) context.get(METADATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);

        Line neptuneLine = exportableData.getLine();
        initializeCodespaces(configuration, exportableData, exportableNetexData);

        produceAndCollectLineData(context, exportableData, exportableNetexData);
        produceAndCollectSharedData(context, exportableData, exportableNetexData);

        String fileName = neptuneLine.getObjectId().replaceAll(":", "-") + (neptuneLine.getNumber() != null ?
                neptuneLine.getNumber() + "-" : "") + (neptuneLine.getPublishedName() != null ?
                "-" + neptuneLine.getPublishedName().replace(' ', '_').replace('/', '_') : "") + ".xml";
        Path filePath = new File(outputPath.toFile(), fileName).toPath();

        NetexFileWriter writer = new NetexFileWriter();
        writer.writeXmlFile(context, filePath, exportableData, exportableNetexData, NetexFragmentMode.LINE);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(neptuneLine.getNetwork()),
                    NeptuneObjectPresenter.getName(neptuneLine)));
        }
    }

    private void initializeCodespaces(NetexprofileExportParameters configuration, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        mobi.chouette.model.Line line = exportableData.getLine();

        Codespace nsrCodespace = netexFactory.createCodespace()
                .withId(NSR_XMLNS.toLowerCase())
                .withXmlns(NSR_XMLNS)
                .withXmlnsUrl(NSR_XMLNSURL);

        Codespace operatorCodespace = null;
        if (configuration.getValidCodespaces() != null) {
            Map<String, Codespace> validCodespaces = new HashMap<>();
            String[] validCodespacesTuples = StringUtils.split(configuration.getValidCodespaces(), ",");

            for (int i = 0; i < validCodespacesTuples.length; i += 2) {
                Codespace codespace = netexFactory.createCodespace()
                        .withId(validCodespacesTuples[i].toLowerCase())
                        .withXmlns(validCodespacesTuples[i])
                        .withXmlnsUrl(validCodespacesTuples[i + 1]);
                validCodespaces.put(validCodespacesTuples[i].toUpperCase(), codespace);
            }
            if (validCodespaces.containsKey(line.objectIdPrefix().toUpperCase())) {
                operatorCodespace = validCodespaces.get(line.objectIdPrefix().toUpperCase());
            }
        } else {
            if (CODESPACE_MAP.containsKey(line.objectIdPrefix().toUpperCase())) {
                operatorCodespace = CODESPACE_MAP.get(line.objectIdPrefix().toUpperCase());
            } else {
                throw new RuntimeException("Unknown operator codespace");
            }
        }
        if (!exportableNetexData.getSharedCodespaces().containsKey(line.objectIdPrefix().toUpperCase())) {
            exportableNetexData.getSharedCodespaces().put(line.objectIdPrefix().toUpperCase(), operatorCodespace);
        }
        if (!exportableNetexData.getSharedCodespaces().containsKey(NSR_XMLNS)) {
            exportableNetexData.getSharedCodespaces().put(NSR_XMLNS, nsrCodespace);
        }
    }

    @SuppressWarnings("unchecked")
    private void produceAndCollectLineData(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        Line neptuneLine = exportableData.getLine();

        AvailabilityCondition availabilityCondition = createAvailabilityCondition(neptuneLine);
        exportableNetexData.setLineCondition(availabilityCondition);

        org.rutebanken.netex.model.Line netexLine = lineProducer.produce(context, neptuneLine);
        exportableNetexData.setLine(netexLine);

        for (mobi.chouette.model.Route neptuneRoute : neptuneLine.getRoutes()) {
            org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(context, neptuneRoute);
            exportableNetexData.getRoutes().add(netexRoute);
        }

        for (mobi.chouette.model.Route route : neptuneLine.getRoutes()) {
            for (mobi.chouette.model.JourneyPattern neptuneJourneyPattern : route.getJourneyPatterns()) {
                org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
                exportableNetexData.getJourneyPatterns().add(netexJourneyPattern);
            }
        }

        Map<String, List<? extends DataManagedObjectStructure>> calendarData = calendarProducer.produce(context, exportableData);

        List<DayType> dayTypes = (List<DayType>) calendarData.get(DAY_TYPES_KEY);
        exportableNetexData.getDayTypes().addAll(dayTypes);

        List<DayTypeAssignment> dayTypeAssignments = (List<DayTypeAssignment>) calendarData.get(DAY_TYPE_ASSIGNMENTS_KEY);
        exportableNetexData.getDayTypeAssignments().addAll(dayTypeAssignments);

        List<OperatingPeriod> operatingPeriods = (List<OperatingPeriod>) calendarData.get(OPERATING_PERIODS_KEY);
        exportableNetexData.getOperatingPeriods().addAll(operatingPeriods);

        for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
            exportableNetexData.getServiceJourneys().add(serviceJourney);
        }
    }

    @SuppressWarnings("Java8MapApi")
    private void produceAndCollectSharedData(Context context, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);

        mobi.chouette.model.Network neptuneNetwork = exportableData.getLine().getNetwork();
        org.rutebanken.netex.model.Network netexNetwork = exportableNetexData.getSharedNetworks().get(neptuneNetwork.getObjectId());

        if (netexNetwork == null) {
            netexNetwork = networkProducer.produce(context, neptuneNetwork);
            exportableNetexData.getSharedNetworks().put(neptuneNetwork.getObjectId(), netexNetwork);
        }
        if (CollectionUtils.isNotEmpty(exportableData.getLine().getGroupOfLines())) {
            GroupOfLine groupOfLine = exportableData.getLine().getGroupOfLines().get(0);
            GroupOfLines groupOfLines = exportableNetexData.getSharedGroupsOfLines().get(groupOfLine.getObjectId());

            if (groupOfLines == null) {
                groupOfLines = createGroupOfLines(groupOfLine);
                exportableNetexData.getSharedGroupsOfLines().put(groupOfLine.getObjectId(), groupOfLines);
            }
            if (netexNetwork.getGroupsOfLines() == null) {
                netexNetwork.setGroupsOfLines(netexFactory.createGroupsOfLinesInFrame_RelStructure());
            }
            if (!netexNetwork.getGroupsOfLines().getGroupOfLines().contains(groupOfLines)) {
                netexNetwork.getGroupsOfLines().getGroupOfLines().add(groupOfLines);
            }
        }

        AvailabilityCondition availabilityCondition = createAvailabilityCondition(neptuneNetwork);
        exportableNetexData.setCommonCondition(availabilityCondition);
        String sourceIdentifier = neptuneNetwork.getSourceIdentifier();

        if (isSet(sourceIdentifier) && idStructurePattern.matcher(sourceIdentifier).matches()) {
            if (!exportableNetexData.getSharedAuthorities().containsKey(sourceIdentifier)) {
                Authority networkAuthority = createNetworkAuthority(neptuneNetwork);
                exportableNetexData.getSharedAuthorities().put(sourceIdentifier, networkAuthority);
            }
        } else {
            String version = neptuneNetwork.getObjectVersion() > 0 ? String.valueOf(neptuneNetwork.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
            String objectId = netexId(neptuneNetwork.objectIdPrefix(), AUTHORITY, neptuneNetwork.objectIdSuffix());

            if (!exportableNetexData.getSharedAuthorities().containsKey(objectId)) {
                Authority networkAuthority = createNetworkAuthority(version, objectId);
                exportableNetexData.getSharedAuthorities().put(objectId, networkAuthority);
            }
        }
        if (!exportableNetexData.getSharedAuthorities().containsKey(NSR_OBJECT_ID)) {
            Authority nsrAuthority = createNsrAuthority(neptuneNetwork);
            exportableNetexData.getSharedAuthorities().put(NSR_OBJECT_ID, nsrAuthority);
        }

        Company company = exportableData.getLine().getCompany();

        if (!exportableNetexData.getSharedOperators().containsKey(company.getObjectId())) {
            Operator operator = operatorProducer.produce(context, company);
            exportableNetexData.getSharedOperators().put(company.getObjectId(), operator);
        }

        if (configuration.isExportStops()) {
            Set<StopArea> stopAreas = new HashSet<>();
            stopAreas.addAll(exportableData.getStopPlaces());
            stopAreas.addAll(exportableData.getCommercialStops());

            for (mobi.chouette.model.StopArea stopArea : stopAreas) {
                if (!exportableNetexData.getSharedStopPlaces().containsKey(stopArea.getObjectId())) {
                    StopPlace stopPlace = stopPlaceProducer.produce(context, stopArea);
                    exportableNetexData.getSharedStopPlaces().put(stopArea.getObjectId(), stopPlace);
                }
            }
        }

        produceAndCollectRoutePoints(exportableData.getLine().getRoutes(), exportableNetexData);
        produceAndCollectScheduledStopPoints(exportableData.getLine().getRoutes(), exportableNetexData);
        produceAndCollectStopAssignments(exportableData.getLine().getRoutes(), exportableNetexData);
    }

    private GroupOfLines createGroupOfLines(GroupOfLine groupOfLine) {
        GroupOfLines groupOfLines = netexFactory.createGroupOfLines();
        groupOfLines.setVersion(groupOfLine.getObjectVersion() > 0 ? String.valueOf(groupOfLine.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        groupOfLines.setId(groupOfLine.getObjectId());

        if (isSet(groupOfLine.getName())) {
            groupOfLines.setName(getMultilingualString(groupOfLine.getName()));
        }

        return groupOfLines;
    }

    private void produceAndCollectRoutePoints(List<Route> routes, ExportableNetexData exportableNetexData) {
        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {
                if (stopPoint != null) {
                    if (isSet(stopPoint.getContainedInStopArea())) {
                        String routePointIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
                        String routePointId = netexId(route.objectIdPrefix(), ROUTE_POINT, routePointIdSuffix);

                        if (!exportableNetexData.getSharedRoutePoints().containsKey(routePointId)) {
                            RoutePoint routePoint = createRoutePoint(routePointId, stopPoint);
                            exportableNetexData.getSharedRoutePoints().put(routePointId, routePoint);
                        }
                    } else {
                        throw new RuntimeException("StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce RoutePoint.");
                    }
                }
            }
        }
    }

    private RoutePoint createRoutePoint(String routePointId, StopPoint stopPoint) {
        String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

        RoutePoint routePoint = netexFactory.createRoutePoint()
                .withVersion(pointVersion)
                .withId(routePointId);

        String containedInSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
        String stopPointIdRef = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, containedInSuffix);
        String pointProjectionId = netexId(stopPoint.objectIdPrefix(), POINT_PROJECTION, containedInSuffix);

        PointRefStructure pointRefStruct = netexFactory.createPointRefStructure().withRef(stopPointIdRef);

        PointProjection pointProjection = netexFactory.createPointProjection()
                .withVersion(pointVersion)
                .withId(pointProjectionId)
                .withProjectedPointRef(pointRefStruct);

        Projections_RelStructure projections = netexFactory.createProjections_RelStructure()
                .withProjectionRefOrProjection(netexFactory.createPointProjection(pointProjection));
        routePoint.setProjections(projections);

        return routePoint;
    }

    private void produceAndCollectScheduledStopPoints(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {

                if (stopPoint != null) {
                    if (isSet(stopPoint.getContainedInStopArea())) {
                        String scheduledStopPointIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
                        String scheduledStopPointId = netexId(stopPoint.objectIdPrefix(), SCHEDULED_STOP_POINT, scheduledStopPointIdSuffix);

                        if (!exportableNetexData.getSharedStopPoints().containsKey(scheduledStopPointId)) {
                            ScheduledStopPoint scheduledStopPoint = createScheduledStopPoint(stopPoint, scheduledStopPointId);
                            exportableNetexData.getSharedStopPoints().put(scheduledStopPointId, scheduledStopPoint);
                        }
                    } else {
                        throw new RuntimeException("StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce ScheduledStopPoint.");
                    }
                }
            }
        }
    }

    private ScheduledStopPoint createScheduledStopPoint(StopPoint stopPoint, String stopPointId) {
        Integer objectVersion = stopPoint.getContainedInStopArea().getObjectVersion();
        ScheduledStopPoint scheduledStopPoint = netexFactory.createScheduledStopPoint();
        scheduledStopPoint.setVersion(objectVersion > 0 ? String.valueOf(objectVersion) : NETEX_DATA_OJBECT_VERSION);
        scheduledStopPoint.setId(stopPointId);

        if (isSet(stopPoint.getContainedInStopArea().getName())) {
            scheduledStopPoint.setName(getMultilingualString(stopPoint.getContainedInStopArea().getName()));
        }

        return scheduledStopPoint;
    }

    private void produceAndCollectStopAssignments(List<mobi.chouette.model.Route> routes, ExportableNetexData exportableNetexData) {
        int index = 1;
        for (mobi.chouette.model.Route route : routes) {
            for (StopPoint stopPoint : route.getStopPoints()) {

                if (stopPoint != null) {
                    if (isSet(stopPoint.getContainedInStopArea())) {
                        String stopAssignmentIdSuffix = stopPoint.getContainedInStopArea().objectIdSuffix();
                        String stopAssignmentId = netexId(stopPoint.objectIdPrefix(), PASSENGER_STOP_ASSIGNMENT, stopAssignmentIdSuffix);

                        if (!exportableNetexData.getSharedStopAssignments().containsKey(stopAssignmentId)) {
                            PassengerStopAssignment stopAssignment = createStopAssignmentNew(stopPoint, stopAssignmentId, index);
                            exportableNetexData.getSharedStopAssignments().put(stopAssignmentId, stopAssignment);
                            index++;
                        }
                    } else {
                        throw new RuntimeException("StopPoint with id : " + stopPoint.getObjectId() + " is not contained in a StopArea. Cannot produce StopAssignment.");
                    }
                }
            }
        }
    }

    private PassengerStopAssignment createStopAssignmentNew(StopPoint stopPoint, String stopAssignmentId, int order) {
        String pointVersion = stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

        PassengerStopAssignment stopAssignment = netexFactory.createPassengerStopAssignment()
                .withVersion(pointVersion)
                .withId(stopAssignmentId)
                .withOrder(new BigInteger(Integer.toString(order)));

        String stopPointIdRef = netexId(stopPoint.getContainedInStopArea().objectIdPrefix(), SCHEDULED_STOP_POINT, stopPoint.getContainedInStopArea().objectIdSuffix());

        ScheduledStopPointRefStructure scheduledStopPointRefStruct = netexFactory.createScheduledStopPointRefStructure().withRef(stopPointIdRef);
        stopAssignment.setScheduledStopPointRef(scheduledStopPointRefStruct);

        if (isSet(stopPoint.getContainedInStopArea())) {
            if (isSet(stopPoint.getContainedInStopArea().getParent())) {
                mobi.chouette.model.StopArea parentStopArea = stopPoint.getContainedInStopArea().getParent();
                String stopPlaceIdRef = netexId(parentStopArea.objectIdPrefix(), STOP_PLACE, parentStopArea.objectIdSuffix());

                StopPlaceRefStructure stopPlaceRefStruct = netexFactory.createStopPlaceRefStructure().withRef(stopPlaceIdRef);
                stopAssignment.setStopPlaceRef(stopPlaceRefStruct);
            }

            mobi.chouette.model.StopArea containedInStopArea = stopPoint.getContainedInStopArea();
            String quayIdRef = netexId(containedInStopArea.objectIdPrefix(), QUAY, containedInStopArea.objectIdSuffix());

            QuayRefStructure quayRefStruct = netexFactory.createQuayRefStructure().withRef(quayIdRef);
            stopAssignment.setQuayRef(quayRefStruct);
        }

        return stopAssignment;
    }

    private Authority createNetworkAuthority(Network network) {
        return createNetworkAuthority(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) :
                NETEX_DATA_OJBECT_VERSION, network.getSourceIdentifier());
    }

    private Authority createNetworkAuthority(String version, String objectId) {
        return netexFactory.createAuthority()
                .withVersion(version)
                .withId(objectId)
                .withCompanyNumber("999999999")
                .withName(getMultilingualString("Dummy Authority"))
                .withLegalName(getMultilingualString("DUMMY AUTHORITY"))
                .withContactDetails(createContactStructure("0047 999 99 999", "http://www.dummy-authority.org/"))
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
    }

    private Authority createNsrAuthority(Network network) {
        return netexFactory.createAuthority()
                .withVersion(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION)
                .withId(NSR_OBJECT_ID)
                .withCompanyNumber(NSR_COMPANY_NUMBER)
                .withName(getMultilingualString(NSR_NAME))
                .withLegalName(getMultilingualString(NSR_LEGAL_NAME))
                .withContactDetails(createContactStructure(NSR_PHONE, NSR_XMLNSURL))
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
    }

    private ContactStructure createContactStructure(String phone, String url) {
        return netexFactory.createContactStructure()
                .withPhone(phone)
                .withUrl(url);
    }

    static {
        try {
            Properties properties = new Properties();
            properties.load(NetexLineDataProducer.class.getResourceAsStream("/codespaces.properties"));
            Set<String> propertyKeys = properties.stringPropertyNames();

            for (String key : propertyKeys) {
                Codespace codespace = netexFactory.createCodespace()
                        .withId(key.toLowerCase())
                        .withXmlns(key)
                        .withXmlnsUrl(properties.getProperty(key));
                CODESPACE_MAP.put(key, codespace);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load codespaces from file");
        }
    }

}
