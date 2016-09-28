package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.*;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.sql.Date;
import java.sql.Time;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Log4j
public class TimetableParser implements Parser, Constant {

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);

        TimetableFrame timetableFrame = (TimetableFrame) context.get(NETEX_LINE_DATA_CONTEXT);

        ValidityConditions_RelStructure validityConditions = timetableFrame.getValidityConditions();
        if (validityConditions != null) {
            parseValidityConditions(referential, validityConditions);
        }
        JourneysInFrame_RelStructure vehicleJourneysStruct = timetableFrame.getVehicleJourneys();
        if (vehicleJourneysStruct != null) {
            parseVehicleJourneys(context, referential, lineData, vehicleJourneysStruct);
        }
    }

    private void parseValidityConditions(Referential referential, ValidityConditions_RelStructure validityConditions)  throws Exception {
        List<Object> availabilityConditionElements = validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_();
        // should iterate all availability conditions, for now only retrieving first occurrence
/*
            for (JAXBElement<AvailabilityCondition> availabilityConditionElement : availabilityConditionElements) {
                AvailabilityCondition value = availabilityConditionElement.getValue();
            }
*/
        // TODO: add more sophisticated check on zoneids and zoneoffsets here
        // how to connect the period to the right timetable instance? we can only get timetables by day type id
        if (availabilityConditionElements != null && availabilityConditionElements.size() > 0) {
            AvailabilityCondition availabilityCondition = ((JAXBElement<AvailabilityCondition>) availabilityConditionElements.get(0)).getValue();
            OffsetDateTime fromDate = availabilityCondition.getFromDate();
            OffsetDateTime toDate = availabilityCondition.getToDate();
            Date startOfPeriod = ParserUtils.getSQLDate(fromDate.toString());
            Date endOfPeriod = ParserUtils.getSQLDate(toDate.toString());
            Period period = new Period(startOfPeriod, endOfPeriod);
            //timetable.addPeriod(period);
        }
    }

    private void parseVehicleJourneys(Context context, Referential referential, PublicationDeliveryStructure lineData, JourneysInFrame_RelStructure vehicleJourneysStruct) throws Exception {
        List<Journey_VersionStructure> serviceJourneyStructs = vehicleJourneysStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();
        for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {
            ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
            parseServiceJourney(context, referential, lineData, serviceJourney);
        }
    }

    private void parseServiceJourney(Context context, Referential referential, PublicationDeliveryStructure lineData, ServiceJourney serviceJourney) throws Exception {
        VehicleJourney vehicleJourney = ObjectFactory.getVehicleJourney(referential, serviceJourney.getId());
        JourneyPattern journeyPattern = null;

        OffsetTime departureTime = serviceJourney.getDepartureTime();
        DayTypeRefs_RelStructure dayTypesStruct = serviceJourney.getDayTypes();
        if (dayTypesStruct != null) {
            parseDayTypes(referential, vehicleJourney, dayTypesStruct);
        }

        JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRefStructElement = serviceJourney.getJourneyPatternRef();
        if (journeyPatternRefStructElement != null) {
            JourneyPatternRefStructure journeyPatternRefStructure = journeyPatternRefStructElement.getValue();
            String journeyPatternId = journeyPatternRefStructure.getRef();
            journeyPattern = ObjectFactory.getJourneyPattern(referential, journeyPatternId);
            vehicleJourney.setJourneyPattern(journeyPattern);
        }

        // String publicCode = serviceJourney.getPublicCode(); // how to handle in chouette model?

        OperatorRefStructure operatorRefStruct = serviceJourney.getOperatorRef();
        if (operatorRefStruct != null) {
            String operatorId = operatorRefStruct.getRef();
            Company company = ObjectFactory.getCompany(referential, operatorId);
            vehicleJourney.setCompany(company);
        }

        // We actually must have a RouteRef in each ServiceJourney instance, if not we get a np in VehicleJourneyUpdater#update:208
        // TODO: probably need schema change too, as RouteRef is not a valid element of a ServiceJourney in the NO-profile
        RouteRefStructure routeRefStruct = serviceJourney.getRouteRef();
        if (routeRefStruct != null) {
            String routeId = routeRefStruct.getRef();
            Route route = ObjectFactory.getRoute(referential, routeId);
            vehicleJourney.setRoute(route);
        } else {
            // TODO: remove temp else block, when RouteRef supported in NO-profile of netex
            JAXBElement<? extends LineRefStructure> lineRefStructElement = serviceJourney.getLineRef();
            if (lineRefStructElement != null) {
                LineRefStructure lineRefStructure = lineRefStructElement.getValue();
                String lineId = lineRefStructure.getRef();
                Line line = ObjectFactory.getLine(referential, lineId);
                Route route = line.getRoutes().get(0);
                vehicleJourney.setRoute(route);
            }
        }

        // TODO: must have a RouteRef in ServiceJourney instead of LineRef, chouette model only supports references to Routes
        JAXBElement<? extends LineRefStructure> lineRefStructElement = serviceJourney.getLineRef();
        if (lineRefStructElement != null) {
            LineRefStructure lineRefStructure = lineRefStructElement.getValue();
            String lineId = lineRefStructure.getRef();
            Line line = ObjectFactory.getLine(referential, lineId);
            //vehicleJourney.setLIne(line);
        }

        TimetabledPassingTimes_RelStructure timetabledPassingTimesStruct = serviceJourney.getPassingTimes();
        if (timetabledPassingTimesStruct != null) {
            parsePassingTimes(context, referential, vehicleJourney, journeyPattern, timetabledPassingTimesStruct);
        }
        vehicleJourney.setFilled(true);
    }

    private void parseDayTypes(Referential referential, VehicleJourney vehicleJourney, DayTypeRefs_RelStructure dayTypesStruct) throws Exception {
        List<JAXBElement<? extends DayTypeRefStructure>> dayTypeRefElements = dayTypesStruct.getDayTypeRef();

        for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefElement : dayTypeRefElements) {
            DayTypeRefStructure dayTypeRefStructure = dayTypeRefElement.getValue();
            String dayTypeRef = dayTypeRefStructure.getRef();
            Timetable timetable = referential.getTimetables().get(dayTypeRef);

            if (timetable != null) {
                vehicleJourney.getTimetables().add(timetable);
            }
        }
    }

    private void parsePassingTimes(Context context, Referential referential, VehicleJourney vehicleJourney, JourneyPattern journeyPattern, TimetabledPassingTimes_RelStructure timetabledPassingTimesStruct) throws Exception {
        List<TimetabledPassingTime> timetabledPassingTimes = timetabledPassingTimesStruct.getTimetabledPassingTime();
        if (timetabledPassingTimes != null && timetabledPassingTimes.size() > 0) {
            for (TimetabledPassingTime timetabledPassingTime : timetabledPassingTimes) {
                parseTimetabledPassingTime(context, referential, vehicleJourney, journeyPattern, timetabledPassingTime);
            }
        }
    }

    private void parseTimetabledPassingTime(Context context, Referential referential, VehicleJourney vehicleJourney,
            JourneyPattern journeyPattern, TimetabledPassingTime timetabledPassingTime) throws Exception {
        VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
        vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

        JAXBElement<? extends PointInJourneyPatternRefStructure> pointInJourneyPatternRefStructElement =
                timetabledPassingTime.getPointInJourneyPatternRef();
        if (pointInJourneyPatternRefStructElement != null) {
            PointInJourneyPatternRefStructure pointInJourneyPatternRefStruct = pointInJourneyPatternRefStructElement.getValue();
            String pointInJourneyPatternId = pointInJourneyPatternRefStruct.getRef();
            Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);
            StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) cachedNetexData.get(pointInJourneyPatternId);
            if (stopPointInJourneyPattern != null) {
                JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRefElement = stopPointInJourneyPattern.getScheduledStopPointRef();
                if (scheduledStopPointRefElement != null) {
                    ScheduledStopPointRefStructure scheduledStopPointRefStruct = scheduledStopPointRefElement.getValue();
                    if (scheduledStopPointRefStruct != null) {
                        String scheduledStopPointRefValue= scheduledStopPointRefStruct.getRef();
                        if (StringUtils.isNotEmpty(scheduledStopPointRefValue)) {
                            List<StopPoint> stopPoints = journeyPattern.getStopPoints();
                            for (StopPoint stopPoint : stopPoints) {
                                if (stopPoint.getObjectId().equals(scheduledStopPointRefValue)) {
                                    vehicleJourneyAtStop.setStopPoint(stopPoint);
                                }
                            }
                        }
                    }
                }
            }
        }
        vehicleJourney.getVehicleJourneyAtStops().add(vehicleJourneyAtStop);

        // TODO: The challenge here is to treat all arrival and departure times with the correct offsets, because daylightsavings (winter/summer time)
        // CET time (winter) 2009-12-31T16:00:00 corresponds to 2009-12-31T17:00:00 in norwegian local time, +01:00
        // CEST TIME (summer) 2009-06-23T16:00:00 corresponds to 2009-12-31T18:00:00 in norwegian local time, +02:00
        // We are fetching aviation data for 4 months, which means arrival and departure times can be different for the same flight in different daylightsavings
        // consider doing the date conversion on integration part instead (extime), and set the correct localtime before converting to netex
        // Oslo is one hour ahead of Greenwich/UTC in winter and two hours ahead in summer.
        // The ZoneId instance for Oslo will reference two ZoneOffset instances - a +01:00 instance for winter, and a +02:00 instance for summer.

        // following is a temporary solution for handling incoming data in UTC
        OffsetTime arrivalTime = timetabledPassingTime.getArrivalTime();
        if (arrivalTime != null) {
            ZoneOffset zoneOffset = arrivalTime.getOffset();
            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localArrivalTime = NetexUtils.convertToSqlTime(arrivalTime, NetexUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setArrivalTime(localArrivalTime);
            }
            // TODO: add support for zone offsets other than utc here (like +02:00, -05:30, etc...)
        }
        OffsetTime departureTime = timetabledPassingTime.getDepartureTime();
        if (departureTime != null) {
            ZoneOffset zoneOffset = departureTime.getOffset();
            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localDepartureTime = NetexUtils.convertToSqlTime(departureTime, NetexUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setDepartureTime(localDepartureTime);
            }
            // TODO: add support for zone offsets other than utc here  (like +02:00, -05:30, etc...)
        }
    }

    static {
        ParserFactory.register(TimetableParser.class.getName(), new ParserFactory() {
            private TimetableParser instance = new TimetableParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
