package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.time.LocalDateTime;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.NetexFragmentMode;
import mobi.chouette.exchange.netexprofile.exporter.NetexprofileExportParameters;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import mobi.chouette.model.util.NamingUtil;

import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.Codespace;
import org.rutebanken.netex.model.Network;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class PublicationDeliveryWriter extends AbstractNetexWriter {

	public static void write(Context context, XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData,
			NetexFragmentMode fragmentMode, Marshaller marshaller) {
		LocalDateTime timestamp = LocalDateTime.now();
		String timestampFormatted = formatter.format(timestamp);

		try {
			writer.writeStartElement(PUBLICATION_DELIVERY);
			writer.writeDefaultNamespace(Constant.NETEX_NAMESPACE);
			writer.writeNamespace("gis", Constant.OPENGIS_NAMESPACE);
			writer.writeNamespace("siri", Constant.SIRI_NAMESPACE);
			writer.writeAttribute(VERSION, NETEX_PROFILE_VERSION);

			writeElement(writer, PUBLICATION_TIMESTAMP, timestampFormatted);
			writeElement(writer, PARTICIPANT_REF, PARTICIPANT_REF_CONTENT);

			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				writeElement(writer, DESCRIPTION,  NamingUtil.getName(exportableData.getLine());
			} else {
				writeElement(writer, DESCRIPTION, "Shared data used across line files");
			}

			writeDataObjectsElement(context, writer, exportableData, exportableNetexData, timestampFormatted, fragmentMode, marshaller);
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeDataObjectsElement(Context context, XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData,
			String timestamp, NetexFragmentMode fragmentMode, Marshaller marshaller) {
		try {
			writer.writeStartElement(DATA_OBJECTS);
			writeCompositeFrameElement(context, writer, exportableData, exportableNetexData, timestamp, fragmentMode, marshaller);
			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeCompositeFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData,
			ExportableNetexData exportableNetexData, String timestamp, NetexFragmentMode fragmentMode, Marshaller marshaller) {
		mobi.chouette.model.Line line = exportableData.getLine();

		String compositeFrameId = NetexProducerUtils.createUniqueId(context, COMPOSITE_FRAME);

		try {
			writer.writeStartElement(COMPOSITE_FRAME);

			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				if (line.getNetwork().getVersionDate() != null) {
					LocalDateTime createdDateTime = TimeUtil.toLocalDateFromJoda(line.getNetwork().getVersionDate()).atStartOfDay();
					writer.writeAttribute(CREATED, formatter.format(createdDateTime));
				} else {
					writer.writeAttribute(CREATED, timestamp);
				}
			} else {
				writer.writeAttribute(CREATED, timestamp);
			}

			writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
			writer.writeAttribute(ID, compositeFrameId);

			writeValidityConditionsElement(writer, exportableNetexData, fragmentMode, marshaller);
			writeCodespacesElement(writer, exportableData, exportableNetexData, fragmentMode, marshaller);
			writeFrameDefaultsElement(writer);
			writeFramesElement(context, writer, exportableNetexData, fragmentMode, marshaller);

			writer.writeEndElement();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeValidityConditionsElement(XMLStreamWriter writer, ExportableNetexData exportableData, NetexFragmentMode fragmentMode,
			Marshaller marshaller) {
		try {
			writer.writeStartElement(VALIDITY_CONDITIONS);

			AvailabilityCondition availabilityCondition;
			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				availabilityCondition = exportableData.getLineCondition();
			} else { // shared data
				availabilityCondition = exportableData.getCommonCondition();
			}

			marshaller.marshal(netexFactory.createAvailabilityCondition(availabilityCondition), writer);
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeCodespacesElement(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData,
			NetexFragmentMode fragmentMode, Marshaller marshaller) {
		try {
			writer.writeStartElement(CODESPACES);

			for(Codespace cs : exportableNetexData.getSharedCodespaces().values()) {
				writeCodespaceElement(writer, cs);
			}

			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeCodespaceElement(XMLStreamWriter writer, Codespace codespace) {
		try {
			writer.writeStartElement(CODESPACE);
			writer.writeAttribute(ID, codespace.getId());
			writeElement(writer, XMLNS, codespace.getXmlns());
			writeElement(writer, XMLNSURL, codespace.getXmlnsUrl());
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeFrameDefaultsElement(XMLStreamWriter writer) {
		try {
			writer.writeStartElement(FRAME_DEFAULTS);
			writer.writeStartElement(DEFAULT_LOCALE);
			writeElement(writer, TIME_ZONE, DEFAULT_ZONE_ID);
			writeElement(writer, DEFAULT_LANGUAGE, DEFAULT_LANGUAGE_CODE);
			writer.writeEndElement();
			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeFramesElement(Context context, XMLStreamWriter writer, ExportableNetexData exportableNetexData, NetexFragmentMode fragmentMode,
			Marshaller marshaller) {
		NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);

		try {
			writer.writeStartElement(FRAMES);

			if (fragmentMode.equals(NetexFragmentMode.LINE)) {
				ServiceFrameWriter.write(writer, context, exportableNetexData, NetexFragmentMode.LINE, marshaller);
				TimetableFrameWriter.write(writer, context, exportableNetexData, marshaller);
			} else { // shared data
				ResourceFrameWriter.write(writer, context, exportableNetexData, marshaller);

				if (configuration.isExportStops()) {
					SiteFrameWriter.write(writer, context, exportableNetexData, marshaller);
				}

				for (Network network : exportableNetexData.getSharedNetworks().values()) {
					ServiceFrameWriter.write(writer, context, network, marshaller);
				}

				ServiceFrameWriter.write(writer, context, exportableNetexData, NetexFragmentMode.SHARED, marshaller);
				ServiceCalendarFrameWriter.write(writer, context, exportableNetexData, marshaller);
			}

			writer.writeEndElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
