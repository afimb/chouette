package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.Operator;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.*;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.ORGANISATIONS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.RESOURCE_FRAME;

public class ResourceFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData) {
        Network network = exportableNetexData.getSharedNetwork();
        String resourceFrameId = netexId(objectIdPrefix(network.getId()), RESOURCE_FRAME, objectIdSuffix(network.getId()));

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
            for (Operator operator : exportableNetexData.getSharedOperators().values()) {
                marshaller.marshal(netexFactory.createOperator(operator), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
