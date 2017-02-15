package mobi.chouette.exchange.netexprofile.exporter;

import com.google.common.collect.Lists;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.*;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.StopPoint;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractNetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class NetexPublicationDeliveryProducer implements Constant {

    // TODO move the following to some common Constant class
    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    public static final String NETEX_DATA_OJBECT_VERSION = "1";
    public static final String DEFAULT_ZONE_ID = "UTC";
    public static final String DEFAULT_LANGUAGE = "no";

    // TODO make the following part of dynamic codespace mapping
    public static final String NSR_XMLNS = "NSR";
    public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";

    public static final String AVINOR_XMLNS = "AVI";
    public static final String AVINOR_XMLNSURL = "http://www.rutebanken.org/ns/avi";

    private static ResourceFrameProducer resourceFrameProducer = new ResourceFrameProducer();
    private static SiteFrameProducer siteFrameProducer = new SiteFrameProducer();

    private static NetworkProducer networkProducer = new NetworkProducer();
    private static LineProducer lineProducer = new LineProducer();
    private static RouteProducer routeProducer = new RouteProducer();
    private static RoutePointProducer routePointProducer = new RoutePointProducer();
    private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    // TODO consider adding producers for each frame, which in turn calls each subproducer (like netex writers)

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        String rootDirectory = jobData.getPathName();

        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(CONFIGURATION);
        boolean addExtension = parameters.isAddExtension(); // TODO find out if needed?

        mobi.chouette.model.Line line = exportableData.getLine();

        String projectionType = parameters.getProjectionType();
        if (projectionType != null && !projectionType.isEmpty()) {
            if (!projectionType.toUpperCase().startsWith("EPSG:")) {
                projectionType = "EPSG:" + projectionType;
            }
        }

        // TODO find out if it is mandatory to set projection on stop areas
        /*
        for (StopArea stopArea : collection.getStopAreas()) {
            stopArea.toProjection(projectionType);
        }
        */

        Metadata metadata = (Metadata) context.get(METADATA);
        OffsetDateTime publicationTimestamp = OffsetDateTime.now();

        PublicationDeliveryStructure rootObject = netexFactory.createPublicationDeliveryStructure()
                .withVersion(NETEX_PROFILE_VERSION)
                .withPublicationTimestamp(publicationTimestamp)
                .withParticipantRef("NSR")
                .withDescription(netexFactory.createMultilingualString().withValue(exportableData.getLine().getName()));

        String compositeFrameId = ModelTranslator.netexId(line.objectIdPrefix(), COMPOSITE_FRAME_KEY, line.objectIdSuffix());

        CompositeFrame compositeFrame = netexFactory.createCompositeFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(compositeFrameId);
                //.withValidityConditions(validityConditionsStruct) // TODO

        if (line.getNetwork().getVersionDate() != null) {
            OffsetDateTime createdDateTime = NetexProducerUtils.convertToOffsetDateTime(line.getNetwork().getVersionDate());
            compositeFrame.setCreated(createdDateTime);
        } else {
            compositeFrame.setCreated(publicationTimestamp);
        }

        // TODO add validity conditions here, based on Timetable period

/*
        String availabilityConditionId = ""; // TODO create id

        Set<Timetable> timetables = collection.getTimetables();
        AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(availabilityConditionId)
                .withFromDate(OffsetDateTime.now())
                .withToDate(OffsetDateTime.now().plusMonths(1));
        netexFactory.createAvailabilityCondition(availabilityCondition);

        ValidityConditions_RelStructure validityConditionsStruct = netexFactory.createValidityConditions_RelStructure()
                .withValidityConditionRefOrValidBetweenOrValidityCondition_(availabilityConditionId);
*/

        // TODO get dynamic codespaces based on id prefix from static structure, see : https://rutebanken.atlassian.net/wiki/display/PUBLIC/Codespace
        Codespace nsrCodespace = netexFactory.createCodespace()
                .withId(NSR_XMLNS.toLowerCase())
                .withXmlns(NSR_XMLNS)
                .withXmlnsUrl(NSR_XMLNSURL);
        Codespace avinorCodespace = netexFactory.createCodespace()
                .withId(AVINOR_XMLNS.toLowerCase())
                .withXmlns(AVINOR_XMLNS)
                .withXmlnsUrl(AVINOR_XMLNSURL);
        Codespaces_RelStructure codespaces = netexFactory.createCodespaces_RelStructure()
                .withCodespaceRefOrCodespace(Arrays.asList(avinorCodespace, nsrCodespace));
        compositeFrame.setCodespaces(codespaces);

        VersionFrameDefaultsStructure frameDefaultsStruct = netexFactory.createVersionFrameDefaultsStructure();
        compositeFrame.setFrameDefaults(frameDefaultsStruct);

        LocaleStructure localeStructure = netexFactory.createLocaleStructure()
                .withTimeZone(DEFAULT_ZONE_ID)
                .withDefaultLanguage(DEFAULT_LANGUAGE);
        frameDefaultsStruct.setDefaultLocale(localeStructure);

        Frames_RelStructure frames = netexFactory.createFrames_RelStructure();
        compositeFrame.setFrames(frames);

        // resource frame
        ResourceFrame resourceFrame = resourceFrameProducer.produce(exportableData);
        frames.getCommonFrame().add(netexFactory.createResourceFrame(resourceFrame));

        // site frame

        SiteFrame siteFrame = siteFrameProducer.produce(exportableData);
        frames.getCommonFrame().add(netexFactory.createSiteFrame(siteFrame));

        // service frame
        String serviceFrameId = ModelTranslator.netexId(
                exportableData.getLine().objectIdPrefix(), SERVICE_FRAME_KEY, exportableData.getLine().objectIdSuffix());

        ServiceFrame serviceFrame = netexFactory.createServiceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(serviceFrameId);
                //.withDestinationDisplays(destinationDisplayStruct)
        frames.getCommonFrame().add(netexFactory.createServiceFrame(serviceFrame));

        if (exportableData.getLine().getNetwork() != null) {
            serviceFrame.setNetwork(networkProducer.produce(exportableData.getLine().getNetwork(), addExtension));
        }

        org.rutebanken.netex.model.Line netexLine = lineProducer.produce(exportableData.getLine(), exportableData.getRoutes(), addExtension);
        LinesInFrame_RelStructure linesInFrameStruct = netexFactory.createLinesInFrame_RelStructure();
        linesInFrameStruct.getLine_().add(netexFactory.createLine(netexLine));
        serviceFrame.setLines(linesInFrameStruct);

        RoutesInFrame_RelStructure routesInFrame = netexFactory.createRoutesInFrame_RelStructure();
        for (mobi.chouette.model.Route chouetteRoute : exportableData.getRoutes()) {
            org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(chouetteRoute, exportableData.getRoutes(), addExtension);
            routesInFrame.getRoute_().add(netexFactory.createRoute(netexRoute));

            // TODO consider adding the route reference to the line here, instead of inside the LineProducer
            //chouetteLineDescription.getChouetteRoute().add(jaxbObj);
        }
        serviceFrame.setRoutes(routesInFrame);

        RoutePointsInFrame_RelStructure routePointsInFrame = netexFactory.createRoutePointsInFrame_RelStructure();
        for (StopPoint stopPoint : exportableData.getStopPoints()) {
            RoutePoint routePoint = routePointProducer.produce(stopPoint, addExtension);
            routePointsInFrame.getRoutePoint().add(routePoint);
        }
        //serviceFrame.setRoutePoints(routePointsInFrame);

        JourneyPatternsInFrame_RelStructure journeyPatternsInFrame = netexFactory.createJourneyPatternsInFrame_RelStructure();
        for (mobi.chouette.model.JourneyPattern chouetteJourneyPattern : exportableData.getJourneyPatterns()) {
            org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(chouetteJourneyPattern, exportableData.getRoutes(), addExtension);
            journeyPatternsInFrame.getJourneyPattern_OrJourneyPatternView().add(netexFactory.createJourneyPattern(netexJourneyPattern));
        }
        serviceFrame.setJourneyPatterns(journeyPatternsInFrame);

        String timetableFrameId = ModelTranslator.netexId(
                exportableData.getLine().objectIdPrefix(), TIMETABLE_FRAME_KEY, exportableData.getLine().objectIdSuffix());

        TimetableFrame timetableFrame = netexFactory.createTimetableFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(timetableFrameId);
        frames.getCommonFrame().add(netexFactory.createTimetableFrame(timetableFrame));

        JourneysInFrame_RelStructure journeysInFrame = netexFactory.createJourneysInFrame_RelStructure();
        for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(vehicleJourney, exportableData.getLine(), addExtension);
            journeysInFrame.getDatedServiceJourneyOrDeadRunOrServiceJourney().add(serviceJourney);
        }
        timetableFrame.setVehicleJourneys(journeysInFrame);

        String serviceCalendarFrameId = ModelTranslator.netexId(
                exportableData.getLine().objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, exportableData.getLine().objectIdSuffix());

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(serviceCalendarFrameId);
                //.withDayTypes(dayTypesStruct)
                //.withDayTypeAssignments(dayTypeAssignmentsStruct);
        frames.getCommonFrame().add(netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

/*
        for (Timetable timetable : collection.getTimetables()) {
            timetable.computeLimitOfPeriods();

            TimetableType jaxbObj = timetableProducer.produce(timetable, addExtension);
            rootObject.getTimetable().add(jaxbObj);
            // add vehiclejourney only for exported ones
            for (mobi.chouette.model.VehicleJourney vehicleJourney : collection.getVehicleJourneys()) {
                if (vehicleJourney.getTimetables().contains(timetable)) {
                    jaxbObj.getVehicleJourneyId().add(vehicleJourney.getObjectId());
                }
            }
            if (metadata != null)
                metadata.getTemporalCoverage().update(timetable.getStartOfPeriod(), timetable.getEndOfPeriod());
        }
*/

        PublicationDeliveryStructure.DataObjects dataObjects = netexFactory.createPublicationDeliveryStructureDataObjects();
        dataObjects.getCompositeFrameOrCommonFrame().add(netexFactory.createCompositeFrame(compositeFrame));
        rootObject.setDataObjects(dataObjects);

        Path dir = Paths.get(rootDirectory, OUTPUT);
        String fileName = exportableData.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        File file = new File(dir.toFile(), fileName);

        JaxbNetexFileConverter writer = JaxbNetexFileConverter.getInstance();
        writer.write(netexFactory.createPublicationDelivery(rootObject), file);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(exportableData.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(exportableData.getLine())));
        }
    }

}
