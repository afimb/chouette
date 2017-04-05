package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
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

import static mobi.chouette.exchange.netexprofile.Constant.PRODUCING_CONTEXT;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.SERVICE_JOURNEY_KEY;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.STOP_POINT_IN_JOURNEY_PATTERN_KEY;

public class ServiceJourneyProducer extends NetexProducer { //implements NetexEntityProducer<ServiceJourney, VehicleJourney> {

    //@Override
    @SuppressWarnings("unchecked")
    public ServiceJourney produce(Context context, VehicleJourney vehicleJourney, Line line) {
        Context producingContext = (Context) context.get(PRODUCING_CONTEXT);
        Context calendarContext = (Context) producingContext.get(CalendarProducer.LOCAL_CONTEXT);

        ServiceJourney serviceJourney = netexFactory.createServiceJourney();
        serviceJourney.setVersion(vehicleJourney.getObjectVersion() > 0 ? String.valueOf(vehicleJourney.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String serviceJourneyId = netexId(vehicleJourney.objectIdPrefix(), SERVICE_JOURNEY_KEY, vehicleJourney.objectIdSuffix());
        serviceJourney.setId(serviceJourneyId);

        if (isSet(vehicleJourney.getPublishedJourneyName())) {
            serviceJourney.setName(getMultilingualString(vehicleJourney.getPublishedJourneyName()));
        }
        if (isSet(vehicleJourney.getPublishedJourneyIdentifier())) {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.getPublishedJourneyIdentifier()));
            serviceJourney.setPublicCode(vehicleJourney.getPublishedJourneyIdentifier());
        } else {
            serviceJourney.setShortName(getMultilingualString(vehicleJourney.objectIdSuffix()));
            serviceJourney.setPublicCode(vehicleJourney.objectIdSuffix());
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

        Context objectContext = (Context) calendarContext.get(vehicleJourney.getObjectId());
        List<String> dayTypeIds = (List<String>) objectContext.get(CalendarProducer.DAY_TYPE_IDS);

        List<Timetable> timetables = vehicleJourney.getTimetables();
        DayTypeRefs_RelStructure dayTypeStruct = netexFactory.createDayTypeRefs_RelStructure();

        for (String dayTypeId : dayTypeIds) {
            DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
            dayTypeRefStruct.setVersion(timetables.get(0).getObjectVersion() > 0 ? String.valueOf(timetables.get(0).getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);
            dayTypeRefStruct.setRef(dayTypeId);
            dayTypeStruct.getDayTypeRef().add(netexFactory.createDayTypeRef(dayTypeRefStruct));
        }

        serviceJourney.setDayTypes(dayTypeStruct);

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
