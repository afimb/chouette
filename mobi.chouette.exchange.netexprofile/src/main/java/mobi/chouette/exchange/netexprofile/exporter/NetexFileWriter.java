package mobi.chouette.exchange.netexprofile.exporter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.writer.PublicationDeliveryWriter;
import mobi.chouette.exchange.netexprofile.jaxb.NetexXMLProcessingHelperFactory;

@Log4j
class NetexFileWriter implements Constant {

	void writeXmlFile(Context context, Path filePath, ExportableData exportableData, ExportableNetexData exportableNetexData, NetexFragmentMode fragmentMode,
			Marshaller marshaller) throws XMLStreamException {

		IndentingXMLStreamWriter writer = null;

		try {
			writer = NetexXMLProcessingHelperFactory.createXMLWriter(filePath);

			writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
			PublicationDeliveryWriter.write(context, writer, exportableData, exportableNetexData, fragmentMode, marshaller);

		} catch (XMLStreamException | IOException e) {
			log.error("Could not produce XML file", e);
			throw new RuntimeException(e);

		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (XMLStreamException e) {
					log.error("Error flushing and closing Netex Export XML file "+filePath.toString(),e);
					throw e;
				}
			}

		}
	}

}
