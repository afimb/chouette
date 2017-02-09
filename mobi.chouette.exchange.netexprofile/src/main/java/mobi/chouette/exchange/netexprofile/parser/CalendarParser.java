package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.CalendarDay;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class CalendarParser implements Parser, Constant {

    private static final String PERIOD_START = "period_start";
    private static final String PERIOD_END = "period_end";

    private Map<String, Date> validityConditions;

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        RelationshipStructure relationshipStruct = (RelationshipStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        Timetable timetable = new Timetable();

        if (relationshipStruct instanceof ValidityConditions_RelStructure) {
            validityConditions = new HashMap<>();
            ValidityConditions_RelStructure validityConditionsStruct = (ValidityConditions_RelStructure) relationshipStruct;
            parseValidityConditions(validityConditionsStruct);
        } else if (relationshipStruct instanceof DayTypesInFrame_RelStructure) {
            DayTypesInFrame_RelStructure dayTypesStruct = (DayTypesInFrame_RelStructure) relationshipStruct;
            List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeElements = dayTypesStruct.getDayType_();

            for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : dayTypeElements) {
                DayType dayType = (DayType) dayTypeElement.getValue();
                parseDayType(dayType, timetable);
            }
        } else if (relationshipStruct instanceof OperatingDaysInFrame_RelStructure) {
            OperatingDaysInFrame_RelStructure operatingDaysStruct = (OperatingDaysInFrame_RelStructure) relationshipStruct;
            List<OperatingDay> operatingDays = operatingDaysStruct.getOperatingDay();

            for (OperatingDay operatingDay : operatingDays) {
                parseOperatingDay(operatingDay, timetable);
            }
        } else if (relationshipStruct instanceof OperatingPeriodsInFrame_RelStructure) {
            OperatingPeriodsInFrame_RelStructure operatingPeriodsStruct = (OperatingPeriodsInFrame_RelStructure) relationshipStruct;
            List<OperatingPeriod_VersionStructure> operatingPeriodStructs = operatingPeriodsStruct.getOperatingPeriodOrUicOperatingPeriod();

            for (OperatingPeriod_VersionStructure operatingPeriodStruct : operatingPeriodStructs) {
                OperatingPeriod operatingPeriod = (OperatingPeriod) operatingPeriodStruct;
                parseOperatingPeriod(operatingPeriod, timetable);
            }
        }

        // link timetable to referential
        ObjectFactory.getTimetable(referential, timetable.getObjectId());
        timetable.setFilled(true);
    }

    @SuppressWarnings("unchecked")
    private void parseValidityConditions(ValidityConditions_RelStructure validityConditionsStruct) throws Exception {
        List<Object> availabilityConditionElements = validityConditionsStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_();
        AvailabilityCondition availabilityCondition = ((JAXBElement<AvailabilityCondition>) availabilityConditionElements.get(0)).getValue();
        OffsetDateTime fromDate = availabilityCondition.getFromDate();
        OffsetDateTime toDate = availabilityCondition.getToDate();
        validityConditions.put(PERIOD_START, ParserUtils.getSQLDate(fromDate.toString()));
        validityConditions.put(PERIOD_END, ParserUtils.getSQLDate(toDate.toString()));
    }

    private void parseDayType(DayType dayType, Timetable timetable) throws Exception {
        timetable.setObjectId(dayType.getId());

        Integer version = Integer.valueOf(dayType.getVersion());
        timetable.setObjectVersion(version != null ? version : 0);

        if (dayType.getName() != null) {
            timetable.setComment(dayType.getName().getValue());
        }
        if (dayType.getShortName() != null) {
            timetable.setVersion(dayType.getName().getValue());
        }

        PropertiesOfDay_RelStructure propertiesOfDayStruct = dayType.getProperties();
        List<PropertyOfDay> propertyOfDayList = propertiesOfDayStruct.getPropertyOfDay();

        for (PropertyOfDay propertyOfDay : propertyOfDayList) {
            List<DayOfWeekEnumeration> daysOfWeek = propertyOfDay.getDaysOfWeek();
            String[] weekDays = StringUtils.splitByWholeSeparator(daysOfWeek.get(0).value(), null);
            List<String> weekDaysList = Arrays.asList(weekDays);
            timetable.setDayTypes(NetexParserUtils.getDayTypes(weekDaysList));
        }
    }

    private void parseOperatingDay(OperatingDay operatingDay, Timetable timetable) throws Exception {
        LocalDate calendarDate = operatingDay.getCalendarDate();
        CalendarDay value = new CalendarDay(Date.valueOf(calendarDate), true);
        timetable.addCalendarDay(value);
    }

    private void parseOperatingPeriod(OperatingPeriod operatingPeriod, Timetable timetable) throws Exception {
        Period period = new Period();

        if (validityConditions != null && validityConditions.size() > 0 ) {
            period.setStartDate(validityConditions.get(PERIOD_START));
            period.setEndDate(validityConditions.get(PERIOD_END));
        } else {
            OffsetDateTime fromDate = operatingPeriod.getFromDate();
            OffsetDateTime toDate = operatingPeriod.getToDate();
            period.setStartDate(ParserUtils.getSQLDate(fromDate.toString()));
            period.setEndDate(ParserUtils.getSQLDate(toDate.toString()));
        }
        timetable.addPeriod(period);
    }

    static {
        ParserFactory.register(CalendarParser.class.getName(),
                new ParserFactory() {
                    private CalendarParser instance = new CalendarParser();

                    @Override
                    protected Parser create() {
                        return instance;
                    }
                });
    }


}
