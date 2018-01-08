package mobi.chouette.exchange.netexprofile.exporter.writer;

import java.math.BigInteger;
import java.util.stream.Collectors;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils;

import org.apache.commons.collections.MapUtils;
import org.rutebanken.netex.model.DayType;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.OperatingPeriod;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.NETEX_DEFAULT_OBJECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceCalendarFrameWriter extends AbstractNetexWriter {

    public static void write(XMLStreamWriter writer, Context context, ExportableNetexData exportableNetexData, Marshaller marshaller) {

		String serviceCalendarFrameId = NetexProducerUtils.createUniqueId(context, SERVICE_CALENDAR_FRAME);

        try {
            writer.writeStartElement(SERVICE_CALENDAR_FRAME);
            writer.writeAttribute(VERSION, NETEX_DEFAULT_OBJECT_VERSION);
            writer.writeAttribute(ID, serviceCalendarFrameId);
            writeDayTypesElement(writer, exportableNetexData,marshaller);

            if (MapUtils.isNotEmpty(exportableNetexData.getSharedOperatingPeriods())) {
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
            for (DayType dayType : exportableData.getSharedDayTypes().values()) {
                marshaller.marshal(netexFactory.createDayType(dayType), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeDayTypeAssignmentsElement(XMLStreamWriter writer, ExportableNetexData exportableData, Marshaller marshaller) {
        try {
            int order=1;
            writer.writeStartElement(DAY_TYPE_ASSIGNMENTS);
            for (DayTypeAssignment dayTypeAssignment : exportableData.getSharedDayTypeAssignments().stream().sorted(new DayTypeAssignmentExportComparator()).collect(Collectors.toList())) {
                dayTypeAssignment.setOrder(BigInteger.valueOf(order++));
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
            for (OperatingPeriod operatingPeriod : exportableData.getSharedOperatingPeriods().values()) {
                marshaller.marshal(netexFactory.createOperatingPeriod(operatingPeriod), writer);
            }
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
