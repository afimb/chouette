package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.Branding;
import org.rutebanken.netex.model.GeneralOrganisation;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.Organisation_VersionStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class ResourceFrameWriter extends AbstractNetexWriter {

	public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		String resourceFrameId = NetexProducerUtils.createUniqueId(context, RESOURCE_FRAME);

		try {
			writer.writeStartElement(RESOURCE_FRAME);
			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, resourceFrameId);
			writeTypesOfValueElement(writer, exportableNetexData, marshaller);
			writeOrganisationsElement(writer, exportableNetexData, marshaller);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeOrganisationsElement(XMLStreamWriter writer, ExportableNetexData exportableNetexData, Marshaller marshaller) {
		try {
			if (!exportableNetexData.getSharedOrganisations().isEmpty()) {
				writer.writeStartElement(ORGANISATIONS);
				for (Organisation_VersionStructure organisation : exportableNetexData.getSharedOrganisations().values()) {
					if (organisation instanceof Operator) {
						marshaller.marshal(netexFactory.createOperator((Operator) organisation), writer);
					} else if (organisation instanceof Authority) {
						marshaller.marshal(netexFactory.createAuthority((Authority) organisation), writer);
					} else {
						marshaller.marshal(netexFactory.createGeneralOrganisation((GeneralOrganisation) organisation), writer);
					}
				}
				writer.writeEndElement();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeTypesOfValueElement(XMLStreamWriter writer, ExportableNetexData exportableNetexData, Marshaller marshaller) {
		try {
			if (!exportableNetexData.getSharedBrandings().isEmpty()) {
				writer.writeStartElement(TYPES_OF_VALUE);
				for (Branding branding : exportableNetexData.getSharedBrandings().values()) {
					marshaller.marshal(netexFactory.createBranding(branding), writer);
				}
				writer.writeEndElement();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
