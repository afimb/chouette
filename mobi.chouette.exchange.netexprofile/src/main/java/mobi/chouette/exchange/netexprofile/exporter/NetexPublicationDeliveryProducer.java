package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.neptune.model.TimeSlot;
import mobi.chouette.exchange.netexprofile.exporter.producer.*;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Company;
import mobi.chouette.model.JourneyFrequency;
import mobi.chouette.model.StopPoint;
import org.rutebanken.netex.model.*;
import org.trident.schema.trident.TimeSlotType;
import org.trident.schema.trident.VehicleJourneyType;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractJaxbNetexProducer.netexFactory;

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

    private static NetworkProducer networkProducer = new NetworkProducer();
    private static OperatorProducer operatorProducer = new OperatorProducer();
    private static LineProducer lineProducer = new LineProducer();
    private static RouteProducer routeProducer = new RouteProducer();
    private static RoutePointProducer routePointProducer = new RoutePointProducer();
    private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        String rootDirectory = jobData.getPathName();

        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(CONFIGURATION);
        boolean addExtension = parameters.isAddExtension(); // TODO find out if needed?

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
                .withDescription(netexFactory.createMultilingualString().withValue(collection.getLine().getName()));

        CompositeFrame compositeFrame = netexFactory.createCompositeFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withCreated(publicationTimestamp)
                .withId("AVI:CompositeFrame:1"); // TODO set as <airline-iata>_<line-id>
                //.withValidityConditions(validityConditionsStruct) // TODO
                //.withCodespaces(codespaces) // TODO

        // TODO create codespace here

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
        ResourceFrame resourceFrame = netexFactory.createResourceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ResourceFrame:1");
        frames.getCommonFrame().add(netexFactory.createResourceFrame(resourceFrame));

        OrganisationsInFrame_RelStructure organisationsStruct = netexFactory.createOrganisationsInFrame_RelStructure();
        for (Company company : collection.getCompanies()) {
            Operator operator = operatorProducer.produce(company, addExtension);
            organisationsStruct.getOrganisation_().add(netexFactory.createOperator(operator));
        }
        resourceFrame.setOrganisations(organisationsStruct);

        // service frame
        ServiceFrame serviceFrame = netexFactory.createServiceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ServiceFrame:1");
                //.withDestinationDisplays(destinationDisplayStruct)
        frames.getCommonFrame().add(netexFactory.createServiceFrame(serviceFrame));

        if (collection.getLine().getNetwork() != null) {
            serviceFrame.setNetwork(networkProducer.produce(collection.getLine().getNetwork(), addExtension));
        }

        Line line = lineProducer.produce(collection.getLine(), collection.getRoutes(), addExtension);
        LinesInFrame_RelStructure linesInFrameStruct = netexFactory.createLinesInFrame_RelStructure();
        linesInFrameStruct.getLine_().add(netexFactory.createLine(line));
        serviceFrame.setLines(linesInFrameStruct);

        RoutesInFrame_RelStructure routesInFrame = netexFactory.createRoutesInFrame_RelStructure();
        for (mobi.chouette.model.Route chouetteRoute : collection.getRoutes()) {
            org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(chouetteRoute, collection.getRoutes(), addExtension);
            routesInFrame.getRoute_().add(netexFactory.createRoute(netexRoute));

            // TODO consider adding the route reference to the line here, instead of inside the LineProducer
            //chouetteLineDescription.getChouetteRoute().add(jaxbObj);
        }
        serviceFrame.setRoutes(routesInFrame);

        RoutePointsInFrame_RelStructure routePointsInFrame = netexFactory.createRoutePointsInFrame_RelStructure();
        for (StopPoint stopPoint : collection.getStopPoints()) {
            RoutePoint routePoint = routePointProducer.produce(stopPoint, addExtension);
            routePointsInFrame.getRoutePoint().add(routePoint);
        }
        //serviceFrame.setRoutePoints(routePointsInFrame);

        JourneyPatternsInFrame_RelStructure journeyPatternsInFrame = netexFactory.createJourneyPatternsInFrame_RelStructure();
        for (mobi.chouette.model.JourneyPattern chouetteJourneyPattern : collection.getJourneyPatterns()) {
            org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(chouetteJourneyPattern, collection.getRoutes(), addExtension);
            journeyPatternsInFrame.getJourneyPattern_OrJourneyPatternView().add(netexFactory.createJourneyPattern(netexJourneyPattern));
        }
        serviceFrame.setJourneyPatterns(journeyPatternsInFrame);

        TimetableFrame timetableFrame = netexFactory.createTimetableFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:TimetableFrame:1");
                //.withVehicleJourneys(journeysInFrameRelStructure);
        frames.getCommonFrame().add(netexFactory.createTimetableFrame(timetableFrame));

        JourneysInFrame_RelStructure journeysInFrame = netexFactory.createJourneysInFrame_RelStructure();
        for (mobi.chouette.model.VehicleJourney vehicleJourney : collection.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(vehicleJourney, collection.getLine(), addExtension);
            journeysInFrame.getDatedServiceJourneyOrDeadRunOrServiceJourney().add(serviceJourney);
        }
        timetableFrame.setVehicleJourneys(journeysInFrame);

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ServiceCalendarFrame:1");
                //.withDayTypes(dayTypesStruct)
                //.withDayTypeAssignments(dayTypeAssignmentsStruct);
        frames.getCommonFrame().add(netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

        PublicationDeliveryStructure.DataObjects dataObjects = netexFactory.createPublicationDeliveryStructureDataObjects();
        dataObjects.getCompositeFrameOrCommonFrame().add(netexFactory.createCompositeFrame(compositeFrame));
        rootObject.setDataObjects(dataObjects);

        Path dir = Paths.get(rootDirectory, OUTPUT);
        String fileName = collection.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        File file = new File(dir.toFile(), fileName);

        JaxbNetexFileConverter writer = JaxbNetexFileConverter.getInstance();
        writer.write(netexFactory.createPublicationDelivery(rootObject), file);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(collection.getLine())));
        }
    }

}
