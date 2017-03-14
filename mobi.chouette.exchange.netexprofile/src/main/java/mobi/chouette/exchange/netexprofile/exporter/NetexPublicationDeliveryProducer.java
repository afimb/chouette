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
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AVAILABILITY_CONDITION_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.COMPOSITE_FRAME_KEY;

public class NetexPublicationDeliveryProducer extends NetexProducer implements Constant {

    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    public static final String NETEX_DATA_OJBECT_VERSION = "1";
    public static final String DEFAULT_ZONE_ID = "UTC";
    public static final String DEFAULT_LANGUAGE = "no";

    public static final String NSR_XMLNS = "NSR";
    public static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";

    public static final String AVINOR_XMLNS = "AVI";
    public static final String AVINOR_XMLNSURL = "http://www.rutebanken.org/ns/avi";

    public static final String RUTER_XMLNS = "RUT";
    public static final String RUTER_XMLNSURL = "http://www.rutebanken.org/ns/rut";

    private static ResourceFrameProducer resourceFrameProducer = new ResourceFrameProducer();
    private static SiteFrameProducer siteFrameProducer = new SiteFrameProducer();
    private static ServiceFrameProducer serviceFrameProducer = new ServiceFrameProducer();
    private static TimetableFrameProducer timetableFrameProducer = new TimetableFrameProducer();
    private static ServiceCalendarFrameProducer serviceCalendarFrameProducer = new ServiceCalendarFrameProducer();

    private static final Map<String, Codespace> codespaceMapping = new HashMap<>();

    static {
        codespaceMapping.put("AVI", netexFactory.createCodespace().withId(AVINOR_XMLNS.toLowerCase()).withXmlns(AVINOR_XMLNS).withXmlnsUrl(AVINOR_XMLNSURL));
        codespaceMapping.put("RUT", netexFactory.createCodespace().withId(RUTER_XMLNS.toLowerCase()).withXmlns(RUTER_XMLNS).withXmlnsUrl(RUTER_XMLNSURL));
    }

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
                .withParticipantRef(NSR_XMLNS)
                .withDescription(netexFactory.createMultilingualString().withValue(exportableData.getLine().getName()));

        String compositeFrameId = netexId(line.objectIdPrefix(), COMPOSITE_FRAME_KEY, line.objectIdSuffix());

        String availabilityConditionId = netexId(line.objectIdPrefix(), AVAILABILITY_CONDITION_KEY, line.objectIdSuffix());
        AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
        availabilityCondition.setVersion(line.getObjectVersion() > 0 ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        availabilityCondition.setId(availabilityConditionId);

        availabilityCondition.setFromDate(OffsetDateTime.now(ZoneId.systemDefault())); // TODO fix correct from date, for now using dummy dates
        availabilityCondition.setToDate(availabilityCondition.getFromDate().plusMonths(1L)); // TODO fix correct to date, for now using dummy dates

        ValidityConditions_RelStructure validityConditionsStruct = netexFactory.createValidityConditions_RelStructure()
                .withValidityConditionRefOrValidBetweenOrValidityCondition_(netexFactory.createAvailabilityCondition(availabilityCondition));

        CompositeFrame compositeFrame = netexFactory.createCompositeFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(compositeFrameId)
                .withValidityConditions(validityConditionsStruct);

        if (line.getNetwork().getVersionDate() != null) {
            OffsetDateTime createdDateTime = NetexProducerUtils.toOffsetDateTime(line.getNetwork().getVersionDate());
            compositeFrame.setCreated(createdDateTime);
        } else {
            compositeFrame.setCreated(publicationTimestamp);
        }

        Codespace nsrCodespace = netexFactory.createCodespace()
                .withId(NSR_XMLNS.toLowerCase())
                .withXmlns(NSR_XMLNS)
                .withXmlnsUrl(NSR_XMLNSURL);

        Codespace operatorCodespace = codespaceMapping.get(line.objectIdPrefix().toUpperCase());

        Codespaces_RelStructure codespaces = netexFactory.createCodespaces_RelStructure()
                .withCodespaceRefOrCodespace(Arrays.asList(operatorCodespace, nsrCodespace));
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
        frames.getCommonFrame().add(netexFactory.createServiceCalendarFrame(serviceCalendarFrame));

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
