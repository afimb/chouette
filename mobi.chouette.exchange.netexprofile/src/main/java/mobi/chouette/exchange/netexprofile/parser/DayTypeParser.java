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
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.List;

@Log4j
public class DayTypeParser extends NetexParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);
        Context dayTypeAssignmentContext = (Context) parsingContext.get(DayTypeAssignmentParser.LOCAL_CONTEXT);
        DayTypesInFrame_RelStructure dayTypeStruct = (DayTypesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        // parse validity conditions

        Period period = new Period();

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
        if (isPeriodEmpty(period)) {
            throw new RuntimeException("No validity conditions available");
        }

        List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeElements = dayTypeStruct.getDayType_();

        for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : dayTypeElements) {
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
                Context dayTypeAssignmentObjectContext = (Context) dayTypeAssignmentContext.get(dayType.getId());

                if (dayTypeAssignmentObjectContext != null) {
                    LocalDate dateOfOperation = (LocalDate) dayTypeAssignmentObjectContext.get(DayTypeAssignmentParser.DATE_OF_OPERATION);
                    timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dateOfOperation), true));
                } else {
                    throw new RuntimeException("No valid day types found");
                }
            }

            timetable.addPeriod(period);
            timetable.setFilled(true);
        }
    }

    static {
        ParserFactory.register(DayTypeParser.class.getName(), new ParserFactory() {
            private DayTypeParser instance = new DayTypeParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
