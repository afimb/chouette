package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Log4j
public class DayTypeParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "DayTypeContext";
    public static final String VEHICLE_JOURNEY_ID = "vehicleJourneyId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        // TODO implement

    }

    public void addVehicleJourneyIdRef(Context context, String objectId, String vehicleJourneyId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(VEHICLE_JOURNEY_ID, vehicleJourneyId);
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context localContext = (Context) parsingContext.get(LOCAL_CONTEXT);

        // TODO consider the best way to get day type instances, from referential (all at once), or per frame
        //DayTypesInFrame_RelStructure dayTypesRelStruct = (DayTypesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        //List<JAXBElement<? extends DataManagedObjectStructure>> dayTypeElements = dayTypesRelStruct.getDayType_();

        // TODO add better support for specific periods
        // temporary fix to support timetable periods
        List<TimetableFrame> timetableFrames = new ArrayList<>(netexReferential.getTimetableFrames().values());
        TimetableFrame mainTimetableFrame = timetableFrames.get(0);

        ValidityConditions_RelStructure validityConditions = mainTimetableFrame.getValidityConditions();
        List<Object> availabilityConditionElements = validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_();
        @SuppressWarnings("unchecked") AvailabilityCondition availabilityCondition =
                ((JAXBElement<AvailabilityCondition>) availabilityConditionElements.get(0)).getValue();

        OffsetDateTime fromDate = availabilityCondition.getFromDate();
        OffsetDateTime toDate = availabilityCondition.getToDate();

        Date startOfPeriod = ParserUtils.getSQLDate(fromDate.toString());
        Date endOfPeriod = ParserUtils.getSQLDate(toDate.toString());
        Period period = new Period(startOfPeriod, endOfPeriod);

        Collection<DayType> dayTypes = netexReferential.getDayTypes().values();

        for (DayType dayType : dayTypes) {
            String netexDayTypeId = dayType.getId();
            Context objectContext = (Context) localContext.get(netexDayTypeId);

            String chouetteTimetableId = dayType.getId(); // TODO generate through id generator/creator
            Timetable timetable = ObjectFactory.getTimetable(chouetteReferential, chouetteTimetableId);

            String netexDayTypeName = dayType.getName().getValue();

            // TODO should probably set some standard name/comment here, see regtopp NamingUtil#setDefaultName
            timetable.setComment(netexDayTypeName);

            PropertiesOfDay_RelStructure propertiesOfDayStruct = dayType.getProperties();
            List<PropertyOfDay> propertyOfDayList = propertiesOfDayStruct.getPropertyOfDay();

            for (PropertyOfDay propertyOfDay : propertyOfDayList) {
                List<DayOfWeekEnumeration> daysOfWeek = propertyOfDay.getDaysOfWeek();

                // TODO: consider processing of multiple DaysOfWeek patterns

                String[] weekDays = StringUtils.splitByWholeSeparator(daysOfWeek.get(0).value(), null);
                List<String> weekDaysList = Arrays.asList(weekDays);
                timetable.setDayTypes(NetexUtils.getDayTypes(weekDaysList));

                // TODO: add support for days of month, year, event, etc...
                // timetable.addCalendarDay(value);
            }

            timetable.setStartOfPeriod(startOfPeriod);
            timetable.setEndOfPeriod(endOfPeriod);
            timetable.addPeriod(period);
            // timetable.getPeriods().add(period);

            String chouetteVehicleJourneyId = (String) objectContext.get(VEHICLE_JOURNEY_ID);
            VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(chouetteReferential, chouetteVehicleJourneyId);
            timetable.addVehicleJourney(vehicleJourney);

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
