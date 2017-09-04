package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.NetexFragmentMode;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.objectIdPrefix;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, Network network, Marshaller marshaller) {
		// TODO temporary generating random id suffix, find a better way to create object id suffixes
		String serviceFrameId = netexId(objectIdPrefix(network.getId()), SERVICE_FRAME, String.valueOf(NetexProducerUtils.generateSequentialId()));

		try {
			writer.writeStartElement(SERVICE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
			writer.writeAttribute(ID, serviceFrameId);
			writeNetworkElement(writer, network,marshaller);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData, NetexFragmentMode fragmentMode, Marshaller marshaller) {

		// TODO temporary generating random id suffix, find a better way to create object id suffixes
		Network network = exportableNetexData.getSharedNetworks().values().iterator().next();
		String serviceFrameId = netexId(objectIdPrefix(network.getId()), SERVICE_FRAME, String.valueOf(NetexProducerUtils.generateSequentialId()));

		try {
			writer.writeStartElement(SERVICE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
			writer.writeAttribute(ID, serviceFrameId);

			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				writeRoutesElement(writer, exportableNetexData,marshaller);
				writeLinesElement(writer, exportableNetexData,marshaller);
				writeJourneyPatternsElement(writer, exportableNetexData,marshaller);
			} else { // shared data
				writeRoutePointsElement(writer, exportableNetexData,marshaller);
				writeDestinationDisplaysElement(writer, exportableNetexData,marshaller);
				writeScheduledStopPointsElement(writer, exportableNetexData,marshaller);
				writeStopAssignmentsElement(writer, exportableNetexData,marshaller);
			}

			writer.writeEndElement();
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
			for (RoutePoint routePoint : exportableData.getSharedRoutePoints().values()) {
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
			for (ScheduledStopPoint scheduledStopPoint : exportableData.getSharedStopPoints().values()) {
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
			for (PassengerStopAssignment stopAssignment : exportableData.getSharedStopAssignments().values()) {
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
			for (JourneyPattern journeyPattern : exportableData.getJourneyPatterns()) {
				marshaller.marshal(netexFactory.createJourneyPattern(journeyPattern), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
