package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetworkProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.OperatorProducer;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Company;
import org.rutebanken.netex.model.*;
import org.trident.schema.trident.CompanyType;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractJaxbNetexProducer.netexFactory;

public class NetexPublicationDeliveryProducer implements Constant {

    // TODO move the following to some common Constant class
    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    public static final String NETEX_DATA_OJBECT_VERSION = "1";
    public static final String DEFAULT_ZONE_ID = "UTC";
    public static final String DEFAULT_LANGUAGE = "no";

    private static NetworkProducer networkProducer = new NetworkProducer();
    private static OperatorProducer operatorProducer = new OperatorProducer();

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

        PublicationDeliveryStructure rootObject = netexFactory.createPublicationDeliveryStructure()
                .withVersion(NETEX_PROFILE_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef("NSR")
                .withDescription(netexFactory.createMultilingualString().withValue(collection.getLine().getName()));

        LocaleStructure localeStructure = netexFactory.createLocaleStructure()
                .withTimeZone(DEFAULT_ZONE_ID)
                .withDefaultLanguage(DEFAULT_LANGUAGE);

        VersionFrameDefaultsStructure frameDefaultsStruct = netexFactory.createVersionFrameDefaultsStructure()
                .withDefaultLocale(localeStructure);

        Frames_RelStructure frames = netexFactory.createFrames_RelStructure();

        // resource frame
        ResourceFrame resourceFrame = netexFactory.createResourceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ResourceFrame:1");

        OrganisationsInFrame_RelStructure organisationsStruct = netexFactory.createOrganisationsInFrame_RelStructure();
        for (Company company : collection.getCompanies()) {
            Operator operator = operatorProducer.produce(company, addExtension);
            organisationsStruct.getOrganisation_().add(netexFactory.createOperator(operator));
        }
        resourceFrame.setOrganisations(organisationsStruct);

        frames.getCommonFrame().add(netexFactory.createResourceFrame(resourceFrame));

        // service frame
        ServiceFrame serviceFrame = netexFactory.createServiceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ServiceFrame:1");
                //.withNetwork(network)
                //.withRoutePoints(routePointsInFrame)
                //.withRoutes(routesInFrame)
                //.withLines(linesInFrame)
                //.withDestinationDisplays(destinationDisplayStruct)
                //.withJourneyPatterns(journeyPatternsInFrame);

        if (collection.getLine().getNetwork() != null) {
            serviceFrame.setNetwork(networkProducer.produce(collection.getLine().getNetwork(), addExtension));
        }

        frames.getCommonFrame().add(netexFactory.createServiceFrame(serviceFrame));

        TimetableFrame timetableFrame = netexFactory.createTimetableFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:TimetableFrame:1");
                //.withVehicleJourneys(journeysInFrameRelStructure);
        frames.getCommonFrame().add(netexFactory.createTimetableFrame(timetableFrame));

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId("AVI:ServiceCalendarFrame:1");
                //.withDayTypes(dayTypesStruct)
                //.withDayTypeAssignments(dayTypeAssignmentsStruct);
        frames.getCommonFrame().add(netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

        CompositeFrame compositeFrame = netexFactory.createCompositeFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                //.withCreated(publicationTimestamp)
                .withId("AVI:CompositeFrame:1")
                //.withValidityConditions(validityConditionsStruct)
                //.withCodespaces(codespaces)
                .withFrameDefaults(frameDefaultsStruct)
                .withFrames(frames);

        JAXBElement<CompositeFrame> compositeFrameElement = netexFactory.createCompositeFrame(compositeFrame);

        PublicationDeliveryStructure.DataObjects dataObjects = netexFactory.createPublicationDeliveryStructureDataObjects();
        dataObjects.getCompositeFrameOrCommonFrame().add(compositeFrameElement);
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
