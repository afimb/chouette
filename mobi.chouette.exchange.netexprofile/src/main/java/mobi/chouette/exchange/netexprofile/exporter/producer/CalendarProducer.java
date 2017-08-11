package mobi.chouette.exchange.netexprofile.exporter.producer;

import com.google.common.base.Joiner;
import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.*;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class CalendarProducer extends NetexProducer {

    public static final String DAY_TYPES_KEY = "DayTypes";
    public static final String DAY_TYPE_ASSIGNMENTS_KEY = "DayTypeAssignments";
    public static final String OPERATING_PERIODS_KEY = "OperatingPeriods";

    static final String LOCAL_CONTEXT = "ServiceCalendar";
    static final String DAY_TYPE_IDS = "dayTypeIds";

    private static final String DAY_TYPE_PATTERN = "MMM_EEE_dd";

    //@Override
    public Map<String, List<? extends DataManagedObjectStructure>> produce(Context context, ExportableData exportableData) {
        Line line = exportableData.getLine();
        Set<String> processedIds = new HashSet<>();
        Map<String, List<? extends DataManagedObjectStructure>> calendarData = new HashMap<>();
        List<DayType> dayTypes = new ArrayList<>();
        List<DayTypeAssignment> dayTypeAssignments = new ArrayList<>();
        List<OperatingPeriod> operatingPeriods = new ArrayList<>();

        for (Timetable timetable : exportableData.getTimetables()) {
            //timetable.computeLimitOfPeriods(); // necessary before export?

            String version = timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;

            if (CollectionUtils.isNotEmpty(timetable.getDayTypes())) {
                Period period = timetable.getPeriods().get(0);
                LocalDate localStartDate = TimeUtil.toLocalTimeFromJoda(period.getStartDate());
                LocalDate localEndDate = TimeUtil.toLocalTimeFromJoda(period.getEndDate());

                // TODO split the day types into weekdays, and weekend days, and sort by weekday nr.
                List<DayOfWeekEnumeration> dayOfWeekEnumerations = NetexProducerUtils.toDayOfWeekEnumeration(timetable.getDayTypes());
                StringBuilder dayOfWeekBuilder = new StringBuilder();

                for (DayOfWeekEnumeration dayOfWeekEnumeration : dayOfWeekEnumerations) {
                    dayOfWeekBuilder.append(dayOfWeekEnumeration.value().substring(0, 2));
                }

                Object[] dayTypeIdSuffixParts = {line.objectIdSuffix(), format(localStartDate), format(localEndDate), dayOfWeekBuilder.toString()};
                String dayTypeId = createDayTypeId(timetable.objectIdPrefix(), dayTypeIdSuffixParts);

                if (!processedIds.contains(dayTypeId)) {
                    DayType dayType = createDayType(version, dayTypeId);
                    dayTypes.add(dayType);

                    String operatingPeriodIdSuffix = Joiner.on("-").join(line.objectIdSuffix(), format(localStartDate), format(localEndDate));
                    String operatingPeriodId = netexId(timetable.objectIdPrefix(), OPERATING_PERIOD, operatingPeriodIdSuffix);

                    if (!processedIds.contains(operatingPeriodId)) {
                        OperatingPeriod operatingPeriod = createOperatingPeriod(version, operatingPeriodId, period);
                        operatingPeriods.add(operatingPeriod);
                        processedIds.add(operatingPeriodId);
                    }

                    dayType.setProperties(createPropertiesOfDay_RelStructure(dayOfWeekEnumerations));

                    DayTypeAssignment dayTypeAssignment = createDayTypeAssignment(version, dayTypeId, createDayTypeAssignmentId(timetable, dayTypeId));
                    dayTypeAssignment.setOperatingPeriodRef(createOperatingPeriodRefStructure(version, operatingPeriodId));
                    dayTypeAssignments.add(dayTypeAssignment);

                    processedIds.add(dayTypeId);
                }

                bindDayTypeToJourneys(context, timetable, dayTypeId, exportableData.getVehicleJourneys());
            }

            if (CollectionUtils.isNotEmpty(timetable.getPeculiarDates())) {
                for (org.joda.time.LocalDate includedDate : timetable.getPeculiarDates()) {
                    String dayTypeId = createDayTypeId(timetable.objectIdPrefix(), new Object[] {line.objectIdSuffix(), format(TimeUtil.toLocalTimeFromJoda(includedDate))});

                    if (!processedIds.contains(dayTypeId)) {
                        DayType dayType = createDayType(version, dayTypeId);
                        dayTypes.add(dayType);

                        DayTypeAssignment dayTypeAssignment = createDayTypeAssignment(version, dayTypeId, createDayTypeAssignmentId(timetable, dayTypeId));
                        dayTypeAssignment.setDate(TimeUtil.toOffsetDateTime(includedDate));
                        dayTypeAssignments.add(dayTypeAssignment);

                        processedIds.add(dayTypeId);
                    }

                    bindDayTypeToJourneys(context, timetable, dayTypeId, exportableData.getVehicleJourneys());
                }
            }

            if (CollectionUtils.isNotEmpty(timetable.getExcludedDates())) {
                for (org.joda.time.LocalDate excludedDate : timetable.getExcludedDates()) {
                    String dayTypeId = createDayTypeId(timetable.objectIdPrefix(), new Object[] {line.objectIdSuffix(), format(TimeUtil.toLocalTimeFromJoda(excludedDate)), "X"});

                    if (!processedIds.contains(dayTypeId)) {
                        DayType dayType = createDayType(version, dayTypeId);
                        dayTypes.add(dayType);

                        DayTypeAssignment dayTypeAssignment = createDayTypeAssignment(version, dayTypeId, createDayTypeAssignmentId(timetable, dayTypeId));
                        dayTypeAssignment.setDate(TimeUtil.toOffsetDateTime(excludedDate));
                        dayTypeAssignment.setIsAvailable(Boolean.FALSE);
                        dayTypeAssignments.add(dayTypeAssignment);

                        processedIds.add(dayTypeId);
                    }

                    bindDayTypeToJourneys(context, timetable, dayTypeId, exportableData.getVehicleJourneys());
                }
            }
        }

        // TODO sort assignments by date?
        for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
            dayTypeAssignment.setOrder(BigInteger.valueOf(dayTypeAssignments.indexOf(dayTypeAssignment)));
        }

        calendarData.put(DAY_TYPES_KEY, dayTypes);
        calendarData.put(DAY_TYPE_ASSIGNMENTS_KEY, dayTypeAssignments);
        calendarData.put(OPERATING_PERIODS_KEY, operatingPeriods);
        return calendarData;
    }

    private void bindDayTypeToJourneys(Context context, Timetable timetable, String dayTypeId, List<VehicleJourney> vehicleJourneys) {
        for (VehicleJourney vehicleJourney : timetable.getVehicleJourneys()) {
            if (vehicleJourneys.contains(vehicleJourney)) {
                addDayTypeId(context, vehicleJourney.getObjectId(), dayTypeId);
            }
        }
    }

    private String createDayTypeId(String dayTypeIdPrefix, Object[] dayTypeIdSuffixParts) {
        return netexId(dayTypeIdPrefix, DAY_TYPE, StringUtils.join(dayTypeIdSuffixParts, "-"));
    }

    private String createDayTypeAssignmentId(Timetable timetable, String dayTypeId) {
        return netexId(timetable.objectIdPrefix(), DAY_TYPE_ASSIGNMENT, objectIdSuffix(dayTypeId));
    }

    private DayType createDayType(String version, String dayTypeId) {
        return netexFactory.createDayType()
                .withVersion(version)
                .withId(dayTypeId);
    }

    private DayTypeAssignment createDayTypeAssignment(String version, String dayTypeId, String dayTypeAssignmentId) {
        DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure()
                .withVersion(version)
                .withRef(dayTypeId);

        return netexFactory.createDayTypeAssignment()
                .withVersion(version)
                .withId(dayTypeAssignmentId)
                .withDayTypeRef(netexFactory.createDayTypeRef(dayTypeRefStruct));
    }

    private OperatingPeriod createOperatingPeriod(String version, String operatingPeriodId, Period period) {
        return new OperatingPeriod()
                .withVersion(version)
                .withId(operatingPeriodId)
                .withFromDate(TimeUtil.toOffsetDateTime(period.getStartDate()))
                .withToDate(TimeUtil.toOffsetDateTime(period.getEndDate()));
    }

    private OperatingPeriodRefStructure createOperatingPeriodRefStructure(String version, String operatingPeriodId) {
        return netexFactory.createOperatingPeriodRefStructure()
                .withVersion(version)
                .withRef(operatingPeriodId);
    }

    private PropertiesOfDay_RelStructure createPropertiesOfDay_RelStructure(List<DayOfWeekEnumeration> dayOfWeekEnumerations) {
        PropertyOfDay propertyOfDay = netexFactory.createPropertyOfDay();
        for (DayOfWeekEnumeration dayOfWeekEnumeration : dayOfWeekEnumerations) {
            propertyOfDay.getDaysOfWeek().add(dayOfWeekEnumeration);
        }

        PropertiesOfDay_RelStructure propertiesOfDay = netexFactory.createPropertiesOfDay_RelStructure();
        propertiesOfDay.getPropertyOfDay().add(propertyOfDay);
        return propertiesOfDay;
    }

    private String format(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(DAY_TYPE_PATTERN));
    }

    @SuppressWarnings("unchecked")
    private void addDayTypeId(Context context, String objectId, String dayTypeId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        List<String> dayTypeIds = (List<String>) objectContext.get(DAY_TYPE_IDS);

        if (dayTypeIds == null) {
            dayTypeIds = new ArrayList<>();
            objectContext.put(DAY_TYPE_IDS, dayTypeIds);
        }

        dayTypeIds.add(dayTypeId);
    }

}
