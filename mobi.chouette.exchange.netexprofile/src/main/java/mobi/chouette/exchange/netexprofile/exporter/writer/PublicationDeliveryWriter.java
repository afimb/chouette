package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import org.rutebanken.netex.model.Codespace;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.OffsetDateTime;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class PublicationDeliveryWriter extends AbstractNetexWriter {

    private static final String OPENGIS_NAMESPACE = "http://www.opengis.net/gml/3.2";
    private static final String SIRI_NAMESPACE = "http://www.siri.org.uk/siri";

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData, Mode mode) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String timestampFormatted = formatter.format(timestamp);

        try {
            writer.writeStartElement(PUBLICATION_DELIVERY);
            writer.writeDefaultNamespace(DEFAULT_NAMESPACE);
            writer.writeNamespace("ns2", OPENGIS_NAMESPACE);
            writer.writeNamespace("ns3", SIRI_NAMESPACE);
            writer.writeAttribute(VERSION, NETEX_PROFILE_VERSION);

            writeElement(writer, PUBLICATION_TIMESTAMP, timestampFormatted);
            writeElement(writer, PARTICIPANT_REF, NSR_XMLNS);
            writeElement(writer, DESCRIPTION, exportableData.getLine().getName());
            writeDataObjectsElement(writer, exportableData, exportableNetexData, timestampFormatted, mode);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDataObjectsElement(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData, String timestamp, Mode mode) {
        try {
            writer.writeStartElement(DATA_OBJECTS);
            writeCompositeFrameElement(writer, exportableData, exportableNetexData, timestamp, mode);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeCompositeFrameElement(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData, String timestamp, Mode mode) {
        mobi.chouette.model.Line line = exportableData.getLine();

        try {
            writer.writeStartElement(COMPOSITE_FRAME);

            if (line.getNetwork().getVersionDate() != null) {
                OffsetDateTime createdDateTime = NetexProducerUtils.toOffsetDateTime(line.getNetwork().getVersionDate());
                writer.writeAttribute(CREATED, formatter.format(createdDateTime));
            } else {
                writer.writeAttribute(CREATED, timestamp);
            }

            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, netexId(line.objectIdPrefix(), COMPOSITE_FRAME, line.objectIdSuffix()));

            writeValidityConditionsElement(writer, exportableNetexData);
            writeCodespacesElement(writer, exportableNetexData);
            writeFrameDefaultsElement(writer);
            writeFramesElement(writer, exportableData, exportableNetexData, mode);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeValidityConditionsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(VALIDITY_CONDITIONS);
            marshaller.marshal(netexFactory.createAvailabilityCondition(exportableData.getAvailabilityCondition()), writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeCodespacesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(CODESPACES);
            for (Codespace codespace : exportableData.getCodespaces()) {
                writeCodespaceElement(writer, codespace);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeCodespaceElement(XMLStreamWriter writer, Codespace codespace) {
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

    private static void writeFrameDefaultsElement(XMLStreamWriter writer) {
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

    private static void writeFramesElement(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData, Mode mode) {
        try {
            writer.writeStartElement(FRAMES);

            if (mode.equals(Mode.line)) {
                ResourceFrameWriter.write(writer, exportableData, exportableNetexData);
                ServiceFrameWriter.write(writer, exportableData, exportableNetexData);
                ServiceCalendarFrameWriter.write(writer, exportableData, exportableNetexData);
                TimetableFrameWriter.write(writer, exportableData, exportableNetexData);
            } else { // shared data
                SiteFrameWriter.write(writer, exportableData, exportableNetexData);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
