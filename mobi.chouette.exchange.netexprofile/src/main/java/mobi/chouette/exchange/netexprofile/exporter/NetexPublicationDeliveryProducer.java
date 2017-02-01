package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.AbstractJaxbNetexProducer;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

public class NetexPublicationDeliveryProducer implements Constant {

    // TODO move the following to some common Constant class
    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    private static final String VERSION_ONE = "1";
    public static final String DEFAULT_ZONE_ID = "UTC";
    public static final String DEFAULT_LANGUAGE = "no";

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        String rootDirectory = jobData.getPathName();

        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(CONFIGURATION);
        boolean addExtension = parameters.isAddExtension();

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

        PublicationDeliveryStructure rootObject = AbstractJaxbNetexProducer.netexFactory.createPublicationDeliveryStructure()
                .withVersion(NETEX_PROFILE_VERSION)
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef("NSR")
                .withDescription(AbstractJaxbNetexProducer.netexFactory.createMultilingualString().withValue(collection.getLine().getName()));

        LocaleStructure localeStructure = AbstractJaxbNetexProducer.netexFactory.createLocaleStructure()
                .withTimeZone(DEFAULT_ZONE_ID)
                .withDefaultLanguage(DEFAULT_LANGUAGE);

        VersionFrameDefaultsStructure frameDefaultsStruct = AbstractJaxbNetexProducer.netexFactory.createVersionFrameDefaultsStructure()
                .withDefaultLocale(localeStructure);

        Frames_RelStructure frames = AbstractJaxbNetexProducer.netexFactory.createFrames_RelStructure();

        ResourceFrame resourceFrame = AbstractJaxbNetexProducer.netexFactory.createResourceFrame()
                .withVersion(VERSION_ONE)
                .withId("AVI:ResourceFrame:1");
        frames.getCommonFrame().add(AbstractJaxbNetexProducer.netexFactory.createResourceFrame(resourceFrame));

        ServiceFrame serviceFrame = AbstractJaxbNetexProducer.netexFactory.createServiceFrame()
                .withVersion(VERSION_ONE)
                .withId("AVI:ServiceFrame:1");
                //.withNetwork(network)
                //.withRoutePoints(routePointsInFrame)
                //.withRoutes(routesInFrame)
                //.withLines(linesInFrame)
                //.withDestinationDisplays(destinationDisplayStruct)
                //.withJourneyPatterns(journeyPatternsInFrame);
        frames.getCommonFrame().add(AbstractJaxbNetexProducer.netexFactory.createServiceFrame(serviceFrame));

        TimetableFrame timetableFrame = AbstractJaxbNetexProducer.netexFactory.createTimetableFrame()
                .withVersion(VERSION_ONE)
                .withId("AVI:TimetableFrame:1");
                //.withVehicleJourneys(journeysInFrameRelStructure);
        frames.getCommonFrame().add(AbstractJaxbNetexProducer.netexFactory.createTimetableFrame(timetableFrame));

        ServiceCalendarFrame serviceCalendarFrame = AbstractJaxbNetexProducer.netexFactory.createServiceCalendarFrame()
                .withVersion(VERSION_ONE)
                .withId("AVI:ServiceCalendarFrame:1");
                //.withDayTypes(dayTypesStruct)
                //.withDayTypeAssignments(dayTypeAssignmentsStruct);
        frames.getCommonFrame().add(AbstractJaxbNetexProducer.netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

        CompositeFrame compositeFrame = AbstractJaxbNetexProducer.netexFactory.createCompositeFrame()
                .withVersion(VERSION_ONE)
                //.withCreated(publicationTimestamp)
                .withId("AVI:CompositeFrame:1")
                //.withValidityConditions(validityConditionsStruct)
                //.withCodespaces(codespaces)
                .withFrameDefaults(frameDefaultsStruct)
                .withFrames(frames);

        JAXBElement<CompositeFrame> compositeFrameElement = AbstractJaxbNetexProducer.netexFactory.createCompositeFrame(compositeFrame);

        PublicationDeliveryStructure.DataObjects dataObjects = AbstractJaxbNetexProducer.netexFactory.createPublicationDeliveryStructureDataObjects();
        dataObjects.getCompositeFrameOrCommonFrame().add(compositeFrameElement);
        rootObject.setDataObjects(dataObjects);

        Path dir = Paths.get(rootDirectory, OUTPUT);
        String fileName = collection.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        File file = new File(dir.toFile(), fileName);

        JaxbNetexFileConverter writer = JaxbNetexFileConverter.getInstance();
        writer.write(AbstractJaxbNetexProducer.netexFactory.createPublicationDelivery(rootObject), file);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(collection.getLine())));
        }
    }

}
