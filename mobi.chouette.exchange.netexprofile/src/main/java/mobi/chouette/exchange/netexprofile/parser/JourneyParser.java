package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.*;
import mobi.chouette.model.Route;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.JourneyPattern;

import javax.xml.bind.JAXBElement;
import java.sql.Time;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class JourneyParser extends AbstractParser {

    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

    private Map<String, StopPointInJourneyPattern> stopPointInJourneyPatternMap;

    @Override
    public void initReferentials(Context context) throws Exception {

    }

    @Override
    public void parse(Context context) throws Exception {
        RelationshipStructure relationshipStruct = (RelationshipStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        if (relationshipStruct instanceof JourneyPatternsInFrame_RelStructure) {
            stopPointInJourneyPatternMap = new HashMap<>();
            JourneyPatternsInFrame_RelStructure contextData = (JourneyPatternsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
            List<JAXBElement<?>> journeyPatternElements = contextData.getJourneyPattern_OrJourneyPatternView();

            for (JAXBElement<?> journeyPatternElement : journeyPatternElements) {
                org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = (org.rutebanken.netex.model.JourneyPattern) journeyPatternElement.getValue();
                parseJourneyPattern(context, netexJourneyPattern);
            }
        } else if (relationshipStruct instanceof JourneysInFrame_RelStructure) {
            JourneysInFrame_RelStructure journeysInFrameRelStruct = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
            List<Journey_VersionStructure> serviceJourneyStructs = journeysInFrameRelStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();

            for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {

                // TODO handle all types of netex journeys, for now only parsing ServiceJourney instances
                ServiceJourney serviceJourney = (ServiceJourney) serviceJourneyStruct;
                parseServiceJourney(context, serviceJourney);
            }
        }
    }

    private void parseJourneyPattern(Context context, JourneyPattern netexJourneyPattern) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        mobi.chouette.model.JourneyPattern chouetteJourneyPattern = mobi.chouette.model.util.ObjectFactory.getJourneyPattern(referential, netexJourneyPattern.getId());

        Integer version = Integer.valueOf(netexJourneyPattern.getVersion());
        chouetteJourneyPattern.setObjectVersion(version != null ? version : 0);

        if (netexJourneyPattern.getName() != null) {
            chouetteJourneyPattern.setName(netexJourneyPattern.getName().getValue());
        }

        String routeIdRef = netexJourneyPattern.getRouteRef().getRef();
        mobi.chouette.model.Route route = mobi.chouette.model.util.ObjectFactory.getRoute(referential, routeIdRef);
        chouetteJourneyPattern.setRoute(route);
        chouetteJourneyPattern.setPublishedName(route.getPublishedName());

        if (netexJourneyPattern.getPrivateCode() != null) {
            chouetteJourneyPattern.setRegistrationNumber(netexJourneyPattern.getPrivateCode().getValue());
        }

        parsePointsInSequence(context, netexJourneyPattern, chouetteJourneyPattern, route);

        // TODO: add all remaining optional elements, for now we only support RouteRef and pointsInSequence.
        //      See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-JourneyPattern

        chouetteJourneyPattern.setFilled(true);
    }

    private void parsePointsInSequence(Context context, JourneyPattern netexJourneyPattern, mobi.chouette.model.JourneyPattern chouetteJourneyPattern, Route route) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();

        List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct
                .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

        for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
            StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;
            String stopPointIdRef = stopPointInJourneyPattern.getScheduledStopPointRef().getValue().getRef();
            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointIdRef);
            stopPoint.setRoute(route);
            chouetteJourneyPattern.addStopPoint(stopPoint);
            stopPointInJourneyPatternMap.put(stopPointInJourneyPattern.getId(), stopPointInJourneyPattern);
        }

        List<StopPoint> addedStopPoints = chouetteJourneyPattern.getStopPoints();
        //chouetteJourneyPattern.getStopPoints().sort(Comparator.comparing(StopPoint::getPosition));
        chouetteJourneyPattern.setDepartureStopPoint(addedStopPoints.get(0));
        chouetteJourneyPattern.setArrivalStopPoint(addedStopPoints.get(addedStopPoints.size() - 1));

/*
        for (StopPoint addedStopPoint : addedStopPoints) {
            StopArea containedInStopArea = addedStopPoint.getContainedInStopArea();

            if (!referential.getStopAreas().containsKey(containedInStopArea.getObjectId())) {
                referential.getStopAreas().put(containedInStopArea.getObjectId(), containedInStopArea);
            }
        }
*/
    }

    private void parseServiceJourney(Context context, ServiceJourney serviceJourney) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        VehicleJourney vehicleJourney = mobi.chouette.model.util.ObjectFactory.getVehicleJourney(referential, serviceJourney.getId());

        Integer version = Integer.valueOf(serviceJourney.getVersion());
        vehicleJourney.setObjectVersion(version != null ? version : 0);

        if (serviceJourney.getName() != null) {
            vehicleJourney.setPublishedJourneyName(serviceJourney.getName().getValue());
        }
        if (serviceJourney.getShortName() != null) {
            vehicleJourney.setPublishedJourneyIdentifier(serviceJourney.getShortName().getValue());
        }
        if (serviceJourney.getPublicCode() != null) {
            vehicleJourney.setComment(serviceJourney.getPublicCode());
        }

        // parse day type refs
        List<JAXBElement<? extends DayTypeRefStructure>> dayTypeRefStructElements = serviceJourney.getDayTypes().getDayTypeRef();
        for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : dayTypeRefStructElements) {
            String dayTypeIdRef = dayTypeRefStructElement.getValue().getRef();
            Timetable timetable = referential.getTimetables().get(dayTypeIdRef);

            if (timetable != null) {
                // vehicleJourney.getTimetables().add(timetable);
                timetable.addVehicleJourney(vehicleJourney);
            }
        }

        // parse journey pattern
        // we must process journey patterns first hand, because we may be forced to get both line and route from journey pattern reference
        String journeyPatternIdRef = serviceJourney.getJourneyPatternRef().getValue().getRef();
        mobi.chouette.model.JourneyPattern journeyPattern = mobi.chouette.model.util.ObjectFactory.getJourneyPattern(referential, journeyPatternIdRef);
        vehicleJourney.setJourneyPattern(journeyPattern);

        // parse company
        Company company;
        if (serviceJourney.getOperatorRef() != null) {
            company = mobi.chouette.model.util.ObjectFactory.getCompany(referential, serviceJourney.getOperatorRef().getRef());
        } else if (serviceJourney.getLineRef() != null) {
            String lineIdRef = serviceJourney.getLineRef().getValue().getRef();
            company = mobi.chouette.model.util.ObjectFactory.getLine(referential, lineIdRef).getCompany();
        } else {
            company = journeyPattern.getRoute().getLine().getCompany();
        }
        vehicleJourney.setCompany(company);

        // parse route reference
        Route route;
        if (serviceJourney.getRouteRef() != null) {
            route = mobi.chouette.model.util.ObjectFactory.getRoute(referential, serviceJourney.getRouteRef().getRef());
        } else {
            route = journeyPattern.getRoute();
        }
        vehicleJourney.setRoute(route);

        parseTimetabledPassingTimes(context, serviceJourney, vehicleJourney, journeyPattern);
        vehicleJourney.setFilled(true);
    }

    private void parseTimetabledPassingTimes(Context context, ServiceJourney serviceJourney, VehicleJourney vehicleJourney, mobi.chouette.model.JourneyPattern journeyPattern) {
        Referential referential = (Referential) context.get(REFERENTIAL);
        List<TimetabledPassingTime> timetabledPassingTimes = serviceJourney.getPassingTimes().getTimetabledPassingTime();

        for (TimetabledPassingTime timetabledPassingTime : timetabledPassingTimes) {
            VehicleJourneyAtStop vehicleJourneyAtStop = mobi.chouette.model.util.ObjectFactory.getVehicleJourneyAtStop();
            vehicleJourneyAtStop.setVehicleJourney(vehicleJourney);

            String pointInJourneyPatternId = timetabledPassingTime.getPointInJourneyPatternRef().getValue().getRef();
            StopPointInJourneyPattern stopPointInJourneyPattern = stopPointInJourneyPatternMap.get(pointInJourneyPatternId);

            // Default = board and alight
            vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

            if (stopPointInJourneyPattern.getId().equalsIgnoreCase(pointInJourneyPatternId)) {
                String stopPointIdRef = stopPointInJourneyPattern.getScheduledStopPointRef().getValue().getRef();
                StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointIdRef);
                vehicleJourneyAtStop.setStopPoint(stopPoint);

                Boolean forBoarding = stopPointInJourneyPattern.isForBoarding();
                Boolean forAlighting = stopPointInJourneyPattern.isForAlighting();

                if (forBoarding == null && forAlighting != null && !forAlighting) {
                    vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardOnly);
                }
                if (forAlighting == null && forBoarding != null && !forBoarding) {
                    vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.AlightOnly);
                }
            }

            parsePassingTimes(context, timetabledPassingTime, vehicleJourneyAtStop);
        }

        // sort the list of added vechicle journey at stops for the current vehicle journey
        vehicleJourney.getVehicleJourneyAtStops().sort((o1, o2) -> {
            StopPoint p1 = o1.getStopPoint();
            StopPoint p2 = o2.getStopPoint();
            if (p1 != null && p2 != null) {
                int pos1 = p1.getPosition() == null ? 0 : p1.getPosition();
                int pos2 = p2.getPosition() == null ? 0 : p2.getPosition();
                return pos1 - pos2;
            }
            return 0;
        });
    }

    // TODO add support for other time zones and zone offsets, for now only handling UTC
    private void parsePassingTimes(Context context, TimetabledPassingTime timetabledPassingTime, VehicleJourneyAtStop vehicleJourneyAtStop) {
        OffsetTime departureTime = timetabledPassingTime.getDepartureTime();
        OffsetTime arrivalTime = timetabledPassingTime.getArrivalTime();

        if (departureTime != null) {
            ZoneOffset zoneOffset = departureTime.getOffset();

            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localDepartureTime = NetexUtils.convertToSqlTime(departureTime, NetexUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setDepartureTime(localDepartureTime);
            }

            // TODO: add support for zone offsets other than utc here  (like +02:00, -05:30, etc...)
        }
        if (arrivalTime != null) {
            ZoneOffset zoneOffset = arrivalTime.getOffset();

            if (zoneOffset.equals(ZoneOffset.UTC)) {
                Time localArrivalTime = NetexUtils.convertToSqlTime(arrivalTime, NetexUtils.getZoneOffset(LOCAL_ZONE_ID));
                vehicleJourneyAtStop.setArrivalTime(localArrivalTime);
            }

            // TODO: add support for zone offsets other than utc here (like +02:00, -05:30, etc...)

        } else {
            // TODO find out if necessary
            // vehicleJourneyAtStop.setArrivalTime(new Time(vehicleJourneyAtStop.getDepartureTime().getTime()));
        }

    }

    static {
        ParserFactory.register(JourneyParser.class.getName(), new ParserFactory() {
            private JourneyParser instance = new JourneyParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
