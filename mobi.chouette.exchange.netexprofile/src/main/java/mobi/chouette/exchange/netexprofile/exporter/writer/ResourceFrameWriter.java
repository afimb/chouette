package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ORGANISATIONS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.RESOURCE_FRAME;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.Organisation_VersionStructure;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class ResourceFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, String defaultCodespacePrefix, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		String resourceFrameId = netexId(defaultCodespacePrefix, RESOURCE_FRAME, String.valueOf(NetexProducerUtils.generateSequentialId()));

		try {
			writer.writeStartElement(RESOURCE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, resourceFrameId);
			writeOrganisationsElement(writer, exportableNetexData, marshaller);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeOrganisationsElement(XMLStreamWriter writer, ExportableNetexData exportableNetexData, Marshaller marshaller) {
		try {
			writer.writeStartElement(ORGANISATIONS);
			for (Organisation_VersionStructure operator : exportableNetexData.getSharedOrganisations().values()) {
				if (operator instanceof Operator) {
					marshaller.marshal(netexFactory.createOperator((Operator) operator), writer);
				} else if (operator instanceof Authority) {
					marshaller.marshal(netexFactory.createAuthority((Authority) operator), writer);
				}
			}
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
