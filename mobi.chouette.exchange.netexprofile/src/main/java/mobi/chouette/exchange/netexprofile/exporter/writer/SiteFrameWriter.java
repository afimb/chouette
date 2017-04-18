package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.rutebanken.netex.model.StopPlace;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SITE_FRAME;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_PLACES;

public class SiteFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String siteFrameId = netexId(line.objectIdPrefix(), SITE_FRAME, line.objectIdSuffix());

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
            for (StopPlace stopPlace : exportableData.getStopPlaces()) {
                marshaller.marshal(netexFactory.createStopPlace(stopPlace), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
