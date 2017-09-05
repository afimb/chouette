package mobi.chouette.exchange.netexprofile.exporter.writer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.DAY_TYPES;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.DAY_TYPE_ASSIGNMENTS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.OPERATING_PERIODS;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_CALENDAR_FRAME;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.OperatingPeriod;

import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

public class ServiceCalendarFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, String defaultCodespacePrefix, ExportableNetexData exportableNetexData, Marshaller marshaller) {

        String serviceCalendarFrameId = netexId(defaultCodespacePrefix, SERVICE_CALENDAR_FRAME, String.valueOf(NetexProducerUtils.generateSequentialId()));

        try {
            writer.writeStartElement(SERVICE_CALENDAR_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceCalendarFrameId);
            writeDayTypesElement(writer, exportableNetexData,marshaller);

            if (CollectionUtils.isNotEmpty(exportableNetexData.getOperatingPeriods())) {
                writeOperatingPeriodsElement(writer, exportableNetexData,marshaller);
            }

            writeDayTypeAssignmentsElement(writer, exportableNetexData,marshaller);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDayTypesElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
        try {
            writer.writeStartElement(DAY_TYPES);
            for (DayType dayType : exportableData.getDayTypes().values()) {
                marshaller.marshal(netexFactory.createDayType(dayType), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDayTypeAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
        try {
            writer.writeStartElement(DAY_TYPE_ASSIGNMENTS);
            for (DayTypeAssignment dayTypeAssignment : exportableData.getDayTypeAssignments()) {
                marshaller.marshal(netexFactory.createDayTypeAssignment(dayTypeAssignment), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeOperatingPeriodsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
        try {
            writer.writeStartElement(OPERATING_PERIODS);
            for (OperatingPeriod operatingPeriod : exportableData.getOperatingPeriods()) {
                marshaller.marshal(netexFactory.createOperatingPeriod(operatingPeriod), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
