package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.Arrays;
import java.util.List;

@Log4j
public class DayTypeParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        DayTypesInFrame_RelStructure contextData = (DayTypesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeStructElements = contextData.getDayType_();
        for (JAXBElement<? extends DataManagedObjectStructure> dayTypeStructElement : dayTypeStructElements) {
            DayType dayType = (DayType) dayTypeStructElement.getValue();
            parseDayType(context, referential, dayType);
        }
    }

    private void parseDayType(Context context, Referential referential, DayType dayType) {
        // TODO: generate a more sophisticated chouette id here...
        Timetable timetable = ObjectFactory.getTimetable(referential, dayType.getId());
        MultilingualString dayTypeName = dayType.getName();
        if (dayTypeName != null) {
            String dayTypeNameValue = dayTypeName.getValue();
            timetable.setComment(dayTypeNameValue);
        }
        PropertiesOfDay_RelStructure propertiesOfDayStruct = dayType.getProperties();
        List<PropertyOfDay> propertyOfDayList = propertiesOfDayStruct.getPropertyOfDay();
        for (PropertyOfDay propertyOfDay : propertyOfDayList) {
            List<DayOfWeekEnumeration> daysOfWeek = propertyOfDay.getDaysOfWeek();
            if (daysOfWeek != null && daysOfWeek.size() > 0) {
                // TODO: consider processing of multiple DaysOfWeek patterns
                String[] weekDays = StringUtils.splitByWholeSeparator(daysOfWeek.get(0).value(), null);
                List<String> weekDaysList = Arrays.asList(weekDays);
                timetable.setDayTypes(NetexUtils.getDayTypes(weekDaysList));
            }
            // TODO: add support for days of month, year, event, etc...
        }
        timetable.setFilled(true);
        referential.getTimetables().put(timetable.getObjectId(), timetable);
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
