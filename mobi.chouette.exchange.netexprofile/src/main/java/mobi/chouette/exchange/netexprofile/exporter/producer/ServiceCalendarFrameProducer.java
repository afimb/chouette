package mobi.chouette.exchange.netexprofile.exporter.producer;

import com.google.common.base.Joiner;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Line;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.toLocalDate;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.toOffsetDateTime;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceCalendarFrameProducer extends NetexProducer implements NetexFrameProducer<ServiceCalendarFrame> {

    static final String LOCAL_CONTEXT = "ServiceCalendar";
    static final String DAY_TYPE_IDS = "dayTypeIds";

    private static final String DAY_TYPE_PATTERN = "MMM_EEE_dd";

    @Override
    public ServiceCalendarFrame produce(Context context, ExportableData data) {
        Line line = data.getLine();

        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME_KEY, line.objectIdSuffix());

        ServiceCalendarFrame serviceCalendarFrame = netexFactory.createServiceCalendarFrame()
                .withVersion("any")
                .withId(serviceCalendarFrameId);

        DayTypesInFrame_RelStructure dayTypesStruct = netexFactory.createDayTypesInFrame_RelStructure();
        DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentsStruct = netexFactory.createDayTypeAssignmentsInFrame_RelStructure();
        OperatingPeriodsInFrame_RelStructure operatingPeriodStruct = netexFactory.createOperatingPeriodsInFrame_RelStructure();

        Set<String> processedIds = new HashSet<>();

        for (Timetable timetable : data.getTimetables()) {

            // TODO find out if this is necessary for export
            timetable.computeLimitOfPeriods();

            if (CollectionUtils.isNotEmpty(timetable.getDayTypes())) {

                // TODO maybe we should get the effective period(s) from timtable and base the OperatingPeriod on that.
                // TODO extract all period stuff to separate method?
                Period period = timetable.getPeriods().get(0);
                LocalDate localStartDate = toLocalDate(period.getStartDate());
                LocalDate localEndDate = toLocalDate(period.getEndDate());

                // TODO how can we split the day types into weekdays, and weekend days?
                //SortedSet<DayOfWeekEnumeration> daysOfWeek = pattern.significantDays.stream().map(sd -> toDayOfWeekEnumeration(sd)).collect(Collectors.toCollection(TreeSet::new));
                List<DayOfWeekEnumeration> dayOfWeekEnumerations = NetexProducerUtils.toDayOfWeekEnumeration(timetable.getDayTypes());
                StringBuilder dayOfWeekBuilder = new StringBuilder();

                for (DayOfWeekEnumeration dayOfWeekEnumeration : dayOfWeekEnumerations) {
                    dayOfWeekBuilder.append(dayOfWeekEnumeration.value().substring(0, 2));
                }

                Object[] dayTypeIdSuffixParts = {line.objectIdSuffix(), format(localStartDate), format(localEndDate), dayOfWeekBuilder.toString()};
                String dayTypeIdSuffix = StringUtils.join(dayTypeIdSuffixParts, "-");
                String dayTypeId = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, dayTypeIdSuffix);

                if (!processedIds.contains(dayTypeId)) {
                    DayType dayType = netexFactory.createDayType();
                    String version = timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                    dayType.setVersion(version);
                    dayType.setId(dayTypeId);

                    String operatingPeriodIdSuffix = Joiner.on("-").join(line.objectIdSuffix(), format(localStartDate), format(localEndDate));
                    String operatingPeriodId = netexId(timetable.objectIdPrefix(), OPERATING_PERIOD_KEY, operatingPeriodIdSuffix);

                    if (!processedIds.contains(operatingPeriodId)) {
                        OperatingPeriod operatingPeriod = new OperatingPeriod()
                                .withVersion(version)
                                .withId(operatingPeriodId)
                                .withFromDate(toOffsetDateTime(period.getStartDate()))
                                .withToDate(toOffsetDateTime(period.getEndDate()));
                        operatingPeriodStruct.getOperatingPeriodOrUicOperatingPeriod().add(operatingPeriod);

                        // TODO sort periods by from date?
                        //operatingPeriodStruct.getOperatingPeriodOrUicOperatingPeriod().sort(Comparator.comparing(OperatingPeriod_VersionStructure::getFromDate));
                        //serviceCalendarFrame.withOperatingPeriods(operatingPeriodStruct);

                        processedIds.add(operatingPeriodId);
                    }

                    PropertyOfDay propertyOfDay = netexFactory.createPropertyOfDay();
                    for (DayOfWeekEnumeration dayOfWeekEnumeration : dayOfWeekEnumerations) {
                        propertyOfDay.getDaysOfWeek().add(dayOfWeekEnumeration);
                    }

                    PropertiesOfDay_RelStructure propertiesOfDay = netexFactory.createPropertiesOfDay_RelStructure();
                    propertiesOfDay.getPropertyOfDay().add(propertyOfDay);
                    dayType.setProperties(propertiesOfDay);

                    dayTypesStruct.getDayType_().add(netexFactory.createDayType(dayType));

                    String dayTypeAssignmentId = netexId(timetable.objectIdPrefix(), DAY_TYPE_ASSIGNMENT_KEY, objectIdSuffix(dayTypeId));

                    DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure()
                            .withVersion(version)
                            .withRef(dayTypeId);

                    OperatingPeriodRefStructure operatingPeriodRefStruct = netexFactory.createOperatingPeriodRefStructure()
                            .withVersion(version)
                            .withRef(operatingPeriodId);

                    DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
                            .withVersion(version)
                            .withId(dayTypeAssignmentId)
                            .withOperatingPeriodRef(operatingPeriodRefStruct)
                            .withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRefStruct));
                    dayTypeAssignmentsStruct.getDayTypeAssignment().add(dayTypeAssignment);

                    processedIds.add(dayTypeId);
                }
                for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys()) {
                    addDayTypeId(context, vehicleJourney.getObjectId(), dayTypeId);
                }
            }

            if (CollectionUtils.isNotEmpty(timetable.getPeculiarDates())) {
                for (Date includedDate : timetable.getPeculiarDates()) {
                    String dayTypeIdSuffix = StringUtils.join(new Object[] {line.objectIdSuffix(), format(toLocalDate(includedDate))}, "-");
                    String dayTypeId = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, dayTypeIdSuffix);

                    if (!processedIds.contains(dayTypeId)) {
                        DayType dayType = netexFactory.createDayType();
                        String version = timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                        dayType.setVersion(version);
                        dayType.setId(dayTypeId);

                        String dayTypeAssignmentId = netexId(timetable.objectIdPrefix(), DAY_TYPE_ASSIGNMENT_KEY, objectIdSuffix(dayTypeId));

                        DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure()
                                .withVersion(version)
                                .withRef(dayTypeId);

                        DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
                                .withVersion(version)
                                .withId(dayTypeAssignmentId)
                                .withDate(toOffsetDateTime(includedDate))
                                .withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRefStruct));

                        dayTypesStruct.getDayType_().add(netexFactory.createDayType(dayType));
                        dayTypeAssignmentsStruct.getDayTypeAssignment().add(dayTypeAssignment);

                        processedIds.add(dayTypeId);
                    }
                    for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys()) {
                        addDayTypeId(context, vehicleJourney.getObjectId(), dayTypeId);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(timetable.getExcludedDates())) {
                for (Date excludedDate : timetable.getExcludedDates()) {
                    String dayTypeIdSuffix = StringUtils.join(new Object[] {line.objectIdSuffix(), format(toLocalDate(excludedDate)), "X"}, "-");
                    String dayTypeId = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, dayTypeIdSuffix);

                    if (!processedIds.contains(dayTypeId)) {
                        DayType dayType = netexFactory.createDayType();
                        String version = timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                        dayType.setVersion(version);
                        dayType.setId(dayTypeId);

                        String dayTypeAssignmentId = netexId(timetable.objectIdPrefix(), DAY_TYPE_ASSIGNMENT_KEY, objectIdSuffix(dayTypeId));

                        DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure()
                                .withVersion(version)
                                .withRef(dayTypeId);

                        DayTypeAssignment dayTypeAssignment = netexFactory.createDayTypeAssignment()
                                .withVersion(version)
                                .withId(dayTypeAssignmentId)
                                .withDate(toOffsetDateTime(excludedDate))
                                .withIsAvailable(Boolean.FALSE)
                                .withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRefStruct));

                        dayTypesStruct.getDayType_().add(netexFactory.createDayType(dayType));
                        dayTypeAssignmentsStruct.getDayTypeAssignment().add(dayTypeAssignment);

                        processedIds.add(dayTypeId);
                    }
                    for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys()) {
                        addDayTypeId(context, vehicleJourney.getObjectId(), dayTypeId);
                    }
                }
            }
        }

        List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentsStruct.getDayTypeAssignment();
        for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
            dayTypeAssignment.setOrder(BigInteger.valueOf(dayTypeAssignments.indexOf(dayTypeAssignment)));
        }

        serviceCalendarFrame.setDayTypes(dayTypesStruct);
        serviceCalendarFrame.setDayTypeAssignments(dayTypeAssignmentsStruct);

        if (CollectionUtils.isNotEmpty(operatingPeriodStruct.getOperatingPeriodOrUicOperatingPeriod())) {
            serviceCalendarFrame.setOperatingPeriods(operatingPeriodStruct);
        }

        return serviceCalendarFrame;
    }

    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DAY_TYPE_PATTERN));
    }

    @SuppressWarnings("unchecked")
    public void addDayTypeId(Context context, String objectId, String dayTypeId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> dayTypeIds = (List<String>) objectContext.get(DAY_TYPE_IDS);

        if (dayTypeIds == null) {
            dayTypeIds = new ArrayList<>();
            objectContext.put(DAY_TYPE_IDS, dayTypeIds);
        }

        dayTypeIds.add(dayTypeId);
    }

}
