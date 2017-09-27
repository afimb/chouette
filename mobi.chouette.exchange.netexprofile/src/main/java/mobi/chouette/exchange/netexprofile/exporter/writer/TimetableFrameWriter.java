package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.JOURNEY_INTERCHANGES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.TIMETABLE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.VEHICLE_JOURNEYS;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.ServiceJourney;
import org.rutebanken.netex.model.ServiceJourneyInterchange;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class TimetableFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		String timetableFrameId = NetexProducerUtils.createUniqueId(context, TIMETABLE_FRAME);

		try {
			writer.writeStartElement(TIMETABLE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, timetableFrameId);
			writeVehicleJourneysElement(writer, exportableNetexData, marshaller);
			ReusedConstructsWriter.writeNoticeAssignmentsElement(writer, exportableNetexData.getNoticeAssignmentsTimetableFrame(), marshaller);

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
