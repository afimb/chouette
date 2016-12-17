package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexFrameContext;
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
import java.util.List;

@Log4j
public class VehicleJourneyParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "VehicleJourneyContext";
    private static final ZoneId LOCAL_ZONE_ID = ZoneId.of("Europe/Oslo");

    @Override
    public void initReferentials(Context context) throws Exception {
        // TODO implement
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        DayTypeParser dayTypeParser = (DayTypeParser) ParserFactory.create(DayTypeParser.class.getName());
        JourneysInFrame_RelStructure journeysInFrameRelStruct = (JourneysInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<Journey_VersionStructure> serviceJourneyStructs = journeysInFrameRelStruct.getDatedServiceJourneyOrDeadRunOrServiceJourney();

        NetexFrameContext netexFrameContext = (NetexFrameContext) context.get(NETEX_FRAME_CONTEXT);
        //TimetableFrame timetableFrame = netexFrameContext.get(TimetableFrame.class);
        //ServiceCalendarFrame serviceCalendarFrame = netexFrameContext.get(ServiceCalendarFrame.class);

        for (Journey_VersionStructure serviceJourneyStruct : serviceJourneyStructs) {

            // TODO handle all types of netex journeys, for now only parsing ServiceJourney instances
            ServiceJourney netexServiceJourney = (ServiceJourney) serviceJourneyStruct;
            VehicleJourney chouetteVehicleJourney = ObjectFactory.getVehicleJourney(chouetteReferential, netexServiceJourney.getId());

            // TODO consider if this field is this useful for anything
            //OffsetTime netexJourneyDepartureTime = netexServiceJourney.getDepartureTime();

            // TODO find out how to find daytype id references in DayTypeParser, is this necessary?
            List<JAXBElement<? extends DayTypeRefStructure>> dayTypeRefStructElements = netexServiceJourney.getDayTypes().getDayTypeRef();

            for (JAXBElement<? extends DayTypeRefStructure> dayTypeRefStructElement : dayTypeRefStructElements) {

                // TODO fix this id reference: als see DayTypeParser line: 101. in cases like this, where the connection is inverted, it could be useful with the object context
                String dayTypeIdRef = dayTypeRefStructElement.getValue().getRef();
                dayTypeParser.addVehicleJourneyIdRef(context, dayTypeIdRef, netexServiceJourney.getId());
            }

            String lineIdRef = netexServiceJourney.getLineRef().getValue().getRef();
            Line line = ObjectFactory.getLine(chouetteReferential, lineIdRef);
            chouetteVehicleJourney.setCompany(line.getCompany());

            // TODO check if the norwegian netex profile supports transport mode on journeys, to avoid getting from line
            TransportModeNameEnum transportModeName = line.getTransportModeName();
            chouetteVehicleJourney.setTransportMode(transportModeName);

            String journeyPatternIdRef = netexServiceJourney.getJourneyPatternRef().getValue().getRef();

            // TODO find out how to retrieve this
            org.rutebanken.netex.model.JourneyPattern_VersionStructure netexJourneyPattern = NetexObjectUtil.getJourneyPattern(netexReferential, journeyPatternIdRef);

            mobi.chouette.model.JourneyPattern chouetteJourneyPattern = ObjectFactory.getJourneyPattern(chouetteReferential, journeyPatternIdRef);
            chouetteVehicleJourney.setJourneyPattern(chouetteJourneyPattern);
            chouetteVehicleJourney.setRoute(chouetteJourneyPattern.getRoute());

            String netexJourneyPublicCode = netexServiceJourney.getPublicCode();
            chouetteVehicleJourney.setPublishedJourneyName(netexJourneyPublicCode);
            chouetteVehicleJourney.setPublishedJourneyIdentifier(netexJourneyPublicCode);
            chouetteVehicleJourney.setComment(netexJourneyPublicCode);

            // TODO implement parsing of JourneyFrequencies

            List<TimetabledPassingTime> timetabledPassingTimes = netexServiceJourney.getPassingTimes().getTimetabledPassingTime();

            for (TimetabledPassingTime timetabledPassingTime : timetabledPassingTimes) {
                VehicleJourneyAtStop vehicleJourneyAtStop = ObjectFactory.getVehicleJourneyAtStop();
                vehicleJourneyAtStop.setVehicleJourney(chouetteVehicleJourney);

                // TODO are these mandatory?
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
                        String stopPointIdRef = stopPointInJourneyPattern.getScheduledStopPointRef().getValue().getRef();
                        StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, stopPointIdRef);
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

                // TODO add support for other time zones and zone offsets, for now only handling UTC
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
                    int pos1 = p1.getPosition() == null ? 0 : p1.getPosition();
                    int pos2 = p2.getPosition() == null ? 0 : p2.getPosition();
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
