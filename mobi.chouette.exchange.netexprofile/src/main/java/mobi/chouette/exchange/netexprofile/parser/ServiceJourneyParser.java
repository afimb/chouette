package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.sql.Time;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;

@Log4j
public class ServiceJourneyParser extends NetexParser implements Parser, Constant {

    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context calendarContext = (Context) parsingContext.get(ServiceCalendarParser.LOCAL_CONTEXT);

        JourneysInFrame_RelStructure journeyStruct = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        for (Journey_VersionStructure serviceJourneyStruct : journeyStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney()) {
            ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
            VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, serviceJourney.getId());

            vehicleJourney.setObjectVersion(NetexParserUtils.getVersion(serviceJourney));

            // TODO check out if this gives the problem with journey names in digitransit (OSL-BGO instead of SK4887)
            if (serviceJourney.getName() != null) {
                vehicleJourney.setPublishedJourneyName(serviceJourney.getName().getValue());
            }
            vehicleJourney.setPublishedJourneyIdentifier(serviceJourney.getPublicCode());

            for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : serviceJourney.getDayTypes().getDayTypeRef()) {
                String dayTypeIdRef = dayTypeRefStructElement.getValue().getRef();
                Context calendarObjectContext = (Context) calendarContext.get(dayTypeIdRef);
                String timetableId = (String) calendarObjectContext.get(ServiceCalendarParser.TIMETABLE_ID);

                if (timetableId != null && !timetableId.isEmpty()) {
                    Timetable timetable = ObjectFactory.getTimetable(referential, timetableId);
                    timetable.addVehicleJourney(vehicleJourney);
                }
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
            ZoneOffset zoneOffset = departureTime.getOffset();

            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localDepartureTime = NetexParserUtils.convertToSqlTime(departureTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setDepartureTime(localDepartureTime);
            }

            // TODO: add support for zone offsets other than utc here  (like +02:00, -05:30, etc...)
        }
        if (arrivalTime != null) {
            ZoneOffset zoneOffset = arrivalTime.getOffset();

            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localArrivalTime = NetexParserUtils.convertToSqlTime(arrivalTime, NetexParserUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setArrivalTime(localArrivalTime);
            }

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
