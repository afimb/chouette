package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.StopPlace;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.*;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SITE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_PLACES;

public class SiteFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableNetexData exportableNetexData) {
        Network network = exportableNetexData.getSharedNetwork();
        String siteFrameId = netexId(objectIdPrefix(network.getId()), SITE_FRAME, objectIdSuffix(network.getId()));

        try {
            writer.writeStartElement(SITE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, siteFrameId);
            writeStoPlacesElement(writer, exportableNetexData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeStoPlacesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
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
