package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.JourneyPattern;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData, Mode mode) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String serviceFrameId = netexId(line.objectIdPrefix(), SERVICE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(SERVICE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceFrameId);

            if (mode.equals(Mode.line)) {
                writeRoutePointsElement(writer, exportableNetexData);
                writeRoutesElement(writer, exportableNetexData);
                writeLinesElement(writer, exportableNetexData);
                writeScheduledStopPointsElement(writer, exportableNetexData);
                writeStopAssignmentsElement(writer, exportableNetexData);
                writeJourneyPatternsElement(writer, exportableNetexData);
            } else { // shared data
                writeNetworkElement(writer, exportableNetexData);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNetworkElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            marshaller.marshal(netexFactory.createNetwork(exportableData.getSharedNetwork()), writer);
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
            for (ScheduledStopPoint scheduledStopPoint : exportableData.getStopPoints()) {
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
            for (PassengerStopAssignment stopAssignment : exportableData.getStopAssignments()) {
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
