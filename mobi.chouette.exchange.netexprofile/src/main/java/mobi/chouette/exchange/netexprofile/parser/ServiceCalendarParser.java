package mobi.chouette.exchange.netexprofile.parser;

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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceCalendarParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "ServiceCalendar";
    static final String TIMETABLE_ID = "timetableId";

    private Map<String, OffsetDateTime> dayTypeIdDateMapper = new HashMap<>();
    private Map<String, OffsetDateTime> operatingDayIdDateMapper = new HashMap<>();

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        ServiceCalendarFrame serviceCalendarFrame = (ServiceCalendarFrame) context.get(NETEX_LINE_DATA_CONTEXT);
        ValidBetween validBetween = getValidBetweenForFrame(context);

        String timetableId = String.format("%s:Timetable:%s", NetexParserUtils.objectIdPrefix(serviceCalendarFrame.getId()),
                NetexParserUtils.objectIdSuffix(serviceCalendarFrame.getId()));

        Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
        timetable.setObjectVersion(NetexParserUtils.getVersion(serviceCalendarFrame));

        if (serviceCalendarFrame.getOperatingDays() != null) {
            for (OperatingDay operatingDay : serviceCalendarFrame.getOperatingDays().getOperatingDay()) {
                if (!operatingDayIdDateMapper.containsKey(operatingDay.getId())) {
                    operatingDayIdDateMapper.put(operatingDay.getId(), operatingDay.getCalendarDate());
                }
            }
        }
        if (serviceCalendarFrame.getServiceCalendar() != null) {
            if (serviceCalendarFrame.getServiceCalendar().getOperatingDays() != null) {
                for (Object genericOperatingDay : serviceCalendarFrame.getServiceCalendar().getOperatingDays().getOperatingDayRefOrOperatingDay()) {
                    OperatingDay operatingDay = (OperatingDay) genericOperatingDay;

                    if (!operatingDayIdDateMapper.containsKey(operatingDay.getId())) {
                        operatingDayIdDateMapper.put(operatingDay.getId(), operatingDay.getCalendarDate());
                    }
                }
            }
        }

        if (serviceCalendarFrame.getDayTypeAssignments() != null) {
            for (DayTypeAssignment dayTypeAssignment : serviceCalendarFrame.getDayTypeAssignments().getDayTypeAssignment()) {
                String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();

                if (dayTypeAssignment.getOperatingDayRef() != null) {
                    String operatingDayIdRef = dayTypeAssignment.getOperatingDayRef().getRef();
                    OffsetDateTime dateOfOperation = operatingDayIdDateMapper.get(operatingDayIdRef);

                    if (dateOfOperation != null && isWithinValidRange(dateOfOperation, validBetween) && !dayTypeIdDateMapper.containsKey(dayTypeIdRef)) {
                        dayTypeIdDateMapper.put(dayTypeIdRef, dateOfOperation);
                    }
                } else {
                	OffsetDateTime dateOfOperation = dayTypeAssignment.getDate();

                    if (dateOfOperation != null && !dayTypeIdDateMapper.containsKey(dayTypeIdRef)) {
                        dayTypeIdDateMapper.put(dayTypeIdRef, dateOfOperation);
                    }
                }
            }
        }

        if (serviceCalendarFrame.getServiceCalendar() != null) {
            ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();
            ValidBetween calendarValidBetween = getValidBetween(context, serviceCalendar);

            //timetable.setObjectVersion(NetexParserUtils.getVersion(serviceCalendar));

            if (serviceCalendar.getName() != null) {
                timetable.setComment(serviceCalendar.getName().getValue());
            }

            if (serviceCalendar.getDayTypeAssignments() != null) {
                for (DayTypeAssignment dayTypeAssignment : serviceCalendar.getDayTypeAssignments().getDayTypeAssignment()) {
                    String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();

                    if (dayTypeAssignment.getOperatingDayRef() != null) {
                        String operatingDayIdRef = dayTypeAssignment.getOperatingDayRef().getRef();
                        OffsetDateTime dateOfOperation = operatingDayIdDateMapper.get(operatingDayIdRef);

                        if (dateOfOperation != null && isWithinValidRange(dateOfOperation, calendarValidBetween) && !dayTypeIdDateMapper.containsKey(dayTypeIdRef)) {
                            dayTypeIdDateMapper.put(dayTypeIdRef, dateOfOperation);
                        }
                    } else {
                        OffsetDateTime dateOfOperation = dayTypeAssignment.getDate();

                        if (dateOfOperation != null && !dayTypeIdDateMapper.containsKey(dayTypeIdRef)) {
                            dayTypeIdDateMapper.put(dayTypeIdRef, dateOfOperation);
                        }
                    }
                }
            }

            if (serviceCalendar.getDayTypes() != null) {
                List<JAXBElement<?>> dayTypeElements = serviceCalendar.getDayTypes().getDayTypeRefOrDayType_();

                for (JAXBElement<?> dayTypeElement : dayTypeElements) {
                    DayType dayType = (DayType) dayTypeElement.getValue();
                    parseDayType(context, dayType, timetable);
                }
            }

            if (serviceCalendar.getOperatingPeriods() != null) {
                List<Object> operatingPeriods = serviceCalendar.getOperatingPeriods().getOperatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod();

                if (CollectionUtils.isNotEmpty(operatingPeriods)) {
                    Period period = new Period();

                    for (Object genericOperatingPeriod : operatingPeriods) {
                        OperatingPeriod operatingPeriod = (OperatingPeriod) genericOperatingPeriod;
                        period.setStartDate(ParserUtils.getSQLDate(operatingPeriod.getFromDate().toString()));
                        period.setEndDate(ParserUtils.getSQLDate(operatingPeriod.getToDate().toString()));
                    }

                    timetable.getPeriods().add(period);
                }
            }
        }

        if (serviceCalendarFrame.getDayTypes() != null) {
            for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : serviceCalendarFrame.getDayTypes().getDayType_()) {
                DayType dayType = (DayType) dayTypeElement.getValue();
                parseDayType(context, dayType, timetable);
            }
        }

        if (serviceCalendarFrame.getOperatingPeriods() != null) {
            List<OperatingPeriod_VersionStructure> operatingPeriodStructs = serviceCalendarFrame.getOperatingPeriods().getOperatingPeriodOrUicOperatingPeriod();

            if (CollectionUtils.isNotEmpty(operatingPeriodStructs)) {
                Period period = new Period();

                for (OperatingPeriod_VersionStructure operatingPeriodStruct : operatingPeriodStructs) {
                    period.setStartDate(ParserUtils.getSQLDate(operatingPeriodStruct.getFromDate().toString()));
                    period.setEndDate(ParserUtils.getSQLDate(operatingPeriodStruct.getToDate().toString()));
                }

                timetable.getPeriods().add(period);
            }
        }

        referential.getTimetables().put(timetable.getObjectId(), timetable);
        timetable.setFilled(true);
    }

    private void parseDayType(Context context, DayType dayType, Timetable timetable) {
        if (dayType.getProperties() != null) {
            for (PropertyOfDay propertyOfDay : dayType.getProperties().getPropertyOfDay()) {
                List<DayOfWeekEnumeration> daysOfWeeks = propertyOfDay.getDaysOfWeek();

                for (DayOfWeekEnumeration dayOfWeek : daysOfWeeks) {
                    List<DayTypeEnum> dayTypeEnums = NetexParserUtils.convertDayOfWeek(dayOfWeek);

                    for (DayTypeEnum dayTypeEnum : dayTypeEnums) {
                        timetable.addDayType(dayTypeEnum);
                    }
                }
            }
            addTimetableId(context, dayType.getId(), timetable.getObjectId());
        } else {
            if (dayTypeIdDateMapper.containsKey(dayType.getId())) {
            	OffsetDateTime dateOfOperation = dayTypeIdDateMapper.get(dayType.getId());

                if (dateOfOperation != null) {
                    timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dateOfOperation.toLocalDate()), true));
                    addTimetableId(context, dayType.getId(), timetable.getObjectId());
                }
            }
        }
    }

    private ValidBetween getValidBetweenForFrame(Context context) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

        ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
        if (calendarFrameValidity != null) {
            return calendarFrameValidity;
        } else {
            ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
            if (compositeFrameValidity != null) {
                return compositeFrameValidity;
            }
        }

        return null;
    }

    private ValidBetween getValidBetween(Context context, ServiceCalendar serviceCalendar) throws Exception {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

        if (serviceCalendar.getFromDate() != null && serviceCalendar.getToDate() != null) {
            OffsetDateTime fromDateTime = serviceCalendar.getFromDate();
            OffsetDateTime toDateTime = serviceCalendar.getToDate();
            return new ValidBetween().withFromDate(fromDateTime).withToDate(toDateTime);
        } else {
            ValidBetween entityValidity = getValidBetween(serviceCalendar);
            if (entityValidity != null) {
                return entityValidity;
            } else {
                ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
                if (calendarFrameValidity != null) {
                    return calendarFrameValidity;
                } else {
                    ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
                    if (compositeFrameValidity != null) {
                        return compositeFrameValidity;
                    }
                }
            }
        }

        return null;
    }

    private boolean isWithinValidRange(OffsetDateTime dateOfOperation, ValidBetween validBetween) {
        return !dateOfOperation.isBefore(validBetween.getFromDate()) && !dateOfOperation.isAfter(validBetween.getToDate());
    }

    private void addTimetableId(Context context, String objectId, String timetableId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(TIMETABLE_ID, timetableId);
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
