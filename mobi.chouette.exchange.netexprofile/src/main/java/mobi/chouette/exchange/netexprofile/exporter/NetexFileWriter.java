package mobi.chouette.exchange.netexprofile.exporter;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.writer.AbstractNetexWriter.Mode;
import mobi.chouette.exchange.netexprofile.exporter.writer.PublicationDeliveryWriter;
import mobi.chouette.exchange.netexprofile.jaxb.EscapingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Log4j
class NetexFileWriter implements Constant {

    private static final String DEFAULT_NAMESPACE = "http://www.netex.org.uk/netex";

    void writeXmlFile(Path filePath, ExportableData exportableData, ExportableNetexData exportableNetexData, Mode mode) {
        try (Writer bufferedWriter = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, CREATE, APPEND)) {
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            //outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
            XMLStreamWriter xmlStreamWriter = null;

            try {
                xmlStreamWriter = outputFactory.createXMLStreamWriter(bufferedWriter);
                xmlStreamWriter.setDefaultNamespace(DEFAULT_NAMESPACE);
                //xmlStreamWriter.setNamespaceContext(namespaces);

                IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(new EscapingXMLStreamWriter(xmlStreamWriter));
                writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
                PublicationDeliveryWriter.write(writer, exportableData, exportableNetexData, mode);
            } finally {
                if (xmlStreamWriter != null) {
                    try {
                        //xmlStreamWriter.writeCharacters("\n");
                        xmlStreamWriter.writeEndDocument();
                        xmlStreamWriter.flush();
                        xmlStreamWriter.close();
                    } catch (XMLStreamException e) {
                        log.error("Could not close XML writer", e);
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            log.error("Could not produce XML file", e);
            throw new RuntimeException(e);
        }
    }

}
