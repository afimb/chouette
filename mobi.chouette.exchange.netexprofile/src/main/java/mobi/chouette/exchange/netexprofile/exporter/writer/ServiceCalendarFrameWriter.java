package mobi.chouette.exchange.netexprofile.exporter.writer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.OperatingPeriod;

import javax.xml.stream.XMLStreamWriter;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceCalendarFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, ExportableData exportableData, ExportableNetexData exportableNetexData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(SERVICE_CALENDAR_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceCalendarFrameId);
            writeDayTypesElement(writer, exportableNetexData);

            if (CollectionUtils.isNotEmpty(exportableNetexData.getOperatingPeriods())) {
                writeOperatingPeriodsElement(writer, exportableNetexData);
            }

            writeDayTypeAssignmentsElement(writer, exportableNetexData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDayTypesElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
        try {
            writer.writeStartElement(DAY_TYPES);
            for (DayType dayType : exportableData.getDayTypes()) {
                marshaller.marshal(netexFactory.createDayType(dayType), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDayTypeAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
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

    private static void writeOperatingPeriodsElement(XMLStreamWriter writer, ExportableNetexData exportableData) {
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
