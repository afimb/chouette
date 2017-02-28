package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.*;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.rutebanken.netex.model.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.COMPOSITE_FRAME_KEY;

public class NetexPublicationDeliveryProducer extends NetexProducer implements Constant {

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
    private static ServiceFrameProducer serviceFrameProducer = new ServiceFrameProducer();
    private static TimetableFrameProducer timetableFrameProducer = new TimetableFrameProducer();
    private static ServiceCalendarFrameProducer serviceCalendarFrameProducer = new ServiceCalendarFrameProducer();

    // TODO consider adding producers for each frame, which in turn calls each subproducer (like netex writers)

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        Metadata metadata = (Metadata) context.get(METADATA);
        String rootDirectory = jobData.getPathName();

        OffsetDateTime publicationTimestamp = OffsetDateTime.now();
        mobi.chouette.model.Line line = exportableData.getLine();

        PublicationDeliveryStructure rootObject = netexFactory.createPublicationDeliveryStructure()
                .withVersion(NETEX_PROFILE_VERSION)
                .withPublicationTimestamp(publicationTimestamp)
                .withParticipantRef("NSR")
                .withDescription(netexFactory.createMultilingualString().withValue(exportableData.getLine().getName()));

        String compositeFrameId = netexId(line.objectIdPrefix(), COMPOSITE_FRAME_KEY, line.objectIdSuffix());

        CompositeFrame compositeFrame = netexFactory.createCompositeFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(compositeFrameId);
                //.withValidityConditions(validityConditionsStruct) // TODO

        if (line.getNetwork().getVersionDate() != null) {
            OffsetDateTime createdDateTime = NetexProducerUtils.toOffsetDateTime(line.getNetwork().getVersionDate());
            compositeFrame.setCreated(createdDateTime);
        } else {
            compositeFrame.setCreated(publicationTimestamp);
        }

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
        ServiceFrame serviceFrame = serviceFrameProducer.produce(exportableData);
        frames.getCommonFrame().add(netexFactory.createServiceFrame(serviceFrame));

        // timetable frame
        TimetableFrame timetableFrame = timetableFrameProducer.produce(exportableData);
        frames.getCommonFrame().add(netexFactory.createTimetableFrame(timetableFrame));

        // service calendar frame
        ServiceCalendarFrame serviceCalendarFrame = serviceCalendarFrameProducer.produce(exportableData);
        //frames.getCommonFrame().add(netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

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
