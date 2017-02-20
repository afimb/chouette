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
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ServiceCalendarParser extends NetexParser implements Parser, Constant {

    @Override
    @SuppressWarnings("unchecked")
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);
        Context dayTypeAssignmentContext = (Context) parsingContext.get(DayTypeAssignmentParser.LOCAL_CONTEXT);

        ServiceCalendar serviceCalendar = (ServiceCalendar) context.get(NETEX_LINE_DATA_CONTEXT);

        // parse validity conditions

        Period period = new Period();

        if (serviceCalendar.getFromDate() != null && serviceCalendar.getToDate() != null) {
            period.setStartDate(ParserUtils.getSQLDate(serviceCalendar.getFromDate().toString()));
            period.setEndDate(ParserUtils.getSQLDate(serviceCalendar.getToDate().toString()));
        } else {
            ValidBetween entityValidity = getValidBetween(serviceCalendar);
            if (entityValidity != null) {
                period.setStartDate(ParserUtils.getSQLDate(entityValidity.getFromDate().toString()));
                period.setEndDate(ParserUtils.getSQLDate(entityValidity.getToDate().toString()));
            } else {
                ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
                if (calendarFrameValidity != null) {
                    period.setStartDate(ParserUtils.getSQLDate(calendarFrameValidity.getFromDate().toString()));
                    period.setEndDate(ParserUtils.getSQLDate(calendarFrameValidity.getToDate().toString()));
                } else {
                    ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
                    if (compositeFrameValidity != null) {
                        period.setStartDate(ParserUtils.getSQLDate(compositeFrameValidity.getFromDate().toString()));
                        period.setEndDate(ParserUtils.getSQLDate(compositeFrameValidity.getToDate().toString()));
                    }
                }
            }
        }
        if (isPeriodEmpty(period)) {
            throw new RuntimeException("No validity conditions available");
        }

        // parse day type assignments if present

        Map<String, LocalDate> dateOfOperationMap = new HashMap<>();

        if (serviceCalendar.getDayTypeAssignments() != null) {
            DayTypeAssignments_RelStructure dayTypeAssignmentStruct = serviceCalendar.getDayTypeAssignments();
            List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentStruct.getDayTypeAssignment();

            for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
                JAXBElement<? extends DayTypeRefStructure> dayTypeRefElement = dayTypeAssignment.getDayTypeRef();
                LocalDate dateOfOperation = dayTypeAssignment.getDate();

                if (dayTypeRefElement != null && dateOfOperation != null) {
                    dateOfOperationMap.put(dayTypeRefElement.getValue().getRef(), dateOfOperation);
                }
            }
        }

        // parse day types

        // TODO consider using reusable logic in DayTypeParser instead

        DayTypes_RelStructure dayTypesStruct = serviceCalendar.getDayTypes();
        List<JAXBElement<?>> dayTypeElements = dayTypesStruct.getDayTypeRefOrDayType_();

        for (JAXBElement<?> dayTypeElement : dayTypeElements) {
            DayType dayType = (DayType) dayTypeElement.getValue();
            Timetable timetable = ObjectFactory.getTimetable(referential, dayType.getId());
            timetable.setObjectVersion(NetexParserUtils.getVersion(dayType));

            if (dayType.getName() != null) {
                timetable.setComment(dayType.getName().getValue());
            }

            PropertiesOfDay_RelStructure propertiesOfDayStruct = dayType.getProperties();

            if (propertiesOfDayStruct != null && !propertiesOfDayStruct.getPropertyOfDay().isEmpty()) {
                List<PropertyOfDay> propertyOfDayList = propertiesOfDayStruct.getPropertyOfDay();

                for (PropertyOfDay propertyOfDay : propertyOfDayList) {
                    List<DayOfWeekEnumeration> daysOfWeeks = propertyOfDay.getDaysOfWeek();

                    for (DayOfWeekEnumeration dayOfWeek : daysOfWeeks) {
                        List<DayTypeEnum> convertDayOfWeek = NetexParserUtils.convertDayOfWeek(dayOfWeek);
                        for (DayTypeEnum e : convertDayOfWeek) {
                            timetable.addDayType(e);
                        }
                    }
                }
            } else {
                if (!CollectionUtils.sizeIsEmpty(dateOfOperationMap) && dateOfOperationMap.containsKey(dayType.getId())) {
                    LocalDate dateOfOperation = dateOfOperationMap.get(dayType.getId());
                    timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dateOfOperation), true));
                } else {
                    Context dayTypeAssignmentObjectContext = (Context) dayTypeAssignmentContext.get(dayType.getId());

                    if (dayTypeAssignmentObjectContext != null) {
                        LocalDate dateOfOperation = (LocalDate) dayTypeAssignmentObjectContext.get(DayTypeAssignmentParser.DATE_OF_OPERATION);
                        timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dateOfOperation), true));
                    } else {
                        throw new RuntimeException("No valid day types found");
                    }
                }
            }

            timetable.addPeriod(period);
            timetable.setFilled(true);
        }
    }

    static {
        ParserFactory.register(ServiceCalendarParser.class.getName(), new ParserFactory() {
            private ServiceCalendarParser instance = new ServiceCalendarParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
