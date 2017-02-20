package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.*;
import mobi.chouette.model.VehicleJourney;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;

import static mobi.chouette.exchange.netexprofile.exporter.ModelTranslator.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.DAY_TYPE_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_JOURNEY_KEY;

public class ServiceJourneyProducer extends AbstractNetexProducer<ServiceJourney, VehicleJourney> {

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

        // TODO find out where and how to get the public code, and set here
        //serviceJourney.setPublicCode("");

        // TODO look over how we import service journey names
        if (isSet(vehicleJourney.getPublishedJourneyIdentifier())) {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.getPublishedJourneyIdentifier()));
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
            dayTypeRefStruct.setVersion(timetable.getObjectVersion() > 0 ? String.valueOf(timetable.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

            String dayTypeIdRef = netexId(timetable.objectIdPrefix(), DAY_TYPE_KEY, timetable.objectIdSuffix());
            dayTypeRefStruct.setRef(dayTypeIdRef);

            dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
        }

        serviceJourney.setDayTypes(dayTypeStruct);

        if (CollectionUtils.isNotEmpty(vehicleJourney.getVehicleJourneyAtStops())) {
            List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourney.getVehicleJourneyAtStops();
            vehicleJourneyAtStops.sort(Comparator.comparingInt(o -> o.getStopPoint().getPosition()));

            Time firstStopDepartureTime = null;
            TimetabledPassingTimes_RelStructure passingTimesStruct = netexFactory.createTimetabledPassingTimes_RelStructure();

            for (VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourneyAtStops) {
                TimetabledPassingTime timetabledPassingTime = netexFactory.createTimetabledPassingTime();

                if (firstStopDepartureTime == null) {
                    // TODO verify that all times conforms to UTC in written netex
                    firstStopDepartureTime = vehicleJourneyAtStop.getDepartureTime();
                    serviceJourney.setDepartureTime(toOffsetTimeUtc(firstStopDepartureTime));
                }
                if (vehicleJourneyAtStop.getArrivalTime() != null) {
                    timetabledPassingTime.setArrivalTime(toOffsetTimeUtc(vehicleJourneyAtStop.getArrivalTime()));
                }
                if (vehicleJourneyAtStop.getDepartureTime() != null) {
                    timetabledPassingTime.setDepartureTime(toOffsetTimeUtc(vehicleJourneyAtStop.getDepartureTime()));
                }
                passingTimesStruct.getTimetabledPassingTime().add(timetabledPassingTime);
            }
            serviceJourney.setPassingTimes(passingTimesStruct);
        }

        return serviceJourney;
    }

}
