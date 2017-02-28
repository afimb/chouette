package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceCalendarFrameProducer extends NetexProducer implements NetexFrameProducer<ServiceCalendarFrame> {

    @Override
    public ServiceCalendarFrame produce(ExportableData data) {
        Line line = data.getLine();

        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, line.objectIdSuffix());

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion("any")
                .withId(serviceCalendarFrameId);

        DayTypesInFrame_RelStructure dayTypesStruct = netexFactory.createDayTypesInFrame_RelStructure();
        DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentsStruct = netexFactory.createDayTypeAssignmentsInFrame_RelStructure();

        for (Timetable timetable : data.getTimetables()) {
            timetable.computeLimitOfPeriods();

            DayType dayType = netexFactory.createDayType();
            String version = timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
            dayType.setVersion(version);

            String dayTypeId = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, timetable.objectIdSuffix());
            dayType.setId(dayTypeId);

            if (CollectionUtils.isNotEmpty(timetable.getDayTypes())) {
                List<DayTypeEnum> dayTypeEnums = timetable.getDayTypes();
                DayOfWeekEnumeration dayOfWeekEnumeration = NetexProducerUtils.toDayOfWeekEnumeration(dayTypeEnums);

                PropertyOfDay propertyOfDay = netexFactory.createPropertyOfDay();
                propertyOfDay.getDaysOfWeek().add(dayOfWeekEnumeration);

                PropertiesOfDay_RelStructure propertiesOfDay = netexFactory.createPropertiesOfDay_RelStructure();
                propertiesOfDay.getPropertyOfDay().add(propertyOfDay);

                dayType.setProperties(propertiesOfDay);
            }

            dayTypesStruct.getDayType_().add(netexFactory.createDayType(dayType));

            // TODO rewrite when handling grouped dates, for now only supporting 1 calendar day per timetable

            if (CollectionUtils.isNotEmpty(timetable.getEffectiveDates())) {
                String dayTypeAssignmentId = netexId(timetable.objectIdPrefix(), DAY_TYPE_ASSIGNMENT_KEY, timetable.objectIdSuffix());

                DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure()
                        .withVersion(version)
                        .withRef(dayTypeId);

                DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
                        .withVersion(version)
                        .withId(dayTypeAssignmentId)
                        .withDate(NetexProducerUtils.toOffsetDateTime(timetable.getEffectiveDates().get(0)))
                        .withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRefStruct));

                dayTypeAssignmentsStruct.getDayTypeAssignment().add(dayTypeAssignment);
            }
        }

        List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentsStruct.getDayTypeAssignment();
        for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
            dayTypeAssignment.setOrder(BigInteger.valueOf(dayTypeAssignments.indexOf(dayTypeAssignment)));
        }

        serviceCalendarFrame.setDayTypes(dayTypesStruct);
        serviceCalendarFrame.setDayTypeAssignments(dayTypeAssignmentsStruct);

        return serviceCalendarFrame;
    }
}
