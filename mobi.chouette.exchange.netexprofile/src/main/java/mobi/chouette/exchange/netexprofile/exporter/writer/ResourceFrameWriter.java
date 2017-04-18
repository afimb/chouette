package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.Operator;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ORGANISATIONS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.RESOURCE_FRAME;

public class ResourceFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String resourceFrameId = netexId(line.objectIdPrefix(), RESOURCE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(RESOURCE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, resourceFrameId);
            writeOrganisationsElement(writer, exportableNetexData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeOrganisationsElement(XMLStreamWriter writer, ExportableNetexData exportableNetexData) {
        try {
            writer.writeStartElement(ORGANISATIONS);
            for (Operator operator : exportableNetexData.getOperators()) {
                marshaller.marshal(netexFactory.createOperator(operator), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
