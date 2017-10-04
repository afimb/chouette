package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.DESTINATION_DISPLAYS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.JOURNEY_PATTERNS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.LINES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ROUTES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ROUTE_POINTS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SCHEDULED_STOP_POINTS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_ASSIGNMENTS;

import java.util.Collection;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.rutebanken.netex.model.DestinationDisplay;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ServiceJourneyPattern;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.NetexFragmentMode;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class ServiceFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, Context context, Network network, Marshaller marshaller) {
		String serviceFrameId = NetexProducerUtils.createUniqueId(context, SERVICE_FRAME);

		try {
			writer.writeStartElement(SERVICE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, serviceFrameId);
			writeNetworkElement(writer, network, marshaller);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData, NetexFragmentMode fragmentMode,
			Marshaller marshaller) {

		String serviceFrameId = NetexProducerUtils.createUniqueId(context, SERVICE_FRAME);

		try {
			writer.writeStartElement(SERVICE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, serviceFrameId);

			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				writeRoutePointsElement(writer, exportableNetexData, marshaller);
				writeRoutesElement(writer, exportableNetexData, marshaller);
				writeLinesElement(writer, exportableNetexData, marshaller);
				writeScheduledStopPointsElement(writer, exportableNetexData, marshaller);
				writeStopAssignmentsElement(writer, exportableNetexData, marshaller);
				writeJourneyPatternsElement(writer, exportableNetexData, marshaller);
				ReusedConstructsWriter.writeNoticeAssignmentsElement(writer, exportableNetexData.getNoticeAssignmentsServiceFrame(), marshaller);
			} else { // shared data
				writeDestinationDisplaysElement(writer, exportableNetexData, marshaller);
				writeNoticesElement(writer, exportableNetexData.getSharedNotices().values(), marshaller);
			}

			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeNoticesElement(XMLStreamWriter writer, Collection<Notice> notices, Marshaller marshaller) {
		try {
			if (!notices.isEmpty()) {
				writer.writeStartElement(NOTICES);
				for (Notice notice : notices) {
					marshaller.marshal(netexFactory.createNotice(notice), writer);
				}
				writer.writeEndElement();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeNetworkElement(XMLStreamWriter writer, Network network, Marshaller marshaller) {
		try {
			marshaller.marshal(netexFactory.createNetwork(network), writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeDestinationDisplaysElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			if (exportableData.getSharedDestinationDisplays().values().size() > 0) {
				writer.writeStartElement(DESTINATION_DISPLAYS);
				for (DestinationDisplay destinationDisplay : exportableData.getSharedDestinationDisplays().values()) {
					marshaller.marshal(netexFactory.createDestinationDisplay(destinationDisplay), writer);
				}
				writer.writeEndElement();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeRoutePointsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(ROUTE_POINTS);
			for (RoutePoint routePoint : exportableData.getRoutePoints().values()) {
				marshaller.marshal(netexFactory.createRoutePoint(routePoint), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeRoutesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
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

	private static void writeLinesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(LINES);
			marshaller.marshal(netexFactory.createLine(exportableData.getLine()), writer);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeScheduledStopPointsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(SCHEDULED_STOP_POINTS);
			for (ScheduledStopPoint scheduledStopPoint : exportableData.getScheduledStopPoints().values()) {
				marshaller.marshal(netexFactory.createScheduledStopPoint(scheduledStopPoint), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeStopAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(STOP_ASSIGNMENTS);
			for (PassengerStopAssignment stopAssignment : exportableData.getStopAssignments().values()) {
				marshaller.marshal(netexFactory.createPassengerStopAssignment(stopAssignment), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeJourneyPatternsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(JOURNEY_PATTERNS);
			for (ServiceJourneyPattern journeyPattern : exportableData.getJourneyPatterns().values()) {
				marshaller.marshal(netexFactory.createServiceJourneyPattern(journeyPattern), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
