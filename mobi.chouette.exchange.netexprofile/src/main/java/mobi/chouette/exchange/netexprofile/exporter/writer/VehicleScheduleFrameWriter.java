package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;
import org.apache.commons.collections.CollectionUtils;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.BLOCKS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.VEHICLE_SCHEDULE_FRAME;

public class VehicleScheduleFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData,
                             Marshaller marshaller) {

        if (CollectionUtils.isNotEmpty(exportableNetexData.getBlocks())) {
            String vehicleScheduleFrameId = NetexProducerUtils.createUniqueId(context, VEHICLE_SCHEDULE_FRAME);
            try {
                writer.writeStartElement(VEHICLE_SCHEDULE_FRAME);
                writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
                writer.writeAttribute(ID, vehicleScheduleFrameId);
                writeBlocksElement(writer, exportableNetexData, marshaller);
                writer.writeEndElement();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    private static void writeBlocksElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
        try {
            writer.writeStartElement(BLOCKS);
            for (org.rutebanken.netex.model.Block block : exportableData.getBlocks()) {
                marshaller.marshal(netexFactory.createBlock(block), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
