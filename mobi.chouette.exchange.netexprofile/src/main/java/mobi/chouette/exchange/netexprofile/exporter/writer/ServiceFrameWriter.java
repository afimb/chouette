package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.NetexFragmentMode;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import org.rutebanken.netex.model.*;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.objectIdPrefix;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, Network network) {
        // TODO temporary generating random id suffix, find a better way to create object id suffixes
        String serviceFrameId = netexId(objectIdPrefix(network.getId()), SERVICE_FRAME, String.valueOf(NetexProducerUtils.generateRandomId()));

        try {
            writer.writeStartElement(SERVICE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceFrameId);
            writeNetworkElement(writer, network);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData, NetexFragmentMode fragmentMode) {

        // TODO temporary generating random id suffix, find a better way to create object id suffixes
        Network network = exportableNetexData.getSharedNetworks().values().iterator().next();
        String serviceFrameId = netexId(objectIdPrefix(network.getId()), SERVICE_FRAME, String.valueOf(NetexProducerUtils.generateRandomId()));

        try {
            writer.writeStartElement(SERVICE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceFrameId);

            if (fragmentMode.equals(NetexFragmentMode.LINE)) {
                writeRoutePointsElement(writer, exportableNetexData);
                writeRoutesElement(writer, exportableNetexData);
                writeLinesElement(writer, exportableNetexData);
                writeJourneyPatternsElement(writer, exportableNetexData);
            } else { // shared data
                writeScheduledStopPointsElement(writer, exportableNetexData);
                writeStopAssignmentsElement(writer, exportableNetexData);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNetworkElement(XMLStreamWriter writer, Network network) {
        try {
            marshaller.marshal(netexFactory.createNetwork(network), writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeRoutePointsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(ROUTE_POINTS);
            for (RoutePoint routePoint : exportableData.getRoutePoints()) {
                marshaller.marshal(netexFactory.createRoutePoint(routePoint), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeRoutesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(ROUTES);
            for (org.rutebanken.netex.model.Route route : exportableData.getRoutes()) {
                marshaller.marshal(netexFactory.createRoute(route), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeLinesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(LINES);
            marshaller.marshal(netexFactory.createLine(exportableData.getLine()), writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeScheduledStopPointsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(SCHEDULED_STOP_POINTS);
            for (ScheduledStopPoint scheduledStopPoint : exportableData.getSharedStopPoints().values()) {
                marshaller.marshal(netexFactory.createScheduledStopPoint(scheduledStopPoint), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeStopAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(STOP_ASSIGNMENTS);
            for (PassengerStopAssignment stopAssignment : exportableData.getSharedStopAssignments().values()) {
                marshaller.marshal(netexFactory.createPassengerStopAssignment(stopAssignment), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeJourneyPatternsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(JOURNEY_PATTERNS);
            for (JourneyPattern journeyPattern : exportableData.getJourneyPatterns()) {
                marshaller.marshal(netexFactory.createJourneyPattern(journeyPattern), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
