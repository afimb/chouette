package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.objectIdPrefix;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.JOURNEY_INTERCHANGES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.NOTICE_ASSIGNMENTS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.TIMETABLE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.VEHICLE_JOURNEYS;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class TimetableFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		// TODO temporary generating random id suffix, find a better way to create object id suffixes
		Network network = exportableNetexData.getSharedNetworks().values().iterator().next();
		String timetableFrameId = netexId(objectIdPrefix(network.getId()), TIMETABLE_FRAME, String.valueOf(NetexProducerUtils.generateSequentialId()));

		try {
			writer.writeStartElement(TIMETABLE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
			writer.writeAttribute(ID, timetableFrameId);
			writeVehicleJourneysElement(writer, exportableNetexData, marshaller);

			if (CollectionUtils.isNotEmpty(exportableNetexData.getNotices()) && CollectionUtils.isNotEmpty(exportableNetexData.getNoticeAssignments())) {
				writeNoticesElement(writer, exportableNetexData, marshaller);
				writeNoticeAssignmentsElement(writer, exportableNetexData, marshaller);
			}

			if (CollectionUtils.isNotEmpty(exportableNetexData.getServiceJourneyInterchanges())) {
				writeServiceJourneyInterchangesElement(writer, exportableNetexData, marshaller);
			}

			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeVehicleJourneysElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
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

	private static void writeNoticesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
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

	private static void writeNoticeAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
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

	private static void writeServiceJourneyInterchangesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(JOURNEY_INTERCHANGES);
			for (ServiceJourneyInterchange serviceJourneyInterchange : exportableData.getServiceJourneyInterchanges()) {
				marshaller.marshal(netexFactory.createServiceJourneyInterchange(serviceJourneyInterchange), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
