package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.ServiceJourney;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.TIMETABLE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.VEHICLE_JOURNEYS;

public class TimetableFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String timetableFrameId = netexId(line.objectIdPrefix(), TIMETABLE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(TIMETABLE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, timetableFrameId);
            writeVehicleJourneysElement(writer, exportableNetexData);
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

}
