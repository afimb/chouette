package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Log4j
public class ServiceJourneyParser extends NetexParser implements Parser, Constant {

    private static final ZoneId LOCAL_ZONE_ID = ZoneId.systemDefault();

    @Override
    public void parse(Context context) throws Exception {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context mainDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

        Referential referential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        JourneysInFrame_RelStructure journeyStructs = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<Journey_VersionStructure> serviceJourneys = journeyStructs.getDatedServiceJourneyOrDeadRunOrServiceJourney();

        for (Journey_VersionStructure journeyStruct : serviceJourneys) {
            ServiceJourney serviceJourney = (ServiceJourney) journeyStruct;

            VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, serviceJourney.getId());
            vehicleJourney.setObjectVersion(NetexParserUtils.getVersion(serviceJourney));

            // TODO check out if this gives the problem with journey names in digitransit (OSL-BGO instead of SK4887)
            if (serviceJourney.getName() != null) {
                vehicleJourney.setPublishedJourneyName(serviceJourney.getName().getValue());
            }
            vehicleJourney.setPublishedJourneyIdentifier(serviceJourney.getPublicCode());

            Context journeyObjectContext = (Context) mainDeliveryContext.get(serviceJourney.getId());
            String timetableId = (String) journeyObjectContext.get(PublicationDeliveryParser.TIMETABLE_ID);

            Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
            timetable.addVehicleJourney(vehicleJourney);

            if (!timetable.isFilled()) {
                for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : serviceJourney.getDayTypes().getDayTypeRef()) {
                    String dayTypeIdRef = dayTypeRefStructElement.getValue().getRef();
                    DayType dayType = NetexObjectUtil.getDayType(netexReferential, dayTypeIdRef);
                    if (dayType != null) {
                        parseDayType(context, netexReferential, dayType, timetable);
                    }
                }
                timetable.setFilled(true);
            }

            String journeyPatternIdRef = null;
            if (serviceJourney.getJourneyPatternRef() != null) {
                JourneyPatternRefStructure patternRefStruct = serviceJourney.getJourneyPatternRef().getValue();
                journeyPatternIdRef = patternRefStruct.getRef();
            }

            mobi.chouette.model.JourneyPattern journeyPattern = ObjectFactory.getJourneyPattern(referential, journeyPatternIdRef);
            vehicleJourney.setJourneyPattern(journeyPattern);

            if (serviceJourney.getOperatorRef() != null) {
                String operatorIdRef = serviceJourney.getOperatorRef().getRef();
                Company company = ObjectFactory.getCompany(referential, operatorIdRef);
                vehicleJourney.setCompany(company);
            } else if (serviceJourney.getLineRef() != null) {
                String lineIdRef = serviceJourney.getLineRef().getValue().getRef();
                Company company = ObjectFactory.getLine(referential, lineIdRef).getCompany();
                vehicleJourney.setCompany(company);
            } else {
                Company company = journeyPattern.getRoute().getLine().getCompany();
                vehicleJourney.setCompany(company);
            }

            if (serviceJourney.getRouteRef() != null) {
                mobi.chouette.model.Route route = ObjectFactory.getRoute(referential, serviceJourney.getRouteRef().getRef());
                vehicleJourney.setRoute(route);
            } else {
                mobi.chouette.model.Route route = journeyPattern.getRoute();
                vehicleJourney.setRoute(route);
            }

            parseTimetabledPassingTimes(context, referential, serviceJourney, vehicleJourney);
            vehicleJourney.setFilled(true);
        }
    }

    private void parseDayType(Context context, NetexReferential netexReferential, DayType dayType, Timetable timetable) throws Exception {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context calendarContext = (Context) parsingContext.get(ServiceCalendarParser.LOCAL_CONTEXT);
        Context calendarObjectContext = (Context) calendarContext.get(dayType.getId());

        ValidBetween validBetween = (ValidBetween) calendarObjectContext.get(ServiceCalendarParser.VALID_BETWEEN);

        if (timetable.getObjectVersion() == null) {
            timetable.setObjectVersion(NetexParserUtils.getVersion(dayType));
        }

        DayTypeAssignment dayTypeAssignment = null;
        if (netexReferential.getDayTypeAssignments().containsKey(dayType.getId())) {
            dayTypeAssignment = netexReferential.getDayTypeAssignments().get(dayType.getId());
        }

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
            if (dayTypeAssignment != null) {

                if (dayTypeAssignment.getOperatingPeriodRef() != null) {
                    String operatingPeriodIdRef = dayTypeAssignment.getOperatingPeriodRef().getRef();
                    OperatingPeriod operatingPeriod = NetexObjectUtil.getOperatingPeriod(netexReferential, operatingPeriodIdRef);

                    Date startDate;
                    Date endDate;

                    if (operatingPeriod.getFromOperatingDayRef() != null) {
                        OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getFromOperatingDayRef().getRef());
                        startDate = ParserUtils.getSQLDate(operatingDay.getCalendarDate().toString());
                    } else {
                        startDate = ParserUtils.getSQLDate(operatingPeriod.getFromDate().toString());
                    }
                    if (operatingPeriod.getToOperatingDayRef() != null) {
                        OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingPeriod.getToOperatingDayRef().getRef());
                        endDate = ParserUtils.getSQLDate(operatingDay.getCalendarDate().toString());
                    } else {
                        endDate = ParserUtils.getSQLDate(operatingPeriod.getToDate().toString());
                    }

                    timetable.addPeriod(new Period(startDate, endDate));

                } else {
                    OffsetDateTime fromDate = validBetween.getFromDate();
                    OffsetDateTime toDate = validBetween.getToDate();

                    if (fromDate != null && toDate != null && fromDate.isBefore(toDate)) {
                        Date sqlFromDate = Date.valueOf(fromDate.toLocalDate());
                        Date sqlToDate = Date.valueOf(toDate.toLocalDate());
                        timetable.addPeriod(new Period(sqlFromDate, sqlToDate));
                    } else {
                        log.error("Validity condition is not valid");
                        throw new RuntimeException("Validity condition is not valid");
                    }
                }
            }

        } else {

            if (dayTypeAssignment != null) {
                if (dayTypeAssignment.getOperatingDayRef() != null) {
                    String operatingDayIdRef = dayTypeAssignment.getOperatingDayRef().getRef();
                    OperatingDay operatingDay = NetexObjectUtil.getOperatingDay(netexReferential, operatingDayIdRef);

                    if (operatingDay.getCalendarDate() != null && isWithinValidRange(operatingDay.getCalendarDate(), validBetween)) {
                        boolean included = dayTypeAssignment.isIsAvailable() != null ? dayTypeAssignment.isIsAvailable() : Boolean.TRUE;
                        timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(operatingDay.getCalendarDate().toLocalDate()), included));
                    }

                } else {
                    if (dayTypeAssignment.getDate() != null && isWithinValidRange(dayTypeAssignment.getDate(), validBetween)) {
                        boolean included = dayTypeAssignment.isIsAvailable() != null ? dayTypeAssignment.isIsAvailable() : Boolean.TRUE;
                        timetable.addCalendarDay(new CalendarDay(java.sql.Date.valueOf(dayTypeAssignment.getDate().toLocalDate()), included));
                    }
                }
            }

        }
    }

    private boolean isWithinValidRange(OffsetDateTime dateOfOperation, ValidBetween validBetween) {
        return !dateOfOperation.isBefore(validBetween.getFromDate()) && !dateOfOperation.isAfter(validBetween.getToDate());
    }

    private void parseTimetabledPassingTimes(Context context, Referential referential, ServiceJourney serviceJourney, VehicleJourney vehicleJourney) {
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context journeyPatternContext = (Context) parsingContext.get(JourneyPatternParser.LOCAL_CONTEXT);

        for (TimetabledPassingTime passingTime : serviceJourney.getPassingTimes().getTimetabledPassingTime()) {
            VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
            String pointInJourneyPatternId = passingTime.getPointInJourneyPatternRef().getValue().getRef();
            StopPointInJourneyPattern stopPointInJourneyPattern = NetexObjectUtil.getStopPointInJourneyPattern(netexReferential, pointInJourneyPatternId);

            Context pointInPatternContext = (Context) journeyPatternContext.get(pointInJourneyPatternId);
            String stopPointId = (String) pointInPatternContext.get(JourneyPatternParser.STOP_POINT_ID);

            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointId);
            vehicleJourneyAtStop.setStopPoint(stopPoint);

            // Default = board and alight
            vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

            Boolean forBoarding = stopPointInJourneyPattern.isForBoarding();
            Boolean forAlighting = stopPointInJourneyPattern.isForAlighting();

            if (forBoarding == null && forAlighting != null && !forAlighting) {
                vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardOnly);
            }
            if (forAlighting == null && forBoarding != null && !forBoarding) {
                vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.AlightOnly);
            }

            parsePassingTimes(passingTime, vehicleJourneyAtStop);
            vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);
        }

        vehicleJourney.getVehicleJourneyAtStops().sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));
    }

    // TODO add support for other time zones and zone offsets, for now only handling UTC
    private void parsePassingTimes(TimetabledPassingTime timetabledPassingTime, VehicleJourneyAtStop vehicleJourneyAtStop) {
        OffsetTime departureTime = timetabledPassingTime.getDepartureTime();
        OffsetTime arrivalTime = timetabledPassingTime.getArrivalTime();

        if (departureTime != null) {

            // TODO rewrite when time handlings is specified
            vehicleJourneyAtStop.setDepartureTime(NetexParserUtils.convertToSqlTime(departureTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID)));


            //
//
//        	ZoneOffset zoneOffset = departureTime.getOffset();
//
//            if (zoneOffset.equals(ZoneOffset.UTC)) {
//                Time localDepartureTime = NetexParserUtils.convertToSqlTime(departureTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID));
//                vehicleJourneyAtStop.setDepartureTime(localDepartureTime);
//            } else {
//
//            }

            // TODO: add support for zone offsets other than utc here  (like +02:00, -05:30, etc...)
        }
        if (arrivalTime != null) {
            vehicleJourneyAtStop.setArrivalTime(NetexParserUtils.convertToSqlTime(arrivalTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID)));
//            ZoneOffset zoneOffset = arrivalTime.getOffset();
//
//            if (zoneOffset.equals(ZoneOffset.UTC)) {
//                Time localArrivalTime = NetexParserUtils.convertToSqlTime(arrivalTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID));
//                vehicleJourneyAtStop.setArrivalTime(localArrivalTime);
//            }

            // TODO: add support for zone offsets other than utc here (like +02:00, -05:30, etc...)

        } else {
            // TODO find out if necessary
            // vehicleJourneyAtStop.setArrivalTime(new Time(vehicleJourneyAtStop.getDepartureTime().getTime()));
        }
    }

    static {
        ParserFactory.register(ServiceJourneyParser.class.getName(), new ParserFactory() {
            private ServiceJourneyParser instance = new ServiceJourneyParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
