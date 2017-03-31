package mobi.chouette.exchange.netexprofile.exporter;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.*;
import mobi.chouette.exchange.netexprofile.jaxb.NetexXmlStreamMarshaller;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

@Log4j
public class NetexPublicationDeliveryProducer extends NetexProducer implements Constant {

    public static final String NETEX_DATA_OJBECT_VERSION = "1";

    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    private static final String DEFAULT_ZONE_ID = "UTC";
    private static final String DEFAULT_LANGUAGE_CODE = "no";
    private static final String NSR_XMLNS = "NSR";
    private static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";

    private static ResourceFrameProducer resourceFrameProducer = new ResourceFrameProducer();
    private static SiteFrameProducer siteFrameProducer = new SiteFrameProducer();
    private static ServiceFrameProducer serviceFrameProducer = new ServiceFrameProducer();
    private static TimetableFrameProducer timetableFrameProducer = new TimetableFrameProducer();
    private static ServiceCalendarFrameProducer serviceCalendarFrameProducer = new ServiceCalendarFrameProducer();

    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    private static final Map<String, Codespace> codespaceMapping = new HashMap<>();

    private static final String DEFAULT_NAMESPACE = "http://www.netex.org.uk/netex";
    private static final String VERSION = "version";
    private static final String ID = "id";
    private static final String CREATED = "created";
    private static final String XMLNS = "Xmlns";
    private static final String XMLNSURL = "XmlnsUrl";

    private static NetexXmlStreamMarshaller marshaller;

    @Getter
    private Set<Codespace> codespaces = new HashSet<>();

    static {
        try {
            Properties properties = new Properties();
            properties.load(NetexPublicationDeliveryProducer.class.getResourceAsStream("/codespaces.properties"));
            Set<String> propertyKeys = properties.stringPropertyNames();

            for (String key : propertyKeys) {
                Codespace codespace = netexFactory.createCodespace()
                        .withId(key.toLowerCase())
                        .withXmlns(key)
                        .withXmlnsUrl(properties.getProperty(key));
                codespaceMapping.put(key, codespace);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load codespaces from file");
        }

        try {
            marshaller = NetexXmlStreamMarshaller.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create marshaller instance");
        }
    }

    public void produce(Context context) throws Exception {
        NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        Metadata metadata = (Metadata) context.get(METADATA);

        initializeCodespaces(configuration, exportableData);

        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        String fileName = exportableData.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        Path filePath = new File(outputPath.toFile(), fileName).toPath();
        writeToXml(context, filePath, exportableData);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(exportableData.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(exportableData.getLine())));
        }
    }

    private void initializeCodespaces(NetexprofileExportParameters configuration, ExportableData exportableData) {
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
            if (codespaceMapping.containsKey(line.objectIdPrefix().toUpperCase())) {
                operatorCodespace = codespaceMapping.get(line.objectIdPrefix().toUpperCase());
            } else {
                throw new RuntimeException("Unknown operator codespace");
            }
        }
        if (!codespaces.isEmpty()) {
            codespaces.clear();
        }
        codespaces.addAll(Arrays.asList(operatorCodespace, nsrCodespace));
    }

    private void writeToXml(Context context, Path path, ExportableData exportableData) throws IOException, XMLStreamException {
        //try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(path, CREATE, APPEND), 4096)) {
        try (Writer bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, CREATE, APPEND)) {
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
            XMLStreamWriter xmlStreamWriter = null;

            try {
                //writer = outputFactory.createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.name());
                xmlStreamWriter = outputFactory.createXMLStreamWriter(bufferedWriter);
                IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(xmlStreamWriter);

                writer.setDefaultNamespace(DEFAULT_NAMESPACE);
                //writer.setNamespaceContext(namespaces);

                writer.setNamespaceContext(new NamespaceContext() {
                    public Iterator getPrefixes(String namespaceURI) {
                        return null;
                    }

                    public String getPrefix(String namespaceURI) {
                        return "";
                    }

                    public String getNamespaceURI(String prefix) {
                        return null;
                    }
                });

                writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
                writePublicationDeliveryElement(context, writer, exportableData);
            } finally {
                if (xmlStreamWriter != null) {
                    try {
                        //writer.writeCharacters("\n");
                        xmlStreamWriter.writeEndDocument();
                        xmlStreamWriter.flush();
                        xmlStreamWriter.close();
                    } catch (XMLStreamException e) {
                        log.error("Could not close XML writer", e);
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            log.error("Could not produce XML file", e);
            throw new RuntimeException(e);
        }
    }

    private void writePublicationDeliveryElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        OffsetDateTime timestamp = OffsetDateTime.now();

        try {
            writer.writeStartElement(PUBLICATION_DELIVERY);

            writer.writeDefaultNamespace(DEFAULT_NAMESPACE);
            writer.writeNamespace("ns2", "http://www.opengis.net/gml/3.2");
            writer.writeNamespace("ns3", "http://www.siri.org.uk/siri");
            writer.writeAttribute(VERSION, NETEX_PROFILE_VERSION);

            writeElement(writer, PUBLICATION_TIMESTAMP, timestamp.toString());
            writeElement(writer, PARTICIPANT_REF, ""); // TODO fill with real data
            writeElement(writer, DESCRIPTION, exportableData.getLine().getName());
            writeDataObjectsElement(context, writer, exportableData, timestamp);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDataObjectsElement(Context context, XMLStreamWriter writer, ExportableData exportableData, OffsetDateTime timestamp) {
        try {
            writer.writeStartElement(DATA_OBJECTS);
            writeCompositeFrameElement(context, writer, exportableData, timestamp);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCompositeFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData, OffsetDateTime timestamp) {
        mobi.chouette.model.Line line = exportableData.getLine();

        try {
            writer.writeStartElement(COMPOSITE_FRAME);

            if (line.getNetwork().getVersionDate() != null) {
                OffsetDateTime createdDateTime = NetexProducerUtils.toOffsetDateTime(line.getNetwork().getVersionDate());
                writer.writeAttribute(CREATED, createdDateTime.toString());
            } else {
                writer.writeAttribute(CREATED, timestamp.toString());
            }

            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, netexId(line.objectIdPrefix(), COMPOSITE_FRAME, line.objectIdSuffix()));

            writeValidityConditionsElement(writer, line);
            writeCodespacesElement(writer);
            writeFrameDefaultsElement(writer);
            writeFramesElement(context, writer, exportableData);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeValidityConditionsElement(XMLStreamWriter writer, mobi.chouette.model.Line line) {
        try {
            writer.writeStartElement(VALIDITY_CONDITIONS);
            marshaller.marshal(netexFactory.createAvailabilityCondition(createAvailabilityCondition(line)), writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCodespacesElement(XMLStreamWriter writer) {
        try {
            writer.writeStartElement(CODESPACES);

            for (Codespace codespace : codespaces) {
                writeCodespaceElement(writer, codespace);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCodespaceElement(XMLStreamWriter writer, Codespace codespace) {
        try {
            writer.writeStartElement(CODESPACE);
            writer.writeAttribute(ID, codespace.getId());
            writeElement(writer, XMLNS, codespace.getXmlns());
            writeElement(writer, XMLNSURL, codespace.getXmlnsUrl());
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFrameDefaultsElement(XMLStreamWriter writer) {
        try {
            writer.writeStartElement(FRAME_DEFAULTS);
            writer.writeStartElement(DEFAULT_LOCALE);
            writeElement(writer, TIME_ZONE, DEFAULT_ZONE_ID);
            writeElement(writer, DEFAULT_LANGUAGE, DEFAULT_LANGUAGE_CODE);
            writer.writeEndElement();
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFramesElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        ResourceFrame resourceFrame = resourceFrameProducer.produce(context, exportableData);
        SiteFrame siteFrame = siteFrameProducer.produce(context, exportableData);
        ServiceFrame serviceFrame = serviceFrameProducer.produce(context, exportableData);
        ServiceCalendarFrame serviceCalendarFrame = serviceCalendarFrameProducer.produce(context, exportableData);

        try {
            writer.writeStartElement(FRAMES);

            marshaller.marshal(netexFactory.createResourceFrame(resourceFrame), writer);
            marshaller.marshal(netexFactory.createSiteFrame(siteFrame), writer);
            marshaller.marshal(netexFactory.createServiceFrame(serviceFrame), writer);
            marshaller.marshal(netexFactory.createServiceCalendarFrame(serviceCalendarFrame), writer);

            writeTimetableFrameElement(context, exportableData, writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTimetableFrameElement(Context context, ExportableData exportableData, XMLStreamWriter writer) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String timetableFrameId = netexId(line.objectIdPrefix(), TIMETABLE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(TIMETABLE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, timetableFrameId);
            writeVehicleJourneysElement(context, exportableData, writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeVehicleJourneysElement(Context context, ExportableData exportableData, XMLStreamWriter writer) {
        List<ServiceJourney> serviceJourneys = new ArrayList<>();

        for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
            serviceJourneys.add(serviceJourney);
        }

        try {
            writer.writeStartElement(VEHICLE_JOURNEYS);

            for (ServiceJourney serviceJourney : serviceJourneys) {
                marshaller.marshal(netexFactory.createServiceJourney(serviceJourney), writer);
                //writer.flush(); // necessary?
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AvailabilityCondition createAvailabilityCondition(mobi.chouette.model.Line line) {
        String availabilityConditionId = netexId(line.objectIdPrefix(), AVAILABILITY_CONDITION_KEY, line.objectIdSuffix());
        AvailabilityCondition availabilityCondition = netexFactory.createAvailabilityCondition();
        availabilityCondition.setVersion(line.getObjectVersion() > 0 ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        availabilityCondition.setId(availabilityConditionId);

        availabilityCondition.setFromDate(OffsetDateTime.now(ZoneId.systemDefault())); // TODO fix correct from date, for now using dummy dates
        availabilityCondition.setToDate(availabilityCondition.getFromDate().plusMonths(1L)); // TODO fix correct to date, for now using dummy dates
        return availabilityCondition;
    }

    private void writeElement(XMLStreamWriter writer, String element, String value) {
        try {
            writer.writeStartElement(element);
            writer.writeCharacters(value);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

}
