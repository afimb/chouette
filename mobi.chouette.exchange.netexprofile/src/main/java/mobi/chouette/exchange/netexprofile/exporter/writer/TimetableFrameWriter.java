package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.ServiceJourney;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.objectIdPrefix;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class TimetableFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData) {

        // TODO temporary generating random id suffix, find a better way to create object id suffixes
        Network network = exportableNetexData.getSharedNetworks().values().iterator().next();
        String timetableFrameId = netexId(objectIdPrefix(network.getId()), TIMETABLE_FRAME, String.valueOf(NetexProducerUtils.generateRandomId()));

        try {
            writer.writeStartElement(TIMETABLE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, timetableFrameId);
            writeVehicleJourneysElement(writer, exportableNetexData);

            if (CollectionUtils.isNotEmpty(exportableNetexData.getNotices()) && CollectionUtils.isNotEmpty(exportableNetexData.getNoticeAssignments())) {
                writeNoticesElement(writer, exportableNetexData);
                writeNoticeAssignmentsElement(writer, exportableNetexData);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeVehicleJourneysElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(VEHICLE_JOURNEYS);
            for (ServiceJourney serviceJourney : exportableData.getServiceJourneys()) {
                marshaller.marshal(netexFactory.createServiceJourney(serviceJourney), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNoticesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(NOTICES);
            for (Notice notice : exportableData.getNotices()) {
                marshaller.marshal(netexFactory.createNotice(notice), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNoticeAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(NOTICE_ASSIGNMENTS);
            for (NoticeAssignment noticeAssignment : exportableData.getNoticeAssignments()) {
                marshaller.marshal(netexFactory.createNoticeAssignment(noticeAssignment), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
