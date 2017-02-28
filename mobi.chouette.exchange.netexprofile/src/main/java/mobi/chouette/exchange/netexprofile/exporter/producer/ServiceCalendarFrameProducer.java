package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import java.sql.Date;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.DAY_TYPE_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_CALENDAR_FRAME_KEY;

public class ServiceCalendarFrameProducer extends NetexProducer { //implements NetexFrameProducer<ServiceCalendarFrame> {

    //@Override
    public ServiceCalendarFrame produce(ExportableData data) {
        Line line = data.getLine();

        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, line.objectIdSuffix());

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion("any")
                .withId(serviceCalendarFrameId);

        DayTypesInFrame_RelStructure dayTypesStruct = netexFactory.createDayTypesInFrame_RelStructure();

        for (Timetable timetable : data.getTimetables()) {
            timetable.computeLimitOfPeriods();

            DayType dayType = netexFactory.createDayType();
            dayType.setVersion(timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String dayTypeId = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, timetable.objectIdSuffix());
            dayType.setId(dayTypeId);

            if (isSet(timetable.getComment())) {
                dayType.setShortName(getMultilingualString(timetable.getComment()));
            }

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

            List<Date> peculiarDates = timetable.getEffectiveDates();
            for (Date peculiarDate : peculiarDates) {
                // TODO create operating days?
            }

            List<Period> effectivePeriods = timetable.getEffectivePeriods();
            for (Period period : effectivePeriods) {
                // TODO create operating periods?
            }

            for (Period period : effectivePeriods) {
                // TODO create assignments between day types and periods
            }

            for (Date day : peculiarDates) {
                // TODO create assignments between day types and operating days
            }
        }

        return serviceCalendarFrame;
    }
}
