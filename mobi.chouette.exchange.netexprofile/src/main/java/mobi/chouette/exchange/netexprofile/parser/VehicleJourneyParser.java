package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.BoardingAlightingPossibilityEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.sql.Time;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

@Log4j
public class VehicleJourneyParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "VehicleJourneyContext";

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        // TODO implement
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context lineContext = (Context) parsingContext.get(LineParser.LOCAL_CONTEXT);
        Context journeyPatternContext = (Context) parsingContext.get(JourneyPatternParser.LOCAL_CONTEXT);
        Context stopPointContext = (Context) parsingContext.get(StopPointParser.LOCAL_CONTEXT);

        DayTypeParser dayTypeParser = (DayTypeParser) ParserFactory.create(DayTypeParser.class.getName());

        //JourneysInFrame_RelStructure journeysInFrameRelStruct = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        //List<Journey_VersionStructure> serviceJourneyStructs = journeysInFrameRelStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();

        Collection<ServiceJourney> netexServiceJourneys = netexReferential.getServiceJourneys().values();

        //for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {
        for (ServiceJourney netexServiceJourney : netexServiceJourneys) {
            // TODO handle all types of netex journeys here, might have an instanceof check here... for now only parsing ServiceJourney instances

            //ServiceJourney netexServiceJourney = (ServiceJourney) serviceJourneyStruct;
            String chouetteVehicleJourneyId = netexServiceJourney.getId();  // TODO generate/create id with id creator
            VehicleJourney chouetteVehicleJourney = ObjectFactory.getVehicleJourney(chouetteReferential, chouetteVehicleJourneyId);

            // TODO is this useful for anything?
            OffsetTime netexJourneyDepartureTime = netexServiceJourney.getDepartureTime(); // most often the same as departure time for the first timetabled passing time

            DayTypeRefs_RelStructure dayTypeRefsRelStruct = netexServiceJourney.getDayTypes();
            List<JAXBElement<? extends DayTypeRefStructure>> dayTypeRefStructElements = dayTypeRefsRelStruct.getDayTypeRef();
            for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : dayTypeRefStructElements) {
                DayTypeRefStructure dayTypeRefStruct = dayTypeRefStructElement.getValue();
                dayTypeParser.addVehicleJourneyIdRef(context, dayTypeRefStruct.getRef(), chouetteVehicleJourneyId);
            }

            LineRefStructure lineRefStruct = netexServiceJourney.getLineRef().getValue();
            Context lineObjectContext = (Context) lineContext.get(lineRefStruct.getRef());
            String chouetteLineId = (String) lineObjectContext.get(LineParser.LINE_ID);
            Line line = ObjectFactory.getLine(chouetteReferential, chouetteLineId);
            chouetteVehicleJourney.setCompany(line.getCompany());

            // TODO check if the norwegian netex profile supports transport mode on journeys, to avoid getting from line
            TransportModeNameEnum transportModeName = line.getTransportModeName();
            chouetteVehicleJourney.setTransportMode(transportModeName);

            JourneyPatternRefStructure journeyPatternRefStruct = netexServiceJourney.getJourneyPatternRef().getValue();
            String netexJourneyPatternIdRef = journeyPatternRefStruct.getRef();

            org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = NetexObjectUtil.getJourneyPattern(netexReferential, netexJourneyPatternIdRef);

            Context journeyPatternObjectContext = (Context) journeyPatternContext.get(netexJourneyPatternIdRef);
            String chouetteJourneyPatternId = (String) journeyPatternObjectContext.get(JourneyPatternParser.JOURNEY_PATTERN_ID);
            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(chouetteReferential, chouetteJourneyPatternId);

            chouetteVehicleJourney.setJourneyPattern(chouetteJourneyPattern);
            chouetteVehicleJourney.setRoute(chouetteJourneyPattern.getRoute());

            String netexJourneyPublicCode = netexServiceJourney.getPublicCode();
            chouetteVehicleJourney.setPublishedJourneyName(netexJourneyPublicCode);
            chouetteVehicleJourney.setPublishedJourneyIdentifier(netexJourneyPublicCode);
            chouetteVehicleJourney.setComment(netexJourneyPublicCode);

            // TODO add more properties to journey like
            // chouetteVehicleJourney.setJourneyCategory(JourneyCategoryEnum.Timesheet);
            // chouetteVehicleJourney.setVehicleTypeIdentifier(null); // e.g. aircraft type?

            // TODO find out how to handle JourneyFrequencies

            List<TimetabledPassingTime> timetabledPassingTimes = netexServiceJourney.getPassingTimes().getTimetabledPassingTime();

            for (TimetabledPassingTime timetabledPassingTime : timetabledPassingTimes) {
                VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
                vehicleJourneyAtStop.setVehicleJourney(chouetteVehicleJourney);

                // TODO what are the conditions for setting the following properties?

                // vehicleJourneyAtStop.setArrivalTime(tripVisitTime.getTime());
                // vehicleJourneyAtStop.setArrivalDayOffset(tripVisitTime.getDayOffset());
                // vehicleJourneyAtStop.setDepartureTime(tripVisitTime.getTime());
                // vehicleJourneyAtStop.setDepartureDayOffset(tripVisitTime.getDayOffset());

                PointInJourneyPatternRefStructure pointInJourneyPatternRefStruct = timetabledPassingTime.getPointInJourneyPatternRef().getValue();
                String pointInJourneyPatternId = pointInJourneyPatternRefStruct.getRef();
                PointsInJourneyPattern_RelStructure pointsInSequenceStruct = netexJourneyPattern.getPointsInSequence();

                List<PointInLinkSequence_VersionedChildStructure> pointsInLinkSequence = pointsInSequenceStruct
                        .getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern();

                // Default = board and alight
                vehicleJourneyAtStop.setBoardingAlightingPossibility(BoardingAlightingPossibilityEnum.BoardAndAlight);

                for (PointInLinkSequence_VersionedChildStructure pointInLinkSequence : pointsInLinkSequence) {
                    StopPointInJourneyPattern stopPointInJourneyPattern = (StopPointInJourneyPattern) pointInLinkSequence;

                    if (stopPointInJourneyPattern.getId().equalsIgnoreCase(pointInJourneyPatternId)) {
                        ScheduledStopPointRefStructure scheduledStopPointRefStruct = stopPointInJourneyPattern.getScheduledStopPointRef().getValue();
                        Context stopPointObjectContext = (Context) stopPointContext.get(scheduledStopPointRefStruct.getRef());
                        String chouetteStopPointId = (String) stopPointObjectContext.get(StopPointParser.STOP_POINT_ID);

                        StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);
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
                }

                // This solution only handles incoming data in UTC for now
                // TODO add support for other time zones and zone offsets

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

            // sort the list of added vechicle journey at stops for the current vehicle journey
            chouetteVehicleJourney.getVehicleJourneyAtStops().sort((o1, o2) -> {
                StopPoint p1 = o1.getStopPoint();
                StopPoint p2 = o2.getStopPoint();
                if (p1 != null && p2 != null) {
                    int pos1 = p1.getPosition() == null ? 0 : p1.getPosition().intValue();
                    int pos2 = p2.getPosition() == null ? 0 : p2.getPosition().intValue();
                    return pos1 - pos2;
                }
                return 0;
            });

            chouetteVehicleJourney.setFilled(true);
        }
    }

    static {
        ParserFactory.register(VehicleJourneyParser.class.getName(), new ParserFactory() {
            private VehicleJourneyParser instance = new VehicleJourneyParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
