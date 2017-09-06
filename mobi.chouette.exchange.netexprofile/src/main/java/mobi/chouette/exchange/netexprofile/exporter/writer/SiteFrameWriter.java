package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SITE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_PLACES;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.rutebanken.netex.model.StopPlace;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class SiteFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		String siteFrameId = NetexProducerUtils.createUniqueId(context, SITE_FRAME);

		try {
			writer.writeStartElement(SITE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, siteFrameId);
			writeStoPlacesElement(writer, exportableNetexData, marshaller);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeStoPlacesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
		try {
			writer.writeStartElement(STOP_PLACES);
			for (StopPlace stopPlace : exportableData.getSharedStopPlaces().values()) {
				marshaller.marshal(netexFactory.createStopPlace(stopPlace), writer);
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
