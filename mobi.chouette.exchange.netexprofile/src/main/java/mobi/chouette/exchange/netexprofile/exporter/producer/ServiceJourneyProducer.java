package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class ServiceJourneyProducer extends NetexProducer { //implements NetexEntityProducer<ServiceJourney, VehicleJourney> {

    //@Override
    public ServiceJourney produce(VehicleJourney vehicleJourney, Line line) {
        ServiceJourney serviceJourney = netexFactory.createServiceJourney();
        serviceJourney.setVersion(vehicleJourney.getObjectVersion() > 0 ? String.valueOf(vehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String serviceJourneyId = netexId(vehicleJourney.objectIdPrefix(), SERVICE_JOURNEY_KEY, vehicleJourney.objectIdSuffix());
        serviceJourney.setId(serviceJourneyId);

        // TODO look over how we import service journey names
        if (isSet(vehicleJourney.getPublishedJourneyName())) {
            serviceJourney.setName(getMultilingualString(vehicleJourney.getPublishedJourneyName()));
        }

        // TODO look over how we import service journey names
        if (isSet(vehicleJourney.getPublishedJourneyIdentifier())) {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.getPublishedJourneyIdentifier()));
            serviceJourney.setPublicCode(vehicleJourney.getPublishedJourneyIdentifier());
        }

        if (isSet(vehicleJourney.getComment())) {
            serviceJourney.setDescription(getMultilingualString(vehicleJourney.getComment()));
        }

        JourneyPattern journeyPattern = vehicleJourney.getJourneyPattern();
        JourneyPatternRefStructure journeyPatternRefStruct = netexFactory.createJourneyPatternRefStructure();
        journeyPatternRefStruct.setVersion(journeyPattern.getObjectVersion() != null ? String.valueOf(journeyPattern.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        journeyPatternRefStruct.setRef(journeyPattern.getObjectId());
        serviceJourney.setJourneyPatternRef(netexFactory.createJourneyPatternRef(journeyPatternRefStruct));

        LineRefStructure lineRefStruct = netexFactory.createLineRefStructure();
        lineRefStruct.setVersion(line.getObjectVersion() != null ? String.valueOf(line.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
        lineRefStruct.setRef(line.getObjectId());
        serviceJourney.setLineRef(netexFactory.createLineRef(lineRefStruct));

        // produce day types

        DayTypeRefs_RelStructure dayTypeStruct = netexFactory.createDayTypeRefs_RelStructure();

        for (Timetable timetable : vehicleJourney.getTimetables()) {
            DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
            //dayTypeRefStruct.setVersion(timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION); // TODO MUST ENABLE AFTER WE HAVE ADDED SERVICE CALENDAR FRAME

            String dayTypeIdRef = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, timetable.objectIdSuffix());
            dayTypeRefStruct.setRef(dayTypeIdRef);
            dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
        }

        serviceJourney.setDayTypes(dayTypeStruct);

        // produce timetabled passing times

        if (CollectionUtils.isNotEmpty(vehicleJourney.getVehicleJourneyAtStops())) {
            List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
            vehicleJourneyAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

            Time firstStopDepartureTime = null;
            TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();
            String[] idSequence = NetexProducerUtils.generateIdSequence(vehicleJourneyAtStops.size());

            for (int i = 0; i < vehicleJourneyAtStops.size(); i++) {
                VehicleJourneyAtStop vehicleJourneyAtStop = vehicleJourneyAtStops.get(i);
                TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();

                StopPoint stopPoint = vehicleJourneyAtStop.getStopPoint();
                StopPointInJourneyPatternRefStructure pointInPatternRefStruct = netexFactory.createStopPointInJourneyPatternRefStructure();
                pointInPatternRefStruct.setVersion(stopPoint.getObjectVersion() > 0 ? String.valueOf(stopPoint.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

                String pointInPatternIdSuffix = journeyPattern.objectIdSuffix() + StringUtils.leftPad(idSequence[i], 2, "0");
                String pointInPatternIdRef = netexId(vehicleJourney.objectIdPrefix(), STOP_POINT_IN_JOURNEY_PATTERN_KEY, pointInPatternIdSuffix);
                pointInPatternRefStruct.setRef(pointInPatternIdRef);
                timetabledPassingTime.setPointInJourneyPatternRef(netexFactory.createStopPointInJourneyPatternRef(pointInPatternRefStruct));

                if (firstStopDepartureTime == null) {
                    firstStopDepartureTime = vehicleJourneyAtStop.getDepartureTime();
                    serviceJourney.setDepartureTime(NetexProducerUtils.toOffsetTimeUtc(firstStopDepartureTime));
                }
                if (vehicleJourneyAtStop.getArrivalTime() != null) {
                    timetabledPassingTime.setArrivalTime(NetexProducerUtils.toOffsetTimeUtc(vehicleJourneyAtStop.getArrivalTime()));
                }
                if (vehicleJourneyAtStop.getDepartureTime() != null) {
                    timetabledPassingTime.setDepartureTime(NetexProducerUtils.toOffsetTimeUtc(vehicleJourneyAtStop.getDepartureTime()));
                }
                passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
            }

            serviceJourney.setPassingTimes(passingTimesStruct);
        }

        return serviceJourney;
    }

}
